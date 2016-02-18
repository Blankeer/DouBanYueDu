package com.douban.book.reader.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.douban.book.reader.constant.Constants;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.view.page.TouchPage;
import java.util.HashMap;

public class PageFragment extends BaseFragment {
    int mBookId;
    private boolean mPageInvalidated;
    int mPageNo;
    TouchPage mTouchPage;

    public PageFragment() {
        this.mPageInvalidated = false;
        this.mTouchPage = null;
    }

    public static PageFragment getInstance(int bookid, int pagenum) {
        PageFragment f = new PageFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.KEY_BOOK_ID, bookid);
        args.putInt(Constants.KEY_PAGE_NO, pagenum);
        f.setArguments(args);
        return f;
    }

    public void updateViewList(HashMap<Integer, PageFragment> pageFragments) {
        if (this.mTouchPage == null) {
            try {
                this.mTouchPage = new TouchPage(getActivity());
                this.mTouchPage.setPage(this.mBookId, this.mPageNo);
            } catch (Throwable e) {
                Logger.e(this.TAG, e);
            }
        }
        if (this.mTouchPage != null) {
            this.mTouchPage.updateTouchPages(pageFragments);
        }
    }

    public void onVisible(int showPage) {
        if (this.mTouchPage != null) {
            this.mTouchPage.onVisible(showPage);
        }
    }

    public void onInvisible() {
        if (this.mTouchPage != null) {
            this.mTouchPage.onInvisible();
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args == null) {
            throw new IllegalArgumentException("args cannot be null");
        }
        this.mBookId = args.getInt(Constants.KEY_BOOK_ID, 0);
        this.mPageNo = args.getInt(Constants.KEY_PAGE_NO, 1);
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mTouchPage = new TouchPage(inflater.getContext());
        this.mTouchPage.setPage(this.mBookId, this.mPageNo);
        this.mPageInvalidated = false;
        return this.mTouchPage;
    }

    public void setPageInvalidated(boolean invalidated) {
        this.mPageInvalidated = invalidated;
    }

    public boolean isPageInvalidated() {
        return this.mPageInvalidated;
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.mTouchPage = null;
    }

    public TouchPage getTouchPage() {
        return this.mTouchPage;
    }

    public int getPageNumber() {
        return this.mPageNo;
    }
}
