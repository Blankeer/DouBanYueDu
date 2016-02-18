package com.douban.book.reader.fragment;

import com.douban.book.reader.R;
import com.douban.book.reader.adapter.ListerViewBinderAdapter;
import com.douban.book.reader.entity.Gift;
import com.douban.book.reader.manager.GiftManager;
import com.douban.book.reader.manager.Lister;
import com.douban.book.reader.view.item.GiftItemView_;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;

@EFragment
public class GiftListFragment extends BaseEndlessGridFragment<Gift> {
    @Bean
    GiftManager mGiftManager;

    public GiftListFragment() {
        setTitle((int) R.string.title_gift_list);
        setEmptyHint((int) R.string.hint_empty_gift_list);
    }

    public Lister<Gift> onCreateLister() {
        return this.mGiftManager.listerForReceivedGifts();
    }

    public ListerViewBinderAdapter<Gift> onCreateAdapter() {
        return new ListerViewBinderAdapter(onCreateLister(), GiftItemView_.class);
    }
}
