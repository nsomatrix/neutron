package org.neutron.tests.m3g;

import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.m3g.*;

public class M3g3dGraphicsMIDlet extends MIDlet {

    private Display display;
    private M3GCanvas canvas;

    protected void startApp() {
        display = Display.getDisplay(this);
        canvas = new M3GCanvas(this);
        display.setCurrent(canvas);
        new Thread(canvas).start();
    }

    protected void pauseApp() {}

    protected void destroyApp(boolean unconditional) {}

    public static class M3GCanvas extends Canvas implements Runnable {
        private final M3g3dGraphicsMIDlet midlet;
        private final Vector logEntries = new Vector();
        private int passCount = 0;
        private int failCount = 0;
        private boolean completed = false;
        private String currentTest = "Initializing...";

        public M3GCanvas(M3g3dGraphicsMIDlet midlet) {
            this.midlet = midlet;
        }

        public void run() {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignore) {}

            System.out.println("=================================================");
            System.out.println("[SUITE-START] JSR-184 (M3G 1.1) 3D Graphics Suite");
            System.out.println("=================================================");

            runTest("Graphics3D Singleton & Properties", new TestCase() {
                public void execute() {
                    Graphics3D g3d = Graphics3D.getInstance();
                    assertTrue("Graphics3D non-null", g3d != null);
                    Hashtable props = Graphics3D.getProperties();
                    assertTrue("Graphics3D properties non-null", props != null);
                }
            });

            runTest("Transform Matrix Computations", new TestCase() {
                public void execute() {
                    Transform t = new Transform();
                    t.postTranslate(10.0f, 20.0f, 30.0f);
                    t.postScale(2.0f, 2.0f, 2.0f);

                    float[] matrix = new float[16];
                    t.get(matrix);
                    assertTrue("Transform matrix element 0 (scale X)", Math.abs(matrix[0] - 2.0f) < 0.001f);
                    assertTrue("Transform matrix element 3 (trans X)", Math.abs(matrix[3] - 10.0f) < 0.001f);
                }
            });

            runTest("VertexBuffer & TriangleStripArray Construction", new TestCase() {
                public void execute() {
                    VertexBuffer vb = new VertexBuffer();
                    short[] positions = new short[] {
                        -10, -10, 0,
                         10, -10, 0,
                          0,  10, 0
                    };
                    VertexArray va = new VertexArray(3, 3, 2);
                    va.set(0, 3, positions);
                    vb.setPositions(va, 1.0f, null);

                    int[] stripLengths = new int[] { 3 };
                    int[] indices = new int[] { 0, 1, 2 };
                    TriangleStripArray tsa = new TriangleStripArray(indices, stripLengths);

                    assertTrue("VertexBuffer vertex count == 3", vb.getVertexCount() == 3);
                    assertTrue("TriangleStripArray index count == 3", tsa.getIndexCount() == 3);
                }
            });

            runTest("Appearance, Material & CompositingMode", new TestCase() {
                public void execute() {
                    Appearance app = new Appearance();
                    Material mat = new Material();
                    mat.setColor(Material.DIFFUSE, 0xFFFF8800);
                    mat.setShininess(64.0f);
                    app.setMaterial(mat);

                    CompositingMode cm = new CompositingMode();
                    cm.setBlending(CompositingMode.ALPHA);
                    cm.setDepthTestEnable(true);
                    cm.setDepthWriteEnable(true);
                    app.setCompositingMode(cm);

                    PolygonMode pm = new PolygonMode();
                    pm.setCulling(PolygonMode.CULL_BACK);
                    pm.setShading(PolygonMode.SHADE_SMOOTH);
                    app.setPolygonMode(pm);

                    assertTrue("Appearance material match", app.getMaterial() == mat);
                    assertTrue("Appearance compositing mode match", app.getCompositingMode() == cm);
                    assertTrue("Appearance polygon mode match", app.getPolygonMode() == pm);
                }
            });

            runTest("Retained Scene Graph (World, Camera, Light, Mesh)", new TestCase() {
                public void execute() {
                    World world = new World();
                    Camera cam = new Camera();
                    cam.setPerspective(60.0f, 1.33f, 1.0f, 1000.0f);
                    world.addChild(cam);
                    world.setActiveCamera(cam);

                    Light light = new Light();
                    light.setMode(Light.DIRECTIONAL);
                    light.setColor(0xFFFFFF);
                    world.addChild(light);

                    assertTrue("World has 2 children", world.getChildCount() == 2);
                    assertTrue("World active camera match", world.getActiveCamera() == cam);
                }
            });

            runTest("Immediate-Mode Graphics3D Render Loop", new TestCase() {
                public void execute() {
                    Graphics3D g3d = Graphics3D.getInstance();
                    Image targetImg = Image.createImage(64, 64);
                    Graphics g = targetImg.getGraphics();

                    g3d.bindTarget(g);

                    Camera cam = new Camera();
                    cam.setPerspective(60.0f, 1.0f, 1.0f, 100.0f);
                    g3d.setCamera(cam, new Transform());

                    VertexBuffer vb = new VertexBuffer();
                    short[] positions = new short[] { -5, -5, 0, 5, -5, 0, 0, 5, 0 };
                    VertexArray va = new VertexArray(3, 3, 2);
                    va.set(0, 3, positions);
                    vb.setPositions(va, 1.0f, null);

                    int[] stripLengths = new int[] { 3 };
                    int[] indices = new int[] { 0, 1, 2 };
                    TriangleStripArray tsa = new TriangleStripArray(indices, stripLengths);

                    Appearance app = new Appearance();
                    Transform t = new Transform();

                    g3d.render(vb, tsa, app, t);
                    g3d.releaseTarget();

                    assertTrue("Graphics3D immediate render executed without error", true);
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
            g.setColor(0x051525);
            g.fillRect(0, 0, w, h);

            g.setColor(0x00E5FF);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL));
            g.drawString("JSR-184 M3G 3D Engine", 5, 5, Graphics.TOP | Graphics.LEFT);

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
