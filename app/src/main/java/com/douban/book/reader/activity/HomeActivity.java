package com.douban.book.reader.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.douban.book.reader.R;
import com.douban.book.reader.activity.HomeActivity_.IntentBuilder_;
import com.douban.book.reader.app.App;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.fragment.BaseFragment;
import com.douban.book.reader.fragment.LoginFragment_;
import com.douban.book.reader.fragment.ShelfFragment_;
import com.douban.book.reader.fragment.StoreFragment_;
import com.douban.book.reader.fragment.TestFieldFragment_;
import com.douban.book.reader.manager.ShelfManager;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.manager.VersionManager;
import com.douban.book.reader.util.DebugSwitch;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.ToastBuilder;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

@EActivity
public class HomeActivity extends BaseDrawerActivity {
    @Extra
    Class<? extends BaseFragment> contentClass;
    @Bean
    ShelfManager mShelfManager;
    private boolean mShouldFinishActivityWhenBackPressed;
    @Bean
    VersionManager mVersionManager;

    public HomeActivity() {
        this.mShouldFinishActivityWhenBackPressed = false;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            if (this.contentClass == null) {
                try {
                    this.contentClass = this.mShelfManager.getCount() > 0 ? ShelfFragment_.class : StoreFragment_.class;
                } catch (Exception e) {
                    Logger.e(this.TAG, e);
                    this.contentClass = StoreFragment_.class;
                }
                if (DebugSwitch.on(Key.APP_DEBUG_SHOW_TEST_FIELD)) {
                    this.contentClass = TestFieldFragment_.class;
                }
            }
            Bundle bundle = getIntent().getExtras();
            String referrer = null;
            if (bundle != null) {
                referrer = bundle.getString(PageOpenHelper.KEY_REFERRER);
            }
            showContent(this.contentClass, referrer);
        }
        checkUpdate();
    }

    public void onBackPressed() {
        if (this.mShouldFinishActivityWhenBackPressed) {
            super.onBackPressed();
            return;
        }
        new ToastBuilder().attachTo(this).autoClose(true).message((int) R.string.toast_press_back_to_exit).show();
        this.mShouldFinishActivityWhenBackPressed = true;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                HomeActivity.this.mShouldFinishActivityWhenBackPressed = false;
            }
        }, 5000);
    }

    public static void showContent(PageOpenHelper helper, Class<? extends BaseFragment> cls) {
        Activity activity = helper.getActivity();
        if (activity instanceof HomeActivity) {
            ((HomeActivity) activity).showContent(cls, helper.getReferrer());
            return;
        }
        App.get().finishAllActivities();
        helper.open(((IntentBuilder_) HomeActivity_.intent(App.get()).contentClass(cls).flags(67108864)).get());
    }

    public static void showHomeEnsuringLogin(PageOpenHelper helper) {
        showHomeEnsuringLogin(helper, null);
    }

    public static void showHomeEnsuringLogin(PageOpenHelper helper, Class<? extends BaseFragment> contentClass) {
        Intent intent = HomeActivity_.intent(App.get()).contentClass(contentClass).get();
        if (UserManager.getInstance().hasAccessToken()) {
            helper.open(intent);
        } else {
            LoginFragment_.builder().intentToStartAfterLogin(intent).build().showAsActivity(helper);
        }
    }

    public boolean shouldBeConsideredAsAPage() {
        return false;
    }

    @Background
    void checkUpdate() {
        this.mVersionManager.promptAppUpdateIfNeeded();
    }
}
