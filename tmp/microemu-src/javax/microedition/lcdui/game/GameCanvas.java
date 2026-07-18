/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.lcdui.game;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import org.microemu.GameCanvasKeyAccess;
import org.microemu.MIDletBridge;

public abstract class GameCanvas
extends Canvas {
    public static final int UP_PRESSED = 2;
    public static final int DOWN_PRESSED = 64;
    public static final int LEFT_PRESSED = 4;
    public static final int RIGHT_PRESSED = 32;
    public static final int FIRE_PRESSED = 256;
    public static final int GAME_A_PRESSED = 512;
    public static final int GAME_B_PRESSED = 1024;
    public static final int GAME_C_PRESSED = 2048;
    public static final int GAME_D_PRESSED = 4096;
    private boolean suppressKeyEvents;
    private int latchedKeyState;
    private int actualKeyState;
    Image offscreenBuffer;

    protected GameCanvas(boolean suppressKeyEvents) {
        MIDletBridge.registerGameCanvasKeyAccess(this, new KeyAccess());
        this.suppressKeyEvents = suppressKeyEvents;
        this.offscreenBuffer = Image.createImage(this.getWidth(), this.getHeight());
    }

    protected Graphics getGraphics() {
        return this.offscreenBuffer.getGraphics();
    }

    public void paint(Graphics g) {
        g.drawImage(this.offscreenBuffer, 0, 0, 20);
    }

    public void flushGraphics(int x, int y, int width, int height) {
        this.repaint(x, y, width, height);
        this.serviceRepaints();
    }

    public void flushGraphics() {
        this.repaint();
        this.serviceRepaints();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getKeyStates() {
        GameCanvas gameCanvas = this;
        synchronized (gameCanvas) {
            int ret = this.latchedKeyState;
            this.latchedKeyState = this.actualKeyState;
            return ret;
        }
    }

    private class KeyAccess
    implements GameCanvasKeyAccess {
        private KeyAccess() {
        }

        public boolean suppressedKeyEvents(GameCanvas canvas) {
            return canvas.suppressKeyEvents;
        }

        public void recordKeyPressed(GameCanvas canvas, int gameCode) {
            int bit = 1 << gameCode;
            GameCanvas.this.latchedKeyState |= bit;
            GameCanvas.this.actualKeyState |= bit;
        }

        public void recordKeyReleased(GameCanvas canvas, int gameCode) {
            int bit = 1 << gameCode;
            GameCanvas.this.actualKeyState &= ~bit;
        }

        public void setActualKeyState(GameCanvas canvas, int keyState) {
            GameCanvas.this.actualKeyState = keyState;
        }

        public void initBuffer() {
            GameCanvas.this.offscreenBuffer = Image.createImage(GameCanvas.this.getWidth(), GameCanvas.this.getHeight());
        }
    }
}

