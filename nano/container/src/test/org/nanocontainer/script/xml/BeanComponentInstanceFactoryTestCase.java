/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.script.xml;

import junit.framework.TestCase;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

/**
 * @author Paul Hammant
 * @author Marcos Tarruella
 */
public class BeanComponentInstanceFactoryTestCase extends TestCase {

    public void testDeserialization() throws ParserConfigurationException, IOException, SAXException {
        BeanComponentInstanceFactory factory = new BeanComponentInstanceFactory();

        StringReader sr = new StringReader("" +
                "<org.nanocontainer.script.xml.TestBean>" +
                "<foo>10</foo>" +
                "<bar>hello</bar>" +
                "</org.nanocontainer.script.xml.TestBean>");
        InputSource is = new InputSource(sr);
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse(is);

        Object o = factory.makeInstance(null, doc.getDocumentElement(), Thread.currentThread().getContextClassLoader());
        TestBean bean = (TestBean) o;
        assertEquals("hello", bean.getBar());
        assertEquals(10, bean.getFoo());
    }

    public void testDeserializationWithMappedName() throws ParserConfigurationException, IOException, SAXException {
        BeanComponentInstanceFactory factory = new BeanComponentInstanceFactory();

        StringReader sr = new StringReader("" +
                "<org.nanocontainer.script.xml.TestBean>" +
                "<any name='foo'>10</any>" +
                "<bar>hello</bar>" +
                "</org.nanocontainer.script.xml.TestBean>");
        InputSource is = new InputSource(sr);
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse(is);

        Object o = factory.makeInstance(null, doc.getDocumentElement(), Thread.currentThread().getContextClassLoader());
        TestBean bean = (TestBean) o;
        assertEquals("hello", bean.getBar());
        assertEquals(10, bean.getFoo());
    }
}
