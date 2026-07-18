/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.lcdui;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Screen;
import javax.microedition.lcdui.StringComponent;
import javax.microedition.lcdui.Ticker;
import org.microemu.device.DeviceFactory;
import org.microemu.device.ui.ListUI;

public class List
extends Screen
implements Choice {
    public static final Command SELECT_COMMAND = new Command("", 1, 0);
    ChoiceGroup choiceGroup;
    private Command selCommand;
    private int initialPressedItem;

    public List(String title, int listType) {
        super(title);
        super.setUI(DeviceFactory.getDevice().getUIFactory().createListUI(this));
        if (listType != 3 && listType != 2 && listType != 1) {
            throw new IllegalArgumentException("Illegal list type");
        }
        this.choiceGroup = listType == 3 ? new ChoiceGroup(null, 3, false) : new ChoiceGroup(null, listType);
        this.choiceGroup.setOwner(this);
        this.choiceGroup.setFocus(true);
        this.selCommand = SELECT_COMMAND;
        this.initialPressedItem = -1;
    }

    public List(String title, int listType, String[] stringElements, Image[] imageElements) {
        super(title);
        super.setUI(DeviceFactory.getDevice().getUIFactory().createListUI(this));
        if (this.ui.getClass().getName().equals("org.microemu.android.device.ui.AndroidListUI")) {
            for (int i = 0; i < stringElements.length; ++i) {
                if (imageElements == null) {
                    this.append(stringElements[i], null);
                    continue;
                }
                this.append(stringElements[i], imageElements[i]);
            }
            this.choiceGroup = new ChoiceGroup(null, listType, stringElements, imageElements, false);
        } else if (listType == 3) {
            this.choiceGroup = new ChoiceGroup(null, 3, stringElements, imageElements, false);
            for (int i = 0; i < this.size(); ++i) {
                this.set(i, this.getString(i), null);
            }
        } else {
            this.choiceGroup = new ChoiceGroup(null, listType, stringElements, imageElements);
        }
        this.choiceGroup.setOwner(this);
        this.choiceGroup.setFocus(true);
        this.selCommand = SELECT_COMMAND;
        this.initialPressedItem = -1;
    }

    public int append(String stringPart, Image imagePart) {
        if (this.ui.getClass().getName().equals("org.microemu.android.device.ui.AndroidListUI")) {
            return ((ListUI)this.ui).append(stringPart, imagePart);
        }
        return this.choiceGroup.append(stringPart, imagePart);
    }

    public void delete(int elementNum) {
        this.choiceGroup.delete(elementNum);
    }

    public void deleteAll() {
        this.choiceGroup.deleteAll();
    }

    public int getFitPolicy() {
        return this.choiceGroup.getFitPolicy();
    }

    public Font getFont(int elementNum) {
        return this.choiceGroup.getFont(elementNum);
    }

    public Image getImage(int elementNum) {
        return this.choiceGroup.getImage(elementNum);
    }

    public int getSelectedFlags(boolean[] selectedArray_return) {
        return this.choiceGroup.getSelectedFlags(selectedArray_return);
    }

    public int getSelectedIndex() {
        if (this.ui.getClass().getName().equals("org.microemu.android.device.ui.AndroidListUI")) {
            return ((ListUI)this.ui).getSelectedIndex();
        }
        return this.choiceGroup.getSelectedIndex();
    }

    public String getString(int elementNum) {
        if (this.ui.getClass().getName().equals("org.microemu.android.device.ui.AndroidListUI")) {
            return ((ListUI)this.ui).getString(elementNum);
        }
        return this.choiceGroup.getString(elementNum);
    }

    public void insert(int elementNum, String stringPart, Image imagePart) {
        this.choiceGroup.insert(elementNum, stringPart, imagePart);
    }

    public boolean isSelected(int elementNum) {
        return this.choiceGroup.isSelected(elementNum);
    }

    public void removeCommand(Command cmd) {
        super.removeCommand(cmd);
    }

    public void set(int elementNum, String stringPart, Image imagePart) {
        this.choiceGroup.set(elementNum, stringPart, imagePart);
    }

    public void setFitPolicy(int policy) {
        this.choiceGroup.setFitPolicy(policy);
    }

    public void setFont(int elementNum, Font font) {
        this.choiceGroup.setFont(elementNum, font);
    }

    public void setSelectCommand(Command command) {
        this.selCommand = command;
        ((ListUI)this.ui).setSelectCommand(command);
    }

    public void setSelectedFlags(boolean[] selectedArray) {
        this.choiceGroup.setSelectedFlags(selectedArray);
    }

    public void setSelectedIndex(int elementNum, boolean selected) {
        this.choiceGroup.setSelectedIndex(elementNum, selected);
    }

    public void setTicker(Ticker ticker) {
        super.setTicker(ticker);
    }

    public void setTitle(String s) {
        super.setTitle(s);
    }

    void keyPressed(int keyCode) {
        if (Display.getGameAction(keyCode) == 8 && this.choiceGroup.select() && super.getCommandListener() != null && this.choiceGroup.choiceType == 3) {
            super.getCommandListener().commandAction(this.selCommand, this);
        } else {
            super.keyPressed(keyCode);
        }
    }

    void pointerPressed(int x, int y) {
        int pressedItem;
        Ticker ticker = this.getTicker();
        if (ticker != null) {
            y -= ticker.getHeight();
        }
        StringComponent title = new StringComponent(this.getTitle());
        y -= title.getHeight();
        if (--y >= 0 && y < this.viewPortHeight && (pressedItem = this.choiceGroup.getItemIndexAt(x, y + this.viewPortY)) != -1) {
            if (this.choiceGroup.choiceType == 2) {
                this.setSelectedIndex(pressedItem, !this.isSelected(pressedItem));
            } else {
                this.setSelectedIndex(pressedItem, true);
            }
            this.initialPressedItem = pressedItem;
        }
    }

    void pointerReleased(int x, int y) {
        int releasedItem;
        Ticker ticker = this.getTicker();
        if (ticker != null) {
            y -= ticker.getHeight();
        }
        StringComponent title = new StringComponent(this.getTitle());
        y -= title.getHeight();
        if (--y >= 0 && y < this.viewPortHeight && this.choiceGroup.choiceType == 3 && (releasedItem = this.choiceGroup.getItemIndexAt(x, y + this.viewPortY)) != -1 && releasedItem == this.initialPressedItem && super.getCommandListener() != null && this.choiceGroup.choiceType == 3) {
            super.getCommandListener().commandAction(SELECT_COMMAND, this);
        }
    }

    int paintContent(Graphics g) {
        return this.choiceGroup.paint(g);
    }

    public int size() {
        return this.choiceGroup.size();
    }

    void showNotify() {
        super.showNotify();
        if (!this.ui.getClass().getName().equals("org.microemu.android.device.ui.AndroidListUI")) {
            int heightToItem;
            int selectedItemIndex = this.getSelectedIndex();
            int heightAfterItem = heightToItem = this.choiceGroup.getHeightToItem(selectedItemIndex);
            if (selectedItemIndex >= 0) {
                heightAfterItem += this.choiceGroup.getItemHeight(selectedItemIndex);
            }
            if (this.viewPortY > heightToItem) {
                this.viewPortY = heightToItem;
            } else if (this.viewPortY + this.viewPortHeight < heightAfterItem) {
                this.viewPortY = heightAfterItem - this.viewPortHeight;
            }
        }
    }

    int traverse(int gameKeyCode, int top, int bottom) {
        int traverse = this.choiceGroup.traverse(gameKeyCode, top, bottom, true);
        if (traverse == Integer.MAX_VALUE) {
            return 0;
        }
        return traverse;
    }
}

