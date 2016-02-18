package com.douban.book.reader.network.client;

import android.net.Uri;
import com.douban.book.reader.util.UriUtils;

public class StringClient extends RestClient<String> {
    public StringClient(String uri) {
        super(UriUtils.resolveRelativeUri(uri), String.class);
    }

    public StringClient(Uri uri) {
        super(uri, String.class);
    }
}
