package com.douban.book.reader.fragment;

import com.douban.book.reader.R;
import com.douban.book.reader.adapter.BaseArrayAdapter;
import com.douban.book.reader.adapter.ViewBinderAdapter;
import com.douban.book.reader.entity.RedeemRecord;
import com.douban.book.reader.manager.Lister;
import com.douban.book.reader.manager.PurchaseManager;
import com.douban.book.reader.view.item.RedeemRecordItemView_;
import com.mcxiaoke.next.ui.endless.EndlessListView;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;

@EFragment
public class RedemptionRecordFragment extends BaseEndlessListFragment<RedeemRecord> {
    @Bean
    PurchaseManager mPurchaseManager;

    public Lister<RedeemRecord> onCreateLister() {
        return this.mPurchaseManager.listForRedemptionRecord();
    }

    public BaseArrayAdapter<RedeemRecord> onCreateAdapter() {
        return new ViewBinderAdapter(RedeemRecordItemView_.class);
    }

    @AfterViews
    void init() {
        setTitle((int) R.string.title_redeem_works_record);
    }

    protected void onListViewCreated(EndlessListView listView) {
        setEmptyHint((int) R.string.hint_empty_record_redemption);
    }
}
