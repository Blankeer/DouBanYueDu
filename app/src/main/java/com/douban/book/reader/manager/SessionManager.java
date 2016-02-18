package com.douban.book.reader.manager;

import android.net.Uri;
import com.douban.book.reader.app.App;
import com.douban.book.reader.constant.Constants;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.entity.Session;
import com.douban.book.reader.network.client.RestClient;
import com.douban.book.reader.network.exception.RestException;
import com.douban.book.reader.network.param.FormRequestParam;
import com.douban.book.reader.network.param.JsonRequestParam;
import com.douban.book.reader.network.param.RequestParam;
import com.douban.book.reader.util.Analysis;
import com.douban.book.reader.util.AnalysisUtils;
import com.douban.book.reader.util.Pref;
import com.douban.book.reader.util.ReaderUri;
import com.douban.book.reader.util.StringUtils;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.component.ShareRequestParam;
import com.sina.weibo.sdk.constant.WBConstants;
import com.tencent.open.SocialConstants;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EBean.Scope;

@EBean(scope = Scope.Singleton)
public class SessionManager {
    public static final String OPENID_APPID_QQ = "101082098";
    public static final String OPENID_APPID_WEIBO = "3247508060";
    public static final String OPENID_APPID_WEIXIN = "wx72811b1bff66105e";
    public static final int OPENID_TYPE_QQ = 103;
    public static final int OPENID_TYPE_WEIBO = 104;
    public static final int OPENID_TYPE_WEIXIN = 110;
    private final Uri baseUri;
    private final RestClient<Session> mRestClient;

    public interface SessionRetriever {
        Session retrieve() throws RestException;
    }

    public static class CodeSessionRetriever implements SessionRetriever {
        private final String mCode;

        public CodeSessionRetriever(String code) {
            this.mCode = code;
        }

        public Session retrieve() throws RestException {
            return SessionManager_.getInstance_(App.get()).retrieveByCode(this.mCode);
        }
    }

    public static class DeviceIdSessionRetriever implements SessionRetriever {
        public Session retrieve() throws RestException {
            return SessionManager_.getInstance_(App.get()).retrieveByDeviceId();
        }
    }

    public static class DirectSessionRetriever implements SessionRetriever {
        private final Session mSession;

        public DirectSessionRetriever(Session session) {
            this.mSession = session;
        }

        public Session retrieve() throws RestException {
            return this.mSession;
        }
    }

    public static class OpenIdSessionRetriever implements SessionRetriever {
        private final String mOpenId;
        private final String mOpenIdAccessToken;
        private final int mOpenIdType;

        public OpenIdSessionRetriever(int openIdType, String openId, String openIdAccessToken) {
            this.mOpenIdType = openIdType;
            this.mOpenId = openId;
            this.mOpenIdAccessToken = openIdAccessToken;
        }

        public Session retrieve() throws RestException {
            return SessionManager_.getInstance_(App.get()).retrieveByOpenId(this.mOpenIdType, this.mOpenId, this.mOpenIdAccessToken);
        }
    }

    public static class PasswordSessionRetriever implements SessionRetriever {
        private final String mPassword;
        private final String mUserName;

        public PasswordSessionRetriever(String userName, String password) {
            this.mUserName = userName;
            this.mPassword = password;
        }

        public Session retrieve() throws RestException {
            return SessionManager_.getInstance_(App.get()).retrieveByPassword(this.mUserName, this.mPassword);
        }
    }

    public static class RefreshTokenSessionRetriever implements SessionRetriever {
        public Session retrieve() throws RestException {
            Session lastSession = SessionManager_.getInstance_(App.get()).getSession();
            Session newSession = SessionManager_.getInstance_(App.get()).retrieveByRefreshToken();
            if (!(lastSession == null || newSession == null)) {
                newSession.openIdType = lastSession.openIdType;
            }
            return newSession;
        }
    }

    public SessionManager() {
        this.baseUri = Uri.parse(Pref.ofApp().getString(Key.APP_OAUTH_BASE_URL, Constants.OAUTH2_AUTH_URI_BASE)).buildUpon().appendEncodedPath("service/auth2/token").build();
        this.mRestClient = new RestClient(this.baseUri, Session.class);
    }

    public Session getSession() {
        return (Session) Pref.ofApp().getObject(Key.APP_PREF_CACHE_SESSION, Session.class);
    }

    public void saveSession(Session session) {
        Pref.ofApp().set(Key.APP_PREF_CACHE_SESSION, session);
    }

