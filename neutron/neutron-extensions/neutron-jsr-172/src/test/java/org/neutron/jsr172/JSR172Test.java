package org.neutron.jsr172;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.namespace.QName;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;

public class JSR172Test {
    @Test
    public void testQName() {
        QName q = new QName("http://example.com", "element", "pre");
        Assert.assertEquals("element", q.getLocalPart());
        Assert.assertEquals("http://example.com", q.getNamespaceURI());
        Assert.assertEquals("pre", q.getPrefix());
    }

    @Test
    public void testSAXParser() throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        Assert.assertNotNull(parser);

        String xml = "<root><item>Hello</item></root>";
        final boolean[] parsed = new boolean[1];
        parser.parse(new ByteArrayInputStream(xml.getBytes()), new DefaultHandler() {
            @Override
            public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes attributes) {
                if ("item".equals(qName)) {
                    parsed[0] = true;
                }
            }
        });
        Assert.assertTrue(parsed[0]);
    }
}

