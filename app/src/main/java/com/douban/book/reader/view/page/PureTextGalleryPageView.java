package com.douban.book.reader.view.page;

import android.content.Context;
import com.douban.book.reader.R;

public class PureTextGalleryPageView extends AbsGalleryPageView {
    public PureTextGalleryPageView(Context context) {
        super(context);
    }

    protected void initView() {
        inflate(getContext(), R.layout.legend_page_view_gallery, this);
        super.initView();
    }
}
