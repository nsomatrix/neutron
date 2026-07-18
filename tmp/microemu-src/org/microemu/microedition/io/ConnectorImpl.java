/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.microedition.io;

import com.sun.cdc.io.ConnectionBaseInterface;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Vector;
import javax.microedition.io.Connection;
import javax.microedition.io.ConnectionNotFoundException;
import org.microemu.cldc.ClosedConnection;
import org.microemu.log.Logger;
import org.microemu.microedition.io.ConnectionImplementation;
import org.microemu.microedition.io.ConnectionInvocationHandler;
import org.microemu.microedition.io.ConnectorAdapter;

public class ConnectorImpl
extends ConnectorAdapter {
    private AccessControlContext acc;
    public static boolean debugConnectionInvocations = false;
    private final boolean needPrivilegedCalls = ConnectorImpl.isWebstart();
    static /* synthetic */ Class class$javax$microedition$io$Connection;

    public ConnectorImpl() {
        this.acc = AccessController.getContext();
    }

    private static boolean isWebstart() {
        try {
            return System.getProperty("javawebstart.version") != null;
        }
        catch (SecurityException e) {
            return false;
        }
    }

    public Connection open(final String name, final int mode, final boolean timeouts) throws IOException {
        try {
            return (Connection)AccessController.doPrivileged(new PrivilegedExceptionAction(){

                public Object run() throws IOException {
                    if (debugConnectionInvocations || ConnectorImpl.this.needPrivilegedCalls) {
                        return ConnectorImpl.this.openSecureProxy(name, mode, timeouts, ConnectorImpl.this.needPrivilegedCalls);
                    }
                    return ConnectorImpl.this.openSecure(name, mode, timeouts);
                }
            }, this.acc);
        }
        catch (PrivilegedActionException e) {
            if (e.getCause() instanceof IOException) {
                throw (IOException)e.getCause();
            }
            throw new IOException(e.toString());
        }
    }

    private static Class[] getAllInterfaces(Class klass) {
        Vector allInterfaces = new Vector();
        for (Class parent = klass; parent != null; parent = parent.getSuperclass()) {
            Class<?>[] interfaces = parent.getInterfaces();
            for (int i = 0; i < interfaces.length; ++i) {
                allInterfaces.add(interfaces[i]);
            }
        }
        return allInterfaces.toArray(new Class[allInterfaces.size()]);
    }

    private Connection openSecureProxy(String name, int mode, boolean timeouts, boolean needPrivilegedCalls) throws IOException {
        Connection origConnection = this.openSecure(name, mode, timeouts);
        Class connectionClass = null;
        Class[] interfaces = ConnectorImpl.getAllInterfaces(origConnection.getClass());
        for (int i = 0; i < interfaces.length; ++i) {
            if ((class$javax$microedition$io$Connection == null ? ConnectorImpl.class$("javax.microedition.io.Connection") : class$javax$microedition$io$Connection).isAssignableFrom(interfaces[i])) {
                connectionClass = interfaces[i];
                break;
            }
            if (!interfaces[i].getClass().getName().equals((class$javax$microedition$io$Connection == null ? ConnectorImpl.class$("javax.microedition.io.Connection") : class$javax$microedition$io$Connection).getName())) continue;
            Logger.debugClassLoader("ME2 Connection.class", class$javax$microedition$io$Connection == null ? ConnectorImpl.class$("javax.microedition.io.Connection") : class$javax$microedition$io$Connection);
            Logger.debugClassLoader(name + " Connection.class", interfaces[i]);
            Logger.error("Connection interface loaded by different ClassLoader");
        }
        if (connectionClass == null) {
            throw new ClassCastException(origConnection.getClass().getName() + " Connection expected");
        }
        return (Connection)Proxy.newProxyInstance(ConnectorImpl.class.getClassLoader(), interfaces, new ConnectionInvocationHandler(origConnection, needPrivilegedCalls));
    }

    private Connection openSecure(String name, int mode, boolean timeouts) throws IOException {
        String className = null;
        String protocol = null;
        try {
            try {
                protocol = name.substring(0, name.indexOf(58));
                className = "org.microemu.cldc." + protocol + ".Connection";
                Class<?> cl = Class.forName(className);
                Object inst = cl.newInstance();
                if (inst instanceof ConnectionImplementation) {
                    return ((ConnectionImplementation)inst).openConnection(name, mode, timeouts);
                }
                return ((ClosedConnection)inst).open(name);
            }
            catch (ClassNotFoundException e) {
                try {
                    className = "com.sun.cdc.io.j2me." + protocol + ".Protocol";
                    Class<?> cl = Class.forName(className);
                    ConnectionBaseInterface base = (ConnectionBaseInterface)cl.newInstance();
                    return base.openPrim(name.substring(name.indexOf(58) + 1), mode, timeouts);
                }
                catch (ClassNotFoundException ex) {
                    Logger.debug("connection [" + protocol + "] class not found", e);
                    Logger.debug("connection [" + protocol + "] class not found", ex);
                    throw new ConnectionNotFoundException("connection [" + protocol + "] class not found");
                }
            }
        }
        catch (InstantiationException e) {
            Logger.error("Unable to create", className, e);
            throw new ConnectionNotFoundException();
        }
        catch (IllegalAccessException e) {
            Logger.error("Unable to create", className, e);
            throw new ConnectionNotFoundException();
        }
    }
}

