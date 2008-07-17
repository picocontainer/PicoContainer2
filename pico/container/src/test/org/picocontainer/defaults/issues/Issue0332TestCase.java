package org.picocontainer.defaults.issues;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.parameters.ComponentParameter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNotNull;import static junit.framework.Assert.assertTrue;

public class Issue0332TestCase {

    /**
     * Sample class that demonstrates literal collection handling.
     */
    public static class Searcher {
    	private final List<String> searchPath;

    	public Searcher(List<String> searchPath) {
    		this.searchPath = searchPath;
    	}

    	public List<String> getSearchPath() {
    		return searchPath;
    	}
    }
    
    @Test
    public void dummy() {
        assertTrue(true);
    }

    //@Test
    public void canInstantiateAutowiredCollectionThatAreDefinedExplicitly() {
    	MutablePicoContainer pico = new DefaultPicoContainer(new Caching());
    	List<String> searchPath = new ArrayList<String>();
    	searchPath.add("a");
    	searchPath.add("b");

    	pico.addComponent("searchPath",searchPath)
    		.addComponent(Searcher.class);

    	assertNotNull(pico.getComponent(Searcher.class));
    	assertNotNull(pico.getComponent(Searcher.class).getSearchPath());
    }

    //@Test 
    public void canInstantiateExplicitCollectionWithComponentParameter() {
    	MutablePicoContainer pico = new DefaultPicoContainer(new Caching());
    	List<String> searchPath = new ArrayList<String>();
    	searchPath.add("a");
    	searchPath.add("b");

    	pico.addComponent("searchPath",searchPath)
    		.addComponent(Searcher.class, Searcher.class, new ComponentParameter("searchPath"));

    	assertNotNull(pico.getComponent(Searcher.class));
    	assertNotNull(pico.getComponent(Searcher.class).getSearchPath());
    }

    public static class StringArrayList extends ArrayList<String> {
    }

    //@Test
    public void canInstantiateAutowiredCollectionThatAreDefinedExplicitlyAmotherWay() {
    	MutablePicoContainer pico = new DefaultPicoContainer(new Caching());
    	List<String> searchPath = new StringArrayList();
    	searchPath.add("a");
    	searchPath.add("b");

    	pico.addComponent("searchPath",searchPath)
    		.addComponent(Searcher.class);

    	assertNotNull(pico.getComponent(Searcher.class));
    	assertNotNull(pico.getComponent(Searcher.class).getSearchPath());
    }



}
