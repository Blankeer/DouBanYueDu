package com.douban.book.reader.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.crashlytics.android.Crashlytics;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.constant.Constants;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.entity.Manifest.GalleryColorTheme;
import com.douban.book.reader.fragment.ReviewDetailFragment_;
import com.douban.book.reader.fragment.WalkThroughFragment;
import com.douban.book.reader.fragment.WalkThroughFragment_;
import com.douban.book.reader.manager.FontScaleManager_;
import com.douban.book.reader.manager.ReviewManager;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.network.param.JsonRequestParam;
import com.douban.book.reader.network.param.RequestParam;
import com.douban.book.reader.theme.Theme;
import com.douban.book.reader.util.Analysis;
import com.douban.book.reader.util.AppInfo;
import com.douban.book.reader.util.ExceptionUtils;
import com.douban.book.reader.util.ImageLoaderUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Pref;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.ViewUtils;
import java.net.UnknownHostException;
import java.util.Calendar;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

@EActivity(2130903069)
public class WelcomeActivity extends BaseActivity {
    private static final String PATH_PREFIX_RATING = "rating";
    private static final String PATH_PREFIX_REVIEW = "review";
    private static String[] PRE_RELEASE_MARKETS = null;
    private static long SHOW_MARKET_LOGO_BEFORE = 0;
    private static final String TAG;
    private static final long TIMER_DELAY = 500;
    @Extra
    Intent forwardIntent;
    @Extra
    int legacyReviewId;
    @ViewById(2131558519)
    Button mBtnRetry;
    private boolean mIsFirstLaunch;
    @ViewById(2131558516)
    View mLayoutLoading;
    @ViewById(2131558517)
    View mLayoutRetry;
    @ViewById(2131558520)
    ImageView mMarketLogo;
    @ViewById(2131558518)
    TextView mMessage;
    @Bean
    ReviewManager mReviewManager;
    @Bean
    UserManager mUserManager;

    public WelcomeActivity() {
        this.mIsFirstLaunch = false;
    }

