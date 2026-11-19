package org.neutron.tests.gcf;

import java.io.*;
import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.io.*;

public class GcfNetworkingMIDlet extends MIDlet {

    private Display display;
    private NetCanvas canvas;

    protected void startApp() {
        display = Display.getDisplay(this);
        canvas = new NetCanvas(this);
        display.setCurrent(canvas);
        new Thread(canvas).start();
    }

    protected void pauseApp() {}

    protected void destroyApp(boolean unconditional) {}

    public static class NetCanvas extends Canvas implements Runnable {
        private final GcfNetworkingMIDlet midlet;
        private final Vector logEntries = new Vector();
        private int passCount = 0;
        private int failCount = 0;
        private boolean completed = false;
        private String currentTest = "Initializing...";

        public NetCanvas(GcfNetworkingMIDlet midlet) {
            this.midlet = midlet;
        }

        public void run() {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignore) {}

            System.out.println("=================================================");
            System.out.println("[SUITE-START] Generic Connection Framework (GCF)");
            System.out.println("=================================================");

            runTest("GCF Protocol Scheme Dispatching", new TestCase() {
                public void execute() {
                    boolean caughtInvalid = false;
                    try {
                        Connector.open("unknownproto://localhost:80");
                    } catch (ConnectionNotFoundException ex) {
                        caughtInvalid = true;
                    } catch (IOException ex) {
                        caughtInvalid = true;
                    }
                    assertTrue("Unknown scheme throws ConnectionNotFoundException", caughtInvalid);
                }
            });

            runTest("Local TCP Loopback Stream Socket", new TestCase() {
                public void execute() throws Exception {
                    final int port = 18456;
                    final ServerSocketConnection server = (ServerSocketConnection) Connector.open("socket://:" + port);
                    assertTrue("ServerSocketConnection open", server != null);

                    final boolean[] serverDone = new boolean[] { false };
                    final String testPayload = "Neutron-GCF-TCP-Test-Data";

                    Thread serverThread = new Thread(new Runnable() {
                        public void run() {
                            try {
                                StreamConnection clientConn = server.acceptAndOpen();
                                InputStream is = clientConn.openInputStream();
                                OutputStream os = clientConn.openOutputStream();

                                byte[] buf = new byte[64];
                                int read = is.read(buf);
                                String received = new String(buf, 0, read);

                                os.write(("ECHO:" + received).getBytes());
                                os.flush();

                                clientConn.close();
                                serverDone[0] = true;
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                    serverThread.start();

                    SocketConnection client = (SocketConnection) Connector.open("socket://127.0.0.1:" + port);
                    OutputStream cos = client.openOutputStream();
                    InputStream cis = client.openInputStream();

                    cos.write(testPayload.getBytes());
                    cos.flush();

                    byte[] clientBuf = new byte[64];
                    int clientRead = cis.read(clientBuf);
                    String clientReceived = new String(clientBuf, 0, clientRead);

                    assertTrue("TCP Echo received correctly", ("ECHO:" + testPayload).equals(clientReceived));

                    client.close();
                    server.close();
                }
            });

            runTest("Local UDP Datagram Connection", new TestCase() {
                public void execute() throws Exception {
                    javax.microedition.io.UDPDatagramConnection server = (javax.microedition.io.UDPDatagramConnection) Connector.open("datagram://:0");
                    int udpPort = server.getLocalPort();
                    DatagramConnection client = (DatagramConnection) Connector.open("datagram://127.0.0.1:" + udpPort);

                    Datagram sendDg = client.newDatagram(64);
                    sendDg.writeUTF("UDP-Ping");
                    client.send(sendDg);

                    Datagram recvDg = server.newDatagram(64);
                    server.receive(recvDg);

                    String recText = recvDg.readUTF();
                    assertTrue("UDP datagram text received", "UDP-Ping".equals(recText));

                    client.close();
                    server.close();
                }
            });

            completed = true;
            currentTest = "Complete";
            System.out.println("=================================================");
            System.out.println("[TEST-SUITE-SUMMARY] Passed: " + passCount + ", Failed: " + failCount + ", Total: " + (passCount + failCount));
            System.out.println("[TEST-SUITE-STATUS: " + (failCount == 0 ? "PASSED" : "FAILED") + "]");
            System.out.println("=================================================");
            repaint();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore) {}
            midlet.notifyDestroyed();
        }

        private void runTest(String name, TestCase test) {
            currentTest = name;
            repaint();
            System.out.println("[TEST-START] " + name);
            try {
                test.execute();
                passCount++;
                logEntries.addElement("[PASS] " + name);
                System.out.println("[TEST-RESULT] " + name + ": PASS");
            } catch (Throwable t) {
                failCount++;
                logEntries.addElement("[FAIL] " + name + " -> " + t.getMessage());
                System.out.println("[TEST-RESULT] " + name + ": FAIL -> " + t.getMessage());
                t.printStackTrace();
            }
            repaint();
        }

        private void assertTrue(String message, boolean condition) {
            if (!condition) {
                System.out.println("  [ASSERT-FAIL] " + message);
                throw new RuntimeException("Assertion Failed: " + message);
            }
            System.out.println("  [ASSERT-OK] " + message);
        }

        protected void paint(Graphics g) {
            int w = getWidth();
            int h = getHeight();
            g.setColor(0x061826);
            g.fillRect(0, 0, w, h);

            g.setColor(0x00FFCC);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL));
            g.drawString("GCF Networking Engine", 5, 5, Graphics.TOP | Graphics.LEFT);

            g.setColor(0xFFFFFF);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
            g.drawString("Running: " + currentTest, 5, 22, Graphics.TOP | Graphics.LEFT);

            int y = 45;
            int startIdx = Math.max(0, logEntries.size() - 8);
            for (int i = startIdx; i < logEntries.size(); i++) {
                String line = (String) logEntries.elementAt(i);
                if (line.startsWith("[PASS]")) {
                    g.setColor(0x00FF88);
                } else {
                    g.setColor(0xFF4444);
                }
                g.drawString(line, 5, y, Graphics.TOP | Graphics.LEFT);
                y += 14;
            }

            g.setColor(0x223344);
            g.fillRect(0, h - 22, w, 22);
            g.setColor(completed && failCount == 0 ? 0x00FF88 : (failCount > 0 ? 0xFF4444 : 0xFFFF00));
            g.drawString("Score: " + passCount + " Pass, " + failCount + " Fail (" + (completed ? "DONE" : "TESTING") + ")", 5, h - 18, Graphics.TOP | Graphics.LEFT);
        }
    }

    public interface TestCase {
        void execute() throws Exception;
    }
}
