/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.cldc;

import java.security.cert.X509Certificate;
import javax.microedition.pki.Certificate;

public class CertificateImpl
implements Certificate {
    private X509Certificate cert;

    public CertificateImpl(X509Certificate cert) {
        this.cert = cert;
    }

    public String getIssuer() {
        return this.cert.getIssuerDN().getName();
    }

    public long getNotAfter() {
        return this.cert.getNotAfter().getTime();
    }

    public long getNotBefore() {
        return this.cert.getNotBefore().getTime();
    }

    public String getSerialNumber() {
        return this.cert.getSerialNumber().toString();
    }

    public String getSigAlgName() {
        return this.cert.getSigAlgName();
    }

    public String getSubject() {
        return this.cert.getSubjectDN().getName();
    }

    public String getType() {
        return this.cert.getType();
    }

    public String getVersion() {
        return Integer.toString(this.cert.getVersion());
    }
}

