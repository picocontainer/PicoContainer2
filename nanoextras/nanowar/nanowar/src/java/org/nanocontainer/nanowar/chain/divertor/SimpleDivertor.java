package org.nanocontainer.nanowar.chain.divertor;

import org.nanocontainer.nanowar.chain.Divertor;

/**
 * simple divertor diverting to hardvired UIRL
 * 
 * @author k.pribluda
 * 
 */
public class SimpleDivertor implements Divertor {
	String path;

	public SimpleDivertor(String path) {
		this.path = path;
	}

	public String divert(Throwable cause) {

		return path;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
