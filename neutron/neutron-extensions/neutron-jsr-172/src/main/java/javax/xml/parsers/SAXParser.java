package javax.xml.parsers;

import java.io.InputStream;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public abstract class SAXParser {
    protected SAXParser() {}
    public abstract boolean isNamespaceAware();
    public abstract boolean isValidating();
    public abstract void parse(InputStream is, DefaultHandler dh) throws SAXException, IOException;
}

