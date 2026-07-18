/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class Type {
    public static final int VOID = 0;
    public static final int BOOLEAN = 1;
    public static final int CHAR = 2;
    public static final int BYTE = 3;
    public static final int SHORT = 4;
    public static final int INT = 5;
    public static final int FLOAT = 6;
    public static final int LONG = 7;
    public static final int DOUBLE = 8;
    public static final int ARRAY = 9;
    public static final int OBJECT = 10;
    public static final Type VOID_TYPE = new Type(0);
    public static final Type BOOLEAN_TYPE = new Type(1);
    public static final Type CHAR_TYPE = new Type(2);
    public static final Type BYTE_TYPE = new Type(3);
    public static final Type SHORT_TYPE = new Type(4);
    public static final Type INT_TYPE = new Type(5);
    public static final Type FLOAT_TYPE = new Type(6);
    public static final Type LONG_TYPE = new Type(7);
    public static final Type DOUBLE_TYPE = new Type(8);
    private final int a;
    private final char[] b;
    private final int c;
    private final int d;

    private Type(int n) {
        this(n, null, 0, 1);
    }

    private Type(int n, char[] cArray, int n2, int n3) {
        this.a = n;
        this.b = cArray;
        this.c = n2;
        this.d = n3;
    }

    public static Type getType(String string) {
        return Type.a(string.toCharArray(), 0);
    }

    public static Type getObjectType(String string) {
        char[] cArray = string.toCharArray();
        return new Type(cArray[0] == '[' ? 9 : 10, cArray, 0, cArray.length);
    }

    public static Type getType(Class clazz) {
        if (clazz.isPrimitive()) {
            if (clazz == Integer.TYPE) {
                return INT_TYPE;
            }
            if (clazz == Void.TYPE) {
                return VOID_TYPE;
            }
            if (clazz == Boolean.TYPE) {
                return BOOLEAN_TYPE;
            }
            if (clazz == Byte.TYPE) {
                return BYTE_TYPE;
            }
            if (clazz == Character.TYPE) {
                return CHAR_TYPE;
            }
            if (clazz == Short.TYPE) {
                return SHORT_TYPE;
            }
            if (clazz == Double.TYPE) {
                return DOUBLE_TYPE;
            }
            if (clazz == Float.TYPE) {
                return FLOAT_TYPE;
            }
            return LONG_TYPE;
        }
        return Type.getType(Type.getDescriptor(clazz));
    }

    public static Type[] getArgumentTypes(String string) {
        char c;
        char[] cArray = string.toCharArray();
        int n = 1;
        int n2 = 0;
        while ((c = cArray[n++]) != ')') {
            if (c == 'L') {
                while (cArray[n++] != ';') {
                }
                ++n2;
                continue;
            }
            if (c == '[') continue;
            ++n2;
        }
        Type[] typeArray = new Type[n2];
        n = 1;
        n2 = 0;
        while (cArray[n] != ')') {
            typeArray[n2] = Type.a(cArray, n);
            n += typeArray[n2].d + (typeArray[n2].a == 10 ? 2 : 0);
            ++n2;
        }
        return typeArray;
    }

    public static Type[] getArgumentTypes(Method method) {
        Class<?>[] classArray = method.getParameterTypes();
        Type[] typeArray = new Type[classArray.length];
        for (int i = classArray.length - 1; i >= 0; --i) {
            typeArray[i] = Type.getType(classArray[i]);
        }
        return typeArray;
    }

    public static Type getReturnType(String string) {
        char[] cArray = string.toCharArray();
        return Type.a(cArray, string.indexOf(41) + 1);
    }

    public static Type getReturnType(Method method) {
        return Type.getType(method.getReturnType());
    }

    private static Type a(char[] cArray, int n) {
        switch (cArray[n]) {
            case 'V': {
                return VOID_TYPE;
            }
            case 'Z': {
                return BOOLEAN_TYPE;
            }
            case 'C': {
                return CHAR_TYPE;
            }
            case 'B': {
                return BYTE_TYPE;
            }
            case 'S': {
                return SHORT_TYPE;
            }
            case 'I': {
                return INT_TYPE;
            }
            case 'F': {
                return FLOAT_TYPE;
            }
            case 'J': {
                return LONG_TYPE;
            }
            case 'D': {
                return DOUBLE_TYPE;
            }
            case '[': {
                int n2 = 1;
                while (cArray[n + n2] == '[') {
                    ++n2;
                }
                if (cArray[n + n2] == 'L') {
                    ++n2;
                    while (cArray[n + n2] != ';') {
                        ++n2;
                    }
                }
                return new Type(9, cArray, n, n2 + 1);
            }
        }
        int n3 = 1;
        while (cArray[n + n3] != ';') {
            ++n3;
        }
        return new Type(10, cArray, n + 1, n3 - 1);
    }

    public int getSort() {
        return this.a;
    }

    public int getDimensions() {
        int n = 1;
        while (this.b[this.c + n] == '[') {
            ++n;
        }
        return n;
    }

    public Type getElementType() {
        return Type.a(this.b, this.c + this.getDimensions());
    }

    public String getClassName() {
        switch (this.a) {
            case 0: {
                return "void";
            }
            case 1: {
                return "boolean";
            }
            case 2: {
                return "char";
            }
            case 3: {
                return "byte";
            }
            case 4: {
                return "short";
            }
            case 5: {
                return "int";
            }
            case 6: {
                return "float";
            }
            case 7: {
                return "long";
            }
            case 8: {
                return "double";
            }
            case 9: {
                StringBuffer stringBuffer = new StringBuffer(this.getElementType().getClassName());
                for (int i = this.getDimensions(); i > 0; --i) {
                    stringBuffer.append("[]");
                }
                return stringBuffer.toString();
            }
        }
        return new String(this.b, this.c, this.d).replace('/', '.');
    }

    public String getInternalName() {
        return new String(this.b, this.c, this.d);
    }

    public String getDescriptor() {
        StringBuffer stringBuffer = new StringBuffer();
        this.a(stringBuffer);
        return stringBuffer.toString();
    }

    public static String getMethodDescriptor(Type type, Type[] typeArray) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append('(');
        for (int i = 0; i < typeArray.length; ++i) {
            typeArray[i].a(stringBuffer);
        }
        stringBuffer.append(')');
        type.a(stringBuffer);
        return stringBuffer.toString();
    }

    private void a(StringBuffer stringBuffer) {
        switch (this.a) {
            case 0: {
                stringBuffer.append('V');
                return;
            }
            case 1: {
                stringBuffer.append('Z');
                return;
            }
            case 2: {
                stringBuffer.append('C');
                return;
            }
            case 3: {
                stringBuffer.append('B');
                return;
            }
            case 4: {
                stringBuffer.append('S');
                return;
            }
            case 5: {
                stringBuffer.append('I');
                return;
            }
            case 6: {
                stringBuffer.append('F');
                return;
            }
            case 7: {
                stringBuffer.append('J');
                return;
            }
            case 8: {
                stringBuffer.append('D');
                return;
            }
            case 9: {
                stringBuffer.append(this.b, this.c, this.d);
                return;
            }
        }
        stringBuffer.append('L');
        stringBuffer.append(this.b, this.c, this.d);
        stringBuffer.append(';');
    }

    public static String getInternalName(Class clazz) {
        return clazz.getName().replace('.', '/');
    }

    public static String getDescriptor(Class clazz) {
        StringBuffer stringBuffer = new StringBuffer();
        Type.a(stringBuffer, clazz);
        return stringBuffer.toString();
    }

    public static String getConstructorDescriptor(Constructor constructor) {
        Class<?>[] classArray = constructor.getParameterTypes();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append('(');
        for (int i = 0; i < classArray.length; ++i) {
            Type.a(stringBuffer, classArray[i]);
        }
        return stringBuffer.append(")V").toString();
    }

    public static String getMethodDescriptor(Method method) {
        Class<?>[] classArray = method.getParameterTypes();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append('(');
        for (int i = 0; i < classArray.length; ++i) {
            Type.a(stringBuffer, classArray[i]);
        }
        stringBuffer.append(')');
        Type.a(stringBuffer, method.getReturnType());
        return stringBuffer.toString();
    }

    private static void a(StringBuffer stringBuffer, Class clazz) {
        Class<?> clazz2 = clazz;
        while (true) {
            if (clazz2.isPrimitive()) {
                int n = clazz2 == Integer.TYPE ? 73 : (clazz2 == Void.TYPE ? 86 : (clazz2 == Boolean.TYPE ? 90 : (clazz2 == Byte.TYPE ? 66 : (clazz2 == Character.TYPE ? 67 : (clazz2 == Short.TYPE ? 83 : (clazz2 == Double.TYPE ? 68 : (clazz2 == Float.TYPE ? 70 : 74)))))));
                stringBuffer.append((char)n);
                return;
            }
            if (!clazz2.isArray()) break;
            stringBuffer.append('[');
            clazz2 = clazz2.getComponentType();
        }
        stringBuffer.append('L');
        String string = clazz2.getName();
        int n = string.length();
        for (int i = 0; i < n; ++i) {
            char c = string.charAt(i);
            stringBuffer.append(c == '.' ? (char)'/' : (char)c);
        }
        stringBuffer.append(';');
    }

    public int getSize() {
        return this.a == 7 || this.a == 8 ? 2 : 1;
    }

    public int getOpcode(int n) {
        if (n == 46 || n == 79) {
            switch (this.a) {
                case 1: 
                case 3: {
                    return n + 5;
                }
                case 2: {
                    return n + 6;
                }
                case 4: {
                    return n + 7;
                }
                case 5: {
                    return n;
                }
                case 6: {
                    return n + 2;
                }
                case 7: {
                    return n + 1;
                }
                case 8: {
                    return n + 3;
                }
            }
            return n + 4;
        }
        switch (this.a) {
            case 0: {
                return n + 5;
            }
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: {
                return n;
            }
            case 6: {
                return n + 2;
            }
            case 7: {
                return n + 1;
            }
            case 8: {
                return n + 3;
            }
        }
        return n + 4;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Type)) {
            return false;
        }
        Type type = (Type)object;
        if (this.a != type.a) {
            return false;
        }
        if (this.a == 10 || this.a == 9) {
            if (this.d != type.d) {
                return false;
            }
            int n = this.c;
            int n2 = type.c;
            int n3 = n + this.d;
            while (n < n3) {
                if (this.b[n] != type.b[n2]) {
                    return false;
                }
                ++n;
                ++n2;
            }
        }
        return true;
    }

    public int hashCode() {
        int n = 13 * this.a;
        if (this.a == 10 || this.a == 9) {
            int n2;
            int n3 = n2 + this.d;
            for (n2 = this.c; n2 < n3; ++n2) {
                n = 17 * (n + this.b[n2]);
            }
        }
        return n;
    }

    public String toString() {
        return this.getDescriptor();
    }
}

