package com.douban.book.reader.helper;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.support.annotation.Nullable;
import com.crashlytics.android.Crashlytics;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.entity.Session;
import com.douban.book.reader.event.ArkEvent;
import com.douban.book.reader.event.ArkRequest;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.fragment.AlertDialogFragment.Builder;
import com.douban.book.reader.fragment.BaseFragment;
import com.douban.book.reader.job.JobUtils;
import com.douban.book.reader.job.MergeAnonymousDataJob;
import com.douban.book.reader.manager.SessionManager;
import com.douban.book.reader.manager.SessionManager.DeviceIdSessionRetriever;
import com.douban.book.reader.manager.SessionManager.DirectSessionRetriever;
import com.douban.book.reader.manager.SessionManager.OpenIdSessionRetriever;
import com.douban.book.reader.manager.SessionManager.PasswordSessionRetriever;
import com.douban.book.reader.manager.ShelfManager;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.Logger;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

@EBean
public class LoginHelper {
    private static final String TAG;
    private BaseFragment mFragment;
    private Intent mIntentToStartAfterLogin;
    private ArkRequest mRequestToSendAfterLogin;
    @Bean
    SessionManager mSessionManager;
    @Bean
    ShelfManager mShelfManager;
    @Bean
    UserManager mUserManager;

    /* renamed from: com.douban.book.reader.helper.LoginHelper.2 */
    class AnonymousClass2 implements OnClickListener {
        final /* synthetic */ String val$lastAccessToken;

        AnonymousClass2(String str) {
            this.val$lastAccessToken = str;
        }

        public void onClick(DialogInterface dialog, int which) {
            LoginHelper.this.migrateLastUserDataAndRedirect(this.val$lastAccessToken);
        }
    }

    static {
        TAG = LoginHelper.class.getSimpleName();
    }

    public void init(BaseFragment fragment, @Nullable Intent intentToStartAfterLogin, @Nullable ArkRequest requestToSendAfterLogin) {
        this.mFragment = fragment;
        this.mIntentToStartAfterLogin = intentToStartAfterLogin;
        this.mRequestToSendAfterLogin = requestToSendAfterLogin;
    }

    public void loginWithPassword(String username, String password) {
        performLogin(new PasswordSessionRetriever(username, password));
    }

    public void loginWithSession(Session session) {
        performLogin(new DirectSessionRetriever(session));
    }

    public void loginWithOpenId(int openIdType, String openId, String openIdAccessToken) {
        performLogin(new OpenIdSessionRetriever(openIdType, openId, openIdAccessToken));
    }

