package com.douban.book.reader.manager;

import com.douban.book.reader.entity.PageMeta;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.network.param.RequestParam;
import com.sina.weibo.sdk.register.mobile.SelectCountryActivity;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EBean.Scope;

@EBean(scope = Scope.Singleton)
public class PageManager extends BaseManager<PageMeta> {

    public enum Page {
        COLUMN,
        HERMES;

        public String toString() {
            return name().toLowerCase();
        }
    }

    public PageManager() {
        super("page_meta", PageMeta.class);
    }

    public PageMeta getPageMeta(Page page) throws DataLoadException {
        return (PageMeta) get((RequestParam) RequestParam.queryString().append(SelectCountryActivity.EXTRA_COUNTRY_NAME, page));
    }
}
