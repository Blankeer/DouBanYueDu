package com.douban.book.reader.view.card;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import com.douban.book.reader.R;
import com.douban.book.reader.activity.ReaderActivity_;
import com.douban.book.reader.activity.ReaderActivity_.IntentBuilder_;
import com.douban.book.reader.app.App;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.task.DownloadManager;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.ReaderUri;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.ViewUtils;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.LongClick;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903075)
public class WorksActionCard extends Card<WorksActionCard> {
    @ViewById(2131558528)
    Button mBtnRead;
    private int mWorksId;

    public WorksActionCard(Context context) {
        super(context);
    }

    public WorksActionCard worksId(int worksId) {
        this.mWorksId = worksId;
        return this;
    }

    @AfterViews
    void init() {
        ViewUtils.setBottomPaddingResId(this, R.dimen.general_subview_vertical_padding_medium);
        ThemedAttrs.ofView(this).append(R.attr.backgroundColorArray, Integer.valueOf(R.array.page_highlight_bg_color)).updateView();
        this.mBtnRead.setText(RichText.textWithIcon((int) R.drawable.v_read, (int) R.string.read));
    }

    @Click({2131558528})
    void onBtnReadClicked() {
        PageOpenHelper.from((View) this).open(((IntentBuilder_) ReaderActivity_.intent(App.get()).mBookId(this.mWorksId).flags(67108864)).get());
    }

    @LongClick({2131558528})
    void onBtnReadLongClicked() {
        DownloadManager.scheduleDownload(ReaderUri.works(this.mWorksId));
        onBtnReadClicked();
    }
}
