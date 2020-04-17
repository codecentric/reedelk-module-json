package com.reedelk.json.internal.exception;

import com.reedelk.runtime.api.exception.PlatformException;

public class ObjectToJSONException extends PlatformException {

    public ObjectToJSONException(String message) {
        super(message);
    }

    public ObjectToJSONException(String message, Throwable exception) {
        super(message, exception);
    }
}
