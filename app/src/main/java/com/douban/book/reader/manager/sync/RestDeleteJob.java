package com.douban.book.reader.manager.sync;

import com.douban.book.reader.entity.Bookmark.Column;
import com.douban.book.reader.network.client.RestClient;
import com.douban.book.reader.network.exception.RestException;
import com.douban.book.reader.network.param.RequestParam;
import java.util.Date;

public class RestDeleteJob<T> extends BaseRestJob<T> {
    private Object mId;
    private Date mUpdateTime;

    public RestDeleteJob(String restPath, Class<T> type, Object id, Date updateTime) {
        super(restPath, type);
        this.mId = id;
        this.mUpdateTime = updateTime;
    }

    protected void doRestRequest(RestClient<T> restClient) throws RestException {
        restClient.delete(this.mId, RequestParam.queryString().append(Column.UPDATE_TIME, this.mUpdateTime));
    }
}
