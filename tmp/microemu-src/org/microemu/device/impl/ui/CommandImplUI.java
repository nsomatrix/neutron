/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.impl.ui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Image;
import org.microemu.device.ui.CommandUI;

public class CommandImplUI
implements CommandUI {
    private Command command;

    public CommandImplUI(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return this.command;
    }

    public void setImage(Image image) {
    }
}

