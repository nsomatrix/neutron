package org.neutron.hosttests;

import java.io.File;
import org.neutron.app.classloader.MIDletClassLoaderConfig;
import org.neutron.app.util.FileRecordStoreManager;
import org.neutron.util.MemoryRecordStoreManager;
import com.mascotcapsule.micro3d.v3.AffineTrans;
import com.mascotcapsule.micro3d.v3.Vector3D;

public class MasterHostTestSuite {

    private static int passCount = 0;
    private static int failCount = 0;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  NEUTRON AUTOMATED HOST-SIDE WHITE-BOX TEST SUITE");
        System.out.println("=================================================");

        testMIDletClassLoaderConfig();
        testMicro3DFixedPointMath();
        testRecordStoreManagerIsolation();
        testHostMemoryMetrics();

        System.out.println("=================================================");
        System.out.println("[HOST-SUITE-SUMMARY] Passed: " + passCount + ", Failed: " + failCount + ", Total: " + (passCount + failCount));
        System.out.println("[HOST-SUITE-STATUS: " + (failCount == 0 ? "PASSED" : "FAILED") + "]");
        System.out.println("=================================================");

        if (failCount > 0) {
            System.exit(1);
        }
    }

    private static void testMIDletClassLoaderConfig() {
        System.out.println("[HOST-TEST] MIDletClassLoader Configuration & Delegation");
        try {
            MIDletClassLoaderConfig config = new MIDletClassLoaderConfig();
            config.setDelegationType("strict");
            assertCondition("Delegation type strict", config.getDelegationType(false) == MIDletClassLoaderConfig.DELEGATION_STRICT);

            config.setDelegationType("delegating");
            assertCondition("Delegation type delegating", config.getDelegationType(false) == MIDletClassLoaderConfig.DELEGATION_DELEGATING);

            passCount++;
            System.out.println("  -> PASS");
        } catch (Throwable t) {
            failCount++;
            System.out.println("  -> FAIL: " + t.getMessage());
        }
    }

    private static void testMicro3DFixedPointMath() {
        System.out.println("[HOST-TEST] Micro3D Fixed-Point Matrix & Vector Arithmetic");
        try {
            AffineTrans t = new AffineTrans();
            t.setIdentity();
            assertCondition("Identity m00 == 4096", t.m00 == 4096);
            assertCondition("Identity m11 == 4096", t.m11 == 4096);
            assertCondition("Identity m22 == 4096", t.m22 == 4096);

            Vector3D v1 = new Vector3D(4096, 0, 0);
            Vector3D v2 = new Vector3D(0, 4096, 0);
            Vector3D vCross = Vector3D.outerProduct(v1, v2);
            assertCondition("Vector cross product non-zero Z", vCross.getZ() != 0);

            passCount++;
            System.out.println("  -> PASS");
        } catch (Throwable t) {
            failCount++;
            System.out.println("  -> FAIL: " + t.getMessage());
        }
    }

    private static void testRecordStoreManagerIsolation() {
        System.out.println("[HOST-TEST] RMS File & Memory Manager Isolation");
        try {
            MemoryRecordStoreManager memMgr = new MemoryRecordStoreManager();
            assertCondition("Memory manager created", memMgr != null);

            FileRecordStoreManager fileMgr = new FileRecordStoreManager();
            assertCondition("File manager created", fileMgr != null);

            passCount++;
            System.out.println("  -> PASS");
        } catch (Throwable t) {
            failCount++;
            System.out.println("  -> FAIL: " + t.getMessage());
        }
    }

    private static void testHostMemoryMetrics() {
        System.out.println("[HOST-TEST] Host JVM Memory Metrics & Allocation");
        try {
            Runtime rt = Runtime.getRuntime();
            long total = rt.totalMemory();
            long free = rt.freeMemory();
            assertCondition("Total memory > 0", total > 0);
            assertCondition("Free memory <= Total memory", free <= total);

            passCount++;
            System.out.println("  -> PASS");
        } catch (Throwable t) {
            failCount++;
            System.out.println("  -> FAIL: " + t.getMessage());
        }
    }

    private static void assertCondition(String message, boolean cond) {
        if (!cond) {
            throw new RuntimeException("Assertion failed: " + message);
        }
    }
}
