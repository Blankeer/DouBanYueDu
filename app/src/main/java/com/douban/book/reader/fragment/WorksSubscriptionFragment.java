package com.douban.book.reader.fragment;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.BaseArrayAdapter;
import com.douban.book.reader.adapter.ViewBinderAdapter;
import com.douban.book.reader.entity.WorksSubscription;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.event.SubscriptionCanceledEvent;
import com.douban.book.reader.fragment.AlertDialogFragment.Builder;
import com.douban.book.reader.manager.Lister;
import com.douban.book.reader.manager.SubscriptionManager;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.view.RedirectEmptyView_;
import com.douban.book.reader.view.item.WorksSubscriptionItemView_;
import com.mcxiaoke.next.ui.endless.EndlessListView;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;

@EFragment
public class WorksSubscriptionFragment extends BaseEndlessListFragment<WorksSubscription> {
    @Bean
    SubscriptionManager mSubscriptionManager;
    @Bean
    WorksManager mWorkManager;

    /* renamed from: com.douban.book.reader.fragment.WorksSubscriptionFragment.1 */
    class AnonymousClass1 implements OnClickListener {
        final /* synthetic */ WorksSubscription val$worksSubscription;

        AnonymousClass1(WorksSubscription worksSubscription) {
            this.val$worksSubscription = worksSubscription;
        }

        public void onClick(DialogInterface dialog, int which) {
            WorksSubscriptionFragment.this.cancelSubscription(this.val$worksSubscription);
        }
    }

    public Lister<WorksSubscription> onCreateLister() {
        return this.mSubscriptionManager.subscriptionLister(false);
    }

    public BaseArrayAdapter<WorksSubscription> onCreateAdapter() {
        return new ViewBinderAdapter(WorksSubscriptionItemView_.class);
    }

    protected View onCreateEmptyView() {
        return RedirectEmptyView_.build(getActivity()).hint(R.string.hint_empty_subscription);
    }

    protected void onListViewCreated(EndlessListView listView) {
        registerForContextMenu(listView);
    }

    @ItemClick({2131558593})
    void onListItemClick(WorksSubscription worksSubscription) {
        WorksProfileFragment_.builder().worksId(worksSubscription.worksId).build().showAsActivity((Fragment) this);
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, 3, 1, R.string.action_cancel_subscription);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getItemId() != 3) {
            return super.onContextItemSelected(item);
        }
        new Builder().setMessage(Res.getString(R.string.dialog_message_cancel_subscription_confirm)).setPositiveButton((int) R.string.dialog_button_ok, new AnonymousClass1((WorksSubscription) this.mAdapter.getItem(info.position))).setNegativeButton(Res.getString(R.string.dialog_button_cancel), null).create().show();
        return true;
    }

    @UiThread
    void cancelSubscriptionInAdapter(WorksSubscription worksSubscription) {
        this.mAdapter.remove(worksSubscription);
        notifyDataChanged();
    }

    @Background
    void cancelSubscription(WorksSubscription worksSubscription) {
        try {
            cancelSubscriptionInAdapter(worksSubscription);
            this.mWorkManager.unSubscribe(worksSubscription.worksId);
            EventBusUtils.post(new SubscriptionCanceledEvent());
        } catch (Exception e) {
            Logger.e(this.TAG, e);
            ToastUtils.showToast((int) R.string.toast_general_op_failed);
        }
    }
}
