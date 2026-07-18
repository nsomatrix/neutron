/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.ui.swing.logconsole;

import java.awt.Rectangle;
import javax.swing.text.DefaultCaret;

public class LogTextCaret
extends DefaultCaret {
    private static final long serialVersionUID = 1L;
    private boolean visibilityAdjustmentEnabled = true;

    protected void adjustVisibility(Rectangle nloc) {
        if (this.visibilityAdjustmentEnabled) {
            super.adjustVisibility(nloc);
        }
    }

    public void setVisibilityAdjustment(boolean flag) {
        this.visibilityAdjustmentEnabled = flag;
    }
}

