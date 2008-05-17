/*
 * Copyright (C) PicoContainer Organization. All rights reserved.
 * --------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.picocontainer.logging.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.picocontainer.logging.Logger;
import org.picocontainer.logging.store.stores.JdkLoggerStore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Mauro Talevi
 * @author Peter Donald
 */
public abstract class AbstractTest {

    protected static final String MESSAGE = "Testing Logger";
    protected static final String MESSAGE2 = "This occurs in sub-category";

    private File logsDir;

    @Before
    public void setUp() throws Exception {
        this.logsDir = new File("logs/");
        this.logsDir.mkdirs();
    }

    protected final InputStream getResource(final String name) {
        return getClass().getResourceAsStream(name);
    }

    protected void runLoggerTest(final String filename, final LoggerStore store, final int level) throws IOException {
        runLoggerTest(filename, store);
    }

    protected void runLoggerTest(final String filename, final LoggerStore store) throws IOException {
        BufferedReader reader = null;
        try {
            final Logger logger = store.getLogger();
            assertNotNull("rootLogger for " + filename, logger);
            logger.info(MESSAGE);
            final Logger noExistLogger = store.getLogger("no-exist");
            assertNotNull("noExistLogger for " + filename, noExistLogger);
            noExistLogger.info(MESSAGE2);

            assertEquals("Same Logger returned multiple times:", noExistLogger, store.getLogger("no-exist"));

            try {
                store.getLogger(null);
                fail("Expected a NullPointerException when passing " + "null in for getLogger parameter");
            } catch (final NullPointerException npe) {
                assertEquals("NullPointer message", "name", npe.getMessage());
            }

            final File logFile = new File(this.logsDir, filename + ".log");
            assertTrue("Checking LogFile Exists: " + filename, logFile.exists());

            reader = new BufferedReader(new InputStreamReader(new FileInputStream(logFile)));
            assertEquals("First line Contents for logger" + filename, MESSAGE, reader.readLine());
            assertEquals("Second line Contents for logger" + filename, MESSAGE2, reader.readLine());
            assertNull("Third Line Contents for logger" + filename, reader.readLine());
            reader.close();
            logFile.delete();

            if (!(store instanceof JdkLoggerStore)) {
                final Logger nejney = store.getLogger("nejney");
                nejney.info(MESSAGE);

                final File logFile2 = new File(this.logsDir, filename + "2.log");
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(logFile2)));
                assertEquals("First line Contents for nejney logger" + filename, MESSAGE, reader.readLine());
                assertNull("Second Line Contents for nejney logger" + filename, reader.readLine());
                reader.close();
                logFile2.delete();
            }
        } finally {
            store.close();
            if (null != reader) {
                reader.close();
            }
        }
    }

    /**
     * Builds an Element from a resource
     * 
     * @param resource the InputStream of the configuration resource
     * @param resolver the EntityResolver required by the DocumentBuilder - or
     *            <code>null</code> if none required
     * @param systemId the String encoding the systemId required by the
     *            InputSource - or <code>null</code> if none required
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    protected static Element buildElement(final InputStream resource, final EntityResolver resolver,
            final String systemId) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        dbf.setValidating(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        if (resolver != null) {
            db.setEntityResolver(resolver);
        }
        InputSource source = new InputSource(resource);
        if (systemId != null) {
            source.setSystemId(systemId);
        }
        Document doc = db.parse(source);
        return doc.getDocumentElement();
    }

    protected void performConsoleTest(final LoggerStore store, final int level) {
        final Logger logger = store.getLogger();
        assertNotNull("rootLogger for console", logger);
        logger.info(MESSAGE);
        final Logger noExistLogger = store.getLogger("no-exist");
        assertNotNull("noExistLogger for console", noExistLogger);
        noExistLogger.info(MESSAGE2);
        store.close();
    }

    protected void runInvalidInputData(final LoggerStoreFactory factory) {
        try {
            factory.createLoggerStore(new HashMap<String, Object>());
            fail("Expected createLoggerStore to fail with invalid input data");
        } catch (Exception e) {
        }
    }

    protected void runStreamBasedFactoryTest(final String inputFile, final LoggerStoreFactory factory, final int level,
            final String outputFile, final HashMap<String, Object> inputData) throws IOException {
        // URL Should in file: format
        final URL url = getClass().getResource(inputFile);
        assertEquals("URL is of file type", url.getProtocol(), "file");

        final HashMap<String, Object> config = new HashMap<String, Object>();
        config.put(LoggerStoreFactory.URL_LOCATION, url.toExternalForm());
        config.putAll(inputData);
        runFactoryTest(factory, level, config, outputFile);
        final HashMap<String, Object> config2 = new HashMap<String, Object>();
        config2.put(URL.class.getName(), url);
        config2.putAll(inputData);
        runFactoryTest(factory, level, config2, outputFile);
        final String filename = url.toExternalForm().substring(5);
        final HashMap<String, Object> config3 = new HashMap<String, Object>();
        config3.put(LoggerStoreFactory.FILE_LOCATION, filename);
        config3.putAll(inputData);
        runFactoryTest(factory, level, config3, outputFile);
        final HashMap<String, Object> config4 = new HashMap<String, Object>();
        config4.put(File.class.getName(), new File(filename));
        config4.putAll(inputData);
        runFactoryTest(factory, level, config4, outputFile);
        final HashMap<String, Object> config5 = new HashMap<String, Object>();
        config5.put(InputStream.class.getName(), new FileInputStream(filename));
        config5.putAll(inputData);
        runFactoryTest(factory, level, config5, outputFile);
    }

    protected void runFactoryTest(final LoggerStoreFactory factory, final int level,
            final HashMap<String, Object> config, final String filename) throws IOException {
        final LoggerStore store = factory.createLoggerStore(config);
        runLoggerTest(filename, store, level);
    }
}
