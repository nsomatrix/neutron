/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.cldc.https;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import javax.microedition.io.HttpsConnection;
import javax.microedition.io.SecurityInfo;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import org.microemu.cldc.CertificateImpl;
import org.microemu.cldc.SecurityInfoImpl;
import org.microemu.log.Logger;

public class Connection
extends org.microemu.cldc.http.Connection
implements HttpsConnection {
    private SSLContext sslContext;
    private SecurityInfo securityInfo;

    public Connection() {
        try {
            this.sslContext = SSLContext.getInstance("SSL");
        }
        catch (NoSuchAlgorithmException ex) {
            Logger.error(ex);
        }
        this.securityInfo = null;
    }

    public SecurityInfo getSecurityInfo() throws IOException {
        if (this.securityInfo == null) {
            HttpsURLConnection https;
            Certificate[] certs;
            if (this.cn == null) {
                throw new IOException();
            }
            if (!this.connected) {
                this.cn.connect();
                this.connected = true;
            }
            if ((certs = (https = (HttpsURLConnection)this.cn).getServerCertificates()).length == 0) {
                throw new IOException();
            }
            this.securityInfo = new SecurityInfoImpl(https.getCipherSuite(), this.sslContext.getProtocol(), new CertificateImpl((X509Certificate)certs[0]));
        }
        return this.securityInfo;
    }

    public String getProtocol() {
        return "https";
    }

    public int getPort() {
        if (this.cn == null) {
            return -1;
        }
        int port = this.cn.getURL().getPort();
        if (port == -1) {
            return 443;
        }
        return port;
    }
}

