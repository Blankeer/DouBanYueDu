package com.douban.book.reader.helper;

import android.net.Uri;
import com.alipay.sdk.protocol.h;
import com.douban.book.reader.activity.HomeActivity;
import com.douban.book.reader.activity.HomeActivity_;
import com.douban.book.reader.activity.WelcomeActivity_;
import com.douban.book.reader.activity.WelcomeActivity_.IntentBuilder_;
import com.douban.book.reader.app.App;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.entity.Bookmark.Column;
import com.douban.book.reader.fragment.AccountBalanceFragment_;
import com.douban.book.reader.fragment.BaseShareEditFragment;
import com.douban.book.reader.fragment.ColumnChapterReaderFragment_;
import com.douban.book.reader.fragment.FeedFragment_;
import com.douban.book.reader.fragment.GiftDetailFragment_;
import com.douban.book.reader.fragment.GiftFragment_;
import com.douban.book.reader.fragment.GiftPackCreateFragment_;
import com.douban.book.reader.fragment.GiftPackDetailFragment_;
import com.douban.book.reader.fragment.IntroPageFragment_;
import com.douban.book.reader.fragment.NoteDetailFragment_;
import com.douban.book.reader.fragment.PurchaseFragment_;
import com.douban.book.reader.fragment.RedeemFragment_;
import com.douban.book.reader.fragment.ReviewDetailFragment_;
import com.douban.book.reader.fragment.StoreFragment_;
import com.douban.book.reader.fragment.StoreTabFragment_;
import com.douban.book.reader.fragment.WorksAgentFragment_;
import com.douban.book.reader.fragment.WorksKindFragment_;
import com.douban.book.reader.fragment.WorksListFragment_;
import com.douban.book.reader.fragment.WorksProfileFragment_;
import com.douban.book.reader.fragment.share.ShareGiftEditFragment_;
import com.douban.book.reader.fragment.share.ShareUrlEditFragment_;
import com.douban.book.reader.manager.PageManager.Page;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.UriUtils;
import com.igexin.download.Downloads;
import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache;
import com.sina.weibo.sdk.register.mobile.SelectCountryActivity;
import com.sina.weibo.sdk.utils.AidTask;
import java.util.UUID;
import se.emilsjolander.stickylistheaders.R;
import u.aly.ci;
import u.aly.dj;
import u.aly.dx;

public class UriOpenHelper {
    private static final String TAG;

    static {
        TAG = UriOpenHelper.class.getSimpleName();
    }

    private static int getIntId(Uri uri) {
        String firstPath = UriUtils.getFirstPathSegment(uri);
        int id = UriUtils.getIntQueryParameter(uri, WorksListUri.KEY_ID);
        if (id <= 0) {
            return UriUtils.getIntPathSegmentNextTo(uri, firstPath);
        }
        return id;
    }

    private static int getIntId(Uri uri, String typeName) {
        int id = UriUtils.getIntQueryParameter(uri, typeName);
        if (id <= 0) {
            return UriUtils.getIntPathSegmentNextTo(uri, typeName);
        }
        return id;
    }

    private static String getId(Uri uri) {
        String firstPath = UriUtils.getFirstPathSegment(uri);
        String id = uri.getQueryParameter(WorksListUri.KEY_ID);
        if (StringUtils.isEmpty(id)) {
            return UriUtils.getPathSegmentNextTo(uri, firstPath);
        }
        return id;
    }

    private static UUID getUuid(Uri uri) {
        String firstPath = UriUtils.getFirstPathSegment(uri);
        String uuid = uri.getQueryParameter(ShareGiftEditFragment_.UUID_ARG);
        if (StringUtils.isEmpty(uuid)) {
            uuid = UriUtils.getPathSegmentNextTo(uri, firstPath);
        }
        return UUID.fromString(uuid);
    }

    private static boolean getBooleanParam(Uri uri, String paramName) {
        return Boolean.valueOf(uri.getQueryParameter(paramName)).booleanValue();
    }

    private static String getStrParam(Uri uri, String paramName) {
        return uri.getQueryParameter(paramName);
    }

    private static int getIntParam(Uri uri, String paramName) {
        return StringUtils.toInt(uri.getQueryParameter(paramName));
    }

    private static String getName(Uri uri) {
        String firstPath = UriUtils.getFirstPathSegment(uri);
        String id = uri.getQueryParameter(SelectCountryActivity.EXTRA_COUNTRY_NAME);
        if (StringUtils.isEmpty(id)) {
            return UriUtils.getPathSegmentNextTo(uri, firstPath);
        }
        return id;
    }

