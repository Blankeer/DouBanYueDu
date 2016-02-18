package com.douban.book.reader.fragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;

@EFragment
public class AgreementFragment extends BaseWebFragment {
    private static final String URL = "http://accounts.douban.com/app/agreement";

    @AfterViews
    void init() {
        loadUrl(URL);
    }
}
