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
    private static int internalScale;
    private static int msaaSamples;
    private static int anisotropicLevel;
    private static boolean asyncPbo;

    static {
        // Initialize from system properties with modern defaults
        // Default to pure-Java software 3D rasterizer
        String backendProp = System.getProperty("neutron.3d.backend", "software").trim().toLowerCase();
        if ("opengl".equals(backendProp) || "gpu".equals(backendProp) || "gl".equals(backendProp)) {
            backendType = RenderBackendType.OPENGL;
        } else {
            backendType = RenderBackendType.SOFTWARE;
        }

        // Standard perspective division enabled by default
        forcePerspective = Boolean.parseBoolean(System.getProperty("neutron.3d.force_perspective", "true"));
        bilinearFiltering = Boolean.parseBoolean(System.getProperty("neutron.3d.bilinear", "false"));
        subpixelPrecision = Boolean.parseBoolean(System.getProperty("neutron.3d.subpixel", "true"));
        perspectiveSubFactor = Integer.getInteger("neutron.3d.subfactor", 3);
        internalScale = Integer.getInteger("neutron.3d.scale", 1);
        msaaSamples = Integer.getInteger("neutron.3d.msaa", 0);
        anisotropicLevel = Integer.getInteger("neutron.3d.anisotropy", 0);
        asyncPbo = Boolean.parseBoolean(System.getProperty("neutron.3d.pbo", "true"));
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

    public static int getInternalScale() {
        return internalScale;
    }

    public static void setInternalScale(int scale) {
        if (scale >= 1 && scale <= 8) {
            internalScale = scale;
        }
    }

    public static int getMsaaSamples() {
        return msaaSamples;
    }

    public static void setMsaaSamples(int samples) {
        if (samples >= 0 && samples <= 16) {
            msaaSamples = samples;
        }
    }

    public static int getAnisotropicLevel() {
        return anisotropicLevel;
    }

    public static void setAnisotropicLevel(int level) {
        if (level >= 0 && level <= 16) {
            anisotropicLevel = level;
        }
    }

    public static boolean isAsyncPbo() {
        return asyncPbo;
    }

    public static void setAsyncPbo(boolean enabled) {
        asyncPbo = enabled;
    }
}

