package com.douban.book.reader.fragment;

import android.view.View;
import android.view.View.OnClickListener;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.entity.Tag;
import com.douban.book.reader.lib.view.FlowLayout;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.view.TagView;
import com.douban.book.reader.view.TagView_;
import com.douban.book.reader.view.card.Card;
import java.util.List;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;

@EFragment
public class TagFragment extends BaseCardFragment implements OnClickListener {
    private OnClickListener mTagClickedListener;
    private FlowLayout mTagLayout;
    private TagLoader mTagLoader;
    @FragmentArg
    String tagTitle;

    public interface TagLoader {
        List<Tag> onLoadTag() throws DataLoadException;
    }

    @AfterViews
    void init() {
        this.mTagLayout = (FlowLayout) View.inflate(App.get(), R.layout.card_works_tags, null);
        addCard(new Card(App.get()).title(this.tagTitle).content(this.mTagLayout).noDivider());
        refreshTag();
    }

    public void setTagClickedListener(OnClickListener listener) {
        this.mTagClickedListener = listener;
    }

    public void setTagLoader(TagLoader tagLoader) {
        this.mTagLoader = tagLoader;
        refreshTag();
    }

    public void onClick(View v) {
        if (this.mTagClickedListener != null) {
            this.mTagClickedListener.onClick(v);
        }
    }

    @Background
    void refreshTag() {
        if (this.mTagLoader != null) {
            try {
                updateTagView(this.mTagLoader.onLoadTag());
            } catch (DataLoadException e) {
                Logger.e(this.TAG, e);
            }
        }
    }

    @UiThread
    void updateTagView(List<Tag> tags) {
        if (this.mTagLayout != null) {
            this.mTagLayout.removeAllViews();
            for (Tag tag : tags) {
                TagView tagView = TagView_.build(App.get());
                tagView.setEntity(tag);
                tagView.setOnClickListener(this);
                this.mTagLayout.addView(tagView);
            }
        }
    }
}
