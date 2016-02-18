package com.sina.weibo.sdk.component;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import com.igexin.download.Downloads;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.constant.WBPageConstants.ParamKey;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.exception.WeiboHttpException;
import com.sina.weibo.sdk.net.HttpManager;
import com.sina.weibo.sdk.net.NetStateManager;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.utils.LogUtil;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.realm.internal.Table;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;

public class GameManager {
    private static final String BOUNDARY;
    public static final String DEFAULT_CHARSET = "UTF-8";
    private static final String HTTP_METHOD_GET = "GET";
    private static final String HTTP_METHOD_POST = "POST";
    private static String INVITATION_ONE_FRINED_URL = null;
    private static String INVITATION_URL = null;
    private static final String MULTIPART_FORM_DATA = "multipart/form-data";
    private static final String TAG = "GameManager";
    private static StringBuffer URL;
    private static String URL_ACHIEVEMENT_ADD_UPDATE;
    private static String URL_ACHIEVEMENT_READ_PLAYER_FRIENDS;
    private static String URL_ACHIEVEMENT_READ_PLAYER_SCORE;
    private static String URL_ACHIEVEMENT_RELATION_ADD_UPDATE;
    private static String URL_ACHIEVEMENT_SCORE_ADD_UPDATE;
    private static String URL_ACHIEVEMENT_USER_GAIN;

    static {
        URL = new StringBuffer("https://api.weibo.com/2/proxy/darwin/graph/game/");
        BOUNDARY = HttpManager.getBoundry();
        URL_ACHIEVEMENT_ADD_UPDATE = URL + "achievement/add.json";
        URL_ACHIEVEMENT_RELATION_ADD_UPDATE = URL + "achievement/gain/add.json";
        URL_ACHIEVEMENT_SCORE_ADD_UPDATE = URL + "score/add.json";
        URL_ACHIEVEMENT_READ_PLAYER_SCORE = URL + "score/read_player.json";
        URL_ACHIEVEMENT_READ_PLAYER_FRIENDS = URL + "score/read_player_friends.json";
        URL_ACHIEVEMENT_USER_GAIN = URL + "achievement/user_gain.json";
        INVITATION_URL = "http://widget.weibo.com/invitation/app.php?";
        INVITATION_ONE_FRINED_URL = "http://widget.weibo.com/invitation/appinfo.php?";
    }

    public static String AddOrUpdateGameAchievement(Context context, WeiboParameters params) {
        SimpleDateFormat myFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        params.put("updated_time", myFmt.format(date));
        if (TextUtils.isEmpty((String) params.get(WBConstants.GAME_PARAMS_GAME_CREATE_TIME))) {
            params.put(WBConstants.GAME_PARAMS_GAME_CREATE_TIME, myFmt.format(date));
        }
        String ans = HttpManager.readRsponse(requestHttpExecute(context, URL_ACHIEVEMENT_ADD_UPDATE, HTTP_METHOD_POST, params));
        LogUtil.d(TAG, "Response : " + ans);
        return ans;
    }

    public static String addOrUpdateGameAchievementRelation(Context context, WeiboParameters params) {
        SimpleDateFormat myFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        params.put("updated_time", myFmt.format(date));
        if (TextUtils.isEmpty((String) params.get(WBConstants.GAME_PARAMS_GAME_CREATE_TIME))) {
            params.put(WBConstants.GAME_PARAMS_GAME_CREATE_TIME, myFmt.format(date));
        }
        String ans = HttpManager.readRsponse(requestHttpExecute(context, URL_ACHIEVEMENT_RELATION_ADD_UPDATE, HTTP_METHOD_POST, params));
        LogUtil.d(TAG, "Response : " + ans);
        return ans;
    }

    public static String addOrUpdateAchievementScore(Context context, String access_token, String appKey, String game_id, String user_id, String score) {
        WeiboParameters params = new WeiboParameters(Table.STRING_DEFAULT_VALUE);
        if (!TextUtils.isEmpty(access_token)) {
            params.put(ShareRequestParam.REQ_PARAM_TOKEN, access_token);
        }
        if (!TextUtils.isEmpty(appKey)) {
            params.put(ShareRequestParam.REQ_PARAM_SOURCE, appKey);
        }
        if (!TextUtils.isEmpty(game_id)) {
            params.put(WBConstants.GAME_PARAMS_GAME_ID, game_id);
        }
        if (!TextUtils.isEmpty(user_id)) {
            params.put(ParamKey.UID, user_id);
        }
        if (!TextUtils.isEmpty(score)) {
            params.put(WBConstants.GAME_PARAMS_SCORE, score);
        }
        SimpleDateFormat myFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        params.put("updated_time", myFmt.format(date));
        if (TextUtils.isEmpty((String) params.get(WBConstants.GAME_PARAMS_GAME_CREATE_TIME))) {
            params.put(WBConstants.GAME_PARAMS_GAME_CREATE_TIME, myFmt.format(date));
        }
        String ans = HttpManager.readRsponse(requestHttpExecute(context, URL_ACHIEVEMENT_SCORE_ADD_UPDATE, HTTP_METHOD_POST, params));
        LogUtil.d(TAG, "Response : " + ans);
        return ans;
    }

