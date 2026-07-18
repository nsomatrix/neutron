/*
 * Decompiled with CFR 0.152.
 */
package org.microemu;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import org.microemu.device.ui.DisplayableUI;

public interface DisplayAccess {
    public void commandAction(Command var1, Displayable var2);

    public Display getDisplay();

    public void keyPressed(int var1);

    public void keyRepeated(int var1);

    public void keyReleased(int var1);

    public void pointerPressed(int var1, int var2);

    public void pointerReleased(int var1, int var2);

    public void pointerDragged(int var1, int var2);

    public void paint(Graphics var1);

    public boolean isFullScreenMode();

    public void serviceRepaints();

    public Displayable getCurrent();

    public DisplayableUI getCurrentUI();

    public void setCurrent(Displayable var1);

    public void sizeChanged();

    public void repaint();

    public void clean();
}

