package com.sina.weibo.sdk.call;

import android.content.Context;
import android.text.TextUtils;
import com.douban.book.reader.fragment.share.ShareUrlEditFragment_;
import com.sina.weibo.sdk.component.ShareRequestParam;
import com.sina.weibo.sdk.constant.WBPageConstants.ExceptionMsg;
import com.sina.weibo.sdk.constant.WBPageConstants.ParamKey;
import com.sina.weibo.sdk.constant.WBPageConstants.Scheme;
import io.realm.internal.Table;
import java.util.HashMap;

public final class WeiboPageUtilsV2 {
    private WeiboPageUtilsV2() {
    }

    public static void postNewWeibo(Context context, HashMap<String, String> params) throws WeiboNotInstalledException {
        if (context == null) {
            throw new WeiboIllegalParameterException(ExceptionMsg.CONTEXT_ERROR);
        }
        StringBuilder uri = new StringBuilder(Scheme.SENDWEIBO);
        if (params != null) {
            uri.append(CommonUtils.buildUriQuery(params));
        }
        if (params == null || TextUtils.isEmpty((CharSequence) params.get(ShareRequestParam.REQ_PARAM_PACKAGENAME))) {
            CommonUtils.openWeiboActivity(context, "android.intent.action.VIEW", uri.toString(), null);
            return;
        }
        StringBuilder packageuri = new StringBuilder(Scheme.SENDWEIBO);
        if (params != null) {
            packageuri.append(CommonUtils.buildUriQuery(params));
        }
        CommonUtils.openWeiboActivity(context, "android.intent.action.VIEW", uri.toString(), (String) params.get(ShareRequestParam.REQ_PARAM_PACKAGENAME));
    }

    public static void viewNearbyPeople(Context context, HashMap<String, String> params) throws WeiboNotInstalledException {
        if (context == null) {
            throw new WeiboIllegalParameterException(ExceptionMsg.CONTEXT_ERROR);
        }
        StringBuilder uri = new StringBuilder(Scheme.NEARBYPEOPLE);
        if (params != null) {
            uri.append(CommonUtils.buildUriQuery(params));
        }
        if (params == null || TextUtils.isEmpty((CharSequence) params.get(ShareRequestParam.REQ_PARAM_PACKAGENAME))) {
            CommonUtils.openWeiboActivity(context, "android.intent.action.VIEW", uri.toString(), null);
            return;
        }
        StringBuilder packageuri = new StringBuilder(Scheme.NEARBYPEOPLE);
        if (params != null) {
            packageuri.append(CommonUtils.buildUriQuery(params));
        }
        CommonUtils.openWeiboActivity(context, "android.intent.action.VIEW", uri.toString(), (String) params.get(ShareRequestParam.REQ_PARAM_PACKAGENAME));
    }

    public static void viewNearbyWeibo(Context context, HashMap<String, String> params) throws WeiboNotInstalledException {
        if (context == null) {
            throw new WeiboIllegalParameterException(ExceptionMsg.CONTEXT_ERROR);
        }
        StringBuilder uri = new StringBuilder(Scheme.NEARBYWEIBO);
        if (params != null) {
            uri.append(CommonUtils.buildUriQuery(params));
        }
        if (params == null || TextUtils.isEmpty((CharSequence) params.get(ShareRequestParam.REQ_PARAM_PACKAGENAME))) {
            CommonUtils.openWeiboActivity(context, "android.intent.action.VIEW", uri.toString(), null);
            return;
        }
        StringBuilder packageuri = new StringBuilder(Scheme.NEARBYWEIBO);
        if (params != null) {
            packageuri.append(CommonUtils.buildUriQuery(params));
        }
        CommonUtils.openWeiboActivity(context, "android.intent.action.VIEW", uri.toString(), (String) params.get(ShareRequestParam.REQ_PARAM_PACKAGENAME));
    }

