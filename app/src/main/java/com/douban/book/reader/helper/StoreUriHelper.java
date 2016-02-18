package com.douban.book.reader.helper;

import android.net.Uri;
import android.net.Uri.Builder;
import com.douban.book.reader.constant.Constants;
import com.douban.book.reader.entity.Annotation;
import com.douban.book.reader.fragment.BaseShareEditFragment;
import com.douban.book.reader.fragment.ReviewEditFragment_;
import com.douban.book.reader.manager.UserManager;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.util.UUID;

public class StoreUriHelper {
    private static Builder arkUriBuilder() {
        return new Builder().scheme(Constants.API_SCHEME).authority(AppUri.AUTHORITY_WEB);
    }

    public static Uri works(int worksId) {
        return arkUriBuilder().appendPath("ebook").appendPath(String.valueOf(worksId)).build();
    }

    public static Uri review(int reviewId) {
        return arkUriBuilder().appendPath(ReviewEditFragment_.RATING_ARG).appendPath(String.valueOf(reviewId)).build();
    }

    public static Uri note(int noteId) {
        return arkUriBuilder().appendPath(Annotation.TABLE_NAME).appendPath(String.valueOf(noteId)).build();
    }

    public static Uri columnChapterReader(int columnId, int chapterId) {
        return arkUriBuilder().appendPath(AppUri.PATH_READER).appendPath("column").appendPath(String.valueOf(columnId)).appendPath(BaseShareEditFragment.CONTENT_TYPE_CHAPTER).appendPath(String.valueOf(chapterId)).build();
    }

    public static Uri changelog() {
        return arkUriBuilder().appendPath(SettingsJsonConstants.APP_KEY).appendPath("changelog").build();
    }

    public static Uri giftPack(String code) {
        return arkUriBuilder().appendPath(BaseShareEditFragment.CONTENT_TYPE_GIFT).appendPath("pack").appendPath(code).build();
    }

    public static Uri gift(UUID uuid) {
        return arkUriBuilder().appendPath(BaseShareEditFragment.CONTENT_TYPE_GIFT).appendPath(String.valueOf(uuid)).build();
    }

    private static Builder doubanUriBuilder() {
        return new Builder().scheme("http").authority(Constants.DOUBAN_HOST);
    }

    public static Uri userProfile(int userId) {
        return doubanUriBuilder().appendPath("people").appendPath(String.valueOf(userId)).build();
    }

    public static Uri myProfile() {
        return userProfile(UserManager.getInstance().getUserId());
    }
}
