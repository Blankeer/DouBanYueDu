package com.j256.ormlite.android.apptools;

import android.app.Service;
import android.content.Context;
import com.j256.ormlite.support.ConnectionSource;

public abstract class OrmLiteBaseService<H extends OrmLiteSqliteOpenHelper> extends Service {
    private volatile boolean created;
    private volatile boolean destroyed;
    private volatile H helper;

    public OrmLiteBaseService() {
        this.created = false;
        this.destroyed = false;
    }

    public H getHelper() {
        if (this.helper != null) {
            return this.helper;
        }
        if (!this.created) {
            throw new IllegalStateException("A call has not been made to onCreate() yet so the helper is null");
        } else if (this.destroyed) {
            throw new IllegalStateException("A call to onDestroy has already been made and the helper cannot be used after that point");
        } else {
            throw new IllegalStateException("Helper is null for some unknown reason");
        }
    }

    public ConnectionSource getConnectionSource() {
        return getHelper().getConnectionSource();
    }

    public void onCreate() {
        if (this.helper == null) {
            this.helper = getHelperInternal(this);
            this.created = true;
        }
        super.onCreate();
    }

    public void onDestroy() {
        super.onDestroy();
        releaseHelper(this.helper);
        this.destroyed = true;
    }

    protected H getHelperInternal(Context context) {
        return OpenHelperManager.getHelper(context);
    }

    protected void releaseHelper(H h) {
        OpenHelperManager.releaseHelper();
        this.helper = null;
    }
}