    public void loginWithDevice() {
        performLogin(new DeviceIdSessionRetriever());
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @org.androidannotations.annotations.Background
    void performLogin(com.douban.book.reader.manager.SessionManager.SessionRetriever r9) {
        /*
        r8 = this;
        r6 = 1;
        r2 = 0;
        r7 = r8.mFragment;
        r7.showBlockingLoadingDialog();
        r7 = r8.mUserManager;	 Catch:{ RestException -> 0x0049 }
        r5 = r7.isAnonymousUser();	 Catch:{ RestException -> 0x0049 }
        r7 = r8.mSessionManager;	 Catch:{ RestException -> 0x0049 }
        r1 = r7.getAccessToken();	 Catch:{ RestException -> 0x0049 }
        r7 = r8.mUserManager;	 Catch:{ RestException -> 0x0049 }
        r7.login(r9);	 Catch:{ RestException -> 0x0049 }
        r7 = r9 instanceof com.douban.book.reader.manager.SessionManager.PasswordSessionRetriever;	 Catch:{ RestException -> 0x0049 }
        if (r7 != 0) goto L_0x0020;
    L_0x001c:
        r7 = r9 instanceof com.douban.book.reader.manager.SessionManager.OpenIdSessionRetriever;	 Catch:{ RestException -> 0x0049 }
        if (r7 == 0) goto L_0x0043;
    L_0x0020:
        r3 = r6;
    L_0x0021:
        r4 = r9 instanceof com.douban.book.reader.manager.SessionManager.DirectSessionRetriever;	 Catch:{ RestException -> 0x0049 }
        if (r3 != 0) goto L_0x0027;
    L_0x0025:
        if (r4 == 0) goto L_0x0028;
    L_0x0027:
        r2 = r6;
    L_0x0028:
        if (r2 == 0) goto L_0x005a;
    L_0x002a:
        if (r5 == 0) goto L_0x005a;
    L_0x002c:
        r6 = 1;
        r6 = new java.lang.CharSequence[r6];	 Catch:{ RestException -> 0x0049 }
        r7 = 0;
        r6[r7] = r1;	 Catch:{ RestException -> 0x0049 }
        r6 = com.douban.book.reader.util.StringUtils.isNotEmpty(r6);	 Catch:{ RestException -> 0x0049 }
        if (r6 == 0) goto L_0x005a;
    L_0x0038:
        if (r3 == 0) goto L_0x0045;
    L_0x003a:
        r8.confirmMigrate(r1);	 Catch:{ RestException -> 0x0049 }
    L_0x003d:
        r6 = r8.mFragment;
        r6.dismissLoadingDialog();
    L_0x0042:
        return;
    L_0x0043:
        r3 = r2;
        goto L_0x0021;
    L_0x0045:
        r8.migrateLastUserDataAndRedirect(r1);	 Catch:{ RestException -> 0x0049 }
        goto L_0x003d;
    L_0x0049:
        r0 = move-exception;
        r6 = 2131099841; // 0x7f0600c1 float:1.7812047E38 double:1.0529032193E-314;
        r6 = com.douban.book.reader.util.ExceptionUtils.getHumanReadableMessage(r0, r6);	 Catch:{ all -> 0x005e }
        com.douban.book.reader.util.ToastUtils.showToast(r6);	 Catch:{ all -> 0x005e }
        r6 = r8.mFragment;
        r6.dismissLoadingDialog();
        goto L_0x0042;
    L_0x005a:
        r8.postLogin();	 Catch:{ RestException -> 0x0049 }
        goto L_0x003d;
    L_0x005e:
        r6 = move-exception;
        r7 = r8.mFragment;
        r7.dismissLoadingDialog();
        throw r6;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.douban.book.reader.helper.LoginHelper.performLogin(com.douban.book.reader.manager.SessionManager$SessionRetriever):void");
    }

    @UiThread
    void confirmMigrate(String lastAccessToken) {
        new Builder().setMessage((int) R.string.dialog_message_merge_anonymous_data).setPositiveButton((int) R.string.dialog_button_merge_anonymous_data, new AnonymousClass2(lastAccessToken)).setNegativeButton((int) R.string.dialog_button_discard_anonymous_data, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                LoginHelper.this.clearLastUserDataAndRedirect();
            }
        }).setCancelable(false).setCanceledOnTouchOutside(false).create().show();
    }

    @Background
    void migrateLastUserDataAndRedirect(String lastAccessToken) {
        this.mFragment.showBlockingLoadingDialog();
        JobUtils.runOrSchedule(new MergeAnonymousDataJob(lastAccessToken));
        refreshShelfItems();
        this.mFragment.dismissLoadingDialog();
        postLogin();
    }

    @Background
    void clearLastUserDataAndRedirect() {
        this.mFragment.showBlockingLoadingDialog();
        try {
            this.mShelfManager.clear();
        } catch (DataLoadException e) {
            Logger.e(TAG, e);
        } catch (Throwable th) {
            this.mFragment.dismissLoadingDialog();
            postLogin();
        }
        App.get().clearContents();
        this.mFragment.dismissLoadingDialog();
        postLogin();
    }

    @Background
    void refreshShelfItems() {
        try {
            this.mShelfManager.refreshShelfItems();
        } catch (DataLoadException e) {
            Logger.e(TAG, e);
        }
    }

    @UiThread
    void postLogin() {
        try {
            if (this.mIntentToStartAfterLogin != null) {
                PageOpenHelper.from(this.mFragment).open(this.mIntentToStartAfterLogin);
            }
            if (this.mRequestToSendAfterLogin != null) {
                EventBusUtils.post(this.mRequestToSendAfterLogin);
            }
        } catch (Exception e) {
            Logger.e(TAG, e);
            Crashlytics.logException(e);
        }
        this.mFragment.finish();
        EventBusUtils.post(ArkEvent.LOGIN_COMPLETED);
    }
}
