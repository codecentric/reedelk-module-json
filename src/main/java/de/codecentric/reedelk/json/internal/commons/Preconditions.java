package de.codecentric.reedelk.json.internal.commons;

import de.codecentric.reedelk.json.internal.exception.JSONToObjectException;

public class Preconditions {

    public static void checkIsStringOrThrow(Object payload) {
        if (!(payload instanceof String)) {
            // The input is not a string.
            String message = Messages.JSONToObject.JSON_INPUT_ERROR.format(payload.getClass().getSimpleName());
            throw new JSONToObjectException(message);
        }
    }
}
