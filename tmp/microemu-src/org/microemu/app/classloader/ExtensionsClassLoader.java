/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.classloader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.StringTokenizer;
import org.microemu.app.util.IOUtils;

public class ExtensionsClassLoader
extends URLClassLoader {
    private static final boolean debug = false;
    private AccessControlContext acc = AccessController.getContext();

    public ExtensionsClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public void addURL(URL url) {
        super.addURL(url);
    }

    public void addClasspath(String classpath) {
        StringTokenizer st = new StringTokenizer(classpath, ";");
        while (st.hasMoreTokens()) {
            try {
                String path = st.nextToken();
                if (path.startsWith("file:")) {
                    this.addURL(new URL(path));
                    continue;
                }
                this.addURL(new URL(IOUtils.getCanonicalFileURL(new File(path))));
            }
            catch (MalformedURLException e) {
                throw new Error(e);
            }
        }
    }

    public URL getResource(final String name) {
        try {
            URL url = (URL)AccessController.doPrivileged(new PrivilegedExceptionAction(){

                public Object run() {
                    return ExtensionsClassLoader.this.findResource(name);
                }
            }, this.acc);
            if (url != null) {
                return url;
            }
        }
        catch (PrivilegedActionException privilegedActionException) {
            // empty catch block
        }
        return super.getResource(name);
    }
}

