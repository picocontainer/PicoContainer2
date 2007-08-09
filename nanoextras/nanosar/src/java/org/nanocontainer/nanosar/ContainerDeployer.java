package org.nanocontainer.nanosar;

/**
 * mbean for deployment in JBoss SAR
 * @author ko5tik
 *
 */
public class ContainerDeployer implements ContainerDeployerMBean {

	String containerComposer;

	String jndiName;

	String parentName;

	String script;

	public String getContainerComposer() {
		return containerComposer;
	}

	public void setContainerComposer(String containerComposer) {
		this.containerComposer = containerComposer;
	}

	public String getJndiName() {
		return jndiName;
	}

	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}
	/**
	 * compose container and start it
	 */
	public void start() throws Exception {
		
		
	}
	/**
	 * stop and dispose container
	 */
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
}
