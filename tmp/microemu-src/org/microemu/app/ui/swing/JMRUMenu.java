/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.ui.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.microemu.app.util.MRUListListener;

public class JMRUMenu
extends JMenu
implements MRUListListener {
    private static final long serialVersionUID = 1L;
    static /* synthetic */ Class class$java$awt$event$ActionListener;

    public JMRUMenu(String s) {
        super(s);
    }

    public void listItemChanged(final Object item) {
        String label = item.toString();
        for (int i = 0; i < this.getItemCount(); ++i) {
            if (!this.getItem(i).getText().equals(label)) continue;
            this.remove(i);
            break;
        }
        AbstractAction a = new AbstractAction(label){
            private static final long serialVersionUID = 1L;
            Object sourceMRU;
            {
                super(x0);
                this.sourceMRU = item;
            }

            public void actionPerformed(ActionEvent e) {
                JMRUMenu.this.fireActionPerformed(new MRUActionEvent(this.sourceMRU, e));
            }
        };
        JMenuItem menu = new JMenuItem(a);
        this.insert(menu, 0);
    }

    protected void fireActionPerformed(ActionEvent event) {
        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] != (class$java$awt$event$ActionListener == null ? JMRUMenu.class$("java.awt.event.ActionListener") : class$java$awt$event$ActionListener)) continue;
            ((ActionListener)listeners[i + 1]).actionPerformed(event);
        }
    }

    public static class MRUActionEvent
    extends ActionEvent {
        private static final long serialVersionUID = 1L;
        Object sourceMRU;

        public MRUActionEvent(Object sourceMRU, ActionEvent orig) {
            super(orig.getSource(), orig.getID(), orig.getActionCommand(), orig.getWhen(), orig.getModifiers());
            this.sourceMRU = sourceMRU;
        }

        public Object getSourceMRU() {
            return this.sourceMRU;
        }
    }
}

