package org.neutron.cldc.socket;

import java.io.IOException;
import java.net.Socket;
import javax.microedition.io.StreamConnection;
import org.junit.Test;
import static org.junit.Assert.*;

public class ServerSocketConnectionTest {

    @Test
    public void testDefaultConstructorBindsEphemeralPort() throws Exception {
        ServerSocketConnection serverConn = new ServerSocketConnection();
        try {
            int port = serverConn.getLocalPort();
            assertTrue("Default constructor should bind an ephemeral port (> 0)", port > 0);
            assertNotNull("Local address should not be null", serverConn.getLocalAddress());

            final StreamConnection[] accepted = new StreamConnection[1];
            final Exception[] error = new Exception[1];
            Thread acceptThread = new Thread(new Runnable() {
                public void run() {
                    try {
                        accepted[0] = serverConn.acceptAndOpen();
                    } catch (Exception e) {
                        error[0] = e;
                    }
                }
            });
            acceptThread.start();

            // Connect a client socket
            Socket client = new Socket("127.0.0.1", port);
            acceptThread.join(2000);

            assertNull("acceptAndOpen must not throw exception", error[0]);
            assertNotNull("Connection must be accepted", accepted[0]);

            client.close();
            accepted[0].close();
        } finally {
            serverConn.close();
        }
    }

    @Test
    public void testExplicitPortBinding() throws Exception {
        ServerSocketConnection serverConn = new ServerSocketConnection(0);
        try {
            assertTrue("Explicit port 0 should bind an ephemeral port (> 0)", serverConn.getLocalPort() > 0);
        } finally {
            serverConn.close();
        }
    }
}

