package com.douban.book.reader.fragment;

import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.BaseArrayAdapter;
import com.douban.book.reader.adapter.ViewBinderAdapter;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.entity.Notification;
import com.douban.book.reader.manager.Lister;
import com.douban.book.reader.manager.NotificationManager;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.LoginPrompt;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.view.item.SimpleNotificationItemView_;
import com.mcxiaoke.next.ui.endless.EndlessListView;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;

@EFragment
@OptionsMenu({2131623941})
public class NotificationListFragment extends BaseEndlessListFragment<Notification> {
    @Bean
    NotificationManager mNotificationManager;

    public Lister<Notification> onCreateLister() {
        return this.mNotificationManager.myList();
    }

    public BaseArrayAdapter<Notification> onCreateAdapter() {
        return new ViewBinderAdapter(SimpleNotificationItemView_.class);
    }

    @AfterViews
    void init() {
        setTitle((int) R.string.title_my_notification);
    }

    protected void onListViewCreated(EndlessListView listView) {
        registerForContextMenu(this.mListView);
        LoginPrompt.showIfNeeded(getActivity());
        setEmptyHint((int) R.string.hint_empty_notification);
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, 1, 1, R.string.action_delete);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getItemId() != 1) {
            return super.onContextItemSelected(item);
        }
        deleteNotification((Notification) this.mAdapter.getItem(info.position));
        return true;
    }

    @ItemClick({2131558593})
    public void onItemClicked(Notification notification) {
        PageOpenHelper.from((Fragment) this).open(notification.uri);
        markAsRead(notification);
    }

    @OptionsItem({2131558984})
    void onMenuItemMarkClicked() {
        markAllAsRead();
    }

    @UiThread
    void removeNotificationInAdapter(Notification notification) {
        this.mAdapter.remove(notification);
        notifyDataChanged();
    }

    @UiThread
    void markAllAsReadInAdapter() {
        for (int index = 0; index < this.mAdapter.getCount(); index++) {
            ((Notification) this.mAdapter.getItem(index)).hasRead = true;
        }
        notifyDataChanged();
    }

    @Background
    void markAllAsRead() {
        try {
            markAllAsReadInAdapter();
            this.mNotificationManager.markAllAsRead();
        } catch (Exception e) {
            Logger.e(this.TAG, e);
            ToastUtils.showToast((int) R.string.toast_general_op_failed);
        }
    }

    @Background
    void deleteNotification(Notification notification) {
        try {
            removeNotificationInAdapter(notification);
            this.mNotificationManager.delete(Integer.valueOf(notification.id));
        } catch (Exception e) {
            Logger.e(this.TAG, e);
            ToastUtils.showToast((int) R.string.toast_general_delete_failed);
        }
    }

    @Background
    void markAsRead(Notification notification) {
        try {
            notification.hasRead = true;
            this.mNotificationManager.markAsRead(notification.id);
        } catch (Exception e) {
            notification.hasRead = false;
            Logger.e(this.TAG, e);
        }
        notifyDataChanged();
    }
}
