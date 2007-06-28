package org.nanocontainer.deployer;

import org.apache.commons.vfs.FileObject;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.nanocontainer.deployer.Deployer;
import org.nanocontainer.deployer.DeployingFolderListener;
import org.nanocontainer.deployer.DifferenceAnalysingFolderContentHandler;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DeployingFolderListenerTestCase extends MockObjectTestCase {
    public void testFolderAddedShouldDeployApplication() {
        Mock folderMock = mock(FileObject.class);
        FileObject folder = (FileObject) folderMock.proxy();

        Mock deployerMock = mock(Deployer.class);
        deployerMock.expects(once())
                    .method("deploy")
                    .with(same(folder), isA(ClassLoader.class), ANYTHING, NULL)
                    .will(returnValue(null));
        Deployer deployer = (Deployer)deployerMock.proxy();
        DifferenceAnalysingFolderContentHandler handler = null;
        DeployingFolderListener deployingFolderListener = new DeployingFolderListener(deployer, handler);

        deployingFolderListener.folderAdded(folder);
    }

}