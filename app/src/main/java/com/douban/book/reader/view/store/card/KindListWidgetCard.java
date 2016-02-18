package com.douban.book.reader.view.store.card;

import android.content.Context;
import android.net.Uri.Builder;
import com.douban.book.reader.R;
import com.douban.book.reader.constant.Dimen;
import com.douban.book.reader.entity.WorksKind;
import com.douban.book.reader.entity.store.KindListStoreWidgetEntity;
import com.douban.book.reader.helper.AppUri;
import com.douban.book.reader.manager.WorksKindManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.ReaderUri;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.view.card.WorksKindGridView;
import com.douban.book.reader.view.card.WorksKindGridView_;
import com.sina.weibo.sdk.register.mobile.SelectCountryActivity;
import io.realm.internal.Table;
import java.util.ArrayList;
import java.util.List;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;

@EViewGroup
public class KindListWidgetCard extends BaseWidgetCard<KindListStoreWidgetEntity> {
    @Bean
    WorksKindManager mWorksKindManager;

    public KindListWidgetCard(Context context) {
        super(context);
    }

    protected void onEntityBound(KindListStoreWidgetEntity entity) {
        loadRootKind(entity);
    }

    @UiThread
    void updateViews(KindListStoreWidgetEntity entity, WorksKind worksRootKind) {
        int maxKindCount;
        noDivider();
        List<WorksKind> kindList = new ArrayList(entity.payload.kindList);
        WorksKind moreKind = null;
        if (entity.payload.hasMoreBtn) {
            moreKind = new WorksKind();
            moreKind.name = Res.getString(R.string.btn_more);
            moreKind.uri = new Builder().scheme(ReaderUri.SCHEME).authority(AppUri.AUTHORITY).appendPath(AppUri.PATH_WORKS_KIND).appendQueryParameter(SelectCountryActivity.EXTRA_COUNTRY_NAME, worksRootKind != null ? worksRootKind.name : Table.STRING_DEFAULT_VALUE).toString();
        }
        WorksKindGridView gridView = (WorksKindGridView) getOrCreateContentView(WorksKindGridView_.class);
        gridView.setShowWorksKindCount(false);
        if (Dimen.isLargeScreen()) {
            maxKindCount = 9;
            gridView.setNumColumns(5);
        } else {
            maxKindCount = 8;
            gridView.setNumColumns(3);
        }
        if (kindList.size() >= maxKindCount && moreKind != null) {
            for (int index = kindList.size() - 1; index >= maxKindCount; index--) {
                kindList.remove(index);
            }
            kindList.add(maxKindCount, moreKind);
        }
        gridView.setWorksKindList(kindList);
    }

    @Background
    void loadRootKind(KindListStoreWidgetEntity entity) {
        WorksKind worksRootKind = null;
        try {
            worksRootKind = this.mWorksKindManager.getRootKind((WorksKind) entity.payload.kindList.get(0));
        } catch (DataLoadException e) {
            Logger.e(this.TAG, e);
        } catch (Exception e2) {
            Logger.e(this.TAG, e2);
        }
        updateViews(entity, worksRootKind);
    }
}
