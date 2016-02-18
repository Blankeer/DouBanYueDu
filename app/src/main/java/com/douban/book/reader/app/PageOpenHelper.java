package com.douban.book.reader.app;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.internal.view.SupportMenu;
import android.view.View;
import com.douban.book.reader.activity.GeneralFragmentActivity;
import com.douban.book.reader.fragment.BaseFragment;
import com.douban.book.reader.fragment.BaseFragment.OnActivityResultHandler;
import com.douban.book.reader.fragment.GeneralWebFragment_;
import com.douban.book.reader.fragment.NavigationDrawerFragment;
import com.douban.book.reader.helper.UriOpenHelper;
import com.douban.book.reader.util.Analysis;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.UriUtils;
import com.douban.book.reader.util.ViewUtils;

public final class PageOpenHelper {
    public static final String KEY_REFERRER = "Referrer";
    private static final String TAG;
    private OnActivityResultHandler mActivityResultHandler;
    private int mFlags;
    private final Object mFrom;
    private boolean mPreferInternalWebView;
    private String mReferrer;

    static {
        TAG = PageOpenHelper.class.getSimpleName();
    }

    private PageOpenHelper(Object from) {
        this(from, formatDefaultReferrer(from, null));
    }

    private PageOpenHelper(Object from, String referrer) {
        this.mFrom = from;
        this.mReferrer = referrer;
        if (from instanceof OnActivityResultHandler) {
            onResult((OnActivityResultHandler) from);
        }
    }

    private static String formatDefaultReferrer(Object from, String viewPath) {
        if (from == null) {
            return null;
        }
        Bundle params;
        if (from instanceof NavigationDrawerFragment) {
            Fragment content = ((NavigationDrawerFragment) from).getContentFragment();
            if (content != null) {
                from = content;
                if (StringUtils.isNotEmpty(viewPath)) {
                    viewPath = String.format("/NavigationDrawer/%s", new Object[]{viewPath});
                }
            }
        }
        Bundle params2 = from instanceof Fragment ? ((Fragment) from).getArguments() : from instanceof Activity ? ((Activity) from).getIntent().getExtras() : null;
        if (params2 != null) {
            params = new Bundle(params2);
            params.remove(KEY_REFERRER);
            params.remove(GeneralFragmentActivity.KEY_ADDITIONAL_ARGS_FOR_FRAGMENT);
        } else {
            params = params2;
        }
        return StringUtils.toStr(Analysis.formatReferrer(from.getClass().getSimpleName(), params, viewPath));
    }

    public static PageOpenHelper from(Fragment fragment) {
        if (fragment instanceof DialogFragment) {
            return from(fragment.getActivity());
        }
        return new PageOpenHelper(fragment);
    }

    public static PageOpenHelper from(Activity activity) {
        return new PageOpenHelper(activity);
    }

    public static PageOpenHelper from(View view) {
        String viewPath = ViewUtils.getViewPath(view);
        Object attachedFragment = ViewUtils.getAttachedFragment(view);
        if (attachedFragment == null) {
            attachedFragment = ViewUtils.getAttachedActivity(view);
            if (attachedFragment == null) {
                return fromApp(viewPath);
            }
        }
        return new PageOpenHelper(attachedFragment, formatDefaultReferrer(attachedFragment, viewPath));
    }

    public static PageOpenHelper fromApp(String referrer) {
        return new PageOpenHelper(null);
    }

    @Nullable
    public Activity getActivity() {
        if (this.mFrom instanceof Activity) {
            return (Activity) this.mFrom;
        }
        if (this.mFrom instanceof Fragment) {
            return ((Fragment) this.mFrom).getActivity();
        }
        return null;
    }

    public String getReferrer() {
        return this.mReferrer;
    }

    public PageOpenHelper flags(int flags) {
        this.mFlags = flags;
        return this;
    }

    public PageOpenHelper onResult(OnActivityResultHandler handler) {
        if (this.mFrom instanceof BaseFragment) {
            this.mActivityResultHandler = handler;
            return this;
        }
        throw new IllegalArgumentException("from must be BaseFragment to have `onResult()`");
    }

    public PageOpenHelper preferInternalWebView() {
        this.mPreferInternalWebView = true;
        return this;
    }

    public void open(Intent intent) {
        if (intent != null) {
            int requestCode = 0;
            if (this.mActivityResultHandler != null) {
                requestCode = intent.hashCode() & SupportMenu.USER_MASK;
            }
            if (this.mFlags > 0) {
                intent.addFlags(this.mFlags);
            }
            addReferrerToIntent(intent);
            if (this.mFrom instanceof Fragment) {
                Fragment fragment = this.mFrom;
                if ((fragment instanceof BaseFragment) && this.mActivityResultHandler != null) {
                    ((BaseFragment) fragment).addActivityResultHandler(requestCode, this.mActivityResultHandler);
                }
                fragment.startActivityForResult(intent, requestCode);
            } else if (this.mFrom instanceof Activity) {
                ((Activity) this.mFrom).startActivityForResult(intent, requestCode);
            } else {
                intent.addFlags(268435456);
                App.get().startActivity(intent);
            }
        }
    }

    public boolean open(Uri uri) {
        if (uri == null) {
            return false;
        }
        if (UriOpenHelper.openUri(this, uri)) {
            return true;
        }
        if ((this.mPreferInternalWebView && UriUtils.isPublicUri(uri)) || UriUtils.isInArkDomain(uri)) {
            GeneralWebFragment_.builder().url(uri.toString()).build().showAsActivity(this);
            return true;
        }
        Intent intent = new Intent("android.intent.action.VIEW", uri);
        addReferrerToIntent(intent);
        try {
            open(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            Logger.ec(TAG, e);
            return false;
        }
    }

    public boolean open(String uri) {
        return open(Uri.parse(uri));
    }

    private void addReferrerToIntent(Intent intent) {
        if (StringUtils.isNotEmpty(this.mReferrer)) {
            GeneralFragmentActivity.putAdditionalArgs(intent, KEY_REFERRER, this.mReferrer);
            intent.putExtra(KEY_REFERRER, this.mReferrer);
        }
    }
}
