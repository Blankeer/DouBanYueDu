package com.douban.book.reader.manager;

import android.net.Uri;
import com.douban.book.reader.R;
import com.douban.book.reader.alipay.AlipayException;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.entity.Bookmark.Column;
import com.douban.book.reader.entity.OrderInfo;
import com.douban.book.reader.entity.VerifyResult;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.network.param.JsonRequestParam;
import com.douban.book.reader.network.param.RequestParam;
import com.douban.book.reader.util.DebugSwitch;
import com.douban.book.reader.util.ReaderUriUtils;
import com.douban.book.reader.util.Res;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EBean.Scope;

@EBean(scope = Scope.Singleton)
public class AlipayManager extends BaseManager<OrderInfo> {
    public AlipayManager() {
        super("alipay", OrderInfo.class);
    }

    public OrderInfo deposit(int amount) throws DataLoadException {
        RequestParam json = RequestParam.json();
        String str = "amount";
        if (DebugSwitch.on(Key.APP_DEBUG_MONEY_SAVING_MODE)) {
            amount = 1;
        }
        return (OrderInfo) getSubManager("deposit", OrderInfo.class).post(json.append(str, Integer.valueOf(amount)));
    }

    public OrderInfo purchase(Uri itemToPurchase, boolean secretly) throws DataLoadException {
        boolean z = true;
        RequestParam param = RequestParam.json().append("is_private", Boolean.valueOf(secretly));
        if (itemToPurchase != null) {
            boolean z2;
            int giftPackId = ReaderUriUtils.getGiftPackId(itemToPurchase);
            int worksId = ReaderUriUtils.getWorksId(itemToPurchase);
            int packageId = ReaderUriUtils.getPackageId(itemToPurchase);
            if (giftPackId > 0) {
                z2 = true;
            } else {
                z2 = false;
            }
            param.appendIf(z2, "gift_pack_id", Integer.valueOf(giftPackId));
            if (worksId > 0) {
                z2 = true;
            } else {
                z2 = false;
            }
            param.appendIf(z2, Column.WORKS_ID, Integer.valueOf(worksId));
            if (packageId <= 0) {
                z = false;
            }
            param.appendIf(z, Column.PACKAGE_ID, Integer.valueOf(packageId));
        }
        return (OrderInfo) getSubManager("purchase", OrderInfo.class).post(param);
    }

    public void verifyOrder(String result) throws DataLoadException, AlipayException {
        String strResult = result.substring(0, result.indexOf("&sign_type="));
        String signStr = "&sign=\"";
        String sign = result.substring(signStr.length() + result.indexOf(signStr));
        if (!((VerifyResult) getSubManager("verify", VerifyResult.class).post((JsonRequestParam) ((JsonRequestParam) RequestParam.json().append("result", strResult)).append("sign", sign.substring(0, sign.length() - 1)))).isValid) {
            throw new AlipayException(Res.getString(R.string.purchase_error_verify_failed));
        }
    }
}
