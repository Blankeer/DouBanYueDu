package com.douban.book.reader.job;

import com.douban.amonsul.network.NetWorker;
import com.douban.book.reader.entity.DummyEntity;
import com.douban.book.reader.network.client.RestClient;
import com.douban.book.reader.network.param.RequestParam;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

public class MergeAnonymousDataJob extends Job {
    private final String mAccessTokenToMigrate;

    public MergeAnonymousDataJob(String accessTokenToMigrate) {
        super(new Params(1).requireNetwork().persist());
        this.mAccessTokenToMigrate = accessTokenToMigrate;
    }

    public void onAdded() {
    }

    public void onRun() throws Throwable {
        new RestClient("account/migrate", DummyEntity.class).post(RequestParam.json().append(NetWorker.PARAM_KEY_TOKEN, this.mAccessTokenToMigrate));
    }

    protected void onCancel() {
    }

    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}
