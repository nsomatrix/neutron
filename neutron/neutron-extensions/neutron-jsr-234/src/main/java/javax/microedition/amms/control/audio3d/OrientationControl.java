/*
 *  Neutron JSR-234 AMMS
 *
 *  Licensed under LGPL 2.1 or Apache 2.0.
 */

package javax.microedition.amms.control.audio3d;

import javax.microedition.media.Control;

public interface OrientationControl extends Control {
	int[] getOrientationVectors();
	void setOrientation(int[] front, int[] top);
}

