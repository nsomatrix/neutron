/*
 *  Neutron
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
 *  
 *  @version $Id$
 */

package org.neutron.app.ui.swing;

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
import javax.microedition.lcdui.game.GameCanvas;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import org.neutron.DisplayAccess;
import org.neutron.DisplayComponent;
import org.neutron.MIDletAccess;
import org.neutron.MIDletBridge;
import org.neutron.app.Config;
import org.neutron.app.Common;
import org.neutron.app.ui.DisplayRepaintListener;
import org.neutron.device.Device;
import org.neutron.device.DeviceDisplay;
import org.neutron.device.DeviceFactory;
import org.neutron.device.impl.ButtonName;
import org.neutron.device.impl.InputMethodImpl;
import org.neutron.device.impl.SoftButton;
import org.neutron.device.impl.ui.CommandManager;
import org.neutron.device.j2se.J2SEButton;
import org.neutron.device.j2se.J2SEDeviceDisplay;
import org.neutron.device.j2se.J2SEGraphicsSurface;
import org.neutron.device.j2se.J2SEInputMethod;

public class SwingDisplayComponent extends JComponent implements DisplayComponent {
	private static final long serialVersionUID = 1L;

	private SwingDeviceComponent deviceComponent;

	private J2SEGraphicsSurface graphicsSurface;

	private BufferedImage scale2xCacheImage;
	private int[] scale2xCacheData;

	private BufferedImage adjustedCacheImage;
	private int[] adjustedCacheData;

	private BufferedImage convolvedCacheImage;

	private BufferedImage ghostingCacheImage;

	private SoftButton initialPressedSoftButton;

	private DisplayRepaintListener displayRepaintListener;

	private boolean showMouseCoordinates = false;

	private Point pressedPoint = new Point();

	private MouseAdapter mouseListener = new MouseAdapter() {

		public void mousePressed(MouseEvent e) {
			deviceComponent.requestFocus();
			pressedPoint = e.getPoint();

			if (e.isControlDown()) {
				Point p = deviceCoordinate(DeviceFactory.getDevice().getDeviceDisplay(), e.getPoint());
				SwingAutoClicker.setCoordinates(p.x, p.y);
				repaint();
				return;
			}

			org.neutron.app.util.SleepManager.notifyActivity();

			if (org.neutron.app.util.SleepManager.isSleepModeActive()) {
				if (SwingSleepUI.isWakeUpClicked(e.getX(), e.getY(), getWidth(), getHeight())) {
					org.neutron.app.util.SleepManager.setSleepModeActive(false);
				}
				return;
			}

			if (MIDletBridge.getCurrentMIDlet() == null) {
				return;
			}

			if (SwingUtilities.isMiddleMouseButton(e)) {
				// fire
				KeyEvent event = new KeyEvent(deviceComponent, 0, System.currentTimeMillis(), 0, KeyEvent.VK_ENTER,
						KeyEvent.CHAR_UNDEFINED);
				deviceComponent.keyPressed(event);
				deviceComponent.keyReleased(event);
				return;
			}

			Device device = DeviceFactory.getDevice();
			J2SEInputMethod inputMethod = (J2SEInputMethod) device.getInputMethod();
			// if the displayable is in full screen mode, we should not
			// invoke any associated commands, but send the raw key codes
			// instead
			boolean fullScreenMode = device.getDeviceDisplay().isFullScreenMode();

			if (device.hasPointerEvents()) {
				if (!fullScreenMode) {
					Iterator it = device.getSoftButtons().iterator();
					while (it.hasNext()) {
						SoftButton button = (SoftButton) it.next();
						if (button.isVisible()) {
							org.neutron.device.impl.Rectangle pb = button.getPaintable();
							Point scaledPt = scaleCoordinate(e.getPoint());
							if (pb != null && pb.contains(scaledPt.x, scaledPt.y)) {
								initialPressedSoftButton = button;
								button.setPressed(true);
								repaintRequest(pb.x, pb.y, pb.width, pb.height);
								break;
							}
						}
					}
				}
				Point p = deviceCoordinate(device.getDeviceDisplay(), e.getPoint());
				inputMethod.pointerPressed(p.x, p.y);
			}
		}

		public void mouseReleased(MouseEvent e) {
			org.neutron.app.util.SleepManager.notifyActivity();
			if (org.neutron.app.util.SleepManager.isSleepModeActive()) {
				return;
			}

			if (MIDletBridge.getCurrentMIDlet() == null) {
				return;
			}

			Device device = DeviceFactory.getDevice();
			J2SEInputMethod inputMethod = (J2SEInputMethod) device.getInputMethod();
			boolean fullScreenMode = device.getDeviceDisplay().isFullScreenMode();
			if (device.hasPointerEvents()) {
				if (!fullScreenMode) {
					if (initialPressedSoftButton != null && initialPressedSoftButton.isPressed()) {
						initialPressedSoftButton.setPressed(false);
						org.neutron.device.impl.Rectangle pb = initialPressedSoftButton.getPaintable();
						if (pb != null) {
							repaintRequest(pb.x, pb.y, pb.width, pb.height);
							Point scaledPt = scaleCoordinate(e.getPoint());
							if (pb.contains(scaledPt.x, scaledPt.y)) {
								MIDletAccess ma = MIDletBridge.getMIDletAccess();
								if (ma == null) {
									return;
								}
								DisplayAccess da = ma.getDisplayAccess();
								if (da == null) {
									return;
								}
								Displayable d = da.getCurrent();
								Command cmd = initialPressedSoftButton.getCommand();
								if (cmd != null) {
									if (cmd.equals(CommandManager.CMD_MENU)) {
										CommandManager.getInstance().commandAction(cmd);
									} else {
										da.commandAction(cmd, d);
									}
								} else {
									if (d != null && d instanceof Screen) {
										if (initialPressedSoftButton.getName().equals("up")) {
											da.keyPressed(getButtonByButtonName(ButtonName.UP).getKeyCode());
										} else if (initialPressedSoftButton.getName().equals("down")) {
											da.keyPressed(getButtonByButtonName(ButtonName.DOWN).getKeyCode());
										}
									}
								}
							}
						}
					}
					initialPressedSoftButton = null;
				}
				Point p = deviceCoordinate(device.getDeviceDisplay(), e.getPoint());
				inputMethod.pointerReleased(p.x, p.y);
			}
		}

	};

