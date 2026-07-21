/**
 *  Neutron
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
 */

package org.neutron.app;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.awt.Image;
import nanoxml.XMLElement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;                                                            
import java.util.List;                                                                
import java.util.NoSuchElementException;                                              
import java.util.Timer;                                                               
import java.util.TimerTask; 

import javax.microedition.midlet.MIDletStateChangeException;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.neutron.DisplayAccess;
import org.neutron.DisplayComponent;
import org.neutron.MIDletAccess;
import org.neutron.MIDletBridge;
import org.neutron.app.capture.AnimatedGifEncoder;
import org.neutron.app.classloader.MIDletClassLoader;
import org.neutron.app.ui.DisplayRepaintListener;
import org.neutron.app.ui.Message;
import org.neutron.app.ui.ResponseInterfaceListener;
import org.neutron.app.ui.StatusBarListener;
import org.neutron.app.ui.GameStateListener;
import org.neutron.app.ui.swing.DropTransferHandler;
import org.neutron.app.ui.swing.ExtensionFileFilter;
import org.neutron.app.ui.swing.JMRUMenu;
import org.neutron.app.ui.swing.MIDletUrlPanel;
import org.neutron.app.ui.swing.RecordStoreManagerDialog;
import org.neutron.app.ui.swing.ResizeDeviceDisplayDialog;
import org.neutron.app.ui.swing.SwingAboutDialog;
import org.neutron.app.ui.swing.SwingDeviceComponent;
import org.neutron.app.ui.swing.SwingDialogWindow;
import org.neutron.app.ui.swing.SwingDisplayComponent;
import org.neutron.app.ui.swing.SwingErrorMessageDialogPanel;
import org.neutron.app.ui.swing.SwingLogConsoleDialog;
import org.neutron.app.ui.swing.SwingVideoSettingsPanel;
import org.neutron.app.ui.swing.SwingProxySettingsPanel;
import org.neutron.app.ui.swing.SwingNetworkCapturePanel;
import org.neutron.app.ui.swing.SwingStatusBar;
import org.neutron.app.ui.swing.SwingPerfHUD;
import org.neutron.app.ui.swing.SwingNetworkOverlay;
import org.neutron.app.ui.swing.SwingAutoClicker;
import org.neutron.app.ui.swing.SwingAutoClickerSettingsPanel;
import org.neutron.app.ui.swing.SwingLibraryExplorerDialog;
import org.neutron.app.ui.swing.SwingSystemInfoDialog;
import org.neutron.app.util.DeviceEntry;
import org.neutron.app.util.IOUtils;
import org.neutron.app.util.MidletURLReference;
import org.neutron.device.Device;
import org.neutron.device.DeviceDisplay;
import org.neutron.device.DeviceFactory;
import org.neutron.device.EmulatorContext;
import org.neutron.device.FontManager;
import org.neutron.device.InputMethod;
import org.neutron.device.impl.DeviceDisplayImpl;                                    
import org.neutron.device.impl.DeviceImpl;                                           
import org.neutron.device.impl.Rectangle; 
import org.neutron.device.impl.SoftButton;
import org.neutron.device.j2se.J2SEDevice;
import org.neutron.device.j2se.J2SEDeviceDisplay;
import org.neutron.device.j2se.J2SEFontManager;
import org.neutron.device.j2se.J2SEGraphicsSurface;
import org.neutron.device.j2se.J2SEInputMethod;
import org.neutron.log.Logger;
import org.neutron.log.QueueAppender;
import org.neutron.util.JadMidletEntry;

public class Main extends JFrame {

	private static final long serialVersionUID = 1L;

	protected Common common;

	private MIDletUrlPanel midletUrlPanel = null;

	private JFileChooser saveForWebChooser;

	private JFileChooser fileChooser = null;

	private JFileChooser captureFileChooser = null;

	private JFileChooser screenshotFileChooser = null;

	private JMenuItem menuOpenMIDletFile;

	private JMenuItem menuOpenMIDletURL;



	private JMenuItem menuStartCapture;

	private JMenuItem menuStopCapture;

	private JMenuItem menuScreenshot;

	private JCheckBoxMenuItem menuMIDletNetworkConnection;

	private JCheckBoxMenuItem menuLogConsole;

	private JCheckBoxMenuItem menuRecordStoreManager;

	private JCheckBoxMenuItem menuSleepMode;



	private static Main instance;

