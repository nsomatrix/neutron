/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.lcdui;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.Screen;

public class Gauge
extends Item {
    static int HEIGHT = 15;
    int value;
    int maxValue;
    boolean interactive;
    public static final int INDEFINITE = -1;
    public static final int CONTINUOUS_IDLE = 0;
    public static final int INCREMENTAL_IDLE = 1;
    public static final int CONTINUOUS_RUNNING = 2;
    public static final int INCREMENTAL_UPDATING = 3;
    private int indefiniteFrame;
    private static final int IDEFINITE_FRAMES = 4;
    static int PAINT_TIMEOUT = 500;
    int prefWidth;
    int prefHeight;

    public Gauge(String label, boolean interactive, int maxValue, int initialValue) {
        super(label);
        this.interactive = interactive;
        this.setMaxValue(maxValue);
        this.setValue(initialValue);
    }

    public void setValue(int value) {
        if (this.hasIndefiniteRange()) {
            if (value != 0 && value != 2 && value != 1 && value != 3) {
                throw new IllegalArgumentException();
            }
            if (value == 3 && this.value == 3) {
                this.updateIndefiniteFrame();
            } else {
                this.value = value;
                this.repaint();
            }
        } else {
            if (value < 0) {
                value = 0;
            }
            if (value > this.maxValue) {
                value = this.maxValue;
            }
            this.value = value;
            this.repaint();
        }
    }

    public int getValue() {
        return this.value;
    }

    public void setMaxValue(int maxValue) {
        if (maxValue > 0) {
            this.maxValue = maxValue;
            this.setValue(this.getValue());
        } else {
            if (this.isInteractive()) {
                throw new IllegalArgumentException();
            }
            if (maxValue != -1) {
                throw new IllegalArgumentException();
            }
            if (this.maxValue == -1) {
                return;
            }
            this.maxValue = -1;
            this.value = 1;
            this.repaint();
        }
    }

    public int getMaxValue() {
        return this.maxValue;
    }

    public boolean isInteractive() {
        return this.interactive;
    }

    boolean hasIndefiniteRange() {
        return !this.isInteractive() && this.getMaxValue() == -1;
    }

    void updateIndefiniteFrame() {
        if (this.hasIndefiniteRange() && (this.getValue() == 2 || this.getValue() == 3)) {
            this.indefiniteFrame = this.indefiniteFrame + 1 < 4 ? ++this.indefiniteFrame : 0;
            this.repaint();
        }
    }

    int getHeight() {
        return super.getHeight() + HEIGHT;
    }

    boolean isFocusable() {
        return this.interactive;
    }

    void keyPressed(int keyCode) {
        if (Display.getGameAction(keyCode) == 2 && this.value > 0) {
            --this.value;
            this.repaint();
        } else if (Display.getGameAction(keyCode) == 5 && this.value < this.maxValue) {
            ++this.value;
            this.repaint();
        }
    }

    int paint(Graphics g) {
        super.paintContent(g);
        g.translate(0, super.getHeight());
        if (this.hasFocus()) {
            g.drawRect(2, 2, this.owner.getWidth() - 5, HEIGHT - 5);
        }
        if (this.hasIndefiniteRange()) {
            if (this.getValue() == 0 || this.getValue() == 1) {
                int width = this.owner.getWidth() - 9;
                g.drawRect(4, 4, width, HEIGHT - 9);
            } else {
                int width = (this.owner.getWidth() - 8 << 1) / 4;
                int offset = (width >>> 1) * this.indefiniteFrame;
                int width2 = 0;
                if (offset + width > this.owner.getWidth() - 8) {
                    width2 = offset + width - (this.owner.getWidth() - 8);
                    width -= width2;
                }
                g.fillRect(4 + offset, 4, width, HEIGHT - 8);
                if (width2 != 0) {
                    g.fillRect(4, 4, width2, HEIGHT - 8);
                }
            }
        } else {
            int width = (this.owner.getWidth() - 8) * this.value / this.maxValue;
            g.fillRect(4, 4, width, HEIGHT - 8);
        }
        g.translate(0, -super.getHeight());
        return this.getHeight();
    }

    int traverse(int gameKeyCode, int top, int bottom, boolean action) {
        if (gameKeyCode == 1) {
            if (top > 0) {
                return -top;
            }
            return Integer.MAX_VALUE;
        }
        if (gameKeyCode == 6) {
            if (this.getHeight() > bottom) {
                return this.getHeight() - bottom;
            }
            return Integer.MAX_VALUE;
        }
        return 0;
    }

    public void setPreferredSize(int w, int h) {
        Screen owner = this.getOwner();
        if (owner != null && owner instanceof Alert) {
            return;
        }
        super.setPreferredSize(w, h);
    }

    public void setLayout(int layout) {
        if (this.owner != null && this.owner instanceof Alert) {
            return;
        }
        super.setLayout(layout);
    }

    public void setLabel(String label) {
        if (this.owner != null && this.owner instanceof Alert) {
            return;
        }
        super.setLabel(label);
    }

    public void addCommand(Command cmd) {
        if (this.owner != null && this.owner instanceof Alert) {
            return;
        }
        super.addCommand(cmd);
    }

    public void setDefaultCommand(Command cmd) {
        if (this.owner != null && this.owner instanceof Alert) {
            return;
        }
        super.setDefaultCommand(cmd);
    }

    public void setItemCommandListener(ItemCommandListener l) {
        if (this.owner != null && this.owner instanceof Alert) {
            return;
        }
        super.setItemCommandListener(l);
    }
}

