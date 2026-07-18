/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.lcdui;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;
import org.microemu.GameCanvasKeyAccess;
import org.microemu.MIDletBridge;
import org.microemu.device.DeviceFactory;

public abstract class Canvas
extends Displayable {
    public static final int UP = 1;
    public static final int DOWN = 6;
    public static final int LEFT = 2;
    public static final int RIGHT = 5;
    public static final int FIRE = 8;
    public static final int GAME_A = 9;
    public static final int GAME_B = 10;
    public static final int GAME_C = 11;
    public static final int GAME_D = 12;
    public static final int KEY_NUM0 = 48;
    public static final int KEY_NUM1 = 49;
    public static final int KEY_NUM2 = 50;
    public static final int KEY_NUM3 = 51;
    public static final int KEY_NUM4 = 52;
    public static final int KEY_NUM5 = 53;
    public static final int KEY_NUM6 = 54;
    public static final int KEY_NUM7 = 55;
    public static final int KEY_NUM8 = 56;
    public static final int KEY_NUM9 = 57;
    public static final int KEY_STAR = 42;
    public static final int KEY_POUND = 35;

    protected Canvas() {
        super(null);
        super.setUI(DeviceFactory.getDevice().getUIFactory().createCanvasUI(this));
    }

    public int getGameAction(int keyCode) {
        return Display.getGameAction(keyCode);
    }

    public int getKeyCode(int gameAction) {
        return Display.getKeyCode(gameAction);
    }

    public String getKeyName(int keyCode) throws IllegalArgumentException {
        return Display.getKeyName(keyCode);
    }

    public boolean hasPointerEvents() {
        return this.device.hasPointerEvents();
    }

    public boolean hasPointerMotionEvents() {
        return this.device.hasPointerMotionEvents();
    }

    public boolean hasRepeatEvents() {
        return this.device.hasRepeatEvents();
    }

    protected void hideNotify() {
    }

    public boolean isDoubleBuffered() {
        return true;
    }

    protected void keyPressed(int keyCode) {
    }

    protected void keyRepeated(int keyCode) {
    }

    protected void keyReleased(int keyCode) {
    }

    protected abstract void paint(Graphics var1);

    protected void pointerPressed(int x, int y) {
    }

    protected void pointerReleased(int x, int y) {
    }

    protected void pointerDragged(int x, int y) {
    }

    public final void repaint() {
        super.repaint();
    }

    public final void repaint(int x, int y, int width, int height) {
        super.repaint(x, y, width, height);
    }

    public final void serviceRepaints() {
        if (this.currentDisplay != null) {
            this.currentDisplay.serviceRepaints();
        }
    }

    public void setFullScreenMode(boolean mode) {
        if (this.fullScreenMode != mode) {
            this.fullScreenMode = mode;
            if (this instanceof GameCanvas) {
                this.width = -1;
                this.height = -1;
                GameCanvasKeyAccess access = MIDletBridge.getGameCanvasKeyAccess((GameCanvas)this);
                access.initBuffer();
            }
            if (this.currentDisplay != null) {
                this.sizeChanged(this.currentDisplay);
            }
        }
    }

    protected void sizeChanged(int w, int h) {
    }

    protected void showNotify() {
    }
}

