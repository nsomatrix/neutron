/*
 *  Neutron
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.neutron.media.control;

import javax.microedition.media.control.VolumeControl;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.Synthesizer;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;

public class J2SEVolumeControl implements VolumeControl
{
    private int level = 100;
    private boolean muted = false;
    private Line audioLine;
    private Synthesizer synthesizer;

    public void setAudioLine(Line line) {
        this.audioLine = line;
        applyVolume();
    }

    public void setSynthesizer(Synthesizer synth) {
        this.synthesizer = synth;
        applyVolume();
    }

    public int getLevel()
    {
        return level;
    }

    public boolean isMuted()
    {
        return muted;
    }

    public synchronized int setLevel(int level)
    {
        if (level < 0) {
            level = 0;
        } else if (level > 100) {
            level = 100;
        }
        this.level = level;
        applyVolume();
        return this.level;
    }

    public synchronized void setMute(boolean mute)
    {
        this.muted = mute;
        applyVolume();
    }

    private void applyVolume() {
        if (audioLine != null && audioLine.isOpen()) {
            try {
                if (audioLine.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl gain = (FloatControl) audioLine.getControl(FloatControl.Type.MASTER_GAIN);
                    if (muted || level <= 0) {
                        gain.setValue(gain.getMinimum());
                    } else {
                        float min = gain.getMinimum();
                        float max = gain.getMaximum();
                        float dB = (float) (Math.log10(level / 100.0) * 20.0);
                        if (dB < min) dB = min;
                        if (dB > max) dB = max;
                        gain.setValue(dB);
                    }
                }
            } catch (Throwable ignored) {}
        }

        if (synthesizer != null && synthesizer.isOpen()) {
            try {
                MidiChannel[] channels = synthesizer.getChannels();
                if (channels != null) {
                    int midiVol = (muted || level <= 0) ? 0 : (int) Math.round(level * 1.27);
                    for (int i = 0; i < channels.length; i++) {
                        if (channels[i] != null) {
                            channels[i].controlChange(7, midiVol);
                        }
                    }
                }
            } catch (Throwable ignored) {}
        }
    }

}
