/*
 *  Neutron JSR-234 AMMS
 *
 *  Licensed under LGPL 2.1 or Apache 2.0.
 */

package javax.microedition.amms.control.audioeffect;

import javax.microedition.amms.control.EffectControl;

public interface EqualizerControl extends EffectControl {
	int getNumberOfBands();
	int getBand(int frequency);
	int getCenterFreq(int band);
	int getBandLevel(int band);
	void setBandLevel(int level, int band);
	int getMinBandLevel();
	int getMaxBandLevel();
}