    public void clearSession() {
        Pref.ofApp().remove(Key.APP_PREF_CACHE_SESSION);
        Pref.ofApp().remove(ShareRequestParam.REQ_PARAM_TOKEN);
        Pref.ofApp().remove(Oauth2AccessToken.KEY_REFRESH_TOKEN);
    }

    public String getAccessToken() {
        Session session = getSession();
        if (session != null) {
            return session.accessToken;
        }
        return Pref.ofApp().getString(ShareRequestParam.REQ_PARAM_TOKEN);
    }

    public String getRefreshToken() {
        Session session = getSession();
        if (session != null) {
            return session.refreshToken;
        }
        return Pref.ofApp().getString(Oauth2AccessToken.KEY_REFRESH_TOKEN);
    }

    public Session retrieveByPassword(String userName, String password) throws RestException {
        RequestParam param = (FormRequestParam) ((FormRequestParam) ((FormRequestParam) baseRequestParam().append(WBConstants.AUTH_PARAMS_GRANT_TYPE, "password")).append("username", userName)).append("password", password);
        if (!StringUtils.equals(Pref.ofApp().getString(Key.APP_CLIENT_ID, Constants.APP_KEY), Constants.APP_KEY)) {
            return (Session) new RestClient("account/auth", Session.class).post(param);
        }
        Session session = (Session) this.mRestClient.post(param);
        Analysis.sendEvent(AnalysisUtils.EVENT_LOGGED_IN_WITH_PASSWORD);
        return session;
    }

    public Session retrieveByRefreshToken() throws RestException {
        return (Session) this.mRestClient.post(((FormRequestParam) baseRequestParam().append(WBConstants.AUTH_PARAMS_GRANT_TYPE, Oauth2AccessToken.KEY_REFRESH_TOKEN)).append(Oauth2AccessToken.KEY_REFRESH_TOKEN, getRefreshToken()));
    }

    public Session retrieveByCode(String code) throws RestException {
        Session session = (Session) this.mRestClient.post(((FormRequestParam) baseRequestParam().append(WBConstants.AUTH_PARAMS_GRANT_TYPE, "web_register")).append("login_code", code));
        Analysis.sendEvent(AnalysisUtils.EVENT_LOGGED_IN_WITH_CODE);
        return session;
    }

    public Session retrieveByOpenId(int openIdType, String openId, String openIdAccessToken) throws RestException {
        Session session = (Session) this.mRestClient.post(((FormRequestParam) ((FormRequestParam) ((FormRequestParam) ((FormRequestParam) ((FormRequestParam) baseRequestParam().append(WBConstants.AUTH_PARAMS_GRANT_TYPE, SocialConstants.PARAM_OPEN_ID)).append(ShareRequestParam.REQ_PARAM_SOURCE, ReaderUri.SCHEME)).append("openid_type", String.valueOf(openIdType))).append("openid_appid", getAppIdByOpenIdType(openIdType))).append(SocialConstants.PARAM_OPEN_ID, openId)).append("openid_access_token", openIdAccessToken));
        session.openIdType = openIdType;
        Analysis.sendEvent(AnalysisUtils.EVENT_LOGGED_IN_WITH_OPENID, String.valueOf(openIdType));
        return session;
    }

    public Session retrieveByDeviceId() throws RestException {
        return (Session) new RestClient("account/register_device", Session.class)
                .post((JsonRequestParam) ((JsonRequestParam) RequestParam.json().append("key", Pref.ofApp()
                        .getString(Key.APP_CLIENT_ID, Constants.APP_KEY)))
                        .append("secret", Pref.ofApp().getString(Key.APP_CLIENT_SECRET, Constants.APP_SECRET)));
    }

    private FormRequestParam baseRequestParam() {
        return (FormRequestParam) ((FormRequestParam) RequestParam.form().append(com.tencent.connect.common.Constants.PARAM_CLIENT_ID, Pref.ofApp().getString(Key.APP_CLIENT_ID, Constants.APP_KEY))).append(WBConstants.AUTH_PARAMS_CLIENT_SECRET, Pref.ofApp().getString(Key.APP_CLIENT_SECRET, Constants.APP_SECRET));
    }

    private static String getAppIdByOpenIdType(int openIdType) {
        switch (openIdType) {
            case OPENID_TYPE_QQ /*103*/:
                return OPENID_APPID_QQ;
            case OPENID_TYPE_WEIBO /*104*/:
                return OPENID_APPID_WEIBO;
            case OPENID_TYPE_WEIXIN /*110*/:
                return OPENID_APPID_WEIXIN;
            default:
                return null;
        }
    }
}
