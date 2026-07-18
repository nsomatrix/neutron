/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.j2se;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Shape;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;
import org.microemu.device.Device;
import org.microemu.device.DeviceFactory;
import org.microemu.device.impl.Rectangle;
import org.microemu.device.impl.SoftButton;
import org.microemu.device.j2se.J2SEButton;
import org.microemu.device.j2se.J2SEDeviceDisplay;
import org.microemu.device.j2se.J2SEFont;
import org.microemu.device.j2se.J2SEFontManager;
import org.microemu.device.j2se.J2SEImmutableImage;

public class J2SESoftButton
extends J2SEButton
implements SoftButton {
    public static int LEFT = 1;
    public static int RIGHT = 2;
    private int type;
    private Image normalImage;
    private Image pressedImage;
    private Vector commandTypes = new Vector();
    private Command command = null;
    private Rectangle paintable;
    private int alignment;
    private boolean visible;
    private boolean pressed;
    private Font font;
    static /* synthetic */ Class class$javax$microedition$lcdui$Command;

    public J2SESoftButton(int skinVersion, String name, org.microemu.device.impl.Shape shape, int keyCode, String keyboardKeys, Rectangle paintable, String alignmentName, Vector commands, Font font) {
        super(skinVersion, name, shape, keyCode, keyboardKeys, null, new Hashtable(), false);
        this.type = 1;
        this.paintable = paintable;
        this.visible = true;
        this.pressed = false;
        this.font = font;
        if (alignmentName != null) {
            try {
                this.alignment = J2SESoftButton.class.getField(alignmentName).getInt(null);
            }
            catch (Exception ex) {
                System.err.println(ex);
            }
        }
        Enumeration e = commands.elements();
        while (e.hasMoreElements()) {
            String tmp = (String)e.nextElement();
            try {
                this.addCommandType((class$javax$microedition$lcdui$Command == null ? J2SESoftButton.class$("javax.microedition.lcdui.Command") : class$javax$microedition$lcdui$Command).getField(tmp).getInt(null));
            }
            catch (Exception ex) {
                System.err.println("a3" + ex);
            }
        }
    }

    public J2SESoftButton(int skinVersion, String name, Rectangle paintable, Image normalImage, Image pressedImage) {
        super(skinVersion, name, null, Integer.MIN_VALUE, null, null, null, false);
        this.type = 2;
        this.paintable = paintable;
        this.normalImage = normalImage;
        this.pressedImage = pressedImage;
        this.visible = true;
        this.pressed = false;
    }

    public int getType() {
        return this.type;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setCommand(Command cmd) {
        J2SESoftButton j2SESoftButton = this;
        synchronized (j2SESoftButton) {
            this.command = cmd;
        }
    }

    public Command getCommand() {
        return this.command;
    }

    public Rectangle getPaintable() {
        return this.paintable;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean state) {
        this.visible = state;
    }

    public boolean isPressed() {
        return this.pressed;
    }

    public void setPressed(boolean state) {
        this.pressed = state;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void paint(Graphics g) {
        if (!this.visible || this.paintable == null) {
            return;
        }
        Shape clip = g.getClip();
        g.setClip(this.paintable.x, this.paintable.y, this.paintable.width, this.paintable.height);
        if (this.type == 1) {
            int xoffset = 0;
            Device device = DeviceFactory.getDevice();
            J2SEDeviceDisplay deviceDisplay = (J2SEDeviceDisplay)device.getDeviceDisplay();
            if (this.pressed) {
                g.setColor(deviceDisplay.foregroundColor);
            } else {
                g.setColor(deviceDisplay.backgroundColor);
            }
            g.fillRect(this.paintable.x, this.paintable.y, this.paintable.width, this.paintable.height);
            J2SESoftButton j2SESoftButton = this;
            synchronized (j2SESoftButton) {
                if (this.command != null) {
                    if (this.font != null) {
                        J2SEFontManager fontManager = (J2SEFontManager)device.getFontManager();
                        J2SEFont buttonFont = (J2SEFont)fontManager.getFont(this.font);
                        g.setFont(buttonFont.getFont());
                    }
                    FontMetrics metrics = g.getFontMetrics();
                    if (this.alignment == RIGHT) {
                        xoffset = this.paintable.width - metrics.stringWidth(this.command.getLabel()) - 1;
                    }
                    if (this.pressed) {
                        g.setColor(deviceDisplay.backgroundColor);
                    } else {
                        g.setColor(deviceDisplay.foregroundColor);
                    }
                    g.drawString(this.command.getLabel(), this.paintable.x + xoffset, this.paintable.y + this.paintable.height - metrics.getDescent());
                }
            }
        } else if (this.type == 2) {
            if (this.pressed) {
                g.drawImage(((J2SEImmutableImage)this.pressedImage).getImage(), this.paintable.x, this.paintable.y, null);
            } else {
                g.drawImage(((J2SEImmutableImage)this.normalImage).getImage(), this.paintable.x, this.paintable.y, null);
            }
        }
        g.setClip(clip);
    }

    public boolean preferredCommandType(Command cmd) {
        Enumeration ct = this.commandTypes.elements();
        while (ct.hasMoreElements()) {
            if (cmd.getCommandType() != ((Integer)ct.nextElement()).intValue()) continue;
            return true;
        }
        return false;
    }

    public void addCommandType(int commandType) {
        this.commandTypes.addElement(new Integer(commandType));
    }
}

