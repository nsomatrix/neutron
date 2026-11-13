/*
 * Neutron Java ME Runtime Engine
 * 
 * Licensed under the Apache License, Version 2.0 or LGPL 2.1
 */
package org.neutron.graphics3d;

import org.neutron.graphics3d.spi.M3GBackend;
import org.neutron.graphics3d.spi.Micro3DBackend;
import org.neutron.log.Logger;

/**
 * Service registry and factory for 3D rendering backends.
 */
public final class Graphics3DProvider {

    private static Micro3DBackend activeMicro3DBackend;
    private static M3GBackend activeM3GBackend;

    private Graphics3DProvider() {
    }

    public static Micro3DBackend getActiveMicro3DBackend() {
        return activeMicro3DBackend;
    }

    public static void registerMicro3DBackend(Micro3DBackend backend) {
        activeMicro3DBackend = backend;
        if (backend != null) {
            Logger.info("Registered Micro3D render backend: " + backend.getClass().getName());
        }
    }

    public static M3GBackend getActiveM3GBackend() {
        return activeM3GBackend;
    }

    public static void registerM3GBackend(M3GBackend backend) {
        activeM3GBackend = backend;
        if (backend != null) {
            Logger.info("Registered M3G render backend: " + backend.getClass().getName());
        }
    }
}
