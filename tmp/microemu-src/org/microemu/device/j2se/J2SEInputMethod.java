/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.j2se;

import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import org.microemu.DisplayAccess;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.device.DeviceFactory;
import org.microemu.device.InputMethodEvent;
import org.microemu.device.impl.ButtonDetaultDeviceKeyCodes;
import org.microemu.device.impl.ButtonName;
import org.microemu.device.impl.InputMethodImpl;
import org.microemu.device.impl.SoftButton;
import org.microemu.device.impl.ui.CommandManager;
import org.microemu.device.j2se.J2SEButton;
import org.microemu.device.j2se.J2SEDeviceButtonsHelper;
import org.microemu.util.ThreadUtils;

public class J2SEInputMethod
extends InputMethodImpl {
    private boolean eventAlreadyConsumed;
    private Timer keyReleasedDelayTimer;
    private List repeatModeKeyCodes = new Vector();

    public J2SEInputMethod() {
        this.keyReleasedDelayTimer = ThreadUtils.createTimer("InputKeyReleasedDelayTimer");
    }

    public int getGameAction(int keyCode) {
        Iterator it = DeviceFactory.getDevice().getButtons().iterator();
        while (it.hasNext()) {
            J2SEButton button = (J2SEButton)it.next();
            if (button.getKeyCode() != keyCode) continue;
            return ButtonDetaultDeviceKeyCodes.getGameAction(button.getFunctionalName());
        }
        return 0;
    }

    public int getKeyCode(int gameAction) {
        ButtonName name = ButtonDetaultDeviceKeyCodes.getButtonNameByGameAction(gameAction);
        return J2SEDeviceButtonsHelper.getButton(name).getKeyCode();
    }

    public String getKeyName(int keyCode) throws IllegalArgumentException {
        Iterator it = DeviceFactory.getDevice().getButtons().iterator();
        while (it.hasNext()) {
            J2SEButton button = (J2SEButton)it.next();
            if (button.getKeyCode() != keyCode) continue;
            return button.getName();
        }
        return Character.toString((char)keyCode);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean fireInputMethodListener(J2SEButton button, char keyChar) {
        MIDletAccess ma = MIDletBridge.getMIDletAccess();
        if (ma == null) {
            return false;
        }
        DisplayAccess da = ma.getDisplayAccess();
        if (da == null) {
            return false;
        }
        int keyCode = keyChar;
        if (button != null && keyChar == 0) {
            keyCode = button.getKeyCode();
        }
        if (this.inputMethodListener == null) {
            da.keyPressed(keyCode);
            return true;
        }
        if (button == null) {
            return true;
        }
        ButtonName functionalName = button.getFunctionalName();
        if (functionalName == ButtonName.UP || functionalName == ButtonName.DOWN) {
            da.keyPressed(button.getKeyCode());
            return true;
        }
        int caret = this.inputMethodListener.getCaretPosition();
        if (button.isModeChange()) {
            switch (this.inputMethodListener.getConstraints() & 0xFFFF) {
                case 0: 
                case 1: 
                case 4: {
                    if (this.getInputMode() == 1) {
                        this.setInputMode(2);
                    } else if (this.getInputMode() == 2) {
                        this.setInputMode(3);
                    } else if (this.getInputMode() == 3) {
                        this.setInputMode(1);
                    }
                    J2SEInputMethod j2SEInputMethod = this;
                    synchronized (j2SEInputMethod) {
                        if (this.lastButton != null) {
                            ++caret;
                            this.lastButton = null;
                            this.lastButtonCharIndex = -1;
                        }
                    }
                    InputMethodEvent event = new InputMethodEvent(1, caret, this.inputMethodListener.getText());
                    this.inputMethodListener.caretPositionChanged(event);
                }
            }
            return true;
        }
        if (functionalName == ButtonName.LEFT || functionalName == ButtonName.RIGHT) {
            Object event = this;
            synchronized (event) {
                if (functionalName == ButtonName.LEFT && caret > 0) {
                    --caret;
                } else if (functionalName == ButtonName.RIGHT && caret < this.inputMethodListener.getText().length()) {
                    ++caret;
                }
                this.lastButton = null;
                this.lastButtonCharIndex = -1;
            }
            event = new InputMethodEvent(1, caret, this.inputMethodListener.getText());
            this.inputMethodListener.caretPositionChanged((InputMethodEvent)event);
            return true;
        }
        if (functionalName == ButtonName.BACK_SPACE) {
            String tmp = "";
            J2SEInputMethod j2SEInputMethod = this;
            synchronized (j2SEInputMethod) {
                if (this.lastButton != null) {
                    ++caret;
                    this.lastButton = null;
                    this.lastButtonCharIndex = -1;
                }
                if (caret > 0) {
                    if (--caret > 0) {
                        tmp = tmp + this.inputMethodListener.getText().substring(0, caret);
                    }
                    if (caret < this.inputMethodListener.getText().length() - 1) {
                        tmp = tmp + this.inputMethodListener.getText().substring(caret + 1);
                    }
                }
            }
            if (!J2SEInputMethod.validate(tmp, this.inputMethodListener.getConstraints())) {
                return true;
            }
            InputMethodEvent event = new InputMethodEvent(2, caret, tmp);
            this.inputMethodListener.inputMethodTextChanged(event);
            event = new InputMethodEvent(1, caret, tmp);
            this.inputMethodListener.caretPositionChanged(event);
            return true;
        }
        if (functionalName == ButtonName.DELETE) {
            String tmp = this.inputMethodListener.getText();
            Object event = this;
            synchronized (event) {
                if (this.lastButton != null) {
                    this.lastButton = null;
                    this.lastButtonCharIndex = -1;
                }
                if (caret != this.inputMethodListener.getText().length()) {
                    tmp = this.inputMethodListener.getText().substring(0, caret) + this.inputMethodListener.getText().substring(caret + 1);
                }
            }
            if (!J2SEInputMethod.validate(tmp, this.inputMethodListener.getConstraints())) {
                return true;
            }
            event = new InputMethodEvent(2, caret, tmp);
            this.inputMethodListener.inputMethodTextChanged((InputMethodEvent)event);
            event = new InputMethodEvent(1, caret, tmp);
            this.inputMethodListener.caretPositionChanged((InputMethodEvent)event);
            return true;
        }
        if (this.inputMethodListener.getText().length() < this.maxSize) {
            StringBuffer editText = new StringBuffer(this.inputMethodListener.getText());
            Object event = this;
            synchronized (event) {
                ++this.lastButtonCharIndex;
                char[] buttonChars = this.filterConstraints(this.filterInputMode(button.getChars(this.getInputMode())));
                if (keyChar != 0) {
                    editText.append((char)keyChar);
                    ++caret;
                    this.lastButton = null;
                    this.lastButtonCharIndex = -1;
                } else if (buttonChars.length > 0) {
                    if (this.lastButtonCharIndex == buttonChars.length) {
                        if (buttonChars.length == 1) {
                            if (this.lastButton != null) {
                                ++caret;
                            }
                            this.lastButton = null;
                        } else {
                            this.lastButtonCharIndex = 0;
                        }
                    }
                    if (this.lastButton != button) {
                        if (this.lastButton != null) {
                            ++caret;
                        }
                        if (editText.length() < caret) {
                            editText.append(buttonChars[0]);
                        } else {
                            editText.insert(caret, buttonChars[0]);
                        }
                        this.lastButton = button;
                        this.lastButtonCharIndex = 0;
                    } else {
                        editText.setCharAt(caret, buttonChars[this.lastButtonCharIndex]);
                        this.lastButton = button;
                    }
                } else {
                    this.lastButton = null;
                    this.lastButtonCharIndex = -1;
                }
                this.resetKey = false;
                this.notify();
            }
            if (!J2SEInputMethod.validate(editText.toString(), this.inputMethodListener.getConstraints())) {
                return false;
            }
            event = new InputMethodEvent(2, caret, editText.toString());
            this.inputMethodListener.inputMethodTextChanged((InputMethodEvent)event);
        }
        return false;
    }

    public void buttonTyped(J2SEButton button) {
        if (this.eventAlreadyConsumed) {
            return;
        }
    }

    public void clipboardPaste(String str) {
        if (this.inputMethodListener != null && this.inputMethodListener.getText() != null && this.inputMethodListener.getText().length() + str.length() <= this.maxSize) {
            this.insertText(str);
        }
        this.eventAlreadyConsumed = true;
    }

    public void buttonPressed(J2SEButton button, char keyChar) {
        Command cmd;
        int keyCode = keyChar;
        if (button != null && keyChar == 0) {
            keyCode = button.getKeyCode();
        }
        this.eventAlreadyConsumed = false;
        if (DeviceFactory.getDevice().hasRepeatEvents()) {
            if (this.repeatModeKeyCodes.contains(new Integer(keyCode))) {
                MIDletAccess ma = MIDletBridge.getMIDletAccess();
                if (ma == null) {
                    return;
                }
                DisplayAccess da = ma.getDisplayAccess();
                if (da == null) {
                    return;
                }
                da.keyRepeated(keyCode);
                this.eventAlreadyConsumed = true;
                return;
            }
            this.repeatModeKeyCodes.add(new Integer(keyCode));
        }
        boolean rawSoftKeys = DeviceFactory.getDevice().getDeviceDisplay().isFullScreenMode();
        if (button instanceof SoftButton && !rawSoftKeys && (cmd = ((SoftButton)((Object)button)).getCommand()) != null) {
            MIDletAccess ma = MIDletBridge.getMIDletAccess();
            if (ma == null) {
                return;
            }
            DisplayAccess da = ma.getDisplayAccess();
            if (da == null) {
                return;
            }
            if (cmd.equals(CommandManager.CMD_MENU)) {
                CommandManager.getInstance().commandAction(cmd);
            } else {
                da.commandAction(cmd, da.getCurrent());
            }
            this.eventAlreadyConsumed = true;
            return;
        }
        if (this.fireInputMethodListener(button, (char)keyChar)) {
            this.eventAlreadyConsumed = true;
            return;
        }
    }

    public void buttonReleased(J2SEButton button, char keyChar) {
        int keyCode = keyChar;
        if (button != null && keyChar == 0) {
            keyCode = button.getKeyCode();
        }
        if (DeviceFactory.getDevice().hasRepeatEvents()) {
            this.repeatModeKeyCodes.remove(new Integer(keyCode));
            this.keyReleasedDelayTimer.schedule((TimerTask)new KeyReleasedDelayTask(keyCode), 50L);
        } else {
            MIDletAccess ma = MIDletBridge.getMIDletAccess();
            if (ma == null) {
                return;
            }
            DisplayAccess da = ma.getDisplayAccess();
            if (da == null) {
                return;
            }
            da.keyReleased(keyCode);
            this.eventAlreadyConsumed = false;
        }
    }

    public J2SEButton getButton(KeyEvent ev) {
        J2SEButton button = J2SEDeviceButtonsHelper.getButton(ev);
        if (button != null) {
            return button;
        }
        if (this.getInputMode() != 1) {
            Enumeration e = DeviceFactory.getDevice().getButtons().elements();
            while (e.hasMoreElements()) {
                button = (J2SEButton)e.nextElement();
                if (!button.isChar(ev.getKeyChar(), this.getInputMode())) continue;
                return button;
            }
        }
        return null;
    }

    private class KeyReleasedDelayTask
    extends TimerTask {
        private int repeatModeKeyCode;

        KeyReleasedDelayTask(int repeatModeKeyCode) {
            this.repeatModeKeyCode = repeatModeKeyCode;
        }

        public void run() {
            if (this.repeatModeKeyCode != Integer.MIN_VALUE) {
                MIDletAccess ma = MIDletBridge.getMIDletAccess();
                if (ma == null) {
                    return;
                }
                DisplayAccess da = ma.getDisplayAccess();
                if (da == null) {
                    return;
                }
                da.keyReleased(this.repeatModeKeyCode);
                J2SEInputMethod.this.eventAlreadyConsumed = false;
                this.repeatModeKeyCode = Integer.MIN_VALUE;
            }
        }
    }
}

