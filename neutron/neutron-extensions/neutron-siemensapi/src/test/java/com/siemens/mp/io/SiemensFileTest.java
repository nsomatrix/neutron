package com.siemens.mp.io;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SiemensFileTest {

    private File fileApi;

    @Before
    public void setUp() {
        fileApi = new File();
        File.delete("testfile.dat");
    }

    @Test
    public void testMultipleOpensReturnDistinctDescriptorsAndIndependentHandles() throws Exception {
        int fd1 = fileApi.open("testfile.dat");
        int fd2 = fileApi.open("testfile.dat");

        assertTrue("First descriptor should be non-negative", fd1 >= 0);
        assertTrue("Second descriptor should be non-negative", fd2 >= 0);
        assertNotEquals("Opening same file twice must return distinct descriptors", fd1, fd2);

        byte[] writeData = "Hello Siemens".getBytes("UTF-8");
        fileApi.write(fd1, writeData, 0, writeData.length);

        // Close fd1
        assertEquals(1, fileApi.close(fd1));

        // fd2 must remain open and readable
        byte[] readBuf = new byte[writeData.length];
        fileApi.seek(fd2, 0);
        int bytesRead = fileApi.read(fd2, readBuf, 0, readBuf.length);

        assertEquals("Must read all bytes through fd2", writeData.length, bytesRead);
        assertEquals("Hello Siemens", new String(readBuf, "UTF-8"));

        // Close fd2
        assertEquals(1, fileApi.close(fd2));
    }

    @Test
    public void testInvalidOrClosedDescriptorsSafelyHandled() {
        assertEquals(-1, fileApi.read(999, new byte[10], 0, 10));
        assertEquals(-1, fileApi.write(999, new byte[10], 0, 10));
        assertEquals(-1, fileApi.seek(999, 0));
        assertEquals(-1, fileApi.length(999));
        assertEquals(-1, fileApi.close(999));
    }
}

