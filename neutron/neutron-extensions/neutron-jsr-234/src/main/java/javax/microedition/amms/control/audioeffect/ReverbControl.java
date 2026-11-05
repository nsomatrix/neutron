/*
 *  Neutron JSR-234 AMMS
 *
 *  Licensed under LGPL 2.1 or Apache 2.0.
 */

package javax.microedition.amms.control.audioeffect;

import javax.microedition.amms.control.EffectControl;

public interface ReverbControl extends EffectControl {
	int getReverbLevel();
	int getReverbTime();
	void setReverbLevel(int level);
	void setReverbTime(int time);
}

