package com.douban.book.reader.fragment;

import android.view.View;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.content.page.Range;
import com.douban.book.reader.entity.Annotation;
import com.douban.book.reader.entity.Annotation.Type;
import com.douban.book.reader.exception.ShareFailedWhileCreatingNoteException;
import com.douban.book.reader.manager.AnnotationManager;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.network.exception.NetworkRequestPostponedException;
import com.douban.book.reader.util.ExceptionUtils;
import com.douban.book.reader.util.Pref;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.view.NotePrivacyInfoView;
import com.douban.book.reader.view.NotePrivacyInfoView_;
import com.douban.book.reader.view.ShareSelectionInfoView;
import com.douban.book.reader.view.ShareSelectionInfoView_;
import com.douban.book.reader.view.ShareTargetSelectorView;
import java.util.UUID;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

@EFragment
public class NoteEditFragment extends BaseEditFragment {
    private Annotation mAnnotation;
    @Bean
    AnnotationManager mAnnotationManager;
    private boolean mIsEditing;
    private NotePrivacyInfoView mNotePrivacyInfoView;
    private ShareTargetSelectorView mShareTargetSelectorView;
    @Bean
    UserManager mUserManager;
    @FragmentArg
    Range range;
    @FragmentArg
    UUID uuid;
    @FragmentArg
    int worksId;

    @AfterViews
    void init() {
        setHint(R.string.hint_new_note_edit);
    }

    protected void initData() throws DataLoadException {
        if (this.uuid != null) {
            this.mIsEditing = true;
            this.mAnnotation = (Annotation) this.mAnnotationManager.get((Object) this.uuid);
            this.worksId = this.mAnnotation.worksId;
            this.range = this.mAnnotation.getRange();
            return;
        }
        this.mAnnotation = new Annotation(this.worksId, Type.NOTE);
        this.mAnnotation.setRange(this.range);
        this.mAnnotation.setIsPrivate(getDefaultNotePrivacy());
        this.uuid = this.mAnnotation.uuid;
    }

    protected void onDataReady() {
        setTitle(Res.getString(this.mIsEditing ? R.string.title_edit_note : R.string.title_new_note));
        setContent(this.mAnnotation.note);
        addBottomView(createRangeView());
        if (!this.mIsEditing) {
            addBottomView(createEditInfoView());
        }
    }

    protected void postToServer(String content) throws DataLoadException {
        if (this.mAnnotation != null) {
            this.mAnnotation.note = content;
            if (this.mIsEditing) {
                this.mAnnotationManager.updateNote(this.mAnnotation.uuid, this.mAnnotation.note);
                return;
            }
            if (this.mNotePrivacyInfoView != null) {
                this.mAnnotation.privacy = this.mNotePrivacyInfoView.getPrivacy();
            }
            this.mAnnotationManager.newNote(this.mAnnotation, this.mShareTargetSelectorView.getRequestParam());
        }
    }

    protected boolean shouldBeConsideredAsSucceed(Throwable e) {
        if (ExceptionUtils.isCausedBy(e, ShareFailedWhileCreatingNoteException.class)) {
            ToastUtils.showToast((int) R.string.toast_note_share_while_create_failed);
        }
        return ExceptionUtils.isCausedBy(e, NetworkRequestPostponedException.class);
    }

    protected void onPostSucceed() {
        if (!this.mIsEditing) {
            this.mAnnotationManager.setActiveNote(this.mAnnotation);
        }
        super.onPostSucceed();
    }

    private boolean getDefaultNotePrivacy() {
        if (this.mUserManager.isAnonymousUser()) {
            return true;
        }
        return Pref.ofApp().getBoolean(Key.APP_CREATE_NOTE_AS_PRIVATE);
    }

    private View createRangeView() {
        ShareSelectionInfoView bottomView = ShareSelectionInfoView_.build(App.get());
        bottomView.setQuoteLineColor(R.array.dark_green);
        bottomView.setData(this.worksId, this.range);
        return bottomView;
    }

    private View createEditInfoView() {
        this.mNotePrivacyInfoView = NotePrivacyInfoView_.build(App.get());
        this.mShareTargetSelectorView = (ShareTargetSelectorView) this.mNotePrivacyInfoView.findViewById(R.id.share_target);
        this.mNotePrivacyInfoView.setShowShareTarget(true);
        this.mNotePrivacyInfoView.setShouldChangePrivacyForRemote(false);
        this.mNotePrivacyInfoView.setData(this.mAnnotation);
        return this.mNotePrivacyInfoView;
    }
}
