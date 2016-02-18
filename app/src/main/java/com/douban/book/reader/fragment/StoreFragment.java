package com.douban.book.reader.fragment;

import android.support.v4.app.Fragment;
import com.douban.book.reader.R;
import com.douban.book.reader.activity.StoreSearchActivity_;
import io.realm.internal.Table;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;

@EFragment
@OptionsMenu({2131623947})
public class StoreFragment extends TabFragment {
    public StoreFragment() {
        setOffScreenPageLimit(2);
        appendTab(StoreTabFragment_.builder().tabName(StoreTabFragment.STORE_TAB_ORIGINAL_WORKS).build().setTitle((int) R.string.title_original_works).setIcon((int) R.drawable.v_self_publishing));
        appendTab(StoreTabFragment_.builder().tabName(StoreTabFragment.STORE_TAB_BOOK).build().setTitle((int) R.string.title_book).setIcon((int) R.drawable.v_ebooks));
        appendTab(StoreTabFragment_.builder().tabName(StoreTabFragment.STORE_TAB_MAGAZINE).build().setTitle((int) R.string.title_magazine).setIcon((int) R.drawable.v_magazine));
        setTitle((int) R.string.act_title_store);
        setDefaultPage(1);
    }

    @OptionsItem({2131558991})
    void onMenuItemSearchClicked() {
        StoreSearchActivity_.intent((Fragment) this).start();
    }

    @OptionsItem({2131558992})
    void onMenuItemWorksKindClicked() {
        WorksKindFragment_.builder().defaultTabTitle(getCurrentTabTitle()).build().showAsActivity((Fragment) this);
    }

    private String getCurrentTabTitle() {
        BaseFragment tab = getCurrentTab();
        if (tab != null) {
            return tab.getTitle().toString();
        }
        return Table.STRING_DEFAULT_VALUE;
    }
}
