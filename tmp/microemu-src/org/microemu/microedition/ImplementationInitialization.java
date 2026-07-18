/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.microedition;

import java.util.Map;

public interface ImplementationInitialization {
    public static final String PARAM_EMULATOR_ID = "emulatorID";

    public void registerImplementation(Map var1);

    public void notifyMIDletStart();

    public void notifyMIDletDestroyed();
}

