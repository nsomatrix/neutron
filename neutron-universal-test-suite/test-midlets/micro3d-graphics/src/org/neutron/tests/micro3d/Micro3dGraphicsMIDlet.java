package org.neutron.tests.micro3d;

import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import com.mascotcapsule.micro3d.v3.*;

public class Micro3dGraphicsMIDlet extends MIDlet {

    private Display display;
    private M3DCanvas canvas;

    protected void startApp() {
        display = Display.getDisplay(this);
        canvas = new M3DCanvas(this);
        display.setCurrent(canvas);
        new Thread(canvas).start();
    }

    protected void pauseApp() {}

    protected void destroyApp(boolean unconditional) {}

    public static class M3DCanvas extends Canvas implements Runnable {
        private final Micro3dGraphicsMIDlet midlet;
        private final Vector logEntries = new Vector();
        private int passCount = 0;
        private int failCount = 0;
        private boolean completed = false;
        private String currentTest = "Initializing...";

        public M3DCanvas(Micro3dGraphicsMIDlet midlet) {
            this.midlet = midlet;
        }

        public void run() {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignore) {}

            System.out.println("=================================================");
            System.out.println("[SUITE-START] MascotCapsule Micro3D v3 Graphics");
            System.out.println("=================================================");

            runTest("Vector3D Operations", new TestCase() {
                public void execute() {
                    Vector3D v1 = new Vector3D(100, 0, 0);
                    Vector3D v2 = new Vector3D(0, 100, 0);

                    assertTrue("Vector3D getX", v1.getX() == 100);
                    assertTrue("Vector3D getY", v2.getY() == 100);

                    int dot = v1.innerProduct(v2);
                    assertTrue("Orthogonal vectors dot product == 0", dot == 0);

                    Vector3D vCross = Vector3D.outerProduct(v1, v2);
                    assertTrue("Cross product Z non-zero", vCross.getZ() != 0);

                    v1.unit();
                    assertTrue("Unit vector X non-zero", v1.getX() > 0);
                }
            });

            runTest("AffineTrans Transformation Matrices", new TestCase() {
                public void execute() {
                    AffineTrans trans = new AffineTrans();
                    trans.setIdentity();
                    assertTrue("m00 == 4096 (1.0 in 4096-fixed-point)", trans.m00 == 4096);
                    assertTrue("m11 == 4096", trans.m11 == 4096);
                    assertTrue("m22 == 4096", trans.m22 == 4096);

                    trans.setRotationY(1024); // 90 degree rotation in fixed point
                    assertTrue("AffineTrans rotation modified matrix", trans.m00 != 4096 || trans.m22 != 4096);
                }
            });

            runTest("FigureLayout & Projections", new TestCase() {
                public void execute() {
                    FigureLayout layout = new FigureLayout();
                    layout.setCenter(120, 160);
                    assertTrue("Center X == 120", layout.getCenterX() == 120);
                    assertTrue("Center Y == 160", layout.getCenterY() == 160);

                    layout.setPerspective(10, 4096, 512); // near, far, angle
                    assertTrue("Perspective near == 10", layout.getNear() == 10);
                    assertTrue("Perspective far == 4096", layout.getFar() == 4096);
                }
            });

            runTest("Effect3D Lighting & Environment Mapping", new TestCase() {
                public void execute() {
                    Effect3D effect = new Effect3D();
                    effect.setShadingType(Effect3D.NORMAL_SHADING);
                    assertTrue("ShadingType == NORMAL", effect.getShadingType() == Effect3D.NORMAL_SHADING);

                    Light light = new Light(new Vector3D(0, 0, 4096), 4096, 1024);
                    effect.setLight(light);
                    assertTrue("Effect light non-null", effect.getLight() == light);
                }
            });

            runTest("Graphics3D Context Bind & Flush", new TestCase() {
                public void execute() {
                    Graphics3D g3d = new Graphics3D();
                    Image targetImg = Image.createImage(64, 64);
                    Graphics g = targetImg.getGraphics();

                    g3d.bind(g);
                    g3d.flush();
                    g3d.release(g);

                    assertTrue("Micro3D context bind, flush, and release succeeded", true);
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
            g.setColor(0x1F0A24);
            g.fillRect(0, 0, w, h);

            g.setColor(0xFF00C8);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL));
            g.drawString("Micro3D v3 Graphics", 5, 5, Graphics.TOP | Graphics.LEFT);

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

