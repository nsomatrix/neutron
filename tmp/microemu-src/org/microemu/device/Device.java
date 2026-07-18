/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device;

import java.util.Map;
import java.util.Vector;
import javax.microedition.lcdui.Image;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.FontManager;
import org.microemu.device.InputMethod;
import org.microemu.device.ui.UIFactory;

public interface Device {
    public void init();

    public void destroy();

    public String getName();

    public InputMethod getInputMethod();

    public FontManager getFontManager();

    public DeviceDisplay getDeviceDisplay();

    public UIFactory getUIFactory();

    public Image getNormalImage();

    public Image getOverImage();

    public Image getPressedImage();

    public Vector getSoftButtons();

    public Vector getButtons();

    public boolean hasPointerEvents();

    public boolean hasPointerMotionEvents();

    public boolean hasRepeatEvents();

    public boolean vibrate(int var1);

    public Map getSystemProperties();
}

