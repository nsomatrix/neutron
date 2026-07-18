/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.classloader;

import java.io.IOException;
import java.io.InputStream;
import org.microemu.app.classloader.ChangeCallsClassVisitor;
import org.microemu.app.classloader.InstrumentationConfig;
import org.microemu.log.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class ClassPreprocessor {
    public static byte[] instrument(InputStream classInputStream, InstrumentationConfig config) {
        try {
            ClassReader cr = new ClassReader(classInputStream);
            ClassWriter cw = new ClassWriter(0);
            ChangeCallsClassVisitor cv = new ChangeCallsClassVisitor(cw, config);
            cr.accept(cv, 0);
            return cw.toByteArray();
        }
        catch (IOException e) {
            Logger.error("Error loading MIDlet class", e);
            return null;
        }
    }
}

