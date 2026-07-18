/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.util;

import java.io.InputStream;
import org.microemu.Injected;
import org.microemu.app.util.MIDletResourceInputStream;
import org.microemu.log.Logger;
import org.microemu.util.ThreadUtils;

public class MIDletResourceLoader {
    public static boolean traceResourceLoading = false;
    public static ClassLoader classLoader;
    private static final String FQCN;

    public static InputStream getResourceAsStream(Class origClass, String resourceName) {
        InputStream is;
        String callLocation;
        if (traceResourceLoading) {
            Logger.debug("Loading MIDlet resource", resourceName);
        }
        if (classLoader == origClass.getClassLoader() || (callLocation = ThreadUtils.getCallLocation(FQCN)) != null) {
            // empty if block
        }
        if ((is = classLoader.getResourceAsStream(resourceName = MIDletResourceLoader.resolveName(origClass, resourceName))) == null) {
            Logger.debug("Resource not found ", resourceName);
            return null;
        }
        return new MIDletResourceInputStream(is);
    }

    private static String resolveName(Class origClass, String name) {
        if (name == null) {
            return name;
        }
        if (!name.startsWith("/")) {
            while (origClass.isArray()) {
                origClass = origClass.getComponentType();
            }
            String baseName = origClass.getName();
            int index = baseName.lastIndexOf(46);
            if (index != -1) {
                name = baseName.substring(0, index).replace('.', '/') + "/" + name;
            }
        } else {
            name = name.substring(1);
        }
        return name;
    }

    static {
        FQCN = Injected.class.getName();
    }
}

