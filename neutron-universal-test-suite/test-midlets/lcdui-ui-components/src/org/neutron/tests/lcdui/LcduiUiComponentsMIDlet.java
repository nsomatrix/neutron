package org.neutron.tests.lcdui;

import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class LcduiUiComponentsMIDlet extends MIDlet implements CommandListener, ItemStateListener {

    private Display display;
    private Form form;
    private Command cmdOk;
    private Command cmdBack;
    private StringItem statusItem;
    private TextField txtInput;
    private ChoiceGroup choiceGrp;
    private Gauge gaugeBar;
    private int passCount = 0;
    private int failCount = 0;

    protected void startApp() {
        display = Display.getDisplay(this);
        form = new Form("LCDUI Components Test");
        cmdOk = new Command("OK", Command.OK, 1);
        cmdBack = new Command("Back", Command.BACK, 2);

        form.addCommand(cmdOk);
        form.addCommand(cmdBack);
        form.setCommandListener(this);
        form.setItemStateListener(this);

        statusItem = new StringItem("Status:", "Starting tests...");
        form.append(statusItem);

        txtInput = new TextField("Input Field:", "Initial Text", 64, TextField.ANY);
        form.append(txtInput);

        choiceGrp = new ChoiceGroup("Options:", Choice.EXCLUSIVE);
        choiceGrp.append("Option A", null);
        choiceGrp.append("Option B", null);
        choiceGrp.append("Option C", null);
        form.append(choiceGrp);

        gaugeBar = new Gauge("Progress:", true, 100, 25);
        form.append(gaugeBar);

        display.setCurrent(form);

        new Thread(new Runnable() {
            public void run() {
                executeTests();
            }
        }).start();
    }

    private void executeTests() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException ignore) {}

        System.out.println("=================================================");
        System.out.println("[SUITE-START] LCDUI High-Level UI Components Suite");
        System.out.println("=================================================");

        runTest("Display & Displayable Dimensions", new TestCase() {
            public void execute() {
                assertTrue("Display non-null", display != null);
                assertTrue("Displayable width > 0", form.getWidth() > 0);
                assertTrue("Displayable height > 0", form.getHeight() > 0);
                assertTrue("Form title match", "LCDUI Components Test".equals(form.getTitle()));
            }
        });

        runTest("Form Item Hierarchy & Insertion", new TestCase() {
            public void execute() {
                int initialCount = form.size();
                StringItem tempItem = new StringItem("Temp", "Temporary Item");
                int index = form.append(tempItem);
                assertTrue("Item appended", form.size() == initialCount + 1);

                form.delete(index);
                assertTrue("Item deleted", form.size() == initialCount);
            }
        });

        runTest("TextField Constraints & Contents", new TestCase() {
            public void execute() {
                txtInput.setString("Updated Value");
                assertTrue("TextField get string", "Updated Value".equals(txtInput.getString()));
                assertTrue("TextField maxSize", txtInput.getMaxSize() >= 64);
                assertTrue("TextField constraints", txtInput.getConstraints() == TextField.ANY);
            }
        });

        runTest("ChoiceGroup Exclusive Selection", new TestCase() {
            public void execute() {
                assertTrue("ChoiceGroup size == 3", choiceGrp.size() == 3);
                choiceGrp.setSelectedIndex(1, true);
                assertTrue("Selected index == 1", choiceGrp.getSelectedIndex() == 1);
                assertTrue("Option B selected", choiceGrp.isSelected(1));
                assertTrue("Option A not selected", !choiceGrp.isSelected(0));
            }
        });

        runTest("Gauge Boundaries & Mutability", new TestCase() {
            public void execute() {
                assertTrue("Gauge maxValue == 100", gaugeBar.getMaxValue() == 100);
                assertTrue("Gauge initial value == 25", gaugeBar.getValue() == 25);
                gaugeBar.setValue(75);
                assertTrue("Gauge updated value == 75", gaugeBar.getValue() == 75);
                assertTrue("Gauge interactive", gaugeBar.isInteractive());
            }
        });

        runTest("Alert Construction & Types", new TestCase() {
            public void execute() {
                Alert alert = new Alert("Notice", "Test Alert Body", null, AlertType.INFO);
                alert.setTimeout(Alert.FOREVER);
                assertTrue("Alert title", "Notice".equals(alert.getTitle()));
                assertTrue("Alert type", alert.getType() == AlertType.INFO);
                assertTrue("Alert timeout FOREVER", alert.getTimeout() == Alert.FOREVER);
            }
        });

        System.out.println("=================================================");
        System.out.println("[TEST-SUITE-SUMMARY] Passed: " + passCount + ", Failed: " + failCount + ", Total: " + (passCount + failCount));
        System.out.println("[TEST-SUITE-STATUS: " + (failCount == 0 ? "PASSED" : "FAILED") + "]");
        System.out.println("=================================================");

        statusItem.setText("Done: " + passCount + " Pass, " + failCount + " Fail");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignore) {}
        notifyDestroyed();
    }

    private void runTest(String name, TestCase test) {
        System.out.println("[TEST-START] " + name);
        try {
            test.execute();
            passCount++;
            System.out.println("[TEST-RESULT] " + name + ": PASS");
        } catch (Throwable t) {
            failCount++;
            System.out.println("[TEST-RESULT] " + name + ": FAIL -> " + t.getMessage());
            t.printStackTrace();
        }
    }

    private void assertTrue(String message, boolean condition) {
        if (!condition) {
            System.out.println("  [ASSERT-FAIL] " + message);
            throw new RuntimeException("Assertion Failed: " + message);
        }
        System.out.println("  [ASSERT-OK] " + message);
    }

    public void commandAction(Command c, Displayable d) {}

    public void itemStateChanged(Item item) {}

    protected void pauseApp() {}

    protected void destroyApp(boolean unconditional) {}

    public interface TestCase {
        void execute() throws Exception;
    }
}

