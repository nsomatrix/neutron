/*
 *  Neutron JSR-234 AMMS
 *
 *  Licensed under LGPL 2.1 or Apache 2.0.
 */

package javax.microedition.amms.control;

import javax.microedition.media.Control;
import javax.microedition.media.MediaException;

public interface EffectControl extends Control {
	int SCOPE_LIVE_ONLY = 1;
	int SCOPE_RECORD_ONLY = 2;
	int SCOPE_LIVE_AND_RECORD = 3;

	int getScope();
	void setScope(int scope) throws MediaException;
	boolean isEnabled();
	void setEnabled(boolean enable);
	String[] getPresetNames();
	void setPreset(String preset);
	String getPreset();
}

