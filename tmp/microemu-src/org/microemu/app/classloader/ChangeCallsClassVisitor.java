/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.classloader;

import java.util.HashMap;
import java.util.Map;
import org.microemu.app.classloader.ChangeCallsMethodVisitor;
import org.microemu.app.classloader.InstrumentationConfig;
import org.microemu.app.util.MIDletThread;
import org.microemu.app.util.MIDletTimer;
import org.microemu.app.util.MIDletTimerTask;
import org.microemu.log.Logger;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class ChangeCallsClassVisitor
extends ClassAdapter {
    InstrumentationConfig config;
    static final Map javaVersion = new HashMap();

    public ChangeCallsClassVisitor(ClassVisitor cv, InstrumentationConfig config) {
        super(cv);
        this.config = config;
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        if ((0xFF & version) >= 49) {
            String v = (String)javaVersion.get(new Integer(version));
            Logger.warn("Loading MIDlet class " + name + " of version " + version + (v == null ? "" : " " + v));
        }
        if (this.config.isEnhanceThreadCreation()) {
            if (superName.equals("java/lang/Thread")) {
                superName = ChangeCallsMethodVisitor.codeName(MIDletThread.class);
            } else if (superName.equals("java/util/Timer")) {
                superName = ChangeCallsMethodVisitor.codeName(MIDletTimer.class);
            } else if (superName.equals("java/util/TimerTask")) {
                superName = ChangeCallsMethodVisitor.codeName(MIDletTimerTask.class);
            }
        }
        super.visit(version, access, name, signature, superName, interfaces);
    }

    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return new ChangeCallsMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions), this.config);
    }

    static {
        javaVersion.put(new Integer(196653), "1.1");
        javaVersion.put(new Integer(196654), "1.2");
        javaVersion.put(new Integer(47), "1.3");
        javaVersion.put(new Integer(48), "1.4");
        javaVersion.put(new Integer(49), "1.5");
        javaVersion.put(new Integer(50), "1.6");
    }
}

