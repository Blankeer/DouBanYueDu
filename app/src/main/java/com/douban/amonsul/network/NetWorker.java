package com.douban.amonsul.network;

import android.content.Context;
import android.text.TextUtils;
import com.douban.amonsul.MobileStat;
import com.douban.amonsul.StatConstant;
import com.douban.amonsul.StatLogger;
import com.douban.amonsul.StatPrefs;
import com.douban.amonsul.StatUtils;
import com.douban.amonsul.device.AppInfo;
import com.douban.amonsul.model.StatEvent;
import com.douban.book.reader.helper.AppUri;
import io.realm.internal.Table;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class NetWorker {
    public static final int NETWORK_STATUS_ERROR = 1;
    public static final int NETWORK_STATUS_ERROR_NO_NETWORK = 2;
    public static final int NETWORK_STATUS_SUCCESS = 0;
    public static final String PARAM_KEY_API_KEY = "apikey";
    public static final String PARAM_KEY_APP_INFO = "info";
    public static final String PARAM_KEY_APP_NAME = "app_name";
    public static final String PARAM_KEY_DEVICE_ID = "did";
    public static final String PARAM_KEY_LOCAL_TIME = "ltime";
    public static final String PARAM_KEY_SDK_VERSION = "sdkVersion";
    public static final String PARAM_KEY_TOKEN = "token";
    public static final String PARAM_KEY_USER_ID = "userid";
    private static final String TAG;
    private URLClient mClient;

    public enum Method {
        GET,
        POST,
        PUT,
        DELETE
    }

    static {
        TAG = NetWorker.class.getSimpleName();
    }

    public NetWorker() {
        this.mClient = new URLClient();
    }

    public List<NameValuePair> getBasicPairs(Context context) {
        List<NameValuePair> pairs = new ArrayList();
        pairs.add(new BasicNameValuePair(PARAM_KEY_API_KEY, AppInfo.getApikey(context)));
        pairs.add(new BasicNameValuePair(PARAM_KEY_APP_NAME, AppInfo.getAppName(context)));
        pairs.add(new BasicNameValuePair(PARAM_KEY_LOCAL_TIME, String.valueOf(System.currentTimeMillis())));
        pairs.add(new BasicNameValuePair(PARAM_KEY_DEVICE_ID, StatPrefs.getInstance(context).getDeviceId()));
        return pairs;
    }

    public Response requestConfig(Context context) {
        try {
            List<NameValuePair> pairs = getBasicPairs(context);
            pairs.add(new BaseNameValuePair(PARAM_KEY_SDK_VERSION, StatConstant.SDK_VERSION));
            Response res = this.mClient.catchConfig(createUrl(StatConstant.BASE_HOST, "check2"), Method.GET, pairs, null);
            if (res == null || res.getResponseCode() <= 0 || res.getResponseCode() >= AppUri.READER) {
                return null;
            }
            StatLogger.d(TAG, " requestConfig response " + res.getResponseContent());
            return res;
        } catch (Exception e) {
            if (!MobileStat.DEBUG) {
                return null;
            }
            e.printStackTrace();
            return null;
        }
    }

    public int sendEvent(Context context, StatEvent event) {
        if (!StatUtils.isNetworkAvailable(context)) {
            return NETWORK_STATUS_ERROR_NO_NETWORK;
        }
        List<StatEvent> list = new ArrayList();
        list.add(event);
        return sendEventsData(context, StatUtils.getEventsBytes(context, StatUtils.arrayEventToJsonArray(context, list)));
    }

    public int sendCrashData(Context context, byte[] data) {
        try {
            List<NameValuePair> pairs = getBasicPairs(context);
            if (AppInfo.getUserId() > 0) {
                pairs.add(new BaseNameValuePair(PARAM_KEY_USER_ID, String.valueOf(AppInfo.getUserId())));
            }
            if (!TextUtils.isEmpty(AppInfo.getToken())) {
                pairs.add(new BaseNameValuePair(PARAM_KEY_TOKEN, AppInfo.getToken()));
            }
            String app = String.valueOf(AppInfo.get(context));
            if (!TextUtils.isEmpty(app)) {
                pairs.add(new BaseNameValuePair(PARAM_KEY_APP_INFO, app));
            }
            Response res = this.mClient.send(createUrl(StatConstant.BASE_HOST, "crash"), Method.POST, pairs, null, new MultipartParameter("ziplogs", "text/plain", data));
            if (res == null || res.getResponseCode() <= 0 || res.getResponseCode() >= AppUri.READER) {
                return NETWORK_STATUS_ERROR;
            }
            StatLogger.d(TAG, " sendCrashData getResponse " + res.getResponseContent());
            if (res.getResponseContent().equalsIgnoreCase("ok")) {
                return NETWORK_STATUS_SUCCESS;
            }
            return NETWORK_STATUS_ERROR;
        } catch (Exception e) {
            if (!MobileStat.DEBUG) {
                return NETWORK_STATUS_ERROR;
            }
            e.printStackTrace();
            return NETWORK_STATUS_ERROR;
        }
    }

    public int sendEventsData(Context context, byte[] events) {
        if (events == null || events.length == 0) {
            return NETWORK_STATUS_ERROR;
        }
        if (MobileStat.DEBUG) {
            StatLogger.d(TAG, "sendEventsData() app: " + AppInfo.get(context));
        }
        try {
            List<NameValuePair> p = getBasicPairs(context);
            if (AppInfo.getUserId() > 0) {
                p.add(new BaseNameValuePair(PARAM_KEY_USER_ID, String.valueOf(AppInfo.getUserId())));
            }
            if (!TextUtils.isEmpty(AppInfo.getToken())) {
                p.add(new BaseNameValuePair(PARAM_KEY_TOKEN, AppInfo.getToken()));
            }
            Response res = this.mClient.send(createUrl(StatConstant.BASE_HOST, Table.STRING_DEFAULT_VALUE), Method.POST, p, null, new MultipartParameter("ziplogs", "text/plain", events));
            int statusCode = res.getResponseCode();
            if (MobileStat.DEBUG) {
                StatLogger.d(TAG, "sendEventsData() response " + statusCode + ":" + res.getResponseContent());
            }
            if (statusCode > 0 && statusCode < AppUri.READER && res.getResponseContent().equalsIgnoreCase("ok")) {
                return NETWORK_STATUS_SUCCESS;
            }
        } catch (Exception e) {
            if (MobileStat.DEBUG) {
                StatLogger.e(TAG, " NetWork sendEventsData error " + e);
                e.printStackTrace();
            }
        }
        return NETWORK_STATUS_ERROR;
    }

    private String createUrl(String host, String path) {
        return "https://" + host + "/" + path;
    }
}
