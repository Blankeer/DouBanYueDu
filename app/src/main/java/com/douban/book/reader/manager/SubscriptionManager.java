package com.douban.book.reader.manager;

import com.douban.book.reader.entity.WorksSubscription;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EBean.Scope;

@EBean(scope = Scope.Singleton)
public class SubscriptionManager extends BaseManager<WorksSubscription> {
    public SubscriptionManager() {
        super("/subscriptions", WorksSubscription.class);
    }

    public Lister<WorksSubscription> subscriptionLister(boolean history) {
        if (history) {
            return defaultLister().filter(new DataFilter().appendIfNotNull("is_in_history", Integer.valueOf(1)));
        }
        return defaultLister();
    }
}
