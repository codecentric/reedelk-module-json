package de.codecentric.reedelk.json.internal.commons;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class IsValid {

    public static boolean json(String input) {
        try {
            new JSONObject(input);
        } catch (JSONException exception1) {
            try {
                new JSONArray(input);
            } catch (JSONException exception2) {
                return false;
            }
        }
        return true;
    }
}
