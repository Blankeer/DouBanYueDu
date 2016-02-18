package com.douban.book.reader.manager.sync;

import com.douban.book.reader.network.client.RestClient;
import com.douban.book.reader.network.exception.RestException;
import com.douban.book.reader.util.JsonUtils;

public class RestPostJob<T> extends BaseRestJob<T> {
    private String mEntityJson;

    public RestPostJob(String restPath, Class<T> type, T entity) {
        super(restPath, type);
        this.mEntityJson = JsonUtils.toJson(entity);
    }

    protected void doRestRequest(RestClient<T> restClient) throws RestException {
        restClient.post(JsonUtils.fromJson(this.mEntityJson, getType()));
    }
}
