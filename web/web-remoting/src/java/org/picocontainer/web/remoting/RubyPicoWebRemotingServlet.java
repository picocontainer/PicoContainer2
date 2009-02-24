/*******************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.web.remoting;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Arrays;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.picocontainer.paranamer.BytecodeReadingParanamer;
import org.picocontainer.paranamer.CachingParanamer;
import org.picocontainer.paranamer.DefaultParanamer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.WriterWrapper;

/**
 * Servlet that uses Ruby as the form of the reply.
 *
 * @author Jean Lazarou
 */
@SuppressWarnings("serial")
public class RubyPicoWebRemotingServlet extends AbstractPicoWebRemotingServlet  {

    private CachingParanamer paranamer = new CachingParanamer(new DefaultParanamer());
    private CachingParanamer paranamer2 = new CachingParanamer(new BytecodeReadingParanamer());

    @Override
	protected XStream createXStream() {
		return new XStream(makeRubyDriver());
	}

    public static HierarchicalStreamDriver makeRubyDriver() {
        HierarchicalStreamDriver driver = new HierarchicalStreamDriver() {
            public HierarchicalStreamReader createReader(Reader reader) {
                throw new UnsupportedOperationException();
            }

            public HierarchicalStreamReader createReader(InputStream inputStream) {
                throw new UnsupportedOperationException();
            }

            public HierarchicalStreamWriter createWriter(Writer out) {
                HierarchicalStreamWriter jsonWriter = new RubyWriter(out);
                return new WriterWrapper(jsonWriter) {
                    public void startNode(String name) {
                        startNode(name, null);
                    }

                    @SuppressWarnings("unchecked")
					public void startNode(String name, Class clazz) {
                        ((RubyWriter) wrapped).startNode(name.replace('-', '_'), clazz);
                    }
                };
            }

            public HierarchicalStreamWriter createWriter(OutputStream outputStream) {
                throw new UnsupportedOperationException();
            }
        };
        return driver;
    }

    protected void respond(HttpServletRequest req, HttpServletResponse resp, String pathInfo) throws IOException {
        if ("/classdefs".equals(pathInfo)) {

            final String function = req.getParameter("fn") != null ? req.getParameter("fn") : "@connection.submit";

            String classList = req.getQueryString();
            if (classList.contains("&")) {
                classList = classList.substring(0, classList.indexOf("&"));
            }
            final String[] classes = classList.split(",");

            resp.setContentType("text/plain");
            final ServletOutputStream outputStream = resp.getOutputStream();
            MethodVisitor mapv = new MethodVisitor() {

                public void method(String methodName, Method method) throws IOException {
                    outputStream.print("\n\n  def " + methodName);
                    String[] paramNames;
                    if (paranamer.areParameterNamesAvailable(method.getDeclaringClass(), method.getName()) ==0) {
                        paramNames = paranamer.lookupParameterNames(method);
                    } else {
                        paramNames = paranamer2.lookupParameterNames(method);
                    }
                    for (int i = 0; i < paramNames.length; i++) {
                        String name = paramNames[i];
                        outputStream.print((i > 0 ? ", " : " ") + name);
                    }
                    outputStream.print("\n");
                    outputStream.print("    "+function+"?(self.class, '" + methodName + "'");
                    for (String name : paramNames) {
                        outputStream.print(", :" + name + " => " + name);
                    }
                    outputStream.println(")\n  end");
                }

                public void superClass(String superClass) throws IOException {
                    if (Arrays.binarySearch(classes, superClass) > -1) {
                        outputStream.print(" < " + superClass);
                    }
                }
            };

            for (String clazz : classes) {
                outputStream.print("class " + clazz);
                super.visitClass(clazz, mapv);
                outputStream.println("\nend");
            }


        } else {
            super.respond(req, resp, pathInfo);
        }
    }

}
