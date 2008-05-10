package org.nanocontainer.deployer;

import org.apache.commons.vfs.FileObject;

/**
 * @author Aslak Helles&oslash;y
 */
public interface FolderContentHandler {
    void setCurrentChildren(FileObject[] currentChildren);

    FileObject getFolder();
}