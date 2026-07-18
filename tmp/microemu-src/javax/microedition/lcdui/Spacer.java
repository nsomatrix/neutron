/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.lcdui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Item;

public class Spacer
extends Item {
    public Spacer(int minWidth, int minHeight) {
        super(null);
        this.setMinimumSize(minWidth, minHeight);
    }

    public void setLabel(String label) {
        throw new IllegalStateException("Spacer items can't have labels");
    }

    public void addCommand(Command cmd) {
        throw new IllegalStateException("Spacer items can't have commands");
    }

    public void setDefaultCommand(Command cmd) {
        throw new IllegalStateException("Spacer items can't have commands");
    }

    public void setMinimumSize(int minWidth, int minHeight) {
        if (minWidth < 0 || minHeight < 0) {
            throw new IllegalArgumentException();
        }
    }

    int paint(Graphics g) {
        return 0;
    }
}

