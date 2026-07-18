/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.lcdui;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringComponent;
import org.microemu.device.DeviceFactory;
import org.microemu.device.InputMethod;
import org.microemu.device.InputMethodEvent;
import org.microemu.device.InputMethodListener;

public class TextField
extends Item {
    public static final int ANY = 0;
    public static final int EMAILADDR = 1;
    public static final int NUMERIC = 2;
    public static final int PHONENUMBER = 3;
    public static final int URL = 4;
    public static final int DECIMAL = 5;
    public static final int PASSWORD = 65536;
    public static final int UNEDITABLE = 131072;
    public static final int SENSITIVE = 262144;
    public static final int NON_PREDICTIVE = 524288;
    public static final int INITIAL_CAPS_WORD = 0x100000;
    public static final int INITIAL_CAPS_SENTENCE = 0x200000;
    public static final int CONSTRAINT_MASK = 65535;
    StringComponent stringComponent;
    private String field;
    private int caret;
    private boolean caretVisible;
    private int maxSize;
    private int constraints;
    private InputMethodListener inputMethodListener = new InputMethodListener(){

        public void caretPositionChanged(InputMethodEvent event) {
            TextField.this.setCaretPosition(event.getCaret());
            TextField.this.setCaretVisible(true);
            TextField.this.repaint();
        }

        public void inputMethodTextChanged(InputMethodEvent event) {
            TextField.this.setCaretVisible(false);
            TextField.this.setString(event.getText(), event.getCaret());
            TextField.this.repaint();
            if (TextField.this.owner instanceof Form) {
                ((Form)TextField.this.owner).fireItemStateListener();
            }
        }

        public int getCaretPosition() {
            return TextField.this.getCaretPosition();
        }

        public String getText() {
            return TextField.this.getString();
        }

        public int getConstraints() {
            return TextField.this.getConstraints();
        }
    };

    public TextField(String label, String text, int maxSize, int constraints) {
        super(label);
        if (maxSize <= 0) {
            throw new IllegalArgumentException();
        }
        this.setConstraints(constraints);
        if (!InputMethod.validate(text, constraints)) {
            throw new IllegalArgumentException();
        }
        this.maxSize = maxSize;
        this.stringComponent = new StringComponent();
        if (text != null) {
            this.setString(text);
        } else {
            this.setString("");
        }
        this.stringComponent.setWidthDecreaser(8);
    }

    public String getString() {
        return this.field;
    }

    public void setString(String text) {
        this.setString(text, text.length());
    }

    void setString(String text, int caret) {
        if (!InputMethod.validate(text, this.constraints)) {
            throw new IllegalArgumentException();
        }
        if (text == null) {
            this.field = "";
            this.stringComponent.setText("");
        } else {
            if (text.length() > this.maxSize) {
                throw new IllegalArgumentException();
            }
            this.field = text;
            if ((this.constraints & 0x10000) == 0) {
                this.stringComponent.setText(text);
            } else {
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < text.length(); ++i) {
                    sb.append('*');
                }
                this.stringComponent.setText(sb.toString());
            }
        }
        this.setCaretPosition(caret);
        this.setCaretVisible(false);
        this.repaint();
    }

    public int getChars(char[] data) {
        if (data.length < this.field.length()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        this.getString().getChars(0, this.field.length(), data, 0);
        return this.field.length();
    }

    public void setChars(char[] data, int offset, int length) {
        if (data == null) {
            this.setString("");
        } else {
            if (length > this.maxSize) {
                throw new IllegalArgumentException();
            }
            String newtext = new String(data, offset, length);
            if (!InputMethod.validate(newtext, this.constraints)) {
                throw new IllegalArgumentException();
            }
            this.setString(newtext);
        }
        this.repaint();
    }

    public void insert(String src, int position) {
        if (!InputMethod.validate(src, this.constraints)) {
            throw new IllegalArgumentException();
        }
        if (this.field.length() + src.length() > this.maxSize) {
            throw new IllegalArgumentException();
        }
        String newtext = "";
        if (position > 0) {
            newtext = this.getString().substring(0, position);
        }
        newtext = newtext + src;
        if (position < this.field.length()) {
            newtext = newtext + this.getString().substring(position + 1);
        }
        this.setString(newtext);
        this.repaint();
    }

    public void insert(char[] data, int offset, int length, int position) {
        if (offset + length > data.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        this.insert(new String(data, offset, length), position);
    }

    public void delete(int offset, int length) {
        if (offset + length > this.field.length()) {
            throw new StringIndexOutOfBoundsException();
        }
        String newtext = "";
        if (offset > 0) {
            newtext = this.getString().substring(0, offset);
        }
        if (offset + length < this.field.length()) {
            newtext = newtext + this.getString().substring(offset + length);
        }
        this.setString(newtext);
        this.repaint();
    }

    public int getMaxSize() {
        return this.maxSize;
    }

    public int setMaxSize(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException();
        }
        if (this.field.length() > maxSize) {
            this.setString(this.getString().substring(0, maxSize));
        }
        this.maxSize = maxSize;
        return maxSize;
    }

    public int size() {
        return this.field.length();
    }

    public int getCaretPosition() {
        return this.caret;
    }

    public void setConstraints(int constraints) {
        if ((constraints & 0xFFFF) < 0 || (constraints & 0xFFFF) > 5) {
            throw new IllegalArgumentException("constraints " + constraints + " is an illegal value");
        }
        this.constraints = constraints;
        if (!InputMethod.validate(this.field, constraints)) {
            this.setString("");
        }
    }

    public int getConstraints() {
        return this.constraints;
    }

    public void setInitialInputMode(String characterSubset) {
    }

    boolean isFocusable() {
        return true;
    }

    int getHeight() {
        return super.getHeight() + this.stringComponent.getHeight() + 8;
    }

    int paint(Graphics g) {
        super.paintContent(g);
        g.translate(0, super.getHeight());
        int savedColor = g.getColor();
        if (!this.hasFocus()) {
            g.setGrayScale(127);
        }
        g.drawRect(1, 1, this.owner.getWidth() - 3, this.stringComponent.getHeight() + 4);
        if (!this.hasFocus()) {
            g.setColor(savedColor);
        }
        g.translate(3, 3);
        this.paintContent(g);
        g.translate(-3, -3);
        g.translate(0, -super.getHeight());
        return this.getHeight();
    }

    void paintContent(Graphics g) {
        this.stringComponent.paint(g);
        if (this.caretVisible) {
            int x_pos = this.stringComponent.getCharPositionX(this.caret);
            int y_pos = this.stringComponent.getCharPositionY(this.caret);
            g.drawLine(x_pos, y_pos, x_pos, y_pos + Font.getDefaultFont().getHeight());
        }
    }

    void setCaretPosition(int position) {
        this.caret = position;
    }

    void setCaretVisible(boolean state) {
        this.caretVisible = state;
    }

    int traverse(int gameKeyCode, int top, int bottom, boolean action) {
        if (gameKeyCode == 1) {
            if (top > 0) {
                return -top;
            }
            return Integer.MAX_VALUE;
        }
        if (gameKeyCode == 6) {
            if (this.getHeight() > bottom) {
                return this.getHeight() - bottom;
            }
            return Integer.MAX_VALUE;
        }
        return 0;
    }

    void setFocus(boolean hasFocus) {
        super.setFocus(hasFocus);
        if (hasFocus) {
            InputMethod inputMethod = DeviceFactory.getDevice().getInputMethod();
            inputMethod.setInputMethodListener(this.inputMethodListener);
            inputMethod.setMaxSize(this.getMaxSize());
            this.setCaretVisible(true);
        } else {
            DeviceFactory.getDevice().getInputMethod().removeInputMethodListener(this.inputMethodListener);
            this.setCaretVisible(false);
        }
    }
}

