/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.microedition;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.Map;
import org.microemu.microedition.Implementation;
import org.microemu.microedition.ImplementationUnloadable;
import org.microemu.microedition.io.ConnectorDelegate;

public class ImplFactory {
    public static final String DEFAULT = "org.microemu.default";
    private static final String INTERFACE_NAME_SUFIX = "Delegate";
    private static final String IMPLEMENTATION_NAME_SUFIX = "Impl";
    private Map implementations = new HashMap();
    private Map implementationsGCF = new HashMap();
    private AccessControlContext acc = AccessController.getContext();
    static /* synthetic */ Class class$org$microemu$microedition$ImplFactory;

    private ImplFactory() {
    }

    public static ImplFactory instance() {
        return SingletonHolder.instance;
    }

    public static void register(Class delegate, Class implementationClass) {
        ImplFactory.instance().implementations.put(delegate, implementationClass);
    }

    public static void register(Class delegate, Object implementationInstance) {
        ImplFactory.instance().implementations.put(delegate, implementationInstance);
    }

    public static void unregister(Class delegate, Class implementation) {
    }

    public static void registerGCF(String scheme, Object implementation) {
        Object impl;
        if (!ConnectorDelegate.class.isAssignableFrom(implementation.getClass())) {
            throw new IllegalArgumentException();
        }
        if (scheme == null) {
            scheme = DEFAULT;
        }
        if ((impl = ImplFactory.instance().implementationsGCF.get(scheme)) instanceof ImplementationUnloadable) {
            ((ImplementationUnloadable)impl).unregisterImplementation();
        }
        ImplFactory.instance().implementationsGCF.put(scheme, implementation);
    }

    public static void unregistedGCF(String scheme, Object implementation) {
        Object impl;
        if (!ConnectorDelegate.class.isAssignableFrom(implementation.getClass())) {
            throw new IllegalArgumentException();
        }
        if (scheme == null) {
            scheme = DEFAULT;
        }
        if ((impl = ImplFactory.instance().implementationsGCF.get(scheme)) == implementation) {
            ImplFactory.instance().implementationsGCF.remove(scheme);
        }
    }

    private Object getDefaultImplementation(Class delegateInterface) {
        try {
            String name = delegateInterface.getName();
            if (name.endsWith(INTERFACE_NAME_SUFIX)) {
                name = name.substring(0, name.length() - INTERFACE_NAME_SUFIX.length());
            }
            final String implClassName = name + IMPLEMENTATION_NAME_SUFIX;
            return AccessController.doPrivileged(new PrivilegedExceptionAction(){

                public Object run() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
                    Class<?> implClass = (class$org$microemu$microedition$ImplFactory == null ? (class$org$microemu$microedition$ImplFactory = ImplFactory.class$("org.microemu.microedition.ImplFactory")) : class$org$microemu$microedition$ImplFactory).getClassLoader().loadClass(implClassName);
                    try {
                        implClass.getConstructor(null);
                    }
                    catch (NoSuchMethodException e) {
                        throw new InstantiationException("No default constructor in class " + implClassName);
                    }
                    return implClass.newInstance();
                }
            }, this.acc);
        }
        catch (Throwable e) {
            throw new RuntimeException("Unable create " + delegateInterface.getName() + " implementation", e);
        }
    }

    private Object implementationNewInstance(final Class implClass) {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction(){

                public Object run() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
                    return implClass.newInstance();
                }
            }, this.acc);
        }
        catch (Throwable e) {
            throw new RuntimeException("Unable create " + implClass.getName() + " implementation", e);
        }
    }

    public static String getCGFScheme(String name) {
        return name.substring(0, name.indexOf(58));
    }

    public static ConnectorDelegate getCGFImplementation(String name) {
        String scheme = ImplFactory.getCGFScheme(name);
        ConnectorDelegate impl = (ConnectorDelegate)ImplFactory.instance().implementationsGCF.get(scheme);
        if (impl != null) {
            return impl;
        }
        impl = (ConnectorDelegate)ImplFactory.instance().implementationsGCF.get(DEFAULT);
        if (impl != null) {
            return impl;
        }
        return (ConnectorDelegate)ImplFactory.instance().getDefaultImplementation(ConnectorDelegate.class);
    }

    public static Implementation getImplementation(Class origClass, Class delegateInterface) {
        Object impl = ImplFactory.instance().implementations.get(delegateInterface);
        if (impl != null) {
            if (impl instanceof Class) {
                return (Implementation)ImplFactory.instance().implementationNewInstance((Class)impl);
            }
            return (Implementation)impl;
        }
        return (Implementation)ImplFactory.instance().getDefaultImplementation(delegateInterface);
    }

    private static class SingletonHolder {
        private static ImplFactory instance = new ImplFactory();

        private SingletonHolder() {
        }
    }
}

