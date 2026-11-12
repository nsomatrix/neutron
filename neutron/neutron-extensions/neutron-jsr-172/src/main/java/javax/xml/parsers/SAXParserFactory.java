package javax.xml.parsers;

public abstract class SAXParserFactory {
    private boolean namespaceAware = false;
    private boolean validating = false;

    protected SAXParserFactory() {}

    public static SAXParserFactory newInstance() {
        return new SAXParserFactoryImpl();
    }

    public abstract SAXParser newSAXParser() throws ParserConfigurationException, org.xml.sax.SAXException;

    public void setNamespaceAware(boolean awareness) {
        this.namespaceAware = awareness;
    }

    public void setValidating(boolean validating) {
        this.validating = validating;
    }

    public boolean isNamespaceAware() {
        return namespaceAware;
    }

    public boolean isValidating() {
        return validating;
    }
}

