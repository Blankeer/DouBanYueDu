package com.douban.book.reader.fragment;

import com.douban.book.reader.R;
import com.douban.book.reader.adapter.BaseArrayAdapter;
import com.douban.book.reader.adapter.ViewBinderAdapter;
import com.douban.book.reader.entity.DepositRecord;
import com.douban.book.reader.manager.Lister;
import com.douban.book.reader.manager.PurchaseManager;
import com.douban.book.reader.view.item.DepositRecordItemView_;
import com.mcxiaoke.next.ui.endless.EndlessListView;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;

@EFragment
public class DepositRecordFragment extends BaseEndlessListFragment<DepositRecord> {
    @Bean
    PurchaseManager mPurchaseManager;

    public Lister<DepositRecord> onCreateLister() {
        return this.mPurchaseManager.listForDeposit();
    }

    public BaseArrayAdapter<DepositRecord> onCreateAdapter() {
        return new ViewBinderAdapter(DepositRecordItemView_.class);
    }

    @AfterViews
    void init() {
        setTitle((int) R.string.title_deposit_record);
    }

    protected void onListViewCreated(EndlessListView listView) {
        setEmptyHint((int) R.string.hint_empty_record_deposit);
    }
}
