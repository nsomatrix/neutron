/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.ui.swing.logconsole;

import java.awt.Rectangle;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import org.microemu.app.ui.swing.logconsole.LogTextCaret;

public class LogTextArea
extends JTextArea {
    private static final long serialVersionUID = 1L;
    private LogTextCaret caret = new LogTextCaret();

    public LogTextArea(int rows, int columns, int maxLines) {
        super(rows, columns);
        this.setCaret(this.caret);
        this.setEditable(false);
    }

    public void setText(String t) {
        super.setText(t);
        this.caret.setVisibilityAdjustment(true);
    }

    public void append(String str) {
        super.append(str);
        JViewport viewport = (JViewport)this.getParent();
        boolean scrollToBottom = Math.abs(viewport.getViewPosition().getY() - (double)(this.getHeight() - viewport.getHeight())) < 100.0;
        this.caret.setVisibilityAdjustment(scrollToBottom);
        if (scrollToBottom) {
            this.setCaretPosition(this.getText().length());
        }
    }

    class SafeScroller
    implements Runnable {
        Rectangle r;

        SafeScroller(Rectangle r) {
            this.r = r;
        }

        public void run() {
            LogTextArea.this.scrollRectToVisible(this.r);
        }
    }
}

