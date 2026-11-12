package org.neutron.jsr211;

import org.junit.Assert;
import org.junit.Test;

import javax.microedition.content.Invocation;
import javax.microedition.content.Registry;

public class JSR211Test {
    @Test
    public void testRegistryInvoke() throws Exception {
        Registry registry = Registry.getRegistry(null);
        Assert.assertNotNull(registry);

        Invocation invocation = new Invocation("http://localhost", "text/html");
        Assert.assertEquals("http://localhost", invocation.getURL());
        Assert.assertEquals("text/html", invocation.getType());

        boolean success = Registry.invoke(invocation);
        Assert.assertTrue(success);
        Assert.assertEquals(Invocation.OK, invocation.getStatus());
    }
}

