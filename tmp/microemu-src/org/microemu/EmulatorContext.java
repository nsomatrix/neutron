/*
 * Decompiled with CFR 0.152.
 */
package org.microemu;

import java.io.InputStream;
import org.microemu.DisplayComponent;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.FontManager;
import org.microemu.device.InputMethod;

public interface EmulatorContext {
    public DisplayComponent getDisplayComponent();

    public InputMethod getDeviceInputMethod();

    public DeviceDisplay getDeviceDisplay();

    public FontManager getDeviceFontManager();

    public InputStream getResourceAsStream(String var1);

    public boolean platformRequest(String var1);
}

