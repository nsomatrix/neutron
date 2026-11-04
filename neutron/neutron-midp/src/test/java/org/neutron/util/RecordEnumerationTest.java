package org.neutron.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordEnumeration;
import org.junit.Before;
import org.junit.Test;
import org.neutron.RecordStoreManager;

import static org.junit.Assert.*;

public class RecordEnumerationTest {

    private RecordStoreImpl recordStore;

    @Before
    public void setUp() throws Exception {
        RecordStoreManager dummyManager = (RecordStoreManager) Proxy.newProxyInstance(
                RecordStoreManager.class.getClassLoader(),
                new Class[]{RecordStoreManager.class},
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if ("getSizeAvailable".equals(method.getName())) {
                            return Integer.valueOf(1024 * 1024);
                        }
                        if ("getName".equals(method.getName())) {
                            return "DummyRMS";
                        }
                        return null;
                    }
                }
        );

        recordStore = new RecordStoreImpl(dummyManager, "testStore");
        recordStore.setOpen(true);
        byte[] rec1 = "First".getBytes("UTF-8");
        byte[] rec2 = "Second".getBytes("UTF-8");
        recordStore.addRecord(rec1, 0, rec1.length);
        recordStore.addRecord(rec2, 0, rec2.length);
    }

    @Test
    public void testPreviousRecordThrowsInvalidRecordIDExceptionWhenAtBeginning() throws Exception {
        RecordEnumeration enumeration = recordStore.enumerateRecords(null, null, false);
        assertFalse("At cursor 0, hasPreviousElement must be false", enumeration.hasPreviousElement());

        try {
            enumeration.previousRecord();
            fail("previousRecord() at cursor 0 must throw InvalidRecordIDException");
        } catch (InvalidRecordIDException e) {
            // Expected specification exception
        } catch (ArrayIndexOutOfBoundsException aiobe) {
            fail("previousRecord() threw ArrayIndexOutOfBoundsException instead of InvalidRecordIDException: " + aiobe);
        }

        try {
            enumeration.previousRecordId();
            fail("previousRecordId() at cursor 0 must throw InvalidRecordIDException");
        } catch (InvalidRecordIDException e) {
            // Expected specification exception
        } catch (ArrayIndexOutOfBoundsException aiobe) {
            fail("previousRecordId() threw ArrayIndexOutOfBoundsException instead of InvalidRecordIDException: " + aiobe);
        }
    }

    @Test
    public void testBidirectionalIterationAndReset() throws Exception {
        RecordEnumeration enumeration = recordStore.enumerateRecords(null, null, false);
        assertEquals(2, enumeration.numRecords());

        assertTrue(enumeration.hasNextElement());
        assertEquals(1, enumeration.nextRecordId());
        assertTrue(enumeration.hasNextElement());
        assertEquals(2, enumeration.nextRecordId());
        assertFalse(enumeration.hasNextElement());

        assertTrue(enumeration.hasPreviousElement());
        assertEquals(2, enumeration.previousRecordId());
        assertTrue(enumeration.hasPreviousElement());
        assertEquals(1, enumeration.previousRecordId());
        assertFalse(enumeration.hasPreviousElement());

        // Boundary condition
        try {
            enumeration.previousRecordId();
            fail("Expected InvalidRecordIDException when attempting previousRecordId() at start");
        } catch (InvalidRecordIDException expected) {
            // Expected
        }

        // Must still be able to iterate forward without corruption
        assertTrue(enumeration.hasNextElement());
        assertEquals(1, enumeration.nextRecordId());
    }
}

