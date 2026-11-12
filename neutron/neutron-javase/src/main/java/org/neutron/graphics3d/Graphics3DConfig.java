/*
 * Neutron Java ME Runtime Engine
 * 
 * Licensed under the Apache License, Version 2.0 or LGPL 2.1
 */
package org.neutron.graphics3d;

/**
 * Unified configuration manager for 3D graphics rendering subsystems
 * across JSR-184 (M3G) and MascotCapsule Micro3D v3.
 */
public final class Graphics3DConfig {

    private static RenderBackendType backendType;
    private static boolean forcePerspective;
    private static boolean bilinearFiltering;
    private static boolean subpixelPrecision;
    private static int perspectiveSubFactor;

    static {
        // Initialize from system properties with modern defaults
        // Default to hardware-accelerated OpenGL backend; fallback to software on headless/CPU-only
        String backendProp = System.getProperty("neutron.3d.backend", "opengl").trim().toLowerCase();
        if ("software".equals(backendProp) || "sw".equals(backendProp) || "cpu".equals(backendProp)) {
            backendType = RenderBackendType.SOFTWARE;
        } else {
            backendType = RenderBackendType.OPENGL;
        }

        // Standard perspective division enabled by default
        forcePerspective = Boolean.parseBoolean(System.getProperty("neutron.3d.force_perspective", "true"));
        bilinearFiltering = Boolean.parseBoolean(System.getProperty("neutron.3d.bilinear", "false"));
        subpixelPrecision = Boolean.parseBoolean(System.getProperty("neutron.3d.subpixel", "true"));
        perspectiveSubFactor = Integer.getInteger("neutron.3d.subfactor", 3);
    }

    private Graphics3DConfig() {
    }

    public static RenderBackendType getBackendType() {
        return backendType;
    }

    public static void setBackendType(RenderBackendType type) {
        if (type != null) {
            backendType = type;
        }
    }

    public static boolean isForcePerspective() {
        return forcePerspective;
    }

    public static void setForcePerspective(boolean force) {
        forcePerspective = force;
    }

    public static boolean isBilinearFiltering() {
        return bilinearFiltering;
    }

    public static void setBilinearFiltering(boolean bilinear) {
        bilinearFiltering = bilinear;
    }

    public static boolean isSubpixelPrecision() {
        return subpixelPrecision;
    }

    public static void setSubpixelPrecision(boolean subpixel) {
        subpixelPrecision = subpixel;
    }

    public static int getPerspectiveSubFactor() {
        return perspectiveSubFactor;
    }

    public static void setPerspectiveSubFactor(int subFactor) {
        if (subFactor >= 0 && subFactor <= 31) {
            perspectiveSubFactor = subFactor;
        }
    }
}

