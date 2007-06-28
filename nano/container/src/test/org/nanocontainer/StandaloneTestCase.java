/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.commons.cli.CommandLine;


/**
 * @author Mauro Talevi
 */
public class StandaloneTestCase extends TestCase {

    public void testShouldBeAbleToInvokeMainMethodWithScriptFromFile() throws IOException {
        File absoluteScriptPath = getAbsoluteScriptPath();
        Standalone.main(new String[] {
            "-c",
            absoluteScriptPath.getAbsolutePath(),
            "-n"
        });
    }

    public void testShouldBeAbleToInvokeMainMethodWithScriptFromClasspathWithXmlIncludes() throws IOException {
        Standalone.main(new String[] {
            "-r",
            "/org/nanocontainer/nanocontainer-with-include.xml", 
            "-n"
        });
    }

    private File getAbsoluteScriptPath() {
        String className = getClass().getName();
        String relativeClassPath = "/" + className.replace('.', '/') + ".class";
        URL classURL = Standalone.class.getResource(relativeClassPath);
        String absoluteClassPath = classURL.getFile();
        File absoluteDirPath = new File(absoluteClassPath).getParentFile();
        return new File(absoluteDirPath, "nanocontainer.xml");
    }

    public void testCommandLineWithHelp() throws Exception {
        CommandLine cl = Standalone.getCommandLine(new String[]{"-h"}, Standalone.createOptions());
        assertTrue(cl.hasOption('h'));
        assertFalse(cl.hasOption('v'));
        assertNull(cl.getOptionValue('c'));
        assertFalse(cl.hasOption('q'));
        assertFalse(cl.hasOption('n'));
    }

    public void testCommandLineWithVersion() throws Exception {
        CommandLine cl = Standalone.getCommandLine(new String[]{"-v"}, Standalone.createOptions());
        assertFalse(cl.hasOption('h'));
        assertTrue(cl.hasOption('v'));
        assertNull(cl.getOptionValue('c'));
        assertFalse(cl.hasOption('q'));
        assertFalse(cl.hasOption('n'));
    }

    public void testCommandLineWithCompostion() throws Exception {
        CommandLine cl = Standalone.getCommandLine(new String[]{"-cpath"}, Standalone.createOptions());
        assertFalse(cl.hasOption('h'));
        assertFalse(cl.hasOption('v'));
        assertEquals("path", cl.getOptionValue('c'));
        assertFalse(cl.hasOption('q'));
        assertFalse(cl.hasOption('n'));
    }

    public void testCommandLineWithCompositionAndQuiet() throws Exception {
        CommandLine cl = Standalone.getCommandLine(new String[]{"-cpath", "-q"}, Standalone.createOptions());
        assertFalse(cl.hasOption('h'));
        assertFalse(cl.hasOption('v'));
        assertEquals("path", cl.getOptionValue('c'));
        assertTrue(cl.hasOption('q'));
        assertFalse(cl.hasOption('n'));
    }

    public void testCommandLineWithCompositionAndQuietAndNowait() throws Exception {
        CommandLine cl = Standalone.getCommandLine(new String[]{"-cpath", "-q", "-n"}, Standalone.createOptions());
        assertFalse(cl.hasOption('h'));
        assertFalse(cl.hasOption('v'));
        assertEquals("path", cl.getOptionValue('c'));
        assertTrue(cl.hasOption('q'));
        assertTrue(cl.hasOption('n'));
    }

}
