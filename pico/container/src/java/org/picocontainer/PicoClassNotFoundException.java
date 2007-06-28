package org.picocontainer;

public class PicoClassNotFoundException extends PicoException {

    public PicoClassNotFoundException(final String className, final ClassNotFoundException cnfe) {
        super("Class '" + className + "' not found", cnfe);  
    }
}
