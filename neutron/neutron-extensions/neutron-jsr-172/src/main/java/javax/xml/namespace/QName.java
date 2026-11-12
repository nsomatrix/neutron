package javax.xml.namespace;

import java.io.Serializable;

public class QName implements Serializable {
    private final String namespaceURI;
    private final String localPart;
    private final String prefix;

    public QName(String namespaceURI, String localPart) {
        this(namespaceURI, localPart, "");
    }

    public QName(String namespaceURI, String localPart, String prefix) {
        this.namespaceURI = (namespaceURI == null) ? "" : namespaceURI;
        this.localPart = (localPart == null) ? "" : localPart;
        this.prefix = (prefix == null) ? "" : prefix;
    }

    public QName(String localPart) {
        this("", localPart, "");
    }

    public String getNamespaceURI() {
        return namespaceURI;
    }

    public String getLocalPart() {
        return localPart;
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof QName)) return false;
        QName q = (QName) obj;
        return namespaceURI.equals(q.namespaceURI) && localPart.equals(q.localPart);
    }

    @Override
    public int hashCode() {
        return namespaceURI.hashCode() ^ localPart.hashCode();
    }

    @Override
    public String toString() {
        return namespaceURI.isEmpty() ? localPart : "{" + namespaceURI + "}" + localPart;
    }
}

