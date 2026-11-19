/*
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

package org.neutron.app.ui.noui;

import javax.microedition.lcdui.Displayable;

import org.neutron.DisplayAccess;
import org.neutron.DisplayComponent;
import org.neutron.MIDletAccess;
import org.neutron.MIDletBridge;
import org.neutron.app.ui.DisplayRepaintListener;
import org.neutron.device.Device;
import org.neutron.device.DeviceFactory;
import org.neutron.device.j2se.J2SEDeviceDisplay;
import org.neutron.device.j2se.J2SEGraphicsSurface;

public class NoUiDisplayComponent implements DisplayComponent {

	private static volatile int totalFrames = 0;
	private static volatile int nonZeroFrames = 0;
	private static volatile int nonZeroPixelsLastFrame = 0;
	private static boolean shutdownHookRegistered = false;

	private J2SEGraphicsSurface graphicsSurface;

	private DisplayRepaintListener displayRepaintListener;

	public NoUiDisplayComponent() {
		synchronized (NoUiDisplayComponent.class) {
			if (!shutdownHookRegistered) {
				shutdownHookRegistered = true;
				Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
					@Override
					public void run() {
						org.neutron.log.Logger.info("Headless summary: total frames=" + totalFrames + 
								", non-zero frames=" + nonZeroFrames + 
								", last non-zero pixels=" + nonZeroPixelsLastFrame);
						String dumpPath = System.getProperty("neutron.headless.dump_frame");
						if (dumpPath != null && graphicsSurface != null && graphicsSurface.getImage() != null) {
							try {
								javax.imageio.ImageIO.write(graphicsSurface.getImage(), "PNG", new java.io.File(dumpPath));
								org.neutron.log.Logger.info("Headless dumped final frame to: " + dumpPath);
							} catch (Exception e) {
								org.neutron.log.Logger.error("Failed to dump headless frame to " + dumpPath, e);
							}
						}
					}
				}, "Headless-Summary-Hook"));
			}
		}
	}

	public static int getTotalFrames() {
		return totalFrames;
	}

	public static int getNonZeroFrames() {
		return nonZeroFrames;
	}

	public static int getNonZeroPixelsLastFrame() {
		return nonZeroPixelsLastFrame;
	}
	
	public void addDisplayRepaintListener(DisplayRepaintListener l) {
		displayRepaintListener = l;
	}

	public void removeDisplayRepaintListener(DisplayRepaintListener l) {
		if (displayRepaintListener == l) {
			displayRepaintListener = null;
		}
	}

	public void repaintRequest(int x, int y, int width, int height) 
	{
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
			if (graphicsSurface == null) {
				graphicsSurface = new J2SEGraphicsSurface(
						device.getDeviceDisplay().getFullWidth(), device.getDeviceDisplay().getFullHeight(), false, 0x000000);
			}
					
			J2SEDeviceDisplay deviceDisplay = (J2SEDeviceDisplay) device.getDeviceDisplay();
			synchronized (graphicsSurface) {
				deviceDisplay.paintDisplayable(graphicsSurface, x, y, width, height);
				if (!deviceDisplay.isFullScreenMode()) {
					deviceDisplay.paintControls(graphicsSurface.getGraphics());
				}
			}

			fireDisplayRepaint(graphicsSurface);
		}	
	}


	public J2SEGraphicsSurface getGraphicsSurface() {
		if (graphicsSurface == null) {
			Device device = DeviceFactory.getDevice();
			if (device != null) {
				graphicsSurface = new J2SEGraphicsSurface(
						device.getDeviceDisplay().getFullWidth(), device.getDeviceDisplay().getFullHeight(), false, 0x000000);
			}
		}
		return graphicsSurface;
	}

	public void fireDisplayRepaint(J2SEGraphicsSurface graphicsSurface)
	{
		totalFrames++;
		if (graphicsSurface != null) {
			int[] pixels = graphicsSurface.getImageData();
			if (pixels != null) {
				int nonZero = 0;
				for (int i = 0; i < pixels.length; i++) {
					if ((pixels[i] & 0x00FFFFFF) != 0) {
						nonZero++;
					}
				}
				nonZeroPixelsLastFrame = nonZero;
				if (nonZero > 0) {
					nonZeroFrames++;
				}
			}
		}

		if (displayRepaintListener != null) {
			displayRepaintListener.repaintInvoked(graphicsSurface);
		}
	}

}
