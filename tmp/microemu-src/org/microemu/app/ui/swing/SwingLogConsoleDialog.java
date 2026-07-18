/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.ui.swing;

import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import org.microemu.app.Config;
import org.microemu.app.ui.swing.logconsole.LogTextArea;
import org.microemu.app.util.RuntimeDetect;
import org.microemu.log.Logger;
import org.microemu.log.LoggerAppender;
import org.microemu.log.LoggingEvent;
import org.microemu.log.QueueAppender;
import org.microemu.log.StdOutAppender;

public class SwingLogConsoleDialog
extends JFrame
implements LoggerAppender {
    private static final long serialVersionUID = 1L;
    private static final boolean tests = false;
    private boolean isShown;
    private LogTextArea logArea;
    private Vector logLinesQueue = new Vector();
    private int testEventCounter = 0;

    public SwingLogConsoleDialog(Frame owner, QueueAppender logQueueAppender) {
        super("Log console");
        this.setIconImage(owner.getIconImage());
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Log");
        JMenuItem menuClear = new JMenuItem("Clear");
        menuClear.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                SwingLogConsoleDialog.this.logArea.setText("");
            }
        });
        menu.add(menuClear);
        menu.addSeparator();
        final JCheckBoxMenuItem menuRecordLocation = new JCheckBoxMenuItem("Show record location");
        menuRecordLocation.setState(Logger.isLocationEnabled());
        menuRecordLocation.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                Logger.setLocationEnabled(menuRecordLocation.getState());
                Config.setLogConsoleLocationEnabled(menuRecordLocation.getState());
            }
        });
        menu.add(menuRecordLocation);
        final JCheckBoxMenuItem menuStdOut = new JCheckBoxMenuItem("Write to standard output");
        menuStdOut.setState(StdOutAppender.enabled);
        menuStdOut.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                StdOutAppender.enabled = menuStdOut.getState();
            }
        });
        menu.add(menuStdOut);
        menuBar.add(menu);
        if (RuntimeDetect.isJava15()) {
            JMenu j5Menu = new JMenu("Threads");
            JMenuItem menuThreadDump = new JMenuItem("ThreadDump to console");
            menuThreadDump.addActionListener(new ActionListener(){

                public void actionPerformed(ActionEvent e) {
                    Logger.threadDumpToConsole();
                }
            });
            j5Menu.add(menuThreadDump);
            JMenuItem menuThreadDumpFile = new JMenuItem("ThreadDump to file");
            menuThreadDumpFile.addActionListener(new ActionListener(){

                public void actionPerformed(ActionEvent e) {
                    Logger.threadDumpToFile();
                }
            });
            j5Menu.add(menuThreadDumpFile);
            menuBar.add(j5Menu);
        }
        this.setJMenuBar(menuBar);
        this.logArea = new LogTextArea(20, 40, 1000);
        Font logFont = new Font("Monospaced", 0, 12);
        this.logArea.setFont(logFont);
        JScrollPane scrollPane = new JScrollPane(this.logArea);
        scrollPane.setAutoscrolls(false);
        this.getContentPane().add(scrollPane);
        Logger.addAppender(this);
        Logger.removeAppender(logQueueAppender);
        LoggingEvent event = null;
        while ((event = logQueueAppender.poll()) != null) {
            this.append(event);
        }
    }

    public void setVisible(boolean b) {
        super.setVisible(b);
        this.isShown = true;
        if (this.isShown) {
            SwingUtilities.invokeLater(new SwingLogUpdater());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void log(String message) {
        boolean createUpdater = false;
        Vector vector = this.logLinesQueue;
        synchronized (vector) {
            if (this.logLinesQueue.isEmpty()) {
                createUpdater = true;
            }
            this.logLinesQueue.addElement(message);
        }
        if (createUpdater && this.isShown) {
            SwingUtilities.invokeLater(new SwingLogUpdater());
        }
    }

    private String formatLocation(StackTraceElement ste) {
        if (ste == null) {
            return "";
        }
        return ste.getClassName() + "." + ste.getMethodName() + "(" + ste.getFileName() + ":" + ste.getLineNumber() + ")";
    }

    private String formatEventTime(long eventTime) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS ");
        return format.format(new Date(eventTime));
    }

    public void append(LoggingEvent event) {
        String location;
        StringBuffer bug = new StringBuffer(this.formatEventTime(event.getEventTime()));
        if (event.getLevel() == 4) {
            bug.append("Error:");
        }
        bug.append(event.getMessage());
        if (event.hasData()) {
            bug.append(" [").append(event.getFormatedData()).append("]");
        }
        if ((location = this.formatLocation(event.getLocation())).length() > 0) {
            bug.append("\n\t  ");
        }
        bug.append(location);
        if (event.getThrowable() != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PrintStream stream = new PrintStream(out);
            event.getThrowable().printStackTrace(stream);
            stream.flush();
            bug.append(((Object)out).toString());
        }
        bug.append("\n");
        this.log(bug.toString());
    }

    private class SwingLogUpdater
    implements Runnable {
        private SwingLogUpdater() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private String getNextLine() {
            Vector vector = SwingLogConsoleDialog.this.logLinesQueue;
            synchronized (vector) {
                if (SwingLogConsoleDialog.this.logLinesQueue.isEmpty()) {
                    return null;
                }
                String line = (String)SwingLogConsoleDialog.this.logLinesQueue.firstElement();
                SwingLogConsoleDialog.this.logLinesQueue.removeElementAt(0);
                return line;
            }
        }

        public void run() {
            String line;
            while ((line = this.getNextLine()) != null) {
                SwingLogConsoleDialog.this.logArea.append(line);
            }
        }
    }
}

