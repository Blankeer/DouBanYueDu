package com.douban.book.reader.fragment.share;

import android.net.Uri;
import android.view.View;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.constant.Char;
import com.douban.book.reader.content.Book;
import com.douban.book.reader.content.Book.ImageSize;
import com.douban.book.reader.content.page.Range;
import com.douban.book.reader.content.paragraph.IllusParagraph;
import com.douban.book.reader.content.paragraph.Paragraph;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.fragment.BaseShareEditFragment;
import com.douban.book.reader.helper.StoreUriHelper;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.ReaderUri;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.WXUtils.MessageBuilder;
import com.douban.book.reader.view.ShareSelectionInfoView;
import com.douban.book.reader.view.ShareSelectionInfoView_;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

@EFragment
public class ShareRangeEditFragment extends BaseShareEditFragment {
    private String mIllusUri;
    private CharSequence mParagraphText;
    private Works mWorks;
    @Bean
    WorksManager mWorksManager;
    @FragmentArg
    Range range;
    @FragmentArg
    int worksId;

    protected void initData() throws DataLoadException {
        this.mWorks = WorksManager.getInstance().getWorks(this.worksId);
        Book book = Book.get(this.worksId);
        if (this.mWorks == null || !this.mWorks.isGallery()) {
            this.mParagraphText = book.getText(this.range);
            return;
        }
        Paragraph paragraph = book.getParagraph(this.range.startPosition);
        if (paragraph != null) {
            this.mParagraphText = paragraph.getPrintableText();
        }
        if (paragraph instanceof IllusParagraph) {
            this.mIllusUri = ReaderUri.illus(this.worksId, this.range.startPosition.packageId, ((IllusParagraph) paragraph).getIllusSeq(), ImageSize.NORMAL).toString();
        }
    }

    protected String getContentType() {
        return isSharingIllus() ? BaseShareEditFragment.CONTENT_TYPE_ILLUS : BaseShareEditFragment.CONTENT_TYPE_SELECTION;
    }

    protected Object getContentId() {
        return null;
    }

    protected String getContentTitle() {
        return Res.getString(R.string.title_for_shared_range, this.mWorks.title, this.mWorks.author);
    }

    protected String getContentDescription() {
        return StringUtils.toStr(StringUtils.quoteIfNotEmpty(this.mParagraphText, Char.LEFT_COMER_BRACKET));
    }

    protected String getContentThumbnailUri() {
        if (StringUtils.isNotEmpty(this.mIllusUri)) {
            return this.mIllusUri;
        }
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

    protected WXMediaMessage completeWeixinShareContent(MessageBuilder builder) {
        if (isSharingIllus()) {
            return builder.imageData(this.mIllusUri).build();
        }
        return builder.text(StringUtils.toStr(getPlainTextShareContent())).build();
    }

    protected void setupViews() {
        setTitle(getTitleStr());
        addBottomView(createBottomView());
    }

    protected void postToServer(String content) throws DataLoadException {
        this.mWorksManager.shareRange(this.worksId, getShareTo(), this.range, content);
    }

    private View createBottomView() {
        ShareSelectionInfoView bottomView = ShareSelectionInfoView_.build(App.get());
        bottomView.setQuoteLineColor(R.array.blue);
        bottomView.setData(this.worksId, this.range);
        return bottomView;
    }

    private CharSequence getTitleStr() {
        if (isSharingIllus()) {
            return Res.getString(R.string.title_share_illus, this.mWorks.title);
        }
        return Res.getString(R.string.title_share_selection, this.mWorks.title);
    }

    private boolean isSharingIllus() {
        if (this.mWorks != null && this.mWorks.isGallery()) {
            if (StringUtils.isNotEmpty(this.mIllusUri)) {
                return true;
            }
        }
        return false;
    }
}
