package com.douban.book.reader.util;

import android.net.Uri;
import android.net.Uri.Builder;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.PurchaseEvent;
import com.crashlytics.android.answers.ShareEvent;
import com.douban.book.reader.app.App;
import com.douban.book.reader.constant.ShareTo;
import com.douban.book.reader.content.pack.Package;
import com.douban.book.reader.controller.TaskController;
import com.douban.book.reader.entity.Bookmark.Column;
import com.douban.book.reader.entity.UserInfo;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.fragment.BaseShareEditFragment;
import com.douban.book.reader.fragment.WorksListFragment_;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.network.param.JsonRequestParam;
import com.douban.book.reader.network.param.RequestParam;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.HitTypes;
import com.google.analytics.tracking.android.MapBuilder;
import com.tencent.connect.share.QzoneShare;
import com.tencent.open.SocialConstants;
import com.umeng.analytics.a;
import java.math.BigDecimal;
import java.util.Currency;

public class AnalysisUtils {
    public static final String EVENT_LOGGED_IN_WITH_CODE = "logged_in_with_code";
    public static final String EVENT_LOGGED_IN_WITH_OPENID = "logged_in_with_openid";
    public static final String EVENT_LOGGED_IN_WITH_PASSWORD = "logged_in_with_password";
    public static final String EVENT_OPEN_WORKS_IN_READER = "open_works_in_reader";
    public static final String EVENT_SHARE = "share";
    public static final String EVENT_SHARE_RESP_FROM_WX = "share_resp_from_wx";
    public static final String EVENT_SYNC_PROGRESS = "syncProgress";
    public static final String SHARE_CHANNEL_WEIXIN = "weixin";
    public static final String SHARE_METHOD_FRIEND = "friend";
    public static final String SHARE_METHOD_TIMELINE = "moments";
    private static final String TAG;

    /* renamed from: com.douban.book.reader.util.AnalysisUtils.1 */
    static class AnonymousClass1 implements Runnable {
        final /* synthetic */ Uri val$purchasedUri;

        AnonymousClass1(Uri uri) {
            this.val$purchasedUri = uri;
        }

        public void run() {
            if (this.val$purchasedUri == null) {
                return;
            }
            if (ReaderUriUtils.isWorksUri(this.val$purchasedUri) || ReaderUriUtils.isPackageUri(this.val$purchasedUri)) {
                try {
                    int identifyId;
                    boolean isWorksUri = ReaderUriUtils.isWorksUri(this.val$purchasedUri);
                    boolean isChapterUri = ReaderUriUtils.isPackageUri(this.val$purchasedUri);
                    UserInfo userInfo = UserManager.getInstance().getUserInfo();
                    int worksId = ReaderUriUtils.getWorksId(this.val$purchasedUri);
                    int chapterId = ReaderUriUtils.getPackageId(this.val$purchasedUri);
                    Works works = (Works) WorksManager.getInstance().get((Object) Integer.valueOf(worksId));
                    double price = (double) (((float) ReaderUriUtils.getPrice(this.val$purchasedUri)) / 100.0f);
                    if (isWorksUri) {
                        identifyId = worksId;
                    } else {
                        identifyId = chapterId;
                    }
                    String productName = isWorksUri ? works.title : String.format("%s_%s", new Object[]{works.title, Package.get(this.val$purchasedUri).getTitle()});
                    String category = isWorksUri ? BaseShareEditFragment.CONTENT_TYPE_WORKS : BaseShareEditFragment.CONTENT_TYPE_CHAPTER;
                    String transactionId = String.format("%s_%s_%s", new Object[]{Integer.valueOf(userInfo.id), Integer.valueOf(identifyId), Long.valueOf(System.currentTimeMillis())});
                    EasyTracker easyTracker = EasyTracker.getInstance(App.get());
                    easyTracker.send(MapBuilder.createTransaction(transactionId, a.b, Double.valueOf(price), Double.valueOf(0.0d), Double.valueOf(0.0d), "CNY").build());
                    easyTracker.send(MapBuilder.createItem(transactionId, productName, "Bie", category, Double.valueOf(price), Long.valueOf(1), "CNY").build());
                    Analysis.sendEventWithExtra("purchase", category, ((JsonRequestParam) ((JsonRequestParam) ((JsonRequestParam) ((JsonRequestParam) RequestParam.json().append(QzoneShare.SHARE_TO_QQ_TITLE, productName)).append("price", Double.valueOf(price))).append(Column.WORKS_ID, Integer.valueOf(worksId))).appendIf(isChapterUri, "chapter_id", Integer.valueOf(chapterId))).toString());
                    Answers.getInstance().logPurchase(new PurchaseEvent().putItemPrice(BigDecimal.valueOf(price)).putCurrency(Currency.getInstance("CNY")).putItemName(productName).putItemType(category).putItemId(String.valueOf(identifyId)));
                } catch (Exception e) {
                    Logger.e(AnalysisUtils.TAG, e);
                }
            }
        }
    }

