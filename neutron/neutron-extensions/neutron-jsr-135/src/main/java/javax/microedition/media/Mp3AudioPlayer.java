/*
 *  Neutron
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 */

package javax.microedition.media;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.microedition.media.control.VolumeControl;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.neutron.media.control.J2SEVolumeControl;

public class Mp3AudioPlayer implements Player, VolumeControl, LineListener {

	private int state = UNREALIZED;
	private String contentType = "audio/mpeg";
	private byte[] audioData = null;
	private long durationUs = 0;
	private long currentMediaTimeUs = 0;
	private int loopCount = 1;

	private Clip clip = null;
	private AudioInputStream audioInputStream = null;
	private AudioInputStream decodedStream = null;
	private boolean lineListenerAdded = false;

	private final J2SEVolumeControl volumeControl = new J2SEVolumeControl();
	private Vector listeners = null;

	// Simulated playback tracking
	private Thread playbackThread = null;
	private long playbackStartTimeMs = 0;
	private long startMediaTimeUs = 0;

	public Mp3AudioPlayer() {
	}

	public synchronized boolean open(InputStream stream, String type) throws IOException {
		if (type != null) {
			this.contentType = type;
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[4096];
		int n;
		while ((n = stream.read(buf)) != -1) {
			baos.write(buf, 0, n);
		}
		this.audioData = baos.toByteArray();

		parseDuration();

		// Attempt hardware/SPI playback via Java Sound if an MP3 SPI is registered
		tryInitJavaSound();

		this.state = UNREALIZED;
		return true;
	}

	private void parseDuration() {
		if (audioData == null || audioData.length < 4) {
			durationUs = 0;
			return;
		}

		int offset = 0;
		// Skip ID3v2 tag if present: "ID3" followed by 2-byte version, 1-byte flags, 4-byte synchsafe size
		if (audioData.length > 10 && audioData[0] == 'I' && audioData[1] == 'D' && audioData[2] == '3') {
			int id3Size = ((audioData[6] & 0x7F) << 21) |
			              ((audioData[7] & 0x7F) << 14) |
			              ((audioData[8] & 0x7F) << 7)  |
			              (audioData[9] & 0x7F);
			offset = 10 + id3Size;
			if (offset >= audioData.length) {
				offset = 0;
			}
		}

		// Estimate duration: standard MP3 frame is 1152 samples (~26ms at 44.1kHz).
		// Count MPEG sync words: 0xFF followed by high 3 bits set (0xE0)
		int frameCount = 0;
		for (int i = offset; i < audioData.length - 1; i++) {
			if ((audioData[i] & 0xFF) == 0xFF && (audioData[i + 1] & 0xE0) == 0xE0) {
				frameCount++;
				i += 200; // approximate frame jump to prevent false sync positives
			}
		}

		if (frameCount > 0) {
			durationUs = frameCount * 26122L; // 1152 / 44100 sec ~= 26122 us
		} else {
			// Fallback estimate based on file size at typical 128kbps (16000 bytes/sec)
			long sec = audioData.length / 16000L;
			durationUs = Math.max(1000000L, sec * 1000000L);
		}
	}

	private void tryInitJavaSound() {
		if (audioData == null || audioData.length == 0) {
			return;
		}
		try {
			audioInputStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(audioData));
			AudioFormat baseFormat = audioInputStream.getFormat();
			if (baseFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
				AudioFormat decodedFormat = new AudioFormat(
					AudioFormat.Encoding.PCM_SIGNED,
					baseFormat.getSampleRate(),
					16,
					baseFormat.getChannels(),
					baseFormat.getChannels() * 2,
					baseFormat.getSampleRate(),
					false
				);
				decodedStream = AudioSystem.getAudioInputStream(decodedFormat, audioInputStream);
				int frameLength = (int) decodedStream.getFrameLength();
				int frameSize = decodedFormat.getFrameSize();
				DataLine.Info info = new DataLine.Info(Clip.class, decodedFormat, frameLength > 0 ? frameLength * frameSize : AudioSystem.NOT_SPECIFIED);
				clip = (Clip) AudioSystem.getLine(info);
				clip.open(decodedStream);
			} else {
				DataLine.Info info = new DataLine.Info(Clip.class, baseFormat, AudioSystem.NOT_SPECIFIED);
				clip = (Clip) AudioSystem.getLine(info);
				clip.open(audioInputStream);
			}
		} catch (UnsupportedAudioFileException e) {
			clip = null;
		} catch (LineUnavailableException e) {
			clip = null;
		} catch (Throwable t) {
			clip = null;
		}
	}

