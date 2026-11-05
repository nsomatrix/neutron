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
}

