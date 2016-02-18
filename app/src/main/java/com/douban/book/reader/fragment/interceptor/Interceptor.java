package com.douban.book.reader.fragment.interceptor;

import android.content.Intent;
import com.douban.book.reader.app.PageOpenHelper;

public interface Interceptor {
    void performShowAsActivity(PageOpenHelper pageOpenHelper, Intent intent);
}
