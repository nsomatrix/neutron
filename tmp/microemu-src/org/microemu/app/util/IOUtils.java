/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

public class IOUtils {
    public static String getCanonicalFileURL(File file) {
        String path = file.getAbsoluteFile().getPath();
        if (File.separatorChar != '/') {
            path = path.replace(File.separatorChar, '/');
        }
        if (!path.startsWith("//")) {
            path = path.startsWith("/") ? "//" + path : "///" + path;
        }
        return "file:" + path;
    }

    public static String getCanonicalFileClassLoaderURL(File file) {
        String url = IOUtils.getCanonicalFileURL(file);
        if (file.isDirectory() && !url.endsWith("/")) {
            url = url + "/";
        }
        return url;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void copyFile(File src, File dst) throws IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(src);
            IOUtils.copyToFile(fis, dst);
        }
        catch (Throwable throwable) {
            IOUtils.closeQuietly(fis);
            throw throwable;
        }
        IOUtils.closeQuietly(fis);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void copyToFile(InputStream is, File dst) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(dst);
            byte[] buf = new byte[1024];
            int i = 0;
            while ((i = is.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        }
        catch (Throwable throwable) {
            IOUtils.closeQuietly(fos);
            throw throwable;
        }
        IOUtils.closeQuietly(fos);
    }

    public static void closeQuietly(InputStream input) {
        try {
            if (input != null) {
                input.close();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public static void closeQuietly(OutputStream output) {
        try {
            if (output != null) {
                output.close();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public static void closeQuietly(Writer output) {
        try {
            if (output != null) {
                output.close();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }
}

