/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.lcdui;

import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.Screen;
import javax.microedition.lcdui.StringComponent;
import org.microemu.device.DeviceFactory;

public abstract class Item {
    static final int OUTOFITEM = Integer.MAX_VALUE;
    public static final int LAYOUT_DEFAULT = 0;
    public static final int LAYOUT_LEFT = 1;
    public static final int LAYOUT_RIGHT = 2;
    public static final int LAYOUT_CENTER = 3;
    public static final int LAYOUT_TOP = 16;
    public static final int LAYOUT_BOTTOM = 32;
    public static final int LAYOUT_VCENTER = 48;
    public static final int LAYOUT_NEWLINE_BEFORE = 256;
    public static final int LAYOUT_NEWLINE_AFTER = 512;
    public static final int LAYOUT_SHRINK = 1024;
    public static final int LAYOUT_EXPAND = 2048;
    public static final int LAYOUT_VSHRINK = 4096;
    public static final int LAYOUT_VEXPAND = 8192;
    public static final int LAYOUT_2 = 16384;
    public static final int PLAIN = 0;
    public static final int HYPERLINK = 1;
    public static final int BUTTON = 2;
    StringComponent labelComponent;
    Screen owner = null;
    private boolean focus = false;
    int layout;
    Vector commands;
    Command defaultCommand;
    ItemCommandListener commandListener;
    private int prefWidth;
    private int prefHeight;

    Item(String label) {
        this.labelComponent = new StringComponent(label);
        this.commands = new Vector();
    }

    public void addCommand(Command cmd) {
        if (cmd == null) {
            throw new NullPointerException();
        }
        if (!this.commands.contains(cmd)) {
            boolean inserted = false;
            for (int i = 0; i < this.commands.size(); ++i) {
                if (cmd.getPriority() >= ((Command)this.commands.elementAt(i)).getPriority()) continue;
                this.commands.insertElementAt(cmd, i);
                inserted = true;
                break;
            }
            if (!inserted) {
                this.commands.addElement(cmd);
            }
            this.repaintOwner();
        }
    }

    public String getLabel() {
        return this.labelComponent.getText();
    }

    public int getLayout() {
        return this.layout;
    }

    public int getMinimumHeight() {
        if (this.labelComponent != null) {
            return this.labelComponent.getHeight();
        }
        return 0;
    }

    public int getMinimumWidth() {
        return this.getMaximumWidth();
    }

    public int getPreferredHeight() {
        int ret = this.prefHeight;
        int min = this.getMinimumHeight();
        int max = this.getMaximumHeight();
        if (ret == -1) {
            return min;
        }
        if (ret < min) {
            ret = min;
        } else if (ret > max) {
            ret = max;
        }
        return ret;
    }

    public int getPreferredWidth() {
        int ret = this.prefWidth;
        int min = this.getMinimumWidth();
        int max = this.getMaximumWidth();
        if (ret == -1) {
            return max;
        }
        if (ret < min) {
            ret = min;
        } else if (ret > max) {
            ret = max;
        }
        return ret;
    }

    public void notifyStateChanged() {
        Screen owner = this.getOwner();
        if (owner != null && owner instanceof Form) {
            Form form = (Form)owner;
            form.fireItemStateListener(this);
        }
    }

    public void removeCommand(Command cmd) {
        this.commands.removeElement(cmd);
        if (this.defaultCommand == cmd) {
            this.defaultCommand = null;
        }
        this.repaintOwner();
    }

    public void setDefaultCommand(Command cmd) {
        this.defaultCommand = cmd;
        if (cmd != null) {
            if (this.commands.contains(cmd)) {
                this.addCommand(cmd);
            } else {
                this.repaintOwner();
            }
        } else {
            this.repaintOwner();
        }
    }

    public void setItemCommandListener(ItemCommandListener l) {
        this.commandListener = l;
    }

    public void setLabel(String label) {
        this.labelComponent.setText(label);
        this.repaint();
    }

    public void setLayout(int layout) {
        if ((layout & 0x400) != 0 && (layout & 0x800) != 0 || (layout & 0x1000) != 0 && (layout & 0x2000) != 0) {
            throw new IllegalArgumentException("Bad combination of layout policies");
        }
        this.layout = layout;
        this.repaint();
    }

    public void setPreferredSize(int width, int height) {
        if (width < -1 || height < -1) {
            throw new IllegalArgumentException();
        }
        this.prefWidth = width;
        this.prefHeight = height;
        this.repaint();
    }

    void repaintOwner() {
        Screen owner = this.getOwner();
        if (owner != null) {
            owner.repaint();
        }
    }

    int getHeight() {
        return this.labelComponent.getHeight();
    }

    boolean isFocusable() {
        return false;
    }

    void keyPressed(int keyCode) {
    }

    abstract int paint(Graphics var1);

    void paintContent(Graphics g) {
        this.labelComponent.paint(g);
    }

    void repaint() {
        if (this.owner != null) {
            this.owner.repaint();
        }
    }

    boolean hasFocus() {
        return this.focus;
    }

    void setFocus(boolean state) {
        this.focus = state;
    }

    Screen getOwner() {
        return this.owner;
    }

    void setOwner(Screen owner) {
        this.owner = owner;
        if (owner == null) {
            this.setFocus(false);
        }
    }

    boolean select() {
        if (this.defaultCommand != null && this.commandListener != null) {
            this.commandListener.commandAction(this.defaultCommand, this);
            return true;
        }
        return false;
    }

    int traverse(int gameKeyCode, int top, int bottom, boolean action) {
        return 0;
    }

    int getMaximumHeight() {
        if (this.owner != null) {
            return this.owner.getHeight() * 10;
        }
        return DeviceFactory.getDevice().getDeviceDisplay().getHeight() * 10;
    }

    int getMaximumWidth() {
        if (this.owner != null) {
            return this.owner.getWidth() - 3;
        }
        return DeviceFactory.getDevice().getDeviceDisplay().getWidth() - 3;
    }

    ItemCommandListener getItemCommandListener() {
        return this.commandListener;
    }
}

