package com.douban.book.reader.helper;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import com.douban.book.reader.R;
import com.douban.book.reader.activity.HomeActivity;
import com.douban.book.reader.app.App;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.controller.TaskController;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.event.LoggedOutEvent;
import com.douban.book.reader.fragment.AlertDialogFragment.Builder;
import com.douban.book.reader.fragment.BaseFragment;
import com.douban.book.reader.job.JobUtils;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.util.Analysis;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.util.Utils;

public class LogoutHelper {
    private static final String TAG;
    private BaseFragment mFragment;

    /* renamed from: com.douban.book.reader.helper.LogoutHelper.1 */
    class AnonymousClass1 implements OnClickListener {
        final /* synthetic */ boolean val$isNetworkAvailable;
        final /* synthetic */ boolean val$needSyncUGC;

        AnonymousClass1(boolean z, boolean z2) {
            this.val$needSyncUGC = z;
            this.val$isNetworkAvailable = z2;
        }

        public void onClick(DialogInterface dialog, int which) {
            LogoutHelper.this.mFragment.showBlockingLoadingDialog();
            if (!this.val$needSyncUGC || this.val$isNetworkAvailable) {
                LogoutHelper.this.performLogout();
            } else {
                LogoutHelper.this.performLogout();
            }
        }
    }

    static {
        TAG = LogoutHelper.class.getSimpleName();
    }

    public LogoutHelper(BaseFragment fragment) {
        this.mFragment = fragment;
    }

    public void logout() {
        boolean isNetworkAvailable = Utils.isNetworkAvailable();
        boolean needSyncUGC = JobUtils.hasJobs();
        Builder dialog = new Builder();
        dialog.setMessage((int) R.string.dialog_msg_logout_confirm);
        dialog.setPositiveButton((int) R.string.dialog_button_ok, new AnonymousClass1(needSyncUGC, isNetworkAvailable));
        dialog.setNegativeButton((int) R.string.dialog_button_cancel, null);
        dialog.create().show();
    }

    private void showLogoutAfterSyncFailedDialog() {
        Builder dialog = new Builder();
        dialog.setTitle((int) R.string.dialog_title_logout_confirm);
        dialog.setIcon(17301659);
        dialog.setMessage((int) R.string.dialog_msg_logout_after_sync_failed);
        dialog.setPositiveButton((int) R.string.dialog_button_ok, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                LogoutHelper.this.mFragment.showLoadingDialog();
                LogoutHelper.this.performLogout();
            }
        });
        dialog.setNegativeButton((int) R.string.dialog_button_cancel, null);
        try {
            dialog.create().show();
        } catch (Throwable e) {
            Logger.e(TAG, e);
            ToastUtils.showToast((int) R.string.toast_logout_failed);
        }
    }

    private void performLogout() {
        TaskController.run(new Runnable() {
            public void run() {
                LogoutHelper.this.mFragment.showBlockingLoadingDialog();
                App.get().clearContents();
                LogoutHelper.this.doLogout();
                App.get().finishAllActivities();
                LogoutHelper.this.mFragment.dismissLoadingDialog();
                App.get().runOnUiThread(new Runnable() {
                    public void run() {
                        HomeActivity.showHomeEnsuringLogin(PageOpenHelper.from(LogoutHelper.this.mFragment));
                    }
                });
            }
        });
    }

    public void doLogout() {
        UserManager.getInstance().logout();
        Analysis.sendEvent("logged_out");
        Analysis.onLogout(App.get());
        EventBusUtils.post(new LoggedOutEvent());
    }
}
