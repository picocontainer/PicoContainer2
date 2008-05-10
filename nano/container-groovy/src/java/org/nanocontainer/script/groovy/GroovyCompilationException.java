package org.nanocontainer.script.groovy;

import java.util.Collections;
import java.util.List;

import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.ErrorCollector;
import org.codehaus.groovy.control.ProcessingUnit;
import org.codehaus.groovy.control.messages.ExceptionMessage;
import org.nanocontainer.script.NanoContainerMarkupException;

/**
 * @author Paul Hammant
 */
public final class GroovyCompilationException extends NanoContainerMarkupException {
    private final CompilationFailedException compilationFailedException;

    public GroovyCompilationException(String message, CompilationFailedException e) {
        super(message,e);
        this.compilationFailedException = e;
    }

    public String getMessage() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.getMessage()).append("\n");
        List errors = getErrors(compilationFailedException);
        for (Object error : errors) {
            if (error instanceof ExceptionMessage) {
                ExceptionMessage em = (ExceptionMessage) error;
                sb.append(em.getCause().getMessage()).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Extract errors from groovy exception, coding defensively against
     * possible null values.
     * @param e the CompilationFailedException
     * @return A List of errors
     */
    private List getErrors(CompilationFailedException e) {
        ProcessingUnit unit = e.getUnit();
        if ( unit != null ){
            ErrorCollector collector = unit.getErrorCollector();
            if ( collector != null ){
                List errors = collector.getErrors();
                if ( errors != null ){
                    return errors;
                }
            }
        }
        return Collections.EMPTY_LIST;
    }
}
