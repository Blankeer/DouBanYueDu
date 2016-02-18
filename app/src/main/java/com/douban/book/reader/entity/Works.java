package com.douban.book.reader.entity;

import android.text.style.StrikethroughSpan;
import com.douban.book.reader.R;
import com.douban.book.reader.helper.StoreUriHelper;
import com.douban.book.reader.manager.cache.Identifiable;
import com.douban.book.reader.span.ThemedForegroundColorSpan;
import com.douban.book.reader.util.DateUtils;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.util.WorksIdentity;
import com.google.gson.annotations.SerializedName;
import io.realm.internal.Table;
import java.util.Date;
import java.util.List;
import u.aly.dx;

public class Works implements Identifiable {
    @SerializedName("abstract")
    public String abstractText;
    public int agentId;
    public int annotationCount;
    public String author;
    public float averageRating;
    public int bookmarkCount;
    public String categoryText;
    public int columnId;
    public String coverUrl;
    public Gift gift;
    public List<GiftEvent> giftEvents;
    public boolean hasOwned;
    public boolean hasSubscribed;
    public int id;
    public int identities;
    public boolean isOnPre;
    public boolean isSalable;
    public int kind;
    public Date lastPurchaseTime;
    public int price;
    public Promotion promotion;
    public String publisher;
    public int ratingCount;
    public ReleaseInfo releaseInfo;
    public Review review;
    public int rootKind;
    public Schedule schedule;
    public int subscriptionCount;
    public String subtitle;
    public String title;
    public String translator;
    public int underlineCount;
    public int unit;

    public static class Gift {
        public String from;
        public String note;
    }

    public static final class Kind {
        public static final int BOOK = 1;
        public static final int MAGAZINE = 2;
        public static final int ORIGIN_WORKS = 0;
    }

    public static class Promotion {
        public Date beginTime;
        public Date endTime;
        public int event_id;
        public int id;
        public int price;
        public String remark;
    }

    public static class ReleaseInfo {
        public Date date;
        public boolean isNotifiee;
    }

    public static class Schedule {
        public int plan_num;
        public int written_num;
    }

    public Integer getId() {
        return Integer.valueOf(this.id);
    }

    public boolean isGallery() {
        return WorksIdentity.isGallery(this.identities);
    }

    public boolean isFree() {
        return this.isSalable && this.price <= 0;
    }

    public boolean isPromotion() {
        return this.promotion != null && DateUtils.isInRange(this.promotion.beginTime, this.promotion.endTime);
    }

    public boolean isColumnOrSerial() {
        return WorksIdentity.isColumnOrSerial(this.identities);
    }

    public boolean isSerial() {
        return WorksIdentity.isSerial(this.identities);
    }

    public boolean isCompleted() {
        return WorksIdentity.isCompleted(this.identities);
    }

    public boolean isUncompletedColumnOrSerial() {
        return isColumnOrSerial() && !isCompleted();
    }

    public boolean isGift() {
        return this.gift != null;
    }

    public boolean isContentReady() {
        return !this.isOnPre;
    }

    public boolean isNotifiee() {
        return this.releaseInfo != null && this.releaseInfo.isNotifiee;
    }

    public int getNoteCount() {
        return (this.bookmarkCount + this.annotationCount) + this.underlineCount;
    }

    public String getWorksSizeStr() {
        return Res.getString(isGallery() ? R.string.works_size_for_gallery : R.string.works_size_for_non_gallery, Integer.valueOf(this.unit));
    }

    public String getWorksRootKindName() {
        switch (this.rootKind) {
            case dx.a /*0*/:
                return Res.getString(R.string.works_kind_original_works);
            case dx.b /*1*/:
                return Res.getString(R.string.works_kind_book);
            case dx.c /*2*/:
                return Res.getString(R.string.works_kind_magazine);
            default:
                return Table.STRING_DEFAULT_VALUE;
        }
    }

    public int getRootKindIcon() {
        switch (this.rootKind) {
            case dx.a /*0*/:
                return R.drawable.v_self_publishing;
            case dx.b /*1*/:
                return R.drawable.v_ebooks;
            case dx.c /*2*/:
                return R.drawable.v_magazine;
            default:
                return 0;
        }
    }

    public String getWorksScheduleSummary() {
        if (this.schedule == null) {
            return Table.STRING_DEFAULT_VALUE;
        }
        return Res.getString(R.string.works_schedule_summary, Integer.valueOf(this.schedule.written_num), Integer.valueOf(this.schedule.plan_num));
    }

