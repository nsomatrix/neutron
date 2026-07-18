/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm;

public class ByteVector {
    byte[] a;
    int b;

    public ByteVector() {
        this.a = new byte[64];
    }

    public ByteVector(int n) {
        this.a = new byte[n];
    }

    public ByteVector putByte(int n) {
        int n2 = this.b;
        if (n2 + 1 > this.a.length) {
            this.a(1);
        }
        this.a[n2++] = (byte)n;
        this.b = n2;
        return this;
    }

    ByteVector a(int n, int n2) {
        int n3 = this.b;
        if (n3 + 2 > this.a.length) {
            this.a(2);
        }
        byte[] byArray = this.a;
        byArray[n3++] = (byte)n;
        byArray[n3++] = (byte)n2;
        this.b = n3;
        return this;
    }

    public ByteVector putShort(int n) {
        int n2 = this.b;
        if (n2 + 2 > this.a.length) {
            this.a(2);
        }
        byte[] byArray = this.a;
        byArray[n2++] = (byte)(n >>> 8);
        byArray[n2++] = (byte)n;
        this.b = n2;
        return this;
    }

    ByteVector b(int n, int n2) {
        int n3 = this.b;
        if (n3 + 3 > this.a.length) {
            this.a(3);
        }
        byte[] byArray = this.a;
        byArray[n3++] = (byte)n;
        byArray[n3++] = (byte)(n2 >>> 8);
        byArray[n3++] = (byte)n2;
        this.b = n3;
        return this;
    }

    public ByteVector putInt(int n) {
        int n2 = this.b;
        if (n2 + 4 > this.a.length) {
            this.a(4);
        }
        byte[] byArray = this.a;
        byArray[n2++] = (byte)(n >>> 24);
        byArray[n2++] = (byte)(n >>> 16);
        byArray[n2++] = (byte)(n >>> 8);
        byArray[n2++] = (byte)n;
        this.b = n2;
        return this;
    }

    public ByteVector putLong(long l) {
        int n = this.b;
        if (n + 8 > this.a.length) {
            this.a(8);
        }
        byte[] byArray = this.a;
        int n2 = (int)(l >>> 32);
        byArray[n++] = (byte)(n2 >>> 24);
        byArray[n++] = (byte)(n2 >>> 16);
        byArray[n++] = (byte)(n2 >>> 8);
        byArray[n++] = (byte)n2;
        n2 = (int)l;
        byArray[n++] = (byte)(n2 >>> 24);
        byArray[n++] = (byte)(n2 >>> 16);
        byArray[n++] = (byte)(n2 >>> 8);
        byArray[n++] = (byte)n2;
        this.b = n;
        return this;
    }

    public ByteVector putUTF8(String string) {
        int n = string.length();
        if (this.b + 2 + n > this.a.length) {
            this.a(2 + n);
        }
        int n2 = this.b;
        byte[] byArray = this.a;
        byArray[n2++] = (byte)(n >>> 8);
        byArray[n2++] = (byte)n;
        for (int i = 0; i < n; ++i) {
            int n3;
            char c = string.charAt(i);
            if (c >= '\u0001' && c <= '\u007f') {
                byArray[n2++] = (byte)c;
                continue;
            }
            int n4 = i;
            for (n3 = i; n3 < n; ++n3) {
                c = string.charAt(n3);
                if (c >= '\u0001' && c <= '\u007f') {
                    ++n4;
                    continue;
                }
                if (c > '\u07ff') {
                    n4 += 3;
                    continue;
                }
                n4 += 2;
            }
            byArray[this.b] = (byte)(n4 >>> 8);
            byArray[this.b + 1] = (byte)n4;
            if (this.b + 2 + n4 > byArray.length) {
                this.b = n2;
                this.a(2 + n4);
                byArray = this.a;
            }
            for (n3 = i; n3 < n; ++n3) {
                c = string.charAt(n3);
                if (c >= '\u0001' && c <= '\u007f') {
                    byArray[n2++] = (byte)c;
                    continue;
                }
                if (c > '\u07ff') {
                    byArray[n2++] = (byte)(0xE0 | c >> 12 & 0xF);
                    byArray[n2++] = (byte)(0x80 | c >> 6 & 0x3F);
                    byArray[n2++] = (byte)(0x80 | c & 0x3F);
                    continue;
                }
                byArray[n2++] = (byte)(0xC0 | c >> 6 & 0x1F);
                byArray[n2++] = (byte)(0x80 | c & 0x3F);
            }
            break;
        }
        this.b = n2;
        return this;
    }

    public ByteVector putByteArray(byte[] byArray, int n, int n2) {
        if (this.b + n2 > this.a.length) {
            this.a(n2);
        }
        if (byArray != null) {
            System.arraycopy(byArray, n, this.a, this.b, n2);
        }
        this.b += n2;
        return this;
    }

    private void a(int n) {
        int n2 = 2 * this.a.length;
        int n3 = this.b + n;
        byte[] byArray = new byte[n2 > n3 ? n2 : n3];
        System.arraycopy(this.a, 0, byArray, 0, this.b);
        this.a = byArray;
    }
}

