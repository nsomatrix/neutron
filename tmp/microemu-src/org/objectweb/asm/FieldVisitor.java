/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;

public interface FieldVisitor {
    public AnnotationVisitor visitAnnotation(String var1, boolean var2);

    public void visitAttribute(Attribute var1);

    public void visitEnd();
}

