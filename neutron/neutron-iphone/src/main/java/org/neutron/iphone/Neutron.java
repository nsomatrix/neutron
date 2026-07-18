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
 *
 *  @version $Id$
 */
package org.neutron.iphone;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import joc.Message;
import obc.CGRect;
import obc.UIApplication;
import obc.UIHardware;
import obc.UIWindow;

import org.neutron.MIDletBridge;
import org.neutron.MIDletContext;
import org.neutron.RecordStoreManager;
import org.neutron.app.CommonInterface;
import org.neutron.app.launcher.Launcher;
import org.neutron.app.util.MIDletResourceLoader;
import org.neutron.device.Device;
import org.neutron.device.DeviceFactory;
import org.neutron.iphone.device.IPhoneDevice;
import org.neutron.iphone.device.IPhoneDeviceDisplay;
import org.neutron.iphone.device.IPhoneFontManager;
import org.neutron.iphone.device.IPhoneInputMethod;
import org.neutron.iphone.device.IPhoneRecordStoreManager;
import org.neutron.iphone.device.ui.AbstractUI;

import com.saurik.uicaboodle.Main;

public class Neutron extends UIApplication {
	private final class IPhoneCommon implements CommonInterface, org.neutron.Neutron {

		private IPhoneLauncher launcher;

		public IPhoneCommon(Device device) {
			DeviceFactory.setDevice(device);

			MIDletBridge.setNeutron(this);
		}

		public void initMIDlet(boolean startMidlet) {
			if (launcher == null)
				launcher = new IPhoneLauncher(this);

			if (launcher.getSelectedMidletEntry() == null) {
				try {
					MIDletBridge.getMIDletAccess(launcher).startApp();
					launcher.setCurrentMIDlet(launcher);
				} catch (MIDletStateChangeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					MIDletResourceLoader.classLoader=launcher.getSelectedMidletEntry().getMIDletClass().getClassLoader();
					MIDlet midlet = (MIDlet) launcher.getSelectedMidletEntry().getMIDletClass().newInstance();
					//set the classloader, so that resource loading works
					MIDletBridge.getMIDletAccess(midlet).startApp();
					launcher.setCurrentMIDlet(midlet);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		public void destroyMIDletContext(MIDletContext midletContext) {
			// TODO Auto-generated method stub

		}

		public String getAppProperty(String key) {
			// TODO Auto-generated method stub
			return null;
		}

		public Launcher getLauncher() {
			return launcher;
		}

		public RecordStoreManager getRecordStoreManager() {
			// TODO Auto-generated method stub
			return new IPhoneRecordStoreManager(Neutron.this);
		}

		public InputStream getResourceAsStream(String name) {
			return MIDletBridge.getCurrentMIDlet().getClass().getResourceAsStream(name);
		}

		public void notifyDestroyed(MIDletContext midletContext) {
			System.out.println("IPhoneCommon.notifyDestroyed()");
			launcher = null;
			initMIDlet(true);
		}

		public int checkPermission(String permission) {
			// TODO

			return 0;
		}

		public boolean platformRequest(String URL) {
			// TODO Auto-generated method stub
			return false;
		}

	}

	private static final class ExceptionHandler implements Thread.UncaughtExceptionHandler {
		public void uncaughtException(Thread arg0, Throwable arg1) {
			System.err.println("Uncaught exception in thread: " + arg0.getName());
			arg1.printStackTrace();
			System.exit(-1);
		}
	}

	public static void main(String[] args) throws Exception {
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
		// Main.main(new String[] { HelloJava.class.getName() });
		Main.main(new String[] { Neutron.class.getName() });
	}

	@Message
	public void applicationDidFinishLaunching$(Object unused) throws Exception {
		CGRect outer = UIHardware.$fullScreenApplicationContentRect();
		window = new UIWindow().initWithContentRect$(outer);

		window.orderFront$(this);
		window.makeKeyAndVisible();

		init(Arrays.asList("--usesystemclassloader"));
	}

	@Message
	public void applicationDidReceiveMemoryWarning$(Object unused) {
		System.out.println("!!!!!!!!!!!MEMORY-WARNING!!!!!!!!!!!!!");
	}
	
	@Override
	@Message
	public void applicationDidResume() {
		System.out.println("Neutron.applicationDidResume()");
		try {
			MIDletBridge.getMIDletContext().getMIDletAccess().startApp();
		} catch (MIDletStateChangeException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	@Message
	public void applicationWillSuspend() {
		System.out.println("Neutron.applicationWillSuspend()");
		MIDletBridge.getMIDletContext().getMIDletAccess().pauseApp();
	}
	
	private IPhoneInputMethod inputMethod = new IPhoneInputMethod();

	private IPhoneDeviceDisplay deviceDisplay;

	private IPhoneFontManager fontManager = new IPhoneFontManager();

	private UIWindow window;
	private static IPhoneCommon common;

	public void init(List<String> params) {
		common = new IPhoneCommon(new IPhoneDevice(this));
		deviceDisplay = new IPhoneDeviceDisplay(common);
		deviceDisplay.displayRectangleWidth = (int) getWindow().bounds().size.width;
		deviceDisplay.displayRectangleHeight = (int) getWindow().bounds().size.height - AbstractUI.TOOLBAR_HEIGHT;

		System.setProperty("microedition.platform", "neutron-iphone");
		System.setProperty("microedition.locale", Locale.getDefault().toString());

		Launcher.setSuiteName("Neutron for iPhone");
		
		// don't know why this is needed...
		ThreadDispatcher.dispatchOnMainThread(new Runnable(){public void run() {
			common.initMIDlet(true);
		}}, false);
	}


	public static CommonInterface getCommon() {
		return common;
	}


	public UIWindow getWindow() {
		return window;
	}

	public IPhoneFontManager getFontManager() {
		return fontManager;
	}
	public IPhoneInputMethod getInputMethod() {
		return inputMethod;
	}
	public IPhoneDeviceDisplay getDeviceDisplay() {
		return deviceDisplay;
	}
}