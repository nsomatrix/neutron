/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app;

import java.io.InputStream;
import java.util.ArrayList;
import org.microemu.DisplayComponent;
import org.microemu.EmulatorContext;
import org.microemu.MIDletBridge;
import org.microemu.app.Common;
import org.microemu.app.ui.Message;
import org.microemu.app.ui.noui.NoUiDisplayComponent;
import org.microemu.app.util.DeviceEntry;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.FontManager;
import org.microemu.device.InputMethod;
import org.microemu.device.j2se.J2SEDevice;
import org.microemu.device.j2se.J2SEDeviceDisplay;
import org.microemu.device.j2se.J2SEFontManager;
import org.microemu.device.j2se.J2SEInputMethod;
import org.microemu.log.Logger;

public class Headless {
    private Common emulator;
    private EmulatorContext context = new EmulatorContext(){
        private DisplayComponent displayComponent = new NoUiDisplayComponent();
        private InputMethod inputMethod = new J2SEInputMethod();
        private DeviceDisplay deviceDisplay = new J2SEDeviceDisplay(this);
        private FontManager fontManager = new J2SEFontManager();

        public DisplayComponent getDisplayComponent() {
            return this.displayComponent;
        }

        public InputMethod getDeviceInputMethod() {
            return this.inputMethod;
        }

        public DeviceDisplay getDeviceDisplay() {
            return this.deviceDisplay;
        }

        public FontManager getDeviceFontManager() {
            return this.fontManager;
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
    };

    public Headless() {
        this.emulator = new Common(this.context);
    }

    public static void main(String[] args) {
        StringBuffer debugArgs = new StringBuffer();
        ArrayList<String> params = new ArrayList<String>();
        params.add("--rms");
        params.add("memory");
        for (int i = 0; i < args.length; ++i) {
            params.add(args[i]);
            if (debugArgs.length() != 0) {
                debugArgs.append(", ");
            }
            debugArgs.append("[").append(args[i]).append("]");
        }
        if (args.length > 0) {
            Logger.debug("headless arguments", debugArgs.toString());
        }
        Headless app = new Headless();
        DeviceEntry defaultDevice = new DeviceEntry("Default device", null, "org/microemu/device/default/device.xml", true, false);
        app.emulator.initParams(params, defaultDevice, J2SEDevice.class);
        app.emulator.initMIDlet(true);
    }
}

