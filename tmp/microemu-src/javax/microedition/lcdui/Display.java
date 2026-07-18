/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.lcdui;

import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.Ticker;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.midlet.MIDlet;
import org.microemu.DisplayAccess;
import org.microemu.GameCanvasKeyAccess;
import org.microemu.MIDletBridge;
import org.microemu.device.DeviceFactory;
import org.microemu.device.ui.DisplayableUI;
import org.microemu.device.ui.EventDispatcher;

public class Display {
    public static final int LIST_ELEMENT = 1;
    public static final int CHOICE_GROUP_ELEMENT = 2;
    public static final int ALERT = 3;
    public static final int COLOR_BACKGROUND = 0;
    public static final int COLOR_FOREGROUND = 1;
    public static final int COLOR_HIGHLIGHTED_BACKGROUND = 2;
    public static final int COLOR_HIGHLIGHTED_FOREGROUND = 3;
    public static final int COLOR_BORDER = 4;
    public static final int COLOR_HIGHLIGHTED_BORDER = 5;
    private Displayable current = null;
    private DisplayAccessor accessor = null;
    private EventDispatcher eventDispatcher;
    private final Timer timer = new Timer();

    Display() {
        this.accessor = new DisplayAccessor(this);
        this.eventDispatcher = DeviceFactory.getDevice().getUIFactory().createEventDispatcher(this);
        this.timer.scheduleAtFixedRate((TimerTask)new RunnableWrapper(new TickerPaintTask()), 0L, (long)Ticker.PAINT_TIMEOUT);
        this.timer.scheduleAtFixedRate((TimerTask)new RunnableWrapper(new GaugePaintTask()), 0L, (long)Ticker.PAINT_TIMEOUT);
    }

    public void callSerially(Runnable runnable) {
        this.eventDispatcher.put(runnable);
    }

    public int numAlphaLevels() {
        return DeviceFactory.getDevice().getDeviceDisplay().numAlphaLevels();
    }

    public int numColors() {
        return DeviceFactory.getDevice().getDeviceDisplay().numColors();
    }

    public boolean flashBacklight(int duration) {
        return false;
    }

    public static Display getDisplay(MIDlet m) {
        Display result;
        if (MIDletBridge.getMIDletAccess(m).getDisplayAccess() == null) {
            result = new Display();
            MIDletBridge.getMIDletAccess(m).setDisplayAccess(result.accessor);
        } else {
            result = MIDletBridge.getMIDletAccess(m).getDisplayAccess().getDisplay();
        }
        return result;
    }

    public int getColor(int colorSpecifier) {
        switch (colorSpecifier) {
            case 0: 
            case 3: 
            case 5: {
                return 0xFFFFFF;
            }
        }
        return 0;
    }

    public int getBorderStyle(boolean highlighted) {
        return highlighted ? 1 : 0;
    }

    public int getBestImageWidth(int imageType) {
        return 0;
    }

    public int getBestImageHeight(int imageType) {
        return 0;
    }

    public Displayable getCurrent() {
        return this.current;
    }

    public boolean isColor() {
        return DeviceFactory.getDevice().getDeviceDisplay().isColor();
    }

    public void setCurrent(final Displayable nextDisplayable) {
        if (nextDisplayable != null) {
            EventDispatcher eventDispatcher = this.eventDispatcher;
            eventDispatcher.getClass();
            this.eventDispatcher.put(new EventDispatcher.ShowNotifyEvent(eventDispatcher, new Runnable(){

                public void run() {
                    if (Display.this.current != null) {
                        EventDispatcher eventDispatcher = Display.this.eventDispatcher;
                        EventDispatcher eventDispatcher2 = Display.this.eventDispatcher;
                        eventDispatcher2.getClass();
                        eventDispatcher.put(new EventDispatcher.HideNotifyEvent(eventDispatcher2, new Runnable(){
                            private Displayable displayable;
                            {
                                this.displayable = Display.this.current;
                            }

                            public void run() {
                                this.displayable.hideNotify(Display.this);
                            }
                        }));
                    }
                    if (nextDisplayable instanceof Alert) {
                        Display.this.setCurrent((Alert)nextDisplayable, Display.this.current);
                        return;
                    }
                    nextDisplayable.showNotify(Display.this);
                    Display.this.current = nextDisplayable;
                    Display.this.setScrollUp(false);
                    Display.this.setScrollDown(false);
                    nextDisplayable.repaint();
                }
            }));
        }
    }

    public void setCurrent(Alert alert, Displayable nextDisplayable) {
        Alert.nextDisplayable = nextDisplayable;
        this.current = alert;
        this.current.showNotify(this);
        this.current.repaint();
        if (alert.getTimeout() != -2) {
            AlertTimeout at = new AlertTimeout(alert.getTimeout());
            Thread t = new Thread(at);
            t.start();
        }
    }

    public void setCurrentItem(Item item) {
    }

    public boolean vibrate(int duration) {
        return DeviceFactory.getDevice().vibrate(duration);
    }

    void clearAlert() {
        this.setCurrent(Alert.nextDisplayable);
    }

