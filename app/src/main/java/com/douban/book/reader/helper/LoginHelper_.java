package com.douban.book.reader.helper;

import android.content.Context;
import com.douban.book.reader.manager.SessionManager.SessionRetriever;
import com.douban.book.reader.manager.SessionManager_;
import com.douban.book.reader.manager.ShelfManager_;
import com.douban.book.reader.manager.UserManager_;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;

public final class LoginHelper_ extends LoginHelper {
    private Context context_;

    /* renamed from: com.douban.book.reader.helper.LoginHelper_.1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ String val$lastAccessToken;

        AnonymousClass1(String str) {
            this.val$lastAccessToken = str;
        }

        public void run() {
            super.confirmMigrate(this.val$lastAccessToken);
        }
    }

    /* renamed from: com.douban.book.reader.helper.LoginHelper_.3 */
    class AnonymousClass3 extends Task {
        final /* synthetic */ SessionRetriever val$sessionRetriever;

        AnonymousClass3(String x0, long x1, String x2, SessionRetriever sessionRetriever) {
            this.val$sessionRetriever = sessionRetriever;
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.performLogin(this.val$sessionRetriever);
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    /* renamed from: com.douban.book.reader.helper.LoginHelper_.4 */
    class AnonymousClass4 extends Task {
        final /* synthetic */ String val$lastAccessToken;

        AnonymousClass4(String x0, long x1, String x2, String str) {
            this.val$lastAccessToken = str;
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.migrateLastUserDataAndRedirect(this.val$lastAccessToken);
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    /* renamed from: com.douban.book.reader.helper.LoginHelper_.5 */
    class AnonymousClass5 extends Task {
        AnonymousClass5(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.clearLastUserDataAndRedirect();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    /* renamed from: com.douban.book.reader.helper.LoginHelper_.6 */
    class AnonymousClass6 extends Task {
        AnonymousClass6(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.refreshShelfItems();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    private LoginHelper_(Context context) {
        this.context_ = context;
        init_();
    }

    public static LoginHelper_ getInstance_(Context context) {
        return new LoginHelper_(context);
    }

    private void init_() {
        this.mUserManager = UserManager_.getInstance_(this.context_);
        this.mSessionManager = SessionManager_.getInstance_(this.context_);
        this.mShelfManager = ShelfManager_.getInstance_(this.context_);
    }

    public void rebind(Context context) {
        this.context_ = context;
        init_();
    }

    void confirmMigrate(String lastAccessToken) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass1(lastAccessToken), 0);
    }

    void postLogin() {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new Runnable() {
            public void run() {
                super.postLogin();
            }
        }, 0);
    }

    void performLogin(SessionRetriever sessionRetriever) {
        BackgroundExecutor.execute(new AnonymousClass3(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE, sessionRetriever));
    }

    void migrateLastUserDataAndRedirect(String lastAccessToken) {
        BackgroundExecutor.execute(new AnonymousClass4(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE, lastAccessToken));
    }

    void clearLastUserDataAndRedirect() {
        BackgroundExecutor.execute(new AnonymousClass5(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }

    void refreshShelfItems() {
        BackgroundExecutor.execute(new AnonymousClass6(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }
}
