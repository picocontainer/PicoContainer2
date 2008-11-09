/*******************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved. 
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.web.remoting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.thoughtworks.xstream.XStream;

/**
 * @author Paul Hammant
 */
public final class PicoWebRemotingServletTestCase {

    XStream xstream = new XStream();
    {
        xstream.alias("dirs", PicoWebRemotingServlet.Directories.class);
        xstream.alias("methods", PicoWebRemotingServlet.WebMethods.class);

    }

    @Test
    public void testPaths() throws Exception {
        Map map = new HashMap();
        PicoWebRemotingServlet.directorize(map, "foo/bar/baz1");
        PicoWebRemotingServlet.directorize(map, "foo/bar/baz2");
        assertEquals(3, map.size());
        assertTrue(map.get("foo") instanceof PicoWebRemotingServlet.Directories);

        PicoWebRemotingServlet.Directories dirs = (PicoWebRemotingServlet.Directories) map.get("foo");
        assertEquals(1, dirs.size());
        assertEquals("bar", dirs.toArray()[0]);

        dirs = (PicoWebRemotingServlet.Directories) map.get("foo/bar");
        List<String> list = listOf(dirs);
        assertEquals(2, list.size());
        assertEquals("baz1", list.get(0));
        assertEquals("baz2", list.get(1));

        dirs = (PicoWebRemotingServlet.Directories) map.get("");
        assertEquals(1, dirs.size());
        assertEquals("foo", dirs.toArray()[0]);
    }

	private List<String> listOf(PicoWebRemotingServlet.Directories dirs) {
		List<String> list = new ArrayList<String>(dirs);
		Collections.sort(list);
		return list;
	}

    @Test
    public void testClasses() throws Exception {
        Map map = new HashMap();
        PicoWebRemotingServlet.directorize(map, "foo/bar/baz1", Foo.class);
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
                        "          <class>org.picocontainer.web.remoting.PicoWebRemotingServletTestCase$Foo</class>\n" +
                        "          <name>hello</name>\n" +
                        "          <parameter-types>\n" +
                        "            <class>long</class>\n" +
                        "          </parameter-types>\n" +
                        "        </method>\n" +
                        "      </map>\n" +
                        "      <methods>\n" +
                        "        <default>\n" +
                        "          <comp>org.picocontainer.web.remoting.PicoWebRemotingServletTestCase$Foo</comp>\n" +
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
