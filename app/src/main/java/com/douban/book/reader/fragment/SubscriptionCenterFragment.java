package com.douban.book.reader.fragment;

import com.douban.book.reader.R;
import org.androidannotations.annotations.EFragment;

@EFragment
public class SubscriptionCenterFragment extends TabFragment {
    public SubscriptionCenterFragment() {
        appendTab(WorksSubscriptionFragment_.builder().build().setTitle((int) R.string.title_works_subscription));
        appendTab(SubscriptionHistoryFragment_.builder().build().setTitle((int) R.string.title_subscription_history));
        setTitle((int) R.string.title_my_subscriptions);
    }
}
