/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.lcdui;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.lcdui.Graphics;
import org.microemu.device.DeviceFactory;

public class Image {
    public static Image createImage(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException();
        }
        return DeviceFactory.getDevice().getDeviceDisplay().createImage(width, height);
    }

    public static Image createImage(String name) throws IOException {
        return DeviceFactory.getDevice().getDeviceDisplay().createImage(name);
    }

    public static Image createImage(Image source) {
        return DeviceFactory.getDevice().getDeviceDisplay().createImage(source);
    }

    public static Image createImage(byte[] imageData, int imageOffset, int imageLength) {
        return DeviceFactory.getDevice().getDeviceDisplay().createImage(imageData, imageOffset, imageLength);
    }

    public Graphics getGraphics() {
        throw new IllegalStateException("Image is immutable");
    }

    public int getHeight() {
        return 0;
    }

    public int getWidth() {
        return 0;
    }

    public boolean isMutable() {
        return false;
    }

    public void getRGB(int[] argb, int offset, int scanlenght, int x, int y, int width, int height) {
    }

    public static Image createImage(InputStream stream) throws IOException {
        return DeviceFactory.getDevice().getDeviceDisplay().createImage(stream);
    }

    public static Image createImage(Image image, int x, int y, int width, int height, int transform) {
        return DeviceFactory.getDevice().getDeviceDisplay().createImage(image, x, y, width, height, transform);
    }

    public static Image createRGBImage(int[] rgb, int width, int height, boolean processAlpha) {
        return DeviceFactory.getDevice().getDeviceDisplay().createRGBImage(rgb, width, height, processAlpha);
    }
}

