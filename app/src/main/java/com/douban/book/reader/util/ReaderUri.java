package com.douban.book.reader.util;

import android.net.Uri;
import android.net.Uri.Builder;
import com.douban.book.reader.content.Book.ImageSize;
import com.douban.book.reader.fragment.BaseShareEditFragment;

public class ReaderUri {
    public static final String AUTHORITY = "data";
    public static final String SCHEME = "ark";

    private static Uri baseUri() {
        return new Builder().scheme(SCHEME).authority(AUTHORITY).build();
    }

    public static Uri works(int worksId) {
        return baseUri().buildUpon().appendPath(BaseShareEditFragment.CONTENT_TYPE_WORKS).appendPath(StringUtils.toStr(Integer.valueOf(worksId))).build();
    }

    public static Uri cover(int worksId) {
        return works(worksId).buildUpon().appendPath("cover").build();
    }

    public static Uri pack(int worksId, int packageId) {
        return works(worksId).buildUpon().appendPath("package").appendPath(StringUtils.toStr(Integer.valueOf(packageId))).build();
    }

    public static Uri illus(int worksId, int packageId, int seq, ImageSize size) {
        return pack(worksId, packageId).buildUpon().appendPath(BaseShareEditFragment.CONTENT_TYPE_ILLUS).appendPath(StringUtils.toStr(Integer.valueOf(seq))).appendPath(size == ImageSize.LARGE ? "large" : "normal").build();
    }

    public static Uri giftPack(int giftPackId) {
        return baseUri().buildUpon().appendPath(BaseShareEditFragment.CONTENT_TYPE_GIFT_PACK).appendPath(StringUtils.toStr(Integer.valueOf(giftPackId))).build();
    }
}
