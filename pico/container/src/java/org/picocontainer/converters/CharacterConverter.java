package org.picocontainer.converters;


/**
 * Converts strings to characters.  It does so by only grabbing
 * the first character in the string. 
 * @author Paul Hammant, Michael Rimov
 */
class CharacterConverter implements Converter<Character> {

    public Character convert(String paramValue) {
        return paramValue.charAt(0);
    }
}
