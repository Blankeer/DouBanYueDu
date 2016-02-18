package com.douban.book.reader.helper;

import com.douban.book.reader.R;
import com.douban.book.reader.activity.HomeActivity;
import com.douban.book.reader.activity.StoreSearchActivity_;
import com.douban.book.reader.app.App;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.fragment.SettingFragment_;
import com.douban.book.reader.fragment.ShelfFragment_;
import com.douban.book.reader.fragment.StoreFragment_;
import com.douban.book.reader.fragment.TestFieldFragment_;
import com.douban.book.reader.fragment.TestWorksFragment_;

public class DrawerItemClickHelper {
    public static final String TAG;

    static {
        TAG = DrawerItemClickHelper.class.getSimpleName();
    }

    public void performClick(int clickedItemId, PageOpenHelper helper) {
        switch (clickedItemId) {
            case R.id.drawer_menu_shelf /*2131558619*/:
                HomeActivity.showContent(helper, ShelfFragment_.class);
            case R.id.drawer_menu_store /*2131558620*/:
                HomeActivity.showContent(helper, StoreFragment_.class);
            case R.id.drawer_menu_search /*2131558621*/:
                helper.open(StoreSearchActivity_.intent(App.get()).get());
            case R.id.drawer_menu_test_field /*2131558623*/:
                HomeActivity.showContent(helper, TestFieldFragment_.class);
            case R.id.drawer_menu_test_works /*2131558625*/:
                HomeActivity.showContent(helper, TestWorksFragment_.class);
            case R.id.drawer_menu_settings /*2131558626*/:
                SettingFragment_.builder().build().showAsActivity(helper);
            default:
        }
    }
}
