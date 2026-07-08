/**
 *  Neutron
 *  Copyright (C) 2008 Markus Heberling <markus@heberling.net>
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
package org.neutron.iphone.device;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;

import joc.Scope;

import org.neutron.device.Device;
import org.neutron.device.DeviceDisplay;
import org.neutron.device.FontManager;
import org.neutron.device.InputMethod;
import org.neutron.device.ui.AlertUI;
import org.neutron.device.ui.CanvasUI;
import org.neutron.device.ui.CommandUI;
import org.neutron.device.ui.EventDispatcher;
import org.neutron.device.ui.FormUI;
import org.neutron.device.ui.ListUI;
import org.neutron.device.ui.TextBoxUI;
import org.neutron.device.ui.UIFactory;
import org.neutron.iphone.Neutron;
import org.neutron.iphone.ThreadDispatcher;
import org.neutron.iphone.device.ui.IPhoneAlertUI;
import org.neutron.iphone.device.ui.IPhoneCanvasUI;
import org.neutron.iphone.device.ui.IPhoneCommandUI;
import org.neutron.iphone.device.ui.IPhoneFormUI;
import org.neutron.iphone.device.ui.IPhoneListUI;
import org.neutron.iphone.device.ui.IPhoneTextBoxUI;

public class IPhoneDevice implements Device {
	private UIFactory ui = new UIFactory() {

		public EventDispatcher createEventDispatcher(Display display) {
			final EventDispatcher eventDispatcher = new EventDispatcher() {

				@Override
				public void run() {
					Scope scope = new Scope();
					try {
						super.run();
					} finally {
						scope.close();
					}
				}

				@Override
				protected void post(Event event) {
					ThreadDispatcher.dispatchOnMainThread(event, false);
				}

			};

			Thread thread = new Thread(eventDispatcher, EventDispatcher.EVENT_DISPATCHER_NAME);
			thread.setDaemon(true);
			thread.start();

			return eventDispatcher;
		}

		public AlertUI createAlertUI(Alert alert) {
			return new IPhoneAlertUI(neutronlator, alert);
		}

		public CanvasUI createCanvasUI(Canvas canvas) {
			return new IPhoneCanvasUI(neutronlator, canvas);
		}

		public FormUI createFormUI(Form form) {
			return new IPhoneFormUI(neutronlator, form);
		}

		public ListUI createListUI(List list) {
			return new IPhoneListUI(neutronlator, list);
		}

		public TextBoxUI createTextBoxUI(TextBox textBox) {
			return new IPhoneTextBoxUI(neutronlator, textBox);
		}
		public CommandUI createCommandUI(Command command) {
			return new IPhoneCommandUI(neutronlator, command);
		}

	};

	private Neutron neutronlator;

	private Map systemProperties = new HashMap();

	private Vector softButtons = new Vector();

	public IPhoneDevice(Neutron neutronlator) {
		this.neutronlator = neutronlator;
	}

	public void destroy() {
		// TODO Auto-generated method stub

	}

	public Vector getButtons() {
		// TODO Auto-generated method stub
		return null;
	}

	public DeviceDisplay getDeviceDisplay() {
		return neutronlator.getDeviceDisplay();
	}

	public FontManager getFontManager() {
		return neutronlator.getFontManager();
	}

	public InputMethod getInputMethod() {
		return neutronlator.getInputMethod();
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public Image getNormalImage() {
		// TODO Auto-generated method stub
		return null;
	}

	public Image getOverImage() {
		// TODO Auto-generated method stub
		return null;
	}

	public Image getPressedImage() {
		// TODO Auto-generated method stub
		return null;
	}

	public Vector getSoftButtons() {
		return softButtons;
	}

	public Map getSystemProperties() {
		return systemProperties;
	}

	public UIFactory getUIFactory() {
		return ui;
	}

	public boolean hasPointerEvents() {
		return true;
	}

	public boolean hasPointerMotionEvents() {
		return true;
	}

	public boolean hasRepeatEvents() {
		// TODO Auto-generated method stub
		return false;
	}

	public void init() {
		// TODO Auto-generated method stub

	}

	public boolean vibrate(int duration) {
		// TODO Auto-generated method stub
		return false;
	}

}
