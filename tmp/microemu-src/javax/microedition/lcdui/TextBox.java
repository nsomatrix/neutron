/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.lcdui;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Screen;
import javax.microedition.lcdui.StringComponent;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.Ticker;
import org.microemu.device.DeviceFactory;
import org.microemu.device.InputMethod;
import org.microemu.device.InputMethodEvent;
import org.microemu.device.InputMethodListener;
import org.microemu.device.ui.TextBoxUI;

public class TextBox
extends Screen {
    TextField tf;
    InputMethodListener inputMethodListener = new InputMethodListener(){

        public void caretPositionChanged(InputMethodEvent event) {
            TextBox.this.setCaretPosition(event.getCaret());
            TextBox.this.tf.setCaretVisible(true);
            TextBox.this.repaint();
        }

        public void inputMethodTextChanged(InputMethodEvent event) {
            TextBox.this.tf.setCaretVisible(false);
            TextBox.this.tf.setString(event.getText(), event.getCaret());
            TextBox.this.repaint();
        }

        public int getCaretPosition() {
            return TextBox.this.getCaretPosition();
        }

        public String getText() {
            return TextBox.this.getString();
        }

        public int getConstraints() {
            return TextBox.this.getConstraints();
        }
    };

    public TextBox(String title, String text, int maxSize, int constraints) {
        super(title);
        this.tf = new TextField(null, text, maxSize, constraints);
        super.setUI(DeviceFactory.getDevice().getUIFactory().createTextBoxUI(this));
    }

    public void delete(int offset, int length) {
        this.tf.delete(offset, length);
    }

    public int getCaretPosition() {
        if (this.ui != null && this.ui.getClass().getName().equals("org.microemu.android.device.ui.AndroidTextBoxUI")) {
            return ((TextBoxUI)this.ui).getCaretPosition();
        }
        return this.tf.getCaretPosition();
    }

    public int getChars(char[] data) {
        return this.tf.getChars(data);
    }

    public int getConstraints() {
        return this.tf.getConstraints();
    }

    public int getMaxSize() {
        return this.tf.getMaxSize();
    }

    public String getString() {
        if (this.ui != null && this.ui.getClass().getName().equals("org.microemu.android.device.ui.AndroidTextBoxUI")) {
            return ((TextBoxUI)this.ui).getString();
        }
        return this.tf.getString();
    }

    public void insert(char[] data, int offset, int length, int position) {
        this.tf.insert(data, offset, length, position);
    }

    public void insert(String src, int position) {
        this.tf.insert(src, position);
    }

    public void setChars(char[] data, int offset, int length) {
        this.tf.setChars(data, offset, length);
    }

    public void setConstraints(int constraints) {
        this.tf.setConstraints(constraints);
    }

    public void setInitialInputMode(String characterSubset) {
    }

    public int setMaxSize(int maxSize) {
        return this.tf.setMaxSize(maxSize);
    }

    public void setString(String text) {
        if (this.ui != null && this.ui.getClass().getName().equals("org.microemu.android.device.ui.AndroidTextBoxUI")) {
            ((TextBoxUI)this.ui).setString(text);
        } else {
            this.tf.setString(text);
        }
    }

    public void setTicker(Ticker ticker) {
    }

    public void setTitle(String s) {
        super.setTitle(s);
    }

    public int size() {
        return this.tf.size();
    }

    void hideNotify() {
        DeviceFactory.getDevice().getInputMethod().removeInputMethodListener(this.inputMethodListener);
        super.hideNotify();
    }

    int paintContent(Graphics g) {
        g.translate(0, this.viewPortY);
        g.drawRect(1, 1, this.getWidth() - 3, this.viewPortHeight - 3);
        g.setClip(3, 3, this.getWidth() - 6, this.viewPortHeight - 6);
        g.translate(3, 3);
        g.translate(0, -this.viewPortY);
        this.tf.paintContent(g);
        return this.tf.stringComponent.getHeight() + 6;
    }

    void setCaretPosition(int position) {
        this.tf.setCaretPosition(position);
        StringComponent tmp = this.tf.stringComponent;
        if (tmp.getCharPositionY(position) < this.viewPortY) {
            this.viewPortY = tmp.getCharPositionY(position);
        } else if (tmp.getCharPositionY(position) + tmp.getCharHeight() > this.viewPortY + this.viewPortHeight - 6) {
            this.viewPortY = tmp.getCharPositionY(position) + tmp.getCharHeight() - (this.viewPortHeight - 6);
        }
    }

    void showNotify() {
        super.showNotify();
        InputMethod inputMethod = DeviceFactory.getDevice().getInputMethod();
        inputMethod.setInputMethodListener(this.inputMethodListener);
        inputMethod.setMaxSize(this.getMaxSize());
        this.setCaretPosition(this.getString().length());
        this.tf.setCaretVisible(true);
    }

    int traverse(int gameKeyCode, int top, int bottom) {
        int traverse = this.tf.traverse(gameKeyCode, top, bottom, true);
        if (traverse == Integer.MAX_VALUE) {
            return 0;
        }
        return traverse;
    }
}

