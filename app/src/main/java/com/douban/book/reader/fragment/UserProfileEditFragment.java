package com.douban.book.reader.fragment;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.UserInfo;
import com.douban.book.reader.helper.SelectPhotoHelper;
import com.douban.book.reader.helper.SelectPhotoHelper.OnPhotoSelectedListener;
import com.douban.book.reader.manager.AccountManager;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.util.ExceptionUtils;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.UserAvatarView;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.EditorAction;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;

@EFragment(2130903112)
public class UserProfileEditFragment extends BaseFragment implements OnPhotoSelectedListener {
    @FragmentArg
    Action action;
    @Bean
    AccountManager mAccountManager;
    @ViewById(2131558665)
    Button mBtnConfirm;
    @ViewById(2131558664)
    EditText mEdtNickname;
    @ViewById(2131558663)
    View mLayoutAvatar;
    private Bitmap mNewUserAvatar;
    private SelectPhotoHelper mSelectPhotoHelper;
    @ViewById(2131558535)
    UserAvatarView mUserAvatar;
    @Bean
    UserManager mUserManager;

    public enum Action {
        EDIT_NICKNAME
    }

    @AfterViews
    void init() {
        if (this.action == Action.EDIT_NICKNAME) {
            setTitle((int) R.string.title_nickname_edit);
            ViewUtils.gone(this.mLayoutAvatar);
            UserInfo userInfo = this.mUserManager.getUserInfo();
            if (userInfo != null) {
                this.mEdtNickname.setText(userInfo.name);
                this.mEdtNickname.selectAll();
                return;
            }
            return;
        }
        setTitle((int) R.string.title_complete_user_profile);
        this.mUserAvatar.showEditButtonWhenEmpty();
        this.mSelectPhotoHelper = new SelectPhotoHelper(this, this);
    }

    @TextChange({2131558664})
    void onEditTextChange() {
        ViewUtils.enableIf(StringUtils.isNotEmpty(this.mEdtNickname.getText()), this.mBtnConfirm);
    }

    @Click({2131558535})
    void onEditAvatarClicked(View view) {
        this.mSelectPhotoHelper.performSelect(view);
    }

    @Click({2131558665})
    @EditorAction({2131558664})
    void onBtnConfirmClicked() {
        setNickname();
    }

    public void onPhotoSelected(Bitmap bitmap) {
        if (bitmap != null) {
            this.mNewUserAvatar = bitmap;
            this.mUserAvatar.setImageBitmap(bitmap);
        }
    }

    @Background
    void setNickname() {
        Throwable e;
        try {
            showBlockingLoadingDialog();
            if (this.mNewUserAvatar != null) {
                this.mAccountManager.uploadAvatar(this.mNewUserAvatar);
            }
            this.mAccountManager.setNickname(this.mEdtNickname.getText());
            this.mUserManager.getCurrentUserFromServer();
            ToastUtils.showToast((int) R.string.toast_user_profile_edit_complete);
            finish();
            dismissLoadingDialog();
        } catch (Throwable e2) {
            e = e2;
            try {
                ToastUtils.showToast(ExceptionUtils.getHumanReadableMessage(e, (int) R.string.toast_general_load_failed));
            } finally {
                dismissLoadingDialog();
            }
        } catch (Throwable e22) {
            e = e22;
            ToastUtils.showToast(ExceptionUtils.getHumanReadableMessage(e, (int) R.string.toast_general_load_failed));
        }
    }
}
