/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

public interface ClassVisitor {
    public void visit(int var1, int var2, String var3, String var4, String var5, String[] var6);

    public void visitSource(String var1, String var2);

    public void visitOuterClass(String var1, String var2, String var3);

    public AnnotationVisitor visitAnnotation(String var1, boolean var2);

    public void visitAttribute(Attribute var1);

    public void visitInnerClass(String var1, String var2, String var3, int var4);

    public FieldVisitor visitField(int var1, String var2, String var3, String var4, Object var5);

    public MethodVisitor visitMethod(int var1, String var2, String var3, String var4, String[] var5);

    public void visitEnd();
}

