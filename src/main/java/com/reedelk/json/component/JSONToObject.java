package com.reedelk.json.component;

import com.reedelk.json.internal.exception.JSONToObjectException;
import com.reedelk.runtime.api.annotation.Description;
import com.reedelk.runtime.api.annotation.ModuleComponent;
import com.reedelk.runtime.api.component.ProcessorSync;
import com.reedelk.runtime.api.exception.PlatformException;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import java.util.List;
import java.util.Map;

import static com.reedelk.json.internal.commons.Messages.JSONToObject.JSON_PARSE_ERROR;

@ModuleComponent("JSON to Object")
@Description("Converts a JSON string into a Java Object. " +
        "A JSON object is mapped to a Java Map and a JSON array is mapped to a Java List.")
@Component(service = JSONToObject.class, scope = ServiceScope.PROTOTYPE)
public class JSONToObject implements ProcessorSync {

    @Override
    public Message apply(FlowContext flowContext, Message message) {

        Object payload = message.payload();
        if (payload == null) {
            // The payload was null, we return an empty message.
            return MessageBuilder.get(JSONToObject.class)
                    .empty()
                    .build();
        }

        checkStringOrThrow(payload);

        Object json;
        try {
            json = new JSONTokener((String) payload).nextValue();
        } catch (JSONException exception) {
            String error = JSON_PARSE_ERROR.format(exception.getMessage());
            throw new JSONToObjectException(error, exception);
        }

        if (json instanceof JSONObject) {
            JSONObject object = (JSONObject) json;
            Map<String, Object> stringObjectMap = object.toMap();
            return MessageBuilder.get(JSONToObject.class)
                    .withJavaObject(stringObjectMap)
                    .build();

        } else if (json instanceof JSONArray) {
            JSONArray array = (JSONArray) json;
            List<Object> objects = array.toList();
            return MessageBuilder.get(JSONToObject.class)
                    .withList(objects, Object.class)
                    .build();

        } else {
            throw new JSONToObjectException("Not a valid JSON");
        }
    }

    private void checkStringOrThrow(Object payload) {
        if (!(payload instanceof String)) {
            // The input is not a string.
            throw new PlatformException("Expected string");
        }
    }
}
