package org.nanocontainer.nanowar.nanoweb;

import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * @author Aslak Helles&oslash;y
 * @author Kouhei Mori
 * @version $Revision$
 */
public class CachingScriptClassLoaderTestCase extends TestCase {
    public void testClassesAreCachedWhenTheUrlTimeStampIsNotMoreRecent() throws IOException, ScriptException {
        CachingScriptClassLoader loader = new CachingScriptClassLoader();

        MockURLConnection urlConnection = new MockURLConnection();
        MockURLStreamHandler mockUrlStreamHandler = new MockURLStreamHandler(urlConnection);

        URL classUrl = new URL("test", "test", -1, "test", mockUrlStreamHandler);
        Class clazz1 = loader.getClass(classUrl);
        Class clazz2 = loader.getClass(classUrl);
        assertSame(clazz1, clazz2);
    }

    public void testClassesAreReloadedWhenTheUrlTimeStampIsMoreRecent() throws IOException, ScriptException {
        CachingScriptClassLoader loader = new CachingScriptClassLoader();

        MockURLConnection urlConnection = new MockURLConnection();
        MockURLStreamHandler urlStreamHandler = new MockURLStreamHandler(urlConnection);

        URL classUrl = new URL("test", "test", -1, "test", urlStreamHandler);
        urlConnection.setLastModified(1);
        Class clazz1 = loader.getClass(classUrl);
        urlConnection.setLastModified(2);
        Class clazz2 = loader.getClass(classUrl);
        assertNotSame(clazz1, clazz2);
    }

    private static class MockURLConnection extends URLConnection {
        private final String GROOVY_CLASS = "class GroovyClass{}";
        private long lastModified;

        public MockURLConnection() {
            super(null);
        }

        public void connect() throws IOException {
        }

        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(GROOVY_CLASS.getBytes());
        }

        public void setLastModified(long lastModified) {
            this.lastModified = lastModified;
        }

        public long getLastModified() {
            return lastModified;
        }
    }

    private static class MockURLStreamHandler extends URLStreamHandler {
        private final MockURLConnection urlConnection;

        public MockURLStreamHandler(MockURLConnection urlConnection) {
            this.urlConnection = urlConnection;
        }

        protected URLConnection openConnection(URL u) throws IOException {
            return urlConnection;
        }
    }
}