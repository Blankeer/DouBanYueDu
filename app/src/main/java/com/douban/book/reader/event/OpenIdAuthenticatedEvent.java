package com.douban.book.reader.event;

public class OpenIdAuthenticatedEvent {
    public String openId;
    public String openIdAccessToken;
    public int openIdType;

    public OpenIdAuthenticatedEvent(int openIdType, String openId, String openIdAccessToken) {
        this.openIdType = openIdType;
        this.openId = openId;
        this.openIdAccessToken = openIdAccessToken;
    }
}
