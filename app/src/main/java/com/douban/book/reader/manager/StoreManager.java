package com.douban.book.reader.manager;

import com.douban.book.reader.entity.store.StoreTabEntity;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.network.param.RequestParam;
import com.sina.weibo.sdk.register.mobile.SelectCountryActivity;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EBean.Scope;

@EBean(scope = Scope.Singleton)
public class StoreManager extends BaseManager<StoreTabEntity> {
    public StoreManager() {
        super("store/index", StoreTabEntity.class);
        maxStaleness(28800000);
    }

    public StoreTabEntity getTab(String tabName) throws DataLoadException {
        return (StoreTabEntity) get((Object) tabName);
    }

    public StoreTabEntity getFromRemote(Object tabName) throws DataLoadException {
        return (StoreTabEntity) get(RequestParam.queryString().append(SelectCountryActivity.EXTRA_COUNTRY_NAME, tabName));
    }
}
