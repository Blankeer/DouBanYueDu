package com.douban.book.reader.alipay;

import android.app.Activity;
import android.net.Uri;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.entity.OrderInfo;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.event.PurchasedEvent;
import com.douban.book.reader.fragment.BaseFragment;
import com.douban.book.reader.manager.AlipayManager;
import com.douban.book.reader.manager.AlipayManager_;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.Tag;

public class PurchaseHelper {
    private final Activity mActivity;
    private final AlipayManager mAlipayManager;

    public PurchaseHelper(BaseFragment fragment) {
        this.mAlipayManager = AlipayManager_.getInstance_(App.get());
        this.mActivity = fragment.getActivity();
    }

    public void deposit(int amount) throws PurchaseException {
        try {
            doPayment(this.mAlipayManager.deposit(amount));
        } catch (Throwable e) {
            throw new PurchaseException(e);
        }
    }

    public void purchase(Uri itemToPurchase, boolean secretly) throws PurchaseException {
        try {
            OrderInfo orderInfo = this.mAlipayManager.purchase(itemToPurchase, secretly);
            if (!orderInfo.hasFinished) {
                doPayment(orderInfo);
            }
            EventBusUtils.post(new PurchasedEvent(itemToPurchase));
        } catch (Throwable e) {
            throw new PurchaseException(e);
        }
    }

    private void doPayment(OrderInfo orderInfo) throws DataLoadException, AlipayException {
        try {
            this.mAlipayManager.verifyOrder(new MobileSecurePayer().pay(orderInfo.order, this.mActivity));
        } catch (AlipayException e) {
            if (!StringUtils.equals(e.getMessage(), Res.getString(R.string.purchase_error_cancelled))) {
                Logger.ec(Tag.PURCHASE, e);
            }
            throw e;
        }
    }
}
