/*
 *  Neutron JSR-234 AMMS
 *
 *  Licensed under LGPL 2.1 or Apache 2.0.
 */

package javax.microedition.amms.control;

import javax.microedition.media.Control;

public interface PanControl extends Control {
	int getPan();
	int setPan(int pan);
}

