/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.ui.swing;

import javax.swing.JTextField;
import org.microemu.app.ui.swing.SwingDialogPanel;

public class MIDletUrlPanel
extends SwingDialogPanel {
    private static final long serialVersionUID = 1L;
    private JTextField jadUrlField = new JTextField(50);

    public MIDletUrlPanel() {
        this.add(this.jadUrlField);
    }

    public String getText() {
        return this.jadUrlField.getText();
    }

    protected void showNotify() {
        this.jadUrlField.setText("");
    }
}

