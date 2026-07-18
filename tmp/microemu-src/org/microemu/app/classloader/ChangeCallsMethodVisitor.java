/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.classloader;

import java.util.HashMap;
import org.microemu.Injected;
import org.microemu.app.classloader.InstrumentationConfig;
import org.microemu.app.util.MIDletThread;
import org.microemu.app.util.MIDletTimer;
import org.microemu.app.util.MIDletTimerTask;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ChangeCallsMethodVisitor
extends MethodAdapter
implements Opcodes {
    private static final String INJECTED_CLASS;
    static String NEW_SYSTEM_OUT_CLASS;
    static String NEW_SYSTEM_PROPERTIES_CLASS;
    static String NEW_RESOURCE_LOADER_CLASS;
    private HashMap catchInfo;
    private InstrumentationConfig config;

    public ChangeCallsMethodVisitor(MethodVisitor mv, InstrumentationConfig config) {
        super(mv);
        this.config = config;
    }

    public static String codeName(Class klass) {
        return klass.getName().replace('.', '/');
    }

    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        switch (opcode) {
            case 178: {
                if (name.equals("out") && owner.equals("java/lang/System")) {
                    this.mv.visitFieldInsn(opcode, NEW_SYSTEM_OUT_CLASS, name, desc);
                    return;
                }
                if (!name.equals("err") || !owner.equals("java/lang/System")) break;
                this.mv.visitFieldInsn(opcode, NEW_SYSTEM_OUT_CLASS, name, desc);
                return;
            }
        }
        this.mv.visitFieldInsn(opcode, owner, name, desc);
    }

    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        switch (opcode) {
            case 184: {
                if (!name.equals("getProperty") || !owner.equals("java/lang/System")) break;
                this.mv.visitMethodInsn(opcode, NEW_SYSTEM_PROPERTIES_CLASS, name, desc);
                return;
            }
            case 182: {
                if (name.equals("getResourceAsStream") && owner.equals("java/lang/Class")) {
                    this.mv.visitMethodInsn(184, NEW_RESOURCE_LOADER_CLASS, name, "(Ljava/lang/Class;Ljava/lang/String;)Ljava/io/InputStream;");
                    return;
                }
                if (!name.equals("printStackTrace") || !owner.equals("java/lang/Throwable")) break;
                this.mv.visitMethodInsn(184, INJECTED_CLASS, name, "(Ljava/lang/Throwable;)V");
                return;
            }
            case 183: {
                if (!this.config.isEnhanceThreadCreation() || !name.equals("<init>")) break;
                if (owner.equals("java/util/Timer")) {
                    owner = ChangeCallsMethodVisitor.codeName(MIDletTimer.class);
                    break;
                }
                if (owner.equals("java/util/TimerTask")) {
                    owner = ChangeCallsMethodVisitor.codeName(MIDletTimerTask.class);
                    break;
                }
                if (!owner.equals("java/lang/Thread")) break;
                owner = ChangeCallsMethodVisitor.codeName(MIDletThread.class);
            }
        }
        this.mv.visitMethodInsn(opcode, owner, name, desc);
    }

    public void visitTypeInsn(int opcode, String desc) {
        if (opcode == 187 && this.config.isEnhanceThreadCreation()) {
            if ("java/util/Timer".equals(desc)) {
                desc = ChangeCallsMethodVisitor.codeName(MIDletTimer.class);
            } else if ("java/util/TimerTask".equals(desc)) {
                desc = ChangeCallsMethodVisitor.codeName(MIDletTimerTask.class);
            } else if ("java/lang/Thread".equals(desc)) {
                desc = ChangeCallsMethodVisitor.codeName(MIDletThread.class);
            }
        }
        this.mv.visitTypeInsn(opcode, desc);
    }

    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        if (this.config.isEnhanceCatchBlock() && type != null) {
            CatchInformation newHandler;
            if (this.catchInfo == null) {
                this.catchInfo = new HashMap();
            }
            if ((newHandler = (CatchInformation)this.catchInfo.get(handler)) == null) {
                newHandler = new CatchInformation(type);
                this.catchInfo.put(handler, newHandler);
            }
            this.mv.visitTryCatchBlock(start, end, newHandler.label, type);
        } else {
            this.mv.visitTryCatchBlock(start, end, handler, type);
        }
    }

    public void visitLabel(Label label) {
        CatchInformation newHandler;
        if (this.config.isEnhanceCatchBlock() && this.catchInfo != null && (newHandler = (CatchInformation)this.catchInfo.get(label)) != null) {
            this.mv.visitLabel(newHandler.label);
            this.mv.visitMethodInsn(184, INJECTED_CLASS, "handleCatchThrowable", "(Ljava/lang/Throwable;)Ljava/lang/Throwable;");
            this.mv.visitTypeInsn(192, newHandler.type);
        }
        this.mv.visitLabel(label);
    }

    static {
        NEW_SYSTEM_OUT_CLASS = INJECTED_CLASS = ChangeCallsMethodVisitor.codeName(Injected.class);
        NEW_SYSTEM_PROPERTIES_CLASS = INJECTED_CLASS;
        NEW_RESOURCE_LOADER_CLASS = INJECTED_CLASS;
    }

    private static class CatchInformation {
        Label label = new Label();
        String type;

        public CatchInformation(String type) {
            this.type = type;
        }
    }
}

