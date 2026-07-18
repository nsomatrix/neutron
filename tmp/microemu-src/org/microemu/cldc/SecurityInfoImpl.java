/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.cldc;

import javax.microedition.io.SecurityInfo;
import javax.microedition.pki.Certificate;
import org.microemu.log.Logger;

public class SecurityInfoImpl
implements SecurityInfo {
    private String cipherSuite;
    private String protocolName;
    private Certificate certificate;

    public SecurityInfoImpl(String cipherSuite, String protocolName, Certificate certificate) {
        this.cipherSuite = cipherSuite;
        this.protocolName = protocolName;
        this.certificate = certificate;
    }

    public String getCipherSuite() {
        return this.cipherSuite;
    }

    public String getProtocolName() {
        if (this.protocolName.startsWith("TLS")) {
            return "TLS";
        }
        if (this.protocolName.startsWith("SSL")) {
            return "SSL";
        }
        try {
            throw new RuntimeException();
        }
        catch (RuntimeException ex) {
            Logger.error(ex);
            throw ex;
        }
    }

    public String getProtocolVersion() {
        if (this.protocolName.startsWith("TLS")) {
            return "3.1";
        }
        if (this.getProtocolName().equals("SSL")) {
            return "3.0";
        }
        try {
            throw new RuntimeException();
        }
        catch (RuntimeException ex) {
            Logger.error(ex);
            throw ex;
        }
    }

    public Certificate getServerCertificate() {
        return this.certificate;
    }
}

