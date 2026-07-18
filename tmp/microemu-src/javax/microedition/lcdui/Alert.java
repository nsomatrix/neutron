/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.lcdui;

import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageStringItem;
import javax.microedition.lcdui.Screen;
import org.microemu.device.DeviceFactory;
import org.microemu.device.ui.AlertUI;

public class Alert
extends Screen {
    public static final int FOREVER = -2;
    ImageStringItem alertContent;
    AlertType type;
    public static final Command DISMISS_COMMAND = new Command("OK", 4, 0);
    int time;
    Gauge indicator;
    static Displayable nextDisplayable;
    static CommandListener defaultListener;

    public Alert(String title) {
        this(title, null, null, null);
    }

    public Alert(String title, String alertText, Image alertImage, AlertType alertType) {
        super(title);
        super.setUI(DeviceFactory.getDevice().getUIFactory().createAlertUI(this));
        this.setTimeout(this.getDefaultTimeout());
        this.setString(alertText);
        this.setImage(alertImage);
        this.setType(alertType);
        super.addCommand(DISMISS_COMMAND);
        super.setCommandListener(defaultListener);
    }

    public void addCommand(Command cmd) {
        if (cmd == DISMISS_COMMAND) {
            return;
        }
        super.addCommand(cmd);
        super.removeCommand(DISMISS_COMMAND);
    }

    public void removeCommand(Command cmd) {
        if (cmd == DISMISS_COMMAND) {
            return;
        }
        super.removeCommand(cmd);
        if (this.getCommands().size() == 0) {
            super.addCommand(DISMISS_COMMAND);
        }
    }

    public int getDefaultTimeout() {
        return -2;
    }

    public String getString() {
        return this.alertContent.getText();
    }

    public int getTimeout() {
        return this.time;
    }

    public AlertType getType() {
        return this.type;
    }

    public void setType(AlertType type) {
        this.type = type;
        this.repaint();
    }

    public void setCommandListener(CommandListener l) {
        if (l == null) {
            l = defaultListener;
        }
        super.setCommandListener(l);
    }

    public Image getImage() {
        return this.alertContent.getImage();
    }

    public void setImage(Image img) {
        if (this.alertContent == null) {
            this.alertContent = new ImageStringItem(null, img, null);
        }
        if (img != null && img.isMutable()) {
            img = Image.createImage(img);
        }
        this.alertContent.setImage(img);
        this.repaint();
    }

    public Gauge getIndicator() {
        return this.indicator;
    }

    public void setIndicator(Gauge indicator) {
        if (indicator == null) {
            if (this.indicator != null) {
                this.indicator.setOwner(null);
            }
            this.indicator = null;
            this.repaint();
            return;
        }
        if (indicator.getLayout() != 0 || indicator.getLabel() != null || indicator.prefHeight != -1 || indicator.prefWidth != -1 || indicator.commandListener != null || indicator.isInteractive() || indicator.getOwner() != null || !indicator.commands.isEmpty()) {
            throw new IllegalArgumentException("This gauge cannot be added to an Alert");
        }
        indicator.setOwner(this);
        this.indicator = indicator;
        this.repaint();
    }

    public void setString(String str) {
        if (this.ui.getClass().getName().equals("org.microemu.android.device.ui.AndroidAlertUI")) {
            ((AlertUI)this.ui).setString(str);
        }
        if (this.alertContent == null) {
            this.alertContent = new ImageStringItem(null, null, str);
        }
        this.alertContent.setText(str);
        this.repaint();
    }

    public void setTimeout(int time) {
        if (time != -2 && time <= 0) {
            throw new IllegalArgumentException();
        }
        if (time != -2 && this.getCommands().size() > 1) {
            time = -2;
        }
        this.time = time;
    }

    private int getContentHeight() {
        return this.alertContent.getHeight();
    }

    int paintContent(Graphics g) {
        return this.alertContent.paint(g);
    }

    void showNotify() {
        super.showNotify();
        this.viewPortY = 0;
    }

    int traverse(int gameKeyCode, int top, int bottom) {
        Font f = Font.getDefaultFont();
        if (gameKeyCode == 1 && top != 0) {
            return -f.getHeight();
        }
        if (gameKeyCode == 6 && bottom < this.getContentHeight()) {
            return f.getHeight();
        }
        return 0;
    }

    static {
        defaultListener = new CommandListener(){

            public void commandAction(Command cmd, Displayable d) {
                ((Alert)d).currentDisplay.setCurrent(nextDisplayable);
            }
        };
    }
}

