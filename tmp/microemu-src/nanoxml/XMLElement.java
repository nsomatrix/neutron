/*
 * Decompiled with CFR 0.152.
 */
package nanoxml;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nanoxml.XMLParseException;

public class XMLElement {
    static final long serialVersionUID = 6685035139346394777L;
    public static final int NANOXML_MAJOR_VERSION = 2;
    public static final int NANOXML_MINOR_VERSION = 2;
    private Hashtable attributes;
    private Vector children;
    private String name;
    private String contents;
    private Hashtable entities;
    private int lineNr;
    private boolean ignoreCase;
    private boolean ignoreWhitespace;
    private char charReadTooMuch;
    private Reader reader;
    private int parserLineNr;

    public XMLElement() {
        this(new Hashtable(), false, true, true);
    }

    public XMLElement(boolean skipLeadingWhitespace, boolean ignoreCase) {
        this(new Hashtable(), skipLeadingWhitespace, true, ignoreCase);
    }

    public XMLElement(Hashtable entities) {
        this(entities, false, true, true);
    }

    public XMLElement(boolean skipLeadingWhitespace) {
        this(new Hashtable(), skipLeadingWhitespace, true, true);
    }

    public XMLElement(Hashtable entities, boolean skipLeadingWhitespace) {
        this(entities, skipLeadingWhitespace, true, true);
    }

    public XMLElement(Hashtable entities, boolean skipLeadingWhitespace, boolean ignoreCase) {
        this(entities, skipLeadingWhitespace, true, ignoreCase);
    }

    protected XMLElement(Hashtable entities, boolean skipLeadingWhitespace, boolean fillBasicConversionTable, boolean ignoreCase) {
        this.ignoreWhitespace = skipLeadingWhitespace;
        this.ignoreCase = ignoreCase;
        this.name = null;
        this.contents = "";
        this.attributes = new Hashtable();
        this.children = new Vector();
        this.entities = entities;
        this.lineNr = 0;
        Enumeration en = this.entities.keys();
        while (en.hasMoreElements()) {
            Object key = en.nextElement();
            Object value = this.entities.get(key);
            if (!(value instanceof String)) continue;
            value = ((String)value).toCharArray();
            this.entities.put(key, value);
        }
        if (fillBasicConversionTable) {
            this.entities.put("amp", new char[]{'&'});
            this.entities.put("quot", new char[]{'\"'});
            this.entities.put("apos", new char[]{'\''});
            this.entities.put("lt", new char[]{'<'});
            this.entities.put("gt", new char[]{'>'});
        }
    }

    public void addChild(XMLElement child) {
        this.children.addElement(child);
    }

    public XMLElement addChild(String name) {
        XMLElement xml = new XMLElement(true, false);
        xml.setName(name);
        this.addChild(xml);
        return xml;
    }

    public XMLElement addChild(String name, String value) {
        XMLElement xml = this.addChild(name);
        xml.setContent(value);
        return xml;
    }

    public void setAttribute(String name, Object value) {
        if (this.ignoreCase) {
            name = name.toUpperCase();
        }
        this.attributes.put(name, value.toString());
    }

    public void addProperty(String name, Object value) {
        this.setAttribute(name, value);
    }

    public void setIntAttribute(String name, int value) {
        if (this.ignoreCase) {
            name = name.toUpperCase();
        }
        this.attributes.put(name, Integer.toString(value));
    }

    public void addProperty(String key, int value) {
        this.setIntAttribute(key, value);
    }

    public void setDoubleAttribute(String name, double value) {
        if (this.ignoreCase) {
            name = name.toUpperCase();
        }
        this.attributes.put(name, Double.toString(value));
    }

    public void addProperty(String name, double value) {
        this.setDoubleAttribute(name, value);
    }

    public int countChildren() {
        return this.children.size();
    }

    public Enumeration enumerateAttributeNames() {
        return this.attributes.keys();
    }

    public Enumeration enumeratePropertyNames() {
        return this.enumerateAttributeNames();
    }

    public Enumeration enumerateChildren() {
        return this.children.elements();
    }

    public Vector getChildren() {
        try {
            return (Vector)this.children.clone();
        }
        catch (Exception e) {
            return null;
        }
    }

