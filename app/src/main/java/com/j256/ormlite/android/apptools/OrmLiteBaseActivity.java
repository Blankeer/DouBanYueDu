package com.j256.ormlite.android.apptools;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.support.ConnectionSource;

public abstract class OrmLiteBaseActivity<H extends OrmLiteSqliteOpenHelper> extends Activity {
    private static Logger logger;
    private volatile boolean created;
    private volatile boolean destroyed;
    private volatile H helper;

    public OrmLiteBaseActivity() {
        this.created = false;
        this.destroyed = false;
    }

    static {
        logger = LoggerFactory.getLogger(OrmLiteBaseActivity.class);
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

    protected void onCreate(Bundle savedInstanceState) {
        if (this.helper == null) {
            this.helper = getHelperInternal(this);
            this.created = true;
        }
        super.onCreate(savedInstanceState);
    }

    protected void onDestroy() {
        super.onDestroy();
        releaseHelper(this.helper);
        this.destroyed = true;
    }

    protected H getHelperInternal(Context context) {
        Object newHelper = OpenHelperManager.getHelper(context);
        logger.trace("{}: got new helper {} from OpenHelperManager", (Object) this, newHelper);
        return newHelper;
    }

    protected void releaseHelper(H helper) {
        OpenHelperManager.releaseHelper();
        logger.trace("{}: helper {} was released, set to null", (Object) this, (Object) helper);
        this.helper = null;
    }

    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(super.hashCode());
    }
}
