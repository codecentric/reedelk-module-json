package com.reedelk.json.internal;

import com.reedelk.runtime.api.commons.FormattedMessage;

public class Messages {

    private Messages() {
    }

    public enum ResultSetAsJson implements FormattedMessage {

        WRONG_ARGUMENT("%s component expects message with payload of type=[%s] " +
                "but type=[%s] was given.");

        private String message;

        ResultSetAsJson(String message) {
            this.message = message;
        }

        @Override
        public String template() {
            return message;
        }
    }
}
