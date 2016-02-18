package com.douban.book.reader.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.constant.Char;
import com.douban.book.reader.constant.ShareTo;
import com.douban.book.reader.controller.TaskController;
import com.douban.book.reader.fragment.interceptor.SelectShareTargetInterceptor;
import com.douban.book.reader.util.AnalysisUtils.ShareEventBuilder;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.PackageUtils;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.util.WXUtils;
import com.douban.book.reader.util.WXUtils.MessageBuilder;
import com.tencent.mm.sdk.constants.ConstantsAPI.WXApp;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import io.realm.internal.Table;
import u.aly.dx;

public abstract class BaseShareEditFragment extends BaseEditFragment {
    public static final String CONTENT_TYPE_CHAPTER = "chapter";
    public static final String CONTENT_TYPE_GIFT = "gift";
    public static final String CONTENT_TYPE_GIFT_PACK = "gift_pack";
    public static final String CONTENT_TYPE_ILLUS = "illus";
    public static final String CONTENT_TYPE_NOTE = "note";
    public static final String CONTENT_TYPE_REVIEW = "review";
    public static final String CONTENT_TYPE_SELECTION = "selection";
    public static final String CONTENT_TYPE_URL = "url";
    public static final String CONTENT_TYPE_WORKS = "works";
    public static final String KEY_SHARE_TO = "shareTo";
    private ShareTo mShareTo;

    /* renamed from: com.douban.book.reader.fragment.BaseShareEditFragment.1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ int val$weixinScene;

        AnonymousClass1(int i) {
            this.val$weixinScene = i;
        }

        public void run() {
            try {
                BaseShareEditFragment.this.showLoadingDialog();
                BaseShareEditFragment.this.getBaseShareEventBuilder().weixinTransaction(WXUtils.shareToWeixin(this.val$weixinScene, BaseShareEditFragment.this.completeWeixinShareContent(new MessageBuilder().title(BaseShareEditFragment.this.getComplicatedContentTitle()).description(BaseShareEditFragment.this.getContentDescription()).url(BaseShareEditFragment.this.getContentUri()).thumbnail(BaseShareEditFragment.this.getContentThumbnailUri())))).send();
            } catch (Throwable e) {
                ToastUtils.showToast(e, (int) R.string.general_load_failed);
                Logger.ec(BaseShareEditFragment.this.TAG, e);
                BaseShareEditFragment.this.finishSkippingCheck();
            } finally {
                BaseShareEditFragment.this.dismissLoadingDialog();
            }
        }
    }

    /* renamed from: com.douban.book.reader.fragment.BaseShareEditFragment.3 */
    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$com$douban$book$reader$constant$ShareTo;