	public synchronized void realize() throws MediaException {
		if (state == CLOSED) {
			throw new IllegalStateException("Player is closed");
		}
		if (state == UNREALIZED) {
			state = REALIZED;
		}
	}

	public synchronized void prefetch() throws MediaException {
		if (state == CLOSED) {
			throw new IllegalStateException("Player is closed");
		}
		if (state == UNREALIZED) {
			realize();
		}
		if (state == REALIZED) {
			state = PREFETCHED;
		}
	}

	public synchronized void start() throws MediaException {
		if (state == CLOSED) {
			throw new IllegalStateException("Player is closed");
		}
		if (state == STARTED) {
			return;
		}
		if (state < PREFETCHED) {
			prefetch();
		}

		state = STARTED;

		if (clip != null) {
			if (!lineListenerAdded) {
				clip.addLineListener(this);
				lineListenerAdded = true;
			}
			clip.setMicrosecondPosition(currentMediaTimeUs);
			clip.start();
		} else {
			startSimulatedPlayback();
		}

		notifyListeners(PlayerListener.STARTED, Long.valueOf(currentMediaTimeUs));
	}

	public synchronized void stop() throws MediaException {
		if (state == CLOSED) {
			throw new IllegalStateException("Player is closed");
		}
		if (state != STARTED) {
			return;
		}

		if (clip != null) {
			currentMediaTimeUs = clip.getMicrosecondPosition();
			clip.stop();
		} else {
			stopSimulatedPlayback();
		}

		state = PREFETCHED;
		notifyListeners(PlayerListener.STOPPED, Long.valueOf(currentMediaTimeUs));
	}

	private synchronized void startSimulatedPlayback() {
		stopSimulatedPlayback();
		playbackStartTimeMs = System.currentTimeMillis();
		startMediaTimeUs = currentMediaTimeUs;

		playbackThread = new Thread(new Runnable() {
			public void run() {
				try {
					while (true) {
						long elapsedMs;
						long currentUs;
						long totalDurationUs;
						synchronized (Mp3AudioPlayer.this) {
							if (state != STARTED) {
								break;
							}
							elapsedMs = System.currentTimeMillis() - playbackStartTimeMs;
							currentUs = startMediaTimeUs + (elapsedMs * 1000L);
							currentMediaTimeUs = currentUs;
							totalDurationUs = durationUs;

							if (totalDurationUs > 0 && currentUs >= totalDurationUs) {
								if (loopCount == -1 || loopCount > 1) {
									if (loopCount > 1) {
										loopCount--;
									}
									playbackStartTimeMs = System.currentTimeMillis();
									startMediaTimeUs = 0;
									currentMediaTimeUs = 0;
									continue;
								} else {
									state = PREFETCHED;
									currentMediaTimeUs = totalDurationUs;
									notifyListeners(PlayerListener.END_OF_MEDIA, Long.valueOf(totalDurationUs));
									break;
								}
							}
						}

						Thread.sleep(30);
					}
				} catch (InterruptedException e) {
				}
			}
		}, "Mp3AudioPlayer-Timer");
		playbackThread.setDaemon(true);
		playbackThread.start();
	}

	private synchronized void stopSimulatedPlayback() {
		if (playbackThread != null) {
			playbackThread.interrupt();
			playbackThread = null;
		}
		if (playbackStartTimeMs > 0) {
			long elapsedMs = System.currentTimeMillis() - playbackStartTimeMs;
			currentMediaTimeUs = startMediaTimeUs + (elapsedMs * 1000L);
			if (durationUs > 0 && currentMediaTimeUs > durationUs) {
				currentMediaTimeUs = durationUs;
			}
		}
	}

	public synchronized void deallocate() {
		if (state == CLOSED) {
			return;
		}
		if (state == STARTED) {
			try {
				stop();
			} catch (MediaException e) {
			}
		}
		if (clip != null) {
			clip.flush();
		}
		state = UNREALIZED;
	}

