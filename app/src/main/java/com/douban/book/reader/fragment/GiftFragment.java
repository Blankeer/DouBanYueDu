package com.douban.book.reader.fragment;

import com.douban.book.reader.R;
import org.androidannotations.annotations.EFragment;

@EFragment
public class GiftFragment extends TabFragment {
    public GiftFragment() {
        setTitle((int) R.string.title_gift);
        appendTab(GiftPackListFragment_.builder().build());
        appendTab(GiftListFragment_.builder().build());
    }
}
