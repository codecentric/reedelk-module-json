package de.codecentric.reedelk.json.internal.exception;

import de.codecentric.reedelk.runtime.api.exception.PlatformException;

public class ObjectToJSONException extends PlatformException {

    public ObjectToJSONException(String message) {
        super(message);
    }

    public ObjectToJSONException(String message, Throwable exception) {
        super(message, exception);
    }
}
