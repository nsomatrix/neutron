/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm;

import java.io.IOException;
import java.io.InputStream;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Item;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.MethodWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class ClassReader {
    public static final int SKIP_CODE = 1;
    public static final int SKIP_DEBUG = 2;
    public static final int SKIP_FRAMES = 4;
    public static final int EXPAND_FRAMES = 8;
    public final byte[] b;
    private final int[] a;
    private final String[] c;
    private final int d;
    public final int header;

    public ClassReader(byte[] byArray) {
        this(byArray, 0, byArray.length);
    }

    public ClassReader(byte[] byArray, int n, int n2) {
        this.b = byArray;
        this.a = new int[this.readUnsignedShort(n + 8)];
        int n3 = this.a.length;
        this.c = new String[n3];
        int n4 = 0;
        int n5 = n + 10;
        for (int i = 1; i < n3; ++i) {
            int n6;
            this.a[i] = n5 + 1;
            switch (byArray[n5]) {
                case 3: 
                case 4: 
                case 9: 
                case 10: 
                case 11: 
                case 12: {
                    n6 = 5;
                    break;
                }
                case 5: 
                case 6: {
                    n6 = 9;
                    ++i;
                    break;
                }
                case 1: {
                    n6 = 3 + this.readUnsignedShort(n5 + 1);
                    if (n6 <= n4) break;
                    n4 = n6;
                    break;
                }
                default: {
                    n6 = 3;
                }
            }
            n5 += n6;
        }
        this.d = n4;
        this.header = n5;
    }

    public int getAccess() {
        return this.readUnsignedShort(this.header);
    }

    public String getClassName() {
        return this.readClass(this.header + 2, new char[this.d]);
    }

    public String getSuperName() {
        int n = this.a[this.readUnsignedShort(this.header + 4)];
        return n == 0 ? null : this.readUTF8(n, new char[this.d]);
    }

    public String[] getInterfaces() {
        int n = this.header + 6;
        int n2 = this.readUnsignedShort(n);
        String[] stringArray = new String[n2];
        if (n2 > 0) {
            char[] cArray = new char[this.d];
            for (int i = 0; i < n2; ++i) {
                stringArray[i] = this.readClass(n += 2, cArray);
            }
        }
        return stringArray;
    }

    void a(ClassWriter classWriter) {
        int n;
        char[] cArray = new char[this.d];
        int n2 = this.a.length;
        Item[] itemArray = new Item[n2];
        for (n = 1; n < n2; ++n) {
            int n3 = this.a[n];
            byte by = this.b[n3 - 1];
            Item item = new Item(n);
            switch (by) {
                case 9: 
                case 10: 
                case 11: {
                    int n4 = this.a[this.readUnsignedShort(n3 + 2)];
                    item.a(by, this.readClass(n3, cArray), this.readUTF8(n4, cArray), this.readUTF8(n4 + 2, cArray));
                    break;
                }
                case 3: {
                    item.a(this.readInt(n3));
                    break;
                }
                case 4: {
                    item.a(Float.intBitsToFloat(this.readInt(n3)));
                    break;
                }
                case 12: {
                    item.a(by, this.readUTF8(n3, cArray), this.readUTF8(n3 + 2, cArray), null);
                    break;
                }
                case 5: {
                    item.a(this.readLong(n3));
                    ++n;
                    break;
                }
                case 6: {
                    item.a(Double.longBitsToDouble(this.readLong(n3)));
                    ++n;
                    break;
                }
                case 1: {
                    String string = this.c[n];
                    if (string == null) {
                        n3 = this.a[n];
                        string = this.c[n] = this.a(n3 + 2, this.readUnsignedShort(n3), cArray);
                    }
                    item.a(by, string, null, null);
                    break;
                }
                default: {
                    item.a(by, this.readUTF8(n3, cArray), null, null);
                }
            }
            int n5 = item.j % itemArray.length;
            item.k = itemArray[n5];
            itemArray[n5] = item;
        }
        n = this.a[1] - 1;
        classWriter.d.putByteArray(this.b, n, this.header - n);
        classWriter.e = itemArray;
        classWriter.f = (int)(0.75 * (double)n2);
        classWriter.c = n2;
    }

    public ClassReader(InputStream inputStream) throws IOException {
        this(ClassReader.a(inputStream));
    }

    public ClassReader(String string) throws IOException {
        this(ClassLoader.getSystemResourceAsStream(string.replace('.', '/') + ".class"));
    }

    private static byte[] a(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IOException("Class not found");
        }
        byte[] byArray = new byte[inputStream.available()];
        int n = 0;
        while (true) {
            byte[] byArray2;
            int n2;
            if ((n2 = inputStream.read(byArray, n, byArray.length - n)) == -1) {
                if (n < byArray.length) {
                    byArray2 = new byte[n];
                    System.arraycopy(byArray, 0, byArray2, 0, n);
                    byArray = byArray2;
                }
                return byArray;
            }
            if ((n += n2) != byArray.length) continue;
            byArray2 = new byte[byArray.length + 1000];
            System.arraycopy(byArray, 0, byArray2, 0, n);
            byArray = byArray2;
        }
    }

    public void accept(ClassVisitor classVisitor, int n) {
        this.accept(classVisitor, new Attribute[0], n);
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public void accept(ClassVisitor var1_1, Attribute[] var2_2, int var3_3) {
        var4_4 = this.b;
        var5_5 = new char[this.d];
        var6_6 = 0;
        var7_7 = 0;
        var8_8 = null;
        var9_9 = this.header;
        var10_10 = this.readUnsignedShort(var9_9);
        var11_11 = this.readClass(var9_9 + 2, var5_5);
        var12_12 = this.a[this.readUnsignedShort(var9_9 + 4)];
        var13_13 = var12_12 == 0 ? null : this.readUTF8(var12_12, var5_5);
        var14_14 = new String[this.readUnsignedShort(var9_9 + 6)];
        var15_15 = 0;
        var9_9 += 8;
        for (var16_16 = 0; var16_16 < var14_14.length; ++var16_16) {
            var14_14[var16_16] = this.readClass(var9_9, var5_5);
            var9_9 += 2;
        }
        var17_17 = (var3_3 & 1) != 0;
        var18_18 = (var3_3 & 2) != 0;
        var19_19 = (var3_3 & 8) != 0;
        var12_12 = var9_9;
        var16_16 = this.readUnsignedShort(var12_12);
        var12_12 += 2;
        while (var16_16 > 0) {
            var20_20 = this.readUnsignedShort(var12_12 + 6);
            var12_12 += 8;
            while (var20_20 > 0) {
                var12_12 += 6 + this.readInt(var12_12 + 2);
                --var20_20;
            }
            --var16_16;
        }
        var16_16 = this.readUnsignedShort(var12_12);
        var12_12 += 2;
        while (var16_16 > 0) {
            var20_20 = this.readUnsignedShort(var12_12 + 6);
            var12_12 += 8;
            while (var20_20 > 0) {
                var12_12 += 6 + this.readInt(var12_12 + 2);
                --var20_20;
            }
            --var16_16;
        }
        var21_21 = null;
        var22_22 = null;
        var23_23 = null;
        var24_24 = null;
        var25_25 = null;
        var26_26 = null;
        var16_16 = this.readUnsignedShort(var12_12);
        var12_12 += 2;
        while (var16_16 > 0) {
            var27_27 = this.readUTF8(var12_12, var5_5);
            if ("SourceFile".equals(var27_27)) {
                var22_22 = this.readUTF8(var12_12 + 6, var5_5);
            } else if ("InnerClasses".equals(var27_27)) {
                var15_15 = var12_12 + 6;
            } else if ("EnclosingMethod".equals(var27_27)) {
                var24_24 = this.readClass(var12_12 + 6, var5_5);
                var28_28 = this.readUnsignedShort(var12_12 + 8);
                if (var28_28 != 0) {
                    var25_25 = this.readUTF8(this.a[var28_28], var5_5);
                    var26_26 = this.readUTF8(this.a[var28_28] + 2, var5_5);
                }
            } else if ("Signature".equals(var27_27)) {
                var21_21 = this.readUTF8(var12_12 + 6, var5_5);
            } else if ("RuntimeVisibleAnnotations".equals(var27_27)) {
                var6_6 = var12_12 + 6;
            } else if ("Deprecated".equals(var27_27)) {
                var10_10 |= 131072;
            } else if ("Synthetic".equals(var27_27)) {
                var10_10 |= 4096;
            } else if ("SourceDebugExtension".equals(var27_27)) {
                var28_28 = this.readInt(var12_12 + 2);
                var23_23 = this.a(var12_12 + 6, var28_28, new char[var28_28]);
            } else if ("RuntimeInvisibleAnnotations".equals(var27_27)) {
                var7_7 = var12_12 + 6;
            } else {
                var29_29 = this.a(var2_2, var27_27, var12_12 + 6, this.readInt(var12_12 + 2), var5_5, -1, null);
                if (var29_29 != null) {
                    var29_29.a = var8_8;
                    var8_8 = var29_29;
                }
            }
            var12_12 += 6 + this.readInt(var12_12 + 2);
            --var16_16;
        }
        var1_1.visit(this.readInt(4), var10_10, var11_11, var21_21, var13_13, var14_14);
        if (!(var18_18 || var22_22 == null && var23_23 == null)) {
            var1_1.visitSource(var22_22, var23_23);
        }
        if (var24_24 != null) {
            var1_1.visitOuterClass(var24_24, var25_25, var26_26);
        }
        for (var16_16 = 1; var16_16 >= 0; --var16_16) {
            v0 = var12_12 = var16_16 == 0 ? var7_7 : var6_6;
            if (var12_12 == 0) continue;
            var20_20 = this.readUnsignedShort(var12_12);
            var12_12 += 2;
            while (var20_20 > 0) {
                var12_12 = this.a(var12_12 + 2, var5_5, true, var1_1.visitAnnotation(this.readUTF8(var12_12, var5_5), var16_16 != 0));
                --var20_20;
            }
        }
        while (var8_8 != null) {
            var29_29 = var8_8.a;
            var8_8.a = null;
            var1_1.visitAttribute(var8_8);
            var8_8 = var29_29;
        }
        if (var15_15 != 0) {
            var16_16 = this.readUnsignedShort(var15_15);
            var15_15 += 2;
            while (var16_16 > 0) {
                var1_1.visitInnerClass(this.readUnsignedShort(var15_15) == 0 ? null : this.readClass(var15_15, var5_5), this.readUnsignedShort(var15_15 + 2) == 0 ? null : this.readClass(var15_15 + 2, var5_5), this.readUnsignedShort(var15_15 + 4) == 0 ? null : this.readUTF8(var15_15 + 4, var5_5), this.readUnsignedShort(var15_15 + 6));
                var15_15 += 8;
                --var16_16;
            }
        }
        var16_16 = this.readUnsignedShort(var9_9);
        var9_9 += 2;
        while (var16_16 > 0) {
            var10_10 = this.readUnsignedShort(var9_9);
            var11_11 = this.readUTF8(var9_9 + 2, var5_5);
            var30_30 = this.readUTF8(var9_9 + 4, var5_5);
            var28_28 = 0;
            var21_21 = null;
            var6_6 = 0;
            var7_7 = 0;
            var8_8 = null;
            var20_20 = this.readUnsignedShort(var9_9 + 6);
            var9_9 += 8;
            while (var20_20 > 0) {
                var27_27 = this.readUTF8(var9_9, var5_5);
                if ("ConstantValue".equals(var27_27)) {
                    var28_28 = this.readUnsignedShort(var9_9 + 6);
                } else if ("Signature".equals(var27_27)) {
                    var21_21 = this.readUTF8(var9_9 + 6, var5_5);
                } else if ("Deprecated".equals(var27_27)) {
                    var10_10 |= 131072;
                } else if ("Synthetic".equals(var27_27)) {
                    var10_10 |= 4096;
                } else if ("RuntimeVisibleAnnotations".equals(var27_27)) {
                    var6_6 = var9_9 + 6;
                } else if ("RuntimeInvisibleAnnotations".equals(var27_27)) {
                    var7_7 = var9_9 + 6;
                } else {
                    var29_29 = this.a(var2_2, var27_27, var9_9 + 6, this.readInt(var9_9 + 2), var5_5, -1, null);
                    if (var29_29 != null) {
                        var29_29.a = var8_8;
                        var8_8 = var29_29;
                    }
                }
                var9_9 += 6 + this.readInt(var9_9 + 2);
                --var20_20;
            }
            var31_31 = var1_1.visitField(var10_10, var11_11, var30_30, var21_21, var28_28 == 0 ? null : this.readConst(var28_28, var5_5));
            if (var31_31 != null) {
                for (var20_20 = 1; var20_20 >= 0; --var20_20) {
                    v1 = var12_12 = var20_20 == 0 ? var7_7 : var6_6;
                    if (var12_12 == 0) continue;
                    var32_33 = this.readUnsignedShort(var12_12);
                    var12_12 += 2;
                    while (var32_33 > 0) {
                        var12_12 = this.a(var12_12 + 2, var5_5, true, var31_31.visitAnnotation(this.readUTF8(var12_12, var5_5), var20_20 != 0));
                        --var32_33;
                    }
                }
                while (var8_8 != null) {
                    var29_29 = var8_8.a;
                    var8_8.a = null;
                    var31_31.visitAttribute(var8_8);
                    var8_8 = var29_29;
                }
                var31_31.visitEnd();
            }
            --var16_16;
        }
        var16_16 = this.readUnsignedShort(var9_9);
        var9_9 += 2;
        while (var16_16 > 0) {
            var28_28 = var9_9 + 6;
            var10_10 = this.readUnsignedShort(var9_9);
            var11_11 = this.readUTF8(var9_9 + 2, var5_5);
            var30_30 = this.readUTF8(var9_9 + 4, var5_5);
            var21_21 = null;
            var6_6 = 0;
            var7_7 = 0;
            var31_32 = 0;
            var33_34 = 0;
            var34_35 = 0;
            var8_8 = null;
            var12_12 = 0;
            var15_15 = 0;
            var20_20 = this.readUnsignedShort(var9_9 + 6);
            var9_9 += 8;
            while (var20_20 > 0) {
                var27_27 = this.readUTF8(var9_9, var5_5);
                var35_37 = this.readInt(var9_9 + 2);
                var9_9 += 6;
                if ("Code".equals(var27_27)) {
                    if (!var17_17) {
                        var12_12 = var9_9;
                    }
                } else if ("Exceptions".equals(var27_27)) {
                    var15_15 = var9_9;
                } else if ("Signature".equals(var27_27)) {
                    var21_21 = this.readUTF8(var9_9, var5_5);
                } else if ("Deprecated".equals(var27_27)) {
                    var10_10 |= 131072;
                } else if ("RuntimeVisibleAnnotations".equals(var27_27)) {
                    var6_6 = var9_9;
                } else if ("AnnotationDefault".equals(var27_27)) {
                    var31_32 = var9_9;
                } else if ("Synthetic".equals(var27_27)) {
                    var10_10 |= 4096;
                } else if ("RuntimeInvisibleAnnotations".equals(var27_27)) {
                    var7_7 = var9_9;
                } else if ("RuntimeVisibleParameterAnnotations".equals(var27_27)) {
                    var33_34 = var9_9;
                } else if ("RuntimeInvisibleParameterAnnotations".equals(var27_27)) {
                    var34_35 = var9_9;
                } else {
                    var29_29 = this.a(var2_2, var27_27, var9_9, var35_37, var5_5, -1, null);
                    if (var29_29 != null) {
                        var29_29.a = var8_8;
                        var8_8 = var29_29;
                    }
                }
                var9_9 += var35_37;
                --var20_20;
            }
            if (var15_15 == 0) {
                var35_36 = null;
            } else {
                var35_36 = new String[this.readUnsignedShort(var15_15)];
                var15_15 += 2;
                for (var20_20 = 0; var20_20 < var35_36.length; ++var20_20) {
                    var35_36[var20_20] = this.readClass(var15_15, var5_5);
                    var15_15 += 2;
                }
            }
            var36_38 = var1_1.visitMethod(var10_10, var11_11, var30_30, var21_21, var35_36);
            if (var36_38 == null) ** GOTO lbl297
            if (!(var36_38 instanceof MethodWriter)) ** GOTO lbl-1000
            var37_39 = (MethodWriter)var36_38;
            if (var37_39.b.J != this || var21_21 != var37_39.g) ** GOTO lbl-1000
            var38_41 = 0;
            if (var35_36 == null) {
                var38_41 = var37_39.j == 0 ? 1 : 0;
            } else if (var35_36.length == var37_39.j) {
                var38_41 = 1;
                for (var20_20 = var35_36.length - 1; var20_20 >= 0; --var20_20) {
                    if (var37_39.k[var20_20] == this.readUnsignedShort(var15_15 -= 2)) continue;
                    var38_41 = 0;
                    break;
                }
            }
            if (var38_41 != 0) {
                var37_39.h = var28_28;
                var37_39.i = var9_9 - var28_28;
            } else lbl-1000:
            // 3 sources

            {
                if (var31_32 != 0) {
                    var37_39 = var36_38.visitAnnotationDefault();
                    this.a(var31_32, var5_5, null, (AnnotationVisitor)var37_39);
                    if (var37_39 != null) {
                        var37_39.visitEnd();
                    }
                }
                for (var20_20 = 1; var20_20 >= 0; --var20_20) {
                    v2 = var15_15 = var20_20 == 0 ? var7_7 : var6_6;
                    if (var15_15 == 0) continue;
                    var32_33 = this.readUnsignedShort(var15_15);
                    var15_15 += 2;
                    while (var32_33 > 0) {
                        var15_15 = this.a(var15_15 + 2, var5_5, true, var36_38.visitAnnotation(this.readUTF8(var15_15, var5_5), var20_20 != 0));
                        --var32_33;
                    }
                }
                if (var33_34 != 0) {
                    this.a(var33_34, var30_30, var5_5, true, var36_38);
                }
                if (var34_35 != 0) {
                    this.a(var34_35, var30_30, var5_5, false, var36_38);
                }
                while (var8_8 != null) {
                    var29_29 = var8_8.a;
                    var8_8.a = null;
                    var36_38.visitAttribute(var8_8);
                    var8_8 = var29_29;
                }
lbl297:
                // 2 sources

                if (var36_38 != null && var12_12 != 0) {
                    var37_40 = this.readUnsignedShort(var12_12);
                    var38_41 = this.readUnsignedShort(var12_12 + 2);
                    var39_42 = this.readInt(var12_12 + 4);
                    var40_43 = var12_12 += 8;
                    var41_44 = var12_12 + var39_42;
                    var36_38.visitCode();
                    var42_45 = new Label[var39_42 + 2];
                    this.readLabel(var39_42 + 1, var42_45);
                    block58: while (var12_12 < var41_44) {
                        var15_15 = var12_12 - var40_43;
                        var43_46 = var4_4[var12_12] & 255;
                        switch (ClassWriter.a[var43_46]) {
                            case 0: 
                            case 4: {
                                ++var12_12;
                                continue block58;
                            }
                            case 8: {
                                this.readLabel(var15_15 + this.readShort(var12_12 + 1), var42_45);
                                var12_12 += 3;
                                continue block58;
                            }
                            case 9: {
                                this.readLabel(var15_15 + this.readInt(var12_12 + 1), var42_45);
                                var12_12 += 5;
                                continue block58;
                            }
                            case 16: {
                                var43_46 = var4_4[var12_12 + 1] & 255;
                                if (var43_46 == 132) {
                                    var12_12 += 6;
                                    continue block58;
                                }
                                var12_12 += 4;
                                continue block58;
                            }
                            case 13: {
                                var12_12 = var12_12 + 4 - (var15_15 & 3);
                                this.readLabel(var15_15 + this.readInt(var12_12), var42_45);
                                var20_20 = this.readInt(var12_12 + 8) - this.readInt(var12_12 + 4) + 1;
                                var12_12 += 12;
                                while (var20_20 > 0) {
                                    this.readLabel(var15_15 + this.readInt(var12_12), var42_45);
                                    var12_12 += 4;
                                    --var20_20;
                                }
                                continue block58;
                            }
                            case 14: {
                                var12_12 = var12_12 + 4 - (var15_15 & 3);
                                this.readLabel(var15_15 + this.readInt(var12_12), var42_45);
                                var20_20 = this.readInt(var12_12 + 4);
                                var12_12 += 8;
                                while (var20_20 > 0) {
                                    this.readLabel(var15_15 + this.readInt(var12_12 + 4), var42_45);
                                    var12_12 += 8;
                                    --var20_20;
                                }
                                continue block58;
                            }
                            case 1: 
                            case 3: 
                            case 10: {
                                var12_12 += 2;
                                continue block58;
                            }
                            case 2: 
                            case 5: 
                            case 6: 
                            case 11: 
                            case 12: {
                                var12_12 += 3;
                                continue block58;
                            }
                            case 7: {
                                var12_12 += 5;
                                continue block58;
                            }
                        }
                        var12_12 += 4;
                    }
                    var20_20 = this.readUnsignedShort(var12_12);
                    var12_12 += 2;
                    while (var20_20 > 0) {
                        var43_48 = this.readLabel(this.readUnsignedShort(var12_12), var42_45);
                        var44_50 = this.readLabel(this.readUnsignedShort(var12_12 + 2), var42_45);
                        var45_52 = this.readLabel(this.readUnsignedShort(var12_12 + 4), var42_45);
                        var46_53 = this.readUnsignedShort(var12_12 + 6);
                        if (var46_53 == 0) {
                            var36_38.visitTryCatchBlock(var43_48, var44_50, var45_52, null);
                        } else {
                            var36_38.visitTryCatchBlock(var43_48, var44_50, var45_52, this.readUTF8(this.a[var46_53], var5_5));
                        }
                        var12_12 += 8;
                        --var20_20;
                    }
                    var43_46 = 0;
                    var44_49 = 0;
                    var45_51 = 0;
                    var46_53 = 0;
                    var47_54 = 0;
                    var48_55 = 0;
                    var49_56 = 0;
                    var50_57 = 0;
                    var51_58 = 0;
                    var52_59 = null;
                    var53_60 = null;
                    var54_61 = true;
                    var8_8 = null;
                    var20_20 = this.readUnsignedShort(var12_12);
                    var12_12 += 2;
                    while (var20_20 > 0) {
                        var27_27 = this.readUTF8(var12_12, var5_5);
                        if ("LocalVariableTable".equals(var27_27)) {
                            if (!var18_18) {
                                var43_46 = var12_12 + 6;
                                var15_15 = var12_12 + 8;
                                for (var32_33 = this.readUnsignedShort(var12_12 + 6); var32_33 > 0; --var32_33) {
                                    var55_62 = this.readUnsignedShort(var15_15);
                                    if (var42_45[var55_62] == null) {
                                        this.readLabel((int)var55_62, (Label[])var42_45).a |= 1;
                                    }
                                    if (var42_45[var55_62 += this.readUnsignedShort(var15_15 + 2)] == null) {
                                        this.readLabel((int)var55_62, (Label[])var42_45).a |= 1;
                                    }
                                    var15_15 += 10;
                                }
                            }
                        } else if ("LocalVariableTypeTable".equals(var27_27)) {
                            var44_49 = var12_12 + 6;
                        } else if ("LineNumberTable".equals(var27_27)) {
                            if (!var18_18) {
                                var15_15 = var12_12 + 8;
                                for (var32_33 = this.readUnsignedShort(var12_12 + 6); var32_33 > 0; --var32_33) {
                                    var55_62 = this.readUnsignedShort(var15_15);
                                    if (var42_45[var55_62] == null) {
                                        this.readLabel((int)var55_62, (Label[])var42_45).a |= 1;
                                    }
                                    var42_45[var55_62].b = this.readUnsignedShort(var15_15 + 2);
                                    var15_15 += 4;
                                }
                            }
                        } else if ("StackMapTable".equals(var27_27)) {
                            if ((var3_3 & 4) == 0) {
                                var45_51 = var12_12 + 8;
                                var46_53 = this.readUnsignedShort(var12_12 + 6);
                            }
                        } else if ("StackMap".equals(var27_27)) {
                            if ((var3_3 & 4) == 0) {
                                var45_51 = var12_12 + 8;
                                var46_53 = this.readUnsignedShort(var12_12 + 6);
                                var54_61 = false;
                            }
                        } else {
                            for (var32_33 = 0; var32_33 < var2_2.length; ++var32_33) {
                                if (!var2_2[var32_33].type.equals(var27_27) || (var29_29 = var2_2[var32_33].read(this, var12_12 + 6, this.readInt(var12_12 + 2), var5_5, var40_43 - 8, var42_45)) == null) continue;
                                var29_29.a = var8_8;
                                var8_8 = var29_29;
                            }
                        }
                        var12_12 += 6 + this.readInt(var12_12 + 2);
                        --var20_20;
                    }
                    if (var45_51 != 0) {
                        var52_59 = new Object[var38_41];
                        var53_60 = new Object[var37_40];
                        if (var19_19) {
                            var56_64 = 0;
                            if ((var10_10 & 8) == 0) {
                                var52_59[var56_64++] = "<init>".equals(var11_11) != false ? Opcodes.UNINITIALIZED_THIS : this.readClass(this.header + 2, var5_5);
                            }
                            var20_20 = 1;
                            block66: while (true) {
                                var32_33 = var20_20;
                                switch (var30_30.charAt(var20_20++)) {
                                    case 'B': 
                                    case 'C': 
                                    case 'I': 
                                    case 'S': 
                                    case 'Z': {
                                        var52_59[var56_64++] = Opcodes.INTEGER;
                                        continue block66;
                                    }
                                    case 'F': {
                                        var52_59[var56_64++] = Opcodes.FLOAT;
                                        continue block66;
                                    }
                                    case 'J': {
                                        var52_59[var56_64++] = Opcodes.LONG;
                                        continue block66;
                                    }
                                    case 'D': {
                                        var52_59[var56_64++] = Opcodes.DOUBLE;
                                        continue block66;
                                    }
                                    case '[': {
                                        while (var30_30.charAt(var20_20) == '[') {
                                            ++var20_20;
                                        }
                                        if (var30_30.charAt(var20_20) == 'L') {
                                            ++var20_20;
                                            while (var30_30.charAt(var20_20) != ';') {
                                                ++var20_20;
                                            }
                                        }
                                        var52_59[var56_64++] = var30_30.substring(var32_33, ++var20_20);
                                        continue block66;
                                    }
                                    case 'L': {
                                        while (var30_30.charAt(var20_20) != ';') {
                                            ++var20_20;
                                        }
                                        var52_59[var56_64++] = var30_30.substring(var32_33 + 1, var20_20++);
                                        continue block66;
                                    }
                                }
                                break;
                            }
                            var49_56 = var56_64;
                        }
                        var48_55 = -1;
                    }
                    var12_12 = var40_43;
                    block70: while (var12_12 < var41_44) {
                        var15_15 = var12_12 - var40_43;
                        var56_65 = var42_45[var15_15];
                        if (var56_65 != null) {
                            var36_38.visitLabel(var56_65);
                            if (!var18_18 && var56_65.b > 0) {
                                var36_38.visitLineNumber(var56_65.b, var56_65);
                            }
                        }
                        while (var52_59 != null && (var48_55 == var15_15 || var48_55 == -1)) {
                            if (!var54_61 || var19_19) {
                                var36_38.visitFrame(-1, var49_56, var52_59, var51_58, var53_60);
                            } else if (var48_55 != -1) {
                                var36_38.visitFrame(var47_54, var50_57, var52_59, var51_58, var53_60);
                            }
                            if (var46_53 > 0) {
                                if (var54_61) {
                                    var57_66 = var4_4[var45_51++] & 255;
                                } else {
                                    var57_66 = 255;
                                    var48_55 = -1;
                                }
                                var50_57 = 0;
                                if (var57_66 < 64) {
                                    var58_68 = var57_66;
                                    var47_54 = 3;
                                    var51_58 = 0;
                                } else if (var57_66 < 128) {
                                    var58_68 = var57_66 - 64;
                                    var45_51 = this.a(var53_60, 0, var45_51, var5_5, var42_45);
                                    var47_54 = 4;
                                    var51_58 = 1;
                                } else {
                                    var58_68 = this.readUnsignedShort(var45_51);
                                    var45_51 += 2;
                                    if (var57_66 == 247) {
                                        var45_51 = this.a(var53_60, 0, var45_51, var5_5, var42_45);
                                        var47_54 = 4;
                                        var51_58 = 1;
                                    } else if (var57_66 >= 248 && var57_66 < 251) {
                                        var47_54 = 2;
                                        var50_57 = 251 - var57_66;
                                        var49_56 -= var50_57;
                                        var51_58 = 0;
                                    } else if (var57_66 == 251) {
                                        var47_54 = 3;
                                        var51_58 = 0;
                                    } else if (var57_66 < 255) {
                                        var20_20 = var19_19 != false ? var49_56 : 0;
                                        for (var32_33 = var57_66 - 251; var32_33 > 0; --var32_33) {
                                            var45_51 = this.a(var52_59, var20_20++, var45_51, var5_5, var42_45);
                                        }
                                        var47_54 = 1;
                                        var50_57 = var57_66 - 251;
                                        var49_56 += var50_57;
                                        var51_58 = 0;
                                    } else {
                                        var47_54 = 0;
                                        var50_57 = var49_56 = this.readUnsignedShort(var45_51);
                                        var45_51 += 2;
                                        var20_20 = 0;
                                        for (var59_69 = var49_56; var59_69 > 0; --var59_69) {
                                            var45_51 = this.a(var52_59, var20_20++, var45_51, var5_5, var42_45);
                                        }
                                        var59_69 = var51_58 = this.readUnsignedShort(var45_51);
                                        var45_51 += 2;
                                        var20_20 = 0;
                                        while (var59_69 > 0) {
                                            var45_51 = this.a(var53_60, var20_20++, var45_51, var5_5, var42_45);
                                            --var59_69;
                                        }
                                    }
                                }
                                this.readLabel(var48_55 += var58_68 + 1, var42_45);
                                --var46_53;
                                continue;
                            }
                            var52_59 = null;
                        }
                        var57_66 = var4_4[var12_12] & 255;
                        switch (ClassWriter.a[var57_66]) {
                            case 0: {
                                var36_38.visitInsn(var57_66);
                                ++var12_12;
                                continue block70;
                            }
                            case 4: {
                                if (var57_66 > 54) {
                                    var36_38.visitVarInsn(54 + ((var57_66 -= 59) >> 2), var57_66 & 3);
                                } else {
                                    var36_38.visitVarInsn(21 + ((var57_66 -= 26) >> 2), var57_66 & 3);
                                }
                                ++var12_12;
                                continue block70;
                            }
                            case 8: {
                                var36_38.visitJumpInsn(var57_66, var42_45[var15_15 + this.readShort(var12_12 + 1)]);
                                var12_12 += 3;
                                continue block70;
                            }
                            case 9: {
                                var36_38.visitJumpInsn(var57_66 - 33, var42_45[var15_15 + this.readInt(var12_12 + 1)]);
                                var12_12 += 5;
                                continue block70;
                            }
                            case 16: {
                                var57_66 = var4_4[var12_12 + 1] & 255;
                                if (var57_66 == 132) {
                                    var36_38.visitIincInsn(this.readUnsignedShort(var12_12 + 2), this.readShort(var12_12 + 4));
                                    var12_12 += 6;
                                    continue block70;
                                }
                                var36_38.visitVarInsn(var57_66, this.readUnsignedShort(var12_12 + 2));
                                var12_12 += 4;
                                continue block70;
                            }
                            case 13: {
                                var12_12 = var12_12 + 4 - (var15_15 & 3);
                                var55_62 = var15_15 + this.readInt(var12_12);
                                var58_68 = this.readInt(var12_12 + 4);
                                var59_69 = this.readInt(var12_12 + 8);
                                var12_12 += 12;
                                var60_70 = new Label[var59_69 - var58_68 + 1];
                                for (var20_20 = 0; var20_20 < var60_70.length; ++var20_20) {
                                    var60_70[var20_20] = var42_45[var15_15 + this.readInt(var12_12)];
                                    var12_12 += 4;
                                }
                                var36_38.visitTableSwitchInsn(var58_68, var59_69, var42_45[var55_62], var60_70);
                                continue block70;
                            }
                            case 14: {
                                var12_12 = var12_12 + 4 - (var15_15 & 3);
                                var55_62 = var15_15 + this.readInt(var12_12);
                                var20_20 = this.readInt(var12_12 + 4);
                                var12_12 += 8;
                                var61_72 /* !! */  = new int[var20_20];
                                var62_73 = new Label[var20_20];
                                for (var20_20 = 0; var20_20 < var61_72 /* !! */ .length; ++var20_20) {
                                    var61_72 /* !! */ [var20_20] = this.readInt(var12_12);
                                    var62_73[var20_20] = var42_45[var15_15 + this.readInt(var12_12 + 4)];
                                    var12_12 += 8;
                                }
                                var36_38.visitLookupSwitchInsn(var42_45[var55_62], var61_72 /* !! */ , var62_73);
                                continue block70;
                            }
                            case 3: {
                                var36_38.visitVarInsn(var57_66, var4_4[var12_12 + 1] & 255);
                                var12_12 += 2;
                                continue block70;
                            }
                            case 1: {
                                var36_38.visitIntInsn(var57_66, var4_4[var12_12 + 1]);
                                var12_12 += 2;
                                continue block70;
                            }
                            case 2: {
                                var36_38.visitIntInsn(var57_66, this.readShort(var12_12 + 1));
                                var12_12 += 3;
                                continue block70;
                            }
                            case 10: {
                                var36_38.visitLdcInsn(this.readConst(var4_4[var12_12 + 1] & 255, var5_5));
                                var12_12 += 2;
                                continue block70;
                            }
                            case 11: {
                                var36_38.visitLdcInsn(this.readConst(this.readUnsignedShort(var12_12 + 1), var5_5));
                                var12_12 += 3;
                                continue block70;
                            }
                            case 6: 
                            case 7: {
                                var63_75 = this.a[this.readUnsignedShort(var12_12 + 1)];
                                var64_76 = this.readClass(var63_75, var5_5);
                                var63_75 = this.a[this.readUnsignedShort(var63_75 + 2)];
                                var65_77 = this.readUTF8(var63_75, var5_5);
                                var66_78 = this.readUTF8(var63_75 + 2, var5_5);
                                if (var57_66 < 182) {
                                    var36_38.visitFieldInsn(var57_66, var64_76, var65_77, var66_78);
                                } else {
                                    var36_38.visitMethodInsn(var57_66, var64_76, var65_77, var66_78);
                                }
                                if (var57_66 == 185) {
                                    var12_12 += 5;
                                    continue block70;
                                }
                                var12_12 += 3;
                                continue block70;
                            }
                            case 5: {
                                var36_38.visitTypeInsn(var57_66, this.readClass(var12_12 + 1, var5_5));
                                var12_12 += 3;
                                continue block70;
                            }
                            case 12: {
                                var36_38.visitIincInsn(var4_4[var12_12 + 1] & 255, var4_4[var12_12 + 2]);
                                var12_12 += 3;
                                continue block70;
                            }
                        }
                        var36_38.visitMultiANewArrayInsn(this.readClass(var12_12 + 1, var5_5), var4_4[var12_12 + 3] & 255);
                        var12_12 += 4;
                    }
                    var56_63 = var42_45[var41_44 - var40_43];
                    if (var56_63 != null) {
                        var36_38.visitLabel(var56_63);
                    }
                    if (!var18_18 && var43_46 != 0) {
                        var57_67 = null;
                        if (var44_49 != 0) {
                            var32_33 = this.readUnsignedShort(var44_49) * 3;
                            var15_15 = var44_49 + 2;
                            var57_67 = new int[var32_33];
                            while (var32_33 > 0) {
                                var57_67[--var32_33] = var15_15 + 6;
                                var57_67[--var32_33] = this.readUnsignedShort(var15_15 + 8);
                                var57_67[--var32_33] = this.readUnsignedShort(var15_15);
                                var15_15 += 10;
                            }
                        }
                        var15_15 = var43_46 + 2;
                        for (var32_33 = this.readUnsignedShort(var43_46); var32_33 > 0; --var32_33) {
                            var58_68 = this.readUnsignedShort(var15_15);
                            var59_69 = this.readUnsignedShort(var15_15 + 2);
                            var60_71 = this.readUnsignedShort(var15_15 + 8);
                            var61_72 /* !! */  = null;
                            if (var57_67 != null) {
                                for (var62_74 = 0; var62_74 < var57_67.length; var62_74 += 3) {
                                    if (var57_67[var62_74] != var58_68 || var57_67[var62_74 + 1] != var60_71) continue;
                                    var61_72 /* !! */  = (int[])this.readUTF8(var57_67[var62_74 + 2], var5_5);
                                    break;
                                }
                            }
                            var36_38.visitLocalVariable(this.readUTF8(var15_15 + 4, var5_5), this.readUTF8(var15_15 + 6, var5_5), (String)var61_72 /* !! */ , var42_45[var58_68], var42_45[var58_68 + var59_69], var60_71);
                            var15_15 += 10;
                        }
                    }
                    while (var8_8 != null) {
                        var29_29 = var8_8.a;
                        var8_8.a = null;
                        var36_38.visitAttribute(var8_8);
                        var8_8 = var29_29;
                    }
                    var36_38.visitMaxs(var37_40, var38_41);
                }
                if (var36_38 != null) {
                    var36_38.visitEnd();
                }
            }
            --var16_16;
        }
        var1_1.visitEnd();
    }

    private void a(int n, String string, char[] cArray, boolean bl, MethodVisitor methodVisitor) {
        AnnotationVisitor annotationVisitor;
        int n2;
        int n3 = this.b[n++] & 0xFF;
        int n4 = Type.getArgumentTypes(string).length - n3;
        for (n2 = 0; n2 < n4; ++n2) {
            annotationVisitor = methodVisitor.visitParameterAnnotation(n2, "Ljava/lang/Synthetic;", false);
            if (annotationVisitor == null) continue;
            annotationVisitor.visitEnd();
        }
        while (n2 < n3 + n4) {
            int n5 = this.readUnsignedShort(n);
            n += 2;
            while (n5 > 0) {
                annotationVisitor = methodVisitor.visitParameterAnnotation(n2, this.readUTF8(n, cArray), bl);
                n = this.a(n + 2, cArray, true, annotationVisitor);
                --n5;
            }
            ++n2;
        }
    }

    private int a(int n, char[] cArray, boolean bl, AnnotationVisitor annotationVisitor) {
        int n2 = this.readUnsignedShort(n);
        n += 2;
        if (bl) {
            while (n2 > 0) {
                n = this.a(n + 2, cArray, this.readUTF8(n, cArray), annotationVisitor);
                --n2;
            }
        } else {
            while (n2 > 0) {
                n = this.a(n, cArray, null, annotationVisitor);
                --n2;
            }
        }
        if (annotationVisitor != null) {
            annotationVisitor.visitEnd();
        }
        return n;
    }

    private int a(int n, char[] cArray, String string, AnnotationVisitor annotationVisitor) {
        if (annotationVisitor == null) {
            switch (this.b[n] & 0xFF) {
                case 101: {
                    return n + 5;
                }
                case 64: {
                    return this.a(n + 3, cArray, true, null);
                }
                case 91: {
                    return this.a(n + 1, cArray, false, null);
                }
            }
            return n + 3;
        }
        block5 : switch (this.b[n++] & 0xFF) {
            case 68: 
            case 70: 
            case 73: 
            case 74: {
                annotationVisitor.visit(string, this.readConst(this.readUnsignedShort(n), cArray));
                n += 2;
                break;
            }
            case 66: {
                annotationVisitor.visit(string, new Byte((byte)this.readInt(this.a[this.readUnsignedShort(n)])));
                n += 2;
                break;
            }
            case 90: {
                annotationVisitor.visit(string, this.readInt(this.a[this.readUnsignedShort(n)]) == 0 ? Boolean.FALSE : Boolean.TRUE);
                n += 2;
                break;
            }
            case 83: {
                annotationVisitor.visit(string, new Short((short)this.readInt(this.a[this.readUnsignedShort(n)])));
                n += 2;
                break;
            }
            case 67: {
                annotationVisitor.visit(string, new Character((char)this.readInt(this.a[this.readUnsignedShort(n)])));
                n += 2;
                break;
            }
            case 115: {
                annotationVisitor.visit(string, this.readUTF8(n, cArray));
                n += 2;
                break;
            }
            case 101: {
                annotationVisitor.visitEnum(string, this.readUTF8(n, cArray), this.readUTF8(n + 2, cArray));
                n += 4;
                break;
            }
            case 99: {
                annotationVisitor.visit(string, Type.getType(this.readUTF8(n, cArray)));
                n += 2;
                break;
            }
            case 64: {
                n = this.a(n + 2, cArray, true, annotationVisitor.visitAnnotation(string, this.readUTF8(n, cArray)));
                break;
            }
            case 91: {
                int n2 = this.readUnsignedShort(n);
                n += 2;
                if (n2 == 0) {
                    return this.a(n - 2, cArray, false, annotationVisitor.visitArray(string));
                }
                switch (this.b[n++] & 0xFF) {
                    case 66: {
                        byte[] byArray = new byte[n2];
                        for (int i = 0; i < n2; ++i) {
                            byArray[i] = (byte)this.readInt(this.a[this.readUnsignedShort(n)]);
                            n += 3;
                        }
                        annotationVisitor.visit(string, byArray);
                        --n;
                        break block5;
                    }
                    case 90: {
                        boolean[] blArray = new boolean[n2];
                        for (int i = 0; i < n2; ++i) {
                            blArray[i] = this.readInt(this.a[this.readUnsignedShort(n)]) != 0;
                            n += 3;
                        }
                        annotationVisitor.visit(string, blArray);
                        --n;
                        break block5;
                    }
                    case 83: {
                        short[] sArray = new short[n2];
                        for (int i = 0; i < n2; ++i) {
                            sArray[i] = (short)this.readInt(this.a[this.readUnsignedShort(n)]);
                            n += 3;
                        }
                        annotationVisitor.visit(string, sArray);
                        --n;
                        break block5;
                    }
                    case 67: {
                        char[] cArray2 = new char[n2];
                        for (int i = 0; i < n2; ++i) {
                            cArray2[i] = (char)this.readInt(this.a[this.readUnsignedShort(n)]);
                            n += 3;
                        }
                        annotationVisitor.visit(string, cArray2);
                        --n;
                        break block5;
                    }
                    case 73: {
                        int[] nArray = new int[n2];
                        for (int i = 0; i < n2; ++i) {
                            nArray[i] = this.readInt(this.a[this.readUnsignedShort(n)]);
                            n += 3;
                        }
                        annotationVisitor.visit(string, nArray);
                        --n;
                        break block5;
                    }
                    case 74: {
                        long[] lArray = new long[n2];
                        for (int i = 0; i < n2; ++i) {
                            lArray[i] = this.readLong(this.a[this.readUnsignedShort(n)]);
                            n += 3;
                        }
                        annotationVisitor.visit(string, lArray);
                        --n;
                        break block5;
                    }
                    case 70: {
                        float[] fArray = new float[n2];
                        for (int i = 0; i < n2; ++i) {
                            fArray[i] = Float.intBitsToFloat(this.readInt(this.a[this.readUnsignedShort(n)]));
                            n += 3;
                        }
                        annotationVisitor.visit(string, fArray);
                        --n;
                        break block5;
                    }
                    case 68: {
                        double[] dArray = new double[n2];
                        for (int i = 0; i < n2; ++i) {
                            dArray[i] = Double.longBitsToDouble(this.readLong(this.a[this.readUnsignedShort(n)]));
                            n += 3;
                        }
                        annotationVisitor.visit(string, dArray);
                        --n;
                        break block5;
                    }
                }
                n = this.a(n - 3, cArray, false, annotationVisitor.visitArray(string));
            }
        }
        return n;
    }

    private int a(Object[] objectArray, int n, int n2, char[] cArray, Label[] labelArray) {
        int n3 = this.b[n2++] & 0xFF;
        switch (n3) {
            case 0: {
                objectArray[n] = Opcodes.TOP;
                break;
            }
            case 1: {
                objectArray[n] = Opcodes.INTEGER;
                break;
            }
            case 2: {
                objectArray[n] = Opcodes.FLOAT;
                break;
            }
            case 3: {
                objectArray[n] = Opcodes.DOUBLE;
                break;
            }
            case 4: {
                objectArray[n] = Opcodes.LONG;
                break;
            }
            case 5: {
                objectArray[n] = Opcodes.NULL;
                break;
            }
            case 6: {
                objectArray[n] = Opcodes.UNINITIALIZED_THIS;
                break;
            }
            case 7: {
                objectArray[n] = this.readClass(n2, cArray);
                n2 += 2;
                break;
            }
            default: {
                objectArray[n] = this.readLabel(this.readUnsignedShort(n2), labelArray);
                n2 += 2;
            }
        }
        return n2;
    }

    protected Label readLabel(int n, Label[] labelArray) {
        if (labelArray[n] == null) {
            labelArray[n] = new Label();
        }
        return labelArray[n];
    }

    private Attribute a(Attribute[] attributeArray, String string, int n, int n2, char[] cArray, int n3, Label[] labelArray) {
        for (int i = 0; i < attributeArray.length; ++i) {
            if (!attributeArray[i].type.equals(string)) continue;
            return attributeArray[i].read(this, n, n2, cArray, n3, labelArray);
        }
        return new Attribute(string).read(this, n, n2, null, -1, null);
    }

    public int getItem(int n) {
        return this.a[n];
    }

    public int readByte(int n) {
        return this.b[n] & 0xFF;
    }

    public int readUnsignedShort(int n) {
        byte[] byArray = this.b;
        return (byArray[n] & 0xFF) << 8 | byArray[n + 1] & 0xFF;
    }

    public short readShort(int n) {
        byte[] byArray = this.b;
        return (short)((byArray[n] & 0xFF) << 8 | byArray[n + 1] & 0xFF);
    }

    public int readInt(int n) {
        byte[] byArray = this.b;
        return (byArray[n] & 0xFF) << 24 | (byArray[n + 1] & 0xFF) << 16 | (byArray[n + 2] & 0xFF) << 8 | byArray[n + 3] & 0xFF;
    }

    public long readLong(int n) {
        long l = this.readInt(n);
        long l2 = (long)this.readInt(n + 4) & 0xFFFFFFFFL;
        return l << 32 | l2;
    }

    public String readUTF8(int n, char[] cArray) {
        int n2 = this.readUnsignedShort(n);
        String string = this.c[n2];
        if (string != null) {
            return string;
        }
        n = this.a[n2];
        this.c[n2] = this.a(n + 2, this.readUnsignedShort(n), cArray);
        return this.c[n2];
    }

    private String a(int n, int n2, char[] cArray) {
        int n3 = n + n2;
        byte[] byArray = this.b;
        int n4 = 0;
        block4: while (n < n3) {
            byte by;
            int n5 = byArray[n++] & 0xFF;
            switch (n5 >> 4) {
                case 0: 
                case 1: 
                case 2: 
                case 3: 
                case 4: 
                case 5: 
                case 6: 
                case 7: {
                    cArray[n4++] = (char)n5;
                    continue block4;
                }
                case 12: 
                case 13: {
                    by = byArray[n++];
                    cArray[n4++] = (char)((n5 & 0x1F) << 6 | by & 0x3F);
                    continue block4;
                }
            }
            by = byArray[n++];
            byte by2 = byArray[n++];
            cArray[n4++] = (char)((n5 & 0xF) << 12 | (by & 0x3F) << 6 | by2 & 0x3F);
        }
        return new String(cArray, 0, n4);
    }

    public String readClass(int n, char[] cArray) {
        return this.readUTF8(this.a[this.readUnsignedShort(n)], cArray);
    }

    public Object readConst(int n, char[] cArray) {
        int n2 = this.a[n];
        switch (this.b[n2 - 1]) {
            case 3: {
                return new Integer(this.readInt(n2));
            }
            case 4: {
                return new Float(Float.intBitsToFloat(this.readInt(n2)));
            }
            case 5: {
                return new Long(this.readLong(n2));
            }
            case 6: {
                return new Double(Double.longBitsToDouble(this.readLong(n2)));
            }
            case 7: {
                return Type.getObjectType(this.readUTF8(n2, cArray));
            }
        }
        return this.readUTF8(n2, cArray);
    }
}

