package com.douban.book.reader.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.douban.book.reader.R;
import com.douban.book.reader.view.TabPageIndicator;

public class TabFragment extends BaseTabFragment<TabFragment> {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_tab, container, false);
        setViewPager((ViewPager) view.findViewById(R.id.view_pager));
        setPageIndicator((TabPageIndicator) view.findViewById(R.id.view_pager_indicator));
        return view;
    }
}