	public synchronized void close() {
		if (state == CLOSED) {
			return;
		}
		Manager.mediaDone(this);
		deallocate();

		if (clip != null) {
			if (lineListenerAdded) {
				clip.removeLineListener(this);
				lineListenerAdded = false;
			}
			clip.close();
			clip = null;
		}

		try {
			if (decodedStream != null) {
				decodedStream.close();
				decodedStream = null;
			}
			if (audioInputStream != null) {
				audioInputStream.close();
				audioInputStream = null;
			}
		} catch (IOException e) {
		}

		state = CLOSED;
		notifyListeners(PlayerListener.CLOSED, null);
	}

	public synchronized long setMediaTime(long now) throws MediaException {
		if (state == CLOSED) {
			throw new IllegalStateException("Player is closed");
		}
		if (now < 0) {
			now = 0;
		}
		if (durationUs > 0 && now > durationUs) {
			now = durationUs;
		}
		currentMediaTimeUs = now;
		if (clip != null) {
			clip.setMicrosecondPosition(now);
		} else if (state == STARTED) {
			playbackStartTimeMs = System.currentTimeMillis();
			startMediaTimeUs = now;
		}
		return currentMediaTimeUs;
	}

	public synchronized long getMediaTime() {
		if (state == CLOSED) {
			throw new IllegalStateException("Player is closed");
		}
		if (clip != null) {
			return clip.getMicrosecondPosition();
		}
		if (state == STARTED && playbackStartTimeMs > 0) {
			long elapsedMs = System.currentTimeMillis() - playbackStartTimeMs;
			long cur = startMediaTimeUs + (elapsedMs * 1000L);
			return durationUs > 0 ? Math.min(cur, durationUs) : cur;
		}
		return currentMediaTimeUs;
	}

	public int getState() {
		return state;
	}

	public long getDuration() {
		if (state == CLOSED) {
			throw new IllegalStateException("Player is closed");
		}
		return durationUs > 0 ? durationUs : TIME_UNKNOWN;
	}

	public String getContentType() {
		return contentType;
	}

	public synchronized void setLoopCount(int count) {
		if (state == CLOSED) {
			throw new IllegalStateException("Player is closed");
		}
		this.loopCount = count;
		if (clip != null) {
			clip.loop(count);
		}
	}

	public synchronized void addPlayerListener(PlayerListener playerListener) {
		if (playerListener == null) {
			return;
		}
		if (listeners == null) {
			listeners = new Vector();
		}
		if (!listeners.contains(playerListener)) {
			listeners.add(playerListener);
		}
	}

	public synchronized void removePlayerListener(PlayerListener playerListener) {
		if (listeners == null || playerListener == null) {
			return;
		}
		listeners.remove(playerListener);
	}

	private void notifyListeners(String event, Object eventData) {
		List copy;
		synchronized (this) {
			if (listeners == null || listeners.isEmpty()) {
				return;
			}
			copy = new ArrayList(listeners);
		}
		for (int i = 0; i < copy.size(); i++) {
			PlayerListener l = (PlayerListener) copy.get(i);
			try {
				l.playerUpdate(this, event, eventData);
			} catch (Throwable t) {
			}
		}
	}

	public Control getControl(String controlType) {
		if ("VolumeControl".equals(controlType) || "javax.microedition.media.control.VolumeControl".equals(controlType)) {
			return volumeControl;
		}
		return null;
	}

	public Control[] getControls() {
		return new Control[] { volumeControl };
	}

	// VolumeControl implementation
	public int getLevel() {
		return volumeControl.getLevel();
	}

	public boolean isMuted() {
		return volumeControl.isMuted();
	}

	public int setLevel(int level) {
		return volumeControl.setLevel(level);
	}

	public void setMute(boolean mute) {
		volumeControl.setMute(mute);
	}

	public void update(LineEvent event) {
		if (LineEvent.Type.STOP.equals(event.getType())) {
			synchronized (this) {
				if (state == STARTED) {
					if (loopCount == -1 || loopCount > 1) {
						if (loopCount > 1) {
							loopCount--;
						}
						if (clip != null) {
							clip.setMicrosecondPosition(0);
							clip.start();
						}
					} else {
						state = PREFETCHED;
						notifyListeners(PlayerListener.END_OF_MEDIA, Long.valueOf(durationUs));
					}
				}
			}
		}
	}
}

