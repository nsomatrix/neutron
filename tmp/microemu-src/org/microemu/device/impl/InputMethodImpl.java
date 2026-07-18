/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.impl;

import org.microemu.MIDletBridge;
import org.microemu.device.DeviceFactory;
import org.microemu.device.InputMethod;
import org.microemu.device.InputMethodEvent;
import org.microemu.device.InputMethodListener;
import org.microemu.device.impl.Button;

public abstract class InputMethodImpl
extends InputMethod
implements Runnable {
    protected boolean resetKey;
    protected Button lastButton = null;
    protected int lastButtonCharIndex = -1;
    private boolean cancel = false;
    private Thread t = new Thread((Runnable)this, "InputMethodThread");

    public InputMethodImpl() {
        this.t.setDaemon(true);
        this.t.start();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void dispose() {
        this.cancel = true;
        InputMethodImpl inputMethodImpl = this;
        synchronized (inputMethodImpl) {
            this.notify();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void run() {
        while (!this.cancel) {
            InputMethodImpl inputMethodImpl;
            try {
                this.resetKey = true;
                inputMethodImpl = this;
                synchronized (inputMethodImpl) {
                    this.wait(1500L);
                }
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            inputMethodImpl = this;
            synchronized (inputMethodImpl) {
                int caret;
                if (this.resetKey && this.lastButton != null && this.inputMethodListener != null && (caret = this.inputMethodListener.getCaretPosition() + 1) <= this.inputMethodListener.getText().length()) {
                    this.lastButton = null;
                    this.lastButtonCharIndex = -1;
                    InputMethodEvent event = new InputMethodEvent(1, caret, this.inputMethodListener.getText());
                    this.inputMethodListener.caretPositionChanged(event);
                }
            }
        }
    }

    public void setInputMethodListener(InputMethodListener l) {
        super.setInputMethodListener(l);
        this.lastButton = null;
        this.lastButtonCharIndex = -1;
    }

    public void pointerPressed(int x, int y) {
        if (DeviceFactory.getDevice().hasPointerEvents()) {
            MIDletBridge.getMIDletAccess().getDisplayAccess().pointerPressed(x, y);
        }
    }

    public void pointerReleased(int x, int y) {
        if (DeviceFactory.getDevice().hasPointerEvents()) {
            MIDletBridge.getMIDletAccess().getDisplayAccess().pointerReleased(x, y);
        }
    }

    public void pointerDragged(int x, int y) {
        if (DeviceFactory.getDevice().hasPointerMotionEvents()) {
            MIDletBridge.getMIDletAccess().getDisplayAccess().pointerDragged(x, y);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void insertText(String str) {
        if (str.length() > 0) {
            int caret = this.inputMethodListener.getCaretPosition();
            String tmp = "";
            InputMethodImpl inputMethodImpl = this;
            synchronized (inputMethodImpl) {
                if (this.lastButton != null) {
                    ++caret;
                    this.lastButton = null;
                    this.lastButtonCharIndex = -1;
                }
                if (caret > 0) {
                    tmp = tmp + this.inputMethodListener.getText().substring(0, caret);
                }
                tmp = tmp + str;
                if (caret < this.inputMethodListener.getText().length()) {
                    tmp = tmp + this.inputMethodListener.getText().substring(caret);
                }
                caret += str.length();
            }
            if (!InputMethodImpl.validate(tmp, this.inputMethodListener.getConstraints())) {
                return;
            }
            InputMethodEvent event = new InputMethodEvent(2, caret, tmp);
            this.inputMethodListener.inputMethodTextChanged(event);
            event = new InputMethodEvent(1, caret, tmp);
            this.inputMethodListener.caretPositionChanged(event);
        }
    }

    protected char[] filterConstraints(char[] chars) {
        int i;
        char[] result = new char[chars.length];
        int j = 0;
        block8: for (i = 0; i < chars.length; ++i) {
            switch (this.inputMethodListener.getConstraints() & 0xFFFF) {
                case 0: {
                    result[j] = chars[i];
                    ++j;
                    continue block8;
                }
                case 1: {
                    continue block8;
                }
                case 2: {
                    if (!Character.isDigit(chars[i]) && chars[i] != '-') continue block8;
                    result[j] = chars[i];
                    ++j;
                    continue block8;
                }
                case 3: {
                    continue block8;
                }
                case 4: {
                    if (chars[i] == '\n') continue block8;
                    result[j] = chars[i];
                    ++j;
                    continue block8;
                }
                case 5: {
                    if (!Character.isDigit(chars[i]) && chars[i] != '-' && chars[i] != '.') continue block8;
                    result[j] = chars[i];
                    ++j;
                }
            }
        }
        if (i != j) {
            char[] newresult = new char[j];
            System.arraycopy(result, 0, newresult, 0, j);
            result = newresult;
        }
        return result;
    }

    protected char[] filterInputMode(char[] chars) {
        int i;
        if (chars == null) {
            return new char[0];
        }
        int inputMode = this.getInputMode();
        char[] result = new char[chars.length];
        int j = 0;
        for (i = 0; i < chars.length; ++i) {
            if (inputMode == 2) {
                result[j] = Character.toUpperCase(chars[i]);
                ++j;
                continue;
            }
            if (inputMode == 3) {
                result[j] = Character.toLowerCase(chars[i]);
                ++j;
                continue;
            }
            if (inputMode != 1 || !Character.isDigit(chars[i]) && chars[i] != '-' && chars[i] != '.') continue;
            result[j] = chars[i];
            ++j;
        }
        if (i != j) {
            char[] newresult = new char[j];
            System.arraycopy(result, 0, newresult, 0, j);
            result = newresult;
        }
        return result;
    }
}