    static {
        TAG = WelcomeActivity.class.getSimpleName();
        PRE_RELEASE_MARKETS = new String[]{"91_market", "Baidu_Market", "Hiapk_Market"};
        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, 1, 8, 0, 0, 0);
        SHOW_MARKET_LOGO_BEFORE = calendar.getTimeInMillis();
    }

    @AfterViews
    void init() {
        checkAndShowMarketLogo();
        if (this.mApp.isExternalStorageAvailable()) {
            this.mIsFirstLaunch = Pref.ofApp().getBoolean(Key.APP_FIRST_LAUNCH, true);
            if (this.mIsFirstLaunch) {
                Pref.ofApp().set(Key.APP_FIRST_LAUNCH, Boolean.valueOf(false));
            }
            if ((this.forwardIntent == null && getRedirectUri() == null && this.legacyReviewId <= 0) || this.mUserManager.hasAccessToken()) {
                onCheckLoggedInFinish();
                return;
            } else {
                anonymousLogin();
                return;
            }
        }
        ViewUtils.visible(this.mLayoutRetry);
        ViewUtils.gone(this.mLayoutLoading, this.mBtnRetry);
        this.mMessage.setText(R.string.message_sd_card_unavailable);
    }

    public void onResume() {
        super.onResume();
        Analysis.sendEventWithExtra("ark_launch", "pref", ((JsonRequestParam) ((JsonRequestParam) ((JsonRequestParam) ((JsonRequestParam) ((JsonRequestParam) ((JsonRequestParam) ((JsonRequestParam) ((JsonRequestParam) RequestParam.json().append(Key.APP_FIRST_INSTALLED_VERSION, Integer.valueOf(Pref.ofApp().getInt(Key.APP_FIRST_INSTALLED_VERSION, 0)))).append("user_type", UserManager.getInstance().getUserTypeName())).append("enable_push", Boolean.valueOf(Pref.ofApp().getBoolean(Key.SETTING_ENABLE_PUSH_SERVICE)))).append(Key.SETTING_THEME, Theme.isNight() ? GalleryColorTheme.NIGHT : GalleryColorTheme.DAY)).append("use_system_brightness", Boolean.valueOf(Pref.ofApp().getBoolean(Key.APP_USE_SYSTEM_BRIGHTNESS)))).append("font_scale", Integer.valueOf(FontScaleManager_.getInstance_(App.get()).getScale()))).append("page_turn_with_volume_key", Boolean.valueOf(Pref.ofApp().getBoolean(Key.SETTING_PAGETURN_WITH_VOLUME)))).append("prevent_screen_save_while_reading", Boolean.valueOf(Pref.ofApp().getBoolean(Key.SETTING_PREVENET_READING_SCREENSAVE)))).getJsonObject());
    }

    @Click({2131558519})
    void onRetryButtonClicked() {
        anonymousLogin();
    }

    @Background
    void anonymousLogin() {
        try {
            startLoading();
            this.mUserManager.anonymousLogin();
            onCheckLoggedInFinish();
            stopLoading();
        } catch (Exception e) {
            onAnonymousLoginError(e);
            if (!ExceptionUtils.isCausedBy(e, UnknownHostException.class)) {
                Crashlytics.logException(e);
            }
            stopLoading();
        } catch (Throwable th) {
            stopLoading();
        }
    }

    @UiThread
    void redirectToNextPage() {
        Intent intent = getIntent();
        if (intent == null || intent.getData() == null) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    WelcomeActivity.this.handleIntent();
                }
            }, TIMER_DELAY);
            return;
        }
        handleIntent();
        finish();
    }

    private void openHomeActivity() {
        HomeActivity.showHomeEnsuringLogin(PageOpenHelper.from((Activity) this));
    }

    private boolean hasRedirectIntent() {
        return (this.forwardIntent == null && (getIntent() == null || getIntent().getData() == null)) ? false : true;
    }

    @UiThread
    void onCheckLoggedInFinish() {
        boolean walkThroughShown = WalkThroughFragment.hasShown();
        if (hasRedirectIntent() || walkThroughShown) {
            redirectToNextPage();
        } else {
            showWalkThrough();
        }
    }

    @UiThread
    void startLoading() {
        ViewUtils.gone(this.mLayoutRetry);
        ViewUtils.visible(this.mLayoutLoading);
    }

    @UiThread
    void stopLoading() {
        ViewUtils.gone(this.mLayoutLoading);
    }

    @UiThread
    void onAnonymousLoginError(Throwable e) {
        ViewUtils.visible(this.mLayoutRetry, this.mMessage);
        this.mMessage.setText(ExceptionUtils.getHumanReadableMessage(e, (int) R.string.message_anonymous_login_failed));
    }

    private void showWalkThrough() {
        WalkThroughFragment_.builder().build().setDrawerEnabled(false).setActionBarVisible(false).showAsActivity((BaseActivity) this);
    }

    private void checkAndShowMarketLogo() {
        String channelName = AppInfo.getChannelName();
        if (StringUtils.inList(channelName, PRE_RELEASE_MARKETS) && System.currentTimeMillis() <= SHOW_MARKET_LOGO_BEFORE) {
            Uri logoUri = new Builder().scheme("assets").authority("channel_variants").appendPath(channelName).appendPath("logo.png").build();
            if (logoUri != null) {
                ImageLoaderUtils.displayImage(logoUri.toString(), this.mMarketLogo);
                ViewUtils.visible(this.mMarketLogo);
            }
        }
    }

    private void handleIntent() {
        if (getIntent() != null) {
            if (this.forwardIntent != null) {
                startActivity(this.forwardIntent);
                return;
            } else if (this.legacyReviewId > 0) {
                openReviewDetailPage(this.legacyReviewId);
                return;
            } else if (PageOpenHelper.from((Activity) this).open(getRedirectUri())) {
                return;
            }
        }
        openHomeActivity();
    }

    @Nullable
    private Uri getRedirectUri() {
        Intent intent = getIntent();
        if (intent != null) {
            CharSequence action = intent.getAction();
            if (StringUtils.equals(Constants.ACTION_DEFAULT, action) || StringUtils.equals(Constants.ACTION_OPEN_BOOK, action) || StringUtils.equals((CharSequence) "android.intent.action.VIEW", action) || intent.hasCategory("android.intent.category.BROWSABLE")) {
                return intent.getData();
            }
        }
        return null;
    }

    @Background
    void openReviewDetailPage(int legacyReviewId) {
        try {
            ReviewDetailFragment_.builder().reviewId(this.mReviewManager.getByLegacyId(legacyReviewId).id).build().showAsActivity((BaseActivity) this);
        } catch (DataLoadException e) {
            openHomeActivity();
            Logger.e(TAG, e);
        }
    }
}
