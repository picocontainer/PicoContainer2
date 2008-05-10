package org.nanocontainer.testmodel;

import java.io.File;

/**
 * Used for testing permissions.
 *
 * @author Paul Hammant
 */
public class FileSystemUsing {

    public FileSystemUsing() {
        File afile = File.listRoots()[0];
        System.out.println("--> " + afile.getAbsolutePath());
        new File(afile,"foo-bar-directory").mkdirs();
        throw new RuntimeException("Whoa, should have barfed with access error");
    }

}
