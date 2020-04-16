package com.reedelk.json.component;

import com.reedelk.runtime.api.annotation.Description;
import com.reedelk.runtime.api.annotation.ModuleComponent;
import com.reedelk.runtime.api.component.ProcessorSync;
import com.reedelk.runtime.api.converter.ConverterService;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageBuilder;
import com.reedelk.runtime.api.message.content.DataRow;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@ModuleComponent("Object to JSON")
@Description("Converts a Java Object into a JSON string. " +
        "A Java List is mapped to a JSON Array and a Java Map is mapped to a Java Map. " +
        "Any other Java object is mapped using getters.")
@Component(service = ObjectToJSON.class, scope = ServiceScope.PROTOTYPE)
public class ObjectToJSON implements ProcessorSync {

    private static final int INDENT_FACTOR = 4;

    @Reference
    ConverterService converterService;

    @Override
    public Message apply(FlowContext flowContext, Message message) {
        Object payload = message.payload();

        Object result = convert(payload);

        String json = null;
        if (result instanceof JSONObject) {
            json = ((JSONObject) result).toString(INDENT_FACTOR);
        } else if (result instanceof JSONArray) {
            json = ((JSONArray) result).toString(INDENT_FACTOR);
        }

        return MessageBuilder.get(ObjectToJSON.class)
                .withJson(json)
                .build();
    }

    private Object convert(Object payload) {
        if (payload instanceof List) {
            // JSON Array
            List<?> payloadAsList = (List<?>) payload;
            JSONArray array = new JSONArray(payloadAsList);
            for (int i = 0; i < payloadAsList.size(); i++) {
                array.put(i, convert(payloadAsList.get(i)));
            }
            return array;

        } else if (payload instanceof Map) {
            // JSON Object
            Map<?,?> payloadAsMap = (Map<?,?>) payload;
            JSONObject object = new JSONObject(payloadAsMap);
            payloadAsMap.forEach((BiConsumer<Object, Object>) (key, value) -> {
                String keyAsString = converterService.convert(key, String.class); // keys must be string
                object.put(keyAsString, convert(value));
            });
            return object;

        } else if (payload instanceof DataRow) {
            // DataRow mapping
            DataRow<?> payloadAsDataRow = (DataRow<?>) payload;

            JSONObject rowObject = new JSONObject();
            int numColumns = payloadAsDataRow.columnCount();
            for (int i = 1; i < numColumns + 1; i++) {
                String columnName = payloadAsDataRow.columnName(i);
                rowObject.put(columnName, payloadAsDataRow.get(i));
            }
            return rowObject;

        } else if (payload != null) {
            // Java beans
            return new JSONObject(payload);

        } else {
            return null;
        }
    }
}
