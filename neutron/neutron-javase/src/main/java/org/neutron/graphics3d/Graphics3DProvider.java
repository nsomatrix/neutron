/*
 * Neutron Java ME Runtime Engine
 * 
 * Licensed under the Apache License, Version 2.0 or LGPL 2.1
 */
package org.neutron.graphics3d;

import org.neutron.log.Logger;

/**
 * Service registry and factory for 3D rendering backends.
 */
public final class Graphics3DProvider {

    private static Object activeMicro3DBackend;
    private static Object activeM3GBackend;

    private Graphics3DProvider() {
    }

    public static Object getActiveMicro3DBackend() {
        return activeMicro3DBackend;
    }

    public static void registerMicro3DBackend(Object backend) {
        activeMicro3DBackend = backend;
        if (backend != null) {
            Logger.debug("Registered Micro3D render backend: " + backend.getClass().getName());
        }
    }

    public static Object getActiveM3GBackend() {
        return activeM3GBackend;
    }

    public static void registerM3GBackend(Object backend) {
        activeM3GBackend = backend;
        if (backend != null) {
            Logger.debug("Registered M3G render backend: " + backend.getClass().getName());
        }
    }
}

