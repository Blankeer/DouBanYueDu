package com.douban.book.reader.fragment;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.entity.Annotation;
import com.douban.book.reader.entity.UserInfo;
import com.douban.book.reader.event.ActiveNoteChangedEvent;
import com.douban.book.reader.event.AnnotationUpdatedEvent;
import com.douban.book.reader.fragment.AlertDialogFragment.Builder;
import com.douban.book.reader.fragment.share.ShareNoteEditFragment_;
import com.douban.book.reader.manager.AnnotationManager;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.manager.exception.NoSuchDataException;
import com.douban.book.reader.util.DateUtils;
import com.douban.book.reader.util.DebugSwitch;
import com.douban.book.reader.util.ExceptionUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.LoadErrorPageView.RefreshClickListener;
import com.douban.book.reader.view.NoteNavigationView;
import com.douban.book.reader.view.NotePrivacyInfoView;
import com.douban.book.reader.view.ParagraphView;
import com.douban.book.reader.view.ParagraphView.Indent;
import com.douban.book.reader.view.UserAvatarView;
import java.util.UUID;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

@EFragment(2130903106)
@OptionsMenu({2131623944, 2131623937, 2131623936})
public class NoteDetailFragment extends BaseFragment {
    @FragmentArg
    String idOrUuid;
    @Bean
    AnnotationManager mAnnotationManager;
    @ViewById(2131558636)
    View mDividerUnderPrivacyView;
    @ViewById(2131558637)
    ViewGroup mLoadErrorViewBase;
    @ViewById(2131558634)
    TextView mNoteCreatedDate;
    @ViewById(2131558632)
    ParagraphView mNoteDetail;
    @ViewById(2131558639)
    NoteNavigationView mNoteNavigationView;
    @ViewById(2131558631)
    TextView mNoteUuid;
    @ViewById(2131558635)
    NotePrivacyInfoView mPrivacyView;
    @ViewById(2131558633)
    ParagraphView mQuotedText;
    @ViewById(2131558535)
    UserAvatarView mUserAvatar;
    @ViewById(2131558630)
    ParagraphView mUserInfo;
    @Bean
    UserManager mUserManager;
    @FragmentArg
    boolean noteNavigationEnabled;
    @FragmentArg
    UUID uuid;

    /* renamed from: com.douban.book.reader.fragment.NoteDetailFragment.1 */
    class AnonymousClass1 implements RefreshClickListener {
        final /* synthetic */ Object val$idOrUuidToLoad;

        AnonymousClass1(Object obj) {
            this.val$idOrUuidToLoad = obj;
        }

        public void onClick() {
            NoteDetailFragment.this.loadData(this.val$idOrUuidToLoad);
        }
    }

    @AfterViews
    void init() {
        setTitle((int) R.string.title_note_detail);
        this.mQuotedText.setFirstLineIndent(Indent.NONE);
        this.mQuotedText.setBlockQuote(true);
        this.mQuotedText.setBlockQuoteLineColor(R.array.dark_green);
        if (this.uuid != null) {
            loadData(this.uuid);
        } else {
            loadData(this.idOrUuid);
        }
    }

    @Background
    void loadData(Object idOrUuidToLoad) {
        try {
            Annotation annotation;
            dismissLoadingErrorView();
            if (idOrUuidToLoad instanceof UUID) {
                annotation = (Annotation) this.mAnnotationManager.get(idOrUuidToLoad);
            } else {
                annotation = this.mAnnotationManager.getByIdOrUuid(String.valueOf(idOrUuidToLoad));
            }
            this.uuid = annotation.uuid;
            if (annotation.canBeDisplayed()) {
                loadUserInfo(annotation.userId);
                updateAnnotationInfo(annotation);
                return;
            }
            throw new NoSuchDataException();
        } catch (DataLoadException e) {
            Logger.e(this.TAG, e);
            showLoadErrorPage(this.mLoadErrorViewBase, e, new AnonymousClass1(idOrUuidToLoad));
        } finally {
            invalidateOptionsMenu();
        }
    }

    @Background
    void loadUserInfo(int userId) {
        updateUserInfo(this.mUserManager.loadUserInfo(Integer.valueOf(userId)));
    }

