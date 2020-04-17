package com.reedelk.json.internal;

import com.reedelk.runtime.api.commons.PlatformTypes;
import com.reedelk.runtime.api.converter.ConverterService;
import com.reedelk.runtime.api.message.content.DataRow;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class ObjectToJSONConverter {

    private final ConverterService converterService;

    public ObjectToJSONConverter(ConverterService converterService) {
        this.converterService = converterService;
    }

    public Object toJSON(Object payload) {
        if (payload instanceof List) {
            return toJSON((List<?>) payload); // To JSON Array
        } else if (payload instanceof Map) {
            return toJSON((Map<?, ?>) payload); // To JSON Object
        } else if (payload instanceof DataRow) {
            return toJSON((DataRow<?>) payload); // To JSON Object
        } else if (payload != null) {
            return PlatformTypes.isPrimitive(payload.getClass()) ? // To Primitive or JSON Object
                    payload : new JSONObject(payload);
        } else {
            return null;
        }
    }

    private JSONObject toJSON(Map<?, ?> map) {
        JSONObject object = new JSONObject(map);
        map.forEach((BiConsumer<Object, Object>) (key, value) -> {
            String keyAsString = converterService.convert(key, String.class); // keys must be string.
            Object convertedValue = toJSON(value); // we need to recursively convert the value (might be a nested object).
            object.put(keyAsString, convertedValue);
        });
        return object;
    }

    private JSONArray toJSON(List<?> list) {
        JSONArray array = new JSONArray(list);
        for (int i = 0; i < list.size(); i++) {
            array.put(i, toJSON(list.get(i)));
        }
        return array;
    }

    private JSONObject toJSON(DataRow<?> dataRow) {
        JSONObject rowObject = new JSONObject();
        int numColumns = dataRow.columnCount();
        for (int i = 0; i < numColumns; i++) {
            String columnName = dataRow.columnName(i);
            rowObject.put(columnName, dataRow.get(i));
        }
        return rowObject;
    }
}
