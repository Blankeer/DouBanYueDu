package com.douban.amonsul.network;

public class MultipartParameter {
    private byte[] content;
    private String contentType;
    private String name;

    public MultipartParameter(String name, String contentType, byte[] content) {
        this.name = name;
        this.contentType = contentType;
        this.content = content;
    }

    public byte[] getContent() {
        return this.content;
    }

    public String getContentType() {
        return this.contentType;
    }

    public String getName() {
        return this.name;
    }
}
