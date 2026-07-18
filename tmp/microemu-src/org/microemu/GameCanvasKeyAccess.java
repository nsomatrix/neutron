/*
 * Decompiled with CFR 0.152.
 */
package org.microemu;

import javax.microedition.lcdui.game.GameCanvas;

public interface GameCanvasKeyAccess {
    public boolean suppressedKeyEvents(GameCanvas var1);

    public void recordKeyPressed(GameCanvas var1, int var2);

    public void recordKeyReleased(GameCanvas var1, int var2);

    public void setActualKeyState(GameCanvas var1, int var2);

    public void initBuffer();
}

