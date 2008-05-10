/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.integrationkit;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.Startable;
import org.picocontainer.Disposable;
import org.picocontainer.ObjectReference;

/**
 * @author Joe Walnes
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author Mauro Talevi
 */
//TODO -- Perhaps the start/stop behavior should be moved to a decorator?
public abstract class AbstractContainerBuilder implements ContainerBuilder {

	private final LifecycleMode startMode;
	
	public AbstractContainerBuilder() {
		this(LifecycleMode.AUTO_LIFECYCLE);
	}
	
	public AbstractContainerBuilder(LifecycleMode startMode) {
		this.startMode = startMode;
	}
	
    public final void buildContainer(ObjectReference<PicoContainer> containerRef, ObjectReference<PicoContainer> parentContainerRef, Object assemblyScope, boolean addChildToParent) {
        PicoContainer parentContainer = parentContainerRef == null ? null : (PicoContainer) parentContainerRef.get();
        PicoContainer container = createContainer(parentContainer, assemblyScope);

        if (parentContainer != null && parentContainer instanceof MutablePicoContainer) {
            MutablePicoContainer mutableParentContainer = (MutablePicoContainer) parentContainer;

            if (addChildToParent) {
                // this synchronization is necessary, because several servlet requests may
                // occur at the same time for given session, and this produce race condition
                // especially in framed environments
                synchronized (mutableParentContainer) {
                    // register the child in the parent so that lifecycle can be propagated down the hierarchy
                    mutableParentContainer.addChildContainer(container);
                }
            }
        }

        if (container instanceof MutablePicoContainer) {
            composeContainer((MutablePicoContainer) container, assemblyScope);
        }
        autoStart(container);

        // hold on to it
        containerRef.set(container);
    }

    protected void autoStart(PicoContainer container) {
    	if (!startMode.isInvokeLifecycle()) {
    		return;
    	}
    	
        if (container instanceof Startable) {
            ((Startable) container).start();
        }
    }

    public void killContainer(ObjectReference<PicoContainer> containerRef) {
        try {
            PicoContainer pico = (PicoContainer) containerRef.get();
        	if (startMode.isInvokeLifecycle()) {
                if (pico instanceof Startable) {
                    ((Startable) pico).stop();
                }
        	}
        	
            if (pico instanceof Disposable) {
                ((Disposable) pico).dispose();
            }
            PicoContainer parent = pico.getParent();
            if (parent != null && parent instanceof MutablePicoContainer) {
                // see comment in buildContainer
                synchronized (parent) {
                    ((MutablePicoContainer) parent).removeChildContainer(pico);
                }
            }
        } finally {
            containerRef.set(null);
        }
    }

    protected abstract void composeContainer(MutablePicoContainer container, Object assemblyScope);

    protected abstract PicoContainer createContainer(PicoContainer parentContainer, Object assemblyScope);
}
