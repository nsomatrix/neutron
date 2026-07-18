/*
 * Decompiled with CFR 0.152.
 */
package com.barteo.emulator;

import java.io.InputStream;
import org.microemu.DisplayComponent;
import org.microemu.MIDletBridge;
import org.microemu.app.ui.Message;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.FontManager;
import org.microemu.device.InputMethod;

public class EmulatorContext
implements org.microemu.EmulatorContext {
    private org.microemu.EmulatorContext context;

    public EmulatorContext(org.microemu.EmulatorContext context) {
        this.context = context;
    }

    public DeviceDisplay getDeviceDisplay() {
        return this.context.getDeviceDisplay();
    }

    public FontManager getDeviceFontManager() {
        return this.context.getDeviceFontManager();
    }

    public InputMethod getDeviceInputMethod() {
        return this.context.getDeviceInputMethod();
    }

    public DisplayComponent getDisplayComponent() {
        return this.context.getDisplayComponent();
    }

    public InputStream getResourceAsStream(String name) {
        return MIDletBridge.getCurrentMIDlet().getClass().getResourceAsStream(name);
    }

    public boolean platformRequest(final String URL2) {
        new Thread(new Runnable(){

            public void run() {
                Message.info("MIDlet requests that the device handle the following URL: " + URL2);
            }
        }).start();
        return false;
    }
}

