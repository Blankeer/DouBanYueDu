package com.douban.book.reader.fragment;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.UserInfo;
import com.douban.book.reader.event.LoggedOutEvent;
import com.douban.book.reader.event.UserInfoUpdatedEvent;
import com.douban.book.reader.fragment.UserProfileEditFragment.Action;
import com.douban.book.reader.helper.LogoutHelper;
import com.douban.book.reader.helper.SelectPhotoHelper;
import com.douban.book.reader.helper.SelectPhotoHelper.OnPhotoSelectedListener;
import com.douban.book.reader.helper.StoreUriHelper;
import com.douban.book.reader.manager.AccountManager;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.util.ExceptionUtils;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.view.UserAvatarView;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

@EFragment(2130903111)
public class UserProfileFragment extends BaseFragment implements OnPhotoSelectedListener {
    @Bean
    AccountManager mAccountManager;
    @ViewById(2131558658)
    TextView mBtnEditAvatar;
    @ViewById(2131558660)
    TextView mBtnEditNickname;
    private SelectPhotoHelper mSelectPhotoHelper;
    @ViewById(2131558662)
    TextView mTvAgreement;
    @ViewById(2131558535)
    UserAvatarView mUserAvatar;
    @Bean
    UserManager mUserManager;
    @ViewById(2131558659)
    TextView mUserName;

    @AfterViews
    void init() {
        setTitle((int) R.string.title_user_profile);
        this.mBtnEditAvatar.setText(RichText.singleIcon(R.drawable.v_camera));
        this.mBtnEditNickname.setText(RichText.singleIcon(R.drawable.v_edit));
        this.mTvAgreement.setText(RichText.format((int) R.string.text_account_acknowledgement, RichText.linkify(R.string.text_account_acknowledgement_goto_douban, StoreUriHelper.myProfile())));
        this.mTvAgreement.setMovementMethod(LinkMovementMethod.getInstance());
        this.mSelectPhotoHelper = new SelectPhotoHelper(this, this);
        updateUserInfo();
    }

    public void onEventMainThread(UserInfoUpdatedEvent event) {
        updateUserInfo();
    }

    public void onEventMainThread(LoggedOutEvent event) {
        finish();
    }

    @Click({2131558658})
    void onBtnEditAvatarClicked(View view) {
        this.mSelectPhotoHelper.performSelect(view);
    }

    @Click({2131558660})
    void onBtnEditNicknameClicked() {
        UserProfileEditFragment_.builder().action(Action.EDIT_NICKNAME).build().showAsActivity((Fragment) this);
    }

    @Click({2131558661})
    void onBtnLogoutClicked() {
        new LogoutHelper(this).logout();
    }

    public void onPhotoSelected(Bitmap bitmap) {
        if (bitmap != null) {
            uploadAvatar(bitmap);
            this.mUserAvatar.setImageBitmap(bitmap);
        }
    }

    @Background
    void uploadAvatar(Bitmap bitmap) {
        Throwable e;
        try {
            showBlockingLoadingDialog();
            this.mAccountManager.uploadAvatar(bitmap);
            this.mUserManager.getCurrentUserFromServer();
            ToastUtils.showToast((int) R.string.toast_avatar_edit_complete);
            dismissLoadingDialog();
        } catch (Throwable e2) {
            e = e2;
            try {
                ToastUtils.showToast(ExceptionUtils.getHumanReadableMessage(e, (int) R.string.toast_general_load_failed));
                updateUserInfo();
            } finally {
                dismissLoadingDialog();
            }
        } catch (Throwable e22) {
            e = e22;
            ToastUtils.showToast(ExceptionUtils.getHumanReadableMessage(e, (int) R.string.toast_general_load_failed));
            updateUserInfo();
        }
    }

    @UiThread
    void updateUserInfo() {
        UserInfo userInfo = this.mUserManager.getUserInfo();
        this.mUserAvatar.displayUserAvatar(userInfo);
        if (userInfo != null) {
            this.mUserName.setText(userInfo.name);
        }
    }
}
