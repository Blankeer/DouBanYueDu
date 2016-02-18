package com.douban.book.reader.manager;

import android.support.annotation.Nullable;
import com.douban.book.reader.app.App;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.entity.Session;
import com.douban.book.reader.entity.UserInfo;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.event.LoggedInEvent;
import com.douban.book.reader.event.UserInfoUpdatedEvent;
import com.douban.book.reader.manager.SessionManager.DeviceIdSessionRetriever;
import com.douban.book.reader.manager.SessionManager.RefreshTokenSessionRetriever;
import com.douban.book.reader.manager.SessionManager.SessionRetriever;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.network.exception.RestException;
import com.douban.book.reader.util.Analysis;
import com.douban.book.reader.util.JsonUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Pref;
import com.douban.book.reader.util.StringUtils;
import java.io.IOException;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EBean.Scope;

@EBean(scope = Scope.Singleton)
public class UserManager extends BaseManager<UserInfo> {
    private static final String TAG;
    @Bean
    SessionManager mSessionManager;
    @Bean
    ShelfManager mShelfManager;
    @Bean
    UnreadCountManager mUnreadCountManager;
    private UserInfo mUserInfo;

    static {
        TAG = UserManager.class.getSimpleName();
    }

    protected UserManager() {
        super("people", UserInfo.class);
    }

    @Deprecated
    public static synchronized UserManager getInstance() {
        UserManager instance_;
        synchronized (UserManager.class) {
            instance_ = UserManager_.getInstance_(App.get());
        }
        return instance_;
    }

    public void logout() {
        clearSession();
        clearUserInfo();
    }

    public int getUserId() {
        return getUserInfo().id;
    }

    public String getDisplayUserName() {
        return getUserInfo().getDisplayName();
    }

    public String getUserAvatarUrl() {
        return getUserInfo().avatar;
    }

    private int getUserType() {
        return getUserInfo().type;
    }

    public boolean isAnonymousUser() {
        if (hasAccessToken() && getUserType() != 0) {
            return false;
        }
        return true;
    }

    public boolean isNormalUser() {
        boolean z = true;
        if (!hasAccessToken()) {
            return false;
        }
        if (getUserType() != 1) {
            z = false;
        }
        return z;
    }

    public boolean isThirdPartyUser() {
        Session session = this.mSessionManager.getSession();
        return session != null && session.isOpenIdLogin();
    }

    public String getUserTypeName() {
        Session session = this.mSessionManager.getSession();
        if (session == null) {
            return "<None>";
        }
        if (isThirdPartyUser()) {
            return session.getOpenIdTypeName();
        }
        if (isAnonymousUser()) {
            return "Anonymous";
        }
        if (isNormalUser()) {
            return "Douban";
        }
        return "<None>";
    }

    public boolean isWeiboUser() {
        Session session = this.mSessionManager.getSession();
        return session != null && session.openIdType == Header.DOUBLE_1;
    }

    public boolean isQQUser() {
        Session session = this.mSessionManager.getSession();
        return session != null && session.openIdType == Header.DOUBLE_0;
    }

    public boolean hasAccessToken() {
        return StringUtils.isNotEmpty(this.mSessionManager.getAccessToken());
    }

    @Nullable
    private UserInfo getLocalUserInfo() {
        if (this.mUserInfo == null) {
            this.mUserInfo = (UserInfo) Pref.ofApp().getObject(Key.APP_USER_INFO, UserInfo.class);
        }
        return this.mUserInfo;
    }

    public UserInfo getUserInfo() {
        UserInfo userInfo = getLocalUserInfo();
        if (userInfo == null) {
            return UserInfo.createDefault();
        }
        return userInfo;
    }

    public UserInfo loadUserInfo(Object id) {
        IOException e;
        try {
            if (!id.equals(Integer.valueOf(getUserId()))) {
                return (UserInfo) get(id);
            }
            UserInfo userInfo = getLocalUserInfo();
            if (userInfo == null) {
                return getCurrentUserFromServer();
            }
            return userInfo;
        } catch (DataLoadException e2) {
            e = e2;
            Logger.e(TAG, e);
            return UserInfo.createDefault();
        } catch (RestException e3) {
            e = e3;
            Logger.e(TAG, e);
            return UserInfo.createDefault();
        }
    }

    public void saveUserInfo(UserInfo userInfo) {
        this.mUserInfo = userInfo;
        Logger.d(TAG, "saveUserInfo() %s", JsonUtils.toJson(this.mUserInfo));
        Pref.ofApp().set(Key.APP_USER_INFO, userJson);
        EventBusUtils.post(new UserInfoUpdatedEvent());
    }

    public void clearUserInfo() {
        Pref.ofApp().remove(Key.APP_USER_INFO);
        this.mUserInfo = null;
    }

    public void clearSession() {
        this.mSessionManager.clearSession();
    }

    public void anonymousLogin() throws RestException {
        login(new DeviceIdSessionRetriever());
    }

    public void refreshLogin() throws RestException {
        login(new RefreshTokenSessionRetriever());
    }

    public void login(SessionRetriever sessionRetriever) throws RestException {
        int lastUserId = getUserId();
        boolean wasAnonymousUser = isAnonymousUser();
        Session session = sessionRetriever.retrieve();
        this.mSessionManager.saveSession(session);
        getCurrentUserFromServer();
        if (wasAnonymousUser && lastUserId != getUserId()) {
            saveAlias(lastUserId);
        }
        EventBusUtils.post(new LoggedInEvent());
        Analysis.onLogin(App.get(), (long) session.doubanUserId);
    }

    public UserInfo getCurrentUserFromServer() throws RestException {
        UserInfo userInfo = (UserInfo) getRestClient().getEntity();
        if (userInfo != null) {
            saveUserInfo(userInfo);
        }
        this.mUnreadCountManager.refresh();
        return userInfo;
    }

    private void saveAlias(int aliasUserId) {
        Pref.ofApp().set(String.format(Key.APP_USER_ALIAS_, new Object[]{Integer.valueOf(getUserId())}), Integer.valueOf(aliasUserId));
    }

    public String getUserDataPath() {
        int userId = getUserId();
        int alias = Pref.ofApp().getInt(String.format(Key.APP_USER_ALIAS_, new Object[]{Integer.valueOf(getUserId())}), 0);
        if (alias <= 0) {
            alias = userId;
        }
        return String.valueOf(alias);
    }
}
