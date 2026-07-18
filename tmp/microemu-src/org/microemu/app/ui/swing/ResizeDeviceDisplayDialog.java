/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.ui.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import org.microemu.app.ui.swing.SwingDialogPanel;

public class ResizeDeviceDisplayDialog
extends SwingDialogPanel {
    private static final long serialVersionUID = 1L;
    private IntegerField widthField = new IntegerField(5, 1, 9999);
    private IntegerField heightField = new IntegerField(5, 1, 9999);

    public ResizeDeviceDisplayDialog() {
        this.add(new JLabel("Width:"));
        this.add(this.widthField);
        this.add(new JLabel("Height:"));
        this.add(this.heightField);
        JButton swapButton = new JButton("Swap");
        swapButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                String tmp = ResizeDeviceDisplayDialog.this.widthField.getText();
                ResizeDeviceDisplayDialog.this.widthField.setText(ResizeDeviceDisplayDialog.this.heightField.getText());
                ResizeDeviceDisplayDialog.this.heightField.setText(tmp);
            }
        });
        this.add(swapButton);
    }

    public void setDeviceDisplaySize(int width, int height) {
        this.widthField.setText("" + width);
        this.heightField.setText("" + height);
    }

    public int getDeviceDisplayWidth() {
        return Integer.parseInt(this.widthField.getText());
    }

    public int getDeviceDisplayHeight() {
        return Integer.parseInt(this.heightField.getText());
    }

    private class IntegerField
    extends JTextField {
        private static final long serialVersionUID = 1L;
        private int minValue;
        private int maxValue;

        public IntegerField(int cols, int minValue, int maxValue) {
            super(cols);
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        protected Document createDefaultModel() {
            return new IntegerDocument();
        }

        class IntegerDocument
        extends PlainDocument {
            private static final long serialVersionUID = 1L;

            IntegerDocument() {
            }

            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if (str == null) {
                    return;
                }
                char[] test = str.toCharArray();
                for (int i = 0; i < test.length; ++i) {
                    if (Character.isDigit(test[i])) continue;
                    return;
                }
                String prevText = this.getText(0, this.getLength());
                super.insertString(offs, str, a);
                int testValue = Integer.parseInt(this.getText(0, this.getLength()));
                if (testValue < IntegerField.this.minValue | testValue > IntegerField.this.maxValue) {
                    this.replace(0, this.getLength(), prevText, a);
                }
            }
        }
    }
}

