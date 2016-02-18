package com.douban.book.reader.fragment.share;

import android.net.Uri;
import android.view.View;
import com.douban.book.reader.R;
import com.douban.book.reader.constant.Char;
import com.douban.book.reader.entity.Annotation;
import com.douban.book.reader.entity.UserInfo;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.fragment.BaseShareEditFragment;
import com.douban.book.reader.helper.StoreUriHelper;
import com.douban.book.reader.manager.AnnotationManager;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.view.ShareNoteInfoView_;
import java.util.UUID;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

@EFragment
public class ShareNoteEditFragment extends BaseShareEditFragment {
    private Annotation mAnnotation;
    @Bean
    AnnotationManager mAnnotationManager;
    private UserInfo mNoteAuthor;
    @Bean
    UserManager mUserManager;
    private Works mWorks;
    @Bean
    WorksManager mWorksManager;
    @FragmentArg
    UUID noteUuid;

    protected void initData() throws DataLoadException {
        this.mAnnotation = (Annotation) this.mAnnotationManager.get((Object) this.noteUuid);
        if (this.mAnnotation.id <= 0) {
            this.mAnnotation = (Annotation) this.mAnnotationManager.addToRemote(this.mAnnotation);
        }
        this.mNoteAuthor = this.mUserManager.loadUserInfo(Integer.valueOf(this.mAnnotation.userId));
        this.mWorks = this.mWorksManager.getWorks(this.mAnnotation.worksId);
    }

    protected String getContentType() {
        return BaseShareEditFragment.CONTENT_TYPE_NOTE;
    }

    protected Object getContentId() {
        return Integer.valueOf(this.mAnnotation.id);
    }

    protected String getContentTitle() {
        return Res.getString(R.string.title_for_shared_note, this.mWorks.title, this.mNoteAuthor.name);
    }

    protected String getComplicatedContentTitle() {
        return new RichText().append(getContentTitle()).append((CharSequence) " | ").append(Res.getString(R.string.app_name)).toString();
    }

    protected String getContentDescription() {
        return new RichText().append(this.mAnnotation.note).appendAsNewLineIfNotEmpty(StringUtils.quoteIfNotEmpty(this.mAnnotation.getMarkedText(), Char.LEFT_COMER_BRACKET)).toString();
    }

    protected String getContentThumbnailUri() {
        return this.mWorks.coverUrl;
    }

    protected Uri getContentUri() {
        return StoreUriHelper.note(this.mAnnotation.id);
    }

    protected Object getRelatedWorksId() {
        return Integer.valueOf(this.mAnnotation.worksId);
    }

    protected String getRelatedWorksTitle() {
        return this.mWorks.title;
    }

    protected void setupViews() {
        setTitle(Res.getString(R.string.title_share_note, this.mWorks.title));
        addBottomView(createBottomView());
    }

    protected void postToServer(String content) throws DataLoadException {
        this.mAnnotationManager.shareAnnotation(this.noteUuid, getShareTo(), content);
    }

    private View createBottomView() {
        return ShareNoteInfoView_.build(getActivity()).noteUuid(this.noteUuid);
    }
}