    public static String readPlayerScoreInfo(Context context, String access_token, String appKey, String game_id, String user_id) {
        WeiboParameters params = new WeiboParameters(Table.STRING_DEFAULT_VALUE);
        if (!TextUtils.isEmpty(access_token)) {
            params.put(ShareRequestParam.REQ_PARAM_TOKEN, access_token);
        }
        if (!TextUtils.isEmpty(appKey)) {
            params.put(ShareRequestParam.REQ_PARAM_SOURCE, appKey);
        }
        if (!TextUtils.isEmpty(game_id)) {
            params.put(WBConstants.GAME_PARAMS_GAME_ID, game_id);
        }
        if (!TextUtils.isEmpty(user_id)) {
            params.put(ParamKey.UID, user_id);
        }
        String ans = HttpManager.readRsponse(requestHttpExecute(context, URL_ACHIEVEMENT_READ_PLAYER_SCORE, HTTP_METHOD_GET, params));
        LogUtil.d(TAG, "Response : " + ans);
        return ans;
    }

    public static String readPlayerFriendsScoreInfo(Context context, String access_token, String appKey, String game_id, String user_id) {
        WeiboParameters params = new WeiboParameters(Table.STRING_DEFAULT_VALUE);
        if (!TextUtils.isEmpty(access_token)) {
            params.put(ShareRequestParam.REQ_PARAM_TOKEN, access_token);
        }
        if (!TextUtils.isEmpty(appKey)) {
            params.put(ShareRequestParam.REQ_PARAM_SOURCE, appKey);
        }
        if (!TextUtils.isEmpty(game_id)) {
            params.put(WBConstants.GAME_PARAMS_GAME_ID, game_id);
        }
        if (!TextUtils.isEmpty(user_id)) {
            params.put(ParamKey.UID, user_id);
        }
        params.put(WBConstants.GAME_PARAMS_GAME_CREATE_TIME, new Timestamp(new Date().getTime()));
        String ans = HttpManager.readRsponse(requestHttpExecute(context, URL_ACHIEVEMENT_READ_PLAYER_FRIENDS, HTTP_METHOD_GET, params));
        LogUtil.d(TAG, "Response : " + ans);
        return ans;
    }

    public static String readPlayerAchievementGain(Context context, String access_token, String appKey, String game_id, String user_id) {
        WeiboParameters params = new WeiboParameters(Table.STRING_DEFAULT_VALUE);
        if (!TextUtils.isEmpty(access_token)) {
            params.put(ShareRequestParam.REQ_PARAM_TOKEN, access_token);
        }
        if (!TextUtils.isEmpty(appKey)) {
            params.put(ShareRequestParam.REQ_PARAM_SOURCE, appKey);
        }
        if (!TextUtils.isEmpty(game_id)) {
            params.put(WBConstants.GAME_PARAMS_GAME_ID, game_id);
        }
        if (!TextUtils.isEmpty(user_id)) {
            params.put(ParamKey.UID, user_id);
        }
        params.put(WBConstants.GAME_PARAMS_GAME_CREATE_TIME, new Timestamp(new Date().getTime()));
        String ans = HttpManager.readRsponse(requestHttpExecute(context, URL_ACHIEVEMENT_USER_GAIN, HTTP_METHOD_GET, params));
        LogUtil.d(TAG, "Response : " + ans);
        return ans;
    }

    public void invatationWeiboFriendsByList(Context context, String access_token, String appKey, String title, WeiboAuthListener listener) {
        WeiboParameters requestParams = new WeiboParameters(appKey);
        requestParams.put(ShareRequestParam.REQ_PARAM_TOKEN, access_token);
        requestParams.put(ShareRequestParam.REQ_PARAM_SOURCE, appKey);
        String UrlStr = new StringBuilder(String.valueOf(INVITATION_URL.toString())).append(requestParams.encodeUrl()).toString();
        GameRequestParam reqParam = new GameRequestParam(context);
        reqParam.setAppKey(appKey);
        reqParam.setToken(access_token);
        reqParam.setLauncher(BrowserLauncher.GAME);
        reqParam.setUrl(UrlStr);
        reqParam.setAuthListener(listener);
        Intent intent = new Intent(context, WeiboSdkBrowser.class);
        Bundle data = reqParam.createRequestParamBundle();
        data.putString("key_specify_title", title);
        intent.putExtras(data);
        context.startActivity(intent);
    }

