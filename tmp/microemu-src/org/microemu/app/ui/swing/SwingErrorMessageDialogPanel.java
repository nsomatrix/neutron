/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.ui.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import org.microemu.app.ui.MessageListener;
import org.microemu.app.ui.swing.SwingDialogPanel;
import org.microemu.app.ui.swing.SwingDialogWindow;

public class SwingErrorMessageDialogPanel
extends SwingDialogPanel
implements MessageListener {
    private static final long serialVersionUID = 1L;
    private Frame parent;
    private JLabel iconLabel;
    private JLabel textLabel;
    private JTextArea stackTraceArea;
    private JScrollPane stackTracePane;

    public SwingErrorMessageDialogPanel(Frame parent) {
        this.parent = parent;
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.ipadx = 10;
        c.ipady = 10;
        c.gridx = 0;
        c.gridy = 0;
        this.iconLabel = new JLabel();
        this.add((Component)this.iconLabel, c);
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1.0;
        this.textLabel = new JLabel();
        this.add((Component)this.textLabel, c);
        this.stackTraceArea = new JTextArea();
        this.stackTraceArea.setEditable(false);
        this.stackTracePane = new JScrollPane(this.stackTraceArea);
        this.stackTracePane.setPreferredSize(new Dimension(250, 250));
    }

    public void showMessage(int level, String title, String text, Throwable throwable) {
        switch (level) {
            case 0: {
                this.iconLabel.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
                break;
            }
            case 2: {
                this.iconLabel.setIcon(UIManager.getIcon("OptionPane.warningIcon"));
                break;
            }
            default: {
                this.iconLabel.setIcon(UIManager.getIcon("OptionPane.informationIcon"));
            }
        }
        this.textLabel.setText(text);
        if (throwable != null) {
            StringWriter writer = new StringWriter();
            throwable.printStackTrace(new PrintWriter(writer));
            this.stackTraceArea.setText(writer.toString());
            this.stackTraceArea.setCaretPosition(0);
            GridBagConstraints c = new GridBagConstraints();
            c.fill = 1;
            c.gridx = 0;
            c.gridy = 1;
            c.gridwidth = 2;
            c.weightx = 1.0;
            c.weighty = 1.0;
            this.add((Component)this.stackTracePane, c);
        }
        SwingDialogWindow.show(this.parent, title, this, false);
        if (throwable != null) {
            this.remove(this.stackTracePane);
        }
    }
}

