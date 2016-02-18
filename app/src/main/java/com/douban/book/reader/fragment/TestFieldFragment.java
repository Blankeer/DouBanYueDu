package com.douban.book.reader.fragment;

import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.entity.Tag;
import com.douban.book.reader.lib.view.FlowLayout;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.view.BoxedWorksView;
import com.douban.book.reader.view.TagView;
import com.douban.book.reader.view.TagView_;
import java.util.List;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

@EFragment(2130903109)
public class TestFieldFragment extends BaseFragment {
    static final int[] ICONS;
    @ViewById(2131558597)
    BoxedWorksView mBoxedWorksView;
    @ViewById(2131558651)
    BoxedWorksView mBoxedWorksView2;
    @ViewById(2131558649)
    TextView mIconFontShowCase;
    @ViewById(2131558650)
    TextView mIconFontShowCaseLarge;
    @ViewById(2131558559)
    FlowLayout mTagsLayout;

    static {
        ICONS = new int[]{R.drawable.v_arrow_right, R.drawable.v_bookmark, R.drawable.v_chinese_yuan, R.drawable.v_column, R.drawable.v_doucoin, R.drawable.v_download, R.drawable.v_ebooks, R.drawable.v_footnote, R.drawable.v_hermes, R.drawable.v_magazine, R.drawable.v_mine, R.drawable.v_note, R.drawable.v_purchase, R.drawable.v_read, R.drawable.v_redeem, R.drawable.v_review, R.drawable.v_self_publishing, R.drawable.v_store, R.drawable.v_underline, R.drawable.v_wallet, R.drawable.v_gift};
    }

    public TestFieldFragment() {
        setTitle((int) R.string.title_test_field);
    }

    @AfterViews
    void init() {
        RichText text = RichText.buildUpon((int) R.string.app_name).append('\n');
        for (int icon : ICONS) {
            text.appendIcon(icon);
        }
        this.mIconFontShowCase.setText(text);
        this.mIconFontShowCaseLarge.setText(text);
        this.mBoxedWorksView.worksId(4134961);
        this.mBoxedWorksView.showBoxCover(true).isOpened(true);
        this.mBoxedWorksView2.worksId(4134961);
    }

    @UiThread
    void updateTagsView(List<Tag> list) {
        for (Tag tag : list) {
            TagView view = TagView_.build(App.get());
            view.setEntity(tag);
            this.mTagsLayout.addView(view);
        }
    }
}
