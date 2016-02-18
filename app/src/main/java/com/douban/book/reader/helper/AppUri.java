package com.douban.book.reader.helper;

import android.content.UriMatcher;
import android.net.Uri;
import android.net.Uri.Builder;
import com.douban.book.reader.entity.Annotation;
import com.douban.book.reader.fragment.BaseShareEditFragment;
import com.douban.book.reader.fragment.PurchaseFragment_;
import com.douban.book.reader.fragment.share.ShareUrlEditFragment_;
import com.douban.book.reader.util.StringUtils;

public class AppUri {
    public static final int ACCOUNT_BALANCE = 5;
    public static final int ANNOTATION = 105;
    public static final String AUTHORITY = "p";
    public static final String AUTHORITY_WEB = "read.douban.com";
    public static final int COLUMN = 1;
    public static final int COLUMN_CHAPTER_PROFILE = 102;
    public static final int COLUMN_PROFILE = 101;
    public static final int GIFT = 402;
    public static final int GIFT_LIST = 403;
    public static final int GIFT_PACK = 401;
    public static final int GIFT_PACK_CREATE = 400;
    public static final int HERMES = 2;
    public static final int LEGACY_REVIEW = 104;
    public static final int OPEN_APP_INSTEAD_OF_DOWNLOAD = 1001;
    public static final int OPEN_URL = 1000;
    public static final String PATH_OPEN_URL = "open_url";
    public static final String PATH_READER = "reader";
    public static final String PATH_WORKS_KIND = "works_kind";
    public static final String PATH_WORKS_LIST = "works_list";
    public static final int PROVIDER = 201;
    public static final int PURCHASE = 6;
    public static final int READER = 300;
    public static final int READER_COLUMN = 301;
    public static final int READER_COLUMN_CHAPTER = 302;
    public static final int REDEEM = 4;
    public static final int REVIEW = 103;
    public static final String SCHEME = "ark";
    public static final int STORE_HOME = 11;
    public static final int STORE_TAB = 10;
    public static final int SUBSCRIPTIONS = 3;
    private static final int[] URIS_OPENED_IN_NATIVE_WEBVIEW;
    public static final int WORKS_KIND = 106;
    public static final int WORKS_LIST = 200;
    public static final int WORKS_PROFILE = 100;
    private static final UriMatcher sUriMatcher;

    static {
        int[] iArr = new int[COLUMN];
        iArr[0] = READER_COLUMN_CHAPTER;
        URIS_OPENED_IN_NATIVE_WEBVIEW = iArr;
        sUriMatcher = new UriMatcher(-1);
        addWebUri("ebook/#", WORKS_PROFILE);
        addWebUri("column/#", COLUMN_PROFILE);
        addWebUri("reader/ebook/#", READER);
        addWebUri("reader/column/#", READER_COLUMN);
        addWebUri("reader/column/#/chapter/#", READER_COLUMN_CHAPTER);
        addWebUri("gift/pack", GIFT_PACK_CREATE);
        addWebUri("account/gifts/packs", GIFT_LIST);
        addWebUri("gift/*", GIFT);
        addWebUri("gift/pack/*", GIFT_PACK);
        addWebUri("review/#", LEGACY_REVIEW);
        addWebUri("rating/#", REVIEW);
        addWebUri("account/redeem", REDEEM);
        addWebUri("account", ACCOUNT_BALANCE);
        addWebUri("app/download", OPEN_APP_INSTEAD_OF_DOWNLOAD);
        addAppUri("column", COLUMN);
        addAppUri("hermes", HERMES);
        addAppUri("store_index", STORE_TAB);
        addAppUri("store_home", STORE_HOME);
        addAppUri("works_profile", WORKS_PROFILE);
        addAppUri(PATH_WORKS_LIST, WORKS_LIST);
        addAppUri("provider", PROVIDER);
        addAppUri(BaseShareEditFragment.CONTENT_TYPE_REVIEW, REVIEW);
        addAppUri(Annotation.TABLE_NAME, ANNOTATION);
        addAppUri(PATH_WORKS_KIND, WORKS_KIND);
        addAppUri("subscriptions", SUBSCRIPTIONS);
        addAppUri("redeem", REDEEM);
        addAppUri("account", ACCOUNT_BALANCE);
        addAppUri("purchase", PURCHASE);
        addAppUri("web_reader", READER_COLUMN_CHAPTER);
        addAppUri("gift_pack_create", GIFT_PACK_CREATE);
        addAppUri(BaseShareEditFragment.CONTENT_TYPE_GIFT_PACK, GIFT_PACK);
        addAppUri(BaseShareEditFragment.CONTENT_TYPE_GIFT, GIFT);
        addAppUri("gift_list", GIFT_LIST);
        addAppUri(PATH_OPEN_URL, OPEN_URL);
    }

