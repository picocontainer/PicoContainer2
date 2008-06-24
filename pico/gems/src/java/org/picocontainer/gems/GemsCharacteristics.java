package org.picocontainer.gems;

import static org.picocontainer.Characteristics.*;

import java.util.Properties;

/**
 * A list of properties to allow switching on and off different characteristics at container 
 * construction time.
 * @author Michael Rimov
 */
public final class GemsCharacteristics {

	private static String _JMX = "jmx";
	
    /**
     * Turn off behavior for {@link org.picocontainer.gems.jmx.JMXExposing JMXExposing}
     */
    public static final Properties NO_JMX = immutable(_JMX, FALSE);

    /**
     * Turn on behavior for {@link org.picocontainer.gems.jmx.JMXExposing JMXExposing}
     */
    public static final Properties JMX = immutable(_JMX, TRUE);
    
    /**
     * Turn on ASM-proxy based hidden implementation.  Also see the JDK 1.4-proxy version.
     * @see org.picocontainer.behaviors.HiddenImplementation<T>
     * @see 
     */
    private static final String _ASM_HIDE_IMPL = "asm-hide-impl";
    
    public static final Properties NO_HIDE_IMPL = immutable(_ASM_HIDE_IMPL, FALSE);    
    
    private static final String _HOT_SWAP = "hotswap";
    
    public static final Properties HOT_SWAP = immutable(_HOT_SWAP, TRUE);
    
    public static final Properties NO_HOT_SWAP = immutable(_HOT_SWAP, FALSE);
    
    private static final String _POOL = "pooled";
    
    public static final Properties POOLED = immutable(_POOL, TRUE);
    
    public static final Properties NO_POOLED = immutable(_POOL, FALSE);
	
    private static final String _THREAD_LOCAL = "threadlocal";
    
    public static final Properties THREAD_LOCAL = immutable(_THREAD_LOCAL, TRUE);
    
    public static final Properties NO_THREAD_LOCAL = immutable(_THREAD_LOCAL, FALSE);
}
