/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.ui.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import org.microemu.app.ui.swing.SwingDialogPanel;

public class SwingDialogWindow {
    public static boolean show(Frame parent, String title, final SwingDialogPanel panel, boolean hasCancel) {
        int y;
        JButton extraButton;
        final JDialog dialog = new JDialog(parent, title, true);
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add((Component)panel, "Center");
        JPanel actionPanel = new JPanel();
        actionPanel.add(panel.btOk);
        if (hasCancel) {
            actionPanel.add(panel.btCancel);
        }
        if ((extraButton = panel.getExtraButton()) != null) {
            actionPanel.add(extraButton);
        }
        dialog.getContentPane().add((Component)actionPanel, "South");
        dialog.pack();
        Dimension frameSize = dialog.getSize();
        int x = parent.getLocation().x + (parent.getWidth() - frameSize.width) / 2;
        if (x < 0) {
            x = 0;
        }
        if ((y = parent.getLocation().y + (parent.getHeight() - frameSize.height) / 2) < 0) {
            y = 0;
        }
        dialog.setLocation(x, y);
        ActionListener closeListener = new ActionListener(){

            public void actionPerformed(ActionEvent event) {
                Object source = event.getSource();
                panel.extra = false;
                if (source == panel.btOk || source == extraButton) {
                    if (panel.check(true)) {
                        if (source == extraButton) {
                            panel.extra = true;
                        }
                        panel.state = true;
                        dialog.setVisible(false);
                        panel.hideNotify();
                    }
                } else {
                    panel.state = false;
                    dialog.setVisible(false);
                    panel.hideNotify();
                }
            }
        };
        WindowAdapter windowAdapter = new WindowAdapter(){

            public void windowClosing(WindowEvent e) {
                panel.state = false;
                panel.hideNotify();
            }
        };
        dialog.addWindowListener(windowAdapter);
        panel.btOk.addActionListener(closeListener);
        panel.btCancel.addActionListener(closeListener);
        if (extraButton != null) {
            extraButton.addActionListener(closeListener);
        }
        panel.showNotify();
        dialog.setVisible(true);
        panel.btOk.removeActionListener(closeListener);
        panel.btCancel.removeActionListener(closeListener);
        if (extraButton != null) {
            extraButton.removeActionListener(closeListener);
        }
        return panel.state;
    }
}

