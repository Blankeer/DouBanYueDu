package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.activity.ReaderActivity_;
import com.douban.book.reader.activity.ReaderActivity_.IntentBuilder_;
import com.douban.book.reader.app.App;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.span.IconFontSpan;
import com.douban.book.reader.util.DebugSwitch;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.ViewUtils;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903205)
public class WorksSummaryView extends RelativeLayout {
    @ViewById(2131558774)
    TextView mAuthors;
    @ViewById(2131558972)
    TextView mCategory;
    @ViewById(2131558973)
    TextView mContentSpace;
    @ViewById(2131558772)
    WorksCoverView mCover;
    @ViewById(2131558549)
    RatingBar mRatingBar;
    @ViewById(2131558550)
    TextView mRatingInfo;
    @ViewById(2131558854)
    TextView mSubTitle;
    @ViewById(2131558462)
    TextView mTitle;
    @ViewById(2131558911)
    TextView mTranslator;
    @ViewById(2131558971)
    TextView mWorksType;

    /* renamed from: com.douban.book.reader.view.WorksSummaryView.1 */
    class AnonymousClass1 implements OnClickListener {
        final /* synthetic */ Works val$works;

        AnonymousClass1(Works works) {
            this.val$works = works;
        }

        public void onClick(View v) {
            PageOpenHelper.from(WorksSummaryView.this).open(((IntentBuilder_) ReaderActivity_.intent(App.get()).mBookId(this.val$works.id).flags(67108864)).get());
        }
    }

    public WorksSummaryView(Context context) {
        super(context);
    }

    public WorksSummaryView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WorksSummaryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setWorks(Works works) {
        this.mCover.works(works);
        if (works.isContentReady()) {
            this.mCover.setOnClickListener(new AnonymousClass1(works));
        }
        this.mTitle.setText(works.title);
        if (DebugSwitch.on(Key.APP_DEBUG_SHOW_BOOK_IDS)) {
            this.mTitle.setText(String.format("%s %s", new Object[]{Integer.valueOf(works.id), works.title}));
        }
        ViewUtils.showTextIfNotEmpty(this.mSubTitle, works.subtitle);
        ViewUtils.showTextIf(StringUtils.isNotEmpty(works.translator), this.mTranslator, Res.getString(R.string.msg_translator, works.translator));
        this.mAuthors.setText(Res.getString(R.string.title_author, works.author));
        this.mContentSpace.setText(works.getWorksSizeStr());
        this.mCategory.setText(works.categoryText);
        this.mWorksType.setText(new RichText().appendIcon(new IconFontSpan(works.getRootKindIcon()).ratio(1.2f).paddingRight(0.15f)).append(works.getWorksRootKindName()));
        ViewUtils.setVerticalPaddingResId(this.mWorksType, R.dimen.general_subview_vertical_padding_small);
        this.mRatingBar.setRating(works.averageRating);
        this.mRatingInfo.setText(works.formatRatingInfo());
        ViewUtils.goneIf(works.isOnPre, this.mRatingBar, this.mRatingInfo, this.mContentSpace);
    }
}
