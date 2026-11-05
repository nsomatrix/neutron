package org.neutron.app.classloader;

import org.junit.Assert;
import org.junit.Test;

public class MIDletClassLoaderTest {

	@Test
	public void testClassLoadByParentExtensions() {
		MIDletClassLoader loader = new MIDletClassLoader(getClass().getClassLoader());
		Assert.assertTrue(loader.classLoadByParent("javax.microedition.lcdui.Display"));
		Assert.assertTrue(loader.classLoadByParent("com.nokia.mid.ui.DirectGraphics"));
		Assert.assertTrue(loader.classLoadByParent("com.mascotcapsule.micro3d.v3.Light"));
		Assert.assertTrue(loader.classLoadByParent("com.siemens.mp.game.Sound"));
		Assert.assertFalse(loader.classLoadByParent("com.fishlabs.game.Main"));
		Assert.assertFalse(loader.classLoadByParent("GOFMIDlet"));
	}
}