    public static void viewUserInfo(Context context, HashMap<String, String> params) throws WeiboNotInstalledException {
        if (context == null) {
            throw new WeiboIllegalParameterException(ExceptionMsg.CONTEXT_ERROR);
        } else if (params == null || (TextUtils.isEmpty((CharSequence) params.get(ParamKey.UID)) && TextUtils.isEmpty((CharSequence) params.get(ParamKey.NICK)))) {
            throw new WeiboIllegalParameterException(ExceptionMsg.UID_NICK_ERROR);
        } else {
            StringBuilder uri = new StringBuilder(Scheme.USERINFO);
            if (params != null) {
                uri.append(CommonUtils.buildUriQuery(params));
            }
            if (params == null || TextUtils.isEmpty((CharSequence) params.get(ShareRequestParam.REQ_PARAM_PACKAGENAME))) {
                CommonUtils.openWeiboActivity(context, "android.intent.action.VIEW", uri.toString(), null);
                return;
            }
            StringBuilder packageuri = new StringBuilder(Scheme.USERINFO);
            if (params != null) {
                packageuri.append(CommonUtils.buildUriQuery(params));
            }
            CommonUtils.openWeiboActivity(context, "android.intent.action.VIEW", uri.toString(), (String) params.get(ShareRequestParam.REQ_PARAM_PACKAGENAME));
        }
    }

    public static void viewUsertrends(Context context, HashMap<String, String> params) throws WeiboNotInstalledException {
        if (context == null) {
            throw new WeiboIllegalParameterException(ExceptionMsg.CONTEXT_ERROR);
        } else if (params == null || TextUtils.isEmpty((CharSequence) params.get(ParamKey.UID))) {
            throw new WeiboIllegalParameterException(ExceptionMsg.UID_NICK_ERROR);
        } else {
            StringBuilder uri = new StringBuilder(Scheme.USERTRENDS);
            if (params != null) {
                uri.append(CommonUtils.buildUriQuery(params));
            }
            if (params == null || TextUtils.isEmpty((CharSequence) params.get(ShareRequestParam.REQ_PARAM_PACKAGENAME))) {
                CommonUtils.openWeiboActivity(context, "android.intent.action.VIEW", uri.toString(), null);
                return;
            }
            StringBuilder packageuri = new StringBuilder(Scheme.USERTRENDS);
            if (params != null) {
                packageuri.append(CommonUtils.buildUriQuery(params));
            }
            CommonUtils.openWeiboActivity(context, "android.intent.action.VIEW", uri.toString(), (String) params.get(ShareRequestParam.REQ_PARAM_PACKAGENAME));
        }
    }

    public static void viewPageInfo(Context context, HashMap<String, String> params) throws WeiboNotInstalledException {
        if (context == null) {
            throw new WeiboIllegalParameterException(ExceptionMsg.CONTEXT_ERROR);
        } else if (params == null || TextUtils.isEmpty((CharSequence) params.get(ParamKey.PAGEID))) {
            throw new WeiboIllegalParameterException(ExceptionMsg.PAGEID_ERROR);
        } else {
            StringBuilder uri = new StringBuilder(Scheme.PAGEINFO);
            if (params != null) {
                uri.append(CommonUtils.buildUriQuery(params));
            }
            if (params == null || TextUtils.isEmpty((CharSequence) params.get(ShareRequestParam.REQ_PARAM_PACKAGENAME))) {
                CommonUtils.openWeiboActivity(context, "android.intent.action.VIEW", uri.toString(), null);
                return;
            }
            StringBuilder packageuri = new StringBuilder(Scheme.PAGEINFO);
            if (params != null) {
                packageuri.append(CommonUtils.buildUriQuery(params));
            }
            CommonUtils.openWeiboActivity(context, "android.intent.action.VIEW", uri.toString(), (String) params.get(ShareRequestParam.REQ_PARAM_PACKAGENAME));
        }
    }

    public static void viewPageProductList(Context context, HashMap<String, String> params) throws WeiboNotInstalledException {
        if (context == null) {
            throw new WeiboIllegalParameterException(ExceptionMsg.CONTEXT_ERROR);
        } else if (params == null) {
            throw new WeiboIllegalParameterException(ExceptionMsg.PAGEID_ERROR);
        } else if (TextUtils.isEmpty((CharSequence) params.get(ParamKey.PAGEID))) {
            throw new WeiboIllegalParameterException(ExceptionMsg.PAGEID_ERROR);
        } else if (TextUtils.isEmpty((CharSequence) params.get(ParamKey.CARDID))) {
            throw new WeiboIllegalParameterException(ExceptionMsg.CARDID_ERROR);
        } else {
            int count;
            try {
                count = Integer.parseInt((String) params.get(ParamKey.COUNT));
            } catch (NumberFormatException e) {
                count = -1;
            }
            if (count < 0) {
                throw new WeiboIllegalParameterException(ExceptionMsg.COUNT_ERROR);
            }
            StringBuilder uri = new StringBuilder(Scheme.PAGEPRODUCTLIST);
            if (params != null) {
                uri.append(CommonUtils.buildUriQuery(params));
            }
            if (params == null || TextUtils.isEmpty((CharSequence) params.get(ShareRequestParam.REQ_PARAM_PACKAGENAME))) {
                CommonUtils.openWeiboActivity(context, "android.intent.action.VIEW", uri.toString(), null);
                return;
            }
            StringBuilder packageuri = new StringBuilder(Scheme.PAGEPRODUCTLIST);
            if (params != null) {
                packageuri.append(CommonUtils.buildUriQuery(params));
            }
            CommonUtils.openWeiboActivity(context, "android.intent.action.VIEW", uri.toString(), (String) params.get(ShareRequestParam.REQ_PARAM_PACKAGENAME));
        }
    }

