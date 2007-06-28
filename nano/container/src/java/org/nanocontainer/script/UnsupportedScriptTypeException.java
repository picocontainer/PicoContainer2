package org.nanocontainer.script;

import org.picocontainer.PicoException;

/**
 * Indicates that a given file extension has no corresponding builder.  The
 * message will also indicate all supported builders.
 * @author Michael Rimov
 * @version 1.0
 */
public class UnsupportedScriptTypeException extends PicoException {

    private final String specifiedFileExtension;

    private final String[] allSupportedFileExtensions;

    public UnsupportedScriptTypeException(String specifiedFileExtension, String[] allSupportedFileExtensions) {
        super();
        this.specifiedFileExtension = specifiedFileExtension;
        this.allSupportedFileExtensions = allSupportedFileExtensions;
    }



    /**
     * Transforms the constructor arguments into a real exption
     * @return String
     */
    private  String buildExceptionMessage() {
        StringBuffer message = new StringBuffer(48);
        message.append("Unsupported file extension '");
        message.append(specifiedFileExtension);
        message.append("'.  Supported extensions are: [");

        if (allSupportedFileExtensions != null) {
            boolean needPipe = false;
            for (String allSupportedFileExtension : allSupportedFileExtensions) {
                if (needPipe) {
                    message.append("|");
                } else {
                    needPipe = true;
                }

                message.append(allSupportedFileExtension);
            }

            message.append("].");
        } else {
            message.append(" null ");
        }

        return message.toString();
    }

    public String getMessage() {
        return buildExceptionMessage();
    }

    public String[] getSystemSupportedExtensions() {
        return allSupportedFileExtensions;
    }

    public String getRequestedExtension() {
        return specifiedFileExtension;
    }

}
