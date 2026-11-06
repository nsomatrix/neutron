package org.neutron.cldc.http;

import org.junit.Test;
import static org.junit.Assert.*;

public class HttpConnectionTest {

    @Test
    public void testGetQueryReturnsQueryString() throws Exception {
        Connection conn = new Connection();
        try {
            conn.openConnection("http://example.com/path?foo=bar&num=123", 0, false);
            // Prior to fix, getQuery() unconditionally returned null
            assertEquals("foo=bar&num=123", conn.getQuery());
        } finally {
            conn.close();
        }
    }

    @Test
    public void testGetQueryNullWhenNoQueryString() throws Exception {
        Connection conn = new Connection();
        try {
            conn.openConnection("http://example.com/path", 0, false);
            assertNull(conn.getQuery());
        } finally {
            conn.close();
        }
    }

    @Test
    public void testDefaultRequestMethodIsGet() throws Exception {
        Connection conn = new Connection();
        try {
            conn.openConnection("http://example.com/path", 0, false);
            // MIDP 2.0 specification: default HTTP request method MUST be GET
            assertEquals("GET", conn.getRequestMethod());
        } finally {
            conn.close();
        }
    }

    @Test
    public void testSetRequestMethodPost() throws Exception {
        Connection conn = new Connection();
        try {
            conn.openConnection("http://example.com/path", 0, false);
            conn.setRequestMethod(javax.microedition.io.HttpConnection.POST);
            assertEquals("POST", conn.getRequestMethod());
        } finally {
            conn.close();
        }
    }
}

