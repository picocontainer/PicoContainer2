/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.tools.ant;

import org.apache.tools.ant.Project;
import org.jmock.MockObjectTestCase;

/**
 * Base class for testing of PicoContainerTask implementations.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public abstract class AbstractPicoContainerTaskTestCase extends MockObjectTestCase {
    protected PicoContainerTask task;

    public void setUp() {
        Project project = new Project();

        task = createPicoContainerTask();
        task.setProject(project);
        task.setTaskName(task.getClass().getName());
    }

    protected abstract PicoContainerTask createPicoContainerTask();
}
