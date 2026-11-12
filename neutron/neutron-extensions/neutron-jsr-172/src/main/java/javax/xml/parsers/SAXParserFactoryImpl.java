package javax.xml.parsers;

import org.xml.sax.SAXException;

class SAXParserFactoryImpl extends SAXParserFactory {
    @Override
    public SAXParser newSAXParser() throws ParserConfigurationException, SAXException {
        return new SAXParserImpl(isNamespaceAware(), isValidating());
    }
}

