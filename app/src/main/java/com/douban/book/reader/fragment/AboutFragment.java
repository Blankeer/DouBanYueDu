package com.douban.book.reader.fragment;

import android.support.v4.app.Fragment;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.constant.Char;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.manager.VersionManager;
import com.douban.book.reader.util.AppInfo;
import com.douban.book.reader.util.ClipboardUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Pref;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ToastUtils;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(2130903089)
public class AboutFragment extends BaseFragment {
    private int mClickedCount;
    private boolean mShowBuild;
    @ViewById(2131558571)
    TextView mTextUid;
    @ViewById(2131558570)
    TextView mTextVersion;
    @Bean
    UserManager mUserManager;
    @Bean
    VersionManager mVersionManager;

    public AboutFragment() {
        this.mClickedCount = 0;
        this.mShowBuild = false;
    }

    @AfterViews
    void init() {
        setTitle((int) R.string.title_about);
        updateVersionText();
        this.mTextUid.setText(Res.getString(R.string.text_uid, Integer.valueOf(this.mUserManager.getUserId())));
    }

    @Click({2131558570})
    void onVersionClicked() {
        this.mShowBuild = !this.mShowBuild;
        updateVersionText();
    }

    @Click({2131558571})
    void onUidClicked() {
        ClipboardUtils.copy(String.valueOf(this.mUserManager.getUserId()));
        ToastUtils.showToast((Fragment) this, (int) R.string.toast_user_id_copied);
    }

    @Background
    @Click({2131558573})
    void onBtnCheckUpdateClicked() {
        try {
            showBlockingLoadingDialog();
            if (this.mVersionManager.isNewVersionAvailable()) {
                this.mVersionManager.promptAppUpdate();
            } else {
                ToastUtils.showToast((Fragment) this, (int) R.string.toast_no_update_available);
            }
            dismissLoadingDialog();
        } catch (Throwable th) {
            dismissLoadingDialog();
        }
    }

    @Click({2131558572})
    void onBtnChangeLogClicked() {
        ChangeLogFragment_.builder().build().showAsActivity((Fragment) this);
    }

    @Click({2131558574})
    void onCopyrightClicked() {
        int i = this.mClickedCount;
        this.mClickedCount = i + 1;
        if (i > 5) {
            Logger.setLogEnabled(true);
            Pref.ofApp().set(Key.APP_DEBUG_PRINT_NETWORK_RESPONSE, Boolean.valueOf(true));
            Logger.d(this.TAG, "--- LOG ENABLED ---", new Object[0]);
        }
    }

    private void updateVersionText() {
        StringBuilder builder = new StringBuilder().append("version ").append(AppInfo.getVersionName());
        if (this.mShowBuild) {
            builder.append(Char.SPACE).append(Char.LEFT_PARENTHESIS).append(AppInfo.getBuild()).append(Char.RIGHT_PARENTHESIS);
        }
        this.mTextVersion.setText(builder);
    }
}
