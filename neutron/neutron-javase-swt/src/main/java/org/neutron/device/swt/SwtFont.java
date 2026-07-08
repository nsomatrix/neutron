package org.neutron.device.swt;

import org.eclipse.swt.graphics.Font;

public interface SwtFont extends org.neutron.device.impl.Font {
	
	Font getFont();

	void setAntialiasing(boolean antialiasing);
	
}
