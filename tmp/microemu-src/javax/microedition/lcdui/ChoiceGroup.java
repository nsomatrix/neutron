/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.lcdui;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageStringItem;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.List;

public class ChoiceGroup
extends Item
implements Choice {
    int choiceType;
    private ChoiceItem[] items = new ChoiceItem[4];
    private int numOfItems = 0;
    private int fitPolicy;
    private int highlightedItemIndex = -1;
    private List popupList;
    private static byte[] multiOff = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, 10, 0, 0, 0, 11, 2, 3, 0, 0, 0, 59, 0, -12, -117, 0, 0, 0, 6, 80, 76, 84, 69, -1, -1, -1, -69, -69, -69, -57, 75, -33, -8, 0, 0, 0, 30, 73, 68, 65, 84, 120, -38, 99, 96, 96, 96, 96, 12, 101, -8, -51, -32, -64, 32, -64, -60, -64, -64, -128, 11, 51, -122, 50, -4, 6, 0, 63, 116, 3, 1, 53, -108, 39, -26, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126};
    private static byte[] multiOn = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, 10, 0, 0, 0, 11, 2, 3, 0, 0, 0, 59, 0, -12, -117, 0, 0, 0, 12, 80, 76, 84, 69, -1, -1, -1, -69, -69, -69, 106, 106, 106, 2, 2, 2, 106, -103, 14, -47, 0, 0, 0, 53, 73, 68, 65, 84, 120, -38, 99, 96, 96, 124, -64, -16, -1, -77, 3, -45, 65, -111, 15, 76, 12, 108, 12, 76, 12, -4, 12, 76, 12, 18, 12, 76, -68, 127, 24, -104, 126, 45, 96, 96, -7, -11, -109, -127, -23, -65, 3, 3, -29, 127, -122, -113, 0, 5, 37, 12, -34, 1, -99, -83, 100, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126};
    private static byte[] radioOff = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, 11, 0, 0, 0, 11, 2, 3, 0, 0, 0, -44, -62, -97, -75, 0, 0, 0, 9, 80, 76, 84, 69, -1, -1, -1, -69, -69, -69, 106, 106, 106, -44, 13, -1, -24, 0, 0, 0, 42, 73, 68, 65, 84, 120, -38, 99, 96, 90, -59, -64, 32, -63, 48, -127, 65, -127, 65, -127, 41, -127, -31, 5, 19, 3, 3, 3, 50, 102, 80, 96, 80, 96, -6, -63, 80, -64, -64, -76, -118, 1, 0, 113, 24, 5, 61, 73, -68, -100, 98, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126};
    private static byte[] radioOn = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, 11, 0, 0, 0, 11, 2, 3, 0, 0, 0, -44, -62, -97, -75, 0, 0, 0, 12, 80, 76, 84, 69, -1, -1, -1, -69, -69, -69, 106, 106, 106, 2, 2, 2, 106, -103, 14, -47, 0, 0, 0, 50, 73, 68, 65, 84, 120, -38, 5, -63, 65, 13, 0, 32, 12, 4, -63, -19, -11, -117, 1, 18, 68, -100, 10, 52, 19, 94, 72, 64, 17, 101, -122, 44, -44, -29, 98, -52, 89, 77, -102, 40, 2, 85, -95, -73, -63, -104, -63, 37, -117, 15, -40, 119, 10, 41, 78, 26, -79, 59, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126};
    private static final Image imgMultiOff = Image.createImage(multiOff, 0, multiOff.length);
    private static final Image imgMultiOn = Image.createImage(multiOn, 0, multiOn.length);
    private static final Image imgRadioOff = Image.createImage(radioOff, 0, radioOff.length);
    private static final Image imgRadioOn = Image.createImage(radioOn, 0, radioOn.length);

    public ChoiceGroup(String label, int choiceType) {
        this(label, choiceType, true);
    }

    public ChoiceGroup(String label, int choiceType, String[] stringElements, Image[] imageElements) {
        this(label, choiceType, stringElements, imageElements, true);
    }

    ChoiceGroup(String label, int choiceType, boolean validateChoiceType) {
        super(label);
        if (validateChoiceType && choiceType != 4 && choiceType != 2 && choiceType != 1) {
            throw new IllegalArgumentException("Illegal choice type");
        }
        this.choiceType = choiceType;
        if (choiceType == 4) {
            this.popupList = new List(label, 3);
            this.popupList.setCommandListener(new ImplicitListener());
        }
    }

    ChoiceGroup(String label, int choiceType, String[] stringElements, Image[] imageElements, boolean validateChoiceType) {
        this(label, choiceType, validateChoiceType);
        for (int i = 0; i < stringElements.length; ++i) {
            if (imageElements == null) {
                this.append(stringElements[i], null);
                continue;
            }
            this.append(stringElements[i], imageElements[i]);
        }
    }

    public int append(String stringPart, Image imagePart) {
        this.insert(this.numOfItems, stringPart, imagePart);
        return this.numOfItems - 1;
    }

    public void delete(int itemNum) {
        if (itemNum < 0 || itemNum >= this.numOfItems) {
            throw new IndexOutOfBoundsException();
        }
        if ((1 == this.choiceType || 4 == this.choiceType) && this.items[itemNum].isSelected() && this.numOfItems > 1) {
            this.items[itemNum != 0 ? 0 : 1].setSelectedState(true);
        }
        if (itemNum != this.numOfItems - 1) {
            System.arraycopy(this.items, itemNum + 1, this.items, itemNum, this.numOfItems - itemNum - 1);
        }
        --this.numOfItems;
        this.items[this.numOfItems] = null;
        if (this.highlightedItemIndex > itemNum) {
            --this.highlightedItemIndex;
        }
        if (this.highlightedItemIndex >= this.numOfItems) {
            this.highlightedItemIndex = this.numOfItems - 1;
        }
        if (this.choiceType == 4) {
            this.popupList.delete(itemNum);
        }
        this.repaint();
    }

    public void deleteAll() {
        for (int i = 0; i < this.numOfItems; ++i) {
            this.items[i] = null;
        }
        this.numOfItems = 0;
        this.highlightedItemIndex = -1;
        if (this.choiceType == 4) {
            this.popupList.deleteAll();
        }
        this.repaint();
    }

    public int getFitPolicy() {
        return this.fitPolicy;
    }

    public Font getFont(int itemNum) {
        if (itemNum < 0 || itemNum >= this.numOfItems) {
            throw new IndexOutOfBoundsException();
        }
        return this.items[itemNum].getFont();
    }

    public Image getImage(int elementNum) {
        if (elementNum < 0 || elementNum >= this.numOfItems) {
            throw new IndexOutOfBoundsException();
        }
        return this.items[elementNum].getImage();
    }

    public int getSelectedFlags(boolean[] selectedArray_return) {
        if (selectedArray_return == null) {
            throw new NullPointerException();
        }
        if (selectedArray_return.length < this.numOfItems) {
            throw new IllegalArgumentException();
        }
        int selectedItemsCount = 0;
        for (int i = 0; i < selectedArray_return.length; ++i) {
            boolean bl = selectedArray_return[i] = i < this.numOfItems ? this.items[i].isSelected() : false;
            if (!selectedArray_return[i]) continue;
            ++selectedItemsCount;
        }
        return selectedItemsCount;
    }

    public int getSelectedIndex() {
        switch (this.choiceType) {
            case 1: 
            case 4: {
                for (int i = 0; i < this.numOfItems; ++i) {
                    if (!this.items[i].isSelected()) continue;
                    return i;
                }
                break;
            }
            case 3: {
                return this.highlightedItemIndex;
            }
        }
        return -1;
    }

    public String getString(int elementNum) {
        if (elementNum < 0 || elementNum >= this.numOfItems) {
            throw new IndexOutOfBoundsException();
        }
        return this.items[elementNum].getText();
    }

    public void insert(int elementNum, String stringPart, Image imagePart) {
        if (elementNum < 0 || elementNum > this.numOfItems) {
            throw new IndexOutOfBoundsException();
        }
        if (stringPart == null) {
            throw new NullPointerException();
        }
        if (this.choiceType == 4) {
            this.popupList.insert(elementNum, stringPart, imagePart);
        }
        if (this.numOfItems == this.items.length) {
            ChoiceItem[] newItems = new ChoiceItem[this.numOfItems + 4];
            System.arraycopy(this.items, 0, newItems, 0, this.numOfItems);
            this.items = newItems;
        }
        System.arraycopy(this.items, elementNum, this.items, elementNum + 1, this.numOfItems - elementNum);
        this.items[elementNum] = new ChoiceItem(null, imagePart, stringPart);
        ++this.numOfItems;
        if (this.numOfItems == 1) {
            this.highlightedItemIndex = 0;
            if (1 == this.choiceType || 4 == this.choiceType) {
                this.setSelectedIndex(0, true);
            }
        }
        this.repaint();
    }

    public boolean isSelected(int elementNum) {
        if (elementNum < 0 || elementNum >= this.numOfItems) {
            throw new IndexOutOfBoundsException();
        }
        return this.items[elementNum].isSelected();
    }

    public void set(int elementNum, String stringPart, Image imagePart) {
        if (elementNum < 0 || elementNum >= this.numOfItems) {
            throw new IndexOutOfBoundsException();
        }
        if (imagePart != null && imagePart.isMutable()) {
            throw new IllegalArgumentException();
        }
        if (stringPart == null) {
            throw new NullPointerException();
        }
        this.items[elementNum].setText(stringPart);
        this.items[elementNum].setImage(imagePart);
        if (this.choiceType == 4) {
            this.popupList.set(elementNum, stringPart, imagePart);
        }
        this.repaint();
    }

    public void setFitPolicy(int policy) {
        if (policy != 0 && policy != 1 && policy != 2) {
            throw new IllegalArgumentException("Bad Policy");
        }
        this.fitPolicy = policy;
        if (this.choiceType == 4) {
            this.popupList.setFitPolicy(policy);
        }
    }

    public void setFont(int itemNum, Font font) {
        if (itemNum < 0 || itemNum >= this.numOfItems) {
            throw new IndexOutOfBoundsException();
        }
        this.items[itemNum].setFont(font);
        if (this.choiceType == 4) {
            this.popupList.setFont(itemNum, font);
        }
    }

    public void setSelectedFlags(boolean[] selectedArray) {
        if (selectedArray == null) {
            throw new NullPointerException();
        }
        if (selectedArray.length < this.numOfItems) {
            throw new NullPointerException();
        }
        if (this.numOfItems == 0) {
            return;
        }
        if (this.choiceType == 2) {
            for (int i = 0; i < this.numOfItems; ++i) {
                this.setSelectedIndex(i, selectedArray[i]);
            }
        } else {
            int selectedItem = -1;
            for (int i = 0; i < this.numOfItems; ++i) {
                if (!selectedArray[i]) continue;
                this.setSelectedIndex(i, true);
                selectedItem = i;
                break;
            }
            if (selectedItem == -1) {
                this.setSelectedIndex(0, true);
            }
            if (this.choiceType == 4) {
                this.popupList.setSelectedFlags(selectedArray);
            }
        }
    }

    public void setSelectedIndex(int elementNum, boolean selected) {
        if (elementNum < 0 || elementNum >= this.numOfItems) {
            throw new IndexOutOfBoundsException();
        }
        this.highlightedItemIndex = elementNum;
        if ((this.choiceType == 1 || this.choiceType == 4) && selected) {
            for (int i = 0; i < this.numOfItems; ++i) {
                this.items[i].setSelectedState(elementNum == i);
            }
            if (this.choiceType == 4) {
                this.popupList.setSelectedIndex(elementNum, true);
            }
            this.repaint();
        } else if (this.choiceType == 2) {
            this.items[elementNum].setSelectedState(selected);
            this.repaint();
        } else if (this.choiceType == 3 && selected) {
            this.items[elementNum].setSelectedState(selected);
            this.repaint();
        }
    }

    public int size() {
        return this.numOfItems;
    }

    boolean isFocusable() {
        return true;
    }

    int getHeight() {
        int height = 0;
        if (this.choiceType == 4) {
            if (this.highlightedItemIndex != -1) {
                height += this.items[this.highlightedItemIndex].getHeight();
            }
        } else {
            for (int i = 0; i < this.numOfItems; ++i) {
                height += this.items[i].getHeight();
            }
        }
        return super.getHeight() + height;
    }

    int getItemIndexAt(int x, int y) {
        x -= super.getHeight();
        int testY = 0;
        for (int i = 0; i < this.numOfItems; ++i) {
            if (y >= (testY += this.items[i].getHeight())) continue;
            return i;
        }
        return -1;
    }

    int getHeightToItem(int itemIndex) {
        int height = 0;
        for (int i = 0; i < itemIndex; ++i) {
            height += this.items[i].getHeight();
        }
        return height;
    }

    int getItemHeight(int itemIndex) {
        return this.items[itemIndex].getHeight();
    }

    int paint(Graphics g) {
        super.paintContent(g);
        g.translate(0, super.getHeight());
        int translatedY = 0;
        if (this.choiceType == 4) {
            int index = this.getSelectedIndex();
            if (index != -1) {
                this.items[index].invertPaint(this.hasFocus());
                this.items[index].paint(g);
            }
            g.translate(0, -super.getHeight());
        } else {
            for (int i = 0; i < this.numOfItems; ++i) {
                this.items[i].invertPaint(i == this.highlightedItemIndex && this.hasFocus());
                this.items[i].paint(g);
                g.translate(0, this.items[i].getHeight());
                translatedY += this.items[i].getHeight();
            }
            g.translate(0, -translatedY);
            g.translate(0, -super.getHeight());
        }
        return this.getHeight();
    }

    boolean select() {
        if (this.numOfItems == 0) {
            return false;
        }
        if (this.choiceType == 4) {
            this.getOwner().currentDisplay.setCurrent(this.popupList);
        } else {
            this.setSelectedIndex(this.highlightedItemIndex, !this.items[this.highlightedItemIndex].isSelected());
        }
        return true;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    int traverse(int gameKeyCode, int top, int bottom, boolean action) {
        if (this.choiceType == 4) {
            if (gameKeyCode == 1) {
                if (top <= 0) return Integer.MAX_VALUE;
                return -top;
            }
            if (gameKeyCode != 6) return 0;
            if (action) return Integer.MAX_VALUE;
            int height = super.getHeight();
            int index = this.getSelectedIndex();
            if (index != -1) {
                height += this.items[index].getHeight();
            }
            if (height > bottom) {
                return height - bottom;
            }
            this.repaint();
            return 0;
        } else {
            int i;
            int height;
            if (gameKeyCode == 1) {
                if (this.highlightedItemIndex > 0) {
                    if (action) {
                        --this.highlightedItemIndex;
                    }
                    height = super.getHeight();
                    for (i = 0; i < this.highlightedItemIndex; ++i) {
                        height += this.items[i].getHeight();
                    }
                    if (height < top) {
                        return height - top;
                    }
                    this.repaint();
                } else {
                    if (top <= 0) return Integer.MAX_VALUE;
                    return -top;
                }
            }
            if (gameKeyCode != 6) return 0;
            if ((action || this.highlightedItemIndex >= this.numOfItems) && (!action || this.highlightedItemIndex >= this.numOfItems - 1)) return Integer.MAX_VALUE;
            if (action) {
                ++this.highlightedItemIndex;
            }
            height = super.getHeight();
            for (i = 0; i <= this.highlightedItemIndex; ++i) {
                height += this.items[i].getHeight();
            }
            if (height > bottom) {
                return height - bottom;
            }
            this.repaint();
        }
        return 0;
    }

    void repaint() {
        if (this.choiceType == 4) {
            this.popupList.repaint();
        }
        super.repaint();
    }

    class ImplicitListener
    implements CommandListener {
        ImplicitListener() {
        }

        public void commandAction(Command c, Displayable d) {
            List list = (List)d;
            ChoiceGroup.this.setSelectedIndex(list.getSelectedIndex(), true);
            try {
                ChoiceGroup.this.getOwner().currentDisplay.setCurrent(ChoiceGroup.this.getOwner());
                ((Form)ChoiceGroup.this.getOwner()).fireItemStateListener();
                ChoiceGroup.this.repaint();
            }
            catch (NullPointerException n) {
                // empty catch block
            }
        }
    }

    class ChoiceItem
    extends ImageStringItem {
        private boolean selected;
        private Font font;
        Image box;

        ChoiceItem(String label, Image image, String text) {
            super(label, image, text);
            this.setSelectedState(false);
            this.font = Font.getDefaultFont();
        }

        Font getFont() {
            return this.font;
        }

        public void setImage(Image img) {
            this.img = img;
            int width = 0;
            if (this.box != null) {
                width += this.box.getWidth();
            }
            if (this.img != null) {
                width += img.getWidth();
            }
            if (width > 0) {
                width += 2;
            }
            this.stringComponent.setWidthDecreaser(width);
        }

        int getHeight() {
            int height = 0;
            if (this.box != null) {
                height = this.box.getHeight();
            }
            if (this.img != null && this.img.getHeight() > height) {
                height = this.img.getHeight();
            }
            if (this.stringComponent.getHeight() > height) {
                height = this.stringComponent.getHeight();
            }
            return height;
        }

        int paint(Graphics g) {
            if (this.stringComponent == null) {
                return 0;
            }
            int widthAddition = 0;
            if (this.box != null) {
                g.drawImage(this.box, 0, 0, 20);
                if (this.img != null) {
                    widthAddition = this.box.getWidth();
                    g.translate(this.box.getWidth(), 0);
                } else {
                    widthAddition = this.box.getWidth() + 2;
                    g.translate(this.box.getWidth() + 2, 0);
                }
            }
            if (this.img != null) {
                widthAddition += this.img.getWidth() + 2;
                g.drawImage(this.img, 0, 0, 20);
                g.translate(this.img.getWidth() + 2, 0);
            }
            int y = this.stringComponent.paint(g);
            if (widthAddition != 0) {
                g.translate(-widthAddition, 0);
            }
            return y;
        }

        boolean isSelected() {
            return this.selected;
        }

        void setFont(Font f) {
            if (f == null) {
                throw new NullPointerException();
            }
            if (f.getHeight() == this.font.getHeight()) {
                this.font = f;
            }
        }

        void setSelectedState(boolean state) {
            this.selected = state;
            if (ChoiceGroup.this.choiceType != 3 && ChoiceGroup.this.choiceType != 4) {
                this.box = 1 == ChoiceGroup.this.choiceType ? (state ? imgRadioOn : imgRadioOff) : (state ? imgMultiOn : imgMultiOff);
            }
        }
    }
}

