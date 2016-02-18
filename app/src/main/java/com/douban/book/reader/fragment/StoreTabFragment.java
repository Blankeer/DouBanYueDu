package com.douban.book.reader.fragment;

import android.view.View;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.entity.store.BaseStoreWidgetEntity;
import com.douban.book.reader.entity.store.StoreTabEntity;
import com.douban.book.reader.manager.StoreManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.view.LoadErrorPageView.RefreshClickListener;
import com.douban.book.reader.view.card.Card;
import com.douban.book.reader.view.card.CardView.CardFactory;
import com.douban.book.reader.view.store.card.BaseWidgetCard;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;

@EFragment
public class StoreTabFragment extends BaseCardFragment {
    public static final String STORE_TAB_BOOK = "book";
    public static final String STORE_TAB_MAGAZINE = "magazine";
    public static final String STORE_TAB_ORIGINAL_WORKS = "origin_works";
    public static final String STORE_TAB_PROMOTION = "promotion";
    private boolean mIsRefreshing;
    @Bean
    StoreManager mStoreManager;
    private StoreTabEntity mStoreTab;
    @FragmentArg
    String tabName;

    public StoreTabFragment() {
        this.mIsRefreshing = false;
    }

    private boolean isRefreshing() {
        return this.mIsRefreshing;
    }

    @AfterViews
    void init() {
        enablePullToRefresh(true);
        loadData();
    }

    public void onRefresh() {
        if (!isRefreshing()) {
            this.mIsRefreshing = true;
            loadData();
        }
    }

    @UiThread
    void updateCards() {
        removeAllCards();
        if (this.mStoreTab != null) {
            if (StringUtils.isNotEmpty(this.mStoreTab.title)) {
                setTitle(this.mStoreTab.title);
            }
            setCardFactory(new CardFactory() {
                public int cardCount() {
                    return StoreTabFragment.this.mStoreTab.widgetList.size();
                }

                public Card createCard(int position, View convertView) {
                    long before = System.currentTimeMillis();
                    Card card = BaseWidgetCard.createOrRebind(App.get(), convertView, (BaseStoreWidgetEntity) StoreTabFragment.this.mStoreTab.widgetList.get(position));
                    if (card == null) {
                        throw new RuntimeException(String.format("Failed creating card (type=%s).", new Object[]{entity.type}));
                    }
                    Logger.d(StoreTabFragment.this.TAG, "[%dms] Creating %s", Long.valueOf(System.currentTimeMillis() - before), card);
                    return card;
                }
            });
        }
    }

    @Background
    void loadData() {
        try {
            showLoadingDialog();
            if (isRefreshing()) {
                this.mStoreTab = this.mStoreManager.getFromRemote(this.tabName);
            } else {
                this.mStoreTab = this.mStoreManager.getTab(this.tabName);
            }
            onLoadDataSucceed();
            dismissLoadingDialog();
        } catch (Throwable e) {
            if (isRefreshing()) {
                ToastUtils.showToast(e, (int) R.string.toast_general_refresh_failed);
            } else {
                onLoadDataFailed(e);
            }
            dismissLoadingDialog();
        } catch (Throwable th) {
            dismissLoadingDialog();
        }
    }

    @UiThread
    void onLoadDataSucceed() {
        if (isRefreshing()) {
            ToastUtils.showToast((int) R.string.toast_general_refresh_success);
            this.mIsRefreshing = false;
        }
        updateCards();
    }

    @UiThread
    void onLoadDataFailed(DataLoadException e) {
        if (isRefreshing()) {
            ToastUtils.showToast((int) R.string.toast_general_refresh_failed);
            this.mIsRefreshing = false;
        }
        showLoadErrorPage(e, new RefreshClickListener() {
            public void onClick() {
                StoreTabFragment.this.loadData();
            }
        });
    }

    public String getName() {
        return this.tabName;
    }
}
