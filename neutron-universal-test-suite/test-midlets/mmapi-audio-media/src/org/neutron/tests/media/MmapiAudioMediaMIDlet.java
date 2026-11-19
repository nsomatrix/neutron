package org.neutron.tests.media;

import java.io.*;
import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;

public class MmapiAudioMediaMIDlet extends MIDlet {

    private Display display;
    private MediaCanvas canvas;

    protected void startApp() {
        display = Display.getDisplay(this);
        canvas = new MediaCanvas(this);
        display.setCurrent(canvas);
        new Thread(canvas).start();
    }

    protected void pauseApp() {}

    protected void destroyApp(boolean unconditional) {}

    public static class MediaCanvas extends Canvas implements Runnable {
        private final MmapiAudioMediaMIDlet midlet;
        private final Vector logEntries = new Vector();
        private int passCount = 0;
        private int failCount = 0;
        private boolean completed = false;
        private String currentTest = "Initializing...";

        public MediaCanvas(MmapiAudioMediaMIDlet midlet) {
            this.midlet = midlet;
        }

        public void run() {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignore) {}

            System.out.println("=================================================");
            System.out.println("[SUITE-START] MMAPI (JSR-135) Audio & Synthesis");
            System.out.println("=================================================");

            runTest("Manager Supported Types & Protocols", new TestCase() {
                public void execute() {
                    String[] types = Manager.getSupportedContentTypes(null);
                    assertTrue("Supported content types non-empty", types != null && types.length > 0);

                    String[] protos = Manager.getSupportedProtocols(null);
                    assertTrue("Supported protocols non-null array", protos != null);
                }
            });

            runTest("Synchronous Tone Synthesis (Manager.playTone)", new TestCase() {
                public void execute() {
                    try {
                        Manager.playTone(60, 50, 70); // Middle C, 50ms, volume 70
                        assertTrue("Manager.playTone executed cleanly", true);
                    } catch (MediaException me) {
                        fail("Manager.playTone threw MediaException: " + me.getMessage());
                    }
                }
            });

            runTest("Tone Sequence Player & ToneControl", new TestCase() {
                public void execute() throws Exception {
                    Player p = Manager.createPlayer(Manager.TONE_DEVICE_LOCATOR);
                    assertTrue("Tone player created", p != null);
                    p.realize();

                    ToneControl tc = (ToneControl) p.getControl("javax.microedition.media.control.ToneControl");
                    if (tc != null) {
                        byte tempo = 30; // 120 bpm
                        byte[] seq = new byte[] {
                            ToneControl.VERSION, 1,
                            ToneControl.TEMPO, tempo,
                            ToneControl.SET_VOLUME, 80,
                            60, 4, // C4
                            64, 4, // E4
                            67, 4  // G4
                        };
                        tc.setSequence(seq);
                        p.prefetch();
                        p.start();
                        Thread.sleep(100);
                        p.stop();
                    }
                    p.close();
                    assertTrue("Tone sequence player closed", p.getState() == Player.CLOSED);
                }
            });

            runTest("Synthetic PCM WAV Audio Player & Lifecycle", new TestCase() {
                public void execute() throws Exception {
                    byte[] wavBytes = createSyntheticWav(8000, 0.2); // 200ms tone at 8kHz
                    ByteArrayInputStream bais = new ByteArrayInputStream(wavBytes);
                    Player p = Manager.createPlayer(bais, "audio/x-wav");

                    assertTrue("Player state UNREALIZED", p.getState() == Player.UNREALIZED);
                    p.realize();
                    assertTrue("Player state REALIZED", p.getState() == Player.REALIZED);

                    p.prefetch();
                    assertTrue("Player state PREFETCHED", p.getState() == Player.PREFETCHED);

                    VolumeControl vc = (VolumeControl) p.getControl("VolumeControl");
                    if (vc != null) {
                        vc.setLevel(60);
                        assertTrue("Volume level set to 60", vc.getLevel() == 60);
                    }

                    p.start();
                    assertTrue("Player state STARTED", p.getState() == Player.STARTED);
                    Thread.sleep(50);

                    p.stop();
                    assertTrue("Player state PREFETCHED after stop", p.getState() == Player.PREFETCHED);

                    p.close();
                    assertTrue("Player state CLOSED", p.getState() == Player.CLOSED);
                }
            });

