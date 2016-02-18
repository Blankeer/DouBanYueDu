package com.crashlytics.android.core;

import android.os.AsyncTask;
import com.tencent.connect.common.Constants;
import io.fabric.sdk.android.Fabric;

public class CrashTest {

    /* renamed from: com.crashlytics.android.core.CrashTest.1 */
    class AnonymousClass1 extends AsyncTask<Void, Void, Void> {
        final /* synthetic */ long val$delayMs;

        AnonymousClass1(long j) {
            this.val$delayMs = j;
        }

        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(this.val$delayMs);
            } catch (InterruptedException e) {
            }
            CrashTest.this.throwRuntimeException("Background thread crash");
            return null;
        }
    }

    public void throwRuntimeException(String message) {
        throw new RuntimeException(message);
    }

    public int stackOverflow() {
        return stackOverflow() + ((int) Math.random());
    }

    public void indexOutOfBounds() {
        Fabric.getLogger().d(CrashlyticsCore.TAG, "Out of bounds value: " + new int[2][10]);
    }

    public void crashAsyncTask(long delayMs) {
        new AnonymousClass1(delayMs).execute(new Void[]{(Void) null});
    }

    public void throwFiveChainedExceptions() {
        try {
            privateMethodThatThrowsException(Constants.VIA_TO_TYPE_QQ_GROUP);
        } catch (Exception ex) {
            throw new RuntimeException(Constants.VIA_SSO_LOGIN, ex);
        } catch (Exception ex2) {
            try {
                throw new RuntimeException(Constants.VIA_TO_TYPE_QQ_DISCUSS_GROUP, ex2);
            } catch (Exception ex22) {
                try {
                    throw new RuntimeException(Constants.VIA_TO_TYPE_QZONE, ex22);
                } catch (Exception ex222) {
                    throw new RuntimeException(Constants.VIA_SHARE_TYPE_TEXT, ex222);
                }
            }
        }
    }

    private void privateMethodThatThrowsException(String message) {
        throw new RuntimeException(message);
    }
}
