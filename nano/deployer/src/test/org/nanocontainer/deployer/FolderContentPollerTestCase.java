package org.nanocontainer.deployer;

import org.apache.commons.vfs.FileObject;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.nanocontainer.deployer.FolderContentHandler;
import org.nanocontainer.deployer.FolderContentPoller;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class FolderContentPollerTestCase extends MockObjectTestCase {

    public void testShouldPollForNewFoldersAtRegularIntervals() throws InterruptedException {
        Mock rootFolderMock = mock(FileObject.class, "rootFolder");
        FileObject[] noChildren = new FileObject[0];

        // Adding a child that will be returned at the second invocation of getChildren
        Mock newChildFolderMock = mock(FileObject.class, "childFolder");
        FileObject[] newChildren = new FileObject[] {(FileObject) newChildFolderMock.proxy()};

        Mock folderContentHandlerMock = mock(FolderContentHandler.class, "folderContentHandlerMock");

        folderContentHandlerMock.expects(once())
                                .method("getFolder")
                                .withNoArguments()
                                .will(returnValue(rootFolderMock.proxy()));

        rootFolderMock.expects(once())
                      .method("close")
                      .withNoArguments();
        rootFolderMock.expects(once())
                      .method("getChildren")
                      .withNoArguments()
                      .will(returnValue(noChildren));
        folderContentHandlerMock.expects(once())
                                .method("setCurrentChildren")
                                .with(same(noChildren));
        FolderContentPoller fileMonitor = new FolderContentPoller((FolderContentHandler) folderContentHandlerMock.proxy());

        fileMonitor.start();
        synchronized(fileMonitor) {
        	fileMonitor.wait(200);
        }

        rootFolderMock.expects(once())
                      .method("close")
                      .withNoArguments();
        rootFolderMock.expects(once())
                      .method("getChildren")
                      .withNoArguments()
                      .will(returnValue(newChildren));
        folderContentHandlerMock.expects(once())
                                .method("setCurrentChildren")
                                .with(same(newChildren));


        synchronized(fileMonitor) {
            fileMonitor.notify();
            fileMonitor.wait(200);
        }
        fileMonitor.stop();
    }
}