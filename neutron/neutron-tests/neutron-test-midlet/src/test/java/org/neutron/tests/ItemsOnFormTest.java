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
package org.neutron.tests;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;

import junit.framework.TestCase;
import org.neutron.device.Device;
import org.neutron.device.DeviceFactory;

public class ItemsOnFormTest extends TestCase {

	public ItemsOnFormTest() {
		
	}
	
	public ItemsOnFormTest(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Device dummyDevice = (Device) createMock(Device.class);
		DeviceFactory.setDevice(dummyDevice);
	}

	private static Object createMock(Class<?> clazz) {
		return Proxy.newProxyInstance(
			clazz.getClassLoader(),
			new Class<?>[] { clazz },
			new InvocationHandler() {
				@Override
				public Object invoke(Object proxy, Method method, Object[] args) {
					Class<?> retType = method.getReturnType();
					if (retType.isInterface()) {
						return createMock(retType);
					}
					if (retType == int.class || retType == Integer.class) {
						if ("getWidth".equals(method.getName())) return 240;
						if ("getHeight".equals(method.getName())) return 320;
						return 10;
					}
					if (retType == boolean.class || retType == Boolean.class) {
						return Boolean.TRUE;
					}
					return null;
				}
			}
		);
	}
	
	public void testAddItems() {
		int step= 0;
		Item[] items= new Item[1];
		items[step]= new StringItem(null, "one");
		
		ItemsOnForm f = new ItemsOnForm(items);
		for (int i = 1 ; i < 15; i++) {
			f.commandAction(ItemsOnForm.addCommand, f);
		}
		assertEquals(15, f.size());
	}
}

