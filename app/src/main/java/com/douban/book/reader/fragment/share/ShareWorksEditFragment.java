package com.douban.book.reader.fragment.share;

import android.net.Uri;
import android.view.View;
import com.douban.book.reader.R;
import com.douban.book.reader.constant.Char;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.fragment.BaseShareEditFragment;
import com.douban.book.reader.helper.StoreUriHelper;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.view.ShareWorksSummaryView;
import com.douban.book.reader.view.ShareWorksSummaryView_;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

@EFragment
public class ShareWorksEditFragment extends BaseShareEditFragment {
    private Works mWorks;
    @Bean
    WorksManager mWorksManager;
    @FragmentArg
    int worksId;

    protected void initData() throws DataLoadException {
        this.mWorks = WorksManager.getInstance().getWorks(this.worksId);
    }

    protected String getContentType() {
        return BaseShareEditFragment.CONTENT_TYPE_WORKS;
    }

    protected Object getContentId() {
        return Integer.valueOf(this.worksId);
    }

    protected String getContentTitle() {
        return Res.getString(R.string.title_for_shared_works, this.mWorks.title, this.mWorks.author);
    }

    protected String getComplicatedContentTitle() {
        RichText append = new RichText().append(StringUtils.quoteIfNotEmpty(new RichText().append(this.mWorks.title).appendIf(StringUtils.isNotEmpty(this.mWorks.subtitle), " - ", this.mWorks.subtitle), Char.LEFT_DOUBLE_ANGLE_BRACKET));
        boolean isNotEmpty = StringUtils.isNotEmpty(this.mWorks.author);
        Object[] objArr = new Object[1];
        objArr[0] = Res.getString(R.string.title_author, this.mWorks.author);
        append = append.appendIf(isNotEmpty, objArr);
        isNotEmpty = StringUtils.isNotEmpty(this.mWorks.translator);
        objArr = new Object[2];
        objArr[0] = " ";
        objArr[1] = Res.getString(R.string.msg_translator, this.mWorks.translator);
        return append.appendIf(isNotEmpty, objArr).appendIf(StringUtils.isNotEmpty(this.mWorks.publisher), " ", this.mWorks.publisher).append((CharSequence) " | ").append(Res.getString(R.string.app_name)).toString();
    }

    protected String getContentDescription() {
        return this.mWorks.abstractText;
    }

    protected String getContentThumbnailUri() {
        return this.mWorks.coverUrl;
    }

    protected Uri getContentUri() {
        return StoreUriHelper.works(this.worksId);
    }

    protected Object getRelatedWorksId() {
        return Integer.valueOf(this.mWorks.id);
    }

    protected String getRelatedWorksTitle() {
        return this.mWorks.title;
    }

    protected void setupViews() {
        super.setupViews();
        setTitle(Res.getString(R.string.title_share_works, this.mWorks.title));
        addBottomView(createBottomView());
    }

    protected void postToServer(String content) throws DataLoadException {
        this.mWorksManager.shareWorks(this.worksId, getShareTo(), content);
    }

    private View createBottomView() {
        View bottomView = ShareWorksSummaryView_.build(getActivity());
        ((ShareWorksSummaryView) bottomView).setWorks(this.mWorks);
        return bottomView;
    }
}
