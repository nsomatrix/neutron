/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.lcdui;

import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.StringComponent;
import javax.microedition.lcdui.Ticker;
import org.microemu.device.Device;
import org.microemu.device.DeviceFactory;
import org.microemu.device.ui.CommandUI;
import org.microemu.device.ui.DisplayableUI;

public abstract class Displayable {
    Device device = DeviceFactory.getDevice();
    Display currentDisplay = null;
    int width = -1;
    int height = -1;
    boolean fullScreenMode = false;
    Ticker ticker;
    int viewPortY;
    int viewPortHeight;
    DisplayableUI ui;
    private String title;
    private CommandListener listener = null;

    Displayable(String title) {
        this.title = title;
    }

    void setUI(DisplayableUI ui) {
        this.ui = ui;
    }

    public void addCommand(Command cmd) {
        this.ui.addCommandUI(cmd.ui);
    }

    public void removeCommand(Command cmd) {
        this.ui.removeCommandUI(cmd.ui);
    }

    public int getWidth() {
        if (this.width == -1) {
            this.width = this.fullScreenMode ? this.device.getDeviceDisplay().getFullWidth() : this.device.getDeviceDisplay().getWidth();
        }
        return this.width;
    }

    public int getHeight() {
        if (this.height == -1) {
            this.height = this.fullScreenMode ? this.device.getDeviceDisplay().getFullHeight() : this.device.getDeviceDisplay().getHeight();
        }
        return this.height;
    }

    public boolean isShown() {
        if (this.currentDisplay == null) {
            return false;
        }
        return this.currentDisplay.isShown(this);
    }

    public Ticker getTicker() {
        return this.ticker;
    }

    public void setTicker(Ticker ticker) {
        this.ticker = ticker;
        this.repaint();
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String s) {
        this.title = s;
        this.ui.invalidate();
    }

    public void setCommandListener(CommandListener l) {
        this.listener = l;
        this.ui.setCommandListener(l);
    }

    CommandListener getCommandListener() {
        return this.listener;
    }

    Vector getCommands() {
        Vector<Command> result = new Vector<Command>();
        Vector commandsUI = this.ui.getCommandsUI();
        for (int i = 0; i < commandsUI.size(); ++i) {
            result.addElement(((CommandUI)commandsUI.elementAt(i)).getCommand());
        }
        return result;
    }

    void hideNotify() {
    }

    final void hideNotify(Display d) {
        this.ui.hideNotify();
        this.hideNotify();
    }

    void keyPressed(int keyCode) {
    }

    void keyRepeated(int keyCode) {
    }

    void keyReleased(int keyCode) {
    }

    void pointerPressed(int x, int y) {
    }

    void pointerReleased(int x, int y) {
    }

    void pointerDragged(int x, int y) {
    }

    abstract void paint(Graphics var1);

    void repaint() {
        if (this.currentDisplay != null) {
            this.repaint(0, 0, this.getWidth(), this.getHeight());
        }
    }

    void repaint(int x, int y, int width, int height) {
        if (this.currentDisplay != null) {
            this.currentDisplay.repaint(this, x, y, width, height);
        }
    }

    protected void sizeChanged(int w, int h) {
    }

    final void sizeChanged(Display d) {
        this.width = -1;
        this.height = -1;
        this.sizeChanged(this.getWidth(), this.getHeight());
    }

    void showNotify() {
    }

    final void showNotify(Display d) {
        this.currentDisplay = d;
        this.viewPortY = 0;
        StringComponent title = new StringComponent(this.getTitle());
        this.viewPortHeight = this.getHeight() - title.getHeight() - 1;
        if (this.ticker != null) {
            this.viewPortHeight -= this.ticker.getHeight();
        }
        int w = this.fullScreenMode ? this.device.getDeviceDisplay().getFullWidth() : this.device.getDeviceDisplay().getWidth();
        int h = this.fullScreenMode ? this.device.getDeviceDisplay().getFullHeight() : this.device.getDeviceDisplay().getHeight();
        if (this.width != w || this.height != h) {
            this.sizeChanged(d);
        }
        this.showNotify();
        this.ui.showNotify();
    }
}

