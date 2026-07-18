/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import org.microemu.DisplayComponent;
import org.microemu.EmulatorContext;
import org.microemu.MIDletBridge;
import org.microemu.app.classloader.ClassPreprocessor;
import org.microemu.app.classloader.ExtensionsClassLoader;
import org.microemu.app.classloader.InstrumentationConfig;
import org.microemu.app.ui.Message;
import org.microemu.app.ui.noui.NoUiDisplayComponent;
import org.microemu.app.util.IOUtils;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.FontManager;
import org.microemu.device.InputMethod;
import org.microemu.device.impl.DeviceDisplayImpl;
import org.microemu.device.impl.DeviceImpl;
import org.microemu.device.j2se.J2SEDevice;
import org.microemu.device.j2se.J2SEDeviceDisplay;
import org.microemu.device.j2se.J2SEFontManager;
import org.microemu.device.j2se.J2SEInputMethod;
import org.microemu.log.Logger;

public class AppletProducer {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void createHtml(File htmlOutputFile, DeviceImpl device, String className, File midletOutputFile, File appletPackageOutputFile, File deviceOutputFile) throws IOException {
        int height;
        int width;
        if (((DeviceDisplayImpl)device.getDeviceDisplay()).isResizable()) {
            width = device.getDeviceDisplay().getFullWidth();
            height = device.getDeviceDisplay().getFullHeight();
        } else {
            width = device.getNormalImage().getWidth();
            height = device.getNormalImage().getHeight();
        }
        FileWriter writer = null;
        try {
            writer = new FileWriter(htmlOutputFile);
            writer.write("");
            writer.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n\n");
            writer.write("<html>\n");
            writer.write("\t<head>\n");
            writer.write("\t\t<title>MicroEmulator</title>\n");
            writer.write("\t</head>\n");
            writer.write("\t<body>\n");
            writer.write("\t\t<applet code=\"org.microemu.applet.Main\"\n");
            writer.write("\t\t\t\twidth=\"" + width + "\" height=\"" + height + "\"\n");
            writer.write("\t\t\t\tarchive=\"" + appletPackageOutputFile.getName() + ",");
            if (deviceOutputFile != null) {
                writer.write(deviceOutputFile.getName() + ",");
            }
            writer.write(midletOutputFile.getName() + "\">\n");
            writer.write("\t\t\t<param name=\"midlet\" value=\"" + className + "\">\n");
            if (device.getDescriptorLocation() != null) {
                writer.write("\t\t\t<param name=\"device\" value=\"" + device.getDescriptorLocation() + "\">\n");
            }
            writer.write("\t\t</applet>\n");
            writer.write("\t</body>\n");
            writer.write("</html>\n");
        }
        catch (Throwable throwable) {
            IOUtils.closeQuietly(writer);
            throw throwable;
        }
        IOUtils.closeQuietly(writer);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void createMidlet(URL midletInputUrl, File midletOutputFile) throws IOException {
        JarOutputStream jos;
        JarInputStream ijis;
        JarInputStream jis;
        block8: {
            jis = null;
            ijis = null;
            jos = null;
            InstrumentationConfig config = new InstrumentationConfig();
            config.setEnhanceThreadCreation(true);
            try {
                JarEntry jarEntry;
                jis = new JarInputStream(midletInputUrl.openStream());
                Manifest manifest = jis.getManifest();
                jos = manifest == null ? new JarOutputStream(new FileOutputStream(midletOutputFile)) : new JarOutputStream((OutputStream)new FileOutputStream(midletOutputFile), manifest);
                byte[] inputBuffer = new byte[1024];
                while ((jarEntry = jis.getNextJarEntry()) != null) {
                    int read;
                    if (jarEntry.isDirectory()) continue;
                    String name = jarEntry.getName();
                    int size = 0;
                    int length = inputBuffer.length;
                    while ((read = jis.read(inputBuffer, size, length)) > 0) {
                        length = 1024;
                        if ((size += read) + length <= inputBuffer.length) continue;
                        byte[] newInputBuffer = new byte[size + length];
                        System.arraycopy(inputBuffer, 0, newInputBuffer, 0, inputBuffer.length);
                        inputBuffer = newInputBuffer;
                    }
                    byte[] outputBuffer = inputBuffer;
                    int outputSize = size;
                    if (name.endsWith(".class")) {
                        outputBuffer = ClassPreprocessor.instrument(new ByteArrayInputStream(inputBuffer, 0, size), config);
                        outputSize = outputBuffer.length;
                    }
                    jos.putNextEntry(new JarEntry(name));
                    jos.write(outputBuffer, 0, outputSize);
                }
                URL url = AppletProducer.class.getResource("/microemu-injected.jar");
                if (url != null) {
                    ijis = new JarInputStream(url.openStream());
                    while ((jarEntry = ijis.getNextJarEntry()) != null) {
                        int read;
                        if (!jarEntry.getName().equals("org/microemu/Injected.class")) continue;
                        jos.putNextEntry(new JarEntry(jarEntry.getName()));
                        while ((read = ijis.read(inputBuffer)) > 0) {
                            jos.write(inputBuffer, 0, read);
                        }
                    }
                    break block8;
                }
                Logger.error("Cannot find microemu-injected.jar resource in classpath");
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(jis);
                IOUtils.closeQuietly(ijis);
                IOUtils.closeQuietly(jos);
                throw throwable;
            }
        }
        IOUtils.closeQuietly(jis);
        IOUtils.closeQuietly(ijis);
        IOUtils.closeQuietly(jos);
    }

