package com.douban.amonsul.network;

import org.apache.http.NameValuePair;

public class BaseNameValuePair implements NameValuePair {
    private String name;
    private String value;

    public BaseNameValuePair(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }
}
