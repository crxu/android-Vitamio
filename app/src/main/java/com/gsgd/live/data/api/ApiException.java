package com.gsgd.live.data.api;

public class ApiException extends Exception {

    private final int code;
    private final String message;

    public ApiException(int errCode, String errMsg) {
        this.code = errCode;
        this.message = errMsg;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }
}