    public static void main(String[] args) {
        String midletClass = null;
        File appletInputFile = null;
        File deviceInputFile = null;
        File midletInputFile = null;
        File htmlOutputFile = null;
        File appletOutputFile = null;
        File deviceOutputFile = null;
        File midletOutputFile = null;
        ArrayList<String> params = new ArrayList<String>();
        for (int i = 0; i < args.length; ++i) {
            params.add(args[i]);
        }
        Iterator argsIterator = params.iterator();
        while (argsIterator.hasNext()) {
            String arg = (String)argsIterator.next();
            argsIterator.remove();
            if (arg.equals("--help") || arg.equals("-help")) {
                System.out.println(AppletProducer.usage());
                System.exit(0);
                continue;
            }
            if (arg.equals("--midletClass")) {
                midletClass = (String)argsIterator.next();
                argsIterator.remove();
                continue;
            }
            if (arg.equals("--appletInput")) {
                appletInputFile = new File((String)argsIterator.next());
                argsIterator.remove();
                continue;
            }
            if (arg.equals("--deviceInput")) {
                deviceInputFile = new File((String)argsIterator.next());
                argsIterator.remove();
                continue;
            }
            if (arg.equals("--midletInput")) {
                midletInputFile = new File((String)argsIterator.next());
                argsIterator.remove();
                continue;
            }
            if (arg.equals("--htmlOutput")) {
                htmlOutputFile = new File((String)argsIterator.next());
                argsIterator.remove();
                continue;
            }
            if (arg.equals("--appletOutput")) {
                appletOutputFile = new File((String)argsIterator.next());
                argsIterator.remove();
                continue;
            }
            if (arg.equals("--deviceOutput")) {
                deviceOutputFile = new File((String)argsIterator.next());
                argsIterator.remove();
                continue;
            }
            if (!arg.equals("--midletOutput")) continue;
            midletOutputFile = new File((String)argsIterator.next());
            argsIterator.remove();
        }
        if (midletClass == null || appletInputFile == null || deviceInputFile == null || midletInputFile == null || htmlOutputFile == null || appletOutputFile == null || deviceOutputFile == null || midletOutputFile == null) {
            System.out.println(AppletProducer.usage());
            System.exit(0);
        }
        try {
            DeviceImpl device = null;
            String descriptorLocation = null;
            JarFile jar = new JarFile(deviceInputFile);
            Enumeration<JarEntry> en = jar.entries();
            while (en.hasMoreElements()) {
                String entry = en.nextElement().getName();
                if (!entry.toLowerCase().endsWith(".xml") && !entry.toLowerCase().endsWith("device.txt") || entry.toLowerCase().startsWith("meta-inf")) continue;
                descriptorLocation = entry;
                break;
            }
            if (descriptorLocation != null) {
                EmulatorContext context = new EmulatorContext(){
                    private DisplayComponent displayComponent = new NoUiDisplayComponent();
                    private InputMethod inputMethod = new J2SEInputMethod();
                    private DeviceDisplay deviceDisplay = new J2SEDeviceDisplay(this);
                    private FontManager fontManager = new J2SEFontManager();

                    public DisplayComponent getDisplayComponent() {
                        return this.displayComponent;
                    }

                    public InputMethod getDeviceInputMethod() {
                        return this.inputMethod;
                    }

                    public DeviceDisplay getDeviceDisplay() {
                        return this.deviceDisplay;
                    }

                    public FontManager getDeviceFontManager() {
                        return this.fontManager;
                    }

                    public InputStream getResourceAsStream(String name) {
                        return MIDletBridge.getCurrentMIDlet().getClass().getResourceAsStream(name);
                    }

                    public boolean platformRequest(final String URL2) {
                        new Thread(new Runnable(){

                            public void run() {
                                Message.info("MIDlet requests that the device handle the following URL: " + URL2);
                            }
                        }).start();
                        return false;
                    }
                };
                URL[] urls = new URL[]{deviceInputFile.toURI().toURL()};
                ExtensionsClassLoader classLoader = new ExtensionsClassLoader(urls, urls.getClass().getClassLoader());
                device = DeviceImpl.create(context, classLoader, descriptorLocation, J2SEDevice.class);
            }
            if (device == null) {
                System.out.println("Error parsing device package: " + descriptorLocation);
                System.exit(0);
            }
            AppletProducer.createHtml(htmlOutputFile, device, midletClass, midletOutputFile, appletOutputFile, deviceOutputFile);
            AppletProducer.createMidlet(midletInputFile.toURI().toURL(), midletOutputFile);
            IOUtils.copyFile(appletInputFile, appletOutputFile);
            IOUtils.copyFile(deviceInputFile, deviceOutputFile);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        System.exit(0);
    }

    private static String usage() {
        return "--midletClass {midlet class name} \n--appletInput {emulator applet jar input file} \n--deviceInput {device jar input file} \n--midletInput {midlet jar input file} \n--htmlOutput {html output file} \n--appletOutput {emulator applet jar output file} \n--deviceOutput {device jar output file} \n--midletOutput {midlet jar output file}";
    }
}

