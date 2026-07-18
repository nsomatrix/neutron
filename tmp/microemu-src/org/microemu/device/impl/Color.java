/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.impl;

public class Color {
    private int value;

    public Color(int value) {
        this.value = value;
    }

    public int getRed() {
        return this.value >> 16 & 0xFF;
    }

    public int getGreen() {
        return this.value >> 8 & 0xFF;
    }

    public int getBlue() {
        return this.value & 0xFF;
    }

    public int getRGB() {
        return this.value;
    }
}

