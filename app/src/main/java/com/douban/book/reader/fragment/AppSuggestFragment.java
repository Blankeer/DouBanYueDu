package com.douban.book.reader.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import com.douban.book.reader.R;
import com.douban.book.reader.util.PackageUtils;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.util.UriUtils;
import com.google.gson.Gson;
import org.androidannotations.annotations.EFragment;
import org.json.JSONArray;

@EFragment
public class AppSuggestFragment extends BaseWebFragment {
    private static final String APP_SUGGEST_URL = "http://mobilestore.douban.com/app/android/read";
    private static final String JS_OBJECT_NAME = "js";

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle((int) R.string.act_title_app_suggest);
        enableJavascript(JS_OBJECT_NAME);
        loadUrl(APP_SUGGEST_URL);
    }

    protected boolean shouldOverrideUrlLoading(String url) {
        Uri uri = Uri.parse(url);
        if (StringUtils.equals(uri.getScheme(), (CharSequence) "package")) {
            try {
                startActivity(getActivity().getPackageManager().getLaunchIntentForPackage(uri.getHost()));
                return true;
            } catch (ActivityNotFoundException e) {
                ToastUtils.showToast((int) R.string.toast_app_not_installed);
                return true;
            }
        } else if (!StringUtils.equalsIgnoreCase(UriUtils.getFilenameSuffix(uri), "apk")) {
            return super.shouldOverrideUrlLoading(url);
        } else {
            Intent intent = new Intent();
            intent.setData(uri);
            intent.setAction("android.intent.action.VIEW");
            startActivity(intent);
            return true;
        }
    }

    protected void onPageFinished(String url) {
        super.onPageFinished(url);
        if (StringUtils.equals((CharSequence) url, APP_SUGGEST_URL)) {
            loadUrl(String.format("javascript:window.%s.processPackage(getPkg())", new Object[]{JS_OBJECT_NAME}));
        }
    }

    @JavascriptInterface
    public void processPackage(String packages) {
        if (!StringUtils.isEmpty(packages)) {
            String[] packageList = (String[]) new Gson().fromJson(packages, String[].class);
            if (packageList != null && packageList.length != 0) {
                JSONArray result = new JSONArray();
                for (String packageName : packageList) {
                    result.put(PackageUtils.isInstalled(packageName));
                }
                invokeJs("setDownloadStatus", result);
            }
        }
    }
}
