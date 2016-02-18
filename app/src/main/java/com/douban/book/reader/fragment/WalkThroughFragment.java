package com.douban.book.reader.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import com.douban.book.reader.R;
import com.douban.book.reader.activity.HomeActivity;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.fragment.WalkThroughPageFragment.Listener;
import com.douban.book.reader.util.Pref;
import com.viewpagerindicator.CirclePageIndicator;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(2130903113)
public class WalkThroughFragment extends BaseFragment {
    private static final int[] PAGE_IMG_RES_ARRAY;
    @ViewById(2131558666)
    CirclePageIndicator mCircleIndicator;
    @ViewById(2131558648)
    ViewPager mViewPager;

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public int getCount() {
            return WalkThroughFragment.PAGE_IMG_RES_ARRAY.length;
        }

        public Fragment getItem(int i) {
            return WalkThroughPageFragment_.builder().pageImgResId(WalkThroughFragment.PAGE_IMG_RES_ARRAY[i]).showCloseBtn(i == WalkThroughFragment.PAGE_IMG_RES_ARRAY.length + -1).build().listener(new Listener() {
                public void onClosed() {
                    Pref.ofApp().set(Key.APP_WALK_THROUGH_SHOWN, Boolean.valueOf(true));
                    HomeActivity.showHomeEnsuringLogin(PageOpenHelper.from(WalkThroughFragment.this));
                    WalkThroughFragment.this.finish();
                }
            });
        }
    }

    static {
        PAGE_IMG_RES_ARRAY = new int[]{R.drawable.walk_through_page_1, R.drawable.walk_through_page_2};
    }

    @AfterViews
    void init() {
        this.mViewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager()));
        this.mViewPager.setOffscreenPageLimit(2);
        this.mCircleIndicator.setViewPager(this.mViewPager);
    }

    public static boolean hasShown() {
        return Pref.ofApp().getBoolean(Key.APP_WALK_THROUGH_SHOWN, false);
    }
}
