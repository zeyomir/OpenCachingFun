package com.zeyomir.ocfun.storage.model;

public enum LogType {
    WILL_ATTEND(Constants.WILL_ATTEND_TEXT),
    ATTENDED(Constants.ATTENDED_TEXT),
    COMMENT(Constants.COMMENT_TEXT),
    FOUND_IT(Constants.FOUND_TEXT),
    DID_NOT_FIND_IT(Constants.NOT_FOUND_TEXT);

    private final String text;

    LogType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static LogType fromText(String type) {
        switch (type) {
            case Constants.WILL_ATTEND_TEXT:
                return WILL_ATTEND;
            case Constants.ATTENDED_TEXT:
                return ATTENDED;
            case Constants.FOUND_TEXT:
                return FOUND_IT;
            case Constants.NOT_FOUND_TEXT:
                return DID_NOT_FIND_IT;
            default:
            case Constants.COMMENT_TEXT:
                return COMMENT;
        }
    }

    private static class Constants {
        private static final String WILL_ATTEND_TEXT = "Will attend";
        private static final String FOUND_TEXT = "Found it";
        private static final String ATTENDED_TEXT = "Attended";
        private static final String COMMENT_TEXT = "Comment";
        private static final String NOT_FOUND_TEXT = "Didn't find it";
    }
}
