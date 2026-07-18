/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm;

import org.objectweb.asm.ByteVector;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;

public class Attribute {
    public final String type;
    byte[] b;
    Attribute a;

    protected Attribute(String string) {
        this.type = string;
    }

    public boolean isUnknown() {
        return true;
    }

    public boolean isCodeAttribute() {
        return false;
    }

    protected Label[] getLabels() {
        return null;
    }

    protected Attribute read(ClassReader classReader, int n, int n2, char[] cArray, int n3, Label[] labelArray) {
        Attribute attribute = new Attribute(this.type);
        attribute.b = new byte[n2];
        System.arraycopy(classReader.b, n, attribute.b, 0, n2);
        return attribute;
    }

    protected ByteVector write(ClassWriter classWriter, byte[] byArray, int n, int n2, int n3) {
        ByteVector byteVector = new ByteVector();
        byteVector.a = this.b;
        byteVector.b = this.b.length;
        return byteVector;
    }

    final int a() {
        int n = 0;
        Attribute attribute = this;
        while (attribute != null) {
            ++n;
            attribute = attribute.a;
        }
        return n;
    }

    final int a(ClassWriter classWriter, byte[] byArray, int n, int n2, int n3) {
        Attribute attribute = this;
        int n4 = 0;
        while (attribute != null) {
            classWriter.newUTF8(attribute.type);
            n4 += attribute.write((ClassWriter)classWriter, (byte[])byArray, (int)n, (int)n2, (int)n3).b + 6;
            attribute = attribute.a;
        }
        return n4;
    }

    final void a(ClassWriter classWriter, byte[] byArray, int n, int n2, int n3, ByteVector byteVector) {
        Attribute attribute = this;
        while (attribute != null) {
            ByteVector byteVector2 = attribute.write(classWriter, byArray, n, n2, n3);
            byteVector.putShort(classWriter.newUTF8(attribute.type)).putInt(byteVector2.b);
            byteVector.putByteArray(byteVector2.a, 0, byteVector2.b);
            attribute = attribute.a;
        }
    }
}