	private MouseMotionListener mouseMotionListener = new MouseMotionListener() {

		public void mouseDragged(MouseEvent e) {
			org.neutron.app.util.SleepManager.notifyActivity();
			if (org.neutron.app.util.SleepManager.isSleepModeActive()) {
				return;
			}
			Point scaledPt = scaleCoordinate(e.getPoint());
			Point scaledPressedPt = scaleCoordinate(pressedPoint);
			if (showMouseCoordinates) {
				StringBuffer buf = new StringBuffer();
				int width = scaledPt.x - scaledPressedPt.x;
				int height = scaledPt.y - scaledPressedPt.y;
				Point p = deviceCoordinate(DeviceFactory.getDevice().getDeviceDisplay(), pressedPoint);
				buf.append(p.x).append(",").append(p.y).append(" ").append(width).append("x").append(height);
				Common.setStatusBar(buf.toString());
			}

			Device device = DeviceFactory.getDevice();
			InputMethodImpl inputMethod = (InputMethodImpl) device.getInputMethod();
			boolean fullScreenMode = device.getDeviceDisplay().isFullScreenMode();
			if (device.hasPointerMotionEvents()) {
				if (!fullScreenMode) {
					if (initialPressedSoftButton != null) {
						org.neutron.device.impl.Rectangle pb = initialPressedSoftButton.getPaintable();
						if (pb != null) {
							if (pb.contains(scaledPt.x, scaledPt.y)) {
								if (!initialPressedSoftButton.isPressed()) {
									initialPressedSoftButton.setPressed(true);
									repaintRequest(pb.x, pb.y, pb.width, pb.height);
								}
							} else {
								if (initialPressedSoftButton.isPressed()) {
									initialPressedSoftButton.setPressed(false);
									repaintRequest(pb.x, pb.y, pb.width, pb.height);
								}
							}
						}
					}
				}
				Point p = deviceCoordinate(device.getDeviceDisplay(), e.getPoint());
				inputMethod.pointerDragged(p.x, p.y);
			}
		}

		public void mouseMoved(MouseEvent e) {
			org.neutron.app.util.SleepManager.notifyActivity();
			if (org.neutron.app.util.SleepManager.isSleepModeActive()) {
				return;
			}
			if (showMouseCoordinates) {
				StringBuffer buf = new StringBuffer();
				Point p = deviceCoordinate(DeviceFactory.getDevice().getDeviceDisplay(), e.getPoint());
				buf.append(p.x).append(",").append(p.y);
				Common.setStatusBar(buf.toString());
			}
		}

	};

