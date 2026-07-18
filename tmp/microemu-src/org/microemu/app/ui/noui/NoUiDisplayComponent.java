/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.ui.noui;

import java.awt.Graphics;
import javax.microedition.lcdui.Displayable;
import org.microemu.DisplayAccess;
import org.microemu.DisplayComponent;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.app.ui.DisplayRepaintListener;
import org.microemu.device.Device;
import org.microemu.device.DeviceFactory;
import org.microemu.device.MutableImage;
import org.microemu.device.j2se.J2SEDeviceDisplay;
import org.microemu.device.j2se.J2SEMutableImage;

public class NoUiDisplayComponent
implements DisplayComponent {
    private J2SEMutableImage displayImage = null;
    private DisplayRepaintListener displayRepaintListener;

    public void addDisplayRepaintListener(DisplayRepaintListener l) {
        this.displayRepaintListener = l;
    }

    public void removeDisplayRepaintListener(DisplayRepaintListener l) {
        if (this.displayRepaintListener == l) {
            this.displayRepaintListener = null;
        }
    }

    public MutableImage getDisplayImage() {
        return this.displayImage;
    }

    public void repaintRequest(int x, int y, int width, int height) {
        MIDletAccess ma = MIDletBridge.getMIDletAccess();
        if (ma == null) {
            return;
        }
        DisplayAccess da = ma.getDisplayAccess();
        if (da == null) {
            return;
        }
        Displayable current = da.getCurrent();
        if (current == null) {
            return;
        }
        Device device = DeviceFactory.getDevice();
        if (device != null) {
            if (this.displayImage == null) {
                this.displayImage = new J2SEMutableImage(device.getDeviceDisplay().getFullWidth(), device.getDeviceDisplay().getFullHeight());
            }
            Graphics gc = this.displayImage.getImage().getGraphics();
            J2SEDeviceDisplay deviceDisplay = (J2SEDeviceDisplay)device.getDeviceDisplay();
            if (!deviceDisplay.isFullScreenMode()) {
                deviceDisplay.paintControls(gc);
            }
            deviceDisplay.paintDisplayable(gc, x, y, width, height);
            this.fireDisplayRepaint(this.displayImage);
        }
    }

    private void fireDisplayRepaint(MutableImage image) {
        if (this.displayRepaintListener != null) {
            this.displayRepaintListener.repaintInvoked(image);
        }
    }
}

