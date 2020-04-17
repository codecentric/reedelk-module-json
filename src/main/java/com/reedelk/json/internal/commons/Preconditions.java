package com.reedelk.json.internal.commons;

import com.reedelk.json.internal.exception.JSONToObjectException;

import static com.reedelk.json.internal.commons.Messages.JSONToObject.JSON_INPUT_ERROR;

public class Preconditions {

    public static void checkIsStringOrThrow(Object payload) {
        if (!(payload instanceof String)) {
            // The input is not a string.
            String message = JSON_INPUT_ERROR.format(payload.getClass().getSimpleName());
            throw new JSONToObjectException(message);
        }
    }
}
