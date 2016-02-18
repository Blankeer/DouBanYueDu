package com.douban.book.reader.util;

import android.content.Context;
import android.net.Uri;
import com.douban.book.reader.app.App;
import com.douban.book.reader.content.pack.WorksData;
import com.douban.book.reader.network.ConnectionUtils;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import io.fabric.sdk.android.services.network.HttpRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class BookImageDownloader extends BaseImageDownloader {
    private static BookImageDownloader sInstance;

    public BookImageDownloader(Context context) {
        super(context);
    }

    public static BookImageDownloader getInstance() {
        if (sInstance == null) {
            sInstance = new BookImageDownloader(App.get());
        }
        return sInstance;
    }

    protected HttpURLConnection createConnection(String url, Object extra) throws IOException {
        HttpURLConnection conn = super.createConnection(url, extra);
        conn.setRequestProperty(HttpRequest.HEADER_USER_AGENT, ConnectionUtils.getUserAgent());
        return conn;
    }

    protected InputStream getStreamFromOtherSource(String imageUri, Object extra) throws IOException {
        return WorksData.getInputStream(Uri.parse(imageUri));
    }
}
