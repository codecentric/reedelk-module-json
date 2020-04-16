package com.reedelk.json.internal.commons;

import com.reedelk.runtime.api.commons.FormattedMessage;

public class Messages {

    private Messages() {
    }

    public enum JSONToObject implements FormattedMessage {

        JSON_INPUT_ERROR("The message payload of type (%s) is not a string. " +
                "Only a payload containing a string type can be converted to JSON."),
        JSON_TOKEN_ERROR("The JSON cannot be parsed, cause=[Token '%s' was not expected]."),
        JSON_PARSE_ERROR("The JSON cannot be parsed, cause=[%s].");

        private String message;

        JSONToObject(String message) {
            this.message = message;
        }

        @Override
        public String template() {
            return message;
        }
    }

    public enum ObjectToJSON implements FormattedMessage {

        JSON_PARSE_ERROR("The JSON is not");

        private final String message;

        ObjectToJSON(String message) {
            this.message = message;
        }

        @Override
        public String template() {
            return message;
        }
    }
}
