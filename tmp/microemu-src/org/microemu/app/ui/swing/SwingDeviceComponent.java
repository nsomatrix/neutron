/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.ui.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.lcdui.Command;
import javax.swing.JPanel;
import javax.swing.UIManager;
import org.microemu.DisplayAccess;
import org.microemu.DisplayComponent;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.app.Common;
import org.microemu.app.ui.swing.SwingDisplayComponent;
import org.microemu.app.ui.swing.XYConstraints;
import org.microemu.app.ui.swing.XYLayout;
import org.microemu.device.Device;
import org.microemu.device.DeviceFactory;
import org.microemu.device.impl.DeviceDisplayImpl;
import org.microemu.device.impl.Polygon;
import org.microemu.device.impl.Rectangle;
import org.microemu.device.impl.SoftButton;
import org.microemu.device.impl.ui.CommandManager;
import org.microemu.device.j2se.J2SEButton;
import org.microemu.device.j2se.J2SEDeviceButtonsHelper;
import org.microemu.device.j2se.J2SEDeviceDisplay;
import org.microemu.device.j2se.J2SEImmutableImage;
import org.microemu.device.j2se.J2SEInputMethod;
import org.microemu.device.j2se.J2SEMutableImage;
import org.microemu.log.Logger;

public class SwingDeviceComponent
extends JPanel
implements KeyListener {
    private static final long serialVersionUID = 1L;
    SwingDisplayComponent dc;
    J2SEButton prevOverButton;
    J2SEButton overButton;
    J2SEButton pressedButton;
    private boolean mouseButtonDown = false;
    Image offi;
    Graphics offg;
    private boolean showMouseCoordinates = false;
    private int pressedX;
    private int pressedY;
    private MouseAdapter mouseListener = new MouseAdapter(){

        public void mousePressed(MouseEvent e) {
            SwingDeviceComponent.this.requestFocus();
            SwingDeviceComponent.this.mouseButtonDown = true;
            SwingDeviceComponent.this.pressedX = e.getX();
            SwingDeviceComponent.this.pressedY = e.getY();
            MouseRepeatedTimerTask.stop();
            if (MIDletBridge.getCurrentMIDlet() == null) {
                return;
            }
            Device device = DeviceFactory.getDevice();
            J2SEInputMethod inputMethod = (J2SEInputMethod)device.getInputMethod();
            boolean fullScreenMode = device.getDeviceDisplay().isFullScreenMode();
            SwingDeviceComponent.this.pressedButton = J2SEDeviceButtonsHelper.getSkinButton(e);
            if (SwingDeviceComponent.this.pressedButton != null) {
                if (SwingDeviceComponent.this.pressedButton instanceof SoftButton && !fullScreenMode) {
                    Command cmd = ((SoftButton)((Object)SwingDeviceComponent.this.pressedButton)).getCommand();
                    if (cmd != null) {
                        MIDletAccess ma = MIDletBridge.getMIDletAccess();
                        if (ma == null) {
                            return;
                        }
                        DisplayAccess da = ma.getDisplayAccess();
                        if (da == null) {
                            return;
                        }
                        if (cmd.equals(CommandManager.CMD_MENU)) {
                            CommandManager.getInstance().commandAction(cmd);
                        } else {
                            da.commandAction(cmd, da.getCurrent());
                        }
                    }
                } else {
                    inputMethod.buttonPressed(SwingDeviceComponent.this.pressedButton, '\u0000');
                    MouseRepeatedTimerTask.schedule(SwingDeviceComponent.this, SwingDeviceComponent.this.pressedButton, inputMethod);
                }
                SwingDeviceComponent.this.repaint(SwingDeviceComponent.this.pressedButton.getShape().getBounds());
            }
        }

        public void mouseReleased(MouseEvent e) {
            SwingDeviceComponent.this.mouseButtonDown = false;
            MouseRepeatedTimerTask.stop();
            if (SwingDeviceComponent.this.pressedButton == null) {
                return;
            }
            if (MIDletBridge.getCurrentMIDlet() == null) {
                return;
            }
            Device device = DeviceFactory.getDevice();
            J2SEInputMethod inputMethod = (J2SEInputMethod)device.getInputMethod();
            J2SEButton prevOverButton = J2SEDeviceButtonsHelper.getSkinButton(e);
            if (prevOverButton != null) {
                inputMethod.buttonReleased(prevOverButton, '\u0000');
            }
            SwingDeviceComponent.this.pressedButton = null;
            if (prevOverButton != null) {
                SwingDeviceComponent.this.repaint(prevOverButton.getShape().getBounds());
            } else {
                SwingDeviceComponent.this.repaint();
            }
        }
    };
    private MouseMotionListener mouseMotionListener = new MouseMotionListener(){

        public void mouseDragged(MouseEvent e) {
            this.mouseMoved(e);
        }

        public void mouseMoved(MouseEvent e) {
            if (SwingDeviceComponent.this.showMouseCoordinates) {
                StringBuffer buf = new StringBuffer();
                if (SwingDeviceComponent.this.mouseButtonDown) {
                    int width = e.getX() - SwingDeviceComponent.this.pressedX;
                    int height = e.getY() - SwingDeviceComponent.this.pressedY;
                    buf.append(SwingDeviceComponent.this.pressedX).append(",").append(SwingDeviceComponent.this.pressedY).append(" ").append(width).append("x").append(height);
                } else {
                    buf.append(e.getX()).append(",").append(e.getY());
                }
                Common.setStatusBar(buf.toString());
            }
            if (SwingDeviceComponent.this.mouseButtonDown && SwingDeviceComponent.this.pressedButton == null) {
                return;
            }
            SwingDeviceComponent.this.prevOverButton = SwingDeviceComponent.this.overButton;
            SwingDeviceComponent.this.overButton = J2SEDeviceButtonsHelper.getSkinButton(e);
            if (SwingDeviceComponent.this.overButton != SwingDeviceComponent.this.prevOverButton) {
                if (SwingDeviceComponent.this.prevOverButton != null) {
                    MouseRepeatedTimerTask.mouseReleased();
                    SwingDeviceComponent.this.pressedButton = null;
                    SwingDeviceComponent.this.repaint(SwingDeviceComponent.this.prevOverButton.getShape().getBounds());
                }
                if (SwingDeviceComponent.this.overButton != null) {
                    SwingDeviceComponent.this.repaint(SwingDeviceComponent.this.overButton.getShape().getBounds());
                }
            } else if (SwingDeviceComponent.this.overButton == null) {
                MouseRepeatedTimerTask.mouseReleased();
                SwingDeviceComponent.this.pressedButton = null;
                if (SwingDeviceComponent.this.prevOverButton != null) {
                    SwingDeviceComponent.this.repaint(SwingDeviceComponent.this.prevOverButton.getShape().getBounds());
                }
            }
        }
    };

    public SwingDeviceComponent() {
        this.dc = new SwingDisplayComponent(this);
        this.setLayout(new XYLayout());
        this.addMouseListener(this.mouseListener);
        this.addMouseMotionListener(this.mouseMotionListener);
    }

    public DisplayComponent getDisplayComponent() {
        return this.dc;
    }

    public void init() {
        this.dc.init();
        this.remove(this.dc);
        Rectangle r = ((J2SEDeviceDisplay)DeviceFactory.getDevice().getDeviceDisplay()).getDisplayRectangle();
        this.add((Component)this.dc, new XYConstraints(r.x, r.y, -1, -1));
        this.revalidate();
    }

    private void repaint(Rectangle r) {
        this.repaint(r.x, r.y, r.width, r.height);
    }

    public void switchShowMouseCoordinates() {
        this.dc.switchShowMouseCoordinates();
    }

    public void keyTyped(KeyEvent ev) {
        if (MIDletBridge.getCurrentMIDlet() == null) {
            return;
        }
        J2SEInputMethod inputMethod = (J2SEInputMethod)DeviceFactory.getDevice().getInputMethod();
        J2SEButton button = inputMethod.getButton(ev);
        if (button != null) {
            inputMethod.buttonTyped(button);
        }
    }

    public void keyPressed(KeyEvent ev) {
        J2SEButton button;
        if (MIDletBridge.getCurrentMIDlet() == null) {
            return;
        }
        Device device = DeviceFactory.getDevice();
        J2SEInputMethod inputMethod = (J2SEInputMethod)device.getInputMethod();
        if (ev.getKeyCode() == 86 && (ev.getModifiers() & 2) != 0) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable transferable = clipboard.getContents(null);
            if (transferable != null) {
                try {
                    Object data = transferable.getTransferData(DataFlavor.stringFlavor);
                    if (data instanceof String) {
                        inputMethod.clipboardPaste((String)data);
                    }
                }
                catch (UnsupportedFlavorException ex) {
                    Logger.error(ex);
                }
                catch (IOException ex) {
                    Logger.error(ex);
                }
            }
            return;
        }
        switch (ev.getKeyCode()) {
            case 16: 
            case 17: 
            case 18: {
                return;
            }
            case 0: {
                if (ev.getKeyChar() != '\u0000') break;
                return;
            }
        }
        char keyChar = '\u0000';
        if (ev.getKeyChar() >= ' ' && ev.getKeyChar() != '\uffff') {
            keyChar = ev.getKeyChar();
        }
        if ((button = inputMethod.getButton(ev)) != null) {
            org.microemu.device.impl.Shape shape;
            this.pressedButton = button;
            if (ev.getKeyCode() >= 96 && ev.getKeyCode() <= 105) {
                keyChar = '\u0000';
            }
            if (ev.getKeyCode() >= 112 && ev.getKeyCode() <= 123) {
                keyChar = '\u0000';
            }
            if ((shape = button.getShape()) != null) {
                this.repaint(shape.getBounds());
            }
        }
        inputMethod.buttonPressed(button, keyChar);
    }

    public void keyReleased(KeyEvent ev) {
        org.microemu.device.impl.Shape shape;
        if (MIDletBridge.getCurrentMIDlet() == null) {
            return;
        }
        switch (ev.getKeyCode()) {
            case 16: 
            case 17: 
            case 18: {
                return;
            }
            case 0: {
                if (ev.getKeyChar() != '\u0000') break;
                return;
            }
        }
        Device device = DeviceFactory.getDevice();
        J2SEInputMethod inputMethod = (J2SEInputMethod)device.getInputMethod();
        char keyChar = '\u0000';
        if (ev.getKeyChar() >= ' ' && ev.getKeyChar() != '\uffff') {
            keyChar = ev.getKeyChar();
        }
        if (ev.getKeyCode() >= 96 && ev.getKeyCode() <= 105) {
            keyChar = '\u0000';
        }
        if (ev.getKeyCode() >= 112 && ev.getKeyCode() <= 123) {
            keyChar = '\u0000';
        }
        inputMethod.buttonReleased(inputMethod.getButton(ev), keyChar);
        this.prevOverButton = this.pressedButton;
        this.pressedButton = null;
        if (this.prevOverButton != null && (shape = this.prevOverButton.getShape()) != null) {
            this.repaint(shape.getBounds());
        }
    }

    public MouseListener getDefaultMouseListener() {
        return this.mouseListener;
    }

    public MouseMotionListener getDefaultMouseMotionListener() {
        return this.mouseMotionListener;
    }

    protected void paintComponent(Graphics g) {
        org.microemu.device.impl.Shape shape;
        if (this.offg == null || this.offi.getWidth(null) != this.getSize().width || this.offi.getHeight(null) != this.getSize().height) {
            this.offi = new J2SEMutableImage(this.getSize().width, this.getSize().height).getImage();
            this.offg = this.offi.getGraphics();
        }
        Dimension size = this.getSize();
        this.offg.setColor(UIManager.getColor("text"));
        try {
            this.offg.fillRect(0, 0, size.width, size.height);
        }
        catch (NullPointerException ex) {
            // empty catch block
        }
        Device device = DeviceFactory.getDevice();
        if (device == null) {
            g.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
            return;
        }
        if (((DeviceDisplayImpl)device.getDeviceDisplay()).isResizable()) {
            return;
        }
        this.offg.drawImage(((J2SEImmutableImage)device.getNormalImage()).getImage(), 0, 0, this);
        if (this.prevOverButton != null) {
            shape = this.prevOverButton.getShape();
            if (shape != null) {
                this.drawImageInShape(this.offg, ((J2SEImmutableImage)device.getNormalImage()).getImage(), shape);
            }
            this.prevOverButton = null;
        }
        if (this.overButton != null && (shape = this.overButton.getShape()) != null) {
            this.drawImageInShape(this.offg, ((J2SEImmutableImage)device.getOverImage()).getImage(), shape);
        }
        if (this.pressedButton != null && (shape = this.pressedButton.getShape()) != null) {
            this.drawImageInShape(this.offg, ((J2SEImmutableImage)device.getPressedImage()).getImage(), shape);
        }
        g.drawImage(this.offi, 0, 0, null);
    }

    private void drawImageInShape(Graphics g, Image image, org.microemu.device.impl.Shape shape) {
        Shape clipSave = g.getClip();
        if (shape instanceof Polygon) {
            java.awt.Polygon poly = new java.awt.Polygon(((Polygon)shape).xpoints, ((Polygon)shape).ypoints, ((Polygon)shape).npoints);
            g.setClip(poly);
        }
        Rectangle r = shape.getBounds();
        g.drawImage(image, r.x, r.y, r.x + r.width, r.y + r.height, r.x, r.y, r.x + r.width, r.y + r.height, null);
        g.setClip(clipSave);
    }

    public Dimension getPreferredSize() {
        Device device = DeviceFactory.getDevice();
        if (device == null) {
            return new Dimension(0, 0);
        }
        DeviceDisplayImpl deviceDisplay = (DeviceDisplayImpl)DeviceFactory.getDevice().getDeviceDisplay();
        if (deviceDisplay.isResizable()) {
            return new Dimension(deviceDisplay.getFullWidth(), deviceDisplay.getFullHeight());
        }
        javax.microedition.lcdui.Image img = device.getNormalImage();
        return new Dimension(img.getWidth(), img.getHeight());
    }

    private static class MouseRepeatedTimerTask
    extends TimerTask {
        private static final int DELAY = 100;
        Timer timer;
        Component source;
        J2SEButton button;
        J2SEInputMethod inputMethod;
        static MouseRepeatedTimerTask task;

        private MouseRepeatedTimerTask() {
        }

        static void schedule(Component source, J2SEButton button, J2SEInputMethod inputMethod) {
            if (task != null) {
                task.cancel();
            }
            task = new MouseRepeatedTimerTask();
            MouseRepeatedTimerTask.task.source = source;
            MouseRepeatedTimerTask.task.button = button;
            MouseRepeatedTimerTask.task.inputMethod = inputMethod;
            MouseRepeatedTimerTask.task.timer = new Timer();
            MouseRepeatedTimerTask.task.timer.scheduleAtFixedRate((TimerTask)task, 500L, 100L);
        }

        static void stop() {
            if (task != null) {
                MouseRepeatedTimerTask.task.inputMethod = null;
                if (MouseRepeatedTimerTask.task.timer != null) {
                    MouseRepeatedTimerTask.task.timer.cancel();
                }
                task.cancel();
                task = null;
            }
        }

        public static void mouseReleased() {
            if (task != null && MouseRepeatedTimerTask.task.inputMethod != null) {
                MouseRepeatedTimerTask.task.inputMethod.buttonReleased(MouseRepeatedTimerTask.task.button, '\u0000');
                MouseRepeatedTimerTask.stop();
            }
        }

        public void run() {
            if (this.inputMethod != null) {
                this.inputMethod.buttonPressed(this.button, '\u0000');
            }
        }
    }
}

