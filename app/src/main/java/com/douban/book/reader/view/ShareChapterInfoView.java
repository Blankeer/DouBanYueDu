package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.douban.book.reader.content.pack.Package;
import com.douban.book.reader.entity.Works;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903183)
public class ShareChapterInfoView extends RelativeLayout {
    @ViewById(2131558774)
    TextView mAuthor;
    @ViewById(2131558462)
    TextView mTitle;

    public ShareChapterInfoView(Context context) {
        super(context);
    }

    public ShareChapterInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShareChapterInfoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setPackage(Works works, Package pack) {
        this.mTitle.setText(pack.getTitle());
        this.mAuthor.setText(works.author);
    }
}
