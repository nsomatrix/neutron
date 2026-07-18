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
package org.neutron.iphone.device.ui;

import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import joc.Message;
import joc.Selector;
import obc.NSMutableArray;
import obc.NSObject;
import obc.UIBarButtonItem;
import obc.UIToolbar;

import org.neutron.device.ui.CommandUI;
import org.neutron.device.ui.DisplayableUI;
import org.neutron.iphone.Neutron;
import org.neutron.iphone.ThreadDispatcher;

public abstract class AbstractUI<T extends Displayable> extends NSObject implements DisplayableUI {

	public static final int NAVIGATION_HEIGHT = 40;

	public static final int TOOLBAR_HEIGHT = 40;

	protected Vector<CommandUI> commands = new Vector<CommandUI>();

	protected CommandListener commandListener;

	protected UIToolbar toolbar;

	protected T displayable;

	protected Neutron neutronlator;

	protected AbstractUI(Neutron neutronlator, T displayable) {
		super();
		this.neutronlator = neutronlator;
		this.displayable = displayable;
	}

	public void addCommandUI(CommandUI cmd) {
		commands.add(cmd);
		ThreadDispatcher.dispatchOnMainThread(new Runnable() {
			public void run() {
				updateToolbar();
			}
		}, false);
	}

	protected void updateToolbar() {
		if (toolbar != null) {
			NSMutableArray items = new NSMutableArray().initWithCapacity$(commands.size());
			for (int i = 0; i < commands.size(); i++) {
				CommandUI command = commands.get(i);
				System.out.println(command.getCommand().getLabel());
				items.setObject$atIndex$(new UIBarButtonItem().initWithTitle$style$target$action$(command.getCommand().getLabel(),
						0, new CommandCaller(command.getCommand()), new Selector("call")), i);
			}
			toolbar.setItems$(items);
		}
	}

	public void removeCommandUI(CommandUI cmd) {
		commands.remove(cmd);
		ThreadDispatcher.dispatchOnMainThread(new Runnable() {
			public void run() {
				updateToolbar();
			}
		}, false);
	}

	public void setCommandListener(CommandListener l) {
		commandListener = l;
	}

	class CommandCaller extends NSObject {
		private Command command;

		private CommandCaller(Command command) {
			super();
			this.command = command;
		}

		@Message
		public void call() {
			commandListener.commandAction(command, displayable);
		}
	}
	
	public Vector getCommandsUI() {
		return commands;
	}
}
