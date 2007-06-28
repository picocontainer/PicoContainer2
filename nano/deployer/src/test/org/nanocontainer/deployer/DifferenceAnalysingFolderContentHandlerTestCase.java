package org.nanocontainer.deployer;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileType;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.nanocontainer.deployer.DifferenceAnalysingFolderContentHandler;
import org.nanocontainer.deployer.FolderListener;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DifferenceAnalysingFolderContentHandlerTestCase extends MockObjectTestCase {
    public void testAddedFoldersShouldCauseFolderAddedEvent() {
        Mock folderMock = mock(FileObject.class);
        DifferenceAnalysingFolderContentHandler handler = new DifferenceAnalysingFolderContentHandler((FileObject) folderMock.proxy(), null);

        Mock addedFolderMock = mock(FileObject.class);
        addedFolderMock.expects(once())
                       .method("getType")
                       .withNoArguments()
                       .will(returnValue(FileType.FOLDER));
        FileObject addedFolder = (FileObject) addedFolderMock.proxy();

        Mock folderListenerMock = mock(FolderListener.class);
        folderListenerMock.expects(once())
                          .method("folderAdded")
                          .with(same(addedFolder));
        handler.addFolderListener((FolderListener) folderListenerMock.proxy());

        handler.setCurrentChildren(new FileObject[]{addedFolder});
    }

    public void testRemovedFoldersShouldCauseFolderRemovedEvent() {
        Mock folderMock = mock(FileObject.class);
        DifferenceAnalysingFolderContentHandler handler = new DifferenceAnalysingFolderContentHandler((FileObject) folderMock.proxy(), null);

        Mock initialFolderOneMock = mock(FileObject.class);
        initialFolderOneMock.expects(once())
                            .method("getType")
                            .withNoArguments()
                            .will(returnValue(FileType.FOLDER));
        FileObject initialFolderOne = (FileObject) initialFolderOneMock.proxy();
        Mock initialFolderTwoMock = mock(FileObject.class);
        initialFolderTwoMock.expects(once())
                            .method("getType")
                            .withNoArguments()
                            .will(returnValue(FileType.FOLDER));
        initialFolderTwoMock.expects(once())
                            .method("getType")
                            .withNoArguments()
                            .will(returnValue(FileType.FOLDER));
        FileObject initialFolderTwo = (FileObject) initialFolderTwoMock.proxy();
        FileObject[] initialFolders = new FileObject[] {initialFolderOne, initialFolderTwo};

        handler.setCurrentChildren(initialFolders);

        FileObject[] foldersAfterRemoval = new FileObject[] {initialFolderOne};
        Mock folderListenerMock = mock(FolderListener.class);
        folderListenerMock.expects(once())
                          .method("folderRemoved")
                          .with(same(initialFolderTwo));
        handler.addFolderListener((FolderListener) folderListenerMock.proxy());

        handler.setCurrentChildren(foldersAfterRemoval);
    }
}