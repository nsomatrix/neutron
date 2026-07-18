/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.lcdui;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import org.microemu.device.DeviceFactory;
import org.microemu.device.ui.CommandUI;

public class Command {
    public static final int SCREEN = 1;
    public static final int BACK = 2;
    public static final int CANCEL = 3;
    public static final int OK = 4;
    public static final int HELP = 5;
    public static final int STOP = 6;
    public static final int EXIT = 7;
    public static final int ITEM = 8;
    private Command originalCommand;
    private Item focusedItem;
    private Command itemCommand;
    String label;
    int commandType;
    int priority;
    CommandUI ui;

    public Command(String label, int commandType, int priority) {
        this.label = label;
        this.commandType = commandType;
        this.priority = priority;
        this.ui = DeviceFactory.getDevice().getUIFactory().createCommandUI(this);
    }

    public Command(String shortLabel, String longLabel, int commandType, int priority) {
        this(shortLabel, commandType, priority);
    }

    public int getCommandType() {
        return this.commandType;
    }

    public String getLabel() {
        return this.label;
    }

    public String getLongLabel() {
        return this.label;
    }

    public int getPriority() {
        return this.priority;
    }

    public void setImage(Image image) {
        this.ui.setImage(image);
    }

    Item getFocusedItem() {
        if (this.isRegularCommand()) {
            throw new IllegalStateException();
        }
        return this.focusedItem;
    }

    Command getItemCommand(Item item) {
        if (!this.isRegularCommand()) {
            throw new IllegalStateException();
        }
        if (item == null) {
            throw new NullPointerException();
        }
        if (this.itemCommand == null) {
            this.itemCommand = new Command(this.getLabel(), 8, this.getPriority());
            this.itemCommand.originalCommand = this;
        }
        this.itemCommand.focusedItem = item;
        return this.itemCommand;
    }

    Command getOriginalCommand() {
        if (this.isRegularCommand()) {
            throw new IllegalStateException();
        }
        return this.originalCommand;
    }

    boolean isRegularCommand() {
        return this.originalCommand == null;
    }
}

