package com.douban.book.reader.manager;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.network.client.AccountClient;
import com.douban.book.reader.network.param.RequestParam;
import com.douban.book.reader.network.param.RequestParam.Type;
import com.douban.book.reader.util.BitmapUtils;
import com.sina.weibo.sdk.register.mobile.SelectCountryActivity;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EBean.Scope;

@EBean(scope = Scope.Singleton)
public class AccountManager {
    public void setNickname(CharSequence nickname) throws DataLoadException {
        try {
            new AccountClient("account/update_profile").post(baseParam().append(SelectCountryActivity.EXTRA_COUNTRY_NAME, nickname));
        } catch (Throwable e) {
            throw new DataLoadException(e);
        }
    }

    public void uploadAvatar(Bitmap bitmap) throws DataLoadException {
        uploadAvatar(BitmapUtils.getCompressedBits(BitmapUtils.scaleToWidth(bitmap, SettingsJsonConstants.ANALYTICS_FLUSH_INTERVAL_SECS_DEFAULT), CompressFormat.JPEG, 100));
    }

    public void uploadAvatar(byte[] bytes) throws DataLoadException {
        try {
            new AccountClient("account/update_avatar").post(baseParam(Type.MULTI_PART).append("avatar", bytes));
        } catch (Throwable e) {
            throw new DataLoadException(e);
        }
    }

    private static RequestParam<?> baseParam() {
        return baseParam(Type.FORM);
    }

    private static RequestParam<?> baseParam(Type type) {
        return RequestParam.ofType(type);
    }
}
