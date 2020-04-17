package com.reedelk.json.internal;

import com.reedelk.json.component.JSONToObject;
import com.reedelk.json.internal.exception.JSONToObjectException;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.List;
import java.util.Map;

import static com.reedelk.json.internal.commons.Messages.JSONToObject.JSON_PARSE_ERROR;
import static com.reedelk.json.internal.commons.Messages.JSONToObject.JSON_TOKEN_ERROR;

public class JSONToObjectConverter {


    public Message toObject(String payload) {
        Object token;
        try {
            token = new JSONTokener(payload).nextValue();
        } catch (JSONException exception) {
            String error = JSON_PARSE_ERROR.format(exception.getMessage());
            throw new JSONToObjectException(error, exception);
        }

        if (token instanceof JSONObject) {
            JSONObject object = (JSONObject) token;
            Map<String, Object> stringObjectMap = object.toMap();
            return MessageBuilder.get(JSONToObject.class)
                    .withJavaObject(stringObjectMap)
                    .build();

        } else if (token instanceof JSONArray) {
            JSONArray array = (JSONArray) token;
            List<Object> objects = array.toList();
            return MessageBuilder.get(JSONToObject.class)
                    .withList(objects, Object.class)
                    .build();

        } else {
            String error = JSON_TOKEN_ERROR.format(token);
            throw new JSONToObjectException(error);
        }
    }
}
