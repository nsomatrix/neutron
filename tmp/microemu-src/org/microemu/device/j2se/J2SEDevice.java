/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.j2se;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;
import org.microemu.device.impl.DeviceImpl;
import org.microemu.device.impl.ui.CommandImplUI;
import org.microemu.device.j2se.ui.J2SEAlertUI;
import org.microemu.device.j2se.ui.J2SECanvasUI;
import org.microemu.device.j2se.ui.J2SEFormUI;
import org.microemu.device.j2se.ui.J2SEListUI;
import org.microemu.device.j2se.ui.J2SETextBoxUI;
import org.microemu.device.ui.AlertUI;
import org.microemu.device.ui.CanvasUI;
import org.microemu.device.ui.CommandUI;
import org.microemu.device.ui.EventDispatcher;
import org.microemu.device.ui.FormUI;
import org.microemu.device.ui.ListUI;
import org.microemu.device.ui.TextBoxUI;
import org.microemu.device.ui.UIFactory;

public class J2SEDevice
extends DeviceImpl {
    private UIFactory ui = new UIFactory(){

        public EventDispatcher createEventDispatcher(Display display) {
            EventDispatcher eventDispatcher = new EventDispatcher();
            Thread thread = new Thread((Runnable)eventDispatcher, "event-thread");
            thread.setDaemon(true);
            thread.start();
            return eventDispatcher;
        }

        public AlertUI createAlertUI(Alert alert) {
            return new J2SEAlertUI(alert);
        }

        public CanvasUI createCanvasUI(Canvas canvas) {
            return new J2SECanvasUI(canvas);
        }

        public CommandUI createCommandUI(Command command) {
            return new CommandImplUI(command);
        }

        public FormUI createFormUI(Form form) {
            return new J2SEFormUI(form);
        }

        public ListUI createListUI(List list) {
            return new J2SEListUI(list);
        }

        public TextBoxUI createTextBoxUI(TextBox textBox) {
            return new J2SETextBoxUI(textBox);
        }
    };

    public UIFactory getUIFactory() {
        return this.ui;
    }
}

