package org.nanocontainer.nanosar;

/**
 * management interface for container deployer
 * @author ko5tik
 *
 */
public interface ContainerDeployerMBean {


	/**
	 * FQ-name of container composer class. only required in cases 
	 * where script name can not resolve proper composer. setting this will 
	 * override implicit specification
	 * @return
	 */
	String getContainerComposer();
	void setContainerComposer(String composer);
	/**
	 * JNDI name to bind container to 
	 * @return
	 */
	String getJndiName();
	void setJndiName(String name);
	/**
	 * JNDI name of parent container
	 * @return
	 */
	String getParentName();
	void setParentName(String name);
	/**
	 * script to feed to composer
	 * @return
	 */
	String getScript();
	void setScript(String script);
	/**
	 * whether this container was started
	 * @return
	 */
	boolean isStarted();
	
	/**
	 * list contents of container
	 * @return stringified container representation
	 */
	String list();

	void start() throws Exception;
	
	void stop() throws Exception;
}
