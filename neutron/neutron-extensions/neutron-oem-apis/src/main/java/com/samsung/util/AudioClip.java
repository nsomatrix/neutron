/*
 *  Neutron
 *
 *  Licensed under LGPL 2.1 or Apache 2.0.
 */

package com.samsung.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.control.VolumeControl;
import org.neutron.log.Logger;

public class AudioClip {

	public static final int TYPE_MMF = 1;
	public static final int TYPE_MP3 = 2;
	public static final int TYPE_MIDI = 3;

	private Player player;
	private int clipType;

	public AudioClip(int type, String filename) {
		this.clipType = type;
		try {
			InputStream in = getClass().getResourceAsStream(filename.startsWith("/") ? filename : "/" + filename);
			if (in != null) {
				String mime = type == TYPE_MIDI ? "audio/midi" : (type == TYPE_MP3 ? "audio/mpeg" : "audio/x-smaf");
				player = Manager.createPlayer(in, mime);
			}
		} catch (Throwable t) {
			Logger.debug("AudioClip init error: " + t.getMessage());
		}
	}

	public AudioClip(int type, byte[] data, int offset, int length) {
		this.clipType = type;
		try {
			if (data != null && length > 0) {
				ByteArrayInputStream bais = new ByteArrayInputStream(data, offset, length);
				String mime = type == TYPE_MIDI ? "audio/midi" : (type == TYPE_MP3 ? "audio/mpeg" : "audio/x-smaf");
				player = Manager.createPlayer(bais, mime);
			}
		} catch (Throwable t) {
			Logger.debug("AudioClip init error: " + t.getMessage());
		}
	}

	public void play(int loop, int volume) {
		if (player != null) {
			try {
				VolumeControl vc = (VolumeControl) player.getControl("VolumeControl");
				if (vc != null && volume >= 0) {
					vc.setLevel(Math.min(100, volume));
				}
				player.setLoopCount(loop == 0 ? -1 : loop);
				player.start();
			} catch (Throwable t) {
				Logger.debug("AudioClip play error: " + t.getMessage());
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
}