    public static void viewPageUserList(Context context, HashMap<String, String> params) throws WeiboNotInstalledException {
        if (context == null) {
            throw new WeiboIllegalParameterException(ExceptionMsg.CONTEXT_ERROR);
        } else if (params == null) {
            throw new WeiboIllegalParameterException(ExceptionMsg.PAGEID_ERROR);
        } else if (TextUtils.isEmpty((CharSequence) params.get(ParamKey.PAGEID))) {
            throw new WeiboIllegalParameterException(ExceptionMsg.PAGEID_ERROR);
        } else if (TextUtils.isEmpty((CharSequence) params.get(ParamKey.CARDID))) {
            throw new WeiboIllegalParameterException(ExceptionMsg.CARDID_ERROR);
        } else {
            int count;
            try {
                count = Integer.parseInt((String) params.get(ParamKey.COUNT));
            } catch (NumberFormatException e) {
                count = -1;
            }
            if (count < 0) {
                throw new WeiboIllegalParameterException(ExceptionMsg.COUNT_ERROR);
            }
            StringBuilder uri = new StringBuilder(Scheme.PAGEUSERLIST);
            if (params != null) {
                uri.append(CommonUtils.buildUriQuery(params));
            }
            if (params == null || TextUtils.isEmpty((CharSequence) params.get(ShareRequestParam.REQ_PARAM_PACKAGENAME))) {
                CommonUtils.openWeiboActivity(context, "android.intent.action.VIEW", uri.toString(), null);
                return;
            }
            StringBuilder packageuri = new StringBuilder(Scheme.PAGEUSERLIST);
            if (params != null) {
                packageuri.append(CommonUtils.buildUriQuery(params));
            }
            CommonUtils.openWeiboActivity(context, "android.intent.action.VIEW", uri.toString(), (String) params.get(ShareRequestParam.REQ_PARAM_PACKAGENAME));
        }
    }

    public static void viewPageWeiboList(Context context, HashMap<String, String> params) throws WeiboNotInstalledException {
        if (context == null) {
            throw new WeiboIllegalParameterException(ExceptionMsg.CONTEXT_ERROR);
        } else if (params == null) {
            throw new WeiboIllegalParameterException(ExceptionMsg.PAGEID_ERROR);
        } else if (TextUtils.isEmpty((CharSequence) params.get(ParamKey.PAGEID))) {
            throw new WeiboIllegalParameterException(ExceptionMsg.PAGEID_ERROR);
        } else if (TextUtils.isEmpty((CharSequence) params.get(ParamKey.CARDID))) {
            throw new WeiboIllegalParameterException(ExceptionMsg.CARDID_ERROR);
        } else {
            int count;
            try {
                count = Integer.parseInt((String) params.get(ParamKey.COUNT));
            } catch (NumberFormatException e) {
                count = -1;
            }
            if (count < 0) {
                throw new WeiboIllegalParameterException(ExceptionMsg.COUNT_ERROR);
            }
            StringBuilder uri = new StringBuilder(Scheme.PAGEWEIBOLIST);
            if (params != null) {
                uri.append(CommonUtils.buildUriQuery(params));
            }
            if (params == null || TextUtils.isEmpty((CharSequence) params.get(ShareRequestParam.REQ_PARAM_PACKAGENAME))) {
                CommonUtils.openWeiboActivity(context, "android.intent.action.VIEW", uri.toString(), null);
                return;
            }
            StringBuilder packageuri = new StringBuilder(Scheme.PAGEWEIBOLIST);
            if (params != null) {
                packageuri.append(CommonUtils.buildUriQuery(params));
            }
            CommonUtils.openWeiboActivity(context, "android.intent.action.VIEW", uri.toString(), (String) params.get(ShareRequestParam.REQ_PARAM_PACKAGENAME));
        }
    }

