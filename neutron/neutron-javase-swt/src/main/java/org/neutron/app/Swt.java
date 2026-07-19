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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.neutron.DisplayComponent;
import org.neutron.device.EmulatorContext;
import org.neutron.MIDletBridge;
import org.neutron.app.ui.Message;
import org.neutron.app.ui.ResponseInterfaceListener;
import org.neutron.app.ui.StatusBarListener;
import org.neutron.app.ui.swt.SwtDeviceComponent;
import org.neutron.app.ui.swt.SwtDialog;
import org.neutron.app.ui.swt.SwtErrorMessageDialogPanel;
import org.neutron.app.ui.swt.SwtInputDialog;
import org.neutron.app.ui.swt.SwtMessageDialog;
import org.neutron.app.ui.swt.SwtSelectDeviceDialog;
import org.neutron.app.util.DeviceEntry;
import org.neutron.app.util.IOUtils;
import org.neutron.device.Device;
import org.neutron.device.DeviceDisplay;
import org.neutron.device.DeviceFactory;
import org.neutron.device.FontManager;
import org.neutron.device.InputMethod;
import org.neutron.device.impl.DeviceImpl;
import org.neutron.device.impl.Rectangle;
import org.neutron.device.swt.SwtDevice;
import org.neutron.device.swt.SwtDeviceDisplay;
import org.neutron.device.swt.SwtFontManager;
import org.neutron.device.swt.SwtInputMethod;

public class Swt extends Common {
	public static Shell shell;

	protected static SwtDeviceComponent devicePanel;

	protected MenuItem menuOpenJADFile;

	protected MenuItem menuOpenJADURL;

	private SwtSelectDeviceDialog selectDeviceDialog;

	private FileDialog fileDialog = null;

	private MenuItem menuSelectDevice;

	private DeviceEntry deviceEntry;

	private Label statusBar;

	private KeyListener keyListener = new KeyListener() {
		public void keyTyped(KeyEvent e) {
		}

		public void keyPressed(KeyEvent e) {
			// devicePanel.keyPressed(e);
		}

		public void keyReleased(KeyEvent e) {
			// devicePanel.keyReleased(e);
		}
	};

	protected Listener menuOpenMIDletFileListener = new Listener() {
		public void handleEvent(Event ev) {
			if (fileDialog == null) {
				fileDialog = new FileDialog(shell, SWT.OPEN);
				fileDialog.setText("Open MIDlet File...");
				fileDialog.setFilterNames(new String[] { "MIDlet files" });
				fileDialog.setFilterExtensions(new String[] { "*.ja[dr]" });
				fileDialog.setFilterPath(Config.getRecentDirectory("recentJadDirectory"));
			}

			fileDialog.open();

			if (fileDialog.getFileName().length() > 0) {
				File selectedFile;
				if (fileDialog.getFilterPath() == null) {
					selectedFile = new File(fileDialog.getFileName());
				} else {
					selectedFile = new File(fileDialog.getFilterPath(), fileDialog.getFileName());
					Config.setRecentDirectory("recentJadDirectory", fileDialog.getFilterPath());
				}
				String url = IOUtils.getCanonicalFileURL(selectedFile);
				Common.openMIDletUrlSafe(url);
			}
		}
	};

	protected Listener menuOpenMIDletURLListener = new Listener() {
		public void handleEvent(Event ev) {
			// TODO change to JadUrlPanel
			SwtInputDialog inputDialog = new SwtInputDialog(shell, "Open...", "Enter MIDlet URL:");
			if (inputDialog.open() == SwtDialog.OK) {
				try {
					openMIDletUrl(inputDialog.getValue());
				} catch (IOException ex) {
					System.err.println("Cannot load " + inputDialog.getValue());
				}
			}
		}
	};

	protected Listener menuExitListener = new Listener() {
		public void handleEvent(Event e) {
			Config.setWindow("main", new Rectangle(shell.getLocation().x, shell.getLocation().y, shell.getSize().x,
					shell.getSize().y), true);

			System.exit(0);
		}
	};

	private Listener menuSelectDeviceListener = new Listener() {
		public void handleEvent(Event e) {
			if (selectDeviceDialog.open() == SwtDialog.OK) {
				if (selectDeviceDialog.getSelectedDeviceEntry().equals(getDevice())) {
					return;
				}
				if (MIDletBridge.getCurrentMIDlet() != getLauncher()) {
					if (!SwtMessageDialog
							.openQuestion(shell, "Question?",
									"Changing device needs MIDlet to be restarted. All MIDlet data will be lost. Are you sure?")) {
						return;
					}
				}
				setDevice(selectDeviceDialog.getSelectedDeviceEntry());

				if (MIDletBridge.getCurrentMIDlet() != getLauncher()) {
					try {
						initMIDlet(true);
					} catch (Exception ex) {
						System.err.println(ex);
					}
				} else {
					startLauncher(MIDletBridge.getMIDletContext());
				}
			}
		}
	};

	private StatusBarListener statusBarListener = new StatusBarListener() {
		public void statusBarChanged(final String text) {
			shell.getDisplay().asyncExec(new Runnable() {
				public void run() {
					statusBar.setText(text);
				}
			});
		}
	};

	private ResponseInterfaceListener responseInterfaceListener = new ResponseInterfaceListener() {
		public void stateChanged(final boolean state) {
			shell.getDisplay().asyncExec(new Runnable() {
				public void run() {
					menuOpenJADFile.setEnabled(state);
					menuOpenJADURL.setEnabled(state);
					menuSelectDevice.setEnabled(state);
				}
			});
		}
	};

