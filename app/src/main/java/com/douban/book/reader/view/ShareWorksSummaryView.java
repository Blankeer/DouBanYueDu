package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.util.Res;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903189)
public class ShareWorksSummaryView extends RelativeLayout {
    @ViewById(2131558774)
    TextView mAuthor;
    @ViewById(2131558772)
    WorksCoverView mCover;
    @ViewById(2131558462)
    TextView mTitle;

    public ShareWorksSummaryView(Context context) {
        super(context);
    }

    public ShareWorksSummaryView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShareWorksSummaryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setWorks(Works works) {
        this.mTitle.setText(works.title);
        this.mAuthor.setText(Res.getString(R.string.title_author, works.author));
        this.mCover.works(works).noLabel();
    }
}
