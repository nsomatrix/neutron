package org.neutron.microedition.io;

import javax.microedition.io.Connector;
import org.neutron.microedition.ImplFactory;
import org.junit.Test;
import static org.junit.Assert.*;

public class ConnectorTest {

    @Test
    public void testGetCGFSchemeValid() {
        assertEquals("http", ImplFactory.getCGFScheme("http://example.com"));
        assertEquals("socket", ImplFactory.getCGFScheme("socket://127.0.0.1:8080"));
        assertEquals("file", ImplFactory.getCGFScheme("file:///root/path"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetCGFSchemeNullThrowsIllegalArgument() {
        ImplFactory.getCGFScheme(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetCGFSchemeColonlessThrowsIllegalArgument() {
        ImplFactory.getCGFScheme("httpexamplecom");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConnectorOpenNullThrowsIllegalArgument() throws Exception {
        Connector.open(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConnectorOpenColonlessThrowsIllegalArgument() throws Exception {
        Connector.open("httpexamplecom");
    }
}

