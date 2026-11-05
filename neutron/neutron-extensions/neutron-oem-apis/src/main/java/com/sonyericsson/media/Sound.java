/*
 *  Neutron
 *
 *  Licensed under LGPL 2.1 or Apache 2.0.
 */

package com.sonyericsson.media;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.control.VolumeControl;
import org.neutron.log.Logger;

public class Sound {

	private Player player;
	private int volume = 100;

	public Sound(byte[] data, int offset, int length) {
		try {
			if (data != null && length > 0) {
				ByteArrayInputStream bais = new ByteArrayInputStream(data, offset, length);
				player = Manager.createPlayer(bais, null);
			}
		} catch (Throwable t) {
			Logger.debug("SonyEricsson Sound init error: " + t.getMessage());
		}
	}

	public Sound(InputStream in, String type) {
		try {
			if (in != null) {
				player = Manager.createPlayer(in, type);
			}
		} catch (Throwable t) {
			Logger.debug("SonyEricsson Sound init error: " + t.getMessage());
		}
	}

	public Sound(String locator) {
		try {
			if (locator != null) {
				player = Manager.createPlayer(locator);
			}
		} catch (Throwable t) {
			Logger.debug("SonyEricsson Sound init error: " + t.getMessage());
		}
	}

	public void play() {
		play(1);
	}

	public void play(int loop) {
		if (player != null) {
			try {
				VolumeControl vc = (VolumeControl) player.getControl("VolumeControl");
				if (vc != null) {
					vc.setLevel(volume);
				}
				player.setLoopCount(loop == 0 ? -1 : loop);
				player.start();
			} catch (Throwable t) {
				Logger.debug("SonyEricsson Sound play error: " + t.getMessage());
			}
		}
	}

	public void stop() {
		if (player != null) {
			try {
				player.stop();
			} catch (Throwable t) {
			}
		}
	}

	public void pause() {
		stop();
	}

	public void resume() {
		if (player != null) {
			try {
				player.start();
			} catch (Throwable t) {
			}
		}
	}

	public void setVolume(int volume) {
		this.volume = Math.max(0, Math.min(100, volume));
		if (player != null) {
			try {
				VolumeControl vc = (VolumeControl) player.getControl("VolumeControl");
				if (vc != null) {
					vc.setLevel(this.volume);
				}
			} catch (Throwable t) {
			}
		}
	}

	public int getVolume() {
		return volume;
	}

	public static void playTone(int note, int duration, int volume) {
		try {
			Manager.playTone(note, duration, volume);
		} catch (Throwable t) {
			Logger.debug("SonyEricsson playTone error: " + t.getMessage());
		}
	}
}