    public String titleWithQuote() {
        return String.format("\u300a%s\u300b", new Object[]{this.title});
    }

    public String getQuotedCopyright() {
        return Res.getString(R.string.works_quoted_copyright, this.title, this.author, StoreUriHelper.works(this.id));
    }

    public String formatOwnedInfo() {
        if (this.lastPurchaseTime != null) {
            StringBuilder builder = new StringBuilder();
            builder.append(DateUtils.formatDate(this.lastPurchaseTime)).append(" ");
            if (!this.hasOwned) {
                builder.append(Res.getString(R.string.works_status_partly_purchased));
            } else if (isGift()) {
                builder.append(Res.getString(R.string.works_status_presented_by, this.gift.from));
            } else if (isFree()) {
                builder.append(Res.getString(R.string.works_status_get));
            } else {
                builder.append(Res.getString(R.string.works_status_purchased));
            }
            return builder.toString();
        } else if (this.hasSubscribed) {
            return Res.getString(R.string.works_status_subscribed);
        } else {
            return Table.STRING_DEFAULT_VALUE;
        }
    }

    public CharSequence formatPrice() {
        return formatPrice(false);
    }

    public CharSequence formatPriceWithColor() {
        return formatPrice(true);
    }

    private CharSequence formatPrice(boolean colorEnabled) {
        ThemedForegroundColorSpan themedForegroundColorSpan = null;
        if (isFree()) {
            return Res.getString(R.string.text_free);
        }
        if (!isPromotion()) {
            return Utils.formatPriceWithSymbol(this.price);
        }
        ThemedForegroundColorSpan themedForegroundColorSpan2;
        RichText richText = new RichText();
        CharSequence formatPrice = Utils.formatPrice(this.price);
        Object[] objArr = new Object[2];
        objArr[0] = new StrikethroughSpan();
        if (colorEnabled) {
            themedForegroundColorSpan2 = new ThemedForegroundColorSpan(R.array.secondary_text_color);
        } else {
            themedForegroundColorSpan2 = null;
        }
        objArr[1] = themedForegroundColorSpan2;
        RichText append = richText.appendWithSpans(formatPrice, objArr).append((CharSequence) " ");
        CharSequence formatPriceWithSymbol = Utils.formatPriceWithSymbol(this.promotion.price);
        Object[] objArr2 = new Object[1];
        if (colorEnabled) {
            themedForegroundColorSpan = new ThemedForegroundColorSpan(R.array.red);
        }
        objArr2[0] = themedForegroundColorSpan;
        return append.appendWithSpans(formatPriceWithSymbol, objArr2);
    }

    public CharSequence formatRatingInfo() {
        if (this.ratingCount <= 0) {
            return Res.getString(R.string.text_no_rating);
        }
        float displayRating = this.averageRating * 2.0f;
        return new RichText().appendWithSpans(String.format("%.1f ", new Object[]{Float.valueOf(displayRating)}), new ThemedForegroundColorSpan(R.array.red)).appendWithSpans(Res.getString(R.string.text_rating_count, Integer.valueOf(this.ratingCount)), new ThemedForegroundColorSpan(R.array.secondary_text_color));
    }

    public CharSequence formatBasicInfo() {
        boolean z;
        RichText richText = new RichText();
        boolean isNotEmpty = StringUtils.isNotEmpty(this.author);
        Object[] objArr = new Object[1];
        objArr[0] = Res.getString(R.string.title_author_with_prefix, this.author);
        richText = richText.appendAsNewLineIf(isNotEmpty, objArr);
        isNotEmpty = StringUtils.isNotEmpty(this.translator);
        objArr = new Object[1];
        objArr[0] = Res.getString(R.string.title_translator, this.translator);
        RichText appendAsNewLineIf = richText.appendAsNewLineIf(isNotEmpty, objArr);
        if (this.rootKind == 1) {
            if (StringUtils.isNotEmpty(this.publisher)) {
                z = true;
                objArr = new Object[1];
                objArr[0] = Res.getString(R.string.title_publisher, this.publisher);
                return appendAsNewLineIf.appendAsNewLineIf(z, objArr);
            }
        }
        z = false;
        objArr = new Object[1];
        objArr[0] = Res.getString(R.string.title_publisher, this.publisher);
        return appendAsNewLineIf.appendAsNewLineIf(z, objArr);
    }
}
