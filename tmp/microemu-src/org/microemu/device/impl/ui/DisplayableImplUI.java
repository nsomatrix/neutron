/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.impl.ui;

import java.util.Vector;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import org.microemu.DisplayAccess;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.device.impl.ui.CommandManager;
import org.microemu.device.ui.CommandUI;
import org.microemu.device.ui.DisplayableUI;

public class DisplayableImplUI
implements DisplayableUI {
    protected Displayable displayable;
    private Vector commands = new Vector();

    protected DisplayableImplUI(Displayable displayable) {
        this.displayable = displayable;
    }

    public void addCommandUI(CommandUI cmd) {
        for (int i = 0; i < this.commands.size(); ++i) {
            if (cmd != (CommandUI)this.commands.elementAt(i)) continue;
            return;
        }
        boolean inserted = false;
        for (int i = 0; i < this.commands.size(); ++i) {
            if (cmd.getCommand().getPriority() >= ((CommandUI)this.commands.elementAt(i)).getCommand().getPriority()) continue;
            this.commands.insertElementAt(cmd, i);
            inserted = true;
            break;
        }
        if (!inserted) {
            this.commands.addElement(cmd);
        }
        if (this.displayable.isShown()) {
            this.updateCommands();
        }
    }

    public void removeCommandUI(CommandUI cmd) {
        this.commands.removeElement(cmd);
        if (this.displayable.isShown()) {
            this.updateCommands();
        }
    }

    public void setCommandListener(CommandListener l) {
    }

    public void hideNotify() {
    }

    public void showNotify() {
        this.updateCommands();
    }

    public void invalidate() {
    }

    public Vector getCommandsUI() {
        return this.commands;
    }

    private void updateCommands() {
        CommandManager.getInstance().updateCommands(this.getCommandsUI());
        MIDletAccess ma = MIDletBridge.getMIDletAccess();
        if (ma == null) {
            return;
        }
        DisplayAccess da = ma.getDisplayAccess();
        if (da == null) {
            return;
        }
        da.repaint();
    }
}

