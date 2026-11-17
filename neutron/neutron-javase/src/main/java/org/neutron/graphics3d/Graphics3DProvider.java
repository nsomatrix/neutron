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
    private static boolean autoRegisterAttempted = false;

    private Graphics3DProvider() {
    }

    private static synchronized void tryAutoRegister() {
        if (autoRegisterAttempted) return;
        autoRegisterAttempted = true;
        try {
            Class<?> clazz = Class.forName("org.neutron.graphics3d.opengl.OpenGLRenderBackend");
            java.lang.reflect.Method m = clazz.getMethod("registerIfAvailable");
            m.invoke(null);
        } catch (Throwable ignored) {
            // OpenGL extension not on classpath or headless
        }
    }

    public static Micro3DBackend getActiveMicro3DBackend() {
        if (activeMicro3DBackend == null && Graphics3DConfig.getBackendType() == RenderBackendType.OPENGL) {
            tryAutoRegister();
        }
        return activeMicro3DBackend;
    }

    public static void registerMicro3DBackend(Micro3DBackend backend) {
        activeMicro3DBackend = backend;
        if (backend != null) {
            Logger.info("Registered Micro3D render backend: " + backend.getClass().getName());
        }
    }

    public static M3GBackend getActiveM3GBackend() {
        if (activeM3GBackend == null && Graphics3DConfig.getBackendType() == RenderBackendType.OPENGL) {
            tryAutoRegister();
        }
        return activeM3GBackend;
    }

    public static void registerM3GBackend(M3GBackend backend) {
        activeM3GBackend = backend;
        if (backend != null) {
            Logger.info("Registered M3G render backend: " + backend.getClass().getName());
        }
    }
}
