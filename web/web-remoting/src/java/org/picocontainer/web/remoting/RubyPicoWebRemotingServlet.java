/*******************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.web.remoting;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.io.IOException;
import java.util.Arrays;
import java.lang.reflect.Method;

import org.picocontainer.web.remoting.RubyWriter;
import org.picocontainer.paranamer.CachingParanamer;
import org.picocontainer.paranamer.DefaultParanamer;
import org.picocontainer.paranamer.BytecodeReadingParanamer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.WriterWrapper;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * All for the calling of methods in a tree of components manages by PicoContainer.
 * Ruby is the form of the reply, the request is plainly mapped from Query Strings
 * and form fields to the method signature.
 *
 * @author Jean Lazarou
 */
@SuppressWarnings("serial")
public class RubyPicoWebRemotingServlet extends AbstractPicoWebRemotingServlet  {

    CachingParanamer paranamer = new CachingParanamer(new DefaultParanamer());
    CachingParanamer paranamer2 = new CachingParanamer(new BytecodeReadingParanamer());

    public void init(ServletConfig servletConfig) throws ServletException {
        setXStream(new XStream(makeRubyDriver()));
        super.init(servletConfig);
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
            MethodAndParamVisitor mapv = new MethodAndParamVisitor() {

                public void method(String methodName, Method method) throws IOException {
                    outputStream.print("\n  def " + methodName);
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
                        outputStream.print(", :" + name + "=>" + name);
                    }
                    outputStream.print(")\n  end\n");
                }

                public void superClass(String superClass) throws IOException {
                    if (Arrays.binarySearch(classes, superClass) > -1) {
                        outputStream.print(" < " + superClass);
                    }
                }
            };

            for (String clazz : classes) {
                outputStream.println("class " + clazz);
                super.visitClass(clazz, mapv);
                outputStream.println("end");
            }


        } else {
            super.respond(req, resp, pathInfo);
        }
    }

}
