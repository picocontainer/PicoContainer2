package org.picocontainer.script.xml;

public class AttributeUtils {
	
    public static final String EMPTY = "";

	
	public static boolean notSet(Object string) {
        return string == null || string.equals(EMPTY);
    }

    public static boolean boolValue(String string, boolean defaultValue) {
        if (notSet(string)) {
            return defaultValue;
        }
        boolean aBoolean = Boolean.valueOf(string).booleanValue();
        return aBoolean;
    }
}
