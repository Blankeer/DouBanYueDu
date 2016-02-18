package com.douban.book.reader.fragment;

import android.support.v4.app.Fragment;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.activity.ReaderActivity_;
import com.douban.book.reader.activity.ReaderActivity_.IntentBuilder_;
import com.douban.book.reader.adapter.BookmarkAdapter;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.entity.Bookmark;
import com.douban.book.reader.manager.BookmarkManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.Logger;
import java.util.List;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

@EFragment(2130903105)
public class BookmarkFragment extends BaseFragment {
    private BookmarkAdapter mAdapter;
    @ViewById(2131558629)
    TextView mHintView;
    @ViewById(2131558593)
    StickyListHeadersListView mListView;
    @FragmentArg
    int worksId;

    public BookmarkFragment() {
        this.mAdapter = null;
        setTitle((int) R.string.panel_tab_bookmark);
    }

    @AfterViews
    void init() {
        loadData();
    }

    @ItemClick({2131558593})
    void onListItemClicked(Bookmark bookmark) {
        PageOpenHelper.from((Fragment) this).open(((IntentBuilder_) ReaderActivity_.intent((Fragment) this).flags(67108864)).mBookId(this.worksId).positionToShow(bookmark.getPosition()).get());
    }

    @Background
    void loadData() {
        try {
            setHint(R.string.dialog_msg_loading);
            onLoadSucceed(BookmarkManager.ofWorks(this.worksId).listAll());
        } catch (DataLoadException e) {
            setHint(R.string.general_load_failed);
            Logger.e(this.TAG, e);
        }
    }

    @UiThread
    void onLoadSucceed(List<Bookmark> data) {
        this.mAdapter = new BookmarkAdapter(getActivity(), this.worksId, data);
        this.mListView.setAdapter(this.mAdapter);
        updateHintView();
    }

    @UiThread
    void setHint(int resId) {
        if (resId > 0) {
            this.mHintView.setText(resId);
            this.mHintView.setVisibility(0);
            return;
        }
        this.mHintView.setVisibility(8);
    }

    private void updateHintView() {
        if (this.mAdapter != null) {
            if (this.mAdapter.isEmpty()) {
                setHint(R.string.text_bookmark_empty);
            } else {
                setHint(0);
            }
        }
    }
}
