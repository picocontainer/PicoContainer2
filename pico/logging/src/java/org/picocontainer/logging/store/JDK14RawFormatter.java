package org.picocontainer.logging.store;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Class that extends base formatter to provide raw text output.
 *
 * @author Peter Donald
 * @version $Revision: 1.2 $ $Date: 2003/12/03 06:32:02 $
 */
public class JDK14RawFormatter
    extends Formatter
{
    public String format( final LogRecord record )
    {
        return formatMessage( record ) + "\n";
    }
}
