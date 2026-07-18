/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.impl;

import org.microemu.device.impl.Rectangle;

public abstract class Shape
implements Cloneable {
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public abstract Rectangle getBounds();

    public abstract boolean contains(int var1, int var2);
}