    public XMLElement getChild(String name) {
        Enumeration en = this.children.elements();
        while (en.hasMoreElements()) {
            XMLElement el = (XMLElement)en.nextElement();
            if (!el.getName().equals(name)) continue;
            return el;
        }
        return null;
    }

    public XMLElement getChildOrNew(String name) {
        XMLElement c = this.getChild(name);
        if (c == null) {
            c = this.addChild(name);
        }
        return c;
    }

    public int getChildCount(String name) {
        int cnt = 0;
        Enumeration en = this.children.elements();
        while (en.hasMoreElements()) {
            XMLElement el = (XMLElement)en.nextElement();
            if (!el.getName().equals(name)) continue;
            ++cnt;
        }
        return cnt;
    }

    public XMLElement getChild(String name, String attrValue) {
        Enumeration en = this.children.elements();
        while (en.hasMoreElements()) {
            XMLElement el = (XMLElement)en.nextElement();
            String elAttrValue = el.getStringAttribute("name");
            if (!el.getName().equals(name) || attrValue != elAttrValue && !attrValue.equals(elAttrValue)) continue;
            return el;
        }
        return null;
    }

    public XMLElement getChild(String name, Map equalAttributesNameValue) {
        Enumeration en = this.children.elements();
        block0: while (en.hasMoreElements()) {
            XMLElement el = (XMLElement)en.nextElement();
            if (!el.getName().equals(name)) continue;
            Iterator i = equalAttributesNameValue.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry atrNameValue = i.next();
                String attrValue = el.getStringAttribute((String)atrNameValue.getKey());
                if (!(atrNameValue.getValue() == null ? attrValue != null : !atrNameValue.getValue().equals(attrValue))) continue;
                continue block0;
            }
            return el;
        }
        return null;
    }

    public int getChildInteger(String name, int defaultValue) {
        XMLElement xml = this.getChild(name);
        if (xml == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(xml.getContent());
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public String getChildString(String name, String defaultValue) {
        XMLElement xml = this.getChild(name);
        if (xml == null) {
            return defaultValue;
        }
        String v = xml.getContent();
        if (v == null || v.length() == 0) {
            return defaultValue;
        }
        return v;
    }

    public String getContents() {
        return this.getContent();
    }

    public String getContent() {
        return this.contents;
    }

    public int getLineNr() {
        return this.lineNr;
    }

    public Object getAttribute(String name) {
        return this.getAttribute(name, null);
    }

    public Object getAttribute(String name, Object defaultValue) {
        Object value;
        if (this.ignoreCase) {
            name = name.toUpperCase();
        }
        if ((value = this.attributes.get(name)) == null) {
            value = defaultValue;
        }
        return value;
    }

    public Object getAttribute(String name, Hashtable valueSet, String defaultKey, boolean allowLiterals) {
        Object result;
        Object key;
        if (this.ignoreCase) {
            name = name.toUpperCase();
        }
        if ((key = this.attributes.get(name)) == null) {
            key = defaultKey;
        }
        if ((result = valueSet.get(key)) == null) {
            if (allowLiterals) {
                result = key;
            } else {
                throw this.invalidValue(name, (String)key);
            }
        }
        return result;
    }

    public String getStringAttribute(String name) {
        return this.getStringAttribute(name, null);
    }

    public Map getStringAttributes(String[] names) {
        HashMap<String, String> attrNameValue = new HashMap<String, String>();
        for (int i = 0; i < names.length; ++i) {
            attrNameValue.put(names[i], this.getStringAttribute(names[i]));
        }
        return attrNameValue;
    }

    public String getStringAttribute(String name, String defaultValue) {
        return (String)this.getAttribute(name, defaultValue);
    }

    public String getStringAttribute(String name, Hashtable valueSet, String defaultKey, boolean allowLiterals) {
        return (String)this.getAttribute(name, valueSet, defaultKey, allowLiterals);
    }

    public int getIntAttribute(String name) {
        return this.getIntAttribute(name, 0);
    }

    public int getIntAttribute(String name, int defaultValue) {
        String value;
        if (this.ignoreCase) {
            name = name.toUpperCase();
        }
        if ((value = (String)this.attributes.get(name)) == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
            throw this.invalidValue(name, value);
        }
    }

    public int getIntAttribute(String name, Hashtable valueSet, String defaultKey, boolean allowLiteralNumbers) {
        Integer result;
        Object key;
        if (this.ignoreCase) {
            name = name.toUpperCase();
        }
        if ((key = this.attributes.get(name)) == null) {
            key = defaultKey;
        }
        try {
            result = (Integer)valueSet.get(key);
        }
        catch (ClassCastException e) {
            throw this.invalidValueSet(name);
        }
        if (result == null) {
            if (!allowLiteralNumbers) {
                throw this.invalidValue(name, (String)key);
            }
            try {
                result = Integer.valueOf((String)key);
            }
            catch (NumberFormatException e) {
                throw this.invalidValue(name, (String)key);
            }
        }
        return result;
    }

    public double getDoubleAttribute(String name) {
        return this.getDoubleAttribute(name, 0.0);
    }

    public double getDoubleAttribute(String name, double defaultValue) {
        String value;
        if (this.ignoreCase) {
            name = name.toUpperCase();
        }
        if ((value = (String)this.attributes.get(name)) == null) {
            return defaultValue;
        }
        try {
            return Double.valueOf(value);
        }
        catch (NumberFormatException e) {
            throw this.invalidValue(name, value);
        }
    }

    public double getDoubleAttribute(String name, Hashtable valueSet, String defaultKey, boolean allowLiteralNumbers) {
        Double result;
        Object key;
        if (this.ignoreCase) {
            name = name.toUpperCase();
        }
        if ((key = this.attributes.get(name)) == null) {
            key = defaultKey;
        }
        try {
            result = (Double)valueSet.get(key);
        }
        catch (ClassCastException e) {
            throw this.invalidValueSet(name);
        }
        if (result == null) {
            if (!allowLiteralNumbers) {
                throw this.invalidValue(name, (String)key);
            }
            try {
                result = Double.valueOf((String)key);
            }
            catch (NumberFormatException e) {
                throw this.invalidValue(name, (String)key);
            }
        }
        return result;
    }

    public boolean getBooleanAttribute(String name, String trueValue, String falseValue, boolean defaultValue) {
        Object value;
        if (this.ignoreCase) {
            name = name.toUpperCase();
        }
        if ((value = this.attributes.get(name)) == null) {
            return defaultValue;
        }
        if (value.equals(trueValue)) {
            return true;
        }
        if (value.equals(falseValue)) {
            return false;
        }
        throw this.invalidValue(name, (String)value);
    }

    public boolean getBooleanAttribute(String name, boolean defaultValue) {
        return this.getBooleanAttribute(name, "true", "false", defaultValue);
    }

    public int getIntProperty(String name, Hashtable valueSet, String defaultKey) {
        return this.getIntAttribute(name, valueSet, defaultKey, false);
    }

    public String getProperty(String name) {
        return this.getStringAttribute(name);
    }

    public String getProperty(String name, String defaultValue) {
        return this.getStringAttribute(name, defaultValue);
    }

    public int getProperty(String name, int defaultValue) {
        return this.getIntAttribute(name, defaultValue);
    }

    public double getProperty(String name, double defaultValue) {
        return this.getDoubleAttribute(name, defaultValue);
    }

    public boolean getProperty(String key, String trueValue, String falseValue, boolean defaultValue) {
        return this.getBooleanAttribute(key, trueValue, falseValue, defaultValue);
    }

    public Object getProperty(String name, Hashtable valueSet, String defaultKey) {
        return this.getAttribute(name, valueSet, defaultKey, false);
    }

    public String getStringProperty(String name, Hashtable valueSet, String defaultKey) {
        return this.getStringAttribute(name, valueSet, defaultKey, false);
    }

    public int getSpecialIntProperty(String name, Hashtable valueSet, String defaultKey) {
        return this.getIntAttribute(name, valueSet, defaultKey, true);
    }

    public double getSpecialDoubleProperty(String name, Hashtable valueSet, String defaultKey) {
        return this.getDoubleAttribute(name, valueSet, defaultKey, true);
    }

    public String getName() {
        return this.name;
    }

    public String getTagName() {
        return this.getName();
    }

    public void parseFromReader(Reader reader) throws IOException, XMLParseException {
        this.parseFromReader(reader, 1);
    }

    public void parseFromReader(Reader reader, int startingLineNr) throws IOException, XMLParseException {
        char ch;
        this.charReadTooMuch = '\u0000';
        this.reader = reader;
        this.parserLineNr = startingLineNr;
        while (true) {
            if ((ch = this.scanWhitespace()) != '<') {
                throw this.expectedInput("<");
            }
            ch = this.readChar();
            if (ch != '!' && ch != '?') break;
            this.skipSpecialTag(0);
        }
        this.unreadChar(ch);
        this.scanElement(this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void parse(InputStream is, int startingLineNr) throws IOException, XMLParseException {
        int c;
        BufferedInputStream in = new BufferedInputStream(is);
        int maxHeader = 100;
        in.mark(maxHeader);
        StringBuffer fistLine = new StringBuffer();
        while ((c = in.read()) != -1 && fistLine.length() < maxHeader) {
            fistLine.append((char)c);
            if (c != 62) continue;
        }
        in.reset();
        String encoding = null;
        Pattern pattern = Pattern.compile("(encoding=\")([\\p{Alnum}-]+)\"");
        Matcher matcher = pattern.matcher(fistLine.toString());
        if (matcher.find()) {
            encoding = matcher.group(2);
        }
        BufferedReader dis = encoding == null ? new BufferedReader(new InputStreamReader(in)) : new BufferedReader(new InputStreamReader((InputStream)in, encoding));
        try {
            this.parseFromReader(dis, 1);
        }
        finally {
            dis.close();
            in.close();
        }
    }

    public void parseString(String string) throws XMLParseException {
        try {
            this.parseFromReader(new StringReader(string), 1);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public void parseString(String string, int offset) throws XMLParseException {
        this.parseString(string.substring(offset));
    }

    public void parseString(String string, int offset, int end) throws XMLParseException {
        this.parseString(string.substring(offset, end));
    }

    public void parseString(String string, int offset, int end, int startingLineNr) throws XMLParseException {
        string = string.substring(offset, end);
        try {
            this.parseFromReader(new StringReader(string), startingLineNr);
        }
        catch (IOException e) {
            // empty catch block
        }
    }

    public void parseCharArray(char[] input, int offset, int end) throws XMLParseException {
        this.parseCharArray(input, offset, end, 1);
    }

    public void parseCharArray(char[] input, int offset, int end, int startingLineNr) throws XMLParseException {
        try {
            CharArrayReader reader = new CharArrayReader(input, offset, end);
            this.parseFromReader(reader, startingLineNr);
        }
        catch (IOException e) {
            // empty catch block
        }
    }

    public void removeChild(XMLElement child) {
        this.children.removeElement(child);
    }

    public void removeChildren() {
        this.children.removeAllElements();
    }

    public void removeAttribute(String name) {
        if (this.ignoreCase) {
            name = name.toUpperCase();
        }
        this.attributes.remove(name);
    }

    public void removeProperty(String name) {
        this.removeAttribute(name);
    }

    public void removeChild(String name) {
        this.removeAttribute(name);
    }

    protected XMLElement createAnotherElement() {
        return new XMLElement(this.entities, this.ignoreWhitespace, false, this.ignoreCase);
    }

    public void setContent(String content) {
        this.contents = content;
    }

    public void setTagName(String name) {
        this.setName(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(out);
            this.write(writer);
            writer.flush();
            return new String(out.toByteArray());
        }
        catch (IOException e) {
            return super.toString();
        }
    }

    public void write(Writer writer) throws IOException {
        this.writeIdented(writer, 0);
    }

    private void writeTabs(Writer writer, int level) throws IOException {
        for (int i = 0; i < level; ++i) {
            writer.write(9);
        }
    }

    private void writeIdented(Writer writer, int level) throws IOException {
        Enumeration en;
        if (this.name == null) {
            this.writeEncoded(writer, this.contents);
            return;
        }
        boolean ident = true;
        this.writeTabs(writer, level);
        writer.write(60);
        writer.write(this.name);
        if (!this.attributes.isEmpty()) {
            en = this.attributes.keys();
            while (en.hasMoreElements()) {
                writer.write(32);
                String key = (String)en.nextElement();
                String value = (String)this.attributes.get(key);
                writer.write(key);
                writer.write(61);
                writer.write(34);
                this.writeEncoded(writer, value);
                writer.write(34);
            }
        }
        if (this.contents != null && this.contents.length() > 0) {
            writer.write(62);
            this.writeEncoded(writer, this.contents);
            writer.write(60);
            writer.write(47);
            writer.write(this.name);
            writer.write(62);
            writer.write(10);
        } else if (this.children.isEmpty()) {
            writer.write(47);
            writer.write(62);
            writer.write(10);
        } else {
            writer.write(62);
            writer.write(10);
            en = this.enumerateChildren();
            while (en.hasMoreElements()) {
                XMLElement child = (XMLElement)en.nextElement();
                child.writeIdented(writer, level + 1);
            }
            this.writeTabs(writer, level);
            writer.write(60);
            writer.write(47);
            writer.write(this.name);
            writer.write(62);
            writer.write(10);
        }
    }

    protected void writeEncoded(Writer writer, String str) throws IOException {
        block7: for (int i = 0; i < str.length(); ++i) {
            char ch = str.charAt(i);
            switch (ch) {
                case '<': {
                    writer.write(38);
                    writer.write(108);
                    writer.write(116);
                    writer.write(59);
                    continue block7;
                }
                case '>': {
                    writer.write(38);
                    writer.write(103);
                    writer.write(116);
                    writer.write(59);
                    continue block7;
                }
                case '&': {
                    writer.write(38);
                    writer.write(97);
                    writer.write(109);
                    writer.write(112);
                    writer.write(59);
                    continue block7;
                }
                case '\"': {
                    writer.write(38);
                    writer.write(113);
                    writer.write(117);
                    writer.write(111);
                    writer.write(116);
                    writer.write(59);
                    continue block7;
                }
                case '\'': {
                    writer.write(38);
                    writer.write(97);
                    writer.write(112);
                    writer.write(111);
                    writer.write(115);
                    writer.write(59);
                    continue block7;
                }
                default: {
                    char unicode = ch;
                    if (unicode < ' ' || unicode > '~') {
                        writer.write(38);
                        writer.write(35);
                        writer.write(120);
                        writer.write(Integer.toString(unicode, 16));
                        writer.write(59);
                        continue block7;
                    }
                    writer.write(ch);
                }
            }
        }
    }

    protected void scanIdentifier(StringBuffer result) throws IOException {
        while (true) {
            char ch;
            if (!((ch = this.readChar()) >= 'A' && ch <= 'Z' || ch >= 'a' && ch <= 'z' || ch >= '0' && ch <= '9' || ch == '_' || ch == '.' || ch == ':' || ch == '-' || ch > '~')) {
                this.unreadChar(ch);
                return;
            }
            result.append(ch);
        }
    }

    protected char scanWhitespace() throws IOException {
        char ch;
        block3: while (true) {
            ch = this.readChar();
            switch (ch) {
                case '\t': 
                case '\n': 
                case '\r': 
                case ' ': {
                    continue block3;
                }
            }
            break;
        }
        return ch;
    }

    protected char scanWhitespace(StringBuffer result) throws IOException {
        char ch;
        block4: while (true) {
            ch = this.readChar();
            switch (ch) {
                case '\t': 
                case '\n': 
                case ' ': {
                    result.append(ch);
                }
                case '\r': {
                    continue block4;
                }
            }
            break;
        }
        return ch;
    }

    protected void scanString(StringBuffer string) throws IOException {
        char delimiter = this.readChar();
        if (delimiter != '\'' && delimiter != '\"') {
            throw this.expectedInput("' or \"");
        }
        char ch;
        while ((ch = this.readChar()) != delimiter) {
            if (ch == '&') {
                this.resolveEntity(string);
                continue;
            }
            string.append(ch);
        }
        return;
    }

    protected void scanPCData(StringBuffer data) throws IOException {
        while (true) {
            char ch;
            if ((ch = this.readChar()) == '<') {
                ch = this.readChar();
                if (ch == '!') {
                    this.checkCDATA(data);
                    continue;
                }
                this.unreadChar(ch);
                return;
            }
            if (ch == '&') {
                this.resolveEntity(data);
                continue;
            }
            data.append(ch);
        }
    }

    protected boolean checkCDATA(StringBuffer buf) throws IOException {
        char ch = this.readChar();
        if (ch != '[') {
            this.unreadChar(ch);
            this.skipSpecialTag(0);
            return false;
        }
        if (!this.checkLiteral("CDATA[")) {
            this.skipSpecialTag(1);
            return false;
        }
        int delimiterCharsSkipped = 0;
        block4: while (delimiterCharsSkipped < 3) {
            int i;
            ch = this.readChar();
            switch (ch) {
                case ']': {
                    if (delimiterCharsSkipped < 2) {
                        ++delimiterCharsSkipped;
                        continue block4;
                    }
                    buf.append(']');
                    buf.append(']');
                    delimiterCharsSkipped = 0;
                    continue block4;
                }
                case '>': {
                    if (delimiterCharsSkipped < 2) {
                        for (i = 0; i < delimiterCharsSkipped; ++i) {
                            buf.append(']');
                        }
                        delimiterCharsSkipped = 0;
                        buf.append('>');
                        continue block4;
                    }
                    delimiterCharsSkipped = 3;
                    continue block4;
                }
            }
            for (i = 0; i < delimiterCharsSkipped; ++i) {
                buf.append(']');
            }
            buf.append(ch);
            delimiterCharsSkipped = 0;
        }
        return true;
    }

    protected void skipComment() throws IOException {
        int dashesToRead = 2;
        while (dashesToRead > 0) {
            char ch = this.readChar();
            if (ch == '-') {
                --dashesToRead;
                continue;
            }
            dashesToRead = 2;
        }
        if (this.readChar() != '>') {
            throw this.expectedInput(">");
        }
    }

    protected void skipSpecialTag(int bracketLevel) throws IOException {
        char ch;
        int tagLevel = 1;
        char stringDelimiter = '\u0000';
        if (bracketLevel == 0) {
            ch = this.readChar();
            if (ch == '[') {
                ++bracketLevel;
            } else if (ch == '-') {
                ch = this.readChar();
                if (ch == '[') {
                    ++bracketLevel;
                } else if (ch == ']') {
                    --bracketLevel;
                } else if (ch == '-') {
                    this.skipComment();
                    return;
                }
            }
        }
        while (tagLevel > 0) {
            ch = this.readChar();
            if (stringDelimiter == '\u0000') {
                if (ch == '\"' || ch == '\'') {
                    stringDelimiter = ch;
                } else if (bracketLevel <= 0) {
                    if (ch == '<') {
                        ++tagLevel;
                    } else if (ch == '>') {
                        --tagLevel;
                    }
                }
                if (ch == '[') {
                    ++bracketLevel;
                    continue;
                }
                if (ch != ']') continue;
                --bracketLevel;
                continue;
            }
            if (ch != stringDelimiter) continue;
            stringDelimiter = '\u0000';
        }
    }

    protected boolean checkLiteral(String literal) throws IOException {
        int length = literal.length();
        for (int i = 0; i < length; ++i) {
            if (this.readChar() == literal.charAt(i)) continue;
            return false;
        }
        return true;
    }

    protected char readChar() throws IOException {
        if (this.charReadTooMuch != '\u0000') {
            char ch = this.charReadTooMuch;
            this.charReadTooMuch = '\u0000';
            return ch;
        }
        int i = this.reader.read();
        if (i < 0) {
            throw this.unexpectedEndOfData();
        }
        if (i == 10) {
            ++this.parserLineNr;
            return '\n';
        }
        return (char)i;
    }

    protected void scanElement(XMLElement elt) throws IOException {
        char ch;
        String name;
        StringBuffer buf;
        block22: {
            buf = new StringBuffer();
            this.scanIdentifier(buf);
            name = buf.toString();
            elt.setName(name);
            ch = this.scanWhitespace();
            while (ch != '>' && ch != '/') {
                buf.setLength(0);
                this.unreadChar(ch);
                this.scanIdentifier(buf);
                String key = buf.toString();
                ch = this.scanWhitespace();
                if (ch != '=') {
                    throw this.expectedInput("=");
                }
                this.unreadChar(this.scanWhitespace());
                buf.setLength(0);
                this.scanString(buf);
                elt.setAttribute(key, buf);
                ch = this.scanWhitespace();
            }
            if (ch == '/') {
                ch = this.readChar();
                if (ch != '>') {
                    throw this.expectedInput(">");
                }
                return;
            }
            buf.setLength(0);
            ch = this.scanWhitespace(buf);
            if (ch != '<') {
                this.unreadChar(ch);
                this.scanPCData(buf);
            } else {
                while ((ch = this.readChar()) == '!') {
                    if (this.checkCDATA(buf)) {
                        this.scanPCData(buf);
                    } else {
                        ch = this.scanWhitespace(buf);
                        if (ch == '<') continue;
                        this.unreadChar(ch);
                        this.scanPCData(buf);
                    }
                    break block22;
                }
                buf.setLength(0);
            }
        }
        if (buf.length() == 0) {
            while (ch != '/') {
                if (ch == '!') {
                    ch = this.readChar();
                    if (ch != '-') {
                        throw this.expectedInput("Comment or Element");
                    }
                    ch = this.readChar();
                    if (ch != '-') {
                        throw this.expectedInput("Comment or Element");
                    }
                    this.skipComment();
                } else {
                    this.unreadChar(ch);
                    XMLElement child = this.createAnotherElement();
                    this.scanElement(child);
                    elt.addChild(child);
                }
                ch = this.scanWhitespace();
                if (ch != '<') {
                    throw this.expectedInput("<");
                }
                ch = this.readChar();
            }
            this.unreadChar(ch);
        } else if (this.ignoreWhitespace) {
            elt.setContent(buf.toString().trim());
        } else {
            elt.setContent(buf.toString());
        }
        ch = this.readChar();
        if (ch != '/') {
            throw this.expectedInput("/");
        }
        this.unreadChar(this.scanWhitespace());
        if (!this.checkLiteral(name)) {
            throw this.expectedInput(name);
        }
        if (this.scanWhitespace() != '>') {
            throw this.expectedInput(">");
        }
    }

    protected void resolveEntity(StringBuffer buf) throws IOException {
        char ch = '\u0000';
        StringBuffer keyBuf = new StringBuffer();
        while ((ch = this.readChar()) != ';') {
            keyBuf.append(ch);
        }
        String key = keyBuf.toString();
        if (key.charAt(0) == '#') {
            try {
                ch = key.charAt(1) == 'x' ? (char)Integer.parseInt(key.substring(2), 16) : (char)Integer.parseInt(key.substring(1), 10);
            }
            catch (NumberFormatException e) {
                throw this.unknownEntity(key);
            }
            buf.append(ch);
        } else {
            char[] value = (char[])this.entities.get(key);
            if (value == null) {
                throw this.unknownEntity(key);
            }
            buf.append(value);
        }
    }

    protected void unreadChar(char ch) {
        this.charReadTooMuch = ch;
    }

    protected XMLParseException invalidValueSet(String name) {
        String msg = "Invalid value set (entity name = \"" + name + "\")";
        return new XMLParseException(this.getName(), this.parserLineNr, msg);
    }

    protected XMLParseException invalidValue(String name, String value) {
        String msg = "Attribute \"" + name + "\" does not contain a valid " + "value (\"" + value + "\")";
        return new XMLParseException(this.getName(), this.parserLineNr, msg);
    }

    protected XMLParseException unexpectedEndOfData() {
        String msg = "Unexpected end of data reached";
        return new XMLParseException(this.getName(), this.parserLineNr, msg);
    }

    protected XMLParseException syntaxError(String context) {
        String msg = "Syntax error while parsing " + context;
        return new XMLParseException(this.getName(), this.parserLineNr, msg);
    }

    protected XMLParseException expectedInput(String charSet) {
        String msg = "Expected: " + charSet;
        return new XMLParseException(this.getName(), this.parserLineNr, msg);
    }

    protected XMLParseException unknownEntity(String name) {
        String msg = "Unknown or invalid entity: &" + name + ";";
        return new XMLParseException(this.getName(), this.parserLineNr, msg);
    }
}

