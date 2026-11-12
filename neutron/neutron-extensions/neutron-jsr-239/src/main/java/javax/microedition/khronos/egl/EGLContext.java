package javax.microedition.khronos.egl;

import javax.microedition.khronos.opengles.GL;
import org.neutron.jsr239.EGL10Impl;

public abstract class EGLContext {
    private static final EGL EGL_INSTANCE = new EGL10Impl();

    public static EGL getEGL() {
        return EGL_INSTANCE;
    }

    public abstract GL getGL();
}