    static int getGameAction(int keyCode) {
        return DeviceFactory.getDevice().getInputMethod().getGameAction(keyCode);
    }

    static int getKeyCode(int gameAction) {
        return DeviceFactory.getDevice().getInputMethod().getKeyCode(gameAction);
    }

    static String getKeyName(int keyCode) throws IllegalArgumentException {
        return DeviceFactory.getDevice().getInputMethod().getKeyName(keyCode);
    }

    boolean isShown(Displayable d) {
        return this.current != null && this.current == d;
    }

    void repaint(Displayable d, int x, int y, int width, int height) {
        if (this.current == d) {
            EventDispatcher eventDispatcher = this.eventDispatcher;
            eventDispatcher.getClass();
            this.eventDispatcher.put(new EventDispatcher.PaintEvent(eventDispatcher, x, y, width, height));
        }
    }

    void serviceRepaints() {
        if ("event-thread".equals(Thread.currentThread().getName())) {
            DeviceFactory.getDevice().getDeviceDisplay().repaint(0, 0, this.current.getWidth(), this.current.getHeight());
            return;
        }
        this.eventDispatcher.serviceRepaints();
    }

    void setScrollDown(boolean state) {
        DeviceFactory.getDevice().getDeviceDisplay().setScrollDown(state);
    }

    void setScrollUp(boolean state) {
        DeviceFactory.getDevice().getDeviceDisplay().setScrollUp(state);
    }

    private final class RunnableWrapper
    extends TimerTask {
        private final Runnable runnable;

        RunnableWrapper(Runnable runnable) {
            this.runnable = runnable;
        }

        public void run() {
            Display.this.eventDispatcher.put(this.runnable);
        }
    }

