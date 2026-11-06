package org.neutron.cldc.datagram;

import javax.microedition.io.Datagram;
import org.junit.Test;
import static org.junit.Assert.*;

public class DatagramConnectionTest {

    @Test
    public void testNewDatagramInClientModeDoesNotThrow() throws Exception {
        Connection clientConn = new Connection();
        try {
            clientConn.openConnection("datagram://127.0.0.1:9876", 0, false);
            // Prior to fix, this threw IllegalArgumentException: Invalid Protocol 127.0.0.1:9876
            Datagram dgram = clientConn.newDatagram(128);
            assertNotNull(dgram);
            assertNotNull(dgram.getAddress());
            assertTrue("Address must have datagram protocol prefix", dgram.getAddress().startsWith("datagram://"));
            assertTrue("Address must contain port 9876", dgram.getAddress().endsWith(":9876"));
        } finally {
            clientConn.close();
        }
    }

    @Test
    public void testNewDatagramInServerModeHasNullAddress() throws Exception {
        Connection serverConn = new Connection();
        try {
            serverConn.openConnection("datagram://:0", 0, false);
            Datagram dgram = serverConn.newDatagram(128);
            assertNotNull(dgram);
            assertNull("Server datagram should not have destination address until assigned", dgram.getAddress());
        } finally {
            serverConn.close();
        }
    }

    @Test
    public void testDatagramGetAddressNullSafe() throws Exception {
        DatagramImpl dgram = new DatagramImpl(64);
        // Prior to fix, calling getAddress on unassigned packet threw NullPointerException
        assertNull("Unaddressed datagram must return null address", dgram.getAddress());

        dgram.setAddress((String) null);
        assertNull("Setting null address must clear address without throwing", dgram.getAddress());

        dgram.setAddress("datagram://127.0.0.1:5555");
        assertNotNull(dgram.getAddress());
        assertTrue(dgram.getAddress().contains("127.0.0.1:5555"));
    }

    @Test
    public void testDatagramWriteLengthTrackingAndReset() throws Exception {
        DatagramImpl dgram = new DatagramImpl(64);
        dgram.reset();
        assertEquals(0, dgram.getLength());

        dgram.writeInt(12345);
        assertEquals("Writing int (4 bytes) must set length to 4", 4, dgram.getLength());

        dgram.writeUTF("hello");
        assertEquals("Writing UTF 'hello' (2 bytes len + 5 bytes) must add 7 bytes (total 11)", 11, dgram.getLength());

        dgram.reset();
        assertEquals("Reset must reset length to 0", 0, dgram.getLength());
    }

    @Test
    public void testDatagramSendAndReceiveLoopback() throws Exception {
        Connection serverConn = new Connection();
        Connection clientConn = new Connection();
        try {
            serverConn.openConnection("datagram://:0", 0, false);
            int serverPort = serverConn.getLocalPort();
            clientConn.openConnection("datagram://127.0.0.1:" + serverPort, 0, false);

            Datagram sendDgram = clientConn.newDatagram(64);
            sendDgram.writeInt(777);
            sendDgram.writeUTF("datagram test");
            clientConn.send(sendDgram);

            Datagram recvDgram = serverConn.newDatagram(64);
            serverConn.receive(recvDgram);

            assertEquals(777, recvDgram.readInt());
            assertEquals("datagram test", recvDgram.readUTF());
        } finally {
            clientConn.close();
            serverConn.close();
        }
    }

    @Test
    public void testDatagramReadFully() throws Exception {
        DatagramImpl dgram = new DatagramImpl(64);
        byte[] original = new byte[] { 10, 20, 30, 40, 50, 60 };
        dgram.write(original);

        byte[] dest = new byte[6];
        dgram.readFully(dest, 0, 6);
        assertArrayEquals(original, dest);
    }
}

