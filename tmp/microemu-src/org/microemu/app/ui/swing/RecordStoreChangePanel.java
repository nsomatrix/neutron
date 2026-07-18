/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.ui.swing;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import org.microemu.app.Common;
import org.microemu.app.ui.swing.SwingDialogPanel;
import org.microemu.app.util.FileRecordStoreManager;

public class RecordStoreChangePanel
extends SwingDialogPanel {
    private static final long serialVersionUID = 1L;
    private Common common;
    private JComboBox selectStoreCombo = new JComboBox<String>(new String[]{"File record store", "Memory record store"});

    public RecordStoreChangePanel(Common common) {
        this.common = common;
        this.add(new JLabel("Record store type:"));
        this.add(this.selectStoreCombo);
    }

    protected void showNotify() {
        if (this.common.getRecordStoreManager() instanceof FileRecordStoreManager) {
            this.selectStoreCombo.setSelectedIndex(0);
        } else {
            this.selectStoreCombo.setSelectedIndex(1);
        }
    }

    public String getSelectedRecordStoreName() {
        return (String)this.selectStoreCombo.getSelectedItem();
    }
}