    public static class ShareEventBuilder {
        private String mContentDescription;
        private Object mContentId;
        private String mContentTitle;
        private String mContentType;
        private String mContentUri;
        private Object mRelatedWorksId;
        private String mRelatedWorksTitle;
        private ShareTo mShareTo;
        private String mWeixinTransaction;

        public ShareEventBuilder shareTarget(ShareTo shareTo) {
            this.mShareTo = shareTo;
            return this;
        }

        public ShareEventBuilder contentType(String contentType) {
            this.mContentType = contentType;
            return this;
        }

        public ShareEventBuilder contentId(Object contentId) {
            this.mContentId = contentId;
            return this;
        }

        public ShareEventBuilder contentTitle(CharSequence contentTitle) {
            this.mContentTitle = String.valueOf(contentTitle);
            return this;
        }

        public ShareEventBuilder contentDescription(CharSequence contentDescription) {
            this.mContentDescription = String.valueOf(contentDescription);
            return this;
        }

        public ShareEventBuilder contentUri(Uri contentUri) {
            this.mContentUri = String.valueOf(contentUri);
            return this;
        }

        public ShareEventBuilder relatedWorksId(Object worksId) {
            this.mRelatedWorksId = worksId;
            return this;
        }

        public ShareEventBuilder relatedWorksTitle(String worksTitle) {
            this.mRelatedWorksTitle = worksTitle;
            return this;
        }

        public ShareEventBuilder weixinTransaction(String transaction) {
            this.mWeixinTransaction = transaction;
            return this;
        }

        public void send() {
            Analysis.sendEventWithExtra(AnalysisUtils.EVENT_SHARE, this.mContentType, ((JsonRequestParam) ((JsonRequestParam) ((JsonRequestParam) ((JsonRequestParam) ((JsonRequestParam) ((JsonRequestParam) ((JsonRequestParam) RequestParam.json().append(BaseShareEditFragment.KEY_SHARE_TO, this.mShareTo)).append(QzoneShare.SHARE_TO_QQ_TITLE, this.mContentTitle)).append("contentId", this.mContentId)).appendIfNotEmpty(SocialConstants.PARAM_COMMENT, this.mContentDescription)).append(WorksListFragment_.URI_ARG, this.mContentUri)).append("relatedWorksId", this.mRelatedWorksId)).appendIfNotEmpty(HitTypes.TRANSACTION, this.mWeixinTransaction)).getJsonObject());
            ShareEvent event = new ShareEvent().putMethod(String.valueOf(this.mShareTo)).putContentType(this.mContentType);
            if (this.mContentId != null) {
                event.putContentId(StringUtils.toStr(this.mContentId));
            }
            if (StringUtils.isNotEmpty(this.mContentTitle)) {
                event.putContentName(this.mContentTitle);
            }
            if (StringUtils.isNotEmpty(this.mContentDescription)) {
                event.putCustomAttribute("contentDescription", this.mContentDescription);
            }
            if (StringUtils.isNotEmpty(this.mContentUri)) {
                event.putCustomAttribute("contentUri", this.mContentUri);
            }
            if (this.mRelatedWorksId != null) {
                event.putCustomAttribute("relatedWorksId", String.valueOf(this.mRelatedWorksId));
            }
            if (StringUtils.isNotEmpty(this.mRelatedWorksTitle)) {
                event.putCustomAttribute("relatedWorksTitle", this.mRelatedWorksTitle);
            }
            if (StringUtils.isNotEmpty(this.mWeixinTransaction)) {
                event.putCustomAttribute("weixinTransaction", this.mWeixinTransaction);
            }
            Answers.getInstance().logShare(event);
        }
    }

    static {
        TAG = AnalysisUtils.class.getSimpleName();
    }

    public static void sendGetShareRespFromWXEvent(String transaction, long respCode) {
        Analysis.sendEventWithExtra(EVENT_SHARE_RESP_FROM_WX, "default", ((JsonRequestParam) ((JsonRequestParam) RequestParam.json().append("respCode", Long.valueOf(respCode))).append(HitTypes.TRANSACTION, transaction)).getJsonObject());
    }

    public static String buildShareUrl(String url, String shareType, String dcm, String dcs) {
        Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter("dcs", dcs);
        builder.appendQueryParameter("dcm", dcm);
        builder.appendQueryParameter("dct", "android-share");
        builder.appendQueryParameter(SocialConstants.PARAM_TYPE, shareType);
        return builder.build().toString();
    }

    public static void sendPurchaseEvent(Uri purchasedUri) {
        TaskController.run(new AnonymousClass1(purchasedUri));
    }
}
