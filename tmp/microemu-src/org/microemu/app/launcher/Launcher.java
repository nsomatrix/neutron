/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.launcher;

import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;
import org.microemu.MIDletEntry;
import org.microemu.app.CommonInterface;

public class Launcher
extends MIDlet
implements CommandListener {
    protected static final Command CMD_LAUNCH = new Command("Start", 8, 0);
    protected static final String NOMIDLETS = "[no midlets]";
    protected CommonInterface common;
    protected List menuList;
    protected static String midletSuiteName = null;
    protected static Vector midletEntries = new Vector();
    protected MIDlet currentMIDlet = null;

    public Launcher(CommonInterface common) {
        this.common = common;
    }

    public String getSuiteName() {
        return midletSuiteName;
    }

    public static void setSuiteName(String midletSuiteName) {
        Launcher.midletSuiteName = midletSuiteName;
    }

    public static void addMIDletEntry(MIDletEntry entry) {
        midletEntries.addElement(entry);
    }

    public static void removeMIDletEntries() {
        midletEntries.removeAllElements();
    }

    public MIDletEntry getSelectedMidletEntry() {
        int idx;
        if (this.menuList != null && !this.menuList.getString(idx = this.menuList.getSelectedIndex()).equals(NOMIDLETS)) {
            return (MIDletEntry)midletEntries.elementAt(idx);
        }
        return null;
    }

    public MIDlet getCurrentMIDlet() {
        return this.currentMIDlet;
    }

    public void setCurrentMIDlet(MIDlet midlet) {
        this.currentMIDlet = midlet;
    }

    public void destroyApp(boolean unconditional) {
    }

    public void pauseApp() {
    }

    public void startApp() {
        this.menuList = new List("Launcher", 3);
        this.menuList.addCommand(CMD_LAUNCH);
        this.menuList.setCommandListener(this);
        if (midletEntries.size() == 0) {
            this.menuList.append(NOMIDLETS, null);
        } else {
            for (int i = 0; i < midletEntries.size(); ++i) {
                this.menuList.append(((MIDletEntry)midletEntries.elementAt(i)).getName(), null);
            }
        }
        Display.getDisplay(this).setCurrent(this.menuList);
    }

    public void commandAction(Command c, Displayable d) {
        MIDletEntry entry;
        if (d == this.menuList && (c == List.SELECT_COMMAND || c == CMD_LAUNCH) && (entry = this.getSelectedMidletEntry()) != null) {
            this.common.initMIDlet(true);
        }
    }
}

