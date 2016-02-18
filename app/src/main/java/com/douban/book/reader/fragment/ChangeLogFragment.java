package com.douban.book.reader.fragment;

import com.douban.book.reader.helper.StoreUriHelper;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;

@EFragment
public class ChangeLogFragment extends BaseWebFragment {
    @AfterViews
    void init() {
        loadUrl(StoreUriHelper.changelog());
    }
}