    public static void viewPagePhotoList(Context context, HashMap<String, String> params) throws WeiboNotInstalledException {
        if (context == null) {
            throw new WeiboIllegalParameterException(ExceptionMsg.CONTEXT_ERROR);
        } else if (params == null) {
            throw new WeiboIllegalParameterException(ExceptionMsg.PAGEID_ERROR);
        } else if (TextUtils.isEmpty((CharSequence) params.get(ParamKey.PAGEID))) {
            throw new WeiboIllegalParameterException(ExceptionMsg.PAGEID_ERROR);
        } else if (TextUtils.isEmpty((CharSequence) params.get(ParamKey.CARDID))) {
            throw new WeiboIllegalParameterException(ExceptionMsg.CARDID_ERROR);
        } else {
            int count;
            try {
                count = Integer.parseInt((String) params.get(ParamKey.COUNT));
            } catch (NumberFormatException e) {
                count = -1;
            }
            if (count < 0) {
                throw new WeiboIllegalParameterException(ExceptionMsg.COUNT_ERROR);
            }
            StringBuilder uri = new StringBuilder(Scheme.PAGEPHOTOLIST);
            if (params != null) {
                uri.append(CommonUtils.buildUriQuery(params));
            }
            if (params == null || TextUtils.isEmpty((CharSequence) params.get(ShareRequestParam.REQ_PARAM_PACKAGENAME))) {
                CommonUtils.openWeiboActivity(context, "android.intent.action.VIEW", uri.toString(), null);
                return;
            }
            StringBuilder packageuri = new StringBuilder(Scheme.PAGEPHOTOLIST);
            if (params != null) {
                packageuri.append(CommonUtils.buildUriQuery(params));
            }
            CommonUtils.openWeiboActivity(context, "android.intent.action.VIEW", uri.toString(), (String) params.get(ShareRequestParam.REQ_PARAM_PACKAGENAME));
        }
    }

    public static void viewPageDetailInfo(Context context, HashMap<String, String> params) throws WeiboNotInstalledException {
        if (context == null) {
            throw new WeiboIllegalParameterException(ExceptionMsg.CONTEXT_ERROR);
        } else if (params == null) {
            throw new WeiboIllegalParameterException(ExceptionMsg.PAGEID_ERROR);
        } else if (TextUtils.isEmpty((CharSequence) params.get(ParamKey.PAGEID))) {
            throw new WeiboIllegalParameterException(ExceptionMsg.PAGEID_ERROR);
        } else if (TextUtils.isEmpty((CharSequence) params.get(ParamKey.CARDID))) {
            throw new WeiboIllegalParameterException(ExceptionMsg.CARDID_ERROR);
        } else {
            StringBuilder uri = new StringBuilder(Scheme.PAGEDETAILINFO);
            if (params != null) {
                uri.append(CommonUtils.buildUriQuery(params));
            }
            if (params == null || TextUtils.isEmpty((CharSequence) params.get(ShareRequestParam.REQ_PARAM_PACKAGENAME))) {
                CommonUtils.openWeiboActivity(context, "android.intent.action.VIEW", uri.toString(), null);
                return;
            }
            StringBuilder packageuri = new StringBuilder(Scheme.PAGEDETAILINFO);
            if (params != null) {
                packageuri.append(CommonUtils.buildUriQuery(params));
            }
            CommonUtils.openWeiboActivity(context, "android.intent.action.VIEW", uri.toString(), (String) params.get(ShareRequestParam.REQ_PARAM_PACKAGENAME));
        }
    }

    public static void openInWeiboBrowser(Context context, String url, String sinainternalbrowser, String extParam, String packageName) throws WeiboNotInstalledException {
        if (context == null) {
            throw new WeiboIllegalParameterException(ExceptionMsg.CONTEXT_ERROR);
        } else if (TextUtils.isEmpty(url)) {
            throw new WeiboIllegalParameterException(ExceptionMsg.URL_ERROR);
        } else if (TextUtils.isEmpty(sinainternalbrowser) || "topnav".equals(sinainternalbrowser) || "default".equals(sinainternalbrowser) || "fullscreen".equals(sinainternalbrowser)) {
            StringBuilder uri = new StringBuilder(Scheme.BROWSER);
            HashMap<String, String> paramMap = new HashMap();
            paramMap.put(ShareUrlEditFragment_.URL_ARG, url);
            paramMap.put(ParamKey.SINAINTERNALBROWSER, sinainternalbrowser);
            paramMap.put(ParamKey.EXTPARAM, extParam);
            uri.append(CommonUtils.buildUriQuery(paramMap));
            if (TextUtils.isEmpty(packageName)) {
                CommonUtils.openWeiboActivity(context, "android.intent.action.VIEW", uri.toString(), null);
                return;
            }
            StringBuilder packageuri = new StringBuilder(Scheme.BROWSER);
            if (paramMap != null) {
                packageuri.append(CommonUtils.buildUriQuery(paramMap));
            }
            CommonUtils.openWeiboActivity(context, "android.intent.action.VIEW", uri.toString(), packageName);
        } else {
            throw new WeiboIllegalParameterException(ExceptionMsg.SINAINTERNALBROWSER);
        }
    }

