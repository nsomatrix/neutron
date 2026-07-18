/*
 * Decompiled with CFR 0.152.
 */
package com.barteo.emulator.device;

import com.barteo.emulator.EmulatorContext;
import org.microemu.device.impl.DeviceImpl;
import org.microemu.device.ui.UIFactory;

public class Device
extends DeviceImpl {
    public void init(EmulatorContext context) {
        super.init(context);
    }

    public void init(EmulatorContext context, String config) {
        super.init(context, config);
    }

    public UIFactory getUIFactory() {
        return null;
    }
}

