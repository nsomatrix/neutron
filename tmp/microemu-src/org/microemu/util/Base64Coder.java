/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.util;

public class Base64Coder {
    private static char[] map1;
    private static byte[] map2;

    public static String encode(String s) {
        return new String(Base64Coder.encode(s.getBytes()));
    }

    public static char[] encode(byte[] in) {
        int iLen = in.length;
        int oDataLen = (iLen * 4 + 2) / 3;
        int oLen = (iLen + 2) / 3 * 4;
        char[] out = new char[oLen];
        int ip = 0;
        int op = 0;
        while (ip < iLen) {
            int i0 = in[ip++] & 0xFF;
            int i1 = ip < iLen ? in[ip++] & 0xFF : 0;
            int i2 = ip < iLen ? in[ip++] & 0xFF : 0;
            int o0 = i0 >>> 2;
            int o1 = (i0 & 3) << 4 | i1 >>> 4;
            int o2 = (i1 & 0xF) << 2 | i2 >>> 6;
            int o3 = i2 & 0x3F;
            out[op++] = map1[o0];
            out[op++] = map1[o1];
            out[op] = op < oDataLen ? map1[o2] : 61;
            int n = ++op < oDataLen ? map1[o3] : 61;
            out[op] = n;
            ++op;
        }
        return out;
    }

    public static String decode(String s) {
        return new String(Base64Coder.decode(s.toCharArray()));
    }

    public static byte[] decode(char[] in) {
        int iLen = in.length;
        if (iLen % 4 != 0) {
            throw new IllegalArgumentException("Length of Base64 encoded input string is not a multiple of 4.");
        }
        while (iLen > 0 && in[iLen - 1] == '=') {
            --iLen;
        }
        int oLen = iLen * 3 / 4;
        byte[] out = new byte[oLen];
        int ip = 0;
        int op = 0;
        while (ip < iLen) {
            int i3;
            char i0 = in[ip++];
            char i1 = in[ip++];
            int i2 = ip < iLen ? in[ip++] : 65;
            int n = i3 = ip < iLen ? in[ip++] : 65;
            if (i0 > '\u007f' || i1 > '\u007f' || i2 > 127 || i3 > 127) {
                throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
            }
            byte b0 = map2[i0];
            byte b1 = map2[i1];
            byte b2 = map2[i2];
            byte b3 = map2[i3];
            if (b0 < 0 || b1 < 0 || b2 < 0 || b3 < 0) {
                throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
            }
            int o0 = b0 << 2 | b1 >>> 4;
            int o1 = (b1 & 0xF) << 4 | b2 >>> 2;
            int o2 = (b2 & 3) << 6 | b3;
            out[op++] = (byte)o0;
            if (op < oLen) {
                out[op++] = (byte)o1;
            }
            if (op >= oLen) continue;
            out[op++] = (byte)o2;
        }
        return out;
    }

    static {
        int c;
        map1 = new char[64];
        int i = 0;
        for (c = 65; c <= 90; c = (int)((char)(c + 1))) {
            Base64Coder.map1[i++] = c;
        }
        for (c = 97; c <= 122; c = (int)((char)(c + 1))) {
            Base64Coder.map1[i++] = c;
        }
        for (c = 48; c <= 57; c = (int)((char)(c + 1))) {
            Base64Coder.map1[i++] = c;
        }
        Base64Coder.map1[i++] = 43;
        Base64Coder.map1[i++] = 47;
        map2 = new byte[128];
        for (i = 0; i < map2.length; ++i) {
            Base64Coder.map2[i] = -1;
        }
        for (i = 0; i < 64; ++i) {
            Base64Coder.map2[Base64Coder.map1[i]] = (byte)i;
        }
    }
}

