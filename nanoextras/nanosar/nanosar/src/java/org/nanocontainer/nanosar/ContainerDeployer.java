package org.nanocontainer.nanosar;

import java.io.InputStreamReader;
import java.io.Reader;

import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nanocontainer.script.ScriptBuilderResolver;
import org.nanocontainer.script.ScriptedContainerBuilderFactory;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ObjectReference;
import org.picocontainer.PicoContainer;
import org.picocontainer.gems.jndi.JNDIContainerVisitor;
import org.picocontainer.gems.jndi.JNDIObjectReference;
import org.picocontainer.references.SimpleReference;

/**
 * mbean for deployment in JBoss SAR
 * 
 * @author ko5tik
 * 
 */
public class ContainerDeployer implements ContainerDeployerMBean, MBeanRegistration {

	String containerComposer;

	ObjectReference<PicoContainer> containerRef;

	String jndiName;

	Log log;

	String parentName;

	String script;

	boolean started = false;
	MBeanServer mbeanServer;
	ObjectName objectName;
	
	public ContainerDeployer() {
		log = LogFactory.getLog(ContainerDeployer.class);
		log.info("instantiating container deployer");
	}

	public String getContainerComposer() {
		return containerComposer;
	}

	/**
	 * jndi name to bind container to.  if not specified, container 
	 * will be not bound
	 */
	public String getJndiName() {
		return jndiName;
	}

	public String getParentName() {
		return parentName;
	}

	public String getScript() {
		return script;
	}

	public boolean isStarted() {
		return started;
	}

	public String list() {
		StringBuffer sb = new StringBuffer();
		for (ComponentAdapter<?> adapter : containerRef.get()
				.getComponentAdapters()) {
			sb.append(adapter.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

	public void setContainerComposer(String containerComposer) {
		this.containerComposer = containerComposer;
	}

	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public void setScript(String script) {
		this.script = script;
	}

	/**
	 * compose container and start it
	 */
	public synchronized void start() throws Exception {
		log.info("starting container");

		if (started) {
			throw new IllegalStateException("container already started");
		}
		
		// create container from script
		ScriptBuilderResolver resolver = new ScriptBuilderResolver();
		if (getScript() == null) {
			throw new Exception("script shall be specified");
		}
		log.info("loading  script:" + getScript());
		
		// explicit setting wins
		String builderName = getContainerComposer();

		// if no builder is set, try to resolve from script
		if (builderName == null) {
			builderName = resolver.getBuilderClassName(getScript().substring(
					getScript().lastIndexOf('.')));
		}
		
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		
		Reader scriptReader = new InputStreamReader(loader
				.getResourceAsStream(getScript()));
		
		ScriptedContainerBuilderFactory factory = new ScriptedContainerBuilderFactory(
				scriptReader, builderName, loader);

		InitialContext context = new InitialContext();

		JNDIObjectReference<PicoContainer> parentContainer = null;
		
		if (getParentName() != null) {
			parentContainer = new JNDIObjectReference<PicoContainer>(
					getParentName(), context);
		}
		
		containerRef = getJndiName() != null? new JNDIObjectReference<PicoContainer>(getJndiName(),
				context): new SimpleReference<PicoContainer>();

		// build and start container
		factory.getContainerBuilder().buildContainer(containerRef,
				parentContainer, null, false);

		log.info("container started an bound to JNDI");

		// expose everything necessary to JNDI
		(new JNDIContainerVisitor()).traverse(containerRef.get());
		log.info("components bound to JNDI");

		// TODO: expose components to JMX
		(new JMXContainerVisitor(objectName,mbeanServer)).traverse(containerRef.get());
		log.info("components are exposed to JMX");
		started = true;
	}

	/**
	 * stop and dispose container
	 */
	public void stop() throws Exception {
		log.info("stopping container");

		if (!started) {
			throw new IllegalStateException("container not started");
		}

		// dispose container out of reference

		containerRef.set(null);
	}

	public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception {

		mbeanServer = server;
		objectName = name;
		return name;
	}

	public void postRegister(Boolean registrationDone) {

	}

	public void preDeregister() throws Exception {
	}

	public void postDeregister() {
	}

}
