/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.Vector;
import nanoxml.XMLElement;
import org.microemu.app.util.XMLItem;
import org.microemu.log.Logger;

public class MidletURLReference
implements XMLItem {
    private String name;
    private String url;

    public MidletURLReference() {
    }

    public MidletURLReference(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof MidletURLReference)) {
            return false;
        }
        return ((MidletURLReference)obj).url.equals(this.url);
    }

    public String toString() {
        URL u;
        try {
            u = new URL(this.url);
        }
        catch (MalformedURLException e) {
            Logger.error(e);
            return this.url;
        }
        StringBuffer b = new StringBuffer();
        String scheme = u.getProtocol();
        if (scheme.equals("file") || scheme.startsWith("http")) {
            b.append(scheme).append("://");
            if (u.getHost() != null) {
                b.append(u.getHost());
            }
            Vector<String> pathComponents = new Vector<String>();
            String pathSeparator = "/";
            StringTokenizer st = new StringTokenizer(u.getPath(), "/");
            while (st.hasMoreTokens()) {
                pathComponents.add(st.nextToken());
            }
            if (pathComponents.size() > 3) {
                b.append("/");
                b.append(pathComponents.get(0));
                b.append("/").append("...").append("/");
                b.append(pathComponents.get(pathComponents.size() - 2));
                b.append("/");
                b.append(pathComponents.get(pathComponents.size() - 1));
            } else {
                b.append(u.getPath());
            }
        } else {
            b.append(this.url);
        }
        if (this.name != null) {
            b.append(" - ");
            b.append(this.name);
        }
        return b.toString();
    }

    public void read(XMLElement xml) {
        this.name = xml.getChildString("name", "");
        this.url = xml.getChildString("url", "");
    }

    public void save(XMLElement xml) {
        xml.removeChildren();
        xml.addChild("name", this.name);
        xml.addChild("url", this.url);
    }

    public String getName() {
        return this.name;
    }

    public String getUrl() {
        return this.url;
    }
}

