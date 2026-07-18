/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.ui.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Enumeration;
import java.util.Iterator;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Screen;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.microemu.DisplayAccess;
import org.microemu.DisplayComponent;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.app.Common;
import org.microemu.app.ui.DisplayRepaintListener;
import org.microemu.app.ui.swing.SwingDeviceComponent;
import org.microemu.device.Device;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.DeviceFactory;
import org.microemu.device.MutableImage;
import org.microemu.device.impl.ButtonName;
import org.microemu.device.impl.InputMethodImpl;
import org.microemu.device.impl.Rectangle;
import org.microemu.device.impl.SoftButton;
import org.microemu.device.impl.ui.CommandManager;
import org.microemu.device.j2se.J2SEButton;
import org.microemu.device.j2se.J2SEDeviceDisplay;
import org.microemu.device.j2se.J2SEInputMethod;
import org.microemu.device.j2se.J2SEMutableImage;

public class SwingDisplayComponent
extends JComponent
implements DisplayComponent {
    private static final long serialVersionUID = 1L;
    private SwingDeviceComponent deviceComponent;
    private J2SEMutableImage displayImage = null;
    private Graphics displayGraphics;
    private SoftButton initialPressedSoftButton;
    private DisplayRepaintListener displayRepaintListener;
    private boolean showMouseCoordinates = false;
    private Point pressedPoint = new Point();
    private MouseAdapter mouseListener = new MouseAdapter(){

        public void mousePressed(MouseEvent e) {
            SwingDisplayComponent.this.deviceComponent.requestFocus();
            SwingDisplayComponent.this.pressedPoint = e.getPoint();
            if (MIDletBridge.getCurrentMIDlet() == null) {
                return;
            }
            if (SwingUtilities.isMiddleMouseButton(e)) {
                KeyEvent event = new KeyEvent(SwingDisplayComponent.this.deviceComponent, 0, System.currentTimeMillis(), 0, 10, '\uffff');
                SwingDisplayComponent.this.deviceComponent.keyPressed(event);
                SwingDisplayComponent.this.deviceComponent.keyReleased(event);
                return;
            }
            Device device = DeviceFactory.getDevice();
            J2SEInputMethod inputMethod = (J2SEInputMethod)device.getInputMethod();
            boolean fullScreenMode = device.getDeviceDisplay().isFullScreenMode();
            if (device.hasPointerEvents()) {
                if (!fullScreenMode) {
                    Iterator it = device.getSoftButtons().iterator();
                    while (it.hasNext()) {
                        Rectangle pb;
                        SoftButton button = (SoftButton)it.next();
                        if (!button.isVisible() || (pb = button.getPaintable()) == null || !pb.contains(e.getX(), e.getY())) continue;
                        SwingDisplayComponent.this.initialPressedSoftButton = button;
                        button.setPressed(true);
                        SwingDisplayComponent.this.repaintRequest(pb.x, pb.y, pb.width, pb.height);
                        break;
                    }
                }
                Point p = SwingDisplayComponent.this.deviceCoordinate(device.getDeviceDisplay(), e.getPoint());
                inputMethod.pointerPressed(p.x, p.y);
            }
        }

        public void mouseReleased(MouseEvent e) {
            if (MIDletBridge.getCurrentMIDlet() == null) {
                return;
            }
            Device device = DeviceFactory.getDevice();
            J2SEInputMethod inputMethod = (J2SEInputMethod)device.getInputMethod();
            boolean fullScreenMode = device.getDeviceDisplay().isFullScreenMode();
            if (device.hasPointerEvents()) {
                if (!fullScreenMode) {
                    if (SwingDisplayComponent.this.initialPressedSoftButton != null && SwingDisplayComponent.this.initialPressedSoftButton.isPressed()) {
                        SwingDisplayComponent.this.initialPressedSoftButton.setPressed(false);
                        Rectangle pb = SwingDisplayComponent.this.initialPressedSoftButton.getPaintable();
                        if (pb != null) {
                            SwingDisplayComponent.this.repaintRequest(pb.x, pb.y, pb.width, pb.height);
                            if (pb.contains(e.getX(), e.getY())) {
                                MIDletAccess ma = MIDletBridge.getMIDletAccess();
                                if (ma == null) {
                                    return;
                                }
                                DisplayAccess da = ma.getDisplayAccess();
                                if (da == null) {
                                    return;
                                }
                                Displayable d = da.getCurrent();
                                Command cmd = SwingDisplayComponent.this.initialPressedSoftButton.getCommand();
                                if (cmd != null) {
                                    if (cmd.equals(CommandManager.CMD_MENU)) {
                                        CommandManager.getInstance().commandAction(cmd);
                                    } else {
                                        da.commandAction(cmd, d);
                                    }
                                } else if (d != null && d instanceof Screen) {
                                    if (SwingDisplayComponent.this.initialPressedSoftButton.getName().equals("up")) {
                                        da.keyPressed(SwingDisplayComponent.this.getButtonByButtonName(ButtonName.UP).getKeyCode());
                                    } else if (SwingDisplayComponent.this.initialPressedSoftButton.getName().equals("down")) {
                                        da.keyPressed(SwingDisplayComponent.this.getButtonByButtonName(ButtonName.DOWN).getKeyCode());
                                    }
                                }
                            }
                        }
                    }
                    SwingDisplayComponent.this.initialPressedSoftButton = null;
                }
                Point p = SwingDisplayComponent.this.deviceCoordinate(device.getDeviceDisplay(), e.getPoint());
                inputMethod.pointerReleased(p.x, p.y);
            }
        }
    };
    private MouseMotionListener mouseMotionListener = new MouseMotionListener(){

        public void mouseDragged(MouseEvent e) {
            Point p;
            if (SwingDisplayComponent.this.showMouseCoordinates) {
                StringBuffer buf = new StringBuffer();
                int width = e.getX() - ((SwingDisplayComponent)SwingDisplayComponent.this).pressedPoint.x;
                int height = e.getY() - ((SwingDisplayComponent)SwingDisplayComponent.this).pressedPoint.y;
                p = SwingDisplayComponent.this.deviceCoordinate(DeviceFactory.getDevice().getDeviceDisplay(), SwingDisplayComponent.this.pressedPoint);
                buf.append(p.x).append(",").append(p.y).append(" ").append(width).append("x").append(height);
                Common.setStatusBar(buf.toString());
            }
            Device device = DeviceFactory.getDevice();
            InputMethodImpl inputMethod = (InputMethodImpl)device.getInputMethod();
            boolean fullScreenMode = device.getDeviceDisplay().isFullScreenMode();
            if (device.hasPointerMotionEvents()) {
                Rectangle pb;
                if (!fullScreenMode && SwingDisplayComponent.this.initialPressedSoftButton != null && (pb = SwingDisplayComponent.this.initialPressedSoftButton.getPaintable()) != null) {
                    if (pb.contains(e.getX(), e.getY())) {
                        if (!SwingDisplayComponent.this.initialPressedSoftButton.isPressed()) {
                            SwingDisplayComponent.this.initialPressedSoftButton.setPressed(true);
                            SwingDisplayComponent.this.repaintRequest(pb.x, pb.y, pb.width, pb.height);
                        }
                    } else if (SwingDisplayComponent.this.initialPressedSoftButton.isPressed()) {
                        SwingDisplayComponent.this.initialPressedSoftButton.setPressed(false);
                        SwingDisplayComponent.this.repaintRequest(pb.x, pb.y, pb.width, pb.height);
                    }
                }
                p = SwingDisplayComponent.this.deviceCoordinate(device.getDeviceDisplay(), e.getPoint());
                inputMethod.pointerDragged(p.x, p.y);
            }
        }

        public void mouseMoved(MouseEvent e) {
            if (SwingDisplayComponent.this.showMouseCoordinates) {
                StringBuffer buf = new StringBuffer();
                Point p = SwingDisplayComponent.this.deviceCoordinate(DeviceFactory.getDevice().getDeviceDisplay(), e.getPoint());
                buf.append(p.x).append(",").append(p.y);
                Common.setStatusBar(buf.toString());
            }
        }
    };
    private MouseWheelListener mouseWheelListener = new MouseWheelListener(){

        public void mouseWheelMoved(MouseWheelEvent ev) {
            if (ev.getWheelRotation() > 0) {
                KeyEvent event = new KeyEvent(SwingDisplayComponent.this.deviceComponent, 0, System.currentTimeMillis(), 0, 40, '\uffff');
                SwingDisplayComponent.this.deviceComponent.keyPressed(event);
                SwingDisplayComponent.this.deviceComponent.keyReleased(event);
            } else {
                KeyEvent event = new KeyEvent(SwingDisplayComponent.this.deviceComponent, 0, System.currentTimeMillis(), 0, 38, '\uffff');
                SwingDisplayComponent.this.deviceComponent.keyPressed(event);
                SwingDisplayComponent.this.deviceComponent.keyReleased(event);
            }
        }
    };

    SwingDisplayComponent(SwingDeviceComponent deviceComponent) {
        this.deviceComponent = deviceComponent;
        this.setFocusable(false);
        this.addMouseListener(this.mouseListener);
        this.addMouseMotionListener(this.mouseMotionListener);
        this.addMouseWheelListener(this.mouseWheelListener);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void init() {
        SwingDisplayComponent swingDisplayComponent = this;
        synchronized (swingDisplayComponent) {
            this.displayImage = null;
            this.initialPressedSoftButton = null;
        }
    }

    public void addDisplayRepaintListener(DisplayRepaintListener l) {
        this.displayRepaintListener = l;
    }

    public void removeDisplayRepaintListener(DisplayRepaintListener l) {
        if (this.displayRepaintListener == l) {
            this.displayRepaintListener = null;
        }
    }

    public MutableImage getDisplayImage() {
        return this.displayImage;
    }

    public Dimension getPreferredSize() {
        Device device = DeviceFactory.getDevice();
        if (device == null) {
            return new Dimension(0, 0);
        }
        return new Dimension(device.getDeviceDisplay().getFullWidth(), device.getDeviceDisplay().getFullHeight());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void paintComponent(Graphics g) {
        if (this.displayImage != null) {
            J2SEMutableImage j2SEMutableImage = this.displayImage;
            synchronized (j2SEMutableImage) {
                g.drawImage(this.displayImage.getImage(), 0, 0, null);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void repaintRequest(int x, int y, int width, int height) {
        MIDletAccess ma = MIDletBridge.getMIDletAccess();
        if (ma == null) {
            return;
        }
        DisplayAccess da = ma.getDisplayAccess();
        if (da == null) {
            return;
        }
        Displayable current = da.getCurrent();
        if (current == null) {
            return;
        }
        Device device = DeviceFactory.getDevice();
        if (device != null) {
            J2SEDeviceDisplay deviceDisplay = (J2SEDeviceDisplay)device.getDeviceDisplay();
            SwingDisplayComponent swingDisplayComponent = this;
            synchronized (swingDisplayComponent) {
                if (this.displayImage == null) {
                    this.displayImage = new J2SEMutableImage(device.getDeviceDisplay().getFullWidth(), device.getDeviceDisplay().getFullHeight());
                    this.displayGraphics = this.displayImage.getImage().getGraphics();
                }
                J2SEMutableImage j2SEMutableImage = this.displayImage;
                synchronized (j2SEMutableImage) {
                    deviceDisplay.paintDisplayable(this.displayGraphics, x, y, width, height);
                    if (!deviceDisplay.isFullScreenMode()) {
                        deviceDisplay.paintControls(this.displayGraphics);
                    }
                }
                this.fireDisplayRepaint(this.displayImage);
            }
            if (deviceDisplay.isFullScreenMode()) {
                this.repaint(x, y, width, height);
            } else {
                this.repaint(0, 0, this.displayImage.getWidth(), this.displayImage.getHeight());
            }
        }
    }

    private void fireDisplayRepaint(MutableImage image) {
        if (this.displayRepaintListener != null) {
            this.displayRepaintListener.repaintInvoked(image);
        }
    }

    Point deviceCoordinate(DeviceDisplay deviceDisplay, Point p) {
        if (deviceDisplay.isFullScreenMode()) {
            return p;
        }
        Rectangle pb = ((J2SEDeviceDisplay)deviceDisplay).getDisplayPaintable();
        return new Point(p.x - pb.x, p.y - pb.y);
    }

    void switchShowMouseCoordinates() {
        this.showMouseCoordinates = !this.showMouseCoordinates;
    }

    public MutableImage getScaledDisplayImage(int zoom) {
        return this.displayImage.scale(zoom);
    }

    public MouseAdapter getMouseListener() {
        return this.mouseListener;
    }

    public MouseMotionListener getMouseMotionListener() {
        return this.mouseMotionListener;
    }

    public MouseWheelListener getMouseWheelListener() {
        return this.mouseWheelListener;
    }

    private J2SEButton getButtonByButtonName(ButtonName buttonName) {
        Enumeration e = DeviceFactory.getDevice().getButtons().elements();
        while (e.hasMoreElements()) {
            J2SEButton result = (J2SEButton)e.nextElement();
            if (result.getFunctionalName() != buttonName) continue;
            return result;
        }
        return null;
    }
}

