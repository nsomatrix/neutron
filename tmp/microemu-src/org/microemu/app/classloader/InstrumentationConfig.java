/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.classloader;

public class InstrumentationConfig {
    private boolean enhanceThreadCreation = false;
    private boolean enhanceCatchBlock = false;

    public boolean isEnhanceCatchBlock() {
        return this.enhanceCatchBlock;
    }

    public void setEnhanceCatchBlock(boolean enhanceCatchBlock) {
        this.enhanceCatchBlock = enhanceCatchBlock;
    }

    public boolean isEnhanceThreadCreation() {
        return this.enhanceThreadCreation;
    }

    public void setEnhanceThreadCreation(boolean enhanceThreadCreation) {
        this.enhanceThreadCreation = enhanceThreadCreation;
    }
}