	private MouseWheelListener mouseWheelListener = new MouseWheelListener() {

		public void mouseWheelMoved(MouseWheelEvent ev) {
			org.neutron.app.util.SleepManager.notifyActivity();
			if (org.neutron.app.util.SleepManager.isSleepModeActive()) {
				return;
			}
			if (ev.getWheelRotation() > 0) {
				// down
				KeyEvent event = new KeyEvent(deviceComponent, 0, System.currentTimeMillis(), 0, KeyEvent.VK_DOWN,
						KeyEvent.CHAR_UNDEFINED);
				deviceComponent.keyPressed(event);
				deviceComponent.keyReleased(event);
			} else {
				// up
				KeyEvent event = new KeyEvent(deviceComponent, 0, System.currentTimeMillis(), 0, KeyEvent.VK_UP,
						KeyEvent.CHAR_UNDEFINED);
				deviceComponent.keyPressed(event);
				deviceComponent.keyReleased(event);
			}
		}

	};

	private javax.swing.Timer sleepRepaintTimer;

	SwingDisplayComponent(SwingDeviceComponent deviceComponent) {
		this.deviceComponent = deviceComponent;

		setFocusable(false);

		addMouseListener(mouseListener);
		addMouseMotionListener(mouseMotionListener);
		addMouseWheelListener(mouseWheelListener);

		org.neutron.app.util.SleepManager.setUiCallback(new Runnable() {
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						boolean active = org.neutron.app.util.SleepManager.isSleepModeActive();
						org.neutron.app.Main.updateSleepModeMenuState(org.neutron.app.util.SleepManager.isSleepEnabled());
						if (active) {
							if (sleepRepaintTimer == null) {
								sleepRepaintTimer = new javax.swing.Timer(33, new java.awt.event.ActionListener() {
									public void actionPerformed(java.awt.event.ActionEvent e) {
										repaint();
									}
								});
								sleepRepaintTimer.start();
							}
						} else {
							if (sleepRepaintTimer != null) {
								sleepRepaintTimer.stop();
								sleepRepaintTimer = null;
							}
						}
						repaint();
						if (SwingDisplayComponent.this.deviceComponent != null) {
							SwingDisplayComponent.this.deviceComponent.repaint();
						}
					}
				});
			}
		});
	}

	public void init() {
		synchronized (this) {
			graphicsSurface = null;
			initialPressedSoftButton = null;
		}
	}

	public void addDisplayRepaintListener(DisplayRepaintListener l) {
		displayRepaintListener = l;
	}

	public void removeDisplayRepaintListener(DisplayRepaintListener l) {
		if (displayRepaintListener == l) {
			displayRepaintListener = null;
		}
	}

	public Dimension getPreferredSize() {
		Device device = DeviceFactory.getDevice();
		if (device == null) {
			return new Dimension(0, 0);
		}

		return new Dimension(device.getDeviceDisplay().getFullWidth(), device.getDeviceDisplay().getFullHeight());
	}

	private BufferedImage getScale2xImage(BufferedImage src, int[] srcData) {
		int width = src.getWidth();
		int height = src.getHeight();
		int destWidth = width * 2;
		int destHeight = height * 2;

		if (scale2xCacheImage == null || scale2xCacheImage.getWidth() != destWidth || scale2xCacheImage.getHeight() != destHeight) {
			scale2xCacheImage = new BufferedImage(destWidth, destHeight, src.getType());
			scale2xCacheData = ((java.awt.image.DataBufferInt) scale2xCacheImage.getRaster().getDataBuffer()).getData();
		}

		for (int y = 0; y < height; y++) {
			int yPrev = y > 0 ? y - 1 : y;
			int yNext = y < height - 1 ? y + 1 : y;

			int rowPrevOffset = yPrev * width;
			int rowCurrOffset = y * width;
			int rowNextOffset = yNext * width;

			int destRow0Offset = (y * 2) * destWidth;
			int destRow1Offset = (y * 2 + 1) * destWidth;

			for (int x = 0; x < width; x++) {
				int xPrev = x > 0 ? x - 1 : x;
				int xNext = x < width - 1 ? x + 1 : x;

				int E = srcData[rowCurrOffset + x];
				int B = srcData[rowPrevOffset + x];
				int D = srcData[rowCurrOffset + xPrev];
				int F = srcData[rowCurrOffset + xNext];
				int H = srcData[rowNextOffset + x];

				int E0, E1, E2, E3;
				if (B != H && D != F) {
					E0 = (D == B) ? D : E;
					E1 = (B == F) ? F : E;
					E2 = (H == D) ? D : E;
					E3 = (F == H) ? F : E;
				} else {
					E0 = E;
					E1 = E;
					E2 = E;
					E3 = E;
				}

				int destX = x * 2;
				scale2xCacheData[destRow0Offset + destX] = E0;
				scale2xCacheData[destRow0Offset + destX + 1] = E1;
				scale2xCacheData[destRow1Offset + destX] = E2;
				scale2xCacheData[destRow1Offset + destX + 1] = E3;
			}
		}

		return scale2xCacheImage;
	}

	private void applyColorAdjustments(int[] srcPixels, int[] destPixels, int length, 
	                                  int brightness, int contrastVal, float gamma, int saturationVal, boolean invert) {
		float contrast = contrastVal / 100.0f;
		float sat = saturationVal / 100.0f;

		int[] gammaLut = null;
		if (Math.abs(gamma - 1.0f) > 0.01f) {
			gammaLut = new int[256];
			for (int i = 0; i < 256; i++) {
				gammaLut[i] = (int) (255.0 * Math.pow(i / 255.0, 1.0 / gamma));
				if (gammaLut[i] > 255) gammaLut[i] = 255;
				if (gammaLut[i] < 0) gammaLut[i] = 0;
			}
		}

		for (int i = 0; i < length; i++) {
			int argb = srcPixels[i];
			int a = argb & 0xff000000;
			int r = (argb >> 16) & 0xff;
			int g = (argb >> 8) & 0xff;
			int b = argb & 0xff;

			if (invert) {
				r = 255 - r;
				g = 255 - g;
				b = 255 - b;
			}

			if (brightness != 0 || contrastVal != 100) {
				r = (int) (r * contrast + brightness);
				g = (int) (g * contrast + brightness);
				b = (int) (b * contrast + brightness);
			}

			if (gammaLut != null) {
				if (r < 0) r = 0; else if (r > 255) r = 255;
				if (g < 0) g = 0; else if (g > 255) g = 255;
				if (b < 0) b = 0; else if (b > 255) b = 255;
				r = gammaLut[r];
				g = gammaLut[g];
				b = gammaLut[b];
			}

			if (saturationVal != 100) {
				int luma = (int) (0.299f * r + 0.587f * g + 0.114f * b);
				r = (int) (luma + sat * (r - luma));
				g = (int) (luma + sat * (g - luma));
				b = (int) (luma + sat * (b - luma));
			}

			if (r < 0) r = 0; else if (r > 255) r = 255;
			if (g < 0) g = 0; else if (g > 255) g = 255;
			if (b < 0) b = 0; else if (b > 255) b = 255;

			destPixels[i] = a | (r << 16) | (g << 8) | b;
		}
	}

	protected void paintComponent(Graphics g) {
		if (org.neutron.app.util.SleepManager.isSleepModeActive()) {
			SwingSleepUI.paintScreensaver(g, getWidth(), getHeight());
		} else {
			J2SEGraphicsSurface localSurface = graphicsSurface;
			if (localSurface != null) {
				synchronized (localSurface) {
					Graphics2D g2d = (Graphics2D) g;
					String filter = Config.getGraphicsFilter();

					BufferedImage currentImg = localSurface.getImage();
					int[] currentData = localSurface.getImageData();

					if ("Scale2x".equals(filter)) {
						currentImg = getScale2xImage(currentImg, currentData);
						currentData = scale2xCacheData;
					}

					int brightness = Config.getBrightness();
					int contrast = Config.getContrast();
					float gamma = Config.getGamma();
					int saturation = Config.getSaturation();
					boolean invert = Config.isInvert();

					boolean hasColorAdjustments = (brightness != 0 || contrast != 100 || Math.abs(gamma - 1.0f) > 0.01f || saturation != 100 || invert);

					if (hasColorAdjustments) {
						int w = currentImg.getWidth();
						int h = currentImg.getHeight();
						if (adjustedCacheImage == null || adjustedCacheImage.getWidth() != w || adjustedCacheImage.getHeight() != h) {
							adjustedCacheImage = new BufferedImage(w, h, currentImg.getType());
							adjustedCacheData = ((java.awt.image.DataBufferInt) adjustedCacheImage.getRaster().getDataBuffer()).getData();
						}
						
						applyColorAdjustments(currentData, adjustedCacheData, w * h, brightness, contrast, gamma, saturation, invert);
						currentImg = adjustedCacheImage;
					}

					int sharpness = Config.getSharpness();
					if (sharpness != 0) {
						int w = currentImg.getWidth();
						int h = currentImg.getHeight();
						if (convolvedCacheImage == null || convolvedCacheImage.getWidth() != w || convolvedCacheImage.getHeight() != h) {
							convolvedCacheImage = new BufferedImage(w, h, currentImg.getType());
						}
						
						float amt = sharpness / 100.0f;
						java.awt.image.Kernel kernel;
						if (sharpness > 0) {
							float[] sharpenKernel = {
								0f, -amt, 0f,
								-amt, 1f + 4f * amt, -amt,
								0f, -amt, 0f
							};
							kernel = new java.awt.image.Kernel(3, 3, sharpenKernel);
						} else {
							float blurAmt = -sharpness / 100.0f;
							float edge = blurAmt / 9.0f;
							float center = 1.0f - 8.0f * edge;
							float[] blurKernel = {
								edge, edge, edge,
								edge, center, edge,
								edge, edge, edge
							};
							kernel = new java.awt.image.Kernel(3, 3, blurKernel);
						}
						
						java.awt.image.ConvolveOp convolve = new java.awt.image.ConvolveOp(kernel, java.awt.image.ConvolveOp.EDGE_NO_OP, null);
						convolve.filter(currentImg, convolvedCacheImage);
						currentImg = convolvedCacheImage;
					}

					int ghosting = Config.getGhosting();
					if (ghosting > 0) {
						int w = currentImg.getWidth();
						int h = currentImg.getHeight();
						if (ghostingCacheImage == null || ghostingCacheImage.getWidth() != w || ghostingCacheImage.getHeight() != h) {
							ghostingCacheImage = new BufferedImage(w, h, currentImg.getType());
							Graphics2D gg = ghostingCacheImage.createGraphics();
							gg.drawImage(currentImg, 0, 0, null);
							gg.dispose();
						} else {
							Graphics2D gg = ghostingCacheImage.createGraphics();
							float alpha = 1.0f - (ghosting / 100.0f);
							gg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
							gg.drawImage(currentImg, 0, 0, null);
							gg.dispose();
						}
						currentImg = ghostingCacheImage;
					}

					if ("Bilinear".equals(filter)) {
						g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
						g2d.drawImage(currentImg, 0, 0, getWidth(), getHeight(), null);
					} else if ("Bicubic".equals(filter)) {
						g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
						g2d.drawImage(currentImg, 0, 0, getWidth(), getHeight(), null);
					} else if ("CRT Scanlines".equals(filter)) {
						g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
						g2d.drawImage(currentImg, 0, 0, getWidth(), getHeight(), null);
						
						g2d.setColor(new java.awt.Color(0, 0, 0, 45));
						for (int y = 0; y < getHeight(); y += 2) {
							g2d.drawLine(0, y, getWidth(), y);
						}
					} else if ("LCD Grid".equals(filter)) {
						g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
						g2d.drawImage(currentImg, 0, 0, getWidth(), getHeight(), null);
						
						g2d.setColor(new java.awt.Color(0, 0, 0, 30));
						for (int y = 0; y < getHeight(); y += 3) {
							g2d.drawLine(0, y, getWidth(), y);
						}
						for (int x = 0; x < getWidth(); x += 3) {
							g2d.drawLine(x, 0, x, getHeight());
						}
					} else { // "Nearest Neighbor", "Scale2x", or default
						g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
						g2d.drawImage(currentImg, 0, 0, getWidth(), getHeight(), null);
					}

					SwingPerfHUD.paint(g2d, getWidth(), getHeight());
					SwingAutoClicker.drawOverlay(g2d, SwingDisplayComponent.this);
				}
			}
		}
	}

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
			J2SEDeviceDisplay deviceDisplay = (J2SEDeviceDisplay) device.getDeviceDisplay();

			J2SEGraphicsSurface localSurface;
			synchronized (this) {
				if (graphicsSurface == null) {
					graphicsSurface = new J2SEGraphicsSurface(
							device.getDeviceDisplay().getFullWidth(), device.getDeviceDisplay().getFullHeight(), false, 0x000000);
				}
				localSurface = graphicsSurface;

				synchronized (localSurface) {
					deviceDisplay.paintDisplayable(localSurface, x, y, width, height);
					if (!deviceDisplay.isFullScreenMode()) {
						deviceDisplay.paintControls(localSurface.getGraphics());
					}
				}
			}

			if (localSurface != null && localSurface.getImage() != null) {
				if (deviceDisplay.isFullScreenMode()) {
					fireDisplayRepaint(
							localSurface, x, y, width, height);
				} else {
					fireDisplayRepaint(
							localSurface, 0, 0, localSurface.getImage().getWidth(), localSurface.getImage().getHeight());
				}
			}
		}
	}

	public void fireDisplayRepaint(J2SEGraphicsSurface graphicsSurface, int x, int y, int width, int height) {
		if (displayRepaintListener != null) {
			displayRepaintListener.repaintInvoked(graphicsSurface);
		}
		
		if (graphicsSurface != null && graphicsSurface.getImage() != null) {
			int imgW = graphicsSurface.getImage().getWidth();
			int imgH = graphicsSurface.getImage().getHeight();
			int compW = getWidth();
			int compH = getHeight();
			if (compW != imgW || compH != imgH) {
				int rx = (int) Math.floor(x * (double) compW / imgW);
				int ry = (int) Math.floor(y * (double) compH / imgH);
				int rw = (int) Math.ceil((x + width) * (double) compW / imgW) - rx;
				int rh = (int) Math.ceil((y + height) * (double) compH / imgH) - ry;
				repaint(rx, ry, rw, rh);
				return;
			}
		}
		repaint(x, y, width, height);
	}

	private Point scaleCoordinate(Point p) {
		J2SEGraphicsSurface localSurface = graphicsSurface;
		if (localSurface == null || localSurface.getImage() == null) {
			return p;
		}
		int imgW = localSurface.getImage().getWidth();
		int imgH = localSurface.getImage().getHeight();
		int compW = getWidth();
		int compH = getHeight();
		if (compW <= 0 || compH <= 0) {
			return p;
		}
		int x = (int) (p.x * (double) imgW / compW);
		int y = (int) (p.y * (double) imgH / compH);
		return new Point(x, y);
	}

	Point deviceCoordinate(DeviceDisplay deviceDisplay, Point p) {
		Point scaledP = scaleCoordinate(p);
		if (deviceDisplay.isFullScreenMode()) {
			return scaledP;
		} else {
			org.neutron.device.impl.Rectangle pb = ((J2SEDeviceDisplay) deviceDisplay).getDisplayPaintable();
			return new Point(scaledP.x - pb.x, scaledP.y - pb.y);
		}
	}

	void switchShowMouseCoordinates() {
		showMouseCoordinates = !showMouseCoordinates;
	}

	public J2SEGraphicsSurface getGraphicsSurface() {
		return graphicsSurface;
}

	public MouseAdapter getMouseListener() {
		return mouseListener;
	}

	public MouseMotionListener getMouseMotionListener() {
		return mouseMotionListener;
	}

	public MouseWheelListener getMouseWheelListener() {
		return mouseWheelListener;
	}
	
	private J2SEButton getButtonByButtonName(ButtonName buttonName) {
		J2SEButton result;
		for (Enumeration e = DeviceFactory.getDevice().getButtons().elements(); e.hasMoreElements();) {
			result = (J2SEButton) e.nextElement();
			if (result.getFunctionalName() == buttonName) {
				return result;
			}
		}

		return null;
	}

	Point componentCoordinate(int devX, int devY) {
		J2SEGraphicsSurface localSurface = graphicsSurface;
		if (localSurface == null || localSurface.getImage() == null) {
			return new Point(devX, devY);
		}
		int imgW = localSurface.getImage().getWidth();
		int imgH = localSurface.getImage().getHeight();
		int compW = getWidth();
		int compH = getHeight();
		if (imgW <= 0 || imgH <= 0 || compW <= 0 || compH <= 0) {
			return new Point(devX, devY);
		}
		int scaledX = devX;
		int scaledY = devY;
		DeviceDisplay deviceDisplay = DeviceFactory.getDevice().getDeviceDisplay();
		if (!deviceDisplay.isFullScreenMode()) {
			org.neutron.device.impl.Rectangle pb = ((J2SEDeviceDisplay) deviceDisplay).getDisplayPaintable();
			scaledX += pb.x;
			scaledY += pb.y;
		}
		int compX = (int) (scaledX * (double) compW / imgW);
		int compY = (int) (scaledY * (double) compH / imgH);
		return new Point(compX, compY);
	}
}
