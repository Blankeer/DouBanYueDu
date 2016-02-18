package com.douban.book.reader.network.client;

import android.net.Uri;
import com.douban.book.reader.util.UriUtils;
import org.json.JSONObject;

public class JsonClient extends RestClient<JSONObject> {
    public JsonClient(String uri) {
        super(UriUtils.resolveRelativeUri(uri), JSONObject.class);
    }

    public JsonClient(Uri uri) {
        super(uri, JSONObject.class);
    }
}
