/*******************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved. 
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.web.call;

import java.util.Map;
import java.util.HashMap;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import com.thoughtworks.xstream.XStream;

/**
 * @author Mauro Talevi
 * @author Konstantin Pribluda
 */
public final class PicoCallTestCase {

    XStream xstream = new XStream();
    {
        xstream.alias("dirs", PicoCallServlet.Directories.class);
        xstream.alias("methods", PicoCallServlet.WebMethods.class);

    }

    @Test
    public void testPaths() throws Exception {
        Map map = new HashMap();
        PicoCallServlet.directorize(map, "foo/bar/baz1");
        PicoCallServlet.directorize(map, "foo/bar/baz2");
        assertEquals(
                "<map>\n" +
                        "  <entry>\n" +
                        "    <string>foo</string>\n" +
                        "    <dirs serialization=\"custom\">\n" +
                        "      <unserializable-parents/>\n" +
                        "      <set>\n" +
                        "        <default/>\n" +
                        "        <int>16</int>\n" +
                        "        <float>0.75</float>\n" +
                        "        <int>1</int>\n" +
                        "        <string>bar</string>\n" +
                        "      </set>\n" +
                        "    </dirs>\n" +
                        "  </entry>\n" +
                        "  <entry>\n" +
                        "    <string>foo/bar</string>\n" +
                        "    <dirs serialization=\"custom\">\n" +
                        "      <unserializable-parents/>\n" +
                        "      <set>\n" +
                        "        <default/>\n" +
                        "        <int>16</int>\n" +
                        "        <float>0.75</float>\n" +
                        "        <int>2</int>\n" +
                        "        <string>baz2</string>\n" +
                        "        <string>baz1</string>\n" +
                        "      </set>\n" +
                        "    </dirs>\n" +
                        "  </entry>\n" +
                        "</map>", xstream.toXML(map));


    }

    @Test
    public void testClasses() throws Exception {
        Map map = new HashMap();
        PicoCallServlet.directorize(map, "foo/bar/baz1", Foo.class);
        assertEquals(
                "<map>\n" +
                        "  <entry>\n" +
                        "    <string>foo/bar/baz1</string>\n" +
                        "    <methods serialization=\"custom\">\n" +
                        "      <unserializable-parents/>\n" +
                        "      <map>\n" +
                        "        <default>\n" +
                        "          <loadFactor>0.75</loadFactor>\n" +
                        "          <threshold>12</threshold>\n" +
                        "        </default>\n" +
                        "        <int>16</int>\n" +
                        "        <int>1</int>\n" +
                        "        <string>hello</string>\n" +
                        "        <method>\n" +
                        "          <class>org.picocontainer.web.call.PicoCallTestCase$Foo</class>\n" +
                        "          <name>hello</name>\n" +
                        "          <parameter-types>\n" +
                        "            <class>long</class>\n" +
                        "          </parameter-types>\n" +
                        "        </method>\n" +
                        "      </map>\n" +
                        "      <methods>\n" +
                        "        <default>\n" +
                        "          <comp>org.picocontainer.web.call.PicoCallTestCase$Foo</comp>\n" +
                        "        </default>\n" +
                        "      </methods>\n" +
                        "    </methods>\n" +
                        "  </entry>\n" +
                        "</map>", xstream.toXML(map));

    }

    public static class Foo {
        public int hello(long l) {
            return 0;
        }
    }

}
