/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.AnnotationWriter;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ByteVector;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.FieldWriter;
import org.objectweb.asm.Item;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.MethodWriter;
import org.objectweb.asm.Type;

public class ClassWriter
implements ClassVisitor {
    public static final int COMPUTE_MAXS = 1;
    public static final int COMPUTE_FRAMES = 2;
    static final byte[] a;
    ClassReader J;
    int b;
    int c = 1;
    final ByteVector d = new ByteVector();
    Item[] e = new Item[256];
    int f = (int)(0.75 * (double)this.e.length);
    final Item g = new Item();
    final Item h = new Item();
    final Item i = new Item();
    Item[] E;
    private short D;
    private int j;
    private int k;
    String F;
    private int l;
    private int m;
    private int n;
    private int[] o;
    private int p;
    private ByteVector q;
    private int r;
    private int s;
    private AnnotationWriter t;
    private AnnotationWriter u;
    private Attribute v;
    private int w;
    private ByteVector x;
    FieldWriter y;
    FieldWriter z;
    MethodWriter A;
    MethodWriter B;
    private final boolean H;
    private final boolean G;
    boolean I;

    public ClassWriter(int n) {
        this.H = (n & 1) != 0;
        this.G = (n & 2) != 0;
    }

    public ClassWriter(ClassReader classReader, int n) {
        this(n);
        classReader.a(this);
        this.J = classReader;
    }

    public void visit(int n, int n2, String string, String string2, String string3, String[] stringArray) {
        this.b = n;
        this.j = n2;
        this.k = this.newClass(string);
        this.F = string;
        if (string2 != null) {
            this.l = this.newUTF8(string2);
        }
        int n3 = this.m = string3 == null ? 0 : this.newClass(string3);
        if (stringArray != null && stringArray.length > 0) {
            this.n = stringArray.length;
            this.o = new int[this.n];
            for (int i = 0; i < this.n; ++i) {
                this.o[i] = this.newClass(stringArray[i]);
            }
        }
    }

    public void visitSource(String string, String string2) {
        if (string != null) {
            this.p = this.newUTF8(string);
        }
        if (string2 != null) {
            this.q = new ByteVector().putUTF8(string2);
        }
    }

    public void visitOuterClass(String string, String string2, String string3) {
        this.r = this.newClass(string);
        if (string2 != null && string3 != null) {
            this.s = this.newNameType(string2, string3);
        }
    }

    public AnnotationVisitor visitAnnotation(String string, boolean bl) {
        ByteVector byteVector = new ByteVector();
        byteVector.putShort(this.newUTF8(string)).putShort(0);
        AnnotationWriter annotationWriter = new AnnotationWriter(this, true, byteVector, byteVector, 2);
        if (bl) {
            annotationWriter.g = this.t;
            this.t = annotationWriter;
        } else {
            annotationWriter.g = this.u;
            this.u = annotationWriter;
        }
        return annotationWriter;
    }

    public void visitAttribute(Attribute attribute) {
        attribute.a = this.v;
        this.v = attribute;
    }

    public void visitInnerClass(String string, String string2, String string3, int n) {
        if (this.x == null) {
            this.x = new ByteVector();
        }
        ++this.w;
        this.x.putShort(string == null ? 0 : this.newClass(string));
        this.x.putShort(string2 == null ? 0 : this.newClass(string2));
        this.x.putShort(string3 == null ? 0 : this.newUTF8(string3));
        this.x.putShort(n);
    }

    public FieldVisitor visitField(int n, String string, String string2, String string3, Object object) {
        return new FieldWriter(this, n, string, string2, string3, object);
    }

    public MethodVisitor visitMethod(int n, String string, String string2, String string3, String[] stringArray) {
        return new MethodWriter(this, n, string, string2, string3, stringArray, this.H, this.G);
    }

    public void visitEnd() {
    }

    public byte[] toByteArray() {
        int n;
        int n2 = 24 + 2 * this.n;
        int n3 = 0;
        FieldWriter fieldWriter = this.y;
        while (fieldWriter != null) {
            ++n3;
            n2 += fieldWriter.a();
            fieldWriter = fieldWriter.a;
        }
        int n4 = 0;
        MethodWriter methodWriter = this.A;
        while (methodWriter != null) {
            ++n4;
            n2 += methodWriter.a();
            methodWriter = methodWriter.a;
        }
        int n5 = 0;
        if (this.l != 0) {
            ++n5;
            n2 += 8;
            this.newUTF8("Signature");
        }
        if (this.p != 0) {
            ++n5;
            n2 += 8;
            this.newUTF8("SourceFile");
        }
        if (this.q != null) {
            ++n5;
            n2 += this.q.b + 4;
            this.newUTF8("SourceDebugExtension");
        }
        if (this.r != 0) {
            ++n5;
            n2 += 10;
            this.newUTF8("EnclosingMethod");
        }
        if ((this.j & 0x20000) != 0) {
            ++n5;
            n2 += 6;
            this.newUTF8("Deprecated");
        }
        if ((this.j & 0x1000) != 0 && (this.b & 0xFFFF) < 49) {
            ++n5;
            n2 += 6;
            this.newUTF8("Synthetic");
        }
        if (this.x != null) {
            ++n5;
            n2 += 8 + this.x.b;
            this.newUTF8("InnerClasses");
        }
        if (this.t != null) {
            ++n5;
            n2 += 8 + this.t.a();
            this.newUTF8("RuntimeVisibleAnnotations");
        }
        if (this.u != null) {
            ++n5;
            n2 += 8 + this.u.a();
            this.newUTF8("RuntimeInvisibleAnnotations");
        }
        if (this.v != null) {
            n5 += this.v.a();
            n2 += this.v.a(this, null, 0, -1, -1);
        }
        ByteVector byteVector = new ByteVector(n2 += this.d.b);
        byteVector.putInt(-889275714).putInt(this.b);
        byteVector.putShort(this.c).putByteArray(this.d.a, 0, this.d.b);
        byteVector.putShort(this.j).putShort(this.k).putShort(this.m);
        byteVector.putShort(this.n);
        for (n = 0; n < this.n; ++n) {
            byteVector.putShort(this.o[n]);
        }
        byteVector.putShort(n3);
        fieldWriter = this.y;
        while (fieldWriter != null) {
            fieldWriter.a(byteVector);
            fieldWriter = fieldWriter.a;
        }
        byteVector.putShort(n4);
        methodWriter = this.A;
        while (methodWriter != null) {
            methodWriter.a(byteVector);
            methodWriter = methodWriter.a;
        }
        byteVector.putShort(n5);
        if (this.l != 0) {
            byteVector.putShort(this.newUTF8("Signature")).putInt(2).putShort(this.l);
        }
        if (this.p != 0) {
            byteVector.putShort(this.newUTF8("SourceFile")).putInt(2).putShort(this.p);
        }
        if (this.q != null) {
            n = this.q.b - 2;
            byteVector.putShort(this.newUTF8("SourceDebugExtension")).putInt(n);
            byteVector.putByteArray(this.q.a, 2, n);
        }
        if (this.r != 0) {
            byteVector.putShort(this.newUTF8("EnclosingMethod")).putInt(4);
            byteVector.putShort(this.r).putShort(this.s);
        }
        if ((this.j & 0x20000) != 0) {
            byteVector.putShort(this.newUTF8("Deprecated")).putInt(0);
        }
        if ((this.j & 0x1000) != 0 && (this.b & 0xFFFF) < 49) {
            byteVector.putShort(this.newUTF8("Synthetic")).putInt(0);
        }
        if (this.x != null) {
            byteVector.putShort(this.newUTF8("InnerClasses"));
            byteVector.putInt(this.x.b + 2).putShort(this.w);
            byteVector.putByteArray(this.x.a, 0, this.x.b);
        }
        if (this.t != null) {
            byteVector.putShort(this.newUTF8("RuntimeVisibleAnnotations"));
            this.t.a(byteVector);
        }
        if (this.u != null) {
            byteVector.putShort(this.newUTF8("RuntimeInvisibleAnnotations"));
            this.u.a(byteVector);
        }
        if (this.v != null) {
            this.v.a(this, null, 0, -1, -1, byteVector);
        }
        if (this.I) {
            ClassWriter classWriter = new ClassWriter(2);
            new ClassReader(byteVector.a).accept(classWriter, 4);
            return classWriter.toByteArray();
        }
        return byteVector.a;
    }

    Item a(Object object) {
        if (object instanceof Integer) {
            int n = (Integer)object;
            return this.a(n);
        }
        if (object instanceof Byte) {
            int n = ((Byte)object).intValue();
            return this.a(n);
        }
        if (object instanceof Character) {
            char c = ((Character)object).charValue();
            return this.a(c);
        }
        if (object instanceof Short) {
            int n = ((Short)object).intValue();
            return this.a(n);
        }
        if (object instanceof Boolean) {
            int n = (Boolean)object != false ? 1 : 0;
            return this.a(n);
        }
        if (object instanceof Float) {
            float f = ((Float)object).floatValue();
            return this.a(f);
        }
        if (object instanceof Long) {
            long l = (Long)object;
            return this.a(l);
        }
        if (object instanceof Double) {
            double d = (Double)object;
            return this.a(d);
        }
        if (object instanceof String) {
            return this.b((String)object);
        }
        if (object instanceof Type) {
            Type type = (Type)object;
            return this.a(type.getSort() == 10 ? type.getInternalName() : type.getDescriptor());
        }
        throw new IllegalArgumentException("value " + object);
    }

    public int newConst(Object object) {
        return this.a((Object)object).a;
    }

    public int newUTF8(String string) {
        this.g.a(1, string, null, null);
        Item item = this.a(this.g);
        if (item == null) {
            this.d.putByte(1).putUTF8(string);
            item = new Item(this.c++, this.g);
            this.b(item);
        }
        return item.a;
    }

    Item a(String string) {
        this.h.a(7, string, null, null);
        Item item = this.a(this.h);
        if (item == null) {
            this.d.b(7, this.newUTF8(string));
            item = new Item(this.c++, this.h);
            this.b(item);
        }
        return item;
    }

    public int newClass(String string) {
        return this.a((String)string).a;
    }

    Item a(String string, String string2, String string3) {
        this.i.a(9, string, string2, string3);
        Item item = this.a(this.i);
        if (item == null) {
            this.a(9, this.newClass(string), this.newNameType(string2, string3));
            item = new Item(this.c++, this.i);
            this.b(item);
        }
        return item;
    }

    public int newField(String string, String string2, String string3) {
        return this.a((String)string, (String)string2, (String)string3).a;
    }

    Item a(String string, String string2, String string3, boolean bl) {
        int n = bl ? 11 : 10;
        this.i.a(n, string, string2, string3);
        Item item = this.a(this.i);
        if (item == null) {
            this.a(n, this.newClass(string), this.newNameType(string2, string3));
            item = new Item(this.c++, this.i);
            this.b(item);
        }
        return item;
    }

    public int newMethod(String string, String string2, String string3, boolean bl) {
        return this.a((String)string, (String)string2, (String)string3, (boolean)bl).a;
    }

    Item a(int n) {
        this.g.a(n);
        Item item = this.a(this.g);
        if (item == null) {
            this.d.putByte(3).putInt(n);
            item = new Item(this.c++, this.g);
            this.b(item);
        }
        return item;
    }

    Item a(float f) {
        this.g.a(f);
        Item item = this.a(this.g);
        if (item == null) {
            this.d.putByte(4).putInt(this.g.c);
            item = new Item(this.c++, this.g);
            this.b(item);
        }
        return item;
    }

    Item a(long l) {
        this.g.a(l);
        Item item = this.a(this.g);
        if (item == null) {
            this.d.putByte(5).putLong(l);
            item = new Item(this.c, this.g);
            this.b(item);
            this.c += 2;
        }
        return item;
    }

    Item a(double d) {
        this.g.a(d);
        Item item = this.a(this.g);
        if (item == null) {
            this.d.putByte(6).putLong(this.g.d);
            item = new Item(this.c, this.g);
            this.b(item);
            this.c += 2;
        }
        return item;
    }

    private Item b(String string) {
        this.h.a(8, string, null, null);
        Item item = this.a(this.h);
        if (item == null) {
            this.d.b(8, this.newUTF8(string));
            item = new Item(this.c++, this.h);
            this.b(item);
        }
        return item;
    }

    public int newNameType(String string, String string2) {
        this.h.a(12, string, string2, null);
        Item item = this.a(this.h);
        if (item == null) {
            this.a(12, this.newUTF8(string), this.newUTF8(string2));
            item = new Item(this.c++, this.h);
            this.b(item);
        }
        return item.a;
    }

    int c(String string) {
        this.g.a(13, string, null, null);
        Item item = this.a(this.g);
        if (item == null) {
            item = this.c(this.g);
        }
        return item.a;
    }

    int a(String string, int n) {
        this.g.b = 14;
        this.g.c = n;
        this.g.g = string;
        this.g.j = Integer.MAX_VALUE & 14 + string.hashCode() + n;
        Item item = this.a(this.g);
        if (item == null) {
            item = this.c(this.g);
        }
        return item.a;
    }

    private Item c(Item item) {
        this.D = (short)(this.D + 1);
        Item item2 = new Item(this.D, this.g);
        this.b(item2);
        if (this.E == null) {
            this.E = new Item[16];
        }
        if (this.D == this.E.length) {
            Item[] itemArray = new Item[2 * this.E.length];
            System.arraycopy(this.E, 0, itemArray, 0, this.E.length);
            this.E = itemArray;
        }
        this.E[this.D] = item2;
        return item2;
    }

    int a(int n, int n2) {
        this.h.b = 15;
        this.h.d = (long)n | (long)n2 << 32;
        this.h.j = Integer.MAX_VALUE & 15 + n + n2;
        Item item = this.a(this.h);
        if (item == null) {
            String string = this.E[n].g;
            String string2 = this.E[n2].g;
            this.h.c = this.c(this.getCommonSuperClass(string, string2));
            item = new Item(0, this.h);
            this.b(item);
        }
        return item.c;
    }

    protected String getCommonSuperClass(String string, String string2) {
        Class<?> clazz;
        Class<?> clazz2;
        try {
            clazz2 = Class.forName(string.replace('/', '.'));
            clazz = Class.forName(string2.replace('/', '.'));
        }
        catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        if (clazz2.isAssignableFrom(clazz)) {
            return string;
        }
        if (clazz.isAssignableFrom(clazz2)) {
            return string2;
        }
        if (clazz2.isInterface() || clazz.isInterface()) {
            return "java/lang/Object";
        }
        while (!(clazz2 = clazz2.getSuperclass()).isAssignableFrom(clazz)) {
        }
        return clazz2.getName().replace('.', '/');
    }

    private Item a(Item item) {
        Item item2 = this.e[item.j % this.e.length];
        while (item2 != null && !item.a(item2)) {
            item2 = item2.k;
        }
        return item2;
    }

    private void b(Item item) {
        int n;
        if (this.c > this.f) {
            n = this.e.length;
            int n2 = n * 2 + 1;
            Item[] itemArray = new Item[n2];
            for (int i = n - 1; i >= 0; --i) {
                Item item2 = this.e[i];
                while (item2 != null) {
                    int n3 = item2.j % itemArray.length;
                    Item item3 = item2.k;
                    item2.k = itemArray[n3];
                    itemArray[n3] = item2;
                    item2 = item3;
                }
            }
            this.e = itemArray;
            this.f = (int)((double)n2 * 0.75);
        }
        n = item.j % this.e.length;
        item.k = this.e[n];
        this.e[n] = item;
    }

    private void a(int n, int n2, int n3) {
        this.d.b(n, n2).putShort(n3);
    }

    static {
        byte[] byArray = new byte[220];
        String string = "AAAAAAAAAAAAAAAABCKLLDDDDDEEEEEEEEEEEEEEEEEEEEAAAAAAAADDDDDEEEEEEEEEEEEEEEEEEEEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAAAAAAAAAAAAAAAAAAAIIIIIIIIIIIIIIIIDNOAAAAAAGGGGGGGHAFBFAAFFAAQPIIJJIIIIIIIIIIIIIIIIII";
        for (int i = 0; i < byArray.length; ++i) {
            byArray[i] = (byte)(string.charAt(i) - 65);
        }
        a = byArray;
    }
}

