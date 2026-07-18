/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.impl;

import javax.microedition.lcdui.Command;
import org.microemu.device.impl.Rectangle;

public interface SoftButton {
    public static final int TYPE_COMMAND = 1;
    public static final int TYPE_ICON = 2;

    public String getName();

    public int getType();

    public Command getCommand();

    public void setCommand(Command var1);

    public boolean isVisible();

    public void setVisible(boolean var1);

    public boolean isPressed();

    public void setPressed(boolean var1);

    public Rectangle getPaintable();

    public boolean preferredCommandType(Command var1);
}

