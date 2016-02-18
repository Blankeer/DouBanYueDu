package com.douban.book.reader.fragment;

import android.os.Bundle;
import android.view.View;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.ListerViewBinderAdapter;
import com.douban.book.reader.entity.GiftPack;
import com.douban.book.reader.manager.GiftPackManager;
import com.douban.book.reader.manager.Lister;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.view.item.GiftPackItemView;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;

@EFragment
public class GiftPackListFragment extends BaseEndlessGridFragment<GiftPack> {
    @Bean
    GiftPackManager mGiftPackManager;

    public GiftPackListFragment() {
        setTitle((int) R.string.title_gift_pack_list);
        setEmptyHint((int) R.string.hint_empty_gift_pack_list);
    }

    public Lister<GiftPack> onCreateLister() {
        return this.mGiftPackManager.listerForGiftPacks();
    }

    public ListerViewBinderAdapter<GiftPack> onCreateAdapter() {
        return new ListerViewBinderAdapter(onCreateLister(), GiftPackItemView.class);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mGridView.setVerticalSpacing(Res.getDimensionPixelSize(R.dimen.general_subview_vertical_padding_normal));
    }
}
