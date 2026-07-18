/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;
import nanoxml.XMLElement;
import nanoxml.XMLParseException;
import org.microemu.EmulatorContext;
import org.microemu.app.util.IOUtils;
import org.microemu.device.Device;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.FontManager;
import org.microemu.device.InputMethod;
import org.microemu.device.impl.Color;
import org.microemu.device.impl.DeviceDisplayImpl;
import org.microemu.device.impl.FontManagerImpl;
import org.microemu.device.impl.Polygon;
import org.microemu.device.impl.PositionedImage;
import org.microemu.device.impl.Rectangle;
import org.microemu.device.impl.Shape;
import org.microemu.device.impl.SoftButton;

public abstract class DeviceImpl
implements Device {
    private String name;
    private static EmulatorContext context;
    private Image normalImage;
    private Image overImage;
    private Image pressedImage;
    private Vector buttons;
    private Vector softButtons;
    private boolean hasPointerEvents;
    private boolean hasPointerMotionEvents;
    private boolean hasRepeatEvents;
    private Map systemProperties = new HashMap();
    private int skinVersion;
    public static final String DEFAULT_LOCATION = "org/microemu/device/default/device.xml";
    public static final String RESIZABLE_LOCATION = "org/microemu/device/resizable/device.xml";
    private String descriptorLocation;
    private static Map specialInheritanceAttributeSet;

    public DeviceImpl() {
        this.buttons = new Vector();
        this.softButtons = new Vector();
    }

    public static DeviceImpl create(EmulatorContext context, ClassLoader classLoader, String descriptorLocation, Class defaultDeviceClass) throws IOException {
        XMLElement doc = DeviceImpl.loadDeviceDescriptor(classLoader, descriptorLocation);
        DeviceImpl device = null;
        Enumeration e = doc.enumerateChildren();
        while (e.hasMoreElements()) {
            XMLElement tmp = (XMLElement)e.nextElement();
            if (!tmp.getName().equals("class-name")) continue;
            try {
                Class<?> deviceClass = Class.forName(tmp.getContent(), true, classLoader);
                device = (DeviceImpl)deviceClass.newInstance();
                break;
            }
            catch (ClassNotFoundException ex) {
                throw new IOException(ex.getMessage());
            }
            catch (InstantiationException ex) {
                throw new IOException(ex.getMessage());
            }
            catch (IllegalAccessException ex) {
                throw new IOException(ex.getMessage());
            }
        }
        if (device == null) {
            try {
                device = (DeviceImpl)defaultDeviceClass.newInstance();
            }
            catch (InstantiationException ex) {
                throw new IOException(ex.getMessage());
            }
            catch (IllegalAccessException ex) {
                throw new IOException(ex.getMessage());
            }
        }
        DeviceImpl.context = context;
        device.descriptorLocation = descriptorLocation;
        device.loadConfig(classLoader, DeviceImpl.besourceBase(descriptorLocation), doc);
        return device;
    }

    public void init() {
    }

    public void init(EmulatorContext context) {
        this.init(context, DEFAULT_LOCATION);
    }

    public void init(EmulatorContext context, String descriptorLocation) {
        DeviceImpl.context = context;
        this.descriptorLocation = descriptorLocation.startsWith("/") ? descriptorLocation.substring(1) : descriptorLocation;
        try {
            String base = descriptorLocation.substring(0, descriptorLocation.lastIndexOf("/"));
            XMLElement doc = DeviceImpl.loadDeviceDescriptor(this.getClass().getClassLoader(), descriptorLocation);
            this.loadConfig(this.getClass().getClassLoader(), base, doc);
        }
        catch (IOException ex) {
            System.out.println("Cannot load config: " + ex);
        }
    }

    public String getDescriptorLocation() {
        return this.descriptorLocation;
    }

    public void destroy() {
    }

    public String getName() {
        return this.name;
    }

    public static EmulatorContext getEmulatorContext() {
        return context;
    }

    public InputMethod getInputMethod() {
        return context.getDeviceInputMethod();
    }

    public FontManager getFontManager() {
        return context.getDeviceFontManager();
    }

    public DeviceDisplay getDeviceDisplay() {
        return context.getDeviceDisplay();
    }

    public Image getNormalImage() {
        return this.normalImage;
    }

    public Image getOverImage() {
        return this.overImage;
    }

    public Image getPressedImage() {
        return this.pressedImage;
    }

    public Vector getSoftButtons() {
        return this.softButtons;
    }

    public Vector getButtons() {
        return this.buttons;
    }

    protected void loadConfig(ClassLoader classLoader, String base, XMLElement doc) throws IOException {
        String deviceName = doc.getStringAttribute("name");
        this.name = deviceName != null ? deviceName : base;
        this.loadSkinVersion(doc);
        this.hasPointerEvents = false;
        this.hasPointerMotionEvents = false;
        this.hasRepeatEvents = false;
        ((FontManagerImpl)this.getFontManager()).setAntialiasing(false);
        this.parseDisplay(classLoader, base, doc.getChild("display"));
        Enumeration e = doc.enumerateChildren();
        while (e.hasMoreElements()) {
            XMLElement tmp = (XMLElement)e.nextElement();
            if (tmp.getName().equals("system-properties")) {
                this.parseSystemProperties(tmp);
                continue;
            }
            if (tmp.getName().equals("img") && !((DeviceDisplayImpl)this.getDeviceDisplay()).isResizable()) {
                try {
                    if (tmp.getStringAttribute("name").equals("normal")) {
                        this.normalImage = this.loadImage(classLoader, base, tmp.getStringAttribute("src"));
                        continue;
                    }
                    if (tmp.getStringAttribute("name").equals("over")) {
                        this.overImage = this.loadImage(classLoader, base, tmp.getStringAttribute("src"));
                        continue;
                    }
                    if (!tmp.getStringAttribute("name").equals("pressed")) continue;
                    this.pressedImage = this.loadImage(classLoader, base, tmp.getStringAttribute("src"));
                    continue;
                }
                catch (IOException ex) {
                    System.out.println("Cannot load " + tmp.getStringAttribute("src"));
                    return;
                }
            }
            if (tmp.getName().equals("fonts")) {
                this.parseFonts(classLoader, base, tmp);
                continue;
            }
            if (!tmp.getName().equals("input") && !tmp.getName().equals("keyboard")) continue;
            this.parseInput(tmp);
        }
    }

    private void loadSkinVersion(XMLElement doc) {
        String xmlns = doc.getStringAttribute("xmlns");
        this.skinVersion = xmlns == null ? 20000 : (xmlns.endsWith("/2.0.2/") ? 20002 : 20000);
    }

    private void parseDisplay(ClassLoader classLoader, String base, XMLElement tmp) throws IOException {
        XMLElement tmp_display;
        DeviceDisplayImpl deviceDisplay = (DeviceDisplayImpl)this.getDeviceDisplay();
        String resizable = tmp.getStringAttribute("resizable", "false");
        if (resizable.equalsIgnoreCase("true")) {
            deviceDisplay.setResizable(true);
        } else {
            deviceDisplay.setResizable(false);
        }
        Enumeration e_display = tmp.enumerateChildren();
        while (e_display.hasMoreElements()) {
            tmp_display = (XMLElement)e_display.nextElement();
            if (tmp_display.getName().equals("numcolors")) {
                deviceDisplay.setNumColors(Integer.parseInt(tmp_display.getContent()));
                continue;
            }
            if (tmp_display.getName().equals("iscolor")) {
                deviceDisplay.setIsColor(this.parseBoolean(tmp_display.getContent()));
                continue;
            }
            if (tmp_display.getName().equals("numalphalevels")) {
                deviceDisplay.setNumAlphaLevels(Integer.parseInt(tmp_display.getContent()));
                continue;
            }
            if (tmp_display.getName().equals("background")) {
                deviceDisplay.setBackgroundColor(new Color(Integer.parseInt(tmp_display.getContent(), 16)));
                continue;
            }
            if (tmp_display.getName().equals("foreground")) {
                deviceDisplay.setForegroundColor(new Color(Integer.parseInt(tmp_display.getContent(), 16)));
                continue;
            }
            if (tmp_display.getName().equals("rectangle")) {
                Rectangle rect = this.getRectangle(tmp_display);
                if (deviceDisplay.isResizable()) {
                    rect.x = 0;
                    rect.y = 0;
                }
                deviceDisplay.setDisplayRectangle(rect);
                continue;
            }
            if (!tmp_display.getName().equals("paintable")) continue;
            deviceDisplay.setDisplayPaintable(this.getRectangle(tmp_display));
        }
        e_display = tmp.enumerateChildren();
        while (e_display.hasMoreElements()) {
            tmp_display = (XMLElement)e_display.nextElement();
            if (tmp_display.getName().equals("img")) {
                if (tmp_display.getStringAttribute("name").equals("up") || tmp_display.getStringAttribute("name").equals("down")) {
                    SoftButton icon = deviceDisplay.createSoftButton(this.skinVersion, tmp_display.getStringAttribute("name"), this.getRectangle(tmp_display.getChild("paintable")), this.loadImage(classLoader, base, tmp_display.getStringAttribute("src")), this.loadImage(classLoader, base, tmp_display.getStringAttribute("src")));
                    this.getSoftButtons().addElement(icon);
                    continue;
                }
                if (!tmp_display.getStringAttribute("name").equals("mode")) continue;
                if (tmp_display.getStringAttribute("type").equals("123")) {
                    deviceDisplay.setMode123Image(new PositionedImage(this.loadImage(classLoader, base, tmp_display.getStringAttribute("src")), this.getRectangle(tmp_display.getChild("paintable"))));
                    continue;
                }
                if (tmp_display.getStringAttribute("type").equals("abc")) {
                    deviceDisplay.setModeAbcLowerImage(new PositionedImage(this.loadImage(classLoader, base, tmp_display.getStringAttribute("src")), this.getRectangle(tmp_display.getChild("paintable"))));
                    continue;
                }
                if (!tmp_display.getStringAttribute("type").equals("ABC")) continue;
                deviceDisplay.setModeAbcUpperImage(new PositionedImage(this.loadImage(classLoader, base, tmp_display.getStringAttribute("src")), this.getRectangle(tmp_display.getChild("paintable"))));
                continue;
            }
            if (tmp_display.getName().equals("icon")) {
                Image iconNormalImage = null;
                Image iconPressedImage = null;
                Enumeration e_icon = tmp_display.enumerateChildren();
                while (e_icon.hasMoreElements()) {
                    XMLElement tmp_icon = (XMLElement)e_icon.nextElement();
                    if (!tmp_icon.getName().equals("img")) continue;
                    if (tmp_icon.getStringAttribute("name").equals("normal")) {
                        iconNormalImage = this.loadImage(classLoader, base, tmp_icon.getStringAttribute("src"));
                        continue;
                    }
                    if (!tmp_icon.getStringAttribute("name").equals("pressed")) continue;
                    iconPressedImage = this.loadImage(classLoader, base, tmp_icon.getStringAttribute("src"));
                }
                SoftButton icon = deviceDisplay.createSoftButton(this.skinVersion, tmp_display.getStringAttribute("name"), this.getRectangle(tmp_display.getChild("paintable")), iconNormalImage, iconPressedImage);
                this.getSoftButtons().addElement(icon);
                continue;
            }
            if (!tmp_display.getName().equals("status") || !tmp_display.getStringAttribute("name").equals("input")) continue;
            Rectangle paintable = this.getRectangle(tmp_display.getChild("paintable"));
            Enumeration e_status = tmp_display.enumerateChildren();
            while (e_status.hasMoreElements()) {
                XMLElement tmp_status = (XMLElement)e_status.nextElement();
                if (!tmp_status.getName().equals("img")) continue;
                if (tmp_status.getStringAttribute("name").equals("123")) {
                    deviceDisplay.setMode123Image(new PositionedImage(this.loadImage(classLoader, base, tmp_status.getStringAttribute("src")), paintable));
                    continue;
                }
                if (tmp_status.getStringAttribute("name").equals("abc")) {
                    deviceDisplay.setModeAbcLowerImage(new PositionedImage(this.loadImage(classLoader, base, tmp_status.getStringAttribute("src")), paintable));
                    continue;
                }
                if (!tmp_status.getStringAttribute("name").equals("ABC")) continue;
                deviceDisplay.setModeAbcUpperImage(new PositionedImage(this.loadImage(classLoader, base, tmp_status.getStringAttribute("src")), paintable));
            }
        }
    }

    private void parseFonts(ClassLoader classLoader, String base, XMLElement tmp) throws IOException {
        FontManagerImpl fontManager = (FontManagerImpl)this.getFontManager();
        String hint = tmp.getStringAttribute("hint");
        boolean antialiasing = false;
        if (hint != null && hint.equals("antialiasing")) {
            antialiasing = true;
        }
        fontManager.setAntialiasing(antialiasing);
        Enumeration e_fonts = tmp.enumerateChildren();
        while (e_fonts.hasMoreElements()) {
            XMLElement tmp_font = (XMLElement)e_fonts.nextElement();
            if (!tmp_font.getName().equals("font")) continue;
            String face = tmp_font.getStringAttribute("face").toLowerCase();
            String style = tmp_font.getStringAttribute("style").toLowerCase();
            String size = tmp_font.getStringAttribute("size").toLowerCase();
            if (face.startsWith("face_")) {
                face = face.substring("face_".length());
            }
            if (style.startsWith("style_")) {
                style = style.substring("style_".length());
            }
            if (size.startsWith("size_")) {
                size = size.substring("size_".length());
            }
            Enumeration e_defs = tmp_font.enumerateChildren();
            while (e_defs.hasMoreElements()) {
                int defSize;
                String defStyle;
                XMLElement tmp_def = (XMLElement)e_defs.nextElement();
                if (tmp_def.getName().equals("system")) {
                    String defName = tmp_def.getStringAttribute("name");
                    defStyle = tmp_def.getStringAttribute("style");
                    defSize = Integer.parseInt(tmp_def.getStringAttribute("size"));
                    fontManager.setFont(face, style, size, fontManager.createSystemFont(defName, defStyle, defSize, antialiasing));
                    continue;
                }
                if (!tmp_def.getName().equals("ttf")) continue;
                String defSrc = tmp_def.getStringAttribute("src");
                defStyle = tmp_def.getStringAttribute("style");
                defSize = Integer.parseInt(tmp_def.getStringAttribute("size"));
                fontManager.setFont(face, style, size, fontManager.createTrueTypeFont(this.getResourceUrl(classLoader, base, defSrc), defStyle, defSize, antialiasing));
            }
        }
    }

    private void parseInput(XMLElement tmp) {
        DeviceDisplayImpl deviceDisplay = (DeviceDisplayImpl)this.getDeviceDisplay();
        boolean resizable = deviceDisplay.isResizable();
        Enumeration e_keyboard = tmp.enumerateChildren();
        while (e_keyboard.hasMoreElements()) {
            XMLElement tmp_keyboard = (XMLElement)e_keyboard.nextElement();
            if (tmp_keyboard.getName().equals("haspointerevents")) {
                this.hasPointerEvents = this.parseBoolean(tmp_keyboard.getContent());
                continue;
            }
            if (tmp_keyboard.getName().equals("haspointermotionevents")) {
                this.hasPointerMotionEvents = this.parseBoolean(tmp_keyboard.getContent());
                continue;
            }
            if (tmp_keyboard.getName().equals("hasrepeatevents")) {
                this.hasRepeatEvents = this.parseBoolean(tmp_keyboard.getContent());
                continue;
            }
            if (tmp_keyboard.getName().equals("button")) {
                Shape shape = null;
                Hashtable<String, char[]> inputToChars = new Hashtable<String, char[]>();
                Enumeration e_button = tmp_keyboard.enumerateChildren();
                while (e_button.hasMoreElements()) {
                    XMLElement tmp_button = (XMLElement)e_button.nextElement();
                    if (tmp_button.getName().equals("chars")) {
                        String input = tmp_button.getStringAttribute("input", "common");
                        Vector<String> stringArray = new Vector<String>();
                        Enumeration e_chars = tmp_button.enumerateChildren();
                        while (e_chars.hasMoreElements()) {
                            XMLElement tmp_chars = (XMLElement)e_chars.nextElement();
                            if (!tmp_chars.getName().equals("char")) continue;
                            stringArray.addElement(tmp_chars.getContent());
                        }
                        char[] charArray = new char[stringArray.size()];
                        for (int i = 0; i < stringArray.size(); ++i) {
                            String str = (String)stringArray.elementAt(i);
                            charArray[i] = str.length() > 0 ? str.charAt(0) : (char)32;
                        }
                        inputToChars.put(input, charArray);
                        continue;
                    }
                    if (tmp_button.getName().equals("rectangle") && !resizable) {
                        shape = this.getRectangle(tmp_button);
                        continue;
                    }
                    if (!tmp_button.getName().equals("polygon") || resizable) continue;
                    shape = this.getPolygon(tmp_button);
                }
                int keyCode = tmp_keyboard.getIntAttribute("keyCode", Integer.MIN_VALUE);
                this.getButtons().addElement(deviceDisplay.createButton(this.skinVersion, tmp_keyboard.getStringAttribute("name"), shape, keyCode, tmp_keyboard.getStringAttribute("key"), tmp_keyboard.getStringAttribute("keyboardChars"), inputToChars, tmp_keyboard.getBooleanAttribute("modeChange", false)));
                continue;
            }
            if (!tmp_keyboard.getName().equals("softbutton")) continue;
            Vector<String> commands = new Vector<String>();
            Shape shape = null;
            Rectangle paintable = null;
            Font font = null;
            Enumeration e_button = tmp_keyboard.enumerateChildren();
            while (e_button.hasMoreElements()) {
                XMLElement tmp_button = (XMLElement)e_button.nextElement();
                if (tmp_button.getName().equals("rectangle") && !resizable) {
                    shape = this.getRectangle(tmp_button);
                    continue;
                }
                if (tmp_button.getName().equals("polygon") && !resizable) {
                    shape = this.getPolygon(tmp_button);
                    continue;
                }
                if (tmp_button.getName().equals("paintable")) {
                    paintable = this.getRectangle(tmp_button);
                    continue;
                }
                if (tmp_button.getName().equals("command")) {
                    commands.addElement(tmp_button.getContent());
                    continue;
                }
                if (!tmp_button.getName().equals("font")) continue;
                font = DeviceImpl.getFont(tmp_button.getStringAttribute("face"), tmp_button.getStringAttribute("style"), tmp_button.getStringAttribute("size"));
            }
            int keyCode = tmp_keyboard.getIntAttribute("keyCode", Integer.MIN_VALUE);
            SoftButton button = deviceDisplay.createSoftButton(this.skinVersion, tmp_keyboard.getStringAttribute("name"), shape, keyCode, tmp_keyboard.getStringAttribute("key"), paintable, tmp_keyboard.getStringAttribute("alignment"), commands, font);
            this.getButtons().addElement(button);
            this.getSoftButtons().addElement(button);
        }
    }

    private void parseSystemProperties(XMLElement tmp) {
        Enumeration e_prop = tmp.enumerateChildren();
        while (e_prop.hasMoreElements()) {
            XMLElement tmp_prop = (XMLElement)e_prop.nextElement();
            if (!tmp_prop.getName().equals("system-property")) continue;
            this.systemProperties.put(tmp_prop.getStringAttribute("name"), tmp_prop.getStringAttribute("value"));
        }
    }

    private static Font getFont(String face, String style, String size) {
        int meFace = 0;
        if (face.equalsIgnoreCase("system")) {
            meFace |= 0;
        } else if (face.equalsIgnoreCase("monospace")) {
            meFace |= 0x20;
        } else if (face.equalsIgnoreCase("proportional")) {
            meFace |= 0x40;
        }
        int meStyle = 0;
        String testStyle = style.toLowerCase();
        if (testStyle.indexOf("plain") != -1) {
            meStyle |= 0;
        }
        if (testStyle.indexOf("bold") != -1) {
            meStyle |= 1;
        }
        if (testStyle.indexOf("italic") != -1) {
            meStyle |= 2;
        }
        if (testStyle.indexOf("underlined") != -1) {
            meStyle |= 4;
        }
        int meSize = 0;
        if (size.equalsIgnoreCase("small")) {
            meSize |= 8;
        } else if (size.equalsIgnoreCase("medium")) {
            meSize |= 0;
        } else if (size.equalsIgnoreCase("large")) {
            meSize |= 0x10;
        }
        return Font.getFont(meFace, meStyle, meSize);
    }

    private Rectangle getRectangle(XMLElement source) {
        Rectangle rect = new Rectangle();
        Enumeration e_rectangle = source.enumerateChildren();
        while (e_rectangle.hasMoreElements()) {
            XMLElement tmp_rectangle = (XMLElement)e_rectangle.nextElement();
            if (tmp_rectangle.getName().equals("x")) {
                rect.x = Integer.parseInt(tmp_rectangle.getContent());
                continue;
            }
            if (tmp_rectangle.getName().equals("y")) {
                rect.y = Integer.parseInt(tmp_rectangle.getContent());
                continue;
            }
            if (tmp_rectangle.getName().equals("width")) {
                rect.width = Integer.parseInt(tmp_rectangle.getContent());
                continue;
            }
            if (!tmp_rectangle.getName().equals("height")) continue;
            rect.height = Integer.parseInt(tmp_rectangle.getContent());
        }
        return rect;
    }

    private Polygon getPolygon(XMLElement source) {
        Polygon poly = new Polygon();
        Enumeration e_poly = source.enumerateChildren();
        while (e_poly.hasMoreElements()) {
            XMLElement tmp_point = (XMLElement)e_poly.nextElement();
            if (!tmp_point.getName().equals("point")) continue;
            poly.addPoint(Integer.parseInt(tmp_point.getStringAttribute("x")), Integer.parseInt(tmp_point.getStringAttribute("y")));
        }
        return poly;
    }

    private boolean parseBoolean(String value) {
        return value.toLowerCase().equals(new String("true").toLowerCase());
    }

    public boolean hasPointerEvents() {
        return this.hasPointerEvents;
    }

    public boolean hasPointerMotionEvents() {
        return this.hasPointerMotionEvents;
    }

    public boolean hasRepeatEvents() {
        return this.hasRepeatEvents;
    }

    public boolean vibrate(int duration) {
        return false;
    }

    public Map getSystemProperties() {
        return this.systemProperties;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void saveDevice(XMLElement doc) {
        File configFile = new File(".", "device-tmp.xml");
        FileWriter fw = null;
        try {
            fw = new FileWriter(configFile);
            doc.write(fw);
            fw.close();
        }
        catch (IOException ex) {
            try {
                System.out.println(ex);
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(fw);
                throw throwable;
            }
            IOUtils.closeQuietly(fw);
        }
        IOUtils.closeQuietly(fw);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static XMLElement loadDeviceDescriptor(ClassLoader classLoader, String descriptorLocation) throws IOException {
        XMLElement doc;
        InputStream descriptor = classLoader.getResourceAsStream(descriptorLocation);
        if (descriptor == null) {
            throw new IOException("Cannot find descriptor at: " + descriptorLocation);
        }
        try {
            doc = DeviceImpl.loadXmlDocument(descriptor);
        }
        finally {
            IOUtils.closeQuietly(descriptor);
        }
        String parent = doc.getStringAttribute("extends");
        if (parent != null) {
            return DeviceImpl.inheritXML(DeviceImpl.loadDeviceDescriptor(classLoader, DeviceImpl.expandResourcePath(DeviceImpl.besourceBase(descriptorLocation), parent)), doc, "/");
        }
        return doc;
    }

    private static void inheritanceConstInit() {
        if (specialInheritanceAttributeSet == null) {
            specialInheritanceAttributeSet = new Hashtable();
            specialInheritanceAttributeSet.put("//FONTS/FONT", new String[]{"face", "style", "size"});
        }
    }

    static XMLElement inheritXML(XMLElement parent, XMLElement child, String parentName) {
        DeviceImpl.inheritanceConstInit();
        if (parent == null) {
            return child;
        }
        parent.setContent(child.getContent());
        Enumeration ena = child.enumerateAttributeNames();
        while (ena.hasMoreElements()) {
            String key = (String)ena.nextElement();
            parent.setAttribute(key, child.getAttribute(key));
        }
        Enumeration enc = child.enumerateChildren();
        while (enc.hasMoreElements()) {
            String[] equalAttributes;
            XMLElement c = (XMLElement)enc.nextElement();
            String fullName = (parentName + "/" + c.getName()).toUpperCase(Locale.ENGLISH);
            boolean inheritWithName = false;
            if (c.getStringAttribute("name") != null) {
                inheritWithName = true;
            } else {
                boolean bl = inheritWithName = child.getChildCount(c.getName()) > 1 || parent.getChildCount(c.getName()) > 1;
            }
            XMLElement p = inheritWithName ? ((equalAttributes = (String[])specialInheritanceAttributeSet.get(fullName)) != null ? parent.getChild(c.getName(), c.getStringAttributes(equalAttributes)) : parent.getChild(c.getName(), c.getStringAttribute("name"))) : parent.getChild(c.getName());
            boolean inheritOverride = c.getBooleanAttribute("override", false);
            if (inheritOverride) {
                c.removeAttribute("override");
                if (p != null) {
                    parent.removeChild(p);
                    p = null;
                }
            }
            if (p == null) {
                parent.addChild(c);
                continue;
            }
            DeviceImpl.inheritXML(p, c, fullName);
        }
        return parent;
    }

    private static XMLElement loadXmlDocument(InputStream descriptor) throws IOException {
        XMLElement doc = new XMLElement();
        try {
            doc.parse(descriptor, 1);
        }
        catch (XMLParseException ex) {
            throw new IOException(ex.toString());
        }
        return doc;
    }

    private static String besourceBase(String descriptorLocation) {
        return descriptorLocation.substring(0, descriptorLocation.lastIndexOf("/"));
    }

    private static String expandResourcePath(String base, String src) throws IOException {
        String expandedSource = src.startsWith("/") ? src : base + "/" + src;
        if (expandedSource.startsWith("/")) {
            expandedSource = expandedSource.substring(1);
        }
        return expandedSource;
    }

    private URL getResourceUrl(ClassLoader classLoader, String base, String src) throws IOException {
        String expandedSource = DeviceImpl.expandResourcePath(base, src);
        URL result = classLoader.getResource(expandedSource);
        if (result == null) {
            throw new IOException("Cannot find resource: " + expandedSource);
        }
        return result;
    }

    private Image loadImage(ClassLoader classLoader, String base, String src) throws IOException {
        URL url = this.getResourceUrl(classLoader, base, src);
        return ((DeviceDisplayImpl)this.getDeviceDisplay()).createSystemImage(url);
    }
}

