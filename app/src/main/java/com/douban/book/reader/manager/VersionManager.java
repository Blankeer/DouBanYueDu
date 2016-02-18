package com.douban.book.reader.manager;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.fragment.AlertDialogFragment.Builder;
import com.douban.book.reader.network.client.RestClient;
import com.douban.book.reader.network.exception.RestException;
import com.douban.book.reader.network.param.RequestParam;
import com.douban.book.reader.util.AppInfo;
import com.douban.book.reader.util.JsonUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Pref;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.Utils;
import com.sina.weibo.sdk.register.mobile.SelectCountryActivity;
import com.umeng.analytics.a;
import io.fabric.sdk.android.services.common.AbstractSpiCall;
import io.realm.internal.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EBean.Scope;
import org.json.JSONObject;

@EBean(scope = Scope.Singleton)
public class VersionManager {
    private static final String TAG;
    private static Map<String, String> sDeviceTypes;
    private static final List<String> sSupportedFeatures;
    private RestClient<JSONObject> mRestClient;
    private JSONObject mVersionInfo;

    public VersionManager() {
        this.mRestClient = new RestClient("check_version", JSONObject.class);
        this.mVersionInfo = null;
    }

    static {
        TAG = VersionManager.class.getSimpleName();
        sDeviceTypes = new HashMap();
        sDeviceTypes.put("web", "Web");
        sDeviceTypes.put("iphone", "iOS");
        sDeviceTypes.put(AbstractSpiCall.ANDROID_CLIENT_TYPE, a.b);
        sSupportedFeatures = new ArrayList(Arrays.asList(new String[]{"formula", SelectCountryActivity.EXTRA_COUNTRY_CODE, "gallery"}));
    }

    public boolean featuresSupported(List<String> features) {
        return containsAll(sSupportedFeatures, features);
    }

    public boolean supportedInLatestVersion(List<String> features) {
        loadVersionInfoIfNeeded();
        if (this.mVersionInfo != null) {
            JSONObject unsupportedFeatureMatrix = this.mVersionInfo.optJSONObject("unavailable_features");
            if (unsupportedFeatureMatrix != null) {
                if (StringUtils.isNotEmpty(this.mVersionInfo.optString("app_code"))) {
                    if (containsAny(JsonUtils.toArrayList(unsupportedFeatureMatrix.optJSONArray(this.mVersionInfo.optString("app_code"))), features)) {
                        return false;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public String formatUnsupportedString(List<String> features) {
        loadVersionInfoIfNeeded();
        if (supportedInLatestVersion(features)) {
            return Res.getString(R.string.features_unsupported_for_this_version, StringUtils.join(Res.getString(R.string.chinese_dot), getUnsupportedFeatures(features)));
        }
        Iterable supportedDevices = new ArrayList();
        Iterable unsupportedDevices = new ArrayList();
        if (this.mVersionInfo != null) {
            JSONObject unsupportedFeatureMatrix = this.mVersionInfo.optJSONObject("unavailable_features");
            if (unsupportedFeatureMatrix != null) {
                for (Entry<String, String> deviceType : sDeviceTypes.entrySet()) {
                    if (containsAny(JsonUtils.toArrayList(unsupportedFeatureMatrix.optJSONArray((String) deviceType.getKey())), features)) {
                        unsupportedDevices.add(deviceType.getValue());
                    } else {
                        supportedDevices.add(deviceType.getValue());
                    }
                }
            }
        }
        return Res.getString(R.string.features_unsupported_for_this_device, StringUtils.join(Res.getString(R.string.chinese_dot), getUnsupportedFeatures(features)), StringUtils.join(Res.getString(R.string.chinese_dot), supportedDevices), StringUtils.join(Res.getString(R.string.chinese_dot), unsupportedDevices));
    }

    public void promptAppUpdateIfNeeded() {
        long lastUpdate = Pref.ofApp().getLong(Key.APP_LAST_UPDATE_CHECK, 0);
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdate > a.g && Utils.isUsingWifi()) {
            promptAppUpdate();
            Pref.ofApp().set(Key.APP_LAST_UPDATE_CHECK, Long.valueOf(currentTime));
        }
    }

    public void promptAppUpdate() {
        if (VersionManager_.getInstance_(App.get()).isNewVersionAvailable()) {
            new Builder().setTitle(Res.getString(R.string.new_version_available, getLatestVersionName())).setMessage(getLatestChangeLog()).setPositiveButton((int) R.string.dialog_button_download, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Uri uri = Uri.parse(String.format("http://andariel.douban.com/d/%s", new Object[]{App.get().getPackageName()}));
                    Intent intent = new Intent();
                    intent.setData(uri);
                    intent.setAction("android.intent.action.VIEW");
                    intent.addFlags(268435456);
                    App.get().startActivity(intent);
                }
            }).setNegativeButton((int) R.string.dialog_button_cancel, null).create().show();
        }
    }

    public boolean isNewVersionAvailable() {
        loadVersionInfoIfNeeded();
        if (this.mVersionInfo == null || this.mVersionInfo.optInt("build") <= AppInfo.getVersionCode()) {
            return false;
        }
        return true;
    }

    private String getLatestVersionName() {
        loadVersionInfoIfNeeded();
        if (this.mVersionInfo != null) {
            return this.mVersionInfo.optString("latest_version");
        }
        return Table.STRING_DEFAULT_VALUE;
    }

    private String getLatestChangeLog() {
        loadVersionInfoIfNeeded();
        if (this.mVersionInfo != null) {
            return this.mVersionInfo.optString("changelog");
        }
        return Table.STRING_DEFAULT_VALUE;
    }

    private List<String> getUnsupportedFeatures(List<String> features) {
        List<String> unsupportedFeatures = new ArrayList();
        for (String feature : features) {
            if (!(TextUtils.isEmpty(feature) || sSupportedFeatures.contains(feature))) {
                unsupportedFeatures.add(getFeatureName(feature));
            }
        }
        return unsupportedFeatures;
    }

    private boolean containsAll(List<String> featureList, List<String> features) {
        for (String feature : features) {
            if (!TextUtils.isEmpty(feature) && !featureList.contains(feature)) {
                return false;
            }
        }
        return true;
    }

    private boolean containsAny(List<String> featureList, List<String> features) {
        for (String feature : features) {
            if (!TextUtils.isEmpty(feature) && featureList.contains(feature)) {
                return true;
            }
        }
        return false;
    }

    private String getFeatureName(String feature) {
        loadVersionInfoIfNeeded();
        if (this.mVersionInfo != null) {
            return this.mVersionInfo.optJSONObject("feature_names").optString(feature);
        }
        return null;
    }

    private void loadVersionInfoIfNeeded() {
        if (this.mVersionInfo == null) {
            try {
                this.mVersionInfo = (JSONObject) this.mRestClient.get(RequestParam.queryString().append("udid", Utils.getDeviceUDID()));
                Logger.e(TAG, "Features: %s", this.mVersionInfo);
            } catch (RestException e) {
                Logger.e(TAG, e);
            }
        }
    }
}
