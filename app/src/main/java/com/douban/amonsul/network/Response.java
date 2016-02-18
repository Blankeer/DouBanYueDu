package com.douban.amonsul.network;

public class Response {
    private String mMessage;
    private int mResponseCode;
    private String mResponseContent;

    public Response(String responseContent, int responseCode, String message) {
        this.mResponseCode = responseCode;
        this.mResponseContent = responseContent;
        this.mMessage = message;
    }

    public String getMessage() {
        return this.mMessage;
    }

    public int getResponseCode() {
        return this.mResponseCode;
    }

    public String getResponseContent() {
        return this.mResponseContent;
    }

    public String toString() {
        return "Response[responseContent='" + this.mResponseContent + '\'' + ", message='" + this.mMessage + '\'' + ", responseCode=" + this.mResponseCode + ']';
    }
}
