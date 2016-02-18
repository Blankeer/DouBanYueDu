package com.douban.book.reader.fragment;

import android.app.Activity;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.activity.BaseDrawerActivity;
import com.douban.book.reader.constant.Dimen;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.event.ColorThemeChangedEvent;
import com.douban.book.reader.event.ShelfNewWorksCountChangedEvent;
import com.douban.book.reader.lib.view.BadgeTextView;
import com.douban.book.reader.manager.ShelfManager;
import com.douban.book.reader.theme.Theme;
import com.douban.book.reader.util.AppInfo;
import com.douban.book.reader.util.DebugSwitch;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.util.ViewUtils;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.LongClick;
import org.androidannotations.annotations.ViewById;

@EFragment(2130903104)
public class NavigationDrawerFragment extends BaseFragment {
    @ViewById(2131558621)
    TextView mMenuSearch;
    @ViewById(2131558626)
    TextView mMenuSetting;
    @ViewById(2131558620)
    TextView mMenuStore;
    @ViewById(2131558627)
    TextView mMenuTheme;
    @Bean
    ShelfManager mShelfManager;
    @ViewById(2131558619)
    BadgeTextView mShelfMenuItem;
    @ViewById(2131558628)
    View mStatusBarShadow;
    @ViewById(2131558622)
    View mTestField;
    @ViewById(2131558624)
    View mTestWorks;
    @ViewById(2131558618)
    View mUserSummary;

    @AfterViews
    void init() {
        initThemeGroup();
        this.mShelfMenuItem.setBadgeCenterXY((float) Utils.dp2pixel(49.0f), (float) Utils.dp2pixel(15.0f));
        this.mMenuSetting.setText(RichText.textWithIcon((int) R.drawable.v_setting, (int) R.string.settings));
        if (VERSION.SDK_INT >= 21) {
            ViewUtils.setTopPadding(this.mUserSummary, Dimen.STATUS_BAR_HEIGHT);
        }
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setAsRootOfContentView(view);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof BaseDrawerActivity)) {
            throw new IllegalArgumentException("Must be attached to BaseDrawerActivity");
        }
    }

    public void onResume() {
        super.onResume();
        ViewUtils.showIf(DebugSwitch.on(Key.APP_DEBUG_SHOW_TEST_FIELD), this.mTestField);
        ViewUtils.showIf(DebugSwitch.on(Key.APP_DEBUG_SHOW_TEST_WORKS), this.mTestWorks);
        refreshShelfNewIcon();
    }

    public Fragment getContentFragment() {
        Activity activity = getActivity();
        if (activity instanceof BaseDrawerActivity) {
            return ((BaseDrawerActivity) activity).getContentFragment();
        }
        return null;
    }

    @Click({2131558620, 2131558619, 2131558621, 2131558626, 2131558623, 2131558625})
    void performClick(View clickedView) {
        BaseDrawerActivity activity = (BaseDrawerActivity) getActivity();
        if (activity != null && clickedView != null) {
            activity.performDrawerItemClick(clickedView.getId());
        }
    }

    @LongClick({2131558626})
    void onSettingLongClicked() {
        if (AppInfo.isDebug()) {
            DebugSwitchFragment_.builder().build().showAsActivity((Fragment) this);
        }
    }

    @Click({2131558627})
    void onThemeClicked() {
        Theme.setColorTheme(Theme.isNight() ? 0 : 1);
    }

    public void onEventMainThread(ShelfNewWorksCountChangedEvent event) {
        refreshShelfNewIcon();
    }

    public void onEventMainThread(ColorThemeChangedEvent event) {
        initThemeGroup();
    }

    private void refreshShelfNewIcon() {
        this.mShelfMenuItem.setBadgeVisible(this.mShelfManager.hasNewAddedWorks());
    }

    private void initThemeGroup() {
        this.mMenuTheme.setText(RichText.textWithIcon(Theme.isNight() ? R.drawable.v_model_day : R.drawable.v_model_night, Theme.isNight() ? R.string.btn_theme_day : R.string.btn_theme_night));
    }
}
