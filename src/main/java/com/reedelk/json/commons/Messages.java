package com.reedelk.json.commons;

public class Messages {

    private Messages() {
    }

    private static String formatMessage(String template, Object ...args) {
        return String.format(template, args);
    }

    interface FormattedMessage {
        String format(Object ...args);
    }

    public enum ResultSetAsJson implements FormattedMessage {

        WRONG_ARGUMENT("%s component expects message with payload of type=[%s] " +
                "but type=[%s] was given.");

        private String msg;

        ResultSetAsJson(String msg) {
            this.msg = msg;
        }

        @Override
        public String format(Object... args) {
            return formatMessage(msg, args);
        }
    }
}
