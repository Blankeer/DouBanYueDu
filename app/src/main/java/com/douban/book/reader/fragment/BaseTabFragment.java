package com.douban.book.reader.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.StringUtils;
import com.viewpagerindicator.PageIndicator;
import io.realm.internal.Table;
import java.util.ArrayList;
import java.util.List;

public class BaseTabFragment<T extends BaseTabFragment> extends BaseFragment {
    private static final String KEY_CURRENT_TAB = "current_tab";
    private int mDefaultPage;
    protected List<BaseFragment> mFragmentList;
    private int mOffScreenPageLimit;
    protected PageIndicator mPageIndicator;
    protected TabFragmentAdapter mTabAdapter;
    protected ViewPager mViewPager;

    public class TabFragmentAdapter extends FragmentPagerAdapter {
        private int mLastPosition;

        public TabFragmentAdapter(FragmentManager fm) {
            super(fm);
            this.mLastPosition = -1;
        }

        public int getCount() {
            return BaseTabFragment.this.mFragmentList.size();
        }

        public Fragment getItem(int position) {
            try {
                Fragment fragment = (Fragment) BaseTabFragment.this.mFragmentList.get(position);
                if (fragment.isAdded()) {
                    return fragment;
                }
                Bundle parentBundle = BaseTabFragment.this.getArguments();
                if (parentBundle == null || parentBundle.isEmpty()) {
                    return fragment;
                }
                if (!StringUtils.isNotEmpty(parentBundle.getString(PageOpenHelper.KEY_REFERRER))) {
                    return fragment;
                }
                ((BaseFragment) fragment).appendArgument(PageOpenHelper.KEY_REFERRER, parentBundle.getString(PageOpenHelper.KEY_REFERRER));
                return fragment;
            } catch (IndexOutOfBoundsException e) {
                return null;
            }
        }

        public CharSequence getPageTitle(int position) {
            Fragment fragment = getItem(position);
            if (fragment instanceof BaseFragment) {
                return RichText.textWithIcon(((BaseFragment) fragment).getIcon(), ((BaseFragment) fragment).getTitle());
            }
            return Table.STRING_DEFAULT_VALUE;
        }

        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            if (position != this.mLastPosition) {
                Fragment fragment = getItem(position);
                if (fragment instanceof BaseFragment) {
                    ((BaseFragment) fragment).onVisible();
                }
                Fragment lastFragment = getItem(this.mLastPosition);
                if (lastFragment instanceof BaseFragment) {
                    ((BaseFragment) lastFragment).onInvisible();
                }
                this.mLastPosition = position;
            }
        }
    }

    public BaseTabFragment() {
        this.mFragmentList = new ArrayList();
        this.mDefaultPage = 0;
        this.mOffScreenPageLimit = 1;
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mTabAdapter = new TabFragmentAdapter(getChildFragmentManager());
        setShouldBeConsideredAsAPage(false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.mViewPager.setOffscreenPageLimit(this.mOffScreenPageLimit);
        if (savedInstanceState != null) {
            this.mDefaultPage = savedInstanceState.getInt(KEY_CURRENT_TAB);
        }
        updateDefaultPage();
    }

    public void onSaveInstanceState(Bundle outState) {
        if (this.mViewPager != null) {
            outState.putInt(KEY_CURRENT_TAB, this.mViewPager.getCurrentItem());
        }
        super.onSaveInstanceState(outState);
    }

    public void setOffScreenPageLimit(int limit) {
        this.mOffScreenPageLimit = limit;
        if (this.mViewPager != null) {
            this.mViewPager.setOffscreenPageLimit(this.mOffScreenPageLimit);
        }
    }

    public T appendTab(BaseFragment fragment) {
        fragment.setShouldBeConsideredAsAPage(true);
        fragment.setVisibleLifeCycleManuallyControlled(true);
        this.mFragmentList.add(fragment);
        if (this.mTabAdapter != null) {
            this.mTabAdapter.notifyDataSetChanged();
        }
        if (this.mPageIndicator != null) {
            this.mPageIndicator.notifyDataSetChanged();
        }
        return self();
    }

    public T appendTabIf(boolean condition, BaseFragment fragment) {
        if (condition) {
            appendTab(fragment);
        }
        return self();
    }

    protected void setViewPager(ViewPager viewPager) {
        this.mViewPager = viewPager;
        this.mViewPager.setAdapter(this.mTabAdapter);
    }

    protected void setPageIndicator(PageIndicator pageIndicator) {
        this.mPageIndicator = pageIndicator;
        if (this.mViewPager != null) {
            this.mPageIndicator.setViewPager(this.mViewPager);
        }
    }

    public void setDefaultPage(int page) {
        this.mDefaultPage = page;
        updateDefaultPage();
    }

    public BaseFragment getCurrentTab() {
        return (BaseFragment) this.mFragmentList.get(this.mViewPager.getCurrentItem());
    }

    public int getCurrentTabIndex() {
        return this.mViewPager.getCurrentItem();
    }

    protected void refreshTab() {
        for (Fragment fragment : this.mFragmentList) {
            if (fragment instanceof Refreshable) {
                ((Refreshable) fragment).onRefresh();
            }
        }
    }

    private void updateDefaultPage() {
        if (this.mViewPager != null && this.mViewPager.getAdapter() != null && this.mDefaultPage < this.mViewPager.getAdapter().getCount()) {
            this.mViewPager.setCurrentItem(this.mDefaultPage);
        }
    }

    private T self() {
        return this;
    }
}
