package org.picocontainer.logging.store.stores;

import java.io.InputStream;
import java.util.logging.LogManager;

import org.picocontainer.logging.Logger;
import org.picocontainer.logging.loggers.Jdk14Logger;


/**
 * Jdk14LoggerStore extends AbstractLoggerStore to provide the implementation
 * specific to the JDK14 logger.
 *
 * @author Mauro Talevi
 */
public class Jdk14LoggerStore
    extends AbstractLoggerStore
{
    /** The LogManager repository */
    private final LogManager m_manager;

    /**
     * Creates a <code>Log4JLoggerStore</code> using the configuration
     * resource.
     *
     * @param resource the InputStream encoding the configuration resource
     * @throws Exception if fails to create or configure Logger
     */
    public Jdk14LoggerStore( final InputStream resource )
        throws Exception
    {
        m_manager = LogManager.getLogManager();
        m_manager.readConfiguration( resource );
        setRootLogger( new Jdk14Logger( m_manager.getLogger( "global" ) ) );
    }

    /**
     * Creates new Jdk14Logger for the given category.
     */
    protected Logger createLogger( final String name )
    {
        return new Jdk14Logger( java.util.logging.Logger.getLogger( name ) );
    }

    /**
     * Closes the LoggerStore and shuts down the logger hierarchy.
     */
    public void close()
    {
        m_manager.reset();
    }
}
