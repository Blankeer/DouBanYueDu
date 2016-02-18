package com.douban.book.reader.manager.sync;

import android.net.Uri;
import com.douban.book.reader.network.client.RestClient;
import com.douban.book.reader.network.exception.RestException;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

public abstract class BaseRestJob<T> extends Job {
    protected String TAG;
    private String mRestUri;
    private Class<T> mType;

    protected abstract void doRestRequest(RestClient<T> restClient) throws RestException;

    public BaseRestJob(String restUri, Class<T> type) {
        super(new Params(1).requireNetwork().persist());
        this.TAG = getClass().getSimpleName();
        this.mRestUri = restUri;
        this.mType = type;
    }

    public void onAdded() {
    }

    public void onRun() throws Throwable {
        doRestRequest(new RestClient(Uri.parse(this.mRestUri), this.mType));
    }

    protected void onCancel() {
    }

    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }

    protected Class<T> getType() {
        return this.mType;
    }
}
