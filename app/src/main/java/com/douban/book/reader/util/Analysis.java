package com.douban.book.reader.util;

import android.content.Context;
import android.os.Bundle;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.LoginEvent;
import com.douban.amonsul.MobileStat;
import com.douban.book.reader.activity.GeneralFragmentActivity;
import com.douban.book.reader.app.App;
import com.douban.book.reader.content.PageMetrics;
import com.douban.book.reader.controller.TaskController;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.network.param.JsonRequestParam;
import com.douban.book.reader.network.param.RequestParam;
import com.douban.book.reader.theme.Theme;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.j256.ormlite.stmt.query.SimpleComparison;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import io.realm.internal.Table;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.json.JSONObject;

public class Analysis {
    private static final String TAG;
    private static String sLastPageName;
    private static final Pattern sSuffixPattern;

    /* renamed from: com.douban.book.reader.util.Analysis.1 */
    static class AnonymousClass1 implements Runnable {
        final /* synthetic */ String val$pageName;
        final /* synthetic */ Bundle val$params;
        final /* synthetic */ int val$visibleTimes;

        AnonymousClass1(String str, Bundle bundle, int i) {
            this.val$pageName = str;
            this.val$params = bundle;
            this.val$visibleTimes = i;
        }

        public void run() {
            String formattedPageName = Analysis.formatPageName(this.val$pageName);
            Tracker tracker = EasyTracker.getInstance(App.get());
            tracker.set(Fields.SCREEN_NAME, formattedPageName);
            tracker.send(MapBuilder.createAppView().build());
            if (StringUtils.isNotEmpty(Analysis.sLastPageName)) {
                MobileStat.onPageEnd(App.get(), Analysis.sLastPageName);
                MobclickAgent.onPageEnd(Analysis.sLastPageName);
            }
            Bundle reducedParams = null;
            if (this.val$params != null) {
                reducedParams = new Bundle(this.val$params);
                reducedParams.remove(GeneralFragmentActivity.KEY_ADDITIONAL_ARGS_FOR_FRAGMENT);
                reducedParams.putInt("visibleTimes", this.val$visibleTimes);
            }
            MobileStat.onPageStart(App.get(), formattedPageName, Analysis.formatParams(reducedParams));
            MobclickAgent.onPageStart(formattedPageName);
            Logger.dc(Analysis.TAG, "Page: %s, %s", this.val$pageName, reducedParams);
            ContentViewEvent event = new ContentViewEvent().putContentName(formattedPageName).putContentType("App Page");
            if (!(reducedParams == null || reducedParams.isEmpty())) {
                for (String key : reducedParams.keySet()) {
                    Object value = reducedParams.get(key);
                    if (value != null) {
                        event.putCustomAttribute(key, String.valueOf(value));
                    }
                }
            }
            Answers.getInstance().logContentView(event);
            Analysis.sLastPageName = formattedPageName;
        }
    }

    static {
        TAG = Analysis.class.getSimpleName();
        sSuffixPattern = Pattern.compile("(Activity|Activity_|Fragment|Fragment_)$");
    }

    public static void sendPageViewEvent(String pageName, Bundle params, int visibleTimes) {
        TaskController.run(new AnonymousClass1(pageName, params, visibleTimes));
    }

    public static void sendEvent(String event) {
        sendEventWithExtra(event, "default", Table.STRING_DEFAULT_VALUE);
    }

    public static void sendEvent(String event, String action) {
        sendEventWithExtra(event, action, Table.STRING_DEFAULT_VALUE);
    }

    public static void sendEventWithExtra(String event, String action, String extraKey, Object extraValue) {
        sendEventWithExtra(event, action, ((JsonRequestParam) RequestParam.json().append(extraKey, extraValue)).getJsonObject());
    }

    public static void sendEventWithExtra(String event, String action, JSONObject param) {
        sendEventWithExtra(event, action, String.valueOf(param));
    }

    public static void sendEventWithExtra(String event, String action, String extra) {
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put(Fields.EVENT_CATEGORY, event);
        hashMap.put(Fields.EVENT_ACTION, action);
        hashMap.put(Fields.EX_DESCRIPTION, extra);
        EasyTracker.getInstance(App.get()).send(hashMap);
        MobileStat.onEvent(App.get(), event, extra, 1, action, false);
        Logger.d(TAG, "Event: %s (%s) %s", event, action, extra);
    }

    public static void sendPrefChangedEvent(String prefKey, Object newValue) {
        sendEventWithExtra("pref_change", "manual", prefKey, newValue);
    }

    public static void init() {
        MobileStat.init(App.get(), (long) UserManager.getInstance().getUserId());
        MobileStat.setAppChannel(AppInfo.getChannelName());
        AnalyticsConfig.setChannel(AppInfo.getChannelName());
        MobclickAgent.openActivityDurationTrack(false);
        try {
            Crashlytics.setString("DisplayMetrics", Utils.getFormattedDisplayMetrics());
            Crashlytics.setString("PageMetrics", PageMetrics.getLast().toString());
            Crashlytics.setString("Theme", String.valueOf(Theme.getCurrent()));
            Crashlytics.setString("UDID", Utils.getDeviceUDID());
            Crashlytics.setString("Connection", Utils.getNetworkInfo());
            Crashlytics.setString("Channel", AppInfo.getChannelName());
            Crashlytics.setString("Installer", AppInfo.getInstallerPackageName());
            Crashlytics.setString("Build", AppInfo.getBuild());
            Crashlytics.setString("Locale", String.valueOf(Res.get().getConfiguration().locale));
            Crashlytics.setFloat("System FontScale", Res.get().getConfiguration().fontScale);
        } catch (Throwable e) {
            Logger.e(TAG, e);
        }
    }

    public static void updateBindUserInfo() {
        App app = App.get();
        if (UserManager.getInstance().hasAccessToken()) {
            MobileStat.onBind(app, (long) UserManager.getInstance().getUserId());
        } else {
            MobileStat.unBind(app);
        }
        Crashlytics.setUserIdentifier(StringUtils.toStr(Integer.valueOf(UserManager.getInstance().getUserId())));
        Crashlytics.setUserName(UserManager.getInstance().getUserInfo().getName());
    }

    public static void onLogin(Context context, long userId) {
        MobileStat.onBind(context, userId);
        Answers.getInstance().logLogin(new LoginEvent().putMethod(UserManager.getInstance().getUserTypeName()));
    }

    public static void onLogout(Context context) {
        MobileStat.unBind(context);
    }

    private static String formatPageName(CharSequence originPageName) {
        String pageName = sSuffixPattern.matcher(originPageName).replaceAll(Table.STRING_DEFAULT_VALUE);
        if (StringUtils.isEmpty(pageName)) {
            return "<unknown>";
        }
        return pageName;
    }

    private static Map<String, String> formatParams(Bundle bundle) {
        Map<String, String> result = new HashMap();
        if (!(bundle == null || bundle.isEmpty())) {
            for (String key : bundle.keySet()) {
                result.put(filter(key), filter(String.valueOf(bundle.get(key))));
            }
        }
        return result;
    }

    public static CharSequence formatReferrer(CharSequence simplePageClassName, Bundle params, String viewPath) {
        return String.format("%s:%s:%s", new Object[]{formatPageName(simplePageClassName), params, viewPath});
    }

    private static String filter(String str) {
        return StringUtils.filterOut(str, SimpleComparison.EQUAL_TO_OPERATION, "|");
    }
}
