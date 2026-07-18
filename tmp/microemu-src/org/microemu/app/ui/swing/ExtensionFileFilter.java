/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.ui.swing;

import java.io.File;
import java.util.Hashtable;
import javax.swing.filechooser.FileFilter;

public class ExtensionFileFilter
extends FileFilter {
    String description;
    Hashtable extensions = new Hashtable();

    public ExtensionFileFilter(String description) {
        this.description = description;
    }

    public boolean accept(File file) {
        if (file != null) {
            if (file.isDirectory()) {
                return true;
            }
            String ext = this.getExtension(file);
            if (ext != null && this.extensions.get(ext) != null) {
                return true;
            }
        }
        return false;
    }

    public void addExtension(String extension) {
        this.extensions.put(extension.toLowerCase(), this);
    }

    public String getDescription() {
        return this.description;
    }

    String getExtension(File file) {
        String filename;
        int i;
        if (file != null && (i = (filename = file.getName()).lastIndexOf(46)) > 0 && i < filename.length() - 1) {
            return filename.substring(i + 1).toLowerCase();
        }
        return null;
    }
}

