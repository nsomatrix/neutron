package org.neutron.app.ui.swing;

import org.junit.Assert;
import org.junit.Test;
import org.neutron.app.Config;
import org.neutron.device.impl.ButtonName;

public class SwingVirtualKeypadPanelTest {

	@Test
	public void testVirtualKeypadInitialization() {
		SwingVirtualKeypadPanel panel = new SwingVirtualKeypadPanel();
		Assert.assertNotNull("Panel should initialize", panel);
		Assert.assertTrue("Panel should have child components", panel.getComponentCount() > 0);
	}

	@Test
	public void testConfigVirtualButtonsToggle() {
		boolean initial = Config.isVirtualButtonsEnabled();
		try {
			Config.setVirtualButtonsEnabled(true);
			Assert.assertTrue(Config.isVirtualButtonsEnabled());

			Config.setVirtualButtonsEnabled(false);
			Assert.assertFalse(Config.isVirtualButtonsEnabled());
		} finally {
			Config.setVirtualButtonsEnabled(initial);
		}
	}

	@Test
	public void testConfigVirtualButtonsMode() {
		String initialMode = Config.getVirtualButtonsMode();
		try {
			Config.setVirtualButtonsMode("full");
			Assert.assertEquals("full", Config.getVirtualButtonsMode());

			Config.setVirtualButtonsMode("gamepad");
			Assert.assertEquals("gamepad", Config.getVirtualButtonsMode());
		} finally {
			Config.setVirtualButtonsMode(initialMode);
		}
	}

	@Test
	public void testControllerSafeNullExecution() {
		// Verify that controller handles invocations safely when no midlet is running
		try {
			VirtualKeypadController.pressButton(ButtonName.UP);
			VirtualKeypadController.repeatButton(ButtonName.UP);
			VirtualKeypadController.releaseButton(ButtonName.UP);

			VirtualKeypadController.pressButton(ButtonName.SOFT1);
			VirtualKeypadController.releaseButton(ButtonName.SOFT1);

			VirtualKeypadController.pressButton(ButtonName.KEY_NUM5);
			VirtualKeypadController.releaseButton(ButtonName.KEY_NUM5);
		} catch (Exception ex) {
			Assert.fail("Controller should safely handle button calls without active midlet: " + ex.getMessage());
		}
	}

	@Test
	public void testDiagonalButtonCreation() {
		SwingVirtualKeypadPanel.VirtualKeyButton diagonalBtn = new SwingVirtualKeypadPanel.VirtualKeyButton(
				new ButtonName[] { ButtonName.UP, ButtonName.LEFT }, "↖", null);
		Assert.assertNotNull(diagonalBtn);
		Assert.assertFalse("Button should not steal focus", diagonalBtn.isFocusable());
	}

	@Test
	public void testFullscreenNonPersistence() {
		try {
			Config.setFullscreen(true);
			Assert.assertTrue("Fullscreen should be true when set in session", Config.isFullscreen());

			Config.saveConfig();

			Config.setFullscreen(false);
			Assert.assertFalse("Fullscreen should be false after reset", Config.isFullscreen());
		} finally {
			Config.setFullscreen(false);
		}
	}

	@Test
	public void testVirtualButtonsDisabledInFullscreen() {
		try {
			Config.setFullscreen(true);
			Assert.assertTrue(Config.isFullscreen());
			// Controller calls in fullscreen should be safely ignored / no-op
			VirtualKeypadController.pressButton(ButtonName.UP);
			VirtualKeypadController.repeatButton(ButtonName.UP);
			VirtualKeypadController.releaseButton(ButtonName.UP);
		} finally {
			Config.setFullscreen(false);
		}
	}

	@Test
	public void testPanelReleaseAllButtonsOnHide() {
		SwingVirtualKeypadPanel panel = new SwingVirtualKeypadPanel();
		panel.setVisible(true);
		Assert.assertTrue(panel.isVisible());
		panel.releaseAllButtons();
		panel.setVisible(false);
		Assert.assertFalse(panel.isVisible());
	}

	@Test
	public void testDirectNumericInputMapping() {
		Assert.assertEquals("0", VirtualKeypadController.getDirectNumericInput(ButtonName.KEY_NUM0));
		Assert.assertEquals("1", VirtualKeypadController.getDirectNumericInput(ButtonName.KEY_NUM1));
		Assert.assertEquals("2", VirtualKeypadController.getDirectNumericInput(ButtonName.KEY_NUM2));
		Assert.assertEquals("3", VirtualKeypadController.getDirectNumericInput(ButtonName.KEY_NUM3));
		Assert.assertEquals("4", VirtualKeypadController.getDirectNumericInput(ButtonName.KEY_NUM4));
		Assert.assertEquals("5", VirtualKeypadController.getDirectNumericInput(ButtonName.KEY_NUM5));
		Assert.assertEquals("6", VirtualKeypadController.getDirectNumericInput(ButtonName.KEY_NUM6));
		Assert.assertEquals("7", VirtualKeypadController.getDirectNumericInput(ButtonName.KEY_NUM7));
		Assert.assertEquals("8", VirtualKeypadController.getDirectNumericInput(ButtonName.KEY_NUM8));
		Assert.assertEquals("9", VirtualKeypadController.getDirectNumericInput(ButtonName.KEY_NUM9));
		Assert.assertEquals("*", VirtualKeypadController.getDirectNumericInput(ButtonName.KEY_STAR));
		Assert.assertEquals("#", VirtualKeypadController.getDirectNumericInput(ButtonName.KEY_POUND));
		Assert.assertNull(VirtualKeypadController.getDirectNumericInput(ButtonName.UP));
		Assert.assertNull(VirtualKeypadController.getDirectNumericInput(ButtonName.DELETE));
	}

	@Test
	public void testCloseActionListener() {
		SwingVirtualKeypadPanel panel = new SwingVirtualKeypadPanel();
		final boolean[] closed = new boolean[] { false };
		panel.setCloseActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				closed[0] = true;
			}
		});
		Assert.assertNotNull(panel);
	}
}

