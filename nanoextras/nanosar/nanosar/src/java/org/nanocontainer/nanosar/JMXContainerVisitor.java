package org.nanocontainer.nanosar;

import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.visitors.TraversalCheckingVisitor;

/**
 * exposes components  to MBeanServer
 * @author k.pribluda
 *
 */
public class JMXContainerVisitor extends TraversalCheckingVisitor {

	private PicoContainer container;
	
	private ObjectName baseName;
	private MBeanServer server;
	
	
	
	public JMXContainerVisitor(ObjectName baseName, MBeanServer server) {
		super();
		this.baseName = baseName;
		this.server = server;
	}

	
	public JMXContainerVisitor(MBeanServer server) {
		this(null,server);
	}
	
	/**
	 * in case component adapter is JNDIExposed, poke it gently and
	 * it will create component and register it to JNDI if not already 
	 * done. 
	 */
	@Override
	public void visitComponentAdapter(ComponentAdapter componentAdapter)
	{
		super.visitComponentAdapter(componentAdapter);

		if(componentAdapter instanceof SimpleJMXExposed) {
			// expose it
			((SimpleJMXExposed)componentAdapter).register(container,server,baseName);
		}
	}

	/**
     * Provides the PicoContainer, that can resolve the components to register as MBean.
     * @see org.picocontainer.PicoVisitor#visitContainer(org.picocontainer.PicoContainer)
     */
    public void visitContainer(final PicoContainer pico) {
        super.visitContainer(pico);
        container = pico;
    }


    /**
     * Entry point for the visitor traversal.
     * @return Returns a {@link Set} with all ObjectInstance instances retrieved from the {@link MBeanServer} for the
     *         registered MBeans.
     * @see org.picocontainer.visitors.AbstractPicoVisitor#traverse(java.lang.Object)
     */
    public Object traverse(final Object node) {
        super.traverse(node);
        container = null;
        return null;
    }
}
