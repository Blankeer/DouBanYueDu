package com.douban.book.reader.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import com.douban.book.reader.R;

public abstract class BaseContentFragment extends BaseRefreshFragment {
    protected abstract int onObtainContentViewLayoutResId();

    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.frag_base_content, container, false);
        ScrollView scrollView = (ScrollView) view.findViewById(R.id.content_scroll_base);
        if (inflater.inflate(onObtainContentViewLayoutResId(), scrollView, true) != null) {
            setAsRootOfContentView(scrollView);
        }
        return view;
    }
}
