/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.AnnotationWriter;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ByteVector;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Edge;
import org.objectweb.asm.Frame;
import org.objectweb.asm.Handler;
import org.objectweb.asm.Item;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

class MethodWriter
implements MethodVisitor {
    MethodWriter a;
    final ClassWriter b;
    private int c;
    private final int d;
    private final int e;
    private final String f;
    String g;
    int h;
    int i;
    int j;
    int[] k;
    private ByteVector l;
    private AnnotationWriter m;
    private AnnotationWriter n;
    private AnnotationWriter[] o;
    private AnnotationWriter[] p;
    private int S;
    private Attribute q;
    private ByteVector r = new ByteVector();
    private int s;
    private int t;
    private int u;
    private ByteVector v;
    private int w;
    private int[] x;
    private int y;
    private int[] z;
    private int A;
    private Handler B;
    private Handler C;
    private int D;
    private ByteVector E;
    private int F;
    private ByteVector G;
    private int H;
    private ByteVector I;
    private Attribute J;
    private boolean K;
    private int L;
    private final int M;
    private Label N;
    private Label O;
    private Label P;
    private int Q;
    private int R;

    MethodWriter(ClassWriter classWriter, int n, String string, String string2, String string3, String[] stringArray, boolean bl, boolean bl2) {
        int n2;
        if (classWriter.A == null) {
            classWriter.A = this;
        } else {
            classWriter.B.a = this;
        }
        classWriter.B = this;
        this.b = classWriter;
        this.c = n;
        this.d = classWriter.newUTF8(string);
        this.e = classWriter.newUTF8(string2);
        this.f = string2;
        this.g = string3;
        if (stringArray != null && stringArray.length > 0) {
            this.j = stringArray.length;
            this.k = new int[this.j];
            for (n2 = 0; n2 < this.j; ++n2) {
                this.k[n2] = classWriter.newClass(stringArray[n2]);
            }
        }
        int n3 = bl2 ? 0 : (this.M = bl ? 1 : 2);
        if (bl || bl2) {
            if (bl2 && "<init>".equals(string)) {
                this.c |= 0x40000;
            }
            n2 = MethodWriter.a(this.f) >> 2;
            if ((n & 8) != 0) {
                --n2;
            }
            this.t = n2;
            this.N = new Label();
            this.N.a |= 8;
            this.visitLabel(this.N);
        }
    }

    public AnnotationVisitor visitAnnotationDefault() {
        this.l = new ByteVector();
        return new AnnotationWriter(this.b, false, this.l, null, 0);
    }

    public AnnotationVisitor visitAnnotation(String string, boolean bl) {
        ByteVector byteVector = new ByteVector();
        byteVector.putShort(this.b.newUTF8(string)).putShort(0);
        AnnotationWriter annotationWriter = new AnnotationWriter(this.b, true, byteVector, byteVector, 2);
        if (bl) {
            annotationWriter.g = this.m;
            this.m = annotationWriter;
        } else {
            annotationWriter.g = this.n;
            this.n = annotationWriter;
        }
        return annotationWriter;
    }

    public AnnotationVisitor visitParameterAnnotation(int n, String string, boolean bl) {
        ByteVector byteVector = new ByteVector();
        if ("Ljava/lang/Synthetic;".equals(string)) {
            this.S = Math.max(this.S, n + 1);
            return new AnnotationWriter(this.b, false, byteVector, null, 0);
        }
        byteVector.putShort(this.b.newUTF8(string)).putShort(0);
        AnnotationWriter annotationWriter = new AnnotationWriter(this.b, true, byteVector, byteVector, 2);
        if (bl) {
            if (this.o == null) {
                this.o = new AnnotationWriter[Type.getArgumentTypes(this.f).length];
            }
            annotationWriter.g = this.o[n];
            this.o[n] = annotationWriter;
        } else {
            if (this.p == null) {
                this.p = new AnnotationWriter[Type.getArgumentTypes(this.f).length];
            }
            annotationWriter.g = this.p[n];
            this.p[n] = annotationWriter;
        }
        return annotationWriter;
    }

    public void visitAttribute(Attribute attribute) {
        if (attribute.isCodeAttribute()) {
            attribute.a = this.J;
            this.J = attribute;
        } else {
            attribute.a = this.q;
            this.q = attribute;
        }
    }

    public void visitCode() {
    }

    public void visitFrame(int n, int n2, Object[] objectArray, int n3, Object[] objectArray2) {
        if (this.M == 0) {
            return;
        }
        if (n == -1) {
            int n4;
            this.a(this.r.b, n2, n3);
            for (n4 = 0; n4 < n2; ++n4) {
                this.z[this.y++] = objectArray[n4] instanceof String ? 0x1700000 | this.b.c((String)objectArray[n4]) : (objectArray[n4] instanceof Integer ? (Integer)objectArray[n4] : 0x1800000 | this.b.a("", ((Label)objectArray[n4]).c));
            }
            for (n4 = 0; n4 < n3; ++n4) {
                this.z[this.y++] = objectArray2[n4] instanceof String ? 0x1700000 | this.b.c((String)objectArray2[n4]) : (objectArray2[n4] instanceof Integer ? (Integer)objectArray2[n4] : 0x1800000 | this.b.a("", ((Label)objectArray2[n4]).c));
            }
            this.b();
        } else {
            int n5;
            if (this.v == null) {
                this.v = new ByteVector();
                n5 = this.r.b;
            } else {
                n5 = this.r.b - this.w - 1;
            }
            switch (n) {
                case 0: {
                    int n6;
                    this.v.putByte(255).putShort(n5).putShort(n2);
                    for (n6 = 0; n6 < n2; ++n6) {
                        this.a(objectArray[n6]);
                    }
                    this.v.putShort(n3);
                    for (n6 = 0; n6 < n3; ++n6) {
                        this.a(objectArray2[n6]);
                    }
                    break;
                }
                case 1: {
                    this.v.putByte(251 + n2).putShort(n5);
                    for (int i = 0; i < n2; ++i) {
                        this.a(objectArray[i]);
                    }
                    break;
                }
                case 2: {
                    this.v.putByte(251 - n2).putShort(n5);
                    break;
                }
                case 3: {
                    if (n5 < 64) {
                        this.v.putByte(n5);
                        break;
                    }
                    this.v.putByte(251).putShort(n5);
                    break;
                }
                case 4: {
                    if (n5 < 64) {
                        this.v.putByte(64 + n5);
                    } else {
                        this.v.putByte(247).putShort(n5);
                    }
                    this.a(objectArray2[0]);
                }
            }
            this.w = this.r.b;
            ++this.u;
        }
    }

    public void visitInsn(int n) {
        this.r.putByte(n);
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(n, 0, null, null);
            } else {
                int n2 = this.Q + Frame.a[n];
                if (n2 > this.R) {
                    this.R = n2;
                }
                this.Q = n2;
            }
            if (n >= 172 && n <= 177 || n == 191) {
                this.e();
            }
        }
    }

    public void visitIntInsn(int n, int n2) {
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(n, n2, null, null);
            } else if (n != 188) {
                int n3 = this.Q + 1;
                if (n3 > this.R) {
                    this.R = n3;
                }
                this.Q = n3;
            }
        }
        if (n == 17) {
            this.r.b(n, n2);
        } else {
            this.r.a(n, n2);
        }
    }

    public void visitVarInsn(int n, int n2) {
        int n3;
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(n, n2, null, null);
            } else if (n == 169) {
                this.P.a |= 0x100;
                this.P.f = this.Q;
                this.e();
            } else {
                n3 = this.Q + Frame.a[n];
                if (n3 > this.R) {
                    this.R = n3;
                }
                this.Q = n3;
            }
        }
        if (this.M != 2 && (n3 = n == 22 || n == 24 || n == 55 || n == 57 ? n2 + 2 : n2 + 1) > this.t) {
            this.t = n3;
        }
        if (n2 < 4 && n != 169) {
            n3 = n < 54 ? 26 + (n - 21 << 2) + n2 : 59 + (n - 54 << 2) + n2;
            this.r.putByte(n3);
        } else if (n2 >= 256) {
            this.r.putByte(196).b(n, n2);
        } else {
            this.r.a(n, n2);
        }
        if (n >= 54 && this.M == 0 && this.A > 0) {
            this.visitLabel(new Label());
        }
    }

    public void visitTypeInsn(int n, String string) {
        Item item = this.b.a(string);
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(n, this.r.b, this.b, item);
            } else if (n == 187) {
                int n2 = this.Q + 1;
                if (n2 > this.R) {
                    this.R = n2;
                }
                this.Q = n2;
            }
        }
        this.r.b(n, item.a);
    }

    public void visitFieldInsn(int n, String string, String string2, String string3) {
        Item item = this.b.a(string, string2, string3);
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(n, 0, this.b, item);
            } else {
                int n2;
                char c = string3.charAt(0);
                switch (n) {
                    case 178: {
                        n2 = this.Q + (c == 'D' || c == 'J' ? 2 : 1);
                        break;
                    }
                    case 179: {
                        n2 = this.Q + (c == 'D' || c == 'J' ? -2 : -1);
                        break;
                    }
                    case 180: {
                        n2 = this.Q + (c == 'D' || c == 'J' ? 1 : 0);
                        break;
                    }
                    default: {
                        n2 = this.Q + (c == 'D' || c == 'J' ? -3 : -2);
                    }
                }
                if (n2 > this.R) {
                    this.R = n2;
                }
                this.Q = n2;
            }
        }
        this.r.b(n, item.a);
    }

    public void visitMethodInsn(int n, String string, String string2, String string3) {
        boolean bl = n == 185;
        Item item = this.b.a(string, string2, string3, bl);
        int n2 = item.c;
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(n, 0, this.b, item);
            } else {
                int n3;
                if (n2 == 0) {
                    item.c = n2 = MethodWriter.a(string3);
                }
                if ((n3 = n == 184 ? this.Q - (n2 >> 2) + (n2 & 3) + 1 : this.Q - (n2 >> 2) + (n2 & 3)) > this.R) {
                    this.R = n3;
                }
                this.Q = n3;
            }
        }
        if (bl) {
            if (n2 == 0) {
                item.c = n2 = MethodWriter.a(string3);
            }
            this.r.b(185, item.a).a(n2 >> 2, 0);
        } else {
            this.r.b(n, item.a);
        }
    }

    public void visitJumpInsn(int n, Label label) {
        Label label2 = null;
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(n, 0, null, null);
                label.a().a |= 0x10;
                this.a(0, label);
                if (n != 167) {
                    label2 = new Label();
                }
            } else if (n == 168) {
                if ((label.a & 0x200) == 0) {
                    label.a |= 0x200;
                    ++this.L;
                }
                this.P.a |= 0x80;
                this.a(this.Q + 1, label);
                label2 = new Label();
            } else {
                this.Q += Frame.a[n];
                this.a(this.Q, label);
            }
        }
        if ((label.a & 2) != 0 && label.c - this.r.b < Short.MIN_VALUE) {
            if (n == 167) {
                this.r.putByte(200);
            } else if (n == 168) {
                this.r.putByte(201);
            } else {
                if (label2 != null) {
                    label2.a |= 0x10;
                }
                this.r.putByte(n <= 166 ? (n + 1 ^ 1) - 1 : n ^ 1);
                this.r.putShort(8);
                this.r.putByte(200);
            }
            label.a(this, this.r, this.r.b - 1, true);
        } else {
            this.r.putByte(n);
            label.a(this, this.r, this.r.b - 1, false);
        }
        if (this.P != null) {
            if (label2 != null) {
                this.visitLabel(label2);
            }
            if (n == 167) {
                this.e();
            }
        }
    }

    public void visitLabel(Label label) {
        this.K |= label.a(this, this.r.b, this.r.a);
        if ((label.a & 1) != 0) {
            return;
        }
        if (this.M == 0) {
            if (this.P != null) {
                if (label.c == this.P.c) {
                    this.P.a |= label.a & 0x10;
                    label.h = this.P.h;
                    return;
                }
                this.a(0, label);
            }
            this.P = label;
            if (label.h == null) {
                label.h = new Frame();
                label.h.b = label;
            }
            if (this.O != null) {
                if (label.c == this.O.c) {
                    this.O.a |= label.a & 0x10;
                    label.h = this.O.h;
                    this.P = this.O;
                    return;
                }
                this.O.i = label;
            }
            this.O = label;
        } else if (this.M == 1) {
            if (this.P != null) {
                this.P.g = this.R;
                this.a(this.Q, label);
            }
            this.P = label;
            this.Q = 0;
            this.R = 0;
            if (this.O != null) {
                this.O.i = label;
            }
            this.O = label;
        }
    }

    public void visitLdcInsn(Object object) {
        int n;
        Item item = this.b.a(object);
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(18, 0, this.b, item);
            } else {
                n = item.b == 5 || item.b == 6 ? this.Q + 2 : this.Q + 1;
                if (n > this.R) {
                    this.R = n;
                }
                this.Q = n;
            }
        }
        n = item.a;
        if (item.b == 5 || item.b == 6) {
            this.r.b(20, n);
        } else if (n >= 256) {
            this.r.b(19, n);
        } else {
            this.r.a(18, n);
        }
    }

    public void visitIincInsn(int n, int n2) {
        int n3;
        if (this.P != null && this.M == 0) {
            this.P.h.a(132, n, null, null);
        }
        if (this.M != 2 && (n3 = n + 1) > this.t) {
            this.t = n3;
        }
        if (n > 255 || n2 > 127 || n2 < -128) {
            this.r.putByte(196).b(132, n).putShort(n2);
        } else {
            this.r.putByte(132).a(n, n2);
        }
    }

    public void visitTableSwitchInsn(int n, int n2, Label label, Label[] labelArray) {
        int n3 = this.r.b;
        this.r.putByte(170);
        this.r.b += (4 - this.r.b % 4) % 4;
        label.a(this, this.r, n3, true);
        this.r.putInt(n).putInt(n2);
        for (int i = 0; i < labelArray.length; ++i) {
            labelArray[i].a(this, this.r, n3, true);
        }
        this.a(label, labelArray);
    }

    public void visitLookupSwitchInsn(Label label, int[] nArray, Label[] labelArray) {
        int n = this.r.b;
        this.r.putByte(171);
        this.r.b += (4 - this.r.b % 4) % 4;
        label.a(this, this.r, n, true);
        this.r.putInt(labelArray.length);
        for (int i = 0; i < labelArray.length; ++i) {
            this.r.putInt(nArray[i]);
            labelArray[i].a(this, this.r, n, true);
        }
        this.a(label, labelArray);
    }

    private void a(Label label, Label[] labelArray) {
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(171, 0, null, null);
                this.a(0, label);
                label.a().a |= 0x10;
                for (int i = 0; i < labelArray.length; ++i) {
                    this.a(0, labelArray[i]);
                    labelArray[i].a().a |= 0x10;
                }
            } else {
                --this.Q;
                this.a(this.Q, label);
                for (int i = 0; i < labelArray.length; ++i) {
                    this.a(this.Q, labelArray[i]);
                }
            }
            this.e();
        }
    }

    public void visitMultiANewArrayInsn(String string, int n) {
        Item item = this.b.a(string);
        if (this.P != null) {
            if (this.M == 0) {
                this.P.h.a(197, n, this.b, item);
            } else {
                this.Q += 1 - n;
            }
        }
        this.r.b(197, item.a).putByte(n);
    }

    public void visitTryCatchBlock(Label label, Label label2, Label label3, String string) {
        ++this.A;
        Handler handler = new Handler();
        handler.a = label;
        handler.b = label2;
        handler.c = label3;
        handler.d = string;
        int n = handler.e = string != null ? this.b.newClass(string) : 0;
        if (this.C == null) {
            this.B = handler;
        } else {
            this.C.f = handler;
        }
        this.C = handler;
    }

    public void visitLocalVariable(String string, String string2, String string3, Label label, Label label2, int n) {
        char c;
        int n2;
        if (string3 != null) {
            if (this.G == null) {
                this.G = new ByteVector();
            }
            ++this.F;
            this.G.putShort(label.c).putShort(label2.c - label.c).putShort(this.b.newUTF8(string)).putShort(this.b.newUTF8(string3)).putShort(n);
        }
        if (this.E == null) {
            this.E = new ByteVector();
        }
        ++this.D;
        this.E.putShort(label.c).putShort(label2.c - label.c).putShort(this.b.newUTF8(string)).putShort(this.b.newUTF8(string2)).putShort(n);
        if (this.M != 2 && (n2 = n + ((c = string2.charAt(0)) == 'J' || c == 'D' ? 2 : 1)) > this.t) {
            this.t = n2;
        }
    }

    public void visitLineNumber(int n, Label label) {
        if (this.I == null) {
            this.I = new ByteVector();
        }
        ++this.H;
        this.I.putShort(label.c);
        this.I.putShort(n);
    }

    public void visitMaxs(int n, int n2) {
        if (this.M == 0) {
            int n3;
            Object object;
            Type[] typeArray;
            Object object2;
            Handler handler = this.B;
            while (handler != null) {
                object2 = handler.a.a();
                typeArray = handler.c.a();
                Label label = handler.b.a();
                object = handler.d == null ? "java/lang/Throwable" : handler.d;
                int n4 = 0x1700000 | this.b.c((String)object);
                typeArray.a |= 0x10;
                while (object2 != label) {
                    Edge edge = new Edge();
                    edge.a = n4;
                    edge.b = typeArray;
                    edge.c = ((Label)object2).j;
                    ((Label)object2).j = edge;
                    object2 = ((Label)object2).i;
                }
                handler = handler.f;
            }
            object2 = this.N.h;
            typeArray = Type.getArgumentTypes(this.f);
            ((Frame)object2).a(this.b, this.c, typeArray, this.t);
            this.b((Frame)object2);
            int n5 = 0;
            object = this.N;
            while (object != null) {
                Object object3 = object;
                object = ((Label)object).k;
                ((Label)object3).k = null;
                object2 = ((Label)object3).h;
                if ((((Label)object3).a & 0x10) != 0) {
                    ((Label)object3).a |= 0x20;
                }
                ((Label)object3).a |= 0x40;
                int n6 = ((Frame)object2).d.length + ((Label)object3).g;
                if (n6 > n5) {
                    n5 = n6;
                }
                Edge edge = ((Label)object3).j;
                while (edge != null) {
                    Label label = edge.b.a();
                    n3 = ((Frame)object2).a(this.b, label.h, edge.a) ? 1 : 0;
                    if (n3 != 0 && label.k == null) {
                        label.k = object;
                        object = label;
                    }
                    edge = edge.c;
                }
            }
            this.s = n5;
            Label label = this.N;
            while (label != null) {
                int n7;
                Label label2;
                int n8;
                object2 = label.h;
                if ((label.a & 0x20) != 0) {
                    this.b((Frame)object2);
                }
                if ((label.a & 0x40) == 0 && (n8 = ((label2 = label.i) == null ? this.r.b : label2.c) - 1) >= (n7 = label.c)) {
                    for (n3 = n7; n3 < n8; ++n3) {
                        this.r.a[n3] = 0;
                    }
                    this.r.a[n8] = -65;
                    this.a(n7, 0, 1);
                    this.z[this.y++] = 0x1700000 | this.b.c("java/lang/Throwable");
                    this.b();
                }
                label = label.i;
            }
        } else if (this.M == 1) {
            Object object;
            Label label;
            Label label3;
            Handler handler = this.B;
            while (handler != null) {
                Label label4 = handler.a;
                label3 = handler.c;
                label = handler.b;
                while (label4 != label) {
                    object = new Edge();
                    ((Edge)object).a = Integer.MAX_VALUE;
                    ((Edge)object).b = label3;
                    if ((label4.a & 0x80) == 0) {
                        ((Edge)object).c = label4.j;
                        label4.j = object;
                    } else {
                        ((Edge)object).c = label4.j.c.c;
                        label4.j.c.c = object;
                    }
                    label4 = label4.i;
                }
                handler = handler.f;
            }
            if (this.L > 0) {
                int n9 = 0;
                this.N.b(null, 1L, this.L);
                label3 = this.N;
                while (label3 != null) {
                    if ((label3.a & 0x80) != 0) {
                        label = label3.j.c.b;
                        if ((label.a & 0x400) == 0) {
                            label.b(null, (long)(++n9) / 32L << 32 | 1L << n9 % 32, this.L);
                        }
                    }
                    label3 = label3.i;
                }
                label3 = this.N;
                while (label3 != null) {
                    if ((label3.a & 0x80) != 0) {
                        label = this.N;
                        while (label != null) {
                            label.a &= 0xFFFFFBFF;
                            label = label.i;
                        }
                        object = label3.j.c.b;
                        ((Label)object).b(label3, 0L, this.L);
                    }
                    label3 = label3.i;
                }
            }
            int n10 = 0;
            label3 = this.N;
            while (label3 != null) {
                label = label3;
                label3 = label3.k;
                int n11 = label.f;
                int n12 = n11 + label.g;
                if (n12 > n10) {
                    n10 = n12;
                }
                Edge edge = label.j;
                if ((label.a & 0x80) != 0) {
                    edge = edge.c;
                }
                while (edge != null) {
                    label = edge.b;
                    if ((label.a & 8) == 0) {
                        label.f = edge.a == Integer.MAX_VALUE ? 1 : n11 + edge.a;
                        label.a |= 8;
                        label.k = label3;
                        label3 = label;
                    }
                    edge = edge.c;
                }
            }
            this.s = n10;
        } else {
            this.s = n;
            this.t = n2;
        }
    }

    public void visitEnd() {
    }

    static int a(String string) {
        int n = 1;
        int n2 = 1;
        while (true) {
            char c;
            if ((c = string.charAt(n2++)) == ')') {
                c = string.charAt(n2);
                return n << 2 | (c == 'V' ? 0 : (c == 'D' || c == 'J' ? 2 : 1));
            }
            if (c == 'L') {
                while (string.charAt(n2++) != ';') {
                }
                ++n;
                continue;
            }
            if (c == '[') {
                while ((c = string.charAt(n2)) == '[') {
                    ++n2;
                }
                if (c != 'D' && c != 'J') continue;
                --n;
                continue;
            }
            if (c == 'D' || c == 'J') {
                n += 2;
                continue;
            }
            ++n;
        }
    }

    private void a(int n, Label label) {
        Edge edge = new Edge();
        edge.a = n;
        edge.b = label;
        edge.c = this.P.j;
        this.P.j = edge;
    }

    private void e() {
        if (this.M == 0) {
            Label label = new Label();
            label.h = new Frame();
            label.h.b = label;
            label.a(this, this.r.b, this.r.a);
            this.O.i = label;
            this.O = label;
        } else {
            this.P.g = this.R;
        }
        this.P = null;
    }

    private void b(Frame frame) {
        int n;
        int n2;
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        int[] nArray = frame.c;
        int[] nArray2 = frame.d;
        for (n2 = 0; n2 < nArray.length; ++n2) {
            n = nArray[n2];
            if (n == 0x1000000) {
                ++n3;
            } else {
                n4 += n3 + 1;
                n3 = 0;
            }
            if (n != 0x1000004 && n != 0x1000003) continue;
            ++n2;
        }
        for (n2 = 0; n2 < nArray2.length; ++n2) {
            n = nArray2[n2];
            ++n5;
            if (n != 0x1000004 && n != 0x1000003) continue;
            ++n2;
        }
        this.a(frame.b.c, n4, n5);
        n2 = 0;
        while (n4 > 0) {
            n = nArray[n2];
            this.z[this.y++] = n;
            if (n == 0x1000004 || n == 0x1000003) {
                ++n2;
            }
            ++n2;
            --n4;
        }
        for (n2 = 0; n2 < nArray2.length; ++n2) {
            n = nArray2[n2];
            this.z[this.y++] = n;
            if (n != 0x1000004 && n != 0x1000003) continue;
            ++n2;
        }
        this.b();
    }

    private void a(int n, int n2, int n3) {
        int n4 = 3 + n2 + n3;
        if (this.z == null || this.z.length < n4) {
            this.z = new int[n4];
        }
        this.z[0] = n;
        this.z[1] = n2;
        this.z[2] = n3;
        this.y = 3;
    }

    private void b() {
        if (this.x != null) {
            if (this.v == null) {
                this.v = new ByteVector();
            }
            this.c();
            ++this.u;
        }
        this.x = this.z;
        this.z = null;
    }

    private void c() {
        int n = this.z[1];
        int n2 = this.z[2];
        if ((this.b.b & 0xFFFF) < 50) {
            this.v.putShort(this.z[0]).putShort(n);
            this.a(3, 3 + n);
            this.v.putShort(n2);
            this.a(3 + n, 3 + n + n2);
            return;
        }
        int n3 = this.x[1];
        int n4 = 255;
        int n5 = 0;
        int n6 = this.u == 0 ? this.z[0] : this.z[0] - this.x[0] - 1;
        if (n2 == 0) {
            n5 = n - n3;
            switch (n5) {
                case -3: 
                case -2: 
                case -1: {
                    n4 = 248;
                    n3 = n;
                    break;
                }
                case 0: {
                    n4 = n6 < 64 ? 0 : 251;
                    break;
                }
                case 1: 
                case 2: 
                case 3: {
                    n4 = 252;
                }
            }
        } else if (n == n3 && n2 == 1) {
            int n7 = n4 = n6 < 63 ? 64 : 247;
        }
        if (n4 != 255) {
            int n8 = 3;
            for (int i = 0; i < n3; ++i) {
                if (this.z[n8] != this.x[n8]) {
                    n4 = 255;
                    break;
                }
                ++n8;
            }
        }
        switch (n4) {
            case 0: {
                this.v.putByte(n6);
                break;
            }
            case 64: {
                this.v.putByte(64 + n6);
                this.a(3 + n, 4 + n);
                break;
            }
            case 247: {
                this.v.putByte(247).putShort(n6);
                this.a(3 + n, 4 + n);
                break;
            }
            case 251: {
                this.v.putByte(251).putShort(n6);
                break;
            }
            case 248: {
                this.v.putByte(251 + n5).putShort(n6);
                break;
            }
            case 252: {
                this.v.putByte(251 + n5).putShort(n6);
                this.a(3 + n3, 3 + n);
                break;
            }
            default: {
                this.v.putByte(255).putShort(n6).putShort(n);
                this.a(3, 3 + n);
                this.v.putShort(n2);
                this.a(3 + n, 3 + n + n2);
            }
        }
    }

    private void a(int n, int n2) {
        for (int i = n; i < n2; ++i) {
            int n3 = this.z[i];
            int n4 = n3 & 0xF0000000;
            if (n4 == 0) {
                int n5 = n3 & 0xFFFFF;
                switch (n3 & 0xFF00000) {
                    case 0x1700000: {
                        this.v.putByte(7).putShort(this.b.newClass(this.b.E[n5].g));
                        break;
                    }
                    case 0x1800000: {
                        this.v.putByte(8).putShort(this.b.E[n5].c);
                        break;
                    }
                    default: {
                        this.v.putByte(n5);
                        break;
                    }
                }
                continue;
            }
            StringBuffer stringBuffer = new StringBuffer();
            n4 >>= 28;
            while (n4-- > 0) {
                stringBuffer.append('[');
            }
            if ((n3 & 0xFF00000) == 0x1700000) {
                stringBuffer.append('L');
                stringBuffer.append(this.b.E[n3 & 0xFFFFF].g);
                stringBuffer.append(';');
            } else {
                switch (n3 & 0xF) {
                    case 1: {
                        stringBuffer.append('I');
                        break;
                    }
                    case 2: {
                        stringBuffer.append('F');
                        break;
                    }
                    case 3: {
                        stringBuffer.append('D');
                        break;
                    }
                    case 9: {
                        stringBuffer.append('Z');
                        break;
                    }
                    case 10: {
                        stringBuffer.append('B');
                        break;
                    }
                    case 11: {
                        stringBuffer.append('C');
                        break;
                    }
                    case 12: {
                        stringBuffer.append('S');
                        break;
                    }
                    default: {
                        stringBuffer.append('J');
                    }
                }
            }
            this.v.putByte(7).putShort(this.b.newClass(stringBuffer.toString()));
        }
    }

    private void a(Object object) {
        if (object instanceof String) {
            this.v.putByte(7).putShort(this.b.newClass((String)object));
        } else if (object instanceof Integer) {
            this.v.putByte((Integer)object);
        } else {
            this.v.putByte(8).putShort(((Label)object).c);
        }
    }

    final int a() {
        int n;
        if (this.h != 0) {
            return 6 + this.i;
        }
        if (this.K) {
            this.d();
        }
        int n2 = 8;
        if (this.r.b > 0) {
            this.b.newUTF8("Code");
            n2 += 18 + this.r.b + 8 * this.A;
            if (this.E != null) {
                this.b.newUTF8("LocalVariableTable");
                n2 += 8 + this.E.b;
            }
            if (this.G != null) {
                this.b.newUTF8("LocalVariableTypeTable");
                n2 += 8 + this.G.b;
            }
            if (this.I != null) {
                this.b.newUTF8("LineNumberTable");
                n2 += 8 + this.I.b;
            }
            if (this.v != null) {
                n = (this.b.b & 0xFFFF) >= 50 ? 1 : 0;
                this.b.newUTF8(n != 0 ? "StackMapTable" : "StackMap");
                n2 += 8 + this.v.b;
            }
            if (this.J != null) {
                n2 += this.J.a(this.b, this.r.a, this.r.b, this.s, this.t);
            }
        }
        if (this.j > 0) {
            this.b.newUTF8("Exceptions");
            n2 += 8 + 2 * this.j;
        }
        if ((this.c & 0x1000) != 0 && (this.b.b & 0xFFFF) < 49) {
            this.b.newUTF8("Synthetic");
            n2 += 6;
        }
        if ((this.c & 0x20000) != 0) {
            this.b.newUTF8("Deprecated");
            n2 += 6;
        }
        if (this.g != null) {
            this.b.newUTF8("Signature");
            this.b.newUTF8(this.g);
            n2 += 8;
        }
        if (this.l != null) {
            this.b.newUTF8("AnnotationDefault");
            n2 += 6 + this.l.b;
        }
        if (this.m != null) {
            this.b.newUTF8("RuntimeVisibleAnnotations");
            n2 += 8 + this.m.a();
        }
        if (this.n != null) {
            this.b.newUTF8("RuntimeInvisibleAnnotations");
            n2 += 8 + this.n.a();
        }
        if (this.o != null) {
            this.b.newUTF8("RuntimeVisibleParameterAnnotations");
            n2 += 7 + 2 * (this.o.length - this.S);
            for (n = this.o.length - 1; n >= this.S; --n) {
                n2 += this.o[n] == null ? 0 : this.o[n].a();
            }
        }
        if (this.p != null) {
            this.b.newUTF8("RuntimeInvisibleParameterAnnotations");
            n2 += 7 + 2 * (this.p.length - this.S);
            for (n = this.p.length - 1; n >= this.S; --n) {
                n2 += this.p[n] == null ? 0 : this.p[n].a();
            }
        }
        if (this.q != null) {
            n2 += this.q.a(this.b, null, 0, -1, -1);
        }
        return n2;
    }

    final void a(ByteVector byteVector) {
        int n;
        byteVector.putShort(this.c).putShort(this.d).putShort(this.e);
        if (this.h != 0) {
            byteVector.putByteArray(this.b.J.b, this.h, this.i);
            return;
        }
        int n2 = 0;
        if (this.r.b > 0) {
            ++n2;
        }
        if (this.j > 0) {
            ++n2;
        }
        if ((this.c & 0x1000) != 0 && (this.b.b & 0xFFFF) < 49) {
            ++n2;
        }
        if ((this.c & 0x20000) != 0) {
            ++n2;
        }
        if (this.g != null) {
            ++n2;
        }
        if (this.l != null) {
            ++n2;
        }
        if (this.m != null) {
            ++n2;
        }
        if (this.n != null) {
            ++n2;
        }
        if (this.o != null) {
            ++n2;
        }
        if (this.p != null) {
            ++n2;
        }
        if (this.q != null) {
            n2 += this.q.a();
        }
        byteVector.putShort(n2);
        if (this.r.b > 0) {
            n = 12 + this.r.b + 8 * this.A;
            if (this.E != null) {
                n += 8 + this.E.b;
            }
            if (this.G != null) {
                n += 8 + this.G.b;
            }
            if (this.I != null) {
                n += 8 + this.I.b;
            }
            if (this.v != null) {
                n += 8 + this.v.b;
            }
            if (this.J != null) {
                n += this.J.a(this.b, this.r.a, this.r.b, this.s, this.t);
            }
            byteVector.putShort(this.b.newUTF8("Code")).putInt(n);
            byteVector.putShort(this.s).putShort(this.t);
            byteVector.putInt(this.r.b).putByteArray(this.r.a, 0, this.r.b);
            byteVector.putShort(this.A);
            if (this.A > 0) {
                Handler handler = this.B;
                while (handler != null) {
                    byteVector.putShort(handler.a.c).putShort(handler.b.c).putShort(handler.c.c).putShort(handler.e);
                    handler = handler.f;
                }
            }
            n2 = 0;
            if (this.E != null) {
                ++n2;
            }
            if (this.G != null) {
                ++n2;
            }
            if (this.I != null) {
                ++n2;
            }
            if (this.v != null) {
                ++n2;
            }
            if (this.J != null) {
                n2 += this.J.a();
            }
            byteVector.putShort(n2);
            if (this.E != null) {
                byteVector.putShort(this.b.newUTF8("LocalVariableTable"));
                byteVector.putInt(this.E.b + 2).putShort(this.D);
                byteVector.putByteArray(this.E.a, 0, this.E.b);
            }
            if (this.G != null) {
                byteVector.putShort(this.b.newUTF8("LocalVariableTypeTable"));
                byteVector.putInt(this.G.b + 2).putShort(this.F);
                byteVector.putByteArray(this.G.a, 0, this.G.b);
            }
            if (this.I != null) {
                byteVector.putShort(this.b.newUTF8("LineNumberTable"));
                byteVector.putInt(this.I.b + 2).putShort(this.H);
                byteVector.putByteArray(this.I.a, 0, this.I.b);
            }
            if (this.v != null) {
                boolean bl = (this.b.b & 0xFFFF) >= 50;
                byteVector.putShort(this.b.newUTF8(bl ? "StackMapTable" : "StackMap"));
                byteVector.putInt(this.v.b + 2).putShort(this.u);
                byteVector.putByteArray(this.v.a, 0, this.v.b);
            }
            if (this.J != null) {
                this.J.a(this.b, this.r.a, this.r.b, this.t, this.s, byteVector);
            }
        }
        if (this.j > 0) {
            byteVector.putShort(this.b.newUTF8("Exceptions")).putInt(2 * this.j + 2);
            byteVector.putShort(this.j);
            for (n = 0; n < this.j; ++n) {
                byteVector.putShort(this.k[n]);
            }
        }
        if ((this.c & 0x1000) != 0 && (this.b.b & 0xFFFF) < 49) {
            byteVector.putShort(this.b.newUTF8("Synthetic")).putInt(0);
        }
        if ((this.c & 0x20000) != 0) {
            byteVector.putShort(this.b.newUTF8("Deprecated")).putInt(0);
        }
        if (this.g != null) {
            byteVector.putShort(this.b.newUTF8("Signature")).putInt(2).putShort(this.b.newUTF8(this.g));
        }
        if (this.l != null) {
            byteVector.putShort(this.b.newUTF8("AnnotationDefault"));
            byteVector.putInt(this.l.b);
            byteVector.putByteArray(this.l.a, 0, this.l.b);
        }
        if (this.m != null) {
            byteVector.putShort(this.b.newUTF8("RuntimeVisibleAnnotations"));
            this.m.a(byteVector);
        }
        if (this.n != null) {
            byteVector.putShort(this.b.newUTF8("RuntimeInvisibleAnnotations"));
            this.n.a(byteVector);
        }
        if (this.o != null) {
            byteVector.putShort(this.b.newUTF8("RuntimeVisibleParameterAnnotations"));
            AnnotationWriter.a(this.o, this.S, byteVector);
        }
        if (this.p != null) {
            byteVector.putShort(this.b.newUTF8("RuntimeInvisibleParameterAnnotations"));
            AnnotationWriter.a(this.p, this.S, byteVector);
        }
        if (this.q != null) {
            this.q.a(this.b, null, 0, -1, -1, byteVector);
        }
    }

    private void d() {
        int n;
        Object object;
        Object object2;
        int n2;
        int n3;
        int n4;
        int n5;
        byte[] byArray = this.r.a;
        Object object3 = new int[]{};
        int[] nArray = new int[]{};
        boolean[] blArray = new boolean[this.r.b];
        int n6 = 3;
        do {
            if (n6 == 3) {
                n6 = 2;
            }
            n5 = 0;
            while (n5 < byArray.length) {
                int n7 = byArray[n5] & 0xFF;
                n4 = 0;
                switch (ClassWriter.a[n7]) {
                    case 0: 
                    case 4: {
                        ++n5;
                        break;
                    }
                    case 8: {
                        if (n7 > 201) {
                            n7 = n7 < 218 ? n7 - 49 : n7 - 20;
                            n3 = n5 + MethodWriter.c(byArray, n5 + 1);
                        } else {
                            n3 = n5 + MethodWriter.b(byArray, n5 + 1);
                        }
                        n2 = MethodWriter.a(object3, nArray, n5, n3);
                        if (!(n2 >= Short.MIN_VALUE && n2 <= Short.MAX_VALUE || blArray[n5])) {
                            n4 = n7 == 167 || n7 == 168 ? 2 : 5;
                            blArray[n5] = true;
                        }
                        n5 += 3;
                        break;
                    }
                    case 9: {
                        n5 += 5;
                        break;
                    }
                    case 13: {
                        if (n6 == 1) {
                            n2 = MethodWriter.a(object3, nArray, 0, n5);
                            n4 = -(n2 & 3);
                        } else if (!blArray[n5]) {
                            n4 = n5 & 3;
                            blArray[n5] = true;
                        }
                        n5 = n5 + 4 - (n5 & 3);
                        n5 += 4 * (MethodWriter.a(byArray, n5 + 8) - MethodWriter.a(byArray, n5 + 4) + 1) + 12;
                        break;
                    }
                    case 14: {
                        if (n6 == 1) {
                            n2 = MethodWriter.a(object3, nArray, 0, n5);
                            n4 = -(n2 & 3);
                        } else if (!blArray[n5]) {
                            n4 = n5 & 3;
                            blArray[n5] = true;
                        }
                        n5 = n5 + 4 - (n5 & 3);
                        n5 += 8 * MethodWriter.a(byArray, n5 + 4) + 8;
                        break;
                    }
                    case 16: {
                        n7 = byArray[n5 + 1] & 0xFF;
                        if (n7 == 132) {
                            n5 += 6;
                            break;
                        }
                        n5 += 4;
                        break;
                    }
                    case 1: 
                    case 3: 
                    case 10: {
                        n5 += 2;
                        break;
                    }
                    case 2: 
                    case 5: 
                    case 6: 
                    case 11: 
                    case 12: {
                        n5 += 3;
                        break;
                    }
                    case 7: {
                        n5 += 5;
                        break;
                    }
                    default: {
                        n5 += 4;
                    }
                }
                if (n4 == 0) continue;
                object2 = new int[((int[])object3).length + 1];
                object = new int[nArray.length + 1];
                System.arraycopy(object3, 0, object2, 0, ((int[])object3).length);
                System.arraycopy(nArray, 0, object, 0, nArray.length);
                object2[((int[])object3).length] = n5;
                object[nArray.length] = n4;
                object3 = object2;
                nArray = object;
                if (n4 <= 0) continue;
                n6 = 3;
            }
            if (n6 >= 3) continue;
            --n6;
        } while (n6 != 0);
        ByteVector byteVector = new ByteVector(this.r.b);
        n5 = 0;
        block24: while (n5 < this.r.b) {
            n4 = byArray[n5] & 0xFF;
            switch (ClassWriter.a[n4]) {
                case 0: 
                case 4: {
                    byteVector.putByte(n4);
                    ++n5;
                    continue block24;
                }
                case 8: {
                    if (n4 > 201) {
                        n4 = n4 < 218 ? n4 - 49 : n4 - 20;
                        n3 = n5 + MethodWriter.c(byArray, n5 + 1);
                    } else {
                        n3 = n5 + MethodWriter.b(byArray, n5 + 1);
                    }
                    n2 = MethodWriter.a(object3, nArray, n5, n3);
                    if (blArray[n5]) {
                        if (n4 == 167) {
                            byteVector.putByte(200);
                        } else if (n4 == 168) {
                            byteVector.putByte(201);
                        } else {
                            byteVector.putByte(n4 <= 166 ? (n4 + 1 ^ 1) - 1 : n4 ^ 1);
                            byteVector.putShort(8);
                            byteVector.putByte(200);
                            n2 -= 3;
                        }
                        byteVector.putInt(n2);
                    } else {
                        byteVector.putByte(n4);
                        byteVector.putShort(n2);
                    }
                    n5 += 3;
                    continue block24;
                }
                case 9: {
                    n3 = n5 + MethodWriter.a(byArray, n5 + 1);
                    n2 = MethodWriter.a(object3, nArray, n5, n3);
                    byteVector.putByte(n4);
                    byteVector.putInt(n2);
                    n5 += 5;
                    continue block24;
                }
                case 13: {
                    int n8 = n5;
                    n5 = n5 + 4 - (n8 & 3);
                    byteVector.putByte(170);
                    byteVector.b += (4 - byteVector.b % 4) % 4;
                    n3 = n8 + MethodWriter.a(byArray, n5);
                    n2 = MethodWriter.a(object3, nArray, n8, n3);
                    byteVector.putInt(n2);
                    int n9 = MethodWriter.a(byArray, n5 += 4);
                    byteVector.putInt(n9);
                    byteVector.putInt(MethodWriter.a(byArray, (n5 += 4) - 4));
                    for (n9 = MethodWriter.a(byArray, n5 += 4) - n9 + 1; n9 > 0; --n9) {
                        n3 = n8 + MethodWriter.a(byArray, n5);
                        n5 += 4;
                        n2 = MethodWriter.a(object3, nArray, n8, n3);
                        byteVector.putInt(n2);
                    }
                    continue block24;
                }
                case 14: {
                    int n9;
                    int n8 = n5;
                    n5 = n5 + 4 - (n8 & 3);
                    byteVector.putByte(171);
                    byteVector.b += (4 - byteVector.b % 4) % 4;
                    n3 = n8 + MethodWriter.a(byArray, n5);
                    n2 = MethodWriter.a(object3, nArray, n8, n3);
                    byteVector.putInt(n2);
                    n5 += 4;
                    byteVector.putInt(n9);
                    for (n9 = MethodWriter.a(byArray, n5 += 4); n9 > 0; --n9) {
                        byteVector.putInt(MethodWriter.a(byArray, n5));
                        n3 = n8 + MethodWriter.a(byArray, n5 += 4);
                        n5 += 4;
                        n2 = MethodWriter.a(object3, nArray, n8, n3);
                        byteVector.putInt(n2);
                    }
                    continue block24;
                }
                case 16: {
                    n4 = byArray[n5 + 1] & 0xFF;
                    if (n4 == 132) {
                        byteVector.putByteArray(byArray, n5, 6);
                        n5 += 6;
                        continue block24;
                    }
                    byteVector.putByteArray(byArray, n5, 4);
                    n5 += 4;
                    continue block24;
                }
                case 1: 
                case 3: 
                case 10: {
                    byteVector.putByteArray(byArray, n5, 2);
                    n5 += 2;
                    continue block24;
                }
                case 2: 
                case 5: 
                case 6: 
                case 11: 
                case 12: {
                    byteVector.putByteArray(byArray, n5, 3);
                    n5 += 3;
                    continue block24;
                }
                case 7: {
                    byteVector.putByteArray(byArray, n5, 5);
                    n5 += 5;
                    continue block24;
                }
            }
            byteVector.putByteArray(byArray, n5, 4);
            n5 += 4;
        }
        if (this.u > 0) {
            if (this.M == 0) {
                this.u = 0;
                this.v = null;
                this.x = null;
                this.z = null;
                Frame frame = new Frame();
                frame.b = this.N;
                object2 = Type.getArgumentTypes(this.f);
                frame.a(this.b, this.c, (Type[])object2, this.t);
                this.b(frame);
                object = this.N;
                while (object != null) {
                    n5 = object.c - 3;
                    if ((object.a & 0x20) != 0 || n5 >= 0 && blArray[n5]) {
                        MethodWriter.a(object3, nArray, (Label)object);
                        this.b(object.h);
                    }
                    object = object.i;
                }
            } else {
                this.b.I = true;
            }
        }
        Handler handler = this.B;
        while (handler != null) {
            MethodWriter.a(object3, nArray, handler.a);
            MethodWriter.a(object3, nArray, handler.b);
            MethodWriter.a(object3, nArray, handler.c);
            handler = handler.f;
        }
        for (n = 0; n < 2; ++n) {
            object2 = n == 0 ? this.E : this.G;
            if (object2 == null) continue;
            byArray = ((ByteVector)object2).a;
            for (n5 = 0; n5 < ((ByteVector)object2).b; n5 += 10) {
                n3 = MethodWriter.c(byArray, n5);
                n2 = MethodWriter.a(object3, nArray, 0, n3);
                MethodWriter.a(byArray, n5, n2);
                n2 = MethodWriter.a(object3, nArray, 0, n3 += MethodWriter.c(byArray, n5 + 2)) - n2;
                MethodWriter.a(byArray, n5 + 2, n2);
            }
        }
        if (this.I != null) {
            byArray = this.I.a;
            for (n5 = 0; n5 < this.I.b; n5 += 4) {
                MethodWriter.a(byArray, n5, MethodWriter.a(object3, nArray, 0, MethodWriter.c(byArray, n5)));
            }
        }
        object2 = this.J;
        while (object2 != null) {
            object = ((Attribute)object2).getLabels();
            if (object != null) {
                for (n = ((int[])object).length - 1; n >= 0; --n) {
                    MethodWriter.a(object3, nArray, (Label)object[n]);
                }
            }
            object2 = ((Attribute)object2).a;
        }
        this.r = byteVector;
    }

    static int c(byte[] byArray, int n) {
        return (byArray[n] & 0xFF) << 8 | byArray[n + 1] & 0xFF;
    }

    static short b(byte[] byArray, int n) {
        return (short)((byArray[n] & 0xFF) << 8 | byArray[n + 1] & 0xFF);
    }

    static int a(byte[] byArray, int n) {
        return (byArray[n] & 0xFF) << 24 | (byArray[n + 1] & 0xFF) << 16 | (byArray[n + 2] & 0xFF) << 8 | byArray[n + 3] & 0xFF;
    }

    static void a(byte[] byArray, int n, int n2) {
        byArray[n] = (byte)(n2 >>> 8);
        byArray[n + 1] = (byte)n2;
    }

    static int a(int[] nArray, int[] nArray2, int n, int n2) {
        int n3 = n2 - n;
        for (int i = 0; i < nArray.length; ++i) {
            if (n < nArray[i] && nArray[i] <= n2) {
                n3 += nArray2[i];
                continue;
            }
            if (n2 >= nArray[i] || nArray[i] > n) continue;
            n3 -= nArray2[i];
        }
        return n3;
    }

    static void a(int[] nArray, int[] nArray2, Label label) {
        if ((label.a & 4) == 0) {
            label.c = MethodWriter.a(nArray, nArray2, 0, label.c);
            label.a |= 4;
        }
    }
}