    public static int getType(String uri) {
        return getType(Uri.parse(uri));
    }

    public static int getType(Uri uri) {
        return sUriMatcher.match(uri);
    }

    public static boolean canBeOpenedByApp(String uri) {
        return canBeOpenedByApp(Uri.parse(uri));
    }

    public static boolean canBeOpenedByApp(Uri uri) {
        return getType(uri) != -1;
    }

    public static boolean canOnlyBeOpenedInWebView(String uri) {
        return canOnlyBeOpenedInWebView(Uri.parse(uri));
    }

    public static boolean canOnlyBeOpenedInWebView(Uri uri) {
        int type = getType(uri);
        int[] iArr = URIS_OPENED_IN_NATIVE_WEBVIEW;
        int length = iArr.length;
        for (int i = 0; i < length; i += COLUMN) {
            if (iArr[i] == type) {
                return true;
            }
        }
        return false;
    }

    public static Builder builder() {
        return new Builder().scheme(SCHEME).authority(AUTHORITY);
    }

    public static Uri reader(int worksId) {
        return builder().appendPath(PATH_READER).appendPath(BaseShareEditFragment.CONTENT_TYPE_WORKS).appendPath(StringUtils.toStr(Integer.valueOf(worksId))).build();
    }

    public static Uri webReader(int worksId, int chapterId) {
        return builder().appendPath("web_reader").appendQueryParameter(BaseShareEditFragment.CONTENT_TYPE_WORKS, String.valueOf(worksId)).appendQueryParameter(BaseShareEditFragment.CONTENT_TYPE_CHAPTER, String.valueOf(chapterId)).build();
    }

    public static Uri purchase(int worksId, int chapterId, boolean promptDownload) {
        return builder().appendPath("purchase").appendQueryParameter(BaseShareEditFragment.CONTENT_TYPE_WORKS, String.valueOf(worksId)).appendQueryParameter(BaseShareEditFragment.CONTENT_TYPE_CHAPTER, String.valueOf(chapterId)).appendQueryParameter(PurchaseFragment_.PROMPT_DOWNLOAD_ARG, String.valueOf(promptDownload)).build();
    }

    public static Uri worksProfile(int worksId) {
        return builder().appendPath("works_profile").appendQueryParameter(WorksListUri.KEY_ID, StringUtils.toStr(Integer.valueOf(worksId))).build();
    }

    public static Uri openInNewPage(Uri uri) {
        return builder().appendPath(PATH_OPEN_URL).appendQueryParameter(ShareUrlEditFragment_.URL_ARG, String.valueOf(uri)).build();
    }

    public static Uri withPath(Object... pathSegments) {
        Builder builder = builder();
        int length = pathSegments.length;
        for (int i = 0; i < length; i += COLUMN) {
            builder.appendPath(String.valueOf(pathSegments[i]));
        }
        return builder.build();
    }

    private static void addWebUri(String path, int code) {
        sUriMatcher.addURI(AUTHORITY_WEB, path, code);
    }

    private static void addAppUri(String path, int code) {
        sUriMatcher.addURI(AUTHORITY, path, code);
    }
}
