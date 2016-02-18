package com.douban.book.reader.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.entity.Annotation;
import com.douban.book.reader.entity.Annotation.Privacy;
import com.douban.book.reader.event.ArkRequest;
import com.douban.book.reader.fragment.AlertDialogFragment.Builder;
import com.douban.book.reader.fragment.LoginFragment_;
import com.douban.book.reader.lib.view.LockView;
import com.douban.book.reader.lib.view.LockView.StatusChangeChecker;
import com.douban.book.reader.lib.view.LockView.UnlockChecker;
import com.douban.book.reader.manager.AnnotationManager;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.util.ExceptionUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Pref;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.util.ViewUtils;
import java.util.UUID;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903170)
public class NotePrivacyInfoView extends LinearLayout implements UnlockChecker, StatusChangeChecker {
    private static final String TAG;
    private Annotation mAnnotation;
    @Bean
    AnnotationManager mAnnotationManager;
    @ViewById(2131558882)
    LockView mLockView;
    @ViewById(2131558883)
    TextView mPrivacyInfo;
    @ViewById(2131558884)
    View mShareTarget;
    private boolean mShouldChangePrivacyForRemote;
    private boolean mShowShareTarget;
    @Bean
    UserManager mUserManager;

    static {
        TAG = NotePrivacyInfoView.class.getSimpleName();
    }

    public NotePrivacyInfoView(Context context) {
        super(context);
        this.mShouldChangePrivacyForRemote = true;
    }

    public NotePrivacyInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mShouldChangePrivacyForRemote = true;
    }

    public NotePrivacyInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mShouldChangePrivacyForRemote = true;
    }

    @AfterViews
    void init() {
        this.mLockView.setUnlockChecker(this);
        this.mLockView.setStatusChangeChecker(this);
        ViewUtils.setEventAware(this);
    }

    public void setData(Annotation annotation) {
        this.mAnnotation = annotation;
        updateView();
    }

    public void setShowShareTarget(boolean showShareTarget) {
        this.mShowShareTarget = showShareTarget;
        updateView();
    }

    public void setShouldChangePrivacyForRemote(boolean shouldChangePrivacyForRemote) {
        this.mShouldChangePrivacyForRemote = shouldChangePrivacyForRemote;
    }

    public String getPrivacy() {
        return this.mLockView.isLocked() ? Privacy.PRIVATE : Privacy.PUBLIC;
    }

    @Click({2131558882})
    void onLockClicked() {
    }

    @CheckedChange({2131558882})
    void onLockChanged(boolean isLocked) {
        if (!this.mShouldChangePrivacyForRemote) {
            this.mAnnotation.setIsPrivate(isLocked);
            updateView();
            Pref.ofApp().set(Key.APP_CREATE_NOTE_AS_PRIVATE, Boolean.valueOf(isLocked));
        }
    }

    @Background
    void updatePrivacyToServer() {
        boolean z = false;
        if (this.mAnnotation != null) {
            try {
                setLockEnabled(false);
                updatePrivateInfoText(R.string.dialog_msg_loading);
                AnnotationManager annotationManager = this.mAnnotationManager;
                UUID uuid = this.mAnnotation.uuid;
                if (!this.mLockView.isLocked()) {
                    z = true;
                }
                this.mAnnotation = annotationManager.setIsPrivate(uuid, z);
            } catch (Throwable e) {
                Logger.e(TAG, e);
                ToastUtils.showToast((View) this, ExceptionUtils.getHumanReadableMessage(e, (int) R.string.toast_general_op_failed));
            } finally {
                updateView();
            }
        }
    }

    @UiThread
    void updateView() {
        if (this.mAnnotation != null) {
            boolean isPrivate = this.mAnnotation.isPrivate();
            this.mLockView.setLocked(isPrivate);
            updatePrivateInfoText(isPrivate ? R.string.text_note_privacy_info_private : R.string.text_note_privacy_info_public);
            if (this.mShowShareTarget) {
                boolean z;
                if (isPrivate) {
                    z = false;
                } else {
                    z = true;
                }
                ViewUtils.visibleIf(z, this.mShareTarget);
                ViewUtils.visibleIf(isPrivate, this.mPrivacyInfo);
            }
            setLockEnabled(true);
        }
    }

    @UiThread
    void setLockEnabled(boolean enabled) {
        this.mLockView.setEnabled(enabled);
    }

    @UiThread
    void updatePrivateInfoText(@StringRes int resId) {
        this.mPrivacyInfo.setText(resId);
    }

    public boolean canUnlock() {
        if (!this.mUserManager.isAnonymousUser()) {
            return true;
        }
        new Builder().setMessage((int) R.string.dialog_message_login_required_to_make_note_public).setPositiveButton((int) R.string.dialog_button_login, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                LoginFragment_.builder().requestToSendAfterLogin(ArkRequest.READER_CHANGE_NOTE_PRIVACY).build().showAsActivity(NotePrivacyInfoView.this);
            }
        }).setNegativeButton((int) R.string.dialog_button_cancel, null).create().show();
        return false;
    }

    public boolean canChangeStatus() {
        if (!this.mShouldChangePrivacyForRemote) {
            return true;
        }
        updatePrivacyToServer();
        return false;
    }

    public void onEventMainThread(ArkRequest request) {
        if (request == ArkRequest.READER_CHANGE_NOTE_PRIVACY && this.mLockView != null) {
            this.mLockView.performClick();
        }
    }
}
