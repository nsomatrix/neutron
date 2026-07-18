/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Label;

public interface MethodVisitor {
    public AnnotationVisitor visitAnnotationDefault();

    public AnnotationVisitor visitAnnotation(String var1, boolean var2);

    public AnnotationVisitor visitParameterAnnotation(int var1, String var2, boolean var3);

    public void visitAttribute(Attribute var1);

    public void visitCode();

    public void visitFrame(int var1, int var2, Object[] var3, int var4, Object[] var5);

    public void visitInsn(int var1);

    public void visitIntInsn(int var1, int var2);

    public void visitVarInsn(int var1, int var2);

    public void visitTypeInsn(int var1, String var2);

    public void visitFieldInsn(int var1, String var2, String var3, String var4);

    public void visitMethodInsn(int var1, String var2, String var3, String var4);

    public void visitJumpInsn(int var1, Label var2);

    public void visitLabel(Label var1);

    public void visitLdcInsn(Object var1);

    public void visitIincInsn(int var1, int var2);

    public void visitTableSwitchInsn(int var1, int var2, Label var3, Label[] var4);

    public void visitLookupSwitchInsn(Label var1, int[] var2, Label[] var3);

    public void visitMultiANewArrayInsn(String var1, int var2);

    public void visitTryCatchBlock(Label var1, Label var2, Label var3, String var4);

    public void visitLocalVariable(String var1, String var2, String var3, Label var4, Label var5, int var6);

    public void visitLineNumber(int var1, Label var2);

    public void visitMaxs(int var1, int var2);

    public void visitEnd();
}