    @UiThread
    void updateUserInfo(UserInfo userInfo) {
        this.mUserInfo.setParagraphText(userInfo.getDisplayName());
        this.mUserAvatar.displayUserAvatar(userInfo);
        if (userInfo.isMe() && userInfo.isAnonymous()) {
            this.mUserAvatar.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    new Builder().setMessage((int) R.string.dialog_message_confirm_to_login_to_sync_note).setPositiveButton((int) R.string.dialog_button_login, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            LoginFragment_.builder().build().showAsActivity(NoteDetailFragment.this);
                        }
                    }).setNegativeButton((int) R.string.dialog_button_cancel, null).create().show();
                }
            });
        } else {
            this.mUserAvatar.setOnClickListener(null);
        }
    }

    @UiThread
    void updateAnnotationInfo(Annotation annotation) {
        ViewUtils.showTextIf(DebugSwitch.on(Key.APP_DEBUG_SHOW_NOTE_IDS), this.mNoteUuid, String.format("%s (%s)", new Object[]{annotation.uuid, Integer.valueOf(annotation.id)}));
        ViewUtils.showTextIfNotEmpty(this.mNoteDetail, annotation.note);
        ViewUtils.showTextIfNotEmpty(this.mQuotedText, annotation.getMarkedText());
        this.mNoteCreatedDate.setText(DateUtils.formatDate(annotation.createTime));
        ViewUtils.showIf(annotation.wasCreatedByMe(), this.mPrivacyView, this.mDividerUnderPrivacyView);
        this.mPrivacyView.setData(annotation);
        ViewUtils.showIf(this.noteNavigationEnabled, this.mNoteNavigationView);
        if (this.noteNavigationEnabled) {
            this.mNoteNavigationView.setCurrentNote(annotation);
        }
    }

    public void onEventMainThread(ActiveNoteChangedEvent event) {
        if (this.noteNavigationEnabled) {
            UUID activeUuid = event.getActiveNoteUuid();
            if (activeUuid != null) {
                loadData(activeUuid);
            }
        }
    }

    public void onEventMainThread(AnnotationUpdatedEvent event) {
        if (!event.isValidFor(this.uuid)) {
            return;
        }
        if (event.isDeletion()) {
            finish();
        } else {
            loadData(this.uuid);
        }
    }

    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (this.uuid == null) {
            hideAllMenuItems();
            return;
        }
        boolean isPublic = false;
        boolean isMine = false;
        boolean canBeDisplayed = true;
        try {
            Annotation annotation = (Annotation) this.mAnnotationManager.getFromCache(this.uuid);
            isPublic = annotation.isPublic();
            isMine = annotation.wasCreatedByMe();
            canBeDisplayed = annotation.canBeDisplayed();
        } catch (DataLoadException e) {
            Logger.e(this.TAG, e);
        }
        if (canBeDisplayed) {
            showMenuItemsIf(isPublic, R.id.action_share);
            showMenuItemsIf(isMine, R.id.action_edit, R.id.action_delete);
            return;
        }
        hideAllMenuItems();
    }

    @Background
    @OptionsItem({2131558988})
    void onMenuShareClicked() {
        if (this.uuid != null) {
            ShareNoteEditFragment_.builder().noteUuid(this.uuid).build().showAsActivity(PageOpenHelper.from((Fragment) this));
        }
    }

    @OptionsItem({2131558980})
    void onMenuEditClicked() {
        if (this.uuid != null) {
            NoteEditFragment_.builder().uuid(this.uuid).build().showAsActivity((Fragment) this);
        }
    }

    @OptionsItem({2131558979})
    void onMenuDeleteClicked() {
        new Builder().setMessage((int) R.string.dialog_message_confirm_to_delete_note).setPositiveButton((int) R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                NoteDetailFragment.this.deleteNote();
            }
        }).setNegativeButton((int) R.string.dialog_button_cancel, null).create().show();
    }

    @Background
    void deleteNote() {
        if (this.uuid != null) {
            try {
                showBlockingLoadingDialog();
                this.mAnnotationManager.asyncDelete(this.uuid);
                finish();
            } catch (Throwable e) {
                ToastUtils.showToast(ExceptionUtils.getHumanReadableMessage(e, (int) R.string.toast_general_op_failed));
            } finally {
                dismissLoadingDialog();
            }
        }
    }

    @UiThread
    void dismissLoadingErrorView() {
        this.mLoadErrorViewBase.removeAllViews();
    }
}
