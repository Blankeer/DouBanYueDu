package com.douban.book.reader.fragment;

import com.douban.book.reader.R;
import com.douban.book.reader.adapter.BaseArrayAdapter;
import com.douban.book.reader.adapter.ViewBinderAdapter;
import com.douban.book.reader.entity.PurchaseRecord;
import com.douban.book.reader.manager.Lister;
import com.douban.book.reader.manager.PurchaseManager;
import com.douban.book.reader.view.item.PurchaseRecordItemView_;
import com.mcxiaoke.next.ui.endless.EndlessListView;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;

@EFragment
public class PurchaseRecordFragment extends BaseEndlessListFragment<PurchaseRecord> {
    @Bean
    PurchaseManager mPurchaseManager;

    public Lister<PurchaseRecord> onCreateLister() {
        return this.mPurchaseManager.listForPurchase();
    }

    public BaseArrayAdapter<PurchaseRecord> onCreateAdapter() {
        return new ViewBinderAdapter(PurchaseRecordItemView_.class);
    }

    @AfterViews
    void init() {
        setTitle((int) R.string.title_purchase_record);
    }

    protected void onListViewCreated(EndlessListView listView) {
        setEmptyHint((int) R.string.hint_empty_record_purchase);
    }
}
