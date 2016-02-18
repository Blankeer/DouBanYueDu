package com.douban.amonsul;

public class StatConstant {
    public static final String APP_EVENT_FILE_PREFIX = "stat_app_evt_file";
    public static String BASE_HOST = null;
    public static final int CONN_RETRY_COUNT = 2;
    public static final int DEFAULT_FORCE_UPLOAD_SECOND = 86400;
    public static final int DEFAULT_MAX_CRASH_EVT_NUMBER = 100;
    public static final int DEFAULT_MAX_EVENT_COUNT = 500;
    public static final int DEFAULT_MAX_FILE_COUNT = 20;
    public static final String DEFAULT_VALUE_APP_NAME = "";
    public static final String DEFAULT_VALUE_CARRIER = "";
    public static final String DEFAULT_VALUE_CELLID = "";
    public static final String DEFAULT_VALUE_CHANNEL = "";
    public static final String DEFAULT_VALUE_COUNTRY = "CH";
    public static final String DEFAULT_VALUE_DEVICE = "";
    public static final String DEFAULT_VALUE_DEVICE_ID = "";
    public static final String DEFAULT_VALUE_DID = "";
    public static final String DEFAULT_VALUE_DID_OLD = "";
    public static final String DEFAULT_VALUE_EMPTY = "";
    public static final String DEFAULT_VALUE_IMEI = "";
    public static final String DEFAULT_VALUE_IMSI = "";
    public static final String DEFAULT_VALUE_LANGUAGE = "sz";
    public static final String DEFAULT_VALUE_LOCATION_ID = "";
    public static final String DEFAULT_VALUE_NET = "";
    public static final String DEFAULT_VALUE_OS_VERSION = "";
    public static final String DEFAULT_VALUE_RESOLUTION = "";
    public static final String DEFAULT_VALUE_ZONE = "8";
    public static final String DOUBAN_APIKEY = "Douban_Apikey";
    public static final String DOUBAN_APP_NAME = "Douban_Appname";
    public static final String DOUBAN_CHANNEL = "Douban_Channel";
    public static final int EVENT_DURING = 2;
    public static final int EVENT_IMPORTANT = 3;
    public static final int EVENT_NORMAL = 4;
    public static final int EVENT_SYSTEM = 0;
    public static final int EVENT_USER = 1;
    public static final String JSON_KEY_APP_NAME = "apn";
    public static final String JSON_KEY_APP_VERSION = "vc";
    public static final String JSON_KEY_CARRIER = "cr";
    public static final String JSON_KEY_CELLID = "cid";
    public static final String JSON_KEY_CHANNEL = "ch";
    public static final String JSON_KEY_COUNTRY = "cn";
    public static final String JSON_KEY_DEVICE = "dv";
    public static final String JSON_KEY_DID = "did";
    public static final String JSON_KEY_DID_OLD = "dido";
    public static final String JSON_KEY_EVENT_ACTION = "ac";
    public static final String JSON_KEY_EVENT_ATTRIBUTES = "attrs";
    public static final String JSON_KEY_EVENT_COUNT = "cnt";
    public static final String JSON_KEY_EVENT_DATE = "tm";
    public static final String JSON_KEY_EVENT_ID = "id";
    public static final String JSON_KEY_EVENT_LABEL = "lb";
    public static final String JSON_KEY_EVENT_NAME = "nm";
    public static final String JSON_KEY_EVENT_PAGE = "pg";
    public static final String JSON_KEY_EVENT_VERSION = "vn";
    public static final String JSON_KEY_IMEI = "ie";
    public static final String JSON_KEY_IMSI = "is";
    public static final String JSON_KEY_IPADDRESS = "ip";
    public static final String JSON_KEY_LAC = "lat";
    public static final String JSON_KEY_LANGUAGE = "lu";
    public static final String JSON_KEY_LNG = "lng";
    public static final String JSON_KEY_LOCID = "lid";
    public static final String JSON_KEY_MAC = "mac";
    public static final String JSON_KEY_NET = "net";
    public static final String JSON_KEY_OS = "os";
    public static final String JSON_KEY_OS_VERSION = "osv";
    public static final String JSON_KEY_RESOLUTION = "rt";
    public static final String JSON_KEY_SDK_VERSION = "sv";
    public static final String JSON_KEY_TIMEZONE = "tz";
    public static final String JSON_KEY_USERID = "ui";
    public static final String KEY_APP = "ai";
    public static final String KEY_DEVICE = "di";
    public static final String KEY_EVENT = "ets";
    public static final String KEY_EVENT_COUNT = "currentcount";
    public static final String KEY_FILE_INDEX = "file_index";
    public static final String KEY_LAST_APP_INFO = "last_app_info";
    public static final String KEY_LAST_UPLOAD = "last_upload";
    public static final String KEY_LAST_VERSION = "last_version";
    public static final String KEY_MAX_EVENT_COUNT = "max_event_count";
    public static final String KEY_MAX_FORCE_UPLOAD_SECOND = "max_upload_time";
    public static final String KEY_STAT_AVAILABLE = "stat_available";
    public static final String PRE_KEY_REQ_STRATEGY = "req_strategy";
    public static final String PRE_KEY_SP_FIRST_RUN = "run_first";
    public static final String SDK_VERSION = "1.0.13";
    public static final String SP_KEY_CONFIG = "config";
    public static final String SP_KEY_CONFIG_UPDATE = "configupdate";
    public static final String STAT_EVENT_ACTION_BEGIN = "begin";
    public static final String STAT_EVENT_ACTION_END = "end";
    public static final String STAT_EVENT_ID_ERROR = "error";
    public static final String STAT_EVENT_ID_ONLAUNCH = "onLaunch";
    public static final String STAT_EVENT_ID_PAUSE = "pause";
    public static final String STAT_EVENT_ID_RESUME = "resume";
    public static final String STAT_EVENT_PAGE_END = "page_end";
    public static final String STAT_EVENT_PAGE_START = "page_start";
    public static final String TIME_FORMAT_PATTERN = "yyyy-MM-dd_HH:mm:ss";

    static {
        BASE_HOST = "amonsul.douban.com";
    }
}
