/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.ui;

import java.util.Vector;
import javax.microedition.lcdui.CommandListener;
import org.microemu.device.ui.CommandUI;

public interface DisplayableUI {
    public void addCommandUI(CommandUI var1);

    public void removeCommandUI(CommandUI var1);

    public void setCommandListener(CommandListener var1);

    public void hideNotify();

    public void showNotify();

    public void invalidate();

    public Vector getCommandsUI();
}

