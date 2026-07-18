/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.classloader;

import java.util.List;
import java.util.Vector;
import org.microemu.app.ConfigurationException;

public class MIDletClassLoaderConfig {
    public static final int DELEGATION_STRICT = 0;
    public static final int DELEGATION_RELAXED = 1;
    public static final int DELEGATION_DELEGATING = 2;
    public static final int DELEGATION_SYSTEM = 3;
    private int delegationType = 0;
    private boolean delegationSelected = false;
    List appclasses = new Vector();
    List appclasspath = new Vector();

    public void setDelegationType(String delegationType) throws ConfigurationException {
        if ("strict".equalsIgnoreCase(delegationType)) {
            this.delegationType = 0;
        } else if ("relaxed".equalsIgnoreCase(delegationType)) {
            this.delegationType = 1;
        } else if ("delegating".equalsIgnoreCase(delegationType)) {
            this.delegationType = 2;
        } else if ("system".equalsIgnoreCase(delegationType)) {
            if (this.appclasses.size() != 0 || this.appclasspath.size() != 0) {
                throw new ConfigurationException("Can't extend system CLASSPATH");
            }
            this.delegationType = 3;
        } else {
            throw new ConfigurationException("Unknown delegationType [" + delegationType + "]");
        }
        this.delegationSelected = true;
    }

    public int getDelegationType(boolean forJad) {
        if (!this.delegationSelected && !forJad) {
            return 1;
        }
        return this.delegationType;
    }

    public boolean isClassLoaderDisabled() {
        return this.delegationType == 3;
    }

    public void addAppClassPath(String path) throws ConfigurationException {
        if (this.delegationType == 3) {
            throw new ConfigurationException("Can't extend system CLASSPATH");
        }
        this.appclasspath.add(path);
    }

    public void addAppClass(String className) throws ConfigurationException {
        if (this.delegationType == 3) {
            throw new ConfigurationException("Can't extend system CLASSPATH");
        }
        this.appclasses.add(className);
    }
}

