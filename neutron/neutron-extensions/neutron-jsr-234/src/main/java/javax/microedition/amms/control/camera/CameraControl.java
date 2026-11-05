/*
 *  Neutron JSR-234 AMMS
 *
 *  Licensed under LGPL 2.1 or Apache 2.0.
 */

package javax.microedition.amms.control.camera;

import javax.microedition.media.Control;

public interface CameraControl extends Control {
	int getCameraRotation();
	void setCameraRotation(int rotation);
}

