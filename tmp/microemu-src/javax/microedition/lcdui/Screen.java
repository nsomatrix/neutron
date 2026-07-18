/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.lcdui;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.StringComponent;

public abstract class Screen
extends Displayable {
    Screen(String title) {
        super(title);
    }

    void scroll(int gameKeyCode) {
        this.viewPortY += this.traverse(gameKeyCode, this.viewPortY, this.viewPortY + this.viewPortHeight);
        this.repaint();
    }

    abstract int traverse(int var1, int var2, int var3);

    void keyPressed(int keyCode) {
        int gameKeyCode = Display.getGameAction(keyCode);
        if (gameKeyCode == 1 || gameKeyCode == 6) {
            this.viewPortY += this.traverse(gameKeyCode, this.viewPortY, this.viewPortY + this.viewPortHeight);
            this.repaint();
        }
    }

    void hideNotify() {
        super.hideNotify();
    }

    void keyRepeated(int keyCode) {
        this.keyPressed(keyCode);
    }

    final void paint(Graphics g) {
        int contentHeight = 0;
        if (this.viewPortY == 0) {
            this.currentDisplay.setScrollUp(false);
        } else {
            this.currentDisplay.setScrollUp(true);
        }
        g.setGrayScale(255);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.setGrayScale(0);
        if (this.getTicker() != null) {
            contentHeight += this.getTicker().paintContent(g);
        }
        g.translate(0, contentHeight);
        int translatedY = contentHeight;
        StringComponent title = new StringComponent(this.getTitle());
        contentHeight += title.paint(g);
        g.drawLine(0, title.getHeight(), this.getWidth(), title.getHeight());
        g.translate(0, ++contentHeight - translatedY);
        translatedY = contentHeight;
        g.setClip(0, 0, this.getWidth(), this.getHeight() - contentHeight);
        g.translate(0, -this.viewPortY);
        g.translate(0, this.viewPortY);
        if ((contentHeight += this.paintContent(g)) - this.viewPortY > this.getHeight()) {
            this.currentDisplay.setScrollDown(true);
        } else {
            this.currentDisplay.setScrollDown(false);
        }
        g.translate(0, -translatedY);
    }

    abstract int paintContent(Graphics var1);

    void repaint() {
        super.repaint();
    }

    void showNotify() {
        this.viewPortY = 0;
        super.showNotify();
    }
}

