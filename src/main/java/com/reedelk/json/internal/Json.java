package com.reedelk.json.internal;

import com.reedelk.json.internal.exception.ObjectToJSONException;
import com.reedelk.runtime.api.annotation.Type;
import com.reedelk.runtime.api.annotation.TypeFunction;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.reedelk.json.internal.commons.Messages.ObjectToJSON.NOT_VALID_JSON_OBJECT;

// TODO: The problem is that autocomplete does not complete if there are two methods with the same name
//  and with different arguments only?
// TODO: Add tests to these functions.
@Type(global = true,
        description = "The Json type provides a set of utility functions to convert from/to JSON.")
public class Json {

    private final ObjectToJSONConverter objectToJSON;
    private final JSONToObjectConverter JSONToObject;

    public Json(ObjectToJSONConverter objectToJSON, JSONToObjectConverter JSONToObject) {
        this.objectToJSON = objectToJSON;
        this.JSONToObject = JSONToObject;
    }

    @TypeFunction(
            cursorOffset = 1,
            signature = "stringify(Object object)",
            example = "Json.stringify(message.payload())",
            description = "Converts the input into a JSON string.")
    public String stringify(Object object) {
        return print(objectToJSON.toJSON(object), false, 0);
    }

    @TypeFunction(
            cursorOffset = 1,
            signature = "stringify(Object object, int indentFactor)",
            example = "Json.stringify(message.payload(), 2)",
            description = "Converts the input into a pretty printed JSON string using the provided indent factor.")
    public String stringify(Object object, int indentFactor) {
        return print(objectToJSON.toJSON(object), true, indentFactor);
    }

    @TypeFunction(
            cursorOffset = 1,
            signature = "parse(String json)",
            example = "Json.parse(\"{'name': 'John', 'surname': 'Doe'}\")",
            description = "Parses the input string into a Java object. The output object is a list or a map according to the input JSON.")
    public Object parse(String json) {
        return JSONToObject.toObject(json);
    }

    private String print(Object result, boolean isPrettyPrint, int theIndentFactor) {
        if (result instanceof JSONObject) {
            JSONObject outObject = (JSONObject) result;
            return isPrettyPrint ?
                    outObject.toString(theIndentFactor) :
                    outObject.toString();
        } else if (result instanceof JSONArray) {
            JSONArray outArray = (JSONArray) result;
            return isPrettyPrint ?
                    outArray.toString(theIndentFactor) :
                    outArray.toString();

        } else {
            // A JSON is valid if and only if the Root is an array or an object.
            throw new ObjectToJSONException(NOT_VALID_JSON_OBJECT.format(result == null ? null : result.getClass()));
        }
    }
}
