package com.douban.book.reader.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;
import com.douban.book.reader.content.Book;
import com.douban.book.reader.fragment.PageFragment;
import java.util.HashMap;

public class BookPagerAdapter extends FragmentStatePagerAdapter {
    public static final String TAG;
    int mCount;
    PageFragment mCurrentPage;
    int mCurrentPageNo;
    private HashMap<Integer, PageFragment> mFragmentList;
    private int mWorksId;

    static {
        TAG = BookPagerAdapter.class.getSimpleName();
    }

    public BookPagerAdapter(FragmentManager fm, int worksId) {
        super(fm);
        this.mCurrentPageNo = -1;
        this.mCurrentPage = null;
        this.mFragmentList = new HashMap();
        this.mWorksId = worksId;
        this.mCount = Book.get(this.mWorksId).getPageCount();
    }

    public Object instantiateItem(ViewGroup container, int position) {
        PageFragment fragment = (PageFragment) super.instantiateItem(container, position);
        this.mFragmentList.put(Integer.valueOf(position), fragment);
        return fragment;
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        this.mFragmentList.remove(Integer.valueOf(position));
    }

    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if (this.mFragmentList == null || this.mCurrentPageNo < 0 || this.mFragmentList.get(Integer.valueOf(this.mCurrentPageNo)) == null) {
            this.mCurrentPageNo = position;
        } else if (object != null) {
            PageFragment fragment = (PageFragment) object;
            ((PageFragment) object).updateViewList(this.mFragmentList);
            if (fragment.getPageNumber() != this.mCurrentPageNo) {
                ((PageFragment) this.mFragmentList.get(Integer.valueOf(this.mCurrentPageNo))).onInvisible();
                ((PageFragment) object).onVisible(position);
            }
            this.mCurrentPage = (PageFragment) object;
            this.mCurrentPageNo = position;
        }
    }

    public int getCurrentPageNo() {
        return this.mCurrentPageNo;
    }

    public int getItemPosition(Object object) {
        if (object instanceof PageFragment) {
            return ((PageFragment) object).isPageInvalidated() ? -2 : -1;
        } else {
            return super.getItemPosition(object);
        }
    }

    public void notifyDataSetChanged() {
        this.mCount = Book.get(this.mWorksId).getPageCount();
        super.notifyDataSetChanged();
    }

    public int getCount() {
        return this.mCount;
    }

    public Fragment getItem(int position) {
        return PageFragment.getInstance(this.mWorksId, position);
    }

    public void redrawAll() {
        for (PageFragment fragment : this.mFragmentList.values()) {
            fragment.setPageInvalidated(true);
        }
    }
}
