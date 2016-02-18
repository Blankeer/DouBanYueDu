package com.douban.book.reader.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.lib.view.BadgeTextView;
import com.douban.book.reader.manager.ShelfManager_;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class NavigationDrawerFragment_ extends NavigationDrawerFragment implements HasViews, OnViewChangedListener {
    private View contentView_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public static class FragmentBuilder_ extends FragmentBuilder<FragmentBuilder_, NavigationDrawerFragment> {
        public NavigationDrawerFragment build() {
            NavigationDrawerFragment_ fragment_ = new NavigationDrawerFragment_();
            fragment_.setArguments(this.args);
            return fragment_;
        }
    }

    public NavigationDrawerFragment_() {
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
            this.contentView_ = inflater.inflate(R.layout.frag_navigation_drawer, container, false);
        }
        return this.contentView_;
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.contentView_ = null;
        this.mStatusBarShadow = null;
        this.mUserSummary = null;
        this.mTestField = null;
        this.mTestWorks = null;
        this.mShelfMenuItem = null;
        this.mMenuStore = null;
        this.mMenuSearch = null;
        this.mMenuSetting = null;
        this.mMenuTheme = null;
    }

    private void init_(Bundle savedInstanceState) {
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        this.mShelfManager = ShelfManager_.getInstance_(getActivity());
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.onViewChangedNotifier_.notifyViewChanged(this);
    }

    public static FragmentBuilder_ builder() {
        return new FragmentBuilder_();
    }

    public void onViewChanged(HasViews hasViews) {
        this.mStatusBarShadow = hasViews.findViewById(R.id.status_bar_shadow);
        this.mUserSummary = hasViews.findViewById(R.id.user_summary);
        this.mTestField = hasViews.findViewById(R.id.test_field);
        this.mTestWorks = hasViews.findViewById(R.id.test_works);
        this.mShelfMenuItem = (BadgeTextView) hasViews.findViewById(R.id.drawer_menu_shelf);
        this.mMenuStore = (TextView) hasViews.findViewById(R.id.drawer_menu_store);
        this.mMenuSearch = (TextView) hasViews.findViewById(R.id.drawer_menu_search);
        this.mMenuSetting = (TextView) hasViews.findViewById(R.id.drawer_menu_settings);
        this.mMenuTheme = (TextView) hasViews.findViewById(R.id.drawer_menu_theme);
        View view_drawer_menu_test_field = hasViews.findViewById(R.id.drawer_menu_test_field);
        View view_drawer_menu_test_works = hasViews.findViewById(R.id.drawer_menu_test_works);
        if (this.mMenuStore != null) {
            this.mMenuStore.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    NavigationDrawerFragment_.this.performClick(view);
                }
            });
        }
        if (this.mShelfMenuItem != null) {
            this.mShelfMenuItem.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    NavigationDrawerFragment_.this.performClick(view);
                }
            });
        }
        if (this.mMenuSearch != null) {
            this.mMenuSearch.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    NavigationDrawerFragment_.this.performClick(view);
                }
            });
        }
        if (this.mMenuSetting != null) {
            this.mMenuSetting.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    NavigationDrawerFragment_.this.performClick(view);
                }
            });
            this.mMenuSetting.setOnLongClickListener(new OnLongClickListener() {
                public boolean onLongClick(View view) {
                    NavigationDrawerFragment_.this.onSettingLongClicked();
                    return true;
                }
            });
        }
        if (view_drawer_menu_test_field != null) {
            view_drawer_menu_test_field.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    NavigationDrawerFragment_.this.performClick(view);
                }
            });
        }
        if (view_drawer_menu_test_works != null) {
            view_drawer_menu_test_works.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    NavigationDrawerFragment_.this.performClick(view);
                }
            });
        }
        if (this.mMenuTheme != null) {
            this.mMenuTheme.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    NavigationDrawerFragment_.this.onThemeClicked();
                }
            });
        }
        init();
    }
}
