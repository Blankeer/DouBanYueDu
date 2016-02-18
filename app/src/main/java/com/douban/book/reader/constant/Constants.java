package com.douban.book.reader.constant;

import android.graphics.PointF;
import android.graphics.RectF;
import android.provider.Settings.Secure;
import android.support.v4.view.ViewConfigurationCompat;
import android.view.ViewConfiguration;
import com.douban.book.reader.app.App;

public class Constants {
    public static final String ACTION_DEFAULT = "com.douban.book.reader";
    public static final String ACTION_DELETE_NOTIFICATION = "com.douban.book.reader.action.DELETE_NOTIFICATION";
    public static final String ACTION_OPEN_BOOK = "com.douban.book.reader.action.OPEN_BOOK";
    public static final String ACTION_OPEN_NOTIFICATION = "com.douban.book.reader.action.OPEN_NOTIFICATION";
    public static final String API_HOST = "www.douban.com";
    public static final String API_HOST_IN_HTTP_HEADER = "read.douban.com";
    public static final String API_SCHEME = "https";
    public static final String APP_KEY = "04962fc37ab154fa0dc28f4a7013feee";
    public static final String APP_SECRET = "2245a724b34f361a";
    public static final String ARK_HOST = "read.douban.com";
    public static final long BACKEND_PAGING_MIN_MEMORY_REQUIREMENT = 62914560;
    public static final int COLOR_INVALID = 1;
    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final String DOUBAN_API_HOST = "api.douban.com";
    public static final String DOUBAN_HOST = "www.douban.com";
    public static final PointF DUMMY_POINT;
    public static final RectF DUMMY_RECT;
    public static final int FALSE = 0;
    public static final String INTENT_FINISH_ALL_ACTIVITIES = "com.douban.book.reader.FINISH_ALL";
    public static final String KEY_BOOK_ID = "KEY_BOOK_ID";
    public static final String KEY_CHAPTER_INDEX = "KEY_CHAPTER_INDEX";
    public static final String KEY_DATA = "KEY_DATA";
    public static final String KEY_FORWARD_INTENT = "KEY_FORWARD_INTENT";
    public static final String KEY_ILLUS_SEQ = "KEY_ILLUS_SEQ";
    public static final String KEY_NOTIFICATION_ID = "KEY_NOTIFICATION_ID";
    public static final String KEY_PACKAGE_ID = "KEY_PACKAGE_ID";
    public static final String KEY_PAGE_NO = "KEY_PAGE_NO";
    public static final String KEY_SECTION_INDEX = "KEY_SECTION_INDEX";
    public static final String KEY_TYPE = "KEY_TYPE";
    public static final String KEY_URL = "KEY_URL";
    public static final String MAGIC;
    public static final int MAX_COPY_STRING_LENGTH = 300;
    public static final int MENU_CANCEL_SUBSCRIPTION = 3;
    public static final int MENU_DELETE = 1;
    public static final String OAUTH2_AUTH_URI_BASE = "https://www.douban.com";
    public static final int PACK_VERSION = 3;
    public static final int RC_LOGIN = 1;
    public static final int[] TEXTSIZE_CATALOG;
    public static final int[] TEXTSIZE_GALLERY_LEGEND;
    public static final int TIME_1HOUR_MILLISECOND = 3600000;
    public static final int TIME_1MIN_MILLISECOND = 60000;
    public static final int TIME_24H_MILLISECOND = 86400000;
    public static final int TOUCH_SLOP;
    public static final int TRUE = 1;
    public static final String WEIBO_APP_KEY = "3012498782";
    public static final String WX_APP_ID = "wx72811b1bff66105e";
    public static final String WX_APP_KEY = "a4267ff75c3a5b74f4713835c0d4277d";
    public static final int WX_THUMBNAIL_SIZE = 96;

    static {
        MAGIC = Secure.getString(App.get().getContentResolver(), "android_id").substring(6);
        TOUCH_SLOP = ViewConfigurationCompat.getScaledPagingTouchSlop(ViewConfiguration.get(App.get()));
        TEXTSIZE_GALLERY_LEGEND = new int[]{14, 16, 18, 20, 22};
        TEXTSIZE_CATALOG = new int[]{15, 19, 22};
        DUMMY_POINT = new PointF(-1.0f, -1.0f);
        DUMMY_RECT = new RectF(-1.0f, -1.0f, -1.0f, -1.0f);
    }
}
