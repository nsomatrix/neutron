/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Stack;
import nanoxml.XMLElement;
import org.microemu.app.util.MRUListListener;
import org.microemu.app.util.XMLItem;

public class MRUList
implements XMLItem {
    private static final long serialVersionUID = 1L;
    private static final int MAXCAPACITY_DEFAULT = 10;
    protected int maxCapacity = 10;
    private Stack items = new Stack();
    private String itemsName;
    private Class classXMLItem;
    private MRUListListener listener;
    private boolean modified = true;

    public MRUList(Class classXMLItem, String itemsName) {
        this.classXMLItem = classXMLItem;
        this.itemsName = itemsName;
    }

    public Object push(Object item) {
        if (!(item instanceof XMLItem)) {
            throw new ClassCastException(item.getClass().getName());
        }
        this.modified = true;
        if (this.items.size() > this.maxCapacity) {
            this.items.pop();
        }
        this.items.remove(item);
        if (this.items.empty()) {
            this.items.add(item);
        } else {
            this.items.insertElementAt(item, 0);
        }
        this.fireListener(item);
        return item;
    }

    private void fireListener(Object item) {
        if (this.listener != null) {
            this.listener.listItemChanged(item);
        }
    }

    public void setListener(MRUListListener l) {
        if (this.listener != null) {
            throw new IllegalArgumentException();
        }
        this.listener = l;
    }

    public int getMaxCapacity() {
        return this.maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public void save(XMLElement xml) {
        if (!this.modified) {
            return;
        }
        xml.removeChildren();
        xml.setAttribute("maxCapacity", String.valueOf(this.maxCapacity));
        Iterator iter = this.items.iterator();
        while (iter.hasNext()) {
            XMLItem element = (XMLItem)iter.next();
            element.save(xml.addChild(this.itemsName));
        }
        this.modified = false;
    }

    public void read(XMLElement xml) {
        this.modified = false;
        this.items.removeAllElements();
        this.maxCapacity = xml.getIntAttribute("maxCapacity", 10);
        Enumeration en = xml.enumerateChildren();
        while (en.hasMoreElements()) {
            XMLElement xmlChild = (XMLElement)en.nextElement();
            if (!xmlChild.getName().equals(this.itemsName)) continue;
            try {
                XMLItem element = (XMLItem)this.classXMLItem.newInstance();
                element.read(xmlChild);
                this.items.add(element);
            }
            catch (InstantiationException e) {
                throw new RuntimeException(e);
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        if (!this.items.empty()) {
            ListIterator iter = this.items.listIterator(this.items.size());
            while (iter.hasPrevious()) {
                XMLItem element = (XMLItem)iter.previous();
                this.fireListener(element);
            }
        }
    }
}

