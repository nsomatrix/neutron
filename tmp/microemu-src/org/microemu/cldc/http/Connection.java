/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.cldc.http;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.microedition.io.HttpConnection;
import org.microemu.microedition.io.ConnectionImplementation;

public class Connection
implements HttpConnection,
ConnectionImplementation {
    protected URLConnection cn;
    protected boolean connected = false;
    protected static boolean allowNetworkConnection = true;

    public javax.microedition.io.Connection openConnection(String name, int mode, boolean timeouts) throws IOException {
        URL url;
        if (!Connection.isAllowNetworkConnection()) {
            throw new IOException("No network");
        }
        try {
            url = new URL(name);
        }
        catch (MalformedURLException ex) {
            throw new IOException(ex.toString());
        }
        this.cn = url.openConnection();
        this.cn.setDoOutput(true);
        if (this.cn instanceof HttpURLConnection) {
            ((HttpURLConnection)this.cn).setInstanceFollowRedirects(false);
        }
        return this;
    }

    public void close() throws IOException {
        if (this.cn == null) {
            return;
        }
        if (this.cn instanceof HttpURLConnection) {
            ((HttpURLConnection)this.cn).disconnect();
        }
        this.cn = null;
    }

    public String getURL() {
        if (this.cn == null) {
            return null;
        }
        return this.cn.getURL().toString();
    }

    public String getProtocol() {
        return "http";
    }

    public String getHost() {
        if (this.cn == null) {
            return null;
        }
        return this.cn.getURL().getHost();
    }

    public String getFile() {
        if (this.cn == null) {
            return null;
        }
        return this.cn.getURL().getFile();
    }

    public String getRef() {
        if (this.cn == null) {
            return null;
        }
        return this.cn.getURL().getRef();
    }

    public String getQuery() {
        if (this.cn == null) {
            return null;
        }
        return null;
    }

    public int getPort() {
        if (this.cn == null) {
            return -1;
        }
        int port = this.cn.getURL().getPort();
        if (port == -1) {
            return 80;
        }
        return port;
    }

    public String getRequestMethod() {
        if (this.cn == null) {
            return null;
        }
        if (this.cn instanceof HttpURLConnection) {
            return ((HttpURLConnection)this.cn).getRequestMethod();
        }
        return null;
    }

    public void setRequestMethod(String method) throws IOException {
        if (this.cn == null) {
            throw new IOException();
        }
        if (method.equals("POST")) {
            this.cn.setDoOutput(true);
        }
        if (this.cn instanceof HttpURLConnection) {
            ((HttpURLConnection)this.cn).setRequestMethod(method);
        }
    }

    public String getRequestProperty(String key) {
        if (this.cn == null) {
            return null;
        }
        return this.cn.getRequestProperty(key);
    }

    public void setRequestProperty(String key, String value) throws IOException {
        if (this.cn == null || this.connected) {
            throw new IOException();
        }
        this.cn.setRequestProperty(key, value);
    }

    public int getResponseCode() throws IOException {
        if (this.cn == null) {
            throw new IOException();
        }
        if (!this.connected) {
            this.cn.connect();
            this.connected = true;
        }
        if (this.cn instanceof HttpURLConnection) {
            return ((HttpURLConnection)this.cn).getResponseCode();
        }
        return -1;
    }

    public String getResponseMessage() throws IOException {
        if (this.cn == null) {
            throw new IOException();
        }
        if (!this.connected) {
            this.cn.connect();
            this.connected = true;
        }
        if (this.cn instanceof HttpURLConnection) {
            return ((HttpURLConnection)this.cn).getResponseMessage();
        }
        return null;
    }

    public long getExpiration() throws IOException {
        if (this.cn == null) {
            throw new IOException();
        }
        if (!this.connected) {
            this.cn.connect();
            this.connected = true;
        }
        return this.cn.getExpiration();
    }

    public long getDate() throws IOException {
        if (this.cn == null) {
            throw new IOException();
        }
        if (!this.connected) {
            this.cn.connect();
            this.connected = true;
        }
        return this.cn.getDate();
    }

    public long getLastModified() throws IOException {
        if (this.cn == null) {
            throw new IOException();
        }
        if (!this.connected) {
            this.cn.connect();
            this.connected = true;
        }
        return this.cn.getLastModified();
    }

    public String getHeaderField(String name) throws IOException {
        if (this.cn == null) {
            throw new IOException();
        }
        if (!this.connected) {
            this.cn.connect();
            this.connected = true;
        }
        return this.cn.getHeaderField(name);
    }

    public int getHeaderFieldInt(String name, int def) throws IOException {
        if (this.cn == null) {
            throw new IOException();
        }
        if (!this.connected) {
            this.cn.connect();
            this.connected = true;
        }
        return this.cn.getHeaderFieldInt(name, def);
    }

    public long getHeaderFieldDate(String name, long def) throws IOException {
        if (this.cn == null) {
            throw new IOException();
        }
        if (!this.connected) {
            this.cn.connect();
            this.connected = true;
        }
        return this.cn.getHeaderFieldDate(name, def);
    }

    public String getHeaderField(int n) throws IOException {
        if (this.cn == null) {
            throw new IOException();
        }
        if (!this.connected) {
            this.cn.connect();
            this.connected = true;
        }
        return this.cn.getHeaderField(this.getImplIndex(n));
    }

    public String getHeaderFieldKey(int n) throws IOException {
        if (this.cn == null) {
            throw new IOException();
        }
        if (!this.connected) {
            this.cn.connect();
            this.connected = true;
        }
        return this.cn.getHeaderFieldKey(this.getImplIndex(n));
    }

    private int getImplIndex(int index) {
        if (this.cn.getHeaderFieldKey(0) == null && this.cn.getHeaderField(0) != null) {
            ++index;
        }
        return index;
    }

    public InputStream openInputStream() throws IOException {
        if (this.cn == null) {
            throw new IOException();
        }
        this.connected = true;
        return this.cn.getInputStream();
    }

    public DataInputStream openDataInputStream() throws IOException {
        return new DataInputStream(this.openInputStream());
    }

    public OutputStream openOutputStream() throws IOException {
        if (this.cn == null) {
            throw new IOException();
        }
        this.connected = true;
        return this.cn.getOutputStream();
    }

    public DataOutputStream openDataOutputStream() throws IOException {
        return new DataOutputStream(this.openOutputStream());
    }

    public String getType() {
        try {
            return this.getHeaderField("content-type");
        }
        catch (IOException ex) {
            return null;
        }
    }

    public String getEncoding() {
        try {
            return this.getHeaderField("content-encoding");
        }
        catch (IOException ex) {
            return null;
        }
    }

    public long getLength() {
        try {
            return this.getHeaderFieldInt("content-length", -1);
        }
        catch (IOException ex) {
            return -1L;
        }
    }

    public static boolean isAllowNetworkConnection() {
        return allowNetworkConnection;
    }

    public static void setAllowNetworkConnection(boolean allowNetworkConnection) {
        Connection.allowNetworkConnection = allowNetworkConnection;
    }
}