    private class AlertTimeout
    implements Runnable {
        int time;

        AlertTimeout(int time) {
            this.time = time;
        }

        public void run() {
            try {
                Thread.sleep(this.time);
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            Displayable d = Display.this.current;
            if (d != null && d instanceof Alert) {
                Alert alert = (Alert)d;
                if (alert.time != -2) {
                    alert.getCommandListener().commandAction((Command)alert.getCommands().get(0), alert);
                }
            }
        }
    }

    private class DisplayAccessor
    implements DisplayAccess {
        Display display;

        DisplayAccessor(Display d) {
            this.display = d;
        }

        public void commandAction(Command c, Displayable d) {
            if (c.isRegularCommand()) {
                if (d == null) {
                    return;
                }
                CommandListener listener = d.getCommandListener();
                if (listener == null) {
                    return;
                }
                listener.commandAction(c, d);
            } else {
                Item item = c.getFocusedItem();
                ItemCommandListener listener = item.getItemCommandListener();
                if (listener == null) {
                    return;
                }
                listener.commandAction(c.getOriginalCommand(), item);
            }
        }

        public Display getDisplay() {
            return this.display;
        }

        private void processGameCanvasKeyEvent(GameCanvas c, int k, boolean press) {
            GameCanvasKeyAccess access = MIDletBridge.getGameCanvasKeyAccess(c);
            int gameCode = c.getGameAction(k);
            boolean suppress = false;
            if (gameCode != 0) {
                if (press) {
                    access.recordKeyPressed(c, gameCode);
                } else {
                    access.recordKeyReleased(c, gameCode);
                }
                suppress = access.suppressedKeyEvents(c);
            }
            if (!suppress) {
                if (press) {
                    Display.this.eventDispatcher.put(new KeyEvent(0, k));
                } else {
                    Display.this.eventDispatcher.put(new KeyEvent(1, k));
                }
            }
        }

        public void keyPressed(int keyCode) {
            if (Display.this.current != null && Display.this.current instanceof GameCanvas) {
                this.processGameCanvasKeyEvent((GameCanvas)Display.this.current, keyCode, true);
            } else {
                Display.this.eventDispatcher.put(new KeyEvent(0, keyCode));
            }
        }

        public void keyRepeated(int keyCode) {
            Display.this.eventDispatcher.put(new KeyEvent(2, keyCode));
        }

        public void keyReleased(int keyCode) {
            if (Display.this.current != null && Display.this.current instanceof GameCanvas) {
                this.processGameCanvasKeyEvent((GameCanvas)Display.this.current, keyCode, false);
            } else {
                Display.this.eventDispatcher.put(new KeyEvent(1, keyCode));
            }
        }

        public void pointerPressed(final int x, final int y) {
            if (Display.this.current != null) {
                EventDispatcher eventDispatcher = Display.this.eventDispatcher;
                EventDispatcher eventDispatcher2 = Display.this.eventDispatcher;
                eventDispatcher2.getClass();
                eventDispatcher.put(new EventDispatcher.PointerEvent(eventDispatcher2, new Runnable(){

                    public void run() {
                        Display.this.current.pointerPressed(x, y);
                    }
                }, 0, x, y));
            }
        }

        public void pointerReleased(final int x, final int y) {
            if (Display.this.current != null) {
                EventDispatcher eventDispatcher = Display.this.eventDispatcher;
                EventDispatcher eventDispatcher2 = Display.this.eventDispatcher;
                eventDispatcher2.getClass();
                eventDispatcher.put(new EventDispatcher.PointerEvent(eventDispatcher2, new Runnable(){

                    public void run() {
                        Display.this.current.pointerReleased(x, y);
                    }
                }, 1, x, y));
            }
        }

        public void pointerDragged(final int x, final int y) {
            if (Display.this.current != null) {
                EventDispatcher eventDispatcher = Display.this.eventDispatcher;
                EventDispatcher eventDispatcher2 = Display.this.eventDispatcher;
                eventDispatcher2.getClass();
                eventDispatcher.put(new EventDispatcher.PointerEvent(eventDispatcher2, new Runnable(){

                    public void run() {
                        Display.this.current.pointerDragged(x, y);
                    }
                }, 2, x, y));
            }
        }

        public void paint(Graphics g) {
            if (Display.this.current != null) {
                try {
                    Display.this.current.paint(g);
                }
                catch (Throwable th) {
                    th.printStackTrace();
                }
                g.translate(-g.getTranslateX(), -g.getTranslateY());
            }
        }

        public Displayable getCurrent() {
            return this.getDisplay().getCurrent();
        }

        public DisplayableUI getCurrentUI() {
            Displayable current = this.getCurrent();
            if (current == null) {
                return null;
            }
            return current.ui;
        }

        public boolean isFullScreenMode() {
            Displayable current = this.getCurrent();
            if (current instanceof Canvas) {
                return ((Canvas)current).fullScreenMode;
            }
            return false;
        }

        public void serviceRepaints() {
            this.getDisplay().serviceRepaints();
        }

        public void setCurrent(Displayable d) {
            this.getDisplay().setCurrent(d);
        }

        public void sizeChanged() {
            if (Display.this.current != null) {
                if (Display.this.current instanceof GameCanvas) {
                    ((Display)Display.this).current.width = -1;
                    ((Display)Display.this).current.height = -1;
                    GameCanvasKeyAccess access = MIDletBridge.getGameCanvasKeyAccess((GameCanvas)Display.this.current);
                    access.initBuffer();
                }
                Display.this.current.sizeChanged(Display.this);
            }
        }

        public void repaint() {
            Displayable d = this.getCurrent();
            if (d != null) {
                this.getDisplay().repaint(d, 0, 0, d.getWidth(), d.getHeight());
            }
        }

        public void clean() {
            if (Display.this.current != null) {
                Display.this.current.hideNotify();
            }
            Display.this.eventDispatcher.cancel();
            Display.this.timer.cancel();
        }
    }

    private final class KeyEvent
    extends EventDispatcher.Event {
        static final short KEY_PRESSED = 0;
        static final short KEY_RELEASED = 1;
        static final short KEY_REPEATED = 2;
        private short type;
        private int keyCode;

        KeyEvent(short type, int keyCode) {
            super(Display.this.eventDispatcher);
            this.type = type;
            this.keyCode = keyCode;
        }

        public void run() {
            switch (this.type) {
                case 0: {
                    if (Display.this.current == null) break;
                    Display.this.current.keyPressed(this.keyCode);
                    break;
                }
                case 1: {
                    if (Display.this.current == null) break;
                    Display.this.current.keyReleased(this.keyCode);
                    break;
                }
                case 2: {
                    if (Display.this.current == null) break;
                    Display.this.current.keyRepeated(this.keyCode);
                }
            }
        }
    }

    private final class TickerPaintTask
    implements Runnable {
        private TickerPaintTask() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            Ticker ticker;
            if (Display.this.current != null && (ticker = Display.this.current.getTicker()) != null) {
                Ticker ticker2 = ticker;
                synchronized (ticker2) {
                    if (ticker.resetTextPosTo != -1) {
                        ticker.textPos = ticker.resetTextPosTo;
                        ticker.resetTextPosTo = -1;
                    }
                    ticker.textPos -= Ticker.PAINT_MOVE;
                }
                Display.this.repaint(Display.this.current, 0, 0, Display.this.current.getWidth(), Display.this.current.getHeight());
            }
        }
    }

    private final class GaugePaintTask
    implements Runnable {
        private GaugePaintTask() {
        }

        public void run() {
            if (Display.this.current != null) {
                if (Display.this.current instanceof Alert) {
                    Gauge gauge = ((Alert)((Display)Display.this).current).indicator;
                    if (gauge != null && gauge.hasIndefiniteRange() && gauge.getValue() == 2) {
                        gauge.updateIndefiniteFrame();
                    }
                } else if (Display.this.current instanceof Form) {
                    Item[] items = ((Form)((Display)Display.this).current).items;
                    for (int i = 0; i < items.length; ++i) {
                        Gauge gauge;
                        Item it = items[i];
                        if (it == null || !(it instanceof Gauge) || !(gauge = (Gauge)it).hasIndefiniteRange() || gauge.getValue() != 2) continue;
                        gauge.updateIndefiniteFrame();
                    }
                }
            }
        }
    }
}

