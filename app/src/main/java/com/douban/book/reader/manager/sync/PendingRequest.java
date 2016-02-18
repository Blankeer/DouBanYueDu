package com.douban.book.reader.manager.sync;

import io.realm.RealmObject;
import io.realm.annotations.Index;

public class PendingRequest extends RealmObject {
    private long createTime;
    private long lastConnectTime;
    private int lastHttpStatusCode;
    private String method;
    private String requestParam;
    private String requestParamType;
    @Index
    private String resourceId;
    @Index
    private String resourceType;
    private int retryCount;
    private String uri;

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getResourceType() {
        return this.resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceId() {
        return this.resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getRequestParam() {
        return this.requestParam;
    }

    public void setRequestParam(String requestParam) {
        this.requestParam = requestParam;
    }

    public String getRequestParamType() {
        return this.requestParamType;
    }

    public void setRequestParamType(String requestParamType) {
        this.requestParamType = requestParamType;
    }

    public long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getLastHttpStatusCode() {
        return this.lastHttpStatusCode;
    }

    public void setLastHttpStatusCode(int lastHttpStatusCode) {
        this.lastHttpStatusCode = lastHttpStatusCode;
    }

    public long getLastConnectTime() {
        return this.lastConnectTime;
    }

    public void setLastConnectTime(long lastConnectTime) {
        this.lastConnectTime = lastConnectTime;
    }

    public int getRetryCount() {
        return this.retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
}
