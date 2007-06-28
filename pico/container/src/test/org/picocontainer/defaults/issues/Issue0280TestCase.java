package org.picocontainer.defaults.issues;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.DefaultPicoContainer;

import junit.framework.TestCase;

/**
 * Test case for issue http://jira.codehaus.org/browse/PICO-280
 */
public class Issue0280TestCase extends TestCase
{
    public void testShouldFailIfInstantiationInChildContainerFails()
    {
        MutablePicoContainer parent = new DefaultPicoContainer();
        MutablePicoContainer child = new DefaultPicoContainer(parent);

        parent.addComponent(CommonInterface.class, ParentImplementation.class);
        child.addComponent(CommonInterface.class, ChildImplementation.class);

        parent.start();
        
        try
        {
            Object result = child.getComponent(CommonInterface.class);
            
            // should never get here
            assertFalse(result.getClass() == ParentImplementation.class);
        }
        catch (Exception e)
        {
            assertTrue(e.getClass() == PicoCompositionException.class);
        }

    }

	public interface CommonInterface
	{
		
	}
	
	public static class ParentImplementation implements CommonInterface
	{
	}

	public static class ChildImplementation implements CommonInterface
	{
		public ChildImplementation()
		{
			throw new PicoCompositionException("Problem during initialization");
		}
	}

}