        static {
            $SwitchMap$com$douban$book$reader$constant$ShareTo = new int[ShareTo.values().length];
            try {
                $SwitchMap$com$douban$book$reader$constant$ShareTo[ShareTo.WEIXIN.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$douban$book$reader$constant$ShareTo[ShareTo.MOMENTS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$douban$book$reader$constant$ShareTo[ShareTo.OTHER_APPS.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$douban$book$reader$constant$ShareTo[ShareTo.WEIBO.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$douban$book$reader$constant$ShareTo[ShareTo.DOUBAN.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    protected abstract String getContentType();

    protected BaseShareEditFragment() {
        setShowInterceptor(new SelectShareTargetInterceptor());
        hasTextLimit(true);
        emptyPostAllowed(true);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.mShareTo = (ShareTo) arguments.getSerializable(KEY_SHARE_TO);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (shouldHideContentView()) {
            hideEditor();
            showLoadingDialogImmediately();
        }
        return view;
    }

    public boolean shouldHideContentView() {
        return this.mShareTo == ShareTo.OTHER_APPS || this.mShareTo == ShareTo.WEIXIN || this.mShareTo == ShareTo.MOMENTS;
    }

    protected ShareTo getShareTo() {
        return this.mShareTo;
    }

    protected Object getContentId() {
        return null;
    }

    protected String getContentTitle() {
        return Table.STRING_DEFAULT_VALUE;
    }

    protected String getComplicatedContentTitle() {
        return getContentTitle();
    }

    protected Object getRelatedWorksId() {
        return null;
    }

    protected String getRelatedWorksTitle() {
        return Table.STRING_DEFAULT_VALUE;
    }

    protected String getContentDescription() {
        return Table.STRING_DEFAULT_VALUE;
    }

    protected String getContentThumbnailUri() {
        return null;
    }

    protected Uri getContentUri() {
        return null;
    }

    protected WXMediaMessage completeWeixinShareContent(MessageBuilder builder) {
        return builder.build();
    }

    protected CharSequence getPlainTextShareContent() {
        boolean z;
        RichText appendAsNewLineIfNotEmpty = new RichText().appendAsNewLineIfNotEmpty(getContentTitle());
        if (getContentUri() != null) {
            z = true;
        } else {
            z = false;
        }
        return appendAsNewLineIfNotEmpty.appendAsNewLineIf(z, getContentUri(), Character.valueOf(Char.SPACE), Res.getString(R.string.share_object_to_other_app)).appendAsNewLineIfNotEmpty(getContentDescription());
    }

    protected CharSequence getWeiboDefaultShareText() {
        return Table.STRING_DEFAULT_VALUE;
    }

    protected void onDataReady() {
        super.onDataReady();
        switch (AnonymousClass3.$SwitchMap$com$douban$book$reader$constant$ShareTo[this.mShareTo.ordinal()]) {
            case dx.b /*1*/:
                performShareToWeixin(0);
                return;
            case dx.c /*2*/:
                performShareToWeixin(1);
                return;
            case dx.d /*3*/:
                performShareToOtherApp();
                return;
            case dx.e /*4*/:
                setContent(getWeiboDefaultShareText());
                break;
        }
        setupViews();
    }

    public void onStop() {
        super.onStop();
        if (shouldHideContentView()) {
            finish();
        }
    }

    public void onResume() {
        super.onResume();
        if (isVisible() && shouldHideContentView()) {
            finish();
        }
    }

    protected void setupViews() {
    }

    protected void performShareToWeixin(int weixinScene) {
        if (!PackageUtils.isInstalled(WXApp.WXAPP_PACKAGE_NAME)) {
            ToastUtils.showToast(Res.getString(R.string.toast_error_weixin_not_installed));
        } else if (weixinScene != 1 || WXUtils.isTimeLineSupported()) {
            TaskController.run(new AnonymousClass1(weixinScene));
        } else {
            ToastUtils.showToast(Res.getString(R.string.toast_error_not_support_share_to_timeline));
        }
    }

    public void performShareToOtherApp() {
        showLoadingDialog();
        TaskController.run(new Runnable() {
            public void run() {
                try {
                    BaseShareEditFragment.this.initData();
                    App.get().runOnUiThread(new Runnable() {
                        public void run() {
                            CharSequence content = BaseShareEditFragment.this.getPlainTextShareContent();
                            Intent shareIntent = new Intent("android.intent.action.SEND");
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra("android.intent.extra.TEXT", String.valueOf(content));
                            shareIntent.addFlags(268435456);
                            PageOpenHelper.fromApp(null).open(shareIntent);
                            BaseShareEditFragment.this.getBaseShareEventBuilder().send();
                        }
                    });
                } catch (Throwable e) {
                    ToastUtils.showToast(e);
                    Logger.ec(BaseShareEditFragment.this.TAG, e);
                    BaseShareEditFragment.this.finishSkippingCheck();
                } finally {
                    BaseShareEditFragment.this.dismissLoadingDialog();
                }
            }
        });
    }

    protected void onPostSucceed() {
        super.onPostSucceed();
        getBaseShareEventBuilder().send();
    }

    protected CharSequence getSucceedToastMessage() {
        return Res.getString(R.string.toast_share_succeed);
    }

    private ShareEventBuilder getBaseShareEventBuilder() {
        return new ShareEventBuilder().shareTarget(this.mShareTo).contentType(getContentType()).contentId(getContentId()).contentTitle(getContentTitle()).contentDescription(StringUtils.truncate(getContentDescription(), 100)).contentUri(getContentUri()).relatedWorksId(getRelatedWorksId()).relatedWorksTitle(getRelatedWorksTitle());
    }
}
