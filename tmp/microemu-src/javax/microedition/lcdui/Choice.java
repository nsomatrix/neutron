/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.lcdui;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;

public interface Choice {
    public static final int EXCLUSIVE = 1;
    public static final int MULTIPLE = 2;
    public static final int IMPLICIT = 3;
    public static final int POPUP = 4;
    public static final int TEXT_WRAP_ON = 1;
    public static final int TEXT_WRAP_OFF = 2;
    public static final int TEXT_WRAP_DEFAULT = 0;

    public int append(String var1, Image var2);

    public void delete(int var1);

    public void deleteAll();

    public int getFitPolicy();

    public Font getFont(int var1);

    public Image getImage(int var1);

    public int getSelectedFlags(boolean[] var1);

    public int getSelectedIndex();

    public String getString(int var1);

    public void insert(int var1, String var2, Image var3);

    public boolean isSelected(int var1);

    public void set(int var1, String var2, Image var3);

    public void setFitPolicy(int var1);

    public void setFont(int var1, Font var2);

    public void setSelectedFlags(boolean[] var1);

    public void setSelectedIndex(int var1, boolean var2);

    public int size();
}

