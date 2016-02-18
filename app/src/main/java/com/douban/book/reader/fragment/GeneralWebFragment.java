package com.douban.book.reader.fragment;

import android.support.v4.app.Fragment;
import android.view.Menu;
import com.douban.book.reader.R;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.fragment.share.ShareUrlEditFragment_;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.UriUtils;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;

@EFragment
@OptionsMenu({2131623944})
public class GeneralWebFragment extends BaseWebFragment {
    @FragmentArg
    String url;

    @AfterViews
    void init() {
        if (StringUtils.isNotEmpty(this.url)) {
            loadUrl(this.url);
        }
    }

    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        showMenuItemsIf(UriUtils.isPublicUri(this.url), R.id.action_share);
    }

    @OptionsItem({2131558988})
    void onMenuItemShare() {
        ShareUrlEditFragment_.builder().defaultTitle(StringUtils.toStr(getTitle())).url(getCurrentUrl()).build().showAsActivity(PageOpenHelper.from((Fragment) this));
    }
}
