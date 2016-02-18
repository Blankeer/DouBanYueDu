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
import com.douban.book.reader.entity.Notification;
import com.douban.book.reader.manager.NotificationManager_;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class NotificationListFragment_ extends NotificationListFragment implements HasViews, OnViewChangedListener {
    private View contentView_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.fragment.NotificationListFragment_.2 */
    class AnonymousClass2 implements Runnable {
        final /* synthetic */ Notification val$notification;

        AnonymousClass2(Notification notification) {
            this.val$notification = notification;
        }

        public void run() {
            super.removeNotificationInAdapter(this.val$notification);
        }
    }

    /* renamed from: com.douban.book.reader.fragment.NotificationListFragment_.4 */
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

    /* renamed from: com.douban.book.reader.fragment.NotificationListFragment_.5 */
    class AnonymousClass5 extends Task {
        final /* synthetic */ Notification val$notification;

        AnonymousClass5(String x0, long x1, String x2, Notification notification) {
            this.val$notification = notification;
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.deleteNotification(this.val$notification);
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    /* renamed from: com.douban.book.reader.fragment.NotificationListFragment_.6 */
    class AnonymousClass6 extends Task {
        final /* synthetic */ Notification val$notification;

        AnonymousClass6(String x0, long x1, String x2, Notification notification) {
            this.val$notification = notification;
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.markAsRead(this.val$notification);
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public static class FragmentBuilder_ extends FragmentBuilder<FragmentBuilder_, NotificationListFragment> {
        public NotificationListFragment build() {
            NotificationListFragment_ fragment_ = new NotificationListFragment_();
            fragment_.setArguments(this.args);
            return fragment_;
        }
    }

    public NotificationListFragment_() {
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
        this.mNotificationManager = NotificationManager_.getInstance_(getActivity());
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
                    NotificationListFragment_.this.onItemClicked((Notification) parent.getAdapter().getItem(position));
                }
            });
        }
        init();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.notification, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != R.id.action_mark_all_read) {
            return super.onOptionsItemSelected(item);
        }
        onMenuItemMarkClicked();
        return true;
    }

    void removeNotificationInAdapter(Notification notification) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass2(notification), 0);
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

    void deleteNotification(Notification notification) {
        BackgroundExecutor.execute(new AnonymousClass5(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE, notification));
    }

    void markAsRead(Notification notification) {
        BackgroundExecutor.execute(new AnonymousClass6(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE, notification));
    }
}
