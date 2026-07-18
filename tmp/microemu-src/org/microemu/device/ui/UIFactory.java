/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.ui;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;
import org.microemu.device.ui.AlertUI;
import org.microemu.device.ui.CanvasUI;
import org.microemu.device.ui.CommandUI;
import org.microemu.device.ui.EventDispatcher;
import org.microemu.device.ui.FormUI;
import org.microemu.device.ui.ListUI;
import org.microemu.device.ui.TextBoxUI;

public interface UIFactory {
    public EventDispatcher createEventDispatcher(Display var1);

    public AlertUI createAlertUI(Alert var1);

    public CanvasUI createCanvasUI(Canvas var1);

    public CommandUI createCommandUI(Command var1);

    public FormUI createFormUI(Form var1);

    public ListUI createListUI(List var1);

    public TextBoxUI createTextBoxUI(TextBox var1);
}

