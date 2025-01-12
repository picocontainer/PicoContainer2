/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.script;

import java.io.*;
import java.net.URL;

import org.picocontainer.PicoContainer;

/**
 * Abstract class for script-based container builders
 *
 * @author Aslak Helles&oslash;y
 * @author Obie Fernandez
 * @author Mauro Talevi
 */
public abstract class ScriptedContainerBuilder extends AbstractContainerBuilder {
    
    private final URL scriptURL;
    private final ClassLoader classLoader;
    
    public ScriptedContainerBuilder(Reader script, ClassLoader classLoader) {
    	this(script,classLoader, LifecycleMode.AUTO_LIFECYCLE);
    }

    abstract protected String naturalFileSuffix();

    public ScriptedContainerBuilder(Reader script, ClassLoader classLoader, LifecycleMode lifecycleMode) {
        super(lifecycleMode);
        if (script == null) {
            throw new NullPointerException("script");
        }
        try {
            File tempFile = File.createTempFile("picocontainer", naturalFileSuffix());
            //tempFile.deleteOnExit();
            FileWriter writer = new FileWriter(tempFile);
            char[] buffer = new char[1024];
            int numCharsRead;
            while ((numCharsRead = script.read(buffer)) != -1) {
                writer.write(buffer, 0, numCharsRead);
            }
            this.scriptURL = tempFile.toURI().toURL();
            System.out.println("SCRIPT FILE=" + this.scriptURL);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temporary script file", e);
        }
        this.classLoader = classLoader;
        if ( classLoader == null) {
            throw new NullPointerException("classLoader");
        }
    }
    
    public ScriptedContainerBuilder(URL script, ClassLoader classLoader)  {
    	this(script,classLoader, LifecycleMode.AUTO_LIFECYCLE);
    }

    public ScriptedContainerBuilder(URL script, ClassLoader classLoader, LifecycleMode lifecycleMode) {
        super(lifecycleMode);
        this.scriptURL = script;
        if (script == null) {
            throw new NullPointerException("script");
        }
        this.classLoader = classLoader;
        if ( classLoader == null) {
            throw new NullPointerException("classLoader");
        }
    }

    @Override
    protected final PicoContainer createContainer(PicoContainer parentContainer, Object assemblyScope) {
        return createContainerFromScript(parentContainer, assemblyScope);
    }

    protected final ClassLoader getClassLoader() {
        return classLoader;
    }
    
//    @SuppressWarnings("synthetic-access")
//    protected final InputStream getScriptInputStream() throws IOException{
//        if ( scriptReader != null ){
//            return new InputStream() {
//                @Override
//                public int read() throws IOException {
//                    return scriptReader.read();
//                }
//            };
//        }
//        return scriptURL.openStream();
//    }

    protected final URL getScriptURL() throws IOException{
        return scriptURL;
    }

//    protected final Reader getScriptReader() throws IOException{
//        if ( scriptReader != null ){
//            return scriptReader;
//        }
//        return new InputStreamReader(scriptURL.openStream());
//    }
    
    protected abstract PicoContainer createContainerFromScript(PicoContainer parentContainer, Object assemblyScope);

}
