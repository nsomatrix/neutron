package org.neutron.app.ui.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;
import org.neutron.device.Device;
import org.neutron.device.DeviceFactory;
import org.neutron.app.Config;
import org.neutron.device.j2se.J2SEInputMethod;

public class SwingAutoClicker {

	private static boolean enabled = false;
	private static final List targets = new ArrayList();
	private static int currentTargetIndex = 0;
	private static int intervalMs = 1000;
	private static Timer timer = null;
	private static boolean setupModeActive = false;

	public static void init() {
		enabled = Config.isAutoClickerEnabled();
		intervalMs = Config.getAutoClickerInterval();
		
		// Load targets
		targets.clear();
		String serializedTargets = Config.getAutoClickerTargets();
		if (serializedTargets != null && serializedTargets.trim().length() > 0) {
			deserializeTargets(serializedTargets);
		} else {
			// Fallback to legacy single coordinate fields if they exist
			int clickX = Config.getAutoClickerX();
			int clickY = Config.getAutoClickerY();
			if (clickX > 0 || clickY > 0) {
				targets.add(new Point(clickX, clickY));
			}
		}
		
		currentTargetIndex = 0;
		updateTimer();
	}

	public static boolean isEnabled() {
		return enabled;
	}

	public static void setEnabled(boolean enabled) {
		SwingAutoClicker.enabled = enabled;
		Config.setAutoClickerEnabled(enabled);
		currentTargetIndex = 0;
		updateTimer();
	}

	public static List getTargets() {
		return targets;
	}

	public static void addCoordinate(int x, int y) {
		targets.add(new Point(x, y));
		saveTargets();
	}

	public static void clearCoordinates() {
		targets.clear();
		currentTargetIndex = 0;
		saveTargets();
	}

	public static void setCoordinates(int x, int y) {
		// Used when Ctrl+Clicking on the screen. Adds to the sequence!
		addCoordinate(x, y);
	}

	public static int getIntervalMs() {
		return intervalMs;
	}

	public static void setIntervalMs(int ms) {
		if (ms < 50) ms = 50; // Clamp to min 50ms to prevent CPU overload
		intervalMs = ms;
		Config.setAutoClickerInterval(ms);
		updateTimer();
	}

	public static boolean isSetupModeActive() {
		return setupModeActive;
	}

	public static void setSetupModeActive(boolean active) {
		setupModeActive = active;
	}

	private static synchronized void updateTimer() {
		if (timer != null) {
			timer.stop();
			timer = null;
		}
		if (enabled && intervalMs > 0 && targets.size() > 0) {
			timer = new Timer(intervalMs, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					performClick();
				}
			});
			timer.start();
		}
	}

	private static void performClick() {
		try {
			if (targets.isEmpty()) {
				return;
			}
			
			Device device = DeviceFactory.getDevice();
			if (device != null && device.hasPointerEvents()) {
				final J2SEInputMethod inputMethod = (J2SEInputMethod) device.getInputMethod();
				if (inputMethod != null) {
					// Fetch next target in sequence
					if (currentTargetIndex >= targets.size()) {
						currentTargetIndex = 0;
					}
					Point target = (Point) targets.get(currentTargetIndex);
					final int targetX = target.x;
					final int targetY = target.y;
					
					// Move index forward
					currentTargetIndex = (currentTargetIndex + 1) % targets.size();
					
					// Send pressed event
					inputMethod.pointerPressed(targetX, targetY);
					
					// Schedule released event after hold time
					int holdTime = Math.min(100, intervalMs / 2);
					Timer releaseTimer = new Timer(holdTime, new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							try {
								inputMethod.pointerReleased(targetX, targetY);
							} catch (Exception ex) {}
						}
					});
					releaseTimer.setRepeats(false);
					releaseTimer.start();
				}
			}
		} catch (Exception ex) {
			// Fail-safe protection
		}
	}

	private static void saveTargets() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < targets.size(); i++) {
			Point p = (Point) targets.get(i);
			sb.append(p.x).append(",").append(p.y);
			if (i < targets.size() - 1) {
				sb.append(";");
			}
		}
		Config.setAutoClickerTargets(sb.toString());
		// Update legacy coordinates to first target for compatibility
		if (!targets.isEmpty()) {
			Point p = (Point) targets.get(0);
			Config.setAutoClickerX(p.x);
			Config.setAutoClickerY(p.y);
		}
		updateTimer();
	}

	private static void deserializeTargets(String serialized) {
		try {
			String[] parts = serialized.split(";");
			for (int i = 0; i < parts.length; i++) {
				String part = parts[i];
				if (part.trim().length() == 0) continue;
				String[] coords = part.split(",");
				if (coords.length == 2) {
					int x = Integer.parseInt(coords[0].trim());
					int y = Integer.parseInt(coords[1].trim());
					targets.add(new Point(x, y));
				}
			}
		} catch (Exception e) {
			// Ignore malformed strings
		}
	}

	public static void drawOverlay(Graphics2D g2d, SwingDisplayComponent displayComp) {
		if (!enabled || targets.isEmpty()) {
			return;
		}

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setStroke(new BasicStroke(2f));
		g2d.setFont(new Font("Arial", Font.BOLD, 12));

		Point lastCompPt = null;
		Point firstCompPt = null;

		// 1. Draw connecting lines between sequence targets
		g2d.setColor(new Color(255, 60, 60, 100)); // Faded red/orange line path
		for (int i = 0; i < targets.size(); i++) {
			Point t = (Point) targets.get(i);
			Point cp = displayComp.componentCoordinate(t.x, t.y);
			if (i == 0) {
				firstCompPt = cp;
			} else {
				g2d.drawLine(lastCompPt.x, lastCompPt.y, cp.x, cp.y);
			}
			lastCompPt = cp;
		}
		// Connect the loop back from last target to first target
		if (targets.size() > 2 && lastCompPt != null && firstCompPt != null) {
			g2d.drawLine(lastCompPt.x, lastCompPt.y, firstCompPt.x, firstCompPt.y);
		}

		// 2. Draw target indicators & numbers
		for (int i = 0; i < targets.size(); i++) {
			Point t = (Point) targets.get(i);
			Point cp = displayComp.componentCoordinate(t.x, t.y);

			// Highlight the next target to be clicked in yellow/gold, others in red
			boolean isNextTarget = (i == currentTargetIndex);
			if (isNextTarget) {
				g2d.setColor(new Color(255, 215, 0, 220)); // Gold
			} else {
				g2d.setColor(new Color(255, 60, 60, 200)); // Red
			}

			// Draw target ring
			g2d.drawOval(cp.x - 10, cp.y - 10, 20, 20);
			g2d.drawLine(cp.x - 14, cp.y, cp.x + 14, cp.y);
			g2d.drawLine(cp.x, cp.y - 14, cp.x, cp.y + 14);

			// Draw center dot
			g2d.fillOval(cp.x - 2, cp.y - 2, 4, 4);

			// Draw sequence index number
			g2d.setColor(Color.WHITE);
			g2d.drawString(String.valueOf(i + 1), cp.x + 12, cp.y - 10);
		}
	}
}
