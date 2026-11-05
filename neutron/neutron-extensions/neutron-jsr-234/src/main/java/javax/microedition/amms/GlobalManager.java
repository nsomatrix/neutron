/*
 *  Neutron JSR-234 AMMS
 *
 *  Licensed under LGPL 2.1 or Apache 2.0.
 */

package javax.microedition.amms;

import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.media.Control;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;

public class GlobalManager {

	private static final String[] SOUND_SOURCE_TYPES = new String[] { "mono", "stereo" };

	public static String[] getSupportedSoundSourceTypes() {
		return (String[]) SOUND_SOURCE_TYPES.clone();
	}

	public static Control getControl(String controlType) {
		return null;
	}

	public static Control[] getControls() {
		return new Control[0];
	}

	public static EffectModule createEffectModule() throws MediaException {
		return new DefaultEffectModule();
	}

	public static SoundSource3D createSoundSource3D() throws MediaException {
		return new DefaultSoundSource3D();
	}

	public static SpectatorModelling getSpectatorModelling() {
		return new DefaultSpectatorModelling();
	}

	public static MediaProcessor createMediaProcessor(String contentType) throws MediaException {
		return new DefaultMediaProcessor();
	}

	private static class DefaultEffectModule implements EffectModule {
		public void addMIDIChannel(Player player, int channel) {}
		public void addPlayer(Player player) {}
		public void removeMIDIChannel(Player player, int channel) {}
		public void removePlayer(Player player) {}
		public Control getControl(String controlType) { return null; }
		public Control[] getControls() { return new Control[0]; }
	}

	private static class DefaultSoundSource3D implements SoundSource3D {
		public void addMIDIChannel(Player player, int channel) {}
		public void addPlayer(Player player) {}
		public void removeMIDIChannel(Player player, int channel) {}
		public void removePlayer(Player player) {}
		public Control getControl(String controlType) { return null; }
		public Control[] getControls() { return new Control[0]; }
	}

	private static class DefaultSpectatorModelling implements SpectatorModelling {
		public Control getControl(String controlType) { return null; }
		public Control[] getControls() { return new Control[0]; }
	}

	private static class DefaultMediaProcessor implements MediaProcessor {
		private int state = UNREALIZED;
		public void complete() throws MediaException {}
		public int getProgress() { return 100; }
		public int getState() { return state; }
		public void setInput(InputStream input, int length) throws MediaException { state = REALIZED; }
		public void setOutput(OutputStream output) {}
		public void start() throws MediaException { state = PROCESSING; }
		public void stop() throws MediaException { state = STOPPED; }
		public Control getControl(String controlType) { return null; }
		public Control[] getControls() { return new Control[0]; }
	}
}

