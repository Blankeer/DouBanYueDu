package com.douban.book.reader.view;

import android.content.Context;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.douban.book.reader.content.Book;
import com.douban.book.reader.content.Book.ImageSize;
import com.douban.book.reader.content.page.Position;
import com.douban.book.reader.content.page.Range;
import com.douban.book.reader.content.paragraph.IllusParagraph;
import com.douban.book.reader.content.paragraph.Paragraph;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.ImageLoaderUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.ReaderUri;
import com.douban.book.reader.util.StringUtils;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903187)
public class ShareSelectionInfoView extends RelativeLayout {
    private static final String TAG;
    @ViewById(2131558504)
    ImageView mImageIllus;
    @ViewById(2131558936)
    ParagraphView mSelection;
    @ViewById(2131558462)
    TextView mTitle;
    @Bean
    WorksManager mWorksManager;

    static {
        TAG = ShareSelectionInfoView.class.getSimpleName();
    }

    public ShareSelectionInfoView(Context context) {
        super(context);
    }

    public ShareSelectionInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShareSelectionInfoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setData(int worksId, Range range) {
        loadData(worksId, range);
    }

    public void setQuoteLineColor(@ColorRes @ArrayRes int quoteLineColorRes) {
        this.mSelection.setBlockQuoteLineColor(quoteLineColorRes);
    }

    @Background
    void loadData(int worksId, Range range) {
        try {
            Works works = this.mWorksManager.getWorks(worksId);
            Book book = Book.get(worksId);
            if (works.isGallery()) {
                Position startPos = range.startPosition;
                Paragraph paragraph = book.getParagraph(startPos);
                if (paragraph instanceof IllusParagraph) {
                    if (StringUtils.isNotEmpty(ReaderUri.illus(worksId, book.getPackageId(startPos.packageIndex), ((IllusParagraph) paragraph).getIllusSeq(), ImageSize.NORMAL).toString())) {
                        setIllusUrl(ReaderUri.illus(worksId, book.getPackageId(startPos.packageIndex), ((IllusParagraph) paragraph).getIllusSeq(), ImageSize.NORMAL).toString());
                        return;
                    }
                    return;
                } else if (paragraph != null) {
                    setSelection(paragraph.getPrintableText().toString());
                    return;
                } else {
                    return;
                }
            }
            setSelection(book.getText(range));
        } catch (DataLoadException e) {
            Logger.e(TAG, e);
        }
    }

    @UiThread
    void setSelection(CharSequence selection) {
        this.mSelection.setVisibility(0);
        this.mSelection.setBlockQuote(true);
        this.mSelection.setParagraphText(selection);
    }

    @UiThread
    void setIllusUrl(String url) {
        this.mImageIllus.setVisibility(0);
        ImageLoaderUtils.displayImage(url, this.mImageIllus);
    }
}
