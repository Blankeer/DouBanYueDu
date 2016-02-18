package com.douban.book.reader.fragment;

import com.douban.book.reader.R;
import com.douban.book.reader.adapter.BaseArrayAdapter;
import com.douban.book.reader.adapter.ViewBinderAdapter;
import com.douban.book.reader.entity.CouponRecord;
import com.douban.book.reader.manager.Lister;
import com.douban.book.reader.manager.PurchaseManager;
import com.douban.book.reader.view.item.CouponRecordItemView_;
import com.mcxiaoke.next.ui.endless.EndlessListView;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;

@EFragment
public class CouponRecordFragment extends BaseEndlessListFragment<CouponRecord> {
    @Bean
    PurchaseManager mPurchaseManager;

    public Lister<CouponRecord> onCreateLister() {
        return this.mPurchaseManager.listForCouponRecord();
    }

    public BaseArrayAdapter<CouponRecord> onCreateAdapter() {
        return new ViewBinderAdapter(CouponRecordItemView_.class);
    }

    @AfterViews
    void init() {
        setTitle((int) R.string.title_redeem_amount_record);
    }

    protected void onListViewCreated(EndlessListView listView) {
        setEmptyHint((int) R.string.hint_empty_record_coupon);
    }
}