	/*
	 * WindowAdapter windowListener = new WindowAdapter() { public void
	 * windowClosing(WindowEvent ev) { menuExitListener.actionPerformed(null); }
	 * 
	 * 
	 * public void windowIconified(WindowEvent ev) {
	 * MIDletBridge.getMIDletAccess
	 * (common.getLauncher().getCurrentMIDlet()).pauseApp(); }
	 * 
	 * public void windowDeiconified(WindowEvent ev) { try {
	 * MIDletBridge.getMIDletAccess
	 * (common.getLauncher().getCurrentMIDlet()).startApp(); } catch
	 * (MIDletStateChangeException ex) { System.err.println(ex); } } };
	 */

	protected Swt(Shell shell) {
		this(shell, null);
	}

	protected Swt(Shell shell, DeviceEntry defaultDevice) {
		super(new EmulatorContext() {
			private InputMethod inputMethod = new SwtInputMethod();

			private DeviceDisplay deviceDisplay = new SwtDeviceDisplay(this);

			private FontManager fontManager = new SwtFontManager();

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

			public InputStream getResourceAsStream(String name) {
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
							if (shell != null && !shell.isDisposed()) {
								shell.getDisplay().asyncExec(new Runnable() {
									public void run() {
										Message.info("MIDlet requests that the device handle the following URL: " + URL);
									}
								});
							} else {
								org.neutron.log.Logger.info("MIDlet requested URL (fallback): " + URL);
							}
						} else {
							org.neutron.log.Logger.info("MIDlet requested and opened URL: " + URL);
						}
					}
				}).start();

				return false;
			}

			@Override
			public InputStream getResourceAsStream(Class origClass, String name) {
				// TODO Auto-generated method stub
				return null;
			}
		});

		initInterface(shell);

		// addWindowListener(windowListener);

		Config.loadConfig(null, emulatorContext);
		loadImplementationsFromConfig();

		Rectangle window = Config.getWindow("main", new Rectangle(0, 0, 160, 120));
		shell.setLocation(window.x, window.y);

		shell.addKeyListener(keyListener);

		selectDeviceDialog = new SwtSelectDeviceDialog(shell, emulatorContext);

		setStatusBarListener(statusBarListener);
		setResponseInterfaceListener(responseInterfaceListener);

		Message.addListener(new SwtErrorMessageDialogPanel(shell));
	}

	protected void initInterface(Shell shell) {
		GridLayout layout = new GridLayout(1, false);
		shell.setLayout(layout);
		shell.setLayoutData(new GridData(GridData.FILL_BOTH));

		Menu bar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(bar);

		MenuItem menuFile = new MenuItem(bar, SWT.CASCADE);
		menuFile.setText("Run");

		Menu fileSubmenu = new Menu(shell, SWT.DROP_DOWN);
		menuFile.setMenu(fileSubmenu);

		menuOpenJADFile = new MenuItem(fileSubmenu, SWT.PUSH);
		menuOpenJADFile.setText("Run JAR File");
		menuOpenJADFile.addListener(SWT.Selection, menuOpenMIDletFileListener);

		menuOpenJADURL = new MenuItem(fileSubmenu, 0);
		menuOpenJADURL.setText("Run from URL");
		menuOpenJADURL.addListener(SWT.Selection, menuOpenMIDletURLListener);

		new MenuItem(fileSubmenu, SWT.SEPARATOR);

		MenuItem menuExit = new MenuItem(fileSubmenu, SWT.PUSH);
		menuExit.setText("Exit");
		menuExit.addListener(SWT.Selection, menuExitListener);

		MenuItem menuOptions = new MenuItem(bar, SWT.CASCADE);
		menuOptions.setText("Config");

		Menu optionsSubmenu = new Menu(shell, SWT.DROP_DOWN);
		menuOptions.setMenu(optionsSubmenu);

		menuSelectDevice = new MenuItem(optionsSubmenu, SWT.PUSH);
		menuSelectDevice.setText("Native Device");
		menuSelectDevice.addListener(SWT.Selection, menuSelectDeviceListener);

		shell.setText("Neutron");

		devicePanel = new SwtDeviceComponent(shell);
		devicePanel.setLayoutData(new GridData(GridData.FILL_BOTH));

		statusBar = new Label(shell, SWT.HORIZONTAL);
		statusBar.setText("Status");
		statusBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	public void setDevice(DeviceEntry entry) {
		if (DeviceFactory.getDevice() != null) {
			// ((SwtDevice) DeviceFactory.getDevice()).dispose();
		}

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
					SwtDevice.class);
			this.deviceEntry = entry;
			setDevice(device);
			updateDevice();
		} catch (MalformedURLException ex) {
			System.err.println(ex);
		} catch (IOException ex) {
			System.err.println(ex);
		}
	}

	protected void updateDevice() {
		shell.setSize(shell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true));
	}

	public static void main(String args[]) {
		Display display = new Display();
		shell = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.MIN);

		List params = new ArrayList();
		for (int i = 0; i < args.length; i++) {
			params.add(args[i]);
		}

		Swt app = new Swt(shell);
		app.initParams(params, app.selectDeviceDialog.getSelectedDeviceEntry(), SwtDevice.class);
		app.updateDevice();

		shell.pack();
		shell.open();

		String midletString;
		try {
			midletString = (String) params.iterator().next();
		} catch (NoSuchElementException ex) {
			midletString = null;
		}
		app.initMIDlet(false);

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

}
