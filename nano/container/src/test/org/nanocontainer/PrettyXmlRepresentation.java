package org.nanocontainer;

import org.picocontainer.MutablePicoContainer;

import java.util.HashSet;
import java.util.ArrayList;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.io.IOException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;

public class PrettyXmlRepresentation {

    private XStream xs;

    public PrettyXmlRepresentation() {
        xs = new XStream();
        xs.registerConverter(new Converter() {
            public boolean canConvert(Class aClass) {
                return aClass.getName().equals("org.picocontainer.DefaultPicoContainer$1") ||
                       aClass.getName().equals("java.util.Properties") ||
                       aClass == Boolean.class ||
                       aClass == HashSet.class ||
                       aClass == ArrayList.class;
            }

            public void marshal(Object o, HierarchicalStreamWriter hierarchicalStreamWriter, MarshallingContext marshallingContext) {
            }

            public Object unmarshal(HierarchicalStreamReader hierarchicalStreamReader, UnmarshallingContext unmarshallingContext) {
                return null;
            }
        });
        xs.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);


    }

    public String simplifyRepresentation(MutablePicoContainer mpc) throws IOException {
        String bar = xs.toXML(mpc);
        LineNumberReader lnr = new LineNumberReader(new StringReader(bar));
        String line = lnr.readLine();
        String foo = "";
        while (line != null) {
            int clo = line.indexOf("</");
            if (clo == -1 || !line.substring(0, clo).trim().equals("")) {
                int l = line.indexOf("<");
                int r = line.lastIndexOf("/>");
                int s = -1;
                if (l != -1) {
                    s = line.indexOf(" ",l);
                }
                if (((l < s && s < r) || l == -1 || r == -1)
                    && line.indexOf("DefaultPicoContainer$OrderedComponentAdapterLifecycleManager") == -1
                    && line.indexOf("DefaultPicoContainer$DefaultComponentStore") == -1) {
                    String s1 = line.trim();
                    if (s1.startsWith("<string>") && line.trim().endsWith("</string>")) {
                        line = line.substring(0, line.indexOf("<string>")) + "string:" + line.substring(line.indexOf("<string>")+"<string>".length(), line.lastIndexOf("</string>"));
                    }
                    foo += line + "\n";
                }
            }
            line = lnr.readLine();
        }

        foo = foo.replaceAll("/>","");
        foo = foo.replaceAll("</","");
        foo = foo.replaceAll("<","");
        foo = foo.replaceAll(">","");
        foo = foo.replaceAll(" class=","=");
        foo = foo.replaceAll("setterMethodPrefix","");
        foo = foo.replaceAll("injectionAnnotation","");
        foo = foo.replaceAll("\"","");

        return foo;
    }



}
