/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.lcdui;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.DateCanvas;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.TimeCanvas;

public class DateField
extends Item {
    public static final int DATE = 1;
    public static final int TIME = 2;
    public static final int DATE_TIME = 3;
    Date date;
    Date time;
    String label;
    int mode;
    ChoiceGroup dateTime;
    DateCanvas dateCanvas;
    TimeCanvas timeCanvas;
    static Command saveCommand = new Command("Save", 4, 0);
    static Command backCommand = new Command("Back", 2, 0);
    CommandListener dateTimeListener = new CommandListener(){

        public void commandAction(Command c, Displayable d) {
            if (c == backCommand) {
                DateField.this.getOwner().currentDisplay.setCurrent(DateField.this.owner);
            } else if (c == saveCommand) {
                Calendar from = Calendar.getInstance();
                Calendar to = Calendar.getInstance();
                to.setTime(new Date(0L));
                if (d == DateField.this.dateCanvas) {
                    from.setTime(DateField.this.dateCanvas.getTime());
                    to.set(5, from.get(5));
                    to.set(2, from.get(2));
                    to.set(1, from.get(1));
                    DateField.this.date = to.getTime();
                } else {
                    from.setTime(DateField.this.timeCanvas.getTime());
                    to.set(11, from.get(11));
                    to.set(12, from.get(12));
                    DateField.this.time = to.getTime();
                }
                DateField.this.updateDateTimeString();
                DateField.this.getOwner().currentDisplay.setCurrent(DateField.this.owner);
            }
        }
    };

    public DateField(String label, int mode) {
        this(label, mode, null);
    }

    public DateField(String label, int mode, TimeZone timeZone) {
        super(null);
        this.label = label;
        this.setInputMode(mode);
        this.dateCanvas = new DateCanvas();
        this.dateCanvas.addCommand(saveCommand);
        this.dateCanvas.addCommand(backCommand);
        this.dateCanvas.setCommandListener(this.dateTimeListener);
        this.timeCanvas = new TimeCanvas();
        this.timeCanvas.addCommand(saveCommand);
        this.timeCanvas.addCommand(backCommand);
        this.timeCanvas.setCommandListener(this.dateTimeListener);
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
        this.updateDateTimeString();
    }

    public int getInputMode() {
        return this.mode;
    }

    public void setInputMode(int mode) {
        if (mode < 1 || mode > 3) {
            throw new IllegalArgumentException();
        }
        this.mode = mode;
        this.dateTime = new ChoiceGroup(this.label, 3, false);
        if ((mode & 1) != 0) {
            this.dateTime.append("[date]", null);
        }
        if ((mode & 2) != 0) {
            this.dateTime.append("[time]", null);
        }
    }

    boolean isFocusable() {
        return true;
    }

    int getHeight() {
        return super.getHeight() + this.dateTime.getHeight();
    }

    int paint(Graphics g) {
        super.paintContent(g);
        g.translate(0, super.getHeight());
        this.dateTime.paint(g);
        g.translate(0, -super.getHeight());
        return this.getHeight();
    }

    void setFocus(boolean state) {
        super.setFocus(state);
        this.dateTime.setFocus(state);
    }

    boolean select() {
        this.dateTime.select();
        if (this.dateTime.getSelectedIndex() == 0 && (this.mode & 1) != 0) {
            if (this.date != null) {
                this.dateCanvas.setTime(this.date);
            } else {
                this.dateCanvas.setTime(new Date());
            }
            this.getOwner().currentDisplay.setCurrent(this.dateCanvas);
        } else {
            if (this.time != null) {
                this.timeCanvas.setTime(this.time);
            } else {
                Calendar cal = Calendar.getInstance();
                cal.set(1, 1970);
                cal.set(2, 0);
                cal.set(5, 1);
                cal.set(11, 12);
                cal.set(12, 0);
                cal.set(13, 0);
                this.timeCanvas.setTime(cal.getTime());
            }
            this.getOwner().currentDisplay.setCurrent(this.timeCanvas);
        }
        return true;
    }

    int traverse(int gameKeyCode, int top, int bottom, boolean action) {
        return this.dateTime.traverse(gameKeyCode, top, bottom, action);
    }

    private String formatDate() {
        if (this.date == null) {
            return "[date]";
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.date);
        int day = cal.get(5);
        int month = cal.get(2) + 1;
        int year = cal.get(1);
        return Integer.toString(day) + "-" + month + "-" + year;
    }

    private String formatTime() {
        if (this.time == null) {
            return "[time]";
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.time);
        int hours = cal.get(11);
        int minutes = cal.get(12);
        return Integer.toString(hours) + ":" + (minutes < 10 ? "0" : "") + minutes;
    }

    void updateDateTimeString() {
        if ((this.mode & 1) != 0) {
            this.dateTime.set(0, this.formatDate(), null);
        }
        if ((this.mode & 2) != 0) {
            this.dateTime.set((this.mode & 1) != 0 ? 1 : 0, this.formatTime(), null);
        }
    }
}