            runTest("Synthetic Standard MIDI Stream Player", new TestCase() {
                public void execute() throws Exception {
                    byte[] midiBytes = createMinimalMidiFile();
                    ByteArrayInputStream bais = new ByteArrayInputStream(midiBytes);
                    Player p = Manager.createPlayer(bais, "audio/midi");
                    assertTrue("MIDI Player created", p != null);

                    p.realize();
                    p.prefetch();
                    p.start();
                    Thread.sleep(50);
                    p.stop();
                    p.close();
                    assertTrue("MIDI Player closed", p.getState() == Player.CLOSED);
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

        private void fail(String message) {
            System.out.println("  [ASSERT-FAIL] " + message);
            throw new RuntimeException("Test Failed: " + message);
        }

        private byte[] createSyntheticWav(int sampleRate, double seconds) throws IOException {
            int numSamples = (int) (sampleRate * seconds);
            int dataSize = numSamples * 2; // 16-bit mono
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            // RIFF header
            dos.writeBytes("RIFF");
            dos.writeInt(Integer.reverseBytes(36 + dataSize));
            dos.writeBytes("WAVE");
            dos.writeBytes("fmt ");
            dos.writeInt(Integer.reverseBytes(16)); // SubChunk1Size (16 for PCM)
            dos.writeShort(Short.reverseBytes((short) 1)); // AudioFormat (1 = PCM)
            dos.writeShort(Short.reverseBytes((short) 1)); // NumChannels (1 = Mono)
            dos.writeInt(Integer.reverseBytes(sampleRate));
            dos.writeInt(Integer.reverseBytes(sampleRate * 2)); // ByteRate
            dos.writeShort(Short.reverseBytes((short) 2)); // BlockAlign
            dos.writeShort(Short.reverseBytes((short) 16)); // BitsPerSample
            dos.writeBytes("data");
            dos.writeInt(Integer.reverseBytes(dataSize));

            // Sine wave samples at 440 Hz
            double freq = 440.0;
            for (int i = 0; i < numSamples; i++) {
                double angle = 2.0 * Math.PI * freq * i / sampleRate;
                short sample = (short) (Math.sin(angle) * 16000.0);
                dos.writeShort(Short.reverseBytes(sample));
            }
            dos.flush();
            return baos.toByteArray();
        }

        private byte[] createMinimalMidiFile() throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // Header chunk "MThd"
            baos.write(new byte[] { 'M', 'T', 'h', 'd' });
            baos.write(new byte[] { 0, 0, 0, 6 }); // length 6
            baos.write(new byte[] { 0, 0 }); // format 0
            baos.write(new byte[] { 0, 1 }); // 1 track
            baos.write(new byte[] { 0, (byte)96 }); // 96 division
            // Track chunk "MTrk"
            baos.write(new byte[] { 'M', 'T', 'r', 'k' });
            byte[] trackData = new byte[] {
                0x00, (byte)0x90, 0x3C, 0x40, // delta=0, note on C4, vel=64
                0x60, (byte)0x80, 0x3C, 0x40, // delta=96, note off C4
                0x00, (byte)0xFF, 0x2F, 0x00  // end of track
            };
            int len = trackData.length;
            baos.write((len >> 24) & 0xFF);
            baos.write((len >> 16) & 0xFF);
            baos.write((len >> 8) & 0xFF);
            baos.write(len & 0xFF);
            baos.write(trackData);
            return baos.toByteArray();
        }

        protected void paint(Graphics g) {
            int w = getWidth();
            int h = getHeight();
            g.setColor(0x1B1005);
            g.fillRect(0, 0, w, h);

            g.setColor(0xFFA500);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL));
            g.drawString("MMAPI Audio & Media", 5, 5, Graphics.TOP | Graphics.LEFT);

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
