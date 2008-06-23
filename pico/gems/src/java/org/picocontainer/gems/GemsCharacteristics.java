package org.picocontainer;

import static org.picocontainer.Characteristics.*;

import java.util.Properties;

/**
 * A list of properties to allow switching on and off different characteristics at container 
 * construction time.
 * @author Michael Rimov
 */
public final class GemsCharacteristics {

	private static String _JMX = "jmx";
	
    public static final Properties NO_JMX = immutable(_JMX, FALSE);

    public static final Properties JMX = immutable(_JMX, TRUE);
    
    private static final String _ASM_HIDING = "asm_hiding";
    
    public static final Properties ASM_HIDING = immutable(_ASM_HIDING, TRUE);
    
    public static final Properties NO_ASM_HIDING = immutable(_ASM_HIDING, FALSE);
    
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
