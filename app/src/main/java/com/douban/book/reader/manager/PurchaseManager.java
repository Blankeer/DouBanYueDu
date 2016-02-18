package com.douban.book.reader.manager;

import com.douban.book.reader.entity.CouponRecord;
import com.douban.book.reader.entity.DepositRecord;
import com.douban.book.reader.entity.PurchaseRecord;
import com.douban.book.reader.entity.RedeemRecord;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EBean.Scope;

@EBean(scope = Scope.Singleton)
public class PurchaseManager extends BaseManager<PurchaseRecord> {
    public PurchaseManager() {
        super("purchases", PurchaseRecord.class);
    }

    public Lister<DepositRecord> listForDeposit() {
        return getSubManager("deposit", DepositRecord.class).list();
    }

    public Lister<PurchaseRecord> listForPurchase() {
        return list();
    }

    public Lister<CouponRecord> listForCouponRecord() {
        return getSubManager("coupon", CouponRecord.class).list();
    }

    public Lister<RedeemRecord> listForRedemptionRecord() {
        return getSubManager("redemption", RedeemRecord.class).list();
    }
}
