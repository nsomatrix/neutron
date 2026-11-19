package org.neutron.graphics3d.opengl;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.neutron.log.Logger;

public class GLContextManager {
    private static volatile boolean initialized = false;
    private static volatile boolean available = false;
    private static long window = 0;

    public static boolean isAvailable() {
        if (!initialized) {
            synchronized (GLContextManager.class) {
                if (!initialized) {
                    init();
                }
            }
        }
        return available;
    }

    private static void init() {
        initialized = true;
        try {
            if (!GLFW.glfwInit()) {
                Logger.info("GLFW initialization failed; OpenGL 3D hardware backend not available.");
                return;
            }

            boolean isMac = System.getProperty("os.name", "").toLowerCase().contains("mac");
            GLFW.glfwDefaultWindowHints();
            GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
            if (!isMac) {
                GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
                GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
                GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_COMPAT_PROFILE);
            }

            window = GLFW.glfwCreateWindow(1, 1, "Neutron-Offscreen", 0, 0);
            if (window == 0) {
                // Fall back to legacy / default context for older GPUs or platforms
                GLFW.glfwDefaultWindowHints();
                GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
                window = GLFW.glfwCreateWindow(1, 1, "Neutron-Offscreen", 0, 0);
            }

            if (window != 0) {
                GLFW.glfwMakeContextCurrent(window);
                GL.createCapabilities();
                available = true;
                Logger.info("OpenGL 3D Hardware Backend initialized: " + GL11.glGetString(GL11.GL_RENDERER)
                        + " (" + GL11.glGetString(GL11.GL_VERSION) + ")");
            }
        } catch (Throwable t) {
            Logger.info("OpenGL 3D hardware backend initialization skipped: " + t.getMessage());
            available = false;
        }
    }

    public static synchronized void makeCurrent() {
        if (available && window != 0) {
            GLFW.glfwMakeContextCurrent(window);
            try {
                GL.createCapabilities();
            } catch (Throwable t) {
                // Ignore if already initialized or failed
            }
        }
    }

    public static synchronized void destroy() {
        if (window != 0) {
            GLFW.glfwDestroyWindow(window);
            window = 0;
        }
        if (available) {
            GLFW.glfwTerminate();
            available = false;
        }
    }
}

