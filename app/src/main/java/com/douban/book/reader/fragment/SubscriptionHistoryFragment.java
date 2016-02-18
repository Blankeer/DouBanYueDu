package com.douban.book.reader.fragment;

import android.support.v4.app.Fragment;
import android.view.View;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.BaseArrayAdapter;
import com.douban.book.reader.adapter.ViewBinderAdapter;
import com.douban.book.reader.entity.WorksSubscription;
import com.douban.book.reader.event.SubscriptionCanceledEvent;
import com.douban.book.reader.manager.Lister;
import com.douban.book.reader.manager.SubscriptionManager;
import com.douban.book.reader.view.RedirectEmptyView_;
import com.douban.book.reader.view.item.WorksSubscriptionItemView;
import com.douban.book.reader.view.item.WorksSubscriptionItemView_;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;

@EFragment
public class SubscriptionHistoryFragment extends BaseEndlessListFragment<WorksSubscription> {
    @Bean
    SubscriptionManager mSubscriptionManager;

    /* renamed from: com.douban.book.reader.fragment.SubscriptionHistoryFragment.1 */
    class AnonymousClass1 extends ViewBinderAdapter<WorksSubscription> {
        AnonymousClass1(Class type) {
            super(type);
        }

        protected void bindView(View itemView, WorksSubscription data) {
            super.bindView(itemView, data);
            ((WorksSubscriptionItemView) itemView).showStatus(true);
        }
    }

    public Lister<WorksSubscription> onCreateLister() {
        return this.mSubscriptionManager.subscriptionLister(true);
    }

    public BaseArrayAdapter<WorksSubscription> onCreateAdapter() {
        return new AnonymousClass1(WorksSubscriptionItemView_.class);
    }

    protected View onCreateEmptyView() {
        return RedirectEmptyView_.build(getActivity()).hint(R.string.hint_empty_subscription);
    }

    @ItemClick({2131558593})
    void onListItemClicked(WorksSubscription worksSubscription) {
        WorksProfileFragment_.builder().worksId(worksSubscription.worksId).build().showAsActivity((Fragment) this);
    }

    public void onEventMainThread(SubscriptionCanceledEvent event) {
        replaceLister(this.mSubscriptionManager.subscriptionLister(true));
    }
}
