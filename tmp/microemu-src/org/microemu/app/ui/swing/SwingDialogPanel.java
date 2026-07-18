/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.ui.swing;

import javax.swing.JButton;
import javax.swing.JPanel;

public class SwingDialogPanel
extends JPanel {
    public JButton btOk = new JButton("OK");
    public JButton btCancel = new JButton("Cancel");
    boolean state;
    boolean extra;

    public boolean check(boolean state) {
        return true;
    }

    protected void hideNotify() {
    }

    protected void showNotify() {
    }

    protected JButton getExtraButton() {
        return null;
    }

    public boolean isExtraButtonPressed() {
        return this.extra;
    }
}

