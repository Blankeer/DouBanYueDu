package com.douban.book.reader.manager;

import com.douban.book.reader.constant.Key;
import com.douban.book.reader.entity.UserInfo;
import com.douban.book.reader.entity.WorksAgent;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.event.UserAgentUpdatedEvent;
import com.douban.book.reader.manager.cache.PrefCache;
import com.douban.book.reader.util.Logger;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EBean.Scope;

@EBean(scope = Scope.Singleton)
public class WorksAgentManager extends BaseManager<WorksAgent> {
    public WorksAgentManager() {
        super("agents", WorksAgent.class);
        cache(new PrefCache(Key.APP_PREF_CACHE_WORKS_AGENT, WorksAgent.class));
    }

    public void checkUserAgent() {
        try {
            UserInfo userInfo = UserManager.getInstance().getUserInfo();
            if (userInfo != null && userInfo.isAuthor()) {
                getFromRemote(Integer.valueOf(userInfo.agentId));
                EventBusUtils.post(new UserAgentUpdatedEvent());
            }
        } catch (Exception e) {
            Logger.e(this.TAG, e);
        }
    }
}