    public static void displayInWeiboMap(Context context, HashMap<String, String> params) throws WeiboNotInstalledException {
        if (context == null) {
            throw new WeiboIllegalParameterException(ExceptionMsg.CONTEXT_ERROR);
        }
        String mapUrl = "http://weibo.cn/dpool/ttt/maps.php?xy=%s,%s&amp;size=320x320&amp;offset=%s";
        String lon = Table.STRING_DEFAULT_VALUE;
        String lat = Table.STRING_DEFAULT_VALUE;
        String offset = Table.STRING_DEFAULT_VALUE;
        if (params != null) {
            lon = (String) params.get(ParamKey.LONGITUDE);
            lat = (String) params.get(ParamKey.LATITUDE);
            offset = (String) params.get(ParamKey.OFFSET);
        }
        String packageName = null;
        if (!(params == null || TextUtils.isEmpty((CharSequence) params.get(ShareRequestParam.REQ_PARAM_PACKAGENAME)))) {
            packageName = (String) params.get(ShareRequestParam.REQ_PARAM_PACKAGENAME);
        }
        if (params != null) {
            openInWeiboBrowser(context, String.format(mapUrl, new Object[]{lon, lat, offset}), "default", (String) params.get(ParamKey.EXTPARAM), packageName);
        }
    }

    public static void openQrcodeScanner(Context context, HashMap<String, String> params) throws WeiboNotInstalledException {
        if (context == null) {
            throw new WeiboIllegalParameterException(ExceptionMsg.CONTEXT_ERROR);
        }
        StringBuilder uri = new StringBuilder(Scheme.QRCODE);
        if (params != null) {
            uri.append(CommonUtils.buildUriQuery(params));
        }
        if (params == null || TextUtils.isEmpty((CharSequence) params.get(ShareRequestParam.REQ_PARAM_PACKAGENAME))) {
            CommonUtils.openWeiboActivity(context, "android.intent.action.VIEW", uri.toString(), null);
            return;
        }
        StringBuilder packageuri = new StringBuilder(Scheme.QRCODE);
        if (params != null) {
            packageuri.append(CommonUtils.buildUriQuery(params));
        }
        CommonUtils.openWeiboActivity(context, "android.intent.action.VIEW", uri.toString(), (String) params.get(ShareRequestParam.REQ_PARAM_PACKAGENAME));
    }

    public static void weiboDetail(Context context, HashMap<String, String> params) throws WeiboNotInstalledException {
        if (context == null) {
            throw new WeiboIllegalParameterException(ExceptionMsg.CONTEXT_ERROR);
        } else if (params == null) {
            throw new WeiboIllegalParameterException(ExceptionMsg.MBLOGID_ERROR);
        } else if (TextUtils.isEmpty((CharSequence) params.get(ParamKey.MBLOGID))) {
            throw new WeiboIllegalParameterException(ExceptionMsg.MBLOGID_ERROR);
        } else {
            StringBuilder uri = new StringBuilder(Scheme.MBLOGDETAIL);
            if (params != null) {
                uri.append(CommonUtils.buildUriQuery(params));
            }
            if (params == null || TextUtils.isEmpty((CharSequence) params.get(ShareRequestParam.REQ_PARAM_PACKAGENAME))) {
                CommonUtils.openWeiboActivity(context, "android.intent.action.VIEW", uri.toString(), null);
                return;
            }
            StringBuilder packageuri = new StringBuilder(Scheme.MBLOGDETAIL);
            if (params != null) {
                packageuri.append(CommonUtils.buildUriQuery(params));
            }
            CommonUtils.openWeiboActivity(context, "android.intent.action.VIEW", uri.toString(), (String) params.get(ShareRequestParam.REQ_PARAM_PACKAGENAME));
        }
    }
}
