/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.lcdui;

import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.lcdui.Screen;
import javax.microedition.lcdui.StringItem;
import org.microemu.device.DeviceFactory;
import org.microemu.device.ui.FormUI;

public class Form
extends Screen {
    Item[] items = new Item[4];
    int numOfItems = 0;
    int focusItemIndex;
    ItemStateListener itemStateListener = null;

    public Form(String title) {
        super(title);
        super.setUI(DeviceFactory.getDevice().getUIFactory().createFormUI(this));
        this.focusItemIndex = -2;
    }

    public Form(String title, Item[] items) {
        this(title);
        if (items != null) {
            this.items = new Item[items.length];
            System.arraycopy(items, 0, this.items, 0, items.length);
            this.numOfItems = this.items.length;
            for (int i = 0; i < this.numOfItems; ++i) {
                this.verifyItem(this.items[i]);
            }
        }
    }

    public int append(Item item) {
        this.verifyItem(item);
        if (this.ui.getClass().getName().equals("org.microemu.android.device.ui.AndroidFormUI")) {
            ((FormUI)this.ui).append(item);
        }
        if (this.numOfItems + 1 >= this.items.length) {
            Item[] newitems = new Item[this.numOfItems + 4];
            System.arraycopy(this.items, 0, newitems, 0, this.numOfItems);
            this.items = newitems;
        }
        this.items[this.numOfItems] = item;
        ++this.numOfItems;
        this.repaint();
        return this.numOfItems - 1;
    }

    public int append(Image img) {
        if (this.ui.getClass().getName().equals("org.microemu.android.device.ui.AndroidFormUI")) {
            ((FormUI)this.ui).append(new ImageItem(null, img, 0, null));
        }
        return this.append(new ImageItem(null, img, 0, null));
    }

    public int append(String str) {
        if (str == null) {
            throw new NullPointerException();
        }
        if (this.ui.getClass().getName().equals("org.microemu.android.device.ui.AndroidFormUI")) {
            ((FormUI)this.ui).append(new StringItem(null, str));
        }
        return this.append(new StringItem(null, str));
    }

    public void delete(int itemNum) {
        this.verifyItemNum(itemNum);
        if (this.ui.getClass().getName().equals("org.microemu.android.device.ui.AndroidFormUI")) {
            ((FormUI)this.ui).delete(itemNum);
        }
        this.items[itemNum].setOwner(null);
        System.arraycopy(this.items, itemNum + 1, this.items, itemNum, this.numOfItems - itemNum - 1);
        --this.numOfItems;
        this.repaint();
    }

    public void deleteAll() {
        if (this.ui.getClass().getName().equals("org.microemu.android.device.ui.AndroidFormUI")) {
            ((FormUI)this.ui).deleteAll();
        }
        for (int i = 0; i < this.numOfItems; ++i) {
            this.items[i].setOwner(null);
        }
        this.numOfItems = 0;
        this.repaint();
    }

    public Item get(int itemNum) {
        this.verifyItemNum(itemNum);
        return this.items[itemNum];
    }

    public int getHeight() {
        return super.getHeight();
    }

    public int getWidth() {
        return super.getWidth();
    }

    public void insert(int itemNum, Item item) {
        if (itemNum != this.numOfItems) {
            this.verifyItemNum(itemNum);
        }
        this.verifyItem(item);
        if (this.ui.getClass().getName().equals("org.microemu.android.device.ui.AndroidFormUI")) {
            ((FormUI)this.ui).insert(itemNum, item);
        }
        if (this.numOfItems + 1 == this.items.length) {
            Item[] newitems = new Item[this.numOfItems + 4];
            System.arraycopy(this.items, 0, newitems, 0, this.numOfItems);
            this.items = newitems;
        }
        System.arraycopy(this.items, itemNum, this.items, itemNum + 1, this.numOfItems - itemNum);
        this.items[itemNum] = item;
        this.items[itemNum].setOwner(this);
        ++this.numOfItems;
        this.repaint();
    }

    public void set(int itemNum, Item item) {
        this.verifyItemNum(itemNum);
        this.verifyItem(item);
        if (this.ui.getClass().getName().equals("org.microemu.android.device.ui.AndroidFormUI")) {
            ((FormUI)this.ui).set(itemNum, item);
        }
        this.items[itemNum].setOwner(null);
        this.items[itemNum] = item;
        this.items[itemNum].setOwner(this);
        this.repaint();
    }

    public void setItemStateListener(ItemStateListener iListener) {
        this.itemStateListener = iListener;
    }

    public int size() {
        return this.numOfItems;
    }

    int paintContent(Graphics g) {
        int contentHeight = 0;
        for (int i = 0; i < this.numOfItems; ++i) {
            int translateY = this.items[i].paint(g);
            g.translate(0, translateY);
            contentHeight += translateY;
        }
        g.translate(0, -contentHeight);
        return contentHeight;
    }

    void fireItemStateListener(Item item) {
        if (this.itemStateListener != null) {
            this.itemStateListener.itemStateChanged(item);
        }
    }

    void fireItemStateListener() {
        if (this.focusItemIndex >= 0 && this.focusItemIndex < this.items.length) {
            this.fireItemStateListener(this.items[this.focusItemIndex]);
        }
    }

    void hideNotify() {
        super.hideNotify();
        for (int i = 0; i < this.numOfItems; ++i) {
            if (!this.items[i].isFocusable() || !this.items[i].hasFocus()) continue;
            this.items[i].setFocus(false);
            this.focusItemIndex = -2;
            break;
        }
    }

    void keyPressed(int keyCode) {
        if (this.focusItemIndex != -1) {
            if (Display.getGameAction(keyCode) == 8) {
                this.items[this.focusItemIndex].select();
                this.fireItemStateListener();
            } else {
                this.items[this.focusItemIndex].keyPressed(keyCode);
            }
        }
        super.keyPressed(keyCode);
    }

    void showNotify() {
        super.showNotify();
        if (this.focusItemIndex == -2) {
            this.focusItemIndex = -1;
            for (int i = 0; i < this.numOfItems; ++i) {
                if (!this.items[i].isFocusable()) continue;
                this.items[i].setFocus(true);
                this.focusItemIndex = i;
                break;
            }
        }
        if (this.focusItemIndex < 0) {
            return;
        }
        int heightToItem = this.getHeightToItem(this.focusItemIndex);
        int heightAfterItem = heightToItem + this.items[this.focusItemIndex].getHeight();
        if (this.viewPortY > heightToItem) {
            this.viewPortY = heightToItem;
        } else if (this.viewPortY + this.viewPortHeight < heightAfterItem) {
            this.viewPortY = heightAfterItem - this.viewPortHeight;
        }
    }

    int traverse(int gameKeyCode, int top, int bottom) {
        int i;
        int traverse;
        int height;
        int testItemIndex;
        int topItemIndex;
        if (this.numOfItems == 0) {
            return 0;
        }
        if (gameKeyCode == 1) {
            topItemIndex = this.getTopVisibleIndex(top);
            if (this.focusItemIndex == -1) {
                testItemIndex = topItemIndex;
                height = this.getHeightToItem(testItemIndex);
                traverse = this.items[testItemIndex].traverse(gameKeyCode, top - height, bottom - height, false);
            } else {
                testItemIndex = this.focusItemIndex;
                height = this.getHeightToItem(testItemIndex);
                traverse = this.items[testItemIndex].traverse(gameKeyCode, top - height, bottom - height, true);
            }
            if (traverse != Integer.MAX_VALUE) {
                if (this.focusItemIndex == -1 && this.items[testItemIndex].isFocusable()) {
                    this.items[testItemIndex].setFocus(true);
                    this.focusItemIndex = testItemIndex;
                }
                return traverse;
            }
            if (testItemIndex > 0) {
                for (i = testItemIndex - 1; i >= topItemIndex; --i) {
                    if (!this.items[i].isFocusable()) continue;
                    if (this.focusItemIndex != -1) {
                        this.items[this.focusItemIndex].setFocus(false);
                    }
                    this.items[i].setFocus(true);
                    this.focusItemIndex = i;
                    height = this.getHeightToItem(i);
                    traverse = this.items[i].traverse(gameKeyCode, top - height, bottom - height, false);
                    if (traverse == Integer.MAX_VALUE) {
                        return 0;
                    }
                    return traverse;
                }
                height = this.getHeightToItem(topItemIndex);
                traverse = this.items[topItemIndex].traverse(gameKeyCode, top - height, bottom - height, false);
                if (traverse != Integer.MAX_VALUE) {
                    int bottomItemIndex = this.getTopVisibleIndex(bottom + traverse);
                    if (this.focusItemIndex != -1 && this.focusItemIndex > bottomItemIndex) {
                        this.items[this.focusItemIndex].setFocus(false);
                        this.focusItemIndex = -1;
                    }
                    return traverse;
                }
            }
        }
        if (gameKeyCode == 6) {
            int bottomItemIndex = this.getBottomVisibleIndex(bottom);
            if (this.focusItemIndex == -1) {
                testItemIndex = bottomItemIndex;
                height = this.getHeightToItem(testItemIndex);
                traverse = this.items[testItemIndex].traverse(gameKeyCode, top - height, bottom - height, false);
            } else {
                testItemIndex = this.focusItemIndex;
                height = this.getHeightToItem(testItemIndex);
                traverse = this.items[testItemIndex].traverse(gameKeyCode, top - height, bottom - height, true);
            }
            if (traverse != Integer.MAX_VALUE) {
                if (this.focusItemIndex == -1 && this.items[testItemIndex].isFocusable()) {
                    this.items[testItemIndex].setFocus(true);
                    this.focusItemIndex = testItemIndex;
                }
                return traverse;
            }
            if (testItemIndex < this.numOfItems - 1) {
                for (i = testItemIndex + 1; i <= bottomItemIndex; ++i) {
                    if (!this.items[i].isFocusable()) continue;
                    if (this.focusItemIndex != -1) {
                        this.items[this.focusItemIndex].setFocus(false);
                    }
                    this.items[i].setFocus(true);
                    this.focusItemIndex = i;
                    height = this.getHeightToItem(i);
                    traverse = this.items[i].traverse(gameKeyCode, top - height, bottom - height, false);
                    if (traverse == Integer.MAX_VALUE) {
                        return 0;
                    }
                    return traverse;
                }
                height = this.getHeightToItem(bottomItemIndex);
                traverse = this.items[bottomItemIndex].traverse(gameKeyCode, top - height, bottom - height, false);
                if (traverse != Integer.MAX_VALUE) {
                    topItemIndex = this.getTopVisibleIndex(top + traverse);
                    if (this.focusItemIndex != -1 && this.focusItemIndex < topItemIndex) {
                        this.items[this.focusItemIndex].setFocus(false);
                        this.focusItemIndex = -1;
                    }
                    return traverse;
                }
            }
        }
        return 0;
    }

    private int getTopVisibleIndex(int top) {
        int height = 0;
        for (int i = 0; i < this.numOfItems; ++i) {
            if ((height += this.items[i].getHeight()) < top) continue;
            return i;
        }
        return this.numOfItems - 1;
    }

    private int getBottomVisibleIndex(int bottom) {
        int height = 0;
        for (int i = 0; i < this.numOfItems; ++i) {
            if ((height += this.items[i].getHeight()) <= bottom) continue;
            return i;
        }
        return this.numOfItems - 1;
    }

    private int getHeightToItem(int itemIndex) {
        int height = 0;
        for (int i = 0; i < itemIndex; ++i) {
            height += this.items[i].getHeight();
        }
        return height;
    }

    private void verifyItem(Item item) {
        if (item == null) {
            throw new NullPointerException("item is null");
        }
        if (item.getOwner() != null) {
            throw new IllegalStateException("item is already owned");
        }
        item.setOwner(this);
    }

    private void verifyItemNum(int itemNum) {
        if (itemNum < 0 || itemNum >= this.numOfItems) {
            throw new IndexOutOfBoundsException("item number is outside range of Form");
        }
    }

    Vector getCommands() {
        int i;
        Vector formCommands = super.getCommands();
        if (this.focusItemIndex < 0) {
            return formCommands;
        }
        Item item = this.items[this.focusItemIndex];
        Vector itemCommands = item.commands;
        if (itemCommands.isEmpty()) {
            return formCommands;
        }
        Vector allCommands = new Vector();
        for (i = 0; i < formCommands.size(); ++i) {
            allCommands.add(formCommands.elementAt(i));
        }
        for (i = 0; i < itemCommands.size(); ++i) {
            Command itemCommand = (Command)itemCommands.elementAt(i);
            itemCommand = itemCommand.getItemCommand(item);
            allCommands.add(itemCommand);
        }
        return allCommands;
    }
}