	private ActionListener menuSleepModeListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			boolean enabled = menuSleepMode.isSelected();
			Config.setSleepModeEnabled(enabled);
			org.neutron.app.util.SleepManager.setSleepEnabled(enabled);
			Common.setStatusBar("Sleep Mode: " + (enabled ? "Enabled" : "Disabled"));
		}
	};

	public static void updateSleepModeMenuState(final boolean active) {
		if (instance != null && instance.menuSleepMode != null) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					instance.menuSleepMode.setSelected(active);
				}
			});
		}
	}

	private JFrame scaledDisplayFrame;

	private JMenu menuTheme;

	private JMenu menuFilter;

	private JCheckBoxMenuItem[] zoomLevels;

	private SwingDeviceComponent devicePanel;

	private SwingLogConsoleDialog logConsoleDialog;

	private JDialog docsDialog;

	private RecordStoreManagerDialog recordStoreManagerDialog;

	private QueueAppender logQueueAppender;

	private DeviceEntry deviceEntry;

	private AnimatedGifEncoder encoder;

	private File currentCaptureFile;

	private SwingStatusBar statusBar = new SwingStatusBar();

	private JCheckBoxMenuItem menuShowMouseCoordinates;

	private JCheckBoxMenuItem menuFullscreen;

	private JMenuBar menuBar;

	private JButton floatingMenuButton;

	private volatile long lastActivityTime = System.currentTimeMillis();

	private Timer menuTimer;

	private Timer statusBarHideTimer;

	private java.awt.event.AWTEventListener awtEventListener;



	private JButton resizeButton = new JButton("Resize");

	private ResizeDeviceDisplayDialog resizeDeviceDisplayDialog = null;

	protected EmulatorContext emulatorContext = new EmulatorContext() {

		private InputMethod inputMethod = new J2SEInputMethod();

		private DeviceDisplay deviceDisplay = new J2SEDeviceDisplay(this);

		private FontManager fontManager = new J2SEFontManager();

		public DisplayComponent getDisplayComponent() {
			return devicePanel.getDisplayComponent();
		}

		public InputMethod getDeviceInputMethod() {
			return inputMethod;
		}

		public DeviceDisplay getDeviceDisplay() {
			return deviceDisplay;
		}

		public FontManager getDeviceFontManager() {
			return fontManager;
		}

		public InputStream getResourceAsStream(Class origClass, String name) {
            return MIDletBridge.getCurrentMIDlet().getClass().getResourceAsStream(name);
		}
		
		public boolean platformRequest(final String URL) {
			new Thread(new Runnable() {
				public void run() {
					boolean opened = false;
					try {
						if (java.awt.Desktop.isDesktopSupported()) {
							java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
							if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
								desktop.browse(new java.net.URI(URL));
								opened = true;
							}
						}
					} catch (Exception e) {
						org.neutron.log.Logger.error("Failed to open URL in browser: " + URL, e);
					}

					if (!opened) {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								Message.info("MIDlet requests that the device handle the following URL: " + URL);
							}
						});
					} else {
						org.neutron.log.Logger.info("MIDlet requested and opened URL: " + URL);
					}
				}
			}).start();

			return false;
		}
	};

	private ActionListener menuOpenMIDletFileListener = new ActionListener() {
		public void actionPerformed(ActionEvent ev) {
			if (fileChooser == null) {
				ExtensionFileFilter fileFilter = new ExtensionFileFilter("MIDlet files");
				fileFilter.addExtension("jad");
				fileFilter.addExtension("jar");
				fileChooser = new JFileChooser();
				fileChooser.setFileFilter(fileFilter);
				fileChooser.setDialogTitle("Open MIDlet File...");
				fileChooser.setCurrentDirectory(new File(Config.getRecentDirectory("recentJadDirectory")));
			} else {
				fileChooser.updateUI();
			}

			int returnVal = fileChooser.showOpenDialog(Main.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				Config.setRecentDirectory("recentJadDirectory", fileChooser.getCurrentDirectory().getAbsolutePath());
				String url = IOUtils.getCanonicalFileURL(fileChooser.getSelectedFile());
				Common.openMIDletUrlSafe(url, new Runnable() {
					public void run() {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								if (recordStoreManagerDialog != null) {
									recordStoreManagerDialog.refresh();
								}
							}
						});
					}
				});
			}
		}
	};

	private ActionListener menuOpenMIDletURLListener = new ActionListener() {
		public void actionPerformed(ActionEvent ev) {
			if (midletUrlPanel == null) {
				midletUrlPanel = new MIDletUrlPanel();
			}
			if (SwingDialogWindow.show(Main.this, "Enter MIDlet URL:", midletUrlPanel, true)) {
				Common.openMIDletUrlSafe(midletUrlPanel.getText(), new Runnable() {
					public void run() {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								if (recordStoreManagerDialog != null) {
									recordStoreManagerDialog.refresh();
								}
							}
						});
					}
				});
			}
		}
	};

	private ActionListener menuCloseMidletListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			common.startLauncher(MIDletBridge.getMIDletContext());
		}
	};



	private ActionListener menuStartCaptureListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (captureFileChooser == null) {
				ExtensionFileFilter fileFilter = new ExtensionFileFilter("GIF files");
				fileFilter.addExtension("gif");
				captureFileChooser = new JFileChooser();
				captureFileChooser.setFileFilter(fileFilter);
				captureFileChooser.setDialogTitle("Capture to GIF File...");
				captureFileChooser.setCurrentDirectory(new File(Config.getRecentDirectory("recentCaptureDirectory")));
			} else {
				captureFileChooser.updateUI();
			}

			if (captureFileChooser.showSaveDialog(Main.this) == JFileChooser.APPROVE_OPTION) {
				Config.setRecentDirectory("recentCaptureDirectory", captureFileChooser.getCurrentDirectory()
						.getAbsolutePath());
				String name = captureFileChooser.getSelectedFile().getName();
				if (!name.toLowerCase().endsWith(".gif") && name.indexOf('.') == -1) {
					name = name + ".gif";
				}
				File captureFile = new File(captureFileChooser.getSelectedFile().getParentFile(), name);
				if (!allowOverride(captureFile)) {
					return;
				}

				encoder = new AnimatedGifEncoder();
				encoder.start(captureFile.getAbsolutePath());
				currentCaptureFile = captureFile;
				Common.setStatusBar("Recording screen to " + captureFile.getName() + "...");

				menuStartCapture.setEnabled(false);
				menuStopCapture.setEnabled(true);

				((SwingDisplayComponent) emulatorContext.getDisplayComponent())
						.addDisplayRepaintListener(new DisplayRepaintListener() {
					long start = 0;

					public void repaintInvoked(Object repaintObject) {
						synchronized (Main.this) {
							if (encoder != null) {
								if (start == 0) {
									start = System.currentTimeMillis();
								} else {
									long current = System.currentTimeMillis();
									encoder.setDelay((int) (current - start));
									start = current;
								}

								encoder.addFrame(((J2SEGraphicsSurface) repaintObject).getImage());
							}
						}
					}
				});
			}
		}

		private boolean allowOverride(File file) {
			if (file.exists()) {
				int answer = JOptionPane.showConfirmDialog(Main.this, "Override the file:" + file + "?", "Question?",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (answer == 1 /* no */) {
					return false;
				}
			}

			return true;
		}
	};

	private ActionListener menuStopCaptureListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			menuStopCapture.setEnabled(false);

			String filename = "";
			synchronized (Main.this) {
				if (encoder != null) {
					encoder.finish();
					encoder = null;
					if (currentCaptureFile != null) {
						filename = currentCaptureFile.getName();
						currentCaptureFile = null;
					}
				}
			}
			if (!filename.isEmpty()) {
				Common.setStatusBar("Recording saved to " + filename);
			}

			menuStartCapture.setEnabled(true);
		}
	};

	private ActionListener menuScreenshotListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SwingDisplayComponent displayComponent = (SwingDisplayComponent) emulatorContext.getDisplayComponent();
			J2SEGraphicsSurface graphicsSurface = displayComponent.getGraphicsSurface();
			if (graphicsSurface == null || graphicsSurface.getImage() == null) {
				JOptionPane.showMessageDialog(Main.this, "No active display to capture.", "Screenshot Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (screenshotFileChooser == null) {
				ExtensionFileFilter fileFilter = new ExtensionFileFilter("PNG files");
				fileFilter.addExtension("png");
				screenshotFileChooser = new JFileChooser();
				screenshotFileChooser.setFileFilter(fileFilter);
				screenshotFileChooser.setDialogTitle("Save Screenshot...");
				screenshotFileChooser.setCurrentDirectory(new File(Config.getRecentDirectory("recentCaptureDirectory")));
			} else {
				screenshotFileChooser.updateUI();
			}

			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss");
			String defaultName = "screenshot_" + sdf.format(new java.util.Date()) + ".png";
			screenshotFileChooser.setSelectedFile(new File(screenshotFileChooser.getCurrentDirectory(), defaultName));

			if (screenshotFileChooser.showSaveDialog(Main.this) == JFileChooser.APPROVE_OPTION) {
				Config.setRecentDirectory("recentCaptureDirectory", screenshotFileChooser.getCurrentDirectory().getAbsolutePath());
				File file = screenshotFileChooser.getSelectedFile();
				String name = file.getName();
				if (!name.toLowerCase().endsWith(".png") && name.indexOf('.') == -1) {
					file = new File(file.getParentFile(), name + ".png");
				}

				if (file.exists()) {
					int answer = JOptionPane.showConfirmDialog(Main.this, "Override the file:" + file + "?", "Question?",
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (answer != JOptionPane.YES_OPTION) {
						return;
					}
				}

				try {
					BufferedImage img = graphicsSurface.getImage();
					BufferedImage imgCopy;
					synchronized (graphicsSurface) {
						imgCopy = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
						java.awt.Graphics2D g = imgCopy.createGraphics();
						g.drawImage(img, 0, 0, null);
						g.dispose();
					}
					javax.imageio.ImageIO.write(imgCopy, "png", file);
					Common.setStatusBar("Screenshot saved to " + file.getName());
				} catch (Exception ex) {
					Common.setStatusBar("Screenshot failed");
					JOptionPane.showMessageDialog(Main.this, "Failed to save screenshot: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	};

	private ActionListener menuMIDletNetworkConnectionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			boolean allowed = menuMIDletNetworkConnection.getState();
			org.neutron.cldc.http.Connection.setAllowNetworkConnection(allowed);
			org.neutron.device.ui.NetworkActivityTracker.setNetworkAccessEnabled(allowed);
			Config.setNetworkAccessEnabled(allowed);
			Common.setStatusBar("Network Access: " + (allowed ? "Enabled" : "Disabled"));
		}
	};

	private ActionListener menuRecordStoreManagerListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (recordStoreManagerDialog == null) {
				recordStoreManagerDialog = new RecordStoreManagerDialog(Main.this, common);
				recordStoreManagerDialog.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						menuRecordStoreManager.setState(false);
						Common.setStatusBar("Record Store Manager: Closed");
					}
				});
				recordStoreManagerDialog.pack();
				Rectangle window = Config.getWindow("recordStoreManager", new Rectangle(0, 0, 640, 320));
				recordStoreManagerDialog.setBounds(window.x, window.y, window.width, window.height);
			}
			boolean visible = !recordStoreManagerDialog.isVisible();
			recordStoreManagerDialog.setVisible(visible);
			Common.setStatusBar("Record Store Manager: " + (visible ? "Opened" : "Closed"));
		}
	};

	private ActionListener menuLogConsoleListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (logConsoleDialog == null) {
				logConsoleDialog = new SwingLogConsoleDialog(Main.this, Main.this.logQueueAppender);
				logConsoleDialog.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						menuLogConsole.setState(false);
						Common.setStatusBar("Log Console: Closed");
					}
				});
				logConsoleDialog.pack();
				// To avoid NPE on MacOS setFocusableWindowState(false) have to be called after pack()
				logConsoleDialog.setFocusableWindowState(false);
				Rectangle window = Config.getWindow("logConsole", new Rectangle(0, 0, 640, 320));
				logConsoleDialog.setBounds(window.x, window.y, window.width, window.height);
			}
			boolean visible = !logConsoleDialog.isVisible();
			logConsoleDialog.setVisible(visible);
			Common.setStatusBar("Log Console: " + (visible ? "Opened" : "Closed"));
		}
	};

	private ActionListener menuAboutListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SwingDialogWindow.show(Main.this, "About", new SwingAboutDialog(), false);
		}
	};

	private ActionListener menuSystemInfoListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SwingDialogWindow.show(Main.this, "System", new SwingSystemInfoDialog(), false);
		}
	};

	private ActionListener menuDocsListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (docsDialog == null) {
				docsDialog = new JDialog(Main.this, "Help & Documentation", false);
				final org.neutron.app.ui.swing.SwingDocsDialog panel = new org.neutron.app.ui.swing.SwingDocsDialog();
				docsDialog.getContentPane().setLayout(new BorderLayout());
				docsDialog.getContentPane().add(panel, BorderLayout.CENTER);

				javax.swing.JPanel actionPanel = new javax.swing.JPanel();
				actionPanel.add(panel.btOk);
				docsDialog.getContentPane().add(actionPanel, BorderLayout.SOUTH);
				docsDialog.pack();

				// Center relative to parent window
				Dimension frameSize = docsDialog.getSize();
				int x = Main.this.getLocation().x + ((Main.this.getWidth() - frameSize.width) / 2);
				int y = Main.this.getLocation().y + ((Main.this.getHeight() - frameSize.height) / 2);
				docsDialog.setLocation(Math.max(0, x), Math.max(0, y));

				panel.btOk.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ev) {
						docsDialog.setVisible(false);
					}
				});

				docsDialog.setIconImage(Main.this.getIconImage());
			}
			docsDialog.setVisible(true);
			docsDialog.toFront();
		}
	};

	private ActionListener menuExitListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			synchronized (Main.this) {
				if (encoder != null) {
					encoder.finish();
					encoder = null;
				}
			}

			if (logConsoleDialog != null) {
				Config.setWindow("logConsole", new Rectangle(logConsoleDialog.getX(), logConsoleDialog.getY(),
						logConsoleDialog.getWidth(), logConsoleDialog.getHeight()), logConsoleDialog.isVisible());
			}
			if (recordStoreManagerDialog != null) {
				Config.setWindow("recordStoreManager", new Rectangle(recordStoreManagerDialog.getX(),
						recordStoreManagerDialog.getY(), recordStoreManagerDialog.getWidth(), recordStoreManagerDialog
								.getHeight()), recordStoreManagerDialog.isVisible());
			}
			if (scaledDisplayFrame != null) {
				Config.setWindow("scaledDisplay", new Rectangle(scaledDisplayFrame.getX(), scaledDisplayFrame.getY(),
						0, 0), false);
			}
			Config.setWindow("main", new Rectangle(Main.this.getX(), Main.this.getY(), Main.this.getWidth(), Main.this
					.getHeight()), true);

			System.exit(0);
		}
	};

	private ActionListener menuScaledDisplayListener = new ActionListener() {
		private DisplayRepaintListener updateScaledImageListener;

		public void actionPerformed(ActionEvent e) {
			final JCheckBoxMenuItem selectedZoomLevelMenuItem = (JCheckBoxMenuItem) e.getSource();
			if (selectedZoomLevelMenuItem.isSelected()) {
				for (int i = 0; i < zoomLevels.length; ++i) {
					if (zoomLevels[i] != e.getSource()) {
						zoomLevels[i].setSelected(false);
					}
				}
				final int scale = Integer.parseInt(e.getActionCommand());
				if (scaledDisplayFrame != null) {
					((SwingDisplayComponent) emulatorContext.getDisplayComponent())
							.removeDisplayRepaintListener(updateScaledImageListener);
					scaledDisplayFrame.dispose();
				}
				scaledDisplayFrame = new JFrame(getTitle());
				scaledDisplayFrame.setContentPane(new JLabel(new ImageIcon()));
				updateScaledImageListener = new DisplayRepaintListener() {
					public void repaintInvoked(Object repaintObject) {
						updateScaledImage(scale, scaledDisplayFrame);
						scaledDisplayFrame.validate();
					}
				};
				scaledDisplayFrame.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent event) {
						selectedZoomLevelMenuItem.setSelected(false);
						menuScaledDisplayListener.actionPerformed(new ActionEvent(selectedZoomLevelMenuItem, ActionEvent.ACTION_PERFORMED, selectedZoomLevelMenuItem.getActionCommand()));
					}
				});
				scaledDisplayFrame.getContentPane().addMouseListener(new MouseListener() {
					private MouseListener receiver = ((SwingDisplayComponent) emulatorContext.getDisplayComponent())
							.getMouseListener();

					public void mouseClicked(MouseEvent e) {
						receiver.mouseClicked(createAdaptedMouseEvent(e, scale));
					}

					public void mousePressed(MouseEvent e) {
						receiver.mousePressed(createAdaptedMouseEvent(e, scale));
					}

					public void mouseReleased(MouseEvent e) {
						receiver.mouseReleased(createAdaptedMouseEvent(e, scale));
					}

					public void mouseEntered(MouseEvent e) {
						receiver.mouseEntered(createAdaptedMouseEvent(e, scale));
					}

					public void mouseExited(MouseEvent e) {
						receiver.mouseExited(createAdaptedMouseEvent(e, scale));
					}
				});
				scaledDisplayFrame.getContentPane().addMouseMotionListener(new MouseMotionListener() {
					private MouseMotionListener receiver = ((SwingDisplayComponent) emulatorContext
							.getDisplayComponent()).getMouseMotionListener();

					public void mouseDragged(MouseEvent e) {
						receiver.mouseDragged(createAdaptedMouseEvent(e, scale));
					}

					public void mouseMoved(MouseEvent e) {
						receiver.mouseMoved(createAdaptedMouseEvent(e, scale));
					}
				});
				scaledDisplayFrame.getContentPane().addMouseWheelListener(new MouseWheelListener() {
					private MouseWheelListener receiver = ((SwingDisplayComponent) emulatorContext
							.getDisplayComponent()).getMouseWheelListener();

					public void mouseWheelMoved(MouseWheelEvent e) {
						MouseWheelEvent adaptedEvent = createAdaptedMouseWheelEvent(e, scale);
						receiver.mouseWheelMoved(adaptedEvent);
					}
				});
				scaledDisplayFrame.addKeyListener(devicePanel);

				updateScaledImage(scale, scaledDisplayFrame);
				((SwingDisplayComponent) emulatorContext.getDisplayComponent())
						.addDisplayRepaintListener(updateScaledImageListener);
				scaledDisplayFrame.setIconImage(getIconImage());
				scaledDisplayFrame.setResizable(false);
				Point location = getLocation();
				Dimension size = getSize();
				Rectangle window = Config.getWindow("scaledDisplay", new Rectangle(location.x + size.width, location.y,
						0, 0));
				scaledDisplayFrame.setLocation(window.x, window.y);
				Config.setWindow("scaledDisplay", new Rectangle(scaledDisplayFrame.getX(), scaledDisplayFrame.getY(),
						0, 0), false);
				Config.setScaledDisplayZoom(scale);
				scaledDisplayFrame.pack();
				scaledDisplayFrame.setVisible(true);
				Common.setStatusBar("Scaled Display Zoom: " + scale + "x");
			} else {
				((SwingDisplayComponent) emulatorContext.getDisplayComponent())
						.removeDisplayRepaintListener(updateScaledImageListener);
				scaledDisplayFrame.dispose();
				Config.setScaledDisplayZoom(-1);
				Common.setStatusBar("Scaled Display Zoom: Off");
			}
		}

		private MouseEvent createAdaptedMouseEvent(MouseEvent e, int scale) {
			return new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(), e.getX() / scale, e
					.getY()
					/ scale, e.getClickCount(), e.isPopupTrigger(), e.getButton());
		}

		private MouseWheelEvent createAdaptedMouseWheelEvent(MouseWheelEvent e, int scale) {
			return new MouseWheelEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(), e.getX() / scale, e
					.getY()
					/ scale, e.getClickCount(), e.isPopupTrigger(), e.getScrollType(), e.getScrollAmount(), e
					.getWheelRotation());
		}

		private void updateScaledImage(int scale, JFrame scaledLCDFrame) {
			J2SEGraphicsSurface graphicsSurface = 
					((SwingDisplayComponent) emulatorContext.getDisplayComponent()).getGraphicsSurface();
			
			BufferedImage img = graphicsSurface.getImage();
			BufferedImage scaledImg = new BufferedImage(img.getWidth() * scale, img.getHeight() * scale, img.getType());
			Graphics2D imgGraphics = scaledImg.createGraphics();
			imgGraphics.scale(scale, scale);
			imgGraphics.drawImage(img, 0, 0, null);
			
			((ImageIcon) (((JLabel) scaledLCDFrame.getContentPane()).getIcon())).setImage(scaledImg);
			((JLabel) scaledLCDFrame.getContentPane()).repaint();
		}
	};

	private StatusBarListener statusBarListener = new StatusBarListener() {
		public void statusBarChanged(final String text) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					statusBar.setText(text);
					if (Config.isFullscreen() && text != null && !text.trim().isEmpty()) {
						statusBar.setVisible(true);
						revalidate();
						repaint();
						if (statusBarHideTimer != null) {
							statusBarHideTimer.cancel();
						}
						statusBarHideTimer = new Timer(true);
						statusBarHideTimer.schedule(new TimerTask() {
							public void run() {
								SwingUtilities.invokeLater(new Runnable() {
									public void run() {
										if (Config.isFullscreen()) {
											statusBar.setVisible(false);
											statusBar.clearTransientMessage();
											revalidate();
											repaint();
										}
									}
								});
							}
						}, 5000);
					}
				}
			});
		}
	};

	private ResponseInterfaceListener responseInterfaceListener = new ResponseInterfaceListener() {
		public void stateChanged(final boolean state) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					menuOpenMIDletFile.setEnabled(state);
					menuOpenMIDletURL.setEnabled(state);
				}
			});
		}
	};

	private ComponentListener componentListener = new ComponentAdapter() {
		Timer timer;

		int count = 0;

		public void componentResized(ComponentEvent e) {
			count++;
			DeviceDisplayImpl deviceDisplay = (DeviceDisplayImpl) DeviceFactory.getDevice().getDeviceDisplay();
			if (deviceDisplay.isResizable()) {
				javax.microedition.midlet.MIDlet current = MIDletBridge.getCurrentMIDlet();
				boolean isGameRunning = (current != null && !(current instanceof org.neutron.app.launcher.Launcher));

				if (!isGameRunning) {
					setDeviceSize(deviceDisplay, devicePanel.getWidth(), devicePanel.getHeight());
					devicePanel.revalidate();
					statusBarListener.statusBarChanged("New size: " + deviceDisplay.getFullWidth() + "x"
							+ deviceDisplay.getFullHeight());
				} else {
					devicePanel.revalidate();
					devicePanel.repaint();
					statusBarListener.statusBarChanged("Window size: " + devicePanel.getWidth() + "x"
							+ devicePanel.getHeight() + " (Scaled from: " + deviceDisplay.getFullWidth() + "x"
							+ deviceDisplay.getFullHeight() + ")");
				}

				synchronized (statusBarListener) {
					if (timer == null) {
						timer = new Timer();
					}
					timer.schedule(new CountTimerTask(count) {
						public void run() {
							if (counter == count) {
								Config.setDeviceEntryDisplaySize(deviceEntry, new Rectangle(0, 0, devicePanel
										.getWidth(), devicePanel.getHeight()));
								statusBarListener.statusBarChanged("");
								timer.cancel();
								timer = null;
							}
						}
					}, 2000);
				}
			}
		}
	};

	private WindowAdapter windowListener = new WindowAdapter() {
		public void windowClosing(WindowEvent ev) {
			menuExitListener.actionPerformed(null);
		}

		public void windowIconified(WindowEvent ev) {
			MIDletBridge.getMIDletAccess(MIDletBridge.getCurrentMIDlet()).pauseApp();
		}

		public void windowDeiconified(WindowEvent ev) {
			try {
				MIDletBridge.getMIDletAccess(MIDletBridge.getCurrentMIDlet()).startApp();
			} catch (MIDletStateChangeException ex) {
				System.err.println(ex);
			}
		}
	};

	public Main() {
		this(null);
	}

	public Main(DeviceEntry defaultDevice) {
		instance = this;

		this.logQueueAppender = new QueueAppender(1024);
		Logger.addAppender(logQueueAppender);

		menuBar = new JMenuBar();

		JMenu menuFile = new JMenu("Run");

		menuOpenMIDletFile = new JMenuItem("Run JAR File");
		menuOpenMIDletFile.addActionListener(menuOpenMIDletFileListener);
		menuFile.add(menuOpenMIDletFile);

		menuOpenMIDletURL = new JMenuItem("Run from URL");
		menuOpenMIDletURL.addActionListener(menuOpenMIDletURLListener);
		menuFile.add(menuOpenMIDletURL);

		final JMenu menuConnectedDirs = new JMenu("Connected Directories");
		menuConnectedDirs.addMenuListener(new javax.swing.event.MenuListener() {
			public void menuSelected(javax.swing.event.MenuEvent e) {
				menuConnectedDirs.removeAll();

				JMenuItem explorerItem = new JMenuItem("Library Explorer");
				explorerItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ev) {
						SwingLibraryExplorerDialog dialog = new SwingLibraryExplorerDialog(Main.this);
						dialog.setVisible(true);
					}
				});
				menuConnectedDirs.add(explorerItem);

				JMenuItem connectItem = new JMenuItem("Connect New Directory");
				connectItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ev) {
						JFileChooser chooser = new JFileChooser();
						chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						chooser.setDialogTitle("Select Directory of JAR Files");
						String lastDir = Config.getRecentDirectory("recentLibraryDirectory");
						if (lastDir != null && !lastDir.isEmpty()) {
							chooser.setCurrentDirectory(new File(lastDir));
						}
						if (chooser.showOpenDialog(Main.this) == JFileChooser.APPROVE_OPTION) {
							File dir = chooser.getSelectedFile();
							Config.setRecentDirectory("recentLibraryDirectory", dir.getParent());
							Config.addConnectedDirectory(dir.getAbsolutePath());
							Common.setStatusBar("Connected directory: " + dir.getName());
						}
					}
				});
				menuConnectedDirs.add(connectItem);

				java.util.List dirs = Config.getConnectedDirectories();
				if (!dirs.isEmpty()) {
					menuConnectedDirs.addSeparator();
					for (int i = 0; i < dirs.size(); i++) {
						final String dirPath = (String) dirs.get(i);
						File dir = new File(dirPath);
						String dirName = dir.getName();
						if (dirName.isEmpty()) {
							dirName = dirPath;
						}
						final JMenu dirMenu = new JMenu(dirName);
						dirMenu.setToolTipText(dirPath);
						dirMenu.add(new JMenuItem("Loading..."));

						dirMenu.addMenuListener(new javax.swing.event.MenuListener() {
							public void menuSelected(javax.swing.event.MenuEvent ev2) {
								dirMenu.removeAll();
								java.util.List games = getCachedOrScannedGames(dirPath);
								if (games.isEmpty()) {
									JMenuItem noneItem = new JMenuItem("(No JAR files found)");
									noneItem.setEnabled(false);
									dirMenu.add(noneItem);
								} else {
									for (int j = 0; j < games.size(); j++) {
										final SwingLibraryExplorerDialog.GameInfo game = (SwingLibraryExplorerDialog.GameInfo) games.get(j);
										JMenuItem gameItem = new JMenuItem(game.name);
										if (game.iconCachePath != null) {
											File iconFile = new File(game.iconCachePath);
											if (iconFile.exists()) {
												try {
													ImageIcon icon = new ImageIcon(iconFile.getAbsolutePath());
													if (icon.getIconWidth() != 16 || icon.getIconHeight() != 16) {
														Image img = icon.getImage();
														Image scaled = img.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
														icon = new ImageIcon(scaled);
													}
													gameItem.setIcon(icon);
												} catch (Exception ex) {
													// ignore icon load errors
												}
											}
										}
										gameItem.addActionListener(new ActionListener() {
											public void actionPerformed(ActionEvent ev3) {
												String url = IOUtils.getCanonicalFileURL(new File(game.jarPath));
												Common.openMIDletUrlSafe(url, null);
											}
										});
										dirMenu.add(gameItem);
									}
								}
							}
							public void menuDeselected(javax.swing.event.MenuEvent ev2) {}
							public void menuCanceled(javax.swing.event.MenuEvent ev2) {}
						});
						menuConnectedDirs.add(dirMenu);
					}
				}
			}
			public void menuDeselected(javax.swing.event.MenuEvent e) {}
			public void menuCanceled(javax.swing.event.MenuEvent e) {}
		});
		menuFile.add(menuConnectedDirs);

		JMenuItem menuItemTmp = new JMenuItem("Terminate Process");
		menuItemTmp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
		menuItemTmp.addActionListener(menuCloseMidletListener);
		menuFile.add(menuItemTmp);

		menuFile.addSeparator();

		JMRUMenu urlsMRU = new JMRUMenu("Installed Games");
		urlsMRU.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (event instanceof JMRUMenu.MRUActionEvent) {
					Common.openMIDletUrlSafe(((MidletURLReference) ((JMRUMenu.MRUActionEvent) event).getSourceMRU())
							.getUrl(), new Runnable() {
						public void run() {
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									if (recordStoreManagerDialog != null) {
										recordStoreManagerDialog.refresh();
									}
								}
							});
						}
					});
				}
			}
		});

		Config.getUrlsMRU().setListener(urlsMRU);
		menuFile.add(urlsMRU);



		menuFile.addSeparator();

		JMenuItem menuOpenRootDirectory = new JMenuItem("Open Root Directory");
		menuOpenRootDirectory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (java.awt.Desktop.isDesktopSupported()) {
						java.awt.Desktop.getDesktop().open(Config.getConfigPath());
						Common.setStatusBar("Opened root directory");
					} else {
						JOptionPane.showMessageDialog(Main.this, "Desktop is not supported on this platform.", "Error", JOptionPane.ERROR_MESSAGE);
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(Main.this, "Failed to open directory: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		menuFile.add(menuOpenRootDirectory);

		JMenu menuSnapshot = new JMenu("Snapshot Manager");
		JMenuItem menuBackup = new JMenuItem("Backup");
		menuBackup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				org.neutron.app.util.SnapshotManager.backup(Main.this);
			}
		});
		menuSnapshot.add(menuBackup);

		JMenuItem menuRestore = new JMenuItem("Restore");
		menuRestore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				org.neutron.app.util.SnapshotManager.restore(Main.this);
			}
		});
		menuSnapshot.add(menuRestore);
		menuFile.add(menuSnapshot);

		menuScreenshot = new JMenuItem("Take Screenshot");
		menuScreenshot.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		menuScreenshot.addActionListener(menuScreenshotListener);
		menuFile.add(menuScreenshot);

		menuStartCapture = new JMenuItem("Start Recording");
		menuStartCapture.addActionListener(menuStartCaptureListener);
		menuFile.add(menuStartCapture);

		menuStopCapture = new JMenuItem("Stop Recording");
		menuStopCapture.setEnabled(false);
		menuStopCapture.addActionListener(menuStopCaptureListener);
		menuFile.add(menuStopCapture);

		menuFile.addSeparator();

		JMenuItem menuItem = new JMenuItem("Exit");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(menuExitListener);
		menuFile.add(menuItem);

		JMenu menuOptions = new JMenu("Config");

		JMenu menuScaleLCD = new JMenu("Scaled Display");
		menuOptions.add(menuScaleLCD);
		zoomLevels = new JCheckBoxMenuItem[3];
		for (int i = 0; i < zoomLevels.length; ++i) {
			zoomLevels[i] = new JCheckBoxMenuItem("x " + (i + 2));
			zoomLevels[i].setActionCommand("" + (i + 2));
			zoomLevels[i].addActionListener(menuScaledDisplayListener);
			menuScaleLCD.add(zoomLevels[i]);
		}





		menuRecordStoreManager = new JCheckBoxMenuItem("Record Store Manager");
		menuRecordStoreManager.setState(false);
		menuRecordStoreManager.addActionListener(menuRecordStoreManagerListener);
		menuOptions.add(menuRecordStoreManager);

		menuLogConsole = new JCheckBoxMenuItem("Log Console");
		menuLogConsole.setState(false);
		menuLogConsole.addActionListener(menuLogConsoleListener);
		menuOptions.add(menuLogConsole);

		menuSleepMode = new JCheckBoxMenuItem("Sleep Mode");
		menuSleepMode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
		menuSleepMode.setState(false);
		menuSleepMode.addActionListener(menuSleepModeListener);
		menuOptions.add(menuSleepMode);

		menuTheme = new JMenu("Theme");
		ButtonGroup themeGroup = new ButtonGroup();
		String[] themes = {
			"FlatLaf Dark", "FlatLaf Light", "FlatLaf IntelliJ", 
			"FlatLaf Dracula", "FlatLaf macOS Dark", "FlatLaf macOS Light", 
			"System Look and Feel"
		};
		String currentTheme = Config.getTheme();
		for (final String themeName : themes) {
			JRadioButtonMenuItem themeItem = new JRadioButtonMenuItem(themeName, themeName.equals(currentTheme));
			themeItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Config.setTheme(themeName);
					applyTheme(themeName, devicePanel);
					Common.setStatusBar("Theme: " + themeName);
				}
			});
			themeGroup.add(themeItem);
			menuTheme.add(themeItem);
		}
		menuOptions.add(menuTheme);

		menuFilter = new JMenu("Graphics Filter");
		ButtonGroup filterGroup = new ButtonGroup();
		String[] filters = {
			"Nearest Neighbor", "Bilinear", "Bicubic", "Scale2x", "CRT Scanlines", "LCD Grid"
		};
		String currentFilter = Config.getGraphicsFilter();
		for (final String filterName : filters) {
			JRadioButtonMenuItem filterItem = new JRadioButtonMenuItem(filterName, filterName.equals(currentFilter));
			filterItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Config.setGraphicsFilter(filterName);
					if (devicePanel != null) {
						SwingDisplayComponent sdc = (SwingDisplayComponent) devicePanel.getDisplayComponent();
						if (sdc != null) {
							sdc.repaint();
						}
					}
					Common.setStatusBar("Graphics Filter: " + filterName);
				}
			});
			filterGroup.add(filterItem);
			menuFilter.add(filterItem);
		}
		menuOptions.add(menuFilter);

		JMenuItem itemVideoSettings = new JMenuItem("Video Settings");
		itemVideoSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingVideoSettingsPanel panel = new SwingVideoSettingsPanel(devicePanel);
				if (SwingDialogWindow.show(Main.this, "Video Settings", panel, true)) {
					Common.setStatusBar("Video Settings updated");
				} else {
					panel.revertSettings();
				}
			}
		});
		menuOptions.add(itemVideoSettings);

		JMenu menuMemoryLimit = new JMenu("Memory Limit");
		ButtonGroup memoryGroup = new ButtonGroup();
		int[] memoryLimits = {0, 32, 64, 128, 256, 512};
		String[] memoryLabels = {"Unlimited", "32 MB", "64 MB", "128 MB", "256 MB", "512 MB"};
		int currentLimit = Config.getMemoryLimit();
		for (int i = 0; i < memoryLimits.length; i++) {
			final int limitVal = memoryLimits[i];
			final String label = memoryLabels[i];
			JRadioButtonMenuItem limitItem = new JRadioButtonMenuItem(label, currentLimit == limitVal);
			limitItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Config.setMemoryLimit(limitVal);
					Common.setStatusBar("Memory Limit: " + label);
				}
			});
			memoryGroup.add(limitItem);
			menuMemoryLimit.add(limitItem);
		}
		menuOptions.add(menuMemoryLimit);

		menuOptions.addSeparator();
		menuShowMouseCoordinates = new JCheckBoxMenuItem("Mouse Coordinates");
		menuShowMouseCoordinates.setState(Config.isShowMouseCoordinates());
		menuShowMouseCoordinates.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				devicePanel.switchShowMouseCoordinates();
			}
		});
		menuOptions.add(menuShowMouseCoordinates);

		JMenu menuControls = new JMenu("Controls");
		menuFullscreen = new JCheckBoxMenuItem("Fullscreen");
		menuFullscreen.setState(Config.isFullscreen());
		menuFullscreen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setFullscreenMode(menuFullscreen.isSelected());
			}
		});
		menuControls.add(menuFullscreen);

		JMenuItem menuProxySettings = new JMenuItem("X-Proxy");
		menuProxySettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingProxySettingsPanel panel = new SwingProxySettingsPanel();
				if (SwingDialogWindow.show(Main.this, "Proxy Settings", panel, true)) {
					Common.setStatusBar("Proxy settings updated");
				}
			}
		});
		menuControls.add(menuProxySettings);

		menuMIDletNetworkConnection = new JCheckBoxMenuItem("Network Access");
		menuMIDletNetworkConnection.setState(Config.isNetworkAccessEnabled());
		menuMIDletNetworkConnection.addActionListener(menuMIDletNetworkConnectionListener);
		menuControls.add(menuMIDletNetworkConnection);

		JMenu menuFrameRate = new JMenu("Frame Rate");
		ButtonGroup fpsGroup = new ButtonGroup();
		int currentFps = Config.getMaxFps();
		int[] fpsValues = { -1, 60, 30, 20, 15 };
		String[] fpsLabels = { "Unlimited", "60 FPS", "30 FPS", "20 FPS", "15 FPS" };
		for (int i = 0; i < fpsValues.length; i++) {
			final int fpsVal = fpsValues[i];
			final String label = fpsLabels[i];
			JRadioButtonMenuItem fpsItem = new JRadioButtonMenuItem(label, currentFps == fpsVal);
			fpsItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Config.setMaxFps(fpsVal);
					org.neutron.device.ui.EventDispatcher.maxFps = fpsVal;
					Common.setStatusBar("Frame Rate Limit: " + label);
				}
			});
			fpsGroup.add(fpsItem);
			menuFrameRate.add(fpsItem);
		}
		menuControls.add(menuFrameRate);

		JMenu menuEmulationSpeed = new JMenu("Emulation Speed");
		ButtonGroup speedGroup = new ButtonGroup();
		double currentSpeed = Config.getSpeedMultiplier();
		double[] speedValues = { 1.0, 1.5, 2.0, 4.0, 8.0, 0.5 };
		String[] speedLabels = { "Normal (1.0x)", "1.5x", "2.0x", "4.0x", "8.0x", "Slow Motion (0.5x)" };
		for (int i = 0; i < speedValues.length; i++) {
			final double speedVal = speedValues[i];
			final String label = speedLabels[i];
			JRadioButtonMenuItem speedItem = new JRadioButtonMenuItem(label, Math.abs(currentSpeed - speedVal) < 0.01);
			speedItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Config.setSpeedMultiplier(speedVal);
					Common.setStatusBar("Emulation Speed: " + label);
				}
			});
			speedGroup.add(speedItem);
			menuEmulationSpeed.add(speedItem);
		}
		menuControls.add(menuEmulationSpeed);

		JMenuItem menuNetworkCapture = new JMenuItem("Network Capture");
		menuNetworkCapture.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingNetworkCapturePanel panel = new SwingNetworkCapturePanel();
				SwingDialogWindow.show(Main.this, "Network Capture", panel, true);
				panel.dispose();
			}
		});
		menuControls.add(menuNetworkCapture);

		final JCheckBoxMenuItem menuHudOverlay = new JCheckBoxMenuItem("HUD Overlay", Config.isPerfHudEnabled());
		menuHudOverlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean enabled = menuHudOverlay.isSelected();
				Config.setPerfHudEnabled(enabled);
				SwingPerfHUD.setEnabled(enabled);
				if (devicePanel != null && devicePanel.getDisplayComponent() != null) {
					((SwingDisplayComponent) devicePanel.getDisplayComponent()).repaint();
				}
				Common.setStatusBar("HUD Overlay: " + (enabled ? "Enabled" : "Disabled"));
			}
		});
		menuControls.add(menuHudOverlay);

		final JCheckBoxMenuItem menuNetworkOverlay = new JCheckBoxMenuItem("Network Overlay", Config.isNetworkOverlayEnabled());
		menuNetworkOverlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean enabled = menuNetworkOverlay.isSelected();
				Config.setNetworkOverlayEnabled(enabled);
				SwingNetworkOverlay.setEnabled(enabled);
				org.neutron.device.ui.NetworkActivityTracker.setPingEnabled(enabled);
				if (devicePanel != null && devicePanel.getDisplayComponent() != null) {
					((SwingDisplayComponent) devicePanel.getDisplayComponent()).repaint();
				}
				Common.setStatusBar("Network Overlay: " + (enabled ? "Enabled" : "Disabled"));
			}
		});
		menuControls.add(menuNetworkOverlay);

		JMenuItem menuAutoClicker = new JMenuItem("Tap Automator");
		menuAutoClicker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingAutoClickerSettingsPanel panel = new SwingAutoClickerSettingsPanel();
				if (SwingDialogWindow.show(Main.this, "Tap Automator Settings", panel, true)) {
					panel.applySettings();
					if (devicePanel != null && devicePanel.getDisplayComponent() != null) {
						((SwingDisplayComponent) devicePanel.getDisplayComponent()).repaint();
					}
					Common.setStatusBar("Tap Automator settings updated");
				}
			}
		});
		menuControls.add(menuAutoClicker);

		JMenu menuMisc = new JMenu("Misc");
		JMenuItem menuAbout = new JMenuItem("About");
		menuAbout.addActionListener(menuAboutListener);
		menuMisc.add(menuAbout);

		JMenuItem menuSystemInfo = new JMenuItem("System");
		menuSystemInfo.addActionListener(menuSystemInfoListener);
		menuMisc.add(menuSystemInfo);

		JMenuItem menuDocs = new JMenuItem("Docs");
		menuDocs.addActionListener(menuDocsListener);
		menuMisc.add(menuDocs);


		menuBar.add(menuFile);
		menuBar.add(menuOptions);
		menuBar.add(menuControls);
		menuBar.add(menuMisc);
		setJMenuBar(menuBar);

		setTitle("Neutron");

		this.setIconImage(Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/org/neutron/icon.png")));

		addWindowListener(windowListener);

		Config.loadConfig(defaultDevice, emulatorContext);
		org.neutron.cldc.http.Connection.setAllowNetworkConnection(Config.isNetworkAccessEnabled());
		org.neutron.device.ui.NetworkActivityTracker.setNetworkAccessEnabled(Config.isNetworkAccessEnabled());
		org.neutron.device.ui.EventDispatcher.maxFps = Config.getMaxFps();
		Logger.setLocationEnabled(Config.isLogConsoleLocationEnabled());
		org.neutron.app.ui.swing.SwingPerfHUD.setEnabled(Config.isPerfHudEnabled());
		org.neutron.app.ui.swing.SwingNetworkOverlay.setEnabled(Config.isNetworkOverlayEnabled());
		org.neutron.device.ui.NetworkCapture.setEnabled(Config.isNetworkCaptureEnabled());
		org.neutron.app.ui.swing.SwingAutoClicker.init();

		int persistedZoom = Config.getScaledDisplayZoom();
		if (persistedZoom >= 2 && persistedZoom <= 4) {
			int index = persistedZoom - 2;
			zoomLevels[index].setSelected(true);
			menuScaledDisplayListener.actionPerformed(new ActionEvent(zoomLevels[index], ActionEvent.ACTION_PERFORMED, zoomLevels[index].getActionCommand()));
		}

		boolean sleepEnabled = Config.isSleepModeEnabled();
		menuSleepMode.setState(sleepEnabled);
		org.neutron.app.util.SleepManager.setSleepEnabled(sleepEnabled);

		Rectangle window = Config.getWindow("main", new Rectangle(0, 0, 160, 120));
		this.setLocation(window.x, window.y);

		getContentPane().add(createContents(getContentPane()), "Center");

		this.common = new Common(emulatorContext);
		this.common.setStatusBarListener(statusBarListener);
		this.common.setResponseInterfaceListener(responseInterfaceListener);
		this.common.setGameStateListener(new GameStateListener() {
			public void gameStateChanged(final boolean gameRunning) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						updateResizeButtonVisibility();
					}
				});
			}
		});
		this.common.loadImplementationsFromConfig();

		this.resizeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				javax.microedition.midlet.MIDlet current = MIDletBridge.getCurrentMIDlet();
				if (current != null && !(current instanceof org.neutron.app.launcher.Launcher)) {
					JOptionPane.showMessageDialog(Main.this,
							"Resizing the internal game resolution is not allowed while a game is running.\n"
							+ "Please resize before starting the game, or simply drag the window borders to stretch/scale the display.",
							"Cannot Resize Internal Resolution",
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				if (resizeDeviceDisplayDialog == null) {
					resizeDeviceDisplayDialog = new ResizeDeviceDisplayDialog();
				}
				DeviceDisplayImpl deviceDisplay = (DeviceDisplayImpl) DeviceFactory.getDevice().getDeviceDisplay();
				resizeDeviceDisplayDialog.setDeviceDisplaySize(deviceDisplay.getFullWidth(), deviceDisplay
						.getFullHeight());
				if (SwingDialogWindow.show(Main.this, "Enter new size...", resizeDeviceDisplayDialog, true)) {
					devicePanel.setPreferredSize(null);
				    setDeviceSize(deviceDisplay, resizeDeviceDisplayDialog.getDeviceDisplayWidth(), resizeDeviceDisplayDialog.getDeviceDisplayHeight());
					pack();
					devicePanel.requestFocus();
				}
			}
		});

		org.neutron.device.ui.NetworkActivityTracker.setPingEnabled(Config.isNetworkOverlayEnabled());

		this.resizeButton.putClientProperty("JButton.buttonType", "toolBarButton");
		this.resizeButton.setFont(this.resizeButton.getFont().deriveFont(11.0f));
		this.resizeButton.setFocusable(false);
		statusBar.getRightPanel().add(this.resizeButton);

		statusBar.setOnCoordinateBadgeClick(new Runnable() {
			public void run() {
				if (devicePanel != null) {
					devicePanel.switchShowMouseCoordinates();
					if (menuShowMouseCoordinates != null) {
						menuShowMouseCoordinates.setSelected(Config.isShowMouseCoordinates());
					}
				}
			}
		});

		getContentPane().add(statusBar, "South");

		Message.addListener(new SwingErrorMessageDialogPanel(this));

		devicePanel.setTransferHandler(new DropTransferHandler());

		// Floating Menu Button setup
		floatingMenuButton = new JButton("☰") {
			@Override
			protected void paintComponent(java.awt.Graphics g) {
				java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
				g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
				// Shadow
				g2.setColor(new java.awt.Color(0, 0, 0, 40));
				g2.fillOval(1, 1, getWidth() - 2, getHeight() - 2);
				// Background
				if (getModel().isPressed()) {
					g2.setColor(new java.awt.Color(80, 80, 80, 200));
				} else if (getModel().isRollover()) {
					g2.setColor(new java.awt.Color(110, 110, 110, 200));
				} else {
					g2.setColor(new java.awt.Color(130, 130, 130, 160));
				}
				g2.fillOval(2, 2, getWidth() - 4, getHeight() - 4);
				// Border
				g2.setColor(new java.awt.Color(255, 255, 255, 120));
				g2.drawOval(2, 2, getWidth() - 4, getHeight() - 4);
				g2.dispose();
				super.paintComponent(g);
			}
		};
		floatingMenuButton.setFocusable(false);
		floatingMenuButton.setToolTipText("Show Menu Bar");
		floatingMenuButton.setSize(32, 32);
		floatingMenuButton.setContentAreaFilled(false);
		floatingMenuButton.setBorderPainted(false);
		floatingMenuButton.setFocusPainted(false);
		floatingMenuButton.setOpaque(false);
		floatingMenuButton.setForeground(java.awt.Color.WHITE);
		floatingMenuButton.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
		getLayeredPane().add(floatingMenuButton, JLayeredPane.PALETTE_LAYER);
		floatingMenuButton.setVisible(false);

		floatingMenuButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuBar.setVisible(true);
				floatingMenuButton.setVisible(false);
				revalidate();
				repaint();
				resetMenuInactivityTimer();
			}
		});

		// Global listener to track mouse activity to reset 30s inactivity timer
		awtEventListener = new java.awt.event.AWTEventListener() {
			public void eventDispatched(java.awt.AWTEvent event) {
				if (event instanceof java.awt.event.MouseEvent) {
					resetMenuInactivityTimer();
				}
			}
		};
		try {
			java.awt.Toolkit.getDefaultToolkit().addAWTEventListener(awtEventListener, java.awt.AWTEvent.MOUSE_EVENT_MASK | java.awt.AWTEvent.MOUSE_MOTION_EVENT_MASK);
		} catch (SecurityException ex) {
			// fallback
		}

		// Periodic checker to auto-hide menu bar after 30 seconds of inactivity
		menuTimer = new Timer(true);
		menuTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (Config.isFullscreen() && menuBar.isVisible()) {
					boolean isMenuOpen = false;
					try {
						isMenuOpen = javax.swing.MenuSelectionManager.defaultManager().getSelectedPath().length > 0;
					} catch (Exception ex) {
						// ignore
					}
					if (isMenuOpen) {
						resetMenuInactivityTimer();
					} else if (System.currentTimeMillis() - lastActivityTime > 30000) {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								if (Config.isFullscreen() && menuBar.isVisible()) {
									menuBar.setVisible(false);
									floatingMenuButton.setVisible(true);
									revalidate();
									repaint();
								}
							}
						});
					}
				}
			}
		}, 1000, 1000);

		// Set initial fullscreen mode
		setFullscreenMode(Config.isFullscreen());
	}

	protected Component createContents(Container parent) {
		devicePanel = new SwingDeviceComponent();
		devicePanel.addKeyListener(devicePanel);
		addKeyListener(devicePanel);

		return devicePanel;
	}

	@Override
	public void validate() {
		super.validate();
		updateFloatingButtonBounds();
	}

	private void updateFloatingButtonBounds() {
		if (floatingMenuButton != null && floatingMenuButton.isVisible()) {
			Point p = SwingUtilities.convertPoint(getContentPane(), new Point(10, 10), getLayeredPane());
			floatingMenuButton.setLocation(p.x, p.y);
		}
	}

	public void setFullscreenMode(final boolean enabled) {
		Config.setFullscreen(enabled);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (menuFullscreen != null) {
					menuFullscreen.setSelected(enabled);
				}
				if (enabled) {
					if (menuBar != null) {
						menuBar.setVisible(false);
					}
					if (statusBar != null) {
						statusBar.setVisible(false);
					}
					if (floatingMenuButton != null) {
						floatingMenuButton.setVisible(true);
					}
				} else {
					if (menuBar != null) {
						menuBar.setVisible(true);
					}
					if (statusBar != null) {
						statusBar.setVisible(true);
					}
					if (floatingMenuButton != null) {
						floatingMenuButton.setVisible(false);
					}
				}
				revalidate();
				repaint();
			}
		});
	}

	private void resetMenuInactivityTimer() {
		lastActivityTime = System.currentTimeMillis();
	}

	@Override
	public void dispose() {
		if (awtEventListener != null) {
			try {
				java.awt.Toolkit.getDefaultToolkit().removeAWTEventListener(awtEventListener);
			} catch (SecurityException ex) {
				// ignore
			}
		}
		if (menuTimer != null) {
			menuTimer.cancel();
		}
		if (statusBarHideTimer != null) {
			statusBarHideTimer.cancel();
		}
		super.dispose();
	}

	public boolean setDevice(DeviceEntry entry) {
		if (DeviceFactory.getDevice() != null) {
			// ((J2SEDevice) DeviceFactory.getDevice()).dispose();
		}
		final String errorTitle = "Error creating device";
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			if (entry.getFileName() != null) {
				URL[] urls = new URL[1];
				urls[0] = new File(Config.getConfigPath(), entry.getFileName()).toURI().toURL();
				classLoader = Common.createExtensionsClassLoader(urls);
			}

			// TODO font manager have to be moved from emulatorContext into
			// device
			emulatorContext.getDeviceFontManager().init();

			Device device = DeviceImpl.create(emulatorContext, classLoader, entry.getDescriptorLocation(),
					J2SEDevice.class);
			this.deviceEntry = entry;

			DeviceDisplayImpl deviceDisplay = (DeviceDisplayImpl) device.getDeviceDisplay();
			if (deviceDisplay.isResizable()) {
				Rectangle size = Config.getDeviceEntryDisplaySize(entry);
				if (size != null) {
				    setDeviceSize(deviceDisplay, size.width, size.height);
				} else {
					setDeviceSize(deviceDisplay, deviceDisplay.getFullWidth(), deviceDisplay.getFullHeight());
				}
			}
			common.setDevice(device);
			updateDevice();
			return true;
		} catch (MalformedURLException e) {
			Message.error(errorTitle, errorTitle + ", " + Message.getCauseMessage(e), e);
		} catch (IOException e) {
			Message.error(errorTitle, errorTitle + ", " + Message.getCauseMessage(e), e);
		} catch (Throwable e) {
			Message.error(errorTitle, errorTitle + ", " + Message.getCauseMessage(e), e);
		}
		return false;
	}
	
	protected void setDeviceSize(DeviceDisplayImpl deviceDisplay, int width, int height) {
	    // move the soft buttons
	    int menuh = 0;
	    Enumeration en = DeviceFactory.getDevice().getSoftButtons().elements();
        while (en.hasMoreElements()) {
            SoftButton button = (SoftButton) en.nextElement();
            Rectangle paintable = button.getPaintable();
            paintable.y = height - paintable.height;
            if ("SOFT2".equals(button.getName())) {
                paintable.x = width - paintable.width - 1;
            } else if ("SOFT1".equals(button.getName())) {
                paintable.x = 1;
            }
            menuh = paintable.height;
        }
        // resize the display area
        deviceDisplay.setDisplayPaintable(new Rectangle(0, 0, width, height - menuh));
        deviceDisplay.setDisplayRectangle(new Rectangle(0, 0, width, height));
        ((SwingDisplayComponent) devicePanel.getDisplayComponent()).init();
        // update display
        MIDletAccess ma = MIDletBridge.getMIDletAccess();
        if (ma == null) {
            return;
        }
        DisplayAccess da = ma.getDisplayAccess();
        if (da != null) {
            da.sizeChanged();
            deviceDisplay.repaint(0, 0, deviceDisplay.getFullWidth(), deviceDisplay.getFullHeight());
        }
	}

	protected void updateThemeSelection() {
		String currentTheme = Config.getTheme();
		for (int i = 0; i < menuTheme.getItemCount(); i++) {
			JMenuItem item = menuTheme.getItem(i);
			if (item instanceof JRadioButtonMenuItem) {
				if (item.getText().equals(currentTheme)) {
					item.setSelected(true);
					break;
				}
			}
		}
	}

	protected void updateFilterSelection() {
		if (menuFilter == null) {
			return;
		}
		String currentFilter = Config.getGraphicsFilter();
		for (int i = 0; i < menuFilter.getItemCount(); i++) {
			JMenuItem item = menuFilter.getItem(i);
			if (item instanceof JRadioButtonMenuItem) {
				if (item.getText().equals(currentFilter)) {
					item.setSelected(true);
					break;
				}
			}
		}
	}

	private void updateResizeButtonVisibility() {
		if (DeviceFactory.getDevice() == null || DeviceFactory.getDevice().getDeviceDisplay() == null) {
			resizeButton.setVisible(false);
			return;
		}
		DeviceDisplayImpl deviceDisplay = (DeviceDisplayImpl) DeviceFactory.getDevice().getDeviceDisplay();
		javax.microedition.midlet.MIDlet current = MIDletBridge.getCurrentMIDlet();
		boolean isGameRunning = (current != null && !(current instanceof org.neutron.app.launcher.Launcher));

		if (deviceDisplay.isResizable()) {
			resizeButton.setVisible(!isGameRunning);
		} else {
			resizeButton.setVisible(false);
		}
		statusBar.revalidate();
		statusBar.repaint();
	}

	protected void updateDevice() {
		devicePanel.init();
		DeviceDisplayImpl deviceDisplay = (DeviceDisplayImpl) DeviceFactory.getDevice().getDeviceDisplay();
		if (deviceDisplay.isResizable()) {
			setResizable(true);
		} else {
			setResizable(false);
		}
		updateResizeButtonVisibility();

		pack();

		if (deviceDisplay.isResizable()) {
			setDeviceSize(deviceDisplay, devicePanel.getWidth(), devicePanel.getHeight());
		}

		devicePanel.requestFocus();
	}

	public static void applyTheme(String theme) {
		applyTheme(theme, null);
	}

	public static void applyTheme(String theme, SwingDeviceComponent devicePanel) {
		try {
			System.setProperty("flatlaf.useWindowDecorations", "true");
			System.setProperty("flatlaf.useSystemFileChooser", "false");
			if ("FlatLaf Light".equals(theme)) {
				com.formdev.flatlaf.FlatLightLaf.setup();
			} else if ("FlatLaf Dark".equals(theme)) {
				com.formdev.flatlaf.FlatDarkLaf.setup();
			} else if ("FlatLaf IntelliJ".equals(theme)) {
				com.formdev.flatlaf.FlatIntelliJLaf.setup();
			} else if ("FlatLaf Dracula".equals(theme)) {
				com.formdev.flatlaf.FlatDarculaLaf.setup();
			} else if ("FlatLaf macOS Light".equals(theme)) {
				com.formdev.flatlaf.themes.FlatMacLightLaf.setup();
			} else if ("FlatLaf macOS Dark".equals(theme)) {
				com.formdev.flatlaf.themes.FlatMacDarkLaf.setup();
			} else {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			com.formdev.flatlaf.FlatLaf.updateUI();
			if (devicePanel != null) {
				try {
					SwingDisplayComponent sdc = (SwingDisplayComponent) devicePanel.getDisplayComponent();
					if (sdc != null) {
						Device device = DeviceFactory.getDevice();
						if (device != null) {
							int w = device.getDeviceDisplay().getFullWidth();
							int h = device.getDeviceDisplay().getFullHeight();
							sdc.repaintRequest(0, 0, w, h);
						}
					}
				} catch (Throwable t) {
					// ignore
				}
			}
		} catch (Exception ex) {
			Logger.error(ex);
		}
	}

	public static void main(String args[]) {
		List params = new ArrayList();
		StringBuffer debugArgs = new StringBuffer();
		for (int i = 0; i < args.length; i++) {
			params.add(args[i]);
			if (debugArgs.length() != 0) {
				debugArgs.append(", ");
			}
			debugArgs.append("[").append(args[i]).append("]");
		}
		if (params.contains("--headless")) {
			Headless.main(args);
			return;
		}

		applyTheme(Config.preLoadTheme());

		final Main app = new Main();
		if (args.length > 0) {
			Logger.debug("arguments", debugArgs.toString());
		}
		
		DeviceEntry devEntry = Config.getDefaultDeviceEntry();
		app.deviceEntry = devEntry;
		if (app.common.initParams(params, devEntry, J2SEDevice.class)) {
			DeviceDisplayImpl deviceDisplay = (DeviceDisplayImpl) DeviceFactory.getDevice().getDeviceDisplay();
			if (deviceDisplay.isResizable()) {
				Rectangle size = Config.getDeviceEntryDisplaySize(app.deviceEntry);
				if (size != null) {
					app.setDeviceSize(deviceDisplay, size.width, size.height);
				} else {
					app.setDeviceSize(deviceDisplay, deviceDisplay.getFullWidth(), deviceDisplay.getFullHeight());
				}
			}
		}
		app.updateThemeSelection();
		app.updateFilterSelection();
		app.updateDevice();

		app.validate();
		app.setVisible(true);

		if (Config.isWindowOnStart("logConsole")) {
			app.menuLogConsoleListener.actionPerformed(null);
			app.menuLogConsole.setSelected(true);
		}
		if (Config.isWindowOnStart("recordStoreManager")) {
			app.menuRecordStoreManagerListener.actionPerformed(null);
			app.menuRecordStoreManager.setSelected(true);
		}

		String midletString;
		try {
			midletString = (String) params.iterator().next();
		} catch (NoSuchElementException ex) {
			midletString = null;
		}
		app.common.initMIDlet(true);

		app.addComponentListener(app.componentListener);

		app.responseInterfaceListener.stateChanged(true);
	}

	private java.util.List getCachedOrScannedGames(String dirPath) {
		java.util.List list = new java.util.ArrayList();
		File dir = new File(dirPath);
		if (!dir.exists() || !dir.isDirectory()) {
			return list;
		}

		File[] files = dir.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				if (file.isFile() && file.getName().toLowerCase().endsWith(".jar")) {
					SwingLibraryExplorerDialog.GameInfo info = SwingLibraryExplorerDialog.getOrScanGame(file);
					list.add(info);
				}
			}
		}
		return list;
	}

	private abstract class CountTimerTask extends TimerTask {

		protected int counter;

		public CountTimerTask(int counter) {
			this.counter = counter;
		}

	}

}