    public static boolean openUri(PageOpenHelper helper, Uri uri) {
        switch (AppUri.getType(uri)) {
            case dx.b /*1*/:
                IntroPageFragment_.builder().page(Page.COLUMN).build().showAsActivity(helper);
                break;
            case dx.c /*2*/:
                IntroPageFragment_.builder().page(Page.HERMES).build().showAsActivity(helper);
                break;
            case dx.d /*3*/:
                FeedFragment_.builder().build().showAsActivity(helper);
                break;
            case dx.e /*4*/:
                RedeemFragment_.builder().build().showAsActivity(helper);
                break;
            case dj.f /*5*/:
                AccountBalanceFragment_.builder().build().showAsActivity(helper);
                break;
            case ci.g /*6*/:
                PurchaseFragment_.builder().worksId(getIntId(uri, BaseShareEditFragment.CONTENT_TYPE_WORKS)).chapterId(getIntId(uri, BaseShareEditFragment.CONTENT_TYPE_CHAPTER)).giftPackId(getIntId(uri, BaseShareEditFragment.CONTENT_TYPE_GIFT_PACK)).promptDownload(getBooleanParam(uri, PurchaseFragment_.PROMPT_DOWNLOAD_ARG)).build().showAsActivity(helper);
                break;
            case h.i /*10*/:
                StoreTabFragment_.builder().tabName(getName(uri)).build().showAsActivity(helper);
                break;
            case R.styleable.StickyListHeadersListView_android_stackFromBottom /*11*/:
                HomeActivity.showHomeEnsuringLogin(helper, StoreFragment_.class);
                break;
            case LruDiskCache.DEFAULT_COMPRESS_QUALITY /*100*/:
                WorksProfileFragment_.builder().worksId(getIntId(uri)).build().showAsActivity(helper);
                break;
            case Header.FLOAT /*101*/:
            case Header.DOUBLE_M1 /*102*/:
                WorksProfileFragment_.builder().legacyColumnId(getIntId(uri)).build().showAsActivity(helper);
                break;
            case Header.DOUBLE_0 /*103*/:
                ReviewDetailFragment_.builder().reviewId(getIntId(uri)).build().showAsActivity(helper);
                break;
            case Header.DOUBLE_1 /*104*/:
                ((IntentBuilder_) WelcomeActivity_.intent(App.get()).legacyReviewId(getIntId(uri)).flags(268435456)).start();
                break;
            case Header.DOUBLE_255 /*105*/:
                NoteDetailFragment_.builder().idOrUuid(getId(uri)).build().showAsActivity(helper);
                break;
            case Header.DOUBLE_SHORT /*106*/:
                WorksKindFragment_.builder().defaultTabTitle(getName(uri)).build().showAsActivity(helper);
                break;
            case Downloads.STATUS_SUCCESS /*200*/:
                WorksListFragment_.builder().uri(WorksListUri.fromWebUri(uri)).build().showAsActivity(helper);
                break;
            case AppUri.PROVIDER /*201*/:
                WorksAgentFragment_.builder().agentId(getIntId(uri)).build().showAsActivity(helper);
                break;
            case AppUri.READER /*300*/:
                WorksProfileFragment_.builder().worksId(getIntId(uri, "ebook")).build().showAsActivity(helper);
                break;
            case AppUri.READER_COLUMN /*301*/:
                WorksProfileFragment_.builder().legacyColumnId(getIntId(uri, "column")).build().showAsActivity(helper);
                break;
            case AppUri.READER_COLUMN_CHAPTER /*302*/:
                ColumnChapterReaderFragment_.builder().worksId(getIntId(uri, BaseShareEditFragment.CONTENT_TYPE_WORKS)).legacyColumnId(getIntId(uri, "column")).chapterId(getIntId(uri, BaseShareEditFragment.CONTENT_TYPE_CHAPTER)).build().showAsActivity(helper);
                break;
            case Downloads.STATUS_BAD_REQUEST /*400*/:
                GiftPackCreateFragment_.builder().worksId(getIntParam(uri, Column.WORKS_ID)).eventId(getIntParam(uri, "event_id")).build().showAsActivity(helper);
                break;
            case AppUri.GIFT_PACK /*401*/:
                int packId = getIntId(uri);
                if (packId > 0) {
                    GiftPackDetailFragment_.builder().packId(packId).build().showAsActivity(helper);
                    break;
                }
                String hashCode = getStrParam(uri, SelectCountryActivity.EXTRA_COUNTRY_CODE);
                if (StringUtils.isEmpty(hashCode)) {
                    hashCode = UriUtils.getPathSegmentNextTo(uri, "pack");
                }
                if (StringUtils.isNotEmpty(hashCode)) {
                    GiftPackDetailFragment_.builder().hashCode(hashCode).build().showAsActivity(helper);
                    break;
                }
                return false;
            case AppUri.GIFT /*402*/:
                GiftDetailFragment_.builder().uuid(getUuid(uri)).build().showAsActivity(helper);
                break;
            case AppUri.GIFT_LIST /*403*/:
                GiftFragment_.builder().build().showAsActivity(helper);
                break;
            case AppUri.OPEN_URL /*1000*/:
                String uriToOpen = uri.getQueryParameter(ShareUrlEditFragment_.URL_ARG);
                if (StringUtils.isEmpty(uriToOpen)) {
                    return false;
                }
                return helper.preferInternalWebView().open(uriToOpen);
            case AidTask.WHAT_LOAD_AID_SUC /*1001*/:
                if (!StringUtils.isNotEmpty(uri.getQueryParameter("ref"))) {
                    ((HomeActivity_.IntentBuilder_) HomeActivity_.intent(App.get()).flags(268435456)).start();
                    break;
                }
                return helper.preferInternalWebView().open(Uri.parse(uri.getQueryParameter("ref")));
            default:
                return false;
        }
        return true;
    }
}
