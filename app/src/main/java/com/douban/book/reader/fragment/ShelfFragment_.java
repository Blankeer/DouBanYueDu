package com.douban.book.reader.fragment;

import android.app.SearchManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.ShelfItem;
import com.douban.book.reader.manager.ShelfManager_;
import io.realm.internal.Table;
import java.util.List;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class ShelfFragment_ extends ShelfFragment implements HasViews, OnViewChangedListener {
    private View contentView_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.fragment.ShelfFragment_.4 */
    class AnonymousClass4 implements Runnable {
        final /* synthetic */ List val$worksList;

        AnonymousClass4(List list) {
            this.val$worksList = list;
        }

        public void run() {
            super.updateView(this.val$worksList);
        }
    }

    /* renamed from: com.douban.book.reader.fragment.ShelfFragment_.6 */
    class AnonymousClass6 extends Task {
        AnonymousClass6(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.loadData();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public static class FragmentBuilder_ extends FragmentBuilder<FragmentBuilder_, ShelfFragment> {
        public ShelfFragment build() {
            ShelfFragment_ fragment_ = new ShelfFragment_();
            fragment_.setArguments(this.args);
            return fragment_;
        }
    }

    public ShelfFragment_() {
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
        if (this.contentView_ == null) {
            this.contentView_ = inflater.inflate(R.layout.frag_shelf, container, false);
        }
        return this.contentView_;
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.contentView_ = null;
        this.mGridView = null;
        this.mSearchEmptyView = null;
        this.mShelfEmptyView = null;
        this.mOpenStore = null;
        this.mOpenMyWorks = null;
    }

    private void init_(Bundle savedInstanceState) {
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        this.mSearchManager = (SearchManager) getActivity().getSystemService("search");
        this.mShelfManager = ShelfManager_.getInstance_(getActivity());
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
        this.mGridView = (GridView) hasViews.findViewById(R.id.shelf_grid);
        this.mSearchEmptyView = (TextView) hasViews.findViewById(R.id.search_empty_hint);
        this.mShelfEmptyView = hasViews.findViewById(R.id.shelf_empty_view);
        this.mOpenStore = (Button) hasViews.findViewById(R.id.btn_to_store);
        this.mOpenMyWorks = (Button) hasViews.findViewById(R.id.btn_to_user_own);
        if (this.mOpenStore != null) {
            this.mOpenStore.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    ShelfFragment_.this.onBtnToStoreClicked();
                }
            });
        }
        if (this.mOpenMyWorks != null) {
            this.mOpenMyWorks.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    ShelfFragment_.this.onBtnToUserOwn();
                }
            });
        }
        if (this.mGridView != null) {
            this.mGridView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ShelfFragment_.this.onShelfItemClicked((ShelfItem) parent.getAdapter().getItem(position));
                }
            });
        }
        init();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.shelf, menu);
        this.mMenuSearch = menu.findItem(R.id.action_search);
        super.onCreateOptionsMenu(menu, inflater);
    }

    void updateView(List<ShelfItem> worksList) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass4(worksList), 0);
    }

    void updateShelfEmptyView() {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new Runnable() {
            public void run() {
                super.updateShelfEmptyView();
            }
        }, 0);
    }

    void loadData() {
        BackgroundExecutor.execute(new AnonymousClass6(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }
}
