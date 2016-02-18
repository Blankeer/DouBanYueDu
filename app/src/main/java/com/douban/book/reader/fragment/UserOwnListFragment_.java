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
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.manager.WorksManager_;
import com.mcxiaoke.next.ui.endless.EndlessListView;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class UserOwnListFragment_ extends UserOwnListFragment implements HasViews, OnViewChangedListener {
    private View contentView_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public static class FragmentBuilder_ extends FragmentBuilder<FragmentBuilder_, UserOwnListFragment> {
        public UserOwnListFragment build() {
            UserOwnListFragment_ fragment_ = new UserOwnListFragment_();
            fragment_.setArguments(this.args);
            return fragment_;
        }
    }

    public UserOwnListFragment_() {
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
        this.mWorksList = null;
    }

    private void init_(Bundle savedInstanceState) {
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        this.mWorksManager = WorksManager_.getInstance_(getActivity());
        setHasOptionsMenu(true);
        restoreSavedInstanceState_(savedInstanceState);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.onViewChangedNotifier_.notifyViewChanged(this);
    }

    public static FragmentBuilder_ builder() {
        return new FragmentBuilder_();
    }

    public void onViewChanged(HasViews hasViews) {
        this.mWorksList = (EndlessListView) hasViews.findViewById(R.id.list);
        if (this.mWorksList != null) {
            this.mWorksList.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    UserOwnListFragment_.this.onWorksItemClicked((Works) parent.getAdapter().getItem(position));
                }
            });
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.user_own, menu);
        this.mMenuViewModeItem = menu.findItem(R.id.action_view_mode);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId_ = item.getItemId();
        if (itemId_ == R.id.action_view_mode) {
            onViewModeItemClicked();
            return true;
        } else if (itemId_ != R.id.action_search) {
            return super.onOptionsItemSelected(item);
        } else {
            onSearchItemClicked();
            return true;
        }
    }

    public void onSaveInstanceState(Bundle bundle_) {
        super.onSaveInstanceState(bundle_);
        bundle_.putBoolean("mBriefMode", this.mBriefMode);
    }

    private void restoreSavedInstanceState_(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.mBriefMode = savedInstanceState.getBoolean("mBriefMode");
        }
    }
}
