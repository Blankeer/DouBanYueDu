package com.douban.book.reader.fragment;

import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.BaseArrayAdapter;
import com.douban.book.reader.adapter.ViewBinderAdapter;
import com.douban.book.reader.entity.Feed;
import com.douban.book.reader.manager.FeedManager;
import com.douban.book.reader.manager.Lister;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.LoginPrompt;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.view.RedirectEmptyView_;
import com.douban.book.reader.view.item.FeedItemView_;
import com.mcxiaoke.next.ui.endless.EndlessListView;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;

@EFragment
@OptionsMenu({2131623940})
public class FeedFragment extends BaseEndlessListFragment<Feed> {
    @Bean
    FeedManager mFeedsManager;

    public Lister<Feed> onCreateLister() {
        return this.mFeedsManager.feedsChapterUpdatedLister();
    }

    public BaseArrayAdapter<Feed> onCreateAdapter() {
        return new ViewBinderAdapter(FeedItemView_.class);
    }

    protected View onCreateEmptyView() {
        return RedirectEmptyView_.build(getActivity()).hint(R.string.hint_empty_feed);
    }

    protected void onListViewCreated(EndlessListView listView) {
        setTitle((int) R.string.title_feeds_chapter_updated);
        registerForContextMenu(listView);
        LoginPrompt.showIfNeeded(getActivity());
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getItemId() != 1) {
            return super.onContextItemSelected(item);
        }
        deleteMsg((Feed) this.mAdapter.getItem(info.position));
        return true;
    }

    @OptionsItem({2131558985})
    void onMenuItemOpenClicked() {
        SubscriptionCenterFragment_.builder().build().showAsActivity((Fragment) this);
    }

    @OptionsItem({2131558984})
    void onMenuItemMarkAllRead() {
        markAllAsRead();
    }

    @ItemClick({2131558593})
    void onListItemClicked(Feed item) {
        markAsRead(item);
        ColumnChapterReaderFragment_.builder().worksId(item.payload.worksId).chapterId(item.payload.chapterId).build().showAsActivity((Fragment) this);
    }

    @UiThread
    void removeMsgInAdapter(Feed msg) {
        this.mAdapter.remove(msg);
    }

    @UiThread
    void markAllAsReadInAdapter() {
        for (int index = 0; index < this.mAdapter.getCount(); index++) {
            ((Feed) this.mAdapter.getItem(index)).hasRead = true;
        }
        notifyDataChanged();
    }

    @Background
    void markAllAsRead() {
        try {
            markAllAsReadInAdapter();
            this.mFeedsManager.markAllAsRead();
        } catch (Exception e) {
            Logger.e(this.TAG, e);
            ToastUtils.showToast((int) R.string.toast_general_op_failed);
        }
    }

    @Background
    void markAsRead(Feed msg) {
        try {
            msg.hasRead = true;
            this.mFeedsManager.markAsRead(msg.id);
        } catch (Exception e) {
            Logger.e(this.TAG, e);
            msg.hasRead = false;
        } finally {
            notifyDataChanged();
        }
    }

    @Background
    void deleteMsg(Feed msg) {
        try {
            removeMsgInAdapter(msg);
            this.mFeedsManager.delete(Integer.valueOf(msg.id));
        } catch (Exception e) {
            Logger.e(this.TAG, e);
            ToastUtils.showToast((int) R.string.toast_general_delete_failed);
        }
    }
}
