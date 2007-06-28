/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package org.nanocontainer.deployer;

import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSelectInfo;
import org.apache.commons.vfs.FileSelector;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.impl.VFSClassLoader;
import org.apache.commons.vfs.VFS;
import org.nanocontainer.integrationkit.ContainerBuilder;
import org.nanocontainer.script.ScriptedContainerBuilderFactory;
import org.picocontainer.ObjectReference;
import org.picocontainer.behaviors.CachingBehavior;

import org.nanocontainer.script.UnsupportedScriptTypeException;
import org.nanocontainer.script.ScriptBuilderResolver;

/**
 * This class is capable of deploying an application from any kind of file system
 * supported by <a href="http://jakarta.apache.org/commons/sandbox/vfs/">Jakarta VFS</a>.
 * (Like local files, zip files etc.) - following the ScriptedContainerBuilderFactory scripting model.
 *
 * The root folder to deploy must have the following file structure:
 * <pre>
 * +-someapp/
 *   +-META-INF/
 *   | +-nanocontainer.[py|js|xml|bsh]
 *   +-com/
 *     +-blablah/
 *       +-Hip.class
 *       +-Hop.class
 * </pre>
 *
 * For those familiar with J2EE containers (or other containers for that matter), the
 * META-INF/picocontainer script is the ScriptedContainerBuilderFactory <em>composition script</em>. It plays the same
 * role as more classical "deployment descriptors", except that deploying via a full blown
 * scripting language is a lot more powerful!
 *
 * A new class loader (which will be a child of parentClassLoader) will be created. This classloader will make
 * the classes under the root folder available to the deployment script.
 *
 * IMPORTANT NOTE:
 * The scripting engine (rhino, jython, groovy etc.) should be loaded by the same classLoader as
 * the appliacation classes, i.e. the VFSClassLoader pointing to the app directory.
 *
 * <pre>
 *    +-------------------+
 *    | xxx               |  <-- parent app loader (must not contain classes from app builder classloader)
 *    +-------------------+
 *              |
 *    +-------------------+
 *    | someapp           | <-- app classloader (must not contain classes from app builder classloader)
 *    +-------------------+
 *              |
 *    +-------------------+
 *    | picocontainer     |
 *    | nanocontainer     |  <-- app builder classloader
 *    | rhino             |
 *    | jython            |
 *    | groovy            |
 *    +-------------------+
 * </pre>
 *
 * This means that these scripting engines should *not* be accessible by any of the app classloader, since this
 * may prevent the scripting engine from seeing the classes loaded by the VFSClassLoader. In other words,
 * the scripting engine classed may be loaded several times by different class loaders - once for each
 * deployed application.
 *
 * @author Aslak Helles&oslash;y
 */
public class NanoContainerDeployer implements Deployer {

    /**
     * VFS file system manager.
     */
    private final FileSystemManager fileSystemManager;

    /**
     * File system basename.  Defaults to 'nanocontainer'.  May be set differently
     * for other applications.
     */
    private final String fileBasename;


    /**
     * File Name to builder class name resolver.
     */
    private ScriptBuilderResolver resolver;


    /**
     * Default constructor that makes sensible defaults.
     * @throws FileSystemException
     */
    public NanoContainerDeployer() throws FileSystemException {
        this(VFS.getManager(), new ScriptBuilderResolver());
    }

    /**
     * Constructs a nanocontainer deployer with the specified file system manager.
     * @param fileSystemManager A VFS FileSystemManager.
     */
    public NanoContainerDeployer(final FileSystemManager fileSystemManager) {
        this(fileSystemManager,"nanocontainer");
    }


    /**
     * Constructs this object with both a VFS file system manager, and
     * @param fileSystemManager FileSystemManager
     * @param builderResolver ScriptBuilderResolver
     */
    public NanoContainerDeployer(final FileSystemManager fileSystemManager, ScriptBuilderResolver builderResolver) {
        this(fileSystemManager);
        resolver = builderResolver;
    }

    /**
     * Constructs a nanocontainer deployer with the specified file system manager
     * and specifies a 'base name' for the configuration file that will be loaded.
     * @param fileSystemManager A VFS FileSystemManager.
     * @todo Deprecate this and replace 'base file name' with the concept
     * of a ArchiveLayout that defines where jars are stored, where the composition
     * script is stored, etc.
     * @param baseFileName
     */
    public NanoContainerDeployer(final FileSystemManager fileSystemManager, String baseFileName) {
        this.fileSystemManager = fileSystemManager;
        fileBasename = baseFileName;
        resolver = new ScriptBuilderResolver();
    }


    public ObjectReference deploy(FileObject applicationFolder, ClassLoader parentClassLoader, ObjectReference parentContainerRef, Object assemblyScope) throws FileSystemException {
        ClassLoader applicationClassLoader = new VFSClassLoader(applicationFolder, fileSystemManager, parentClassLoader);

        FileObject deploymentScript = getDeploymentScript(applicationFolder);

        ObjectReference result = new CachingBehavior.SimpleReference();

        String extension = "." + deploymentScript.getName().getExtension();
        Reader scriptReader = new InputStreamReader(deploymentScript.getContent().getInputStream());
        String builderClassName;
        try {
            builderClassName = resolver.getBuilderClassName(extension);
        } catch (UnsupportedScriptTypeException ex) {
            throw new FileSystemException("Could not find a suitable builder for: " + deploymentScript.getName()
                + ".  Known extensions are: [groovy|bsh|js|py|xml]", ex);
        }


        ScriptedContainerBuilderFactory scriptedContainerBuilderFactory = new ScriptedContainerBuilderFactory(scriptReader, builderClassName, applicationClassLoader);
        ContainerBuilder builder = scriptedContainerBuilderFactory.getContainerBuilder();
        builder.buildContainer(result, parentContainerRef, assemblyScope, true);

        return result;

    }




    /**
     * Given the base application folder, return a file object that represents the
     * nanocontainer configuration script.
     * @param applicationFolder FileObject
     * @return FileObject
     * @throws FileSystemException
     */
    protected FileObject getDeploymentScript(FileObject applicationFolder) throws FileSystemException {
        final FileObject metaInf = applicationFolder.getChild("META-INF");
        if(metaInf == null) {
            throw new FileSystemException("Missing META-INF folder in " + applicationFolder.getName().getPath());
        }
        final FileObject[] nanocontainerScripts = metaInf.findFiles(new FileSelector(){

            public boolean includeFile(FileSelectInfo fileSelectInfo) throws Exception {
                return fileSelectInfo.getFile().getName().getBaseName().startsWith(getFileBasename());
            }

            public boolean traverseDescendents(FileSelectInfo fileSelectInfo) throws Exception {
              //
              //nanocontainer.* can easily be deep inside a directory tree and
              //we end up not picking up our desired script.
              //
                return fileSelectInfo.getDepth() <= 1;
            }
        });

        if(nanocontainerScripts == null || nanocontainerScripts.length < 1) {
            throw new FileSystemException("No deployment script ("+ getFileBasename() +".[groovy|bsh|js|py|xml]) in " + applicationFolder.getName().getPath() + "/META-INF");
        }

        if (nanocontainerScripts.length == 1) {
          return nanocontainerScripts[0];
        } else {
          throw new FileSystemException("Found more than one candidate config script in : " + applicationFolder.getName().getPath() + "/META-INF."
              + "Please only have one " + getFileBasename() + ".[groovy|bsh|js|py|xml] this directory.");
        }

    }


    /**
     * Retrieve the file base name.
     * @return String
     */
    public String getFileBasename() {
        return fileBasename;
    }
}
