package javax.xml.parsers;

import java.io.InputStream;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class SAXParserImpl extends SAXParser {
    private final boolean namespaceAware;
    private final boolean validating;

    SAXParserImpl(boolean namespaceAware, boolean validating) {
        this.namespaceAware = namespaceAware;
        this.validating = validating;
    }

    @Override
    public boolean isNamespaceAware() {
        return namespaceAware;
    }

    @Override
    public boolean isValidating() {
        return validating;
    }

    @Override
    public void parse(InputStream is, DefaultHandler dh) throws SAXException, IOException {
        try {
            javax.xml.parsers.SAXParserFactory factory = javax.xml.parsers.SAXParserFactory.newInstance();
            factory.setNamespaceAware(namespaceAware);
            factory.setValidating(validating);
            factory.newSAXParser().parse(is, dh);
        } catch (ParserConfigurationException e) {
            throw new SAXException(e.getMessage());
        }
    }
}

