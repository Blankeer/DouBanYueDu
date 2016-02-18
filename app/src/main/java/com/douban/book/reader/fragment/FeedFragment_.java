package com.douban.book.reader.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.Feed;
import com.douban.book.reader.manager.FeedManager_;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class FeedFragment_ extends FeedFragment implements HasViews, OnViewChangedListener {
    private View contentView_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.fragment.FeedFragment_.2 */
    class AnonymousClass2 implements Runnable {
        final /* synthetic */ Feed val$msg;

        AnonymousClass2(Feed feed) {
            this.val$msg = feed;
        }

        public void run() {
            super.removeMsgInAdapter(this.val$msg);
        }
    }

    /* renamed from: com.douban.book.reader.fragment.FeedFragment_.4 */
    class AnonymousClass4 extends Task {
        AnonymousClass4(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.markAllAsRead();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    /* renamed from: com.douban.book.reader.fragment.FeedFragment_.5 */
    class AnonymousClass5 extends Task {
        final /* synthetic */ Feed val$msg;

        AnonymousClass5(String x0, long x1, String x2, Feed feed) {
            this.val$msg = feed;
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.markAsRead(this.val$msg);
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    /* renamed from: com.douban.book.reader.fragment.FeedFragment_.6 */
    class AnonymousClass6 extends Task {
        final /* synthetic */ Feed val$msg;

        AnonymousClass6(String x0, long x1, String x2, Feed feed) {
            this.val$msg = feed;
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.deleteMsg(this.val$msg);
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public static class FragmentBuilder_ extends FragmentBuilder<FragmentBuilder_, FeedFragment> {
        public FeedFragment build() {
            FeedFragment_ fragment_ = new FeedFragment_();
            fragment_.setArguments(this.args);
            return fragment_;
        }
    }

    public FeedFragment_() {
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
        this.mFeedsManager = FeedManager_.getInstance_(getActivity());
        setHasOptionsMenu(true);
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
                    FeedFragment_.this.onListItemClicked((Feed) parent.getAdapter().getItem(position));
                }
            });
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.feeds_chapter_updated, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId_ = item.getItemId();
        if (itemId_ == R.id.action_open_subscription_center) {
            onMenuItemOpenClicked();
            return true;
        } else if (itemId_ != R.id.action_mark_all_read) {
            return super.onOptionsItemSelected(item);
        } else {
            onMenuItemMarkAllRead();
            return true;
        }
    }

    void removeMsgInAdapter(Feed msg) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass2(msg), 0);
    }

    void markAllAsReadInAdapter() {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new Runnable() {
            public void run() {
                super.markAllAsReadInAdapter();
            }
        }, 0);
    }

    void markAllAsRead() {
        BackgroundExecutor.execute(new AnonymousClass4(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }

    void markAsRead(Feed msg) {
        BackgroundExecutor.execute(new AnonymousClass5(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE, msg));
    }

    void deleteMsg(Feed msg) {
        BackgroundExecutor.execute(new AnonymousClass6(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE, msg));
    }
}
