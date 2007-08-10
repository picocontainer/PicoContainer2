package org.nanocontainer.nanosar;

/**
 * management interface for container deployer
 * @author ko5tik
 *
 */
public interface ContainerDeployerMBean {


	/**
	 * JNDI name of parent container
	 * @return
	 */
	String getParentName();
	
	/**
	 * set JNDI name of parent container
	 * @param name
	 */
	void setParentName(String name);
	/**
	 * JNDI name to bind container to 
	 * @return
	 */
	String getJndiName();
	/**
	 * JNDI name to bind contaienr to
	 * @param jndiName
	 */
	void setJndiName(String jndiName);
	
	/**
	 * FQ-name of container composer class
	 * @return
	 */
	String getContainerComposer();
	
	/**
	 * FQN of container composer class
	 * @param composer
	 */
	void setContainerComposer(String composer);
	
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
	
	void start() throws Exception;
	void stop() throws Exception;
	
	
}
