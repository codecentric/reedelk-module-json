package de.codecentric.reedelk.json.internal.exception;

import de.codecentric.reedelk.runtime.api.exception.PlatformException;
import org.json.JSONException;

public class JSONToObjectException extends PlatformException {

    public JSONToObjectException(String message) {
        super(message);
    }

    public JSONToObjectException(String message, JSONException exception) {
        super(message, exception);
    }
}
