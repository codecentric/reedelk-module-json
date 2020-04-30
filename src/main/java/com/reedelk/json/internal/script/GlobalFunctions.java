package com.reedelk.json.internal.script;

import com.reedelk.json.internal.JSONToObjectConverter;
import com.reedelk.json.internal.Json;
import com.reedelk.json.internal.ObjectToJSONConverter;
import com.reedelk.runtime.api.script.ScriptGlobalFunctions;

import java.util.HashMap;
import java.util.Map;

public class GlobalFunctions implements ScriptGlobalFunctions {

    private final long moduleId;
    private final ObjectToJSONConverter objectToJSON;
    private final JSONToObjectConverter JSONToObject;

    public GlobalFunctions(long moduleId, ObjectToJSONConverter objectToJSON, JSONToObjectConverter JSONToObject) {
        this.moduleId = moduleId;
        this.objectToJSON = objectToJSON;
        this.JSONToObject = JSONToObject;
    }

    @Override
    public long moduleId() {
        return moduleId;
    }

    @Override
    public Map<String, Object> bindings() {
        Map<String, Object> bindings = new HashMap<>();
        bindings.put("Json", new Json(objectToJSON, JSONToObject));       // binding key are names of the global variables.
        return bindings;
    }
}
