package com.reedelk.json.internal;

import com.reedelk.json.internal.exception.JSONToObjectException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import static com.reedelk.json.internal.commons.Messages.JSONToObject.JSON_PARSE_ERROR;
import static com.reedelk.json.internal.commons.Messages.JSONToObject.JSON_TOKEN_ERROR;

public class JSONToObjectConverter {

    public Object toObject(String payload) {
        Object token;
        try {
            token = new JSONTokener(payload).nextValue();
        } catch (JSONException exception) {
            String error = JSON_PARSE_ERROR.format(exception.getMessage());
            throw new JSONToObjectException(error, exception);
        }

        if (token instanceof JSONObject) {
            JSONObject object = (JSONObject) token;
            return object.toMap();

        } else if (token instanceof JSONArray) {
            JSONArray array = (JSONArray) token;
            return array.toList();

        } else {
            String error = JSON_TOKEN_ERROR.format(token);
            throw new JSONToObjectException(error);
        }
    }
}
