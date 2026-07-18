/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.impl.ui;

import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import org.microemu.MIDletBridge;
import org.microemu.device.DeviceFactory;
import org.microemu.device.impl.SoftButton;
import org.microemu.device.ui.CommandUI;

public class CommandManager {
    public static final Command CMD_MENU = new Command("Menu", 7, 0);
    private static final Command CMD_BACK = new Command("Back", 2, 0);
    private static final Command CMD_SELECT = new Command("Select", 4, 0);
    private static CommandManager instance = new CommandManager();
    private List menuList = null;
    private Vector menuCommands;
    private Displayable previous;
    private CommandListener menuCommandListener = new CommandListener(){

        public void commandAction(Command c, Displayable d) {
            if (CommandManager.this.menuList == null) {
                CommandManager.this.lateInit();
            }
            MIDletBridge.getMIDletAccess().getDisplayAccess().setCurrent(CommandManager.this.previous);
            if (c == CMD_SELECT || c == List.SELECT_COMMAND) {
                MIDletBridge.getMIDletAccess().getDisplayAccess().commandAction((Command)CommandManager.this.menuCommands.elementAt(CommandManager.this.menuList.getSelectedIndex()), CommandManager.this.previous);
            }
        }
    };

    private CommandManager() {
    }

    private void lateInit() {
        this.menuList = new List("Menu", 3);
        this.menuList.addCommand(CMD_BACK);
        this.menuList.addCommand(CMD_SELECT);
        this.menuList.setCommandListener(this.menuCommandListener);
    }

    public static CommandManager getInstance() {
        return instance;
    }

    public void commandAction(Command command) {
        if (this.menuList == null) {
            this.lateInit();
        }
        this.previous = MIDletBridge.getMIDletAccess().getDisplayAccess().getCurrent();
        MIDletBridge.getMIDletAccess().getDisplayAccess().setCurrent(this.menuList);
    }

    public void updateCommands(Vector commands) {
        int i;
        if (this.menuList == null) {
            this.lateInit();
        }
        Vector buttons = DeviceFactory.getDevice().getSoftButtons();
        int numOfButtons = 0;
        Enumeration en = buttons.elements();
        while (en.hasMoreElements()) {
            SoftButton button = (SoftButton)en.nextElement();
            if (button.getType() != 1) continue;
            button.setCommand(null);
            ++numOfButtons;
        }
        if (commands == null) {
            return;
        }
        Vector<Command> commandsTable = new Vector<Command>();
        for (i = 0; i < commands.size(); ++i) {
            commandsTable.addElement(null);
        }
        en = commands.elements();
        block2: while (en.hasMoreElements()) {
            Command commandToSort = ((CommandUI)en.nextElement()).getCommand();
            for (int i2 = 0; i2 < commandsTable.size(); ++i2) {
                if (commandsTable.elementAt(i2) == null) {
                    commandsTable.setElementAt(commandToSort, i2);
                    continue block2;
                }
                if (commandToSort.getPriority() >= ((Command)commandsTable.elementAt(i2)).getPriority()) continue;
                for (int j = commandsTable.size() - 1; j > i2; --j) {
                    if (commandsTable.elementAt(j - 1) == null) continue;
                    commandsTable.setElementAt((Command)commandsTable.elementAt(j - 1), j);
                }
            }
        }
        if (commandsTable.size() <= numOfButtons) {
            this.fillPossibleCommands(buttons, commandsTable);
            return;
        }
        commandsTable.insertElementAt(CMD_MENU, 0);
        this.fillPossibleCommands(buttons, commandsTable);
        while (this.menuList.size() > 0) {
            this.menuList.delete(0);
        }
        for (i = 0; i < commandsTable.size(); ++i) {
            this.menuCommands = commandsTable;
            this.menuList.append(((Command)commandsTable.elementAt(i)).getLabel(), null);
        }
    }

    private void fillPossibleCommands(Vector buttons, Vector commandsTable) {
        SoftButton button;
        Enumeration en;
        int i;
        block0: for (i = 0; i < commandsTable.size(); ++i) {
            en = buttons.elements();
            while (en.hasMoreElements()) {
                button = (SoftButton)en.nextElement();
                if (button.getType() != 1 || button.getCommand() != null || !button.preferredCommandType((Command)commandsTable.elementAt(i))) continue;
                button.setCommand((Command)commandsTable.elementAt(i));
                commandsTable.removeElementAt(i);
                --i;
                continue block0;
            }
        }
        block2: for (i = 0; i < commandsTable.size(); ++i) {
            en = buttons.elements();
            while (en.hasMoreElements()) {
                button = (SoftButton)en.nextElement();
                if (button.getType() != 1 || button.getCommand() != null) continue;
                button.setCommand((Command)commandsTable.elementAt(i));
                commandsTable.removeElementAt(i);
                --i;
                continue block2;
            }
        }
        Enumeration hiddenEn = buttons.elements();
        block4: while (hiddenEn.hasMoreElements()) {
            SoftButton hiddenButton = (SoftButton)hiddenEn.nextElement();
            if (hiddenButton.getType() != 1 || hiddenButton.getPaintable() != null || hiddenButton.getCommand() == null) continue;
            Enumeration en2 = buttons.elements();
            while (en2.hasMoreElements()) {
                SoftButton button2 = (SoftButton)en2.nextElement();
                if (button2.getType() != 1 || button2.getPaintable() == null || button2.getCommand() != null) continue;
                button2.setCommand(hiddenButton.getCommand());
                continue block4;
            }
        }
    }
}

