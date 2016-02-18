package com.douban.book.reader.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.WorksSubscription;
import com.douban.book.reader.manager.SubscriptionManager_;
import com.douban.book.reader.manager.WorksManager_;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class WorksSubscriptionFragment_ extends WorksSubscriptionFragment implements HasViews, OnViewChangedListener {
    private View contentView_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.fragment.WorksSubscriptionFragment_.2 */
    class AnonymousClass2 implements Runnable {
        final /* synthetic */ WorksSubscription val$worksSubscription;

        AnonymousClass2(WorksSubscription worksSubscription) {
            this.val$worksSubscription = worksSubscription;
        }

        public void run() {
            super.cancelSubscriptionInAdapter(this.val$worksSubscription);
        }
    }

    /* renamed from: com.douban.book.reader.fragment.WorksSubscriptionFragment_.3 */
    class AnonymousClass3 extends Task {
        final /* synthetic */ WorksSubscription val$worksSubscription;

        AnonymousClass3(String x0, long x1, String x2, WorksSubscription worksSubscription) {
            this.val$worksSubscription = worksSubscription;
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.cancelSubscription(this.val$worksSubscription);
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public static class FragmentBuilder_ extends FragmentBuilder<FragmentBuilder_, WorksSubscriptionFragment> {
        public WorksSubscriptionFragment build() {
            WorksSubscriptionFragment_ fragment_ = new WorksSubscriptionFragment_();
            fragment_.setArguments(this.args);
            return fragment_;
        }
    }

    public WorksSubscriptionFragment_() {
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
    }

    public void onCreate(Bundle savedInstanceState) {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        init_(savedInstanceState);
        super.onCreate(savedInstanceState);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public View findViewById(int id) {
        if (this.contentView_ == null) {
            return null;
        }
        return this.contentView_.findViewById(id);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.contentView_ = super.onCreateView(inflater, container, savedInstanceState);
        return this.contentView_;
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.contentView_ = null;
    }

    private void init_(Bundle savedInstanceState) {
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        this.mWorkManager = WorksManager_.getInstance_(getActivity());
        this.mSubscriptionManager = SubscriptionManager_.getInstance_(getActivity());
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.onViewChangedNotifier_.notifyViewChanged(this);
    }

    public static FragmentBuilder_ builder() {
        return new FragmentBuilder_();
    }

    public void onViewChanged(HasViews hasViews) {
        AdapterView<?> view_list = (AdapterView) hasViews.findViewById(R.id.list);
        if (view_list != null) {
            view_list.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    WorksSubscriptionFragment_.this.onListItemClick((WorksSubscription) parent.getAdapter().getItem(position));
                }
            });
        }
    }

    void cancelSubscriptionInAdapter(WorksSubscription worksSubscription) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass2(worksSubscription), 0);
    }

    void cancelSubscription(WorksSubscription worksSubscription) {
        BackgroundExecutor.execute(new AnonymousClass3(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE, worksSubscription));
    }
}
