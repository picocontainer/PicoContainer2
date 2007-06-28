package org.nanocontainer.script;

import org.picocontainer.PicoException;

/**
 * Indicates that a given class for a builder was not found by the ScriptedContainerBuilderFactory
 * when trying to use its specified classloader.
 *
 * @author Michael Rimov
 */
public class BuilderClassNotFoundException extends PicoException {

    public BuilderClassNotFoundException(String message) {
        super(message);
    }

    public BuilderClassNotFoundException(Throwable cause) {
        super(cause);
    }

    public BuilderClassNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
