package com.google.tagmanager;

import android.net.Uri;
import com.j256.ormlite.stmt.query.SimpleComparison;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.realm.internal.Table;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

class PreviewManager {
    static final String BASE_DEBUG_QUERY = "&gtm_debug=x";
    private static final String CONTAINER_BASE_PATTERN = "^tagmanager.c.\\S+:\\/\\/preview\\/p\\?id=\\S+&";
    private static final String CONTAINER_DEBUG_STRING_PATTERN = ".*?&gtm_debug=x$";
    private static final String CONTAINER_PREVIEW_EXIT_URL_PATTERN = "^tagmanager.c.\\S+:\\/\\/preview\\/p\\?id=\\S+&gtm_preview=$";
    private static final String CONTAINER_PREVIEW_URL_PATTERN = "^tagmanager.c.\\S+:\\/\\/preview\\/p\\?id=\\S+&gtm_auth=\\S+&gtm_preview=\\d+(&gtm_debug=x)?$";
    static final String CTFE_URL_PATH_PREFIX = "/r?";
    private static PreviewManager sInstance;
    private volatile String mCTFEUrlPath;
    private volatile String mCTFEUrlQuery;
    private volatile String mContainerId;
    private volatile PreviewMode mPreviewMode;

    enum PreviewMode {
        NONE,
        CONTAINER,
        CONTAINER_DEBUG
    }

    PreviewManager() {
        clear();
    }

    static PreviewManager getInstance() {
        PreviewManager previewManager;
        synchronized (PreviewManager.class) {
            if (sInstance == null) {
                sInstance = new PreviewManager();
            }
            previewManager = sInstance;
        }
        return previewManager;
    }

    synchronized boolean setPreviewData(Uri data) {
        boolean z = true;
        synchronized (this) {
            try {
                String uriStr = URLDecoder.decode(data.toString(), HttpRequest.CHARSET_UTF8);
                if (uriStr.matches(CONTAINER_PREVIEW_URL_PATTERN)) {
                    Log.v("Container preview url: " + uriStr);
                    if (uriStr.matches(CONTAINER_DEBUG_STRING_PATTERN)) {
                        this.mPreviewMode = PreviewMode.CONTAINER_DEBUG;
                    } else {
                        this.mPreviewMode = PreviewMode.CONTAINER;
                    }
                    this.mCTFEUrlQuery = getQueryWithoutDebugParameter(data);
                    if (this.mPreviewMode == PreviewMode.CONTAINER || this.mPreviewMode == PreviewMode.CONTAINER_DEBUG) {
                        this.mCTFEUrlPath = CTFE_URL_PATH_PREFIX + this.mCTFEUrlQuery;
                    }
                    this.mContainerId = getContainerId(this.mCTFEUrlQuery);
                } else {
                    if (!uriStr.matches(CONTAINER_PREVIEW_EXIT_URL_PATTERN)) {
                        Log.w("Invalid preview uri: " + uriStr);
                        z = false;
                    } else if (getContainerId(data.getQuery()).equals(this.mContainerId)) {
                        Log.v("Exit preview mode for container: " + this.mContainerId);
                        this.mPreviewMode = PreviewMode.NONE;
                        this.mCTFEUrlPath = null;
                    } else {
                        z = false;
                    }
                }
            } catch (UnsupportedEncodingException e) {
                z = false;
            }
        }
        return z;
    }

    private String getQueryWithoutDebugParameter(Uri data) {
        return data.getQuery().replace(BASE_DEBUG_QUERY, Table.STRING_DEFAULT_VALUE);
    }

    PreviewMode getPreviewMode() {
        return this.mPreviewMode;
    }

    String getCTFEUrlPath() {
        return this.mCTFEUrlPath;
    }

    String getContainerId() {
        return this.mContainerId;
    }

    String getCTFEUrlDebugQuery() {
        return this.mCTFEUrlQuery;
    }

    void clear() {
        this.mPreviewMode = PreviewMode.NONE;
        this.mCTFEUrlPath = null;
        this.mContainerId = null;
        this.mCTFEUrlQuery = null;
    }

    private String getContainerId(String query) {
        return query.split("&")[0].split(SimpleComparison.EQUAL_TO_OPERATION)[1];
    }
}
