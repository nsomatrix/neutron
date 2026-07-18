/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm;

public interface AnnotationVisitor {
    public void visit(String var1, Object var2);

    public void visitEnum(String var1, String var2, String var3);

    public AnnotationVisitor visitAnnotation(String var1, String var2);

    public AnnotationVisitor visitArray(String var1);

    public void visitEnd();
}

