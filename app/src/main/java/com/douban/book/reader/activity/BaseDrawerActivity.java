package com.douban.book.reader.activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.douban.book.reader.R;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.event.NavigationDrawerClosedEvent;
import com.douban.book.reader.event.NavigationDrawerOpenedEvent;
import com.douban.book.reader.fragment.BaseFragment;
import com.douban.book.reader.fragment.NavigationDrawerFragment_;
import com.douban.book.reader.helper.DrawerItemClickHelper;
import com.douban.book.reader.panel.Transaction;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.StringUtils;

public abstract class BaseDrawerActivity extends BaseActivity {
    private int mClickedDrawerMenuItemResId;
    private DrawerItemClickHelper mDrawerItemClickHelper;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    /* renamed from: com.douban.book.reader.activity.BaseDrawerActivity.1 */
    class AnonymousClass1 extends ActionBarDrawerToggle {
        AnonymousClass1(Activity arg0, DrawerLayout arg1, int arg2, int arg3) {
            super(arg0, arg1, arg2, arg3);
        }

        public void onDrawerStateChanged(int newState) {
            super.onDrawerStateChanged(newState);
            BaseDrawerActivity.this.invalidateOptionsMenu();
        }

        public void onDrawerClosed(View drawerView) {
            super.onDrawerClosed(drawerView);
            if (BaseDrawerActivity.this.mClickedDrawerMenuItemResId != 0) {
                BaseDrawerActivity.this.onDrawerItemSelected(BaseDrawerActivity.this.mClickedDrawerMenuItemResId);
                BaseDrawerActivity.this.mClickedDrawerMenuItemResId = 0;
            }
            BaseDrawerActivity.this.refreshTitleWithContent();
            EventBusUtils.post(new NavigationDrawerClosedEvent());
        }

        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            BaseDrawerActivity.this.setTitle((int) R.string.app_name);
            EventBusUtils.post(new NavigationDrawerOpenedEvent());
        }
    }

    public BaseDrawerActivity() {
        this.mClickedDrawerMenuItemResId = 0;
        this.mDrawerItemClickHelper = new DrawerItemClickHelper();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.act_base_drawer);
        this.mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.mDrawerToggle = new AnonymousClass1(this, this.mDrawerLayout, R.string.content_description_drawer_open, R.string.content_description_drawer_close);
        this.mDrawerLayout.setDrawerListener(this.mDrawerToggle);
        onBindDrawer();
    }

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        this.mDrawerToggle.syncState();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerVisible = this.mDrawerLayout.isDrawerVisible((int) GravityCompat.START);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            menuItem.setVisible(!drawerVisible);
            if (drawerVisible) {
                menuItem.collapseActionView();
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (this.mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onStop() {
        super.onStop();
        if (this.mDrawerLayout != null) {
            this.mDrawerLayout.closeDrawers();
        }
    }

    public void setShowDrawerToggle(boolean showDrawerToggle) {
        this.mDrawerToggle.setDrawerIndicatorEnabled(showDrawerToggle);
    }

    public void performDrawerItemClick(int clickedResId) {
        this.mClickedDrawerMenuItemResId = clickedResId;
        if (this.mDrawerLayout != null) {
            this.mDrawerLayout.closeDrawers();
        }
    }

    public void showContent(Class<? extends BaseFragment> cls, String referrer) {
        try {
            Fragment fragment = (BaseFragment) cls.newInstance();
            fragment.setShouldBeConsideredAsAPage(true);
            fragment.setTitleUpdatedToActivity(true);
            if (StringUtils.isNotEmpty(referrer)) {
                fragment.appendArgument(PageOpenHelper.KEY_REFERRER, referrer);
            }
            Transaction.begin((FragmentActivity) this, (int) R.id.frag_container).replace(fragment).commit();
            refreshTitleWithContent();
        } catch (Exception e) {
            Logger.e(this.TAG, e);
        }
    }

    public Fragment getContentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.frag_container);
    }

    public void onDrawerItemSelected(int selectedResId) {
        PageOpenHelper helper;
        Fragment fragment = getContentFragment();
        if (fragment != null) {
            helper = PageOpenHelper.from(fragment);
        } else {
            helper = PageOpenHelper.from((Activity) this);
        }
        this.mDrawerItemClickHelper.performClick(selectedResId, helper);
    }

    protected void onBindDrawer() {
        Transaction.begin((FragmentActivity) this, (int) R.id.frag_drawer).show(NavigationDrawerFragment_.class).commit();
    }

    private void refreshTitleWithContent() {
        getSupportFragmentManager().executePendingTransactions();
        BaseFragment displayedFragment = (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.frag_container);
        if (displayedFragment != null) {
            if (StringUtils.isNotEmpty(displayedFragment.getTitle())) {
                setTitle(displayedFragment.getTitle());
                return;
            }
        }
        setTitle((int) R.string.app_name);
    }
}