    public void invatationWeiboFriendsInOnePage(Context context, String access_token, String appKey, String title, WeiboAuthListener listener, ArrayList<String> userIdList) {
        StringBuffer userIds = new StringBuffer();
        if (userIdList != null) {
            for (int i = 0; i < userIdList.size(); i++) {
                String user = (String) userIdList.get(i);
                if (i == 0) {
                    userIds.append(user);
                } else {
                    userIds.append("," + user);
                }
            }
        }
        WeiboParameters requestParams = new WeiboParameters(appKey);
        requestParams.put(ShareRequestParam.REQ_PARAM_TOKEN, access_token);
        requestParams.put(ShareRequestParam.REQ_PARAM_SOURCE, appKey);
        String UrlStr = new StringBuilder(String.valueOf(INVITATION_ONE_FRINED_URL.toString())).append(requestParams.encodeUrl()).append("&uids=").append(userIds.toString()).toString();
        GameRequestParam reqParam = new GameRequestParam(context);
        reqParam.setAppKey(appKey);
        reqParam.setToken(access_token);
        reqParam.setLauncher(BrowserLauncher.GAME);
        reqParam.setUrl(UrlStr);
        reqParam.setAuthListener(listener);
        Intent intent = new Intent(context, WeiboSdkBrowser.class);
        Bundle data = reqParam.createRequestParamBundle();
        data.putString("key_specify_title", title);
        intent.putExtras(data);
        context.startActivity(intent);
    }

    private static HttpResponse requestHttpExecute(Context context, String url, String method, WeiboParameters params) {
        Throwable e;
        Throwable th;
        HttpClient client = null;
        ByteArrayOutputStream baos = null;
        try {
            client = HttpManager.getNewHttpClient();
            client.getParams().setParameter("http.route.default-proxy", NetStateManager.getAPN());
            HttpUriRequest request = null;
            if (method.equals(HTTP_METHOD_GET)) {
                url = new StringBuilder(String.valueOf(url)).append("?").append(params.encodeUrl()).toString();
                request = new HttpGet(url);
                LogUtil.d(TAG, "requestHttpExecute GET Url : " + url);
            } else {
                if (method.equals(HTTP_METHOD_POST)) {
                    LogUtil.d(TAG, "requestHttpExecute POST Url : " + url);
                    HttpPost post = new HttpPost(url);
                    Object request2 = post;
                    ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                    try {
                        if (params.hasBinaryData()) {
                            post.setHeader(HttpRequest.HEADER_CONTENT_TYPE, "multipart/form-data; boundary=" + BOUNDARY);
                            HttpManager.buildParams(baos2, params);
                        } else {
                            Object value = params.get("content-type");
                            if (value == null || !(value instanceof String)) {
                                post.setHeader(HttpRequest.HEADER_CONTENT_TYPE, HttpRequest.CONTENT_TYPE_FORM);
                            } else {
                                params.remove("content-type");
                                post.setHeader(HttpRequest.HEADER_CONTENT_TYPE, (String) value);
                            }
                            String postParam = params.encodeUrl();
                            LogUtil.d(TAG, "requestHttpExecute POST postParam : " + postParam);
                            baos2.write(postParam.getBytes(DEFAULT_CHARSET));
                        }
                        post.setEntity(new ByteArrayEntity(baos2.toByteArray()));
                        baos = baos2;
                    } catch (IOException e2) {
                        e = e2;
                        baos = baos2;
                        try {
                            throw new WeiboException(e);
                        } catch (Throwable th2) {
                            th = th2;
                            if (baos != null) {
                                try {
                                    baos.close();
                                } catch (IOException e3) {
                                }
                            }
                            HttpManager.shutdownHttpClient(client);
                            throw th;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        baos = baos2;
                        if (baos != null) {
                            baos.close();
                        }
                        HttpManager.shutdownHttpClient(client);
                        throw th;
                    }
                }
                if (method.equals(HttpRequest.METHOD_DELETE)) {
                    request = new HttpDelete(url);
                }
            }
            HttpResponse response = client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != Downloads.STATUS_SUCCESS) {
                throw new WeiboHttpException(HttpManager.readRsponse(response), statusCode);
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e4) {
                }
            }
            HttpManager.shutdownHttpClient(client);
            return response;
        } catch (IOException e5) {
            e = e5;
            throw new WeiboException(e);
        }
    }
}
