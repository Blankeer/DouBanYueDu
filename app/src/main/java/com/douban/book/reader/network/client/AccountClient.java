package com.douban.book.reader.network.client;

import android.net.Uri;
import android.net.Uri.Builder;
import com.douban.book.reader.constant.Constants;

public class AccountClient extends JsonClient {
    private static final Uri BASE_URI;

    static {
        BASE_URI = new Builder().scheme(Constants.API_SCHEME).authority(Constants.DOUBAN_API_HOST).build();
    }

    public AccountClient(String relativeUri) {
        super(BASE_URI.buildUpon().appendEncodedPath(relativeUri).build());
    }
}
