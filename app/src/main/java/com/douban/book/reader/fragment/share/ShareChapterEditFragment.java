package com.douban.book.reader.fragment.share;

import android.net.Uri;
import android.view.View;
import com.douban.book.reader.R;
import com.douban.book.reader.content.pack.Package;
import com.douban.book.reader.entity.Manifest;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.fragment.BaseShareEditFragment;
import com.douban.book.reader.helper.StoreUriHelper;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.view.ShareChapterInfoView;
import com.douban.book.reader.view.ShareChapterInfoView_;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

@EFragment
public class ShareChapterEditFragment extends BaseShareEditFragment {
    @FragmentArg
    int chapterId;
    private Package mPackage;
    private Works mWorks;
    @Bean
    WorksManager mWorksManager;
    @FragmentArg
    int worksId;

    protected void initData() throws DataLoadException {
        this.mWorks = this.mWorksManager.getWorks(this.worksId);
        try {
            Manifest.load(this.worksId);
            this.mPackage = Package.get(this.worksId, this.chapterId);
        } catch (Throwable e) {
            throw new DataLoadException(e);
        }
    }

    protected String getContentType() {
        return BaseShareEditFragment.CONTENT_TYPE_CHAPTER;
    }

    protected Object getContentId() {
        return Integer.valueOf(this.chapterId);
    }

    protected String getContentTitle() {
        return Res.getString(this.mWorks.isSerial() ? R.string.title_for_shared_serial_chapter : R.string.title_for_shared_column_chapter, this.mPackage.getTitle(), this.mWorks.author);
    }

    protected String getComplicatedContentTitle() {
        return Res.getString(this.mWorks.isSerial() ? R.string.complicated_title_for_shared_serial_chapter : R.string.complicated_title_for_shared_column_chapter, this.mPackage.getTitle(), this.mWorks.author, this.mWorks.title);
    }

    protected String getContentDescription() {
        return this.mPackage.getAbstractText();
    }

    protected String getContentThumbnailUri() {
        return this.mWorks.coverUrl;
    }

    protected Uri getContentUri() {
        return StoreUriHelper.columnChapterReader(this.mWorks.columnId, this.chapterId);
    }

    protected Object getRelatedWorksId() {
        return Integer.valueOf(this.mWorks.id);
    }

    protected String getRelatedWorksTitle() {
        return this.mWorks.title;
    }

    protected void setupViews() {
        setTitle(Res.getString(this.mWorks.isSerial() ? R.string.title_share_serial_chapter : R.string.title_share_column_chapter));
        addBottomView(createBottomView());
    }

    protected void postToServer(String content) throws DataLoadException {
        this.mWorksManager.shareWorks(this.chapterId, getShareTo(), content);
    }

    private View createBottomView() {
        ShareChapterInfoView view = ShareChapterInfoView_.build(getActivity());
        view.setPackage(this.mWorks, this.mPackage);
        return view;
    }
}
