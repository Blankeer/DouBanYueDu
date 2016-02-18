package com.douban.book.reader.fragment;

import android.view.View;
import android.view.View.OnClickListener;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.entity.RedeemRecord;
import com.douban.book.reader.entity.RedeemRecord.Type;
import com.douban.book.reader.fragment.interceptor.LoginRecommendedInterceptor;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ToastBuilder;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.view.ParagraphView.Indent;
import com.douban.book.reader.view.RedeemView.RedeemViewListener;
import com.douban.book.reader.view.card.RedeemCard;
import com.douban.book.reader.view.card.TextCard;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;

@EFragment
public class RedeemFragment extends BaseCardFragment implements RedeemViewListener {
    public RedeemFragment() {
        setShowInterceptor(new LoginRecommendedInterceptor(Res.getString(R.string.title_redeem)));
    }

    @AfterViews
    void init() {
        setTitle((int) R.string.title_redeem);
        addCard(new RedeemCard(App.get()).setRedeemViewListener(this));
        addCard(((TextCard) new TextCard(App.get()).title((int) R.string.title_description)).contentShowBullet().content(Res.getString(R.string.msg_description_for_redeem)).firstLineIndent(Indent.NONE));
    }

    public void onRedeemStarted() {
        showBlockingLoadingDialog();
    }

    public void onRedeemSucceed(RedeemRecord redeemRecord) {
        dismissLoadingDialog();
        if (redeemRecord.type == Type.WORKS) {
            new ToastBuilder().message(Res.getString(R.string.toast_redeem_works_succeed, redeemRecord.works.title)).click(new OnClickListener() {
                public void onClick(View v) {
                    UserOwnListFragment_.builder().build().showAsActivity(RedeemFragment.this);
                }
            }).attachTo(getActivity()).show();
        } else if (redeemRecord.type == Type.CASH) {
            new ToastBuilder().message(Res.getString(R.string.toast_redeem_cash_succeed, Utils.formatPrice(redeemRecord.amount))).click(new OnClickListener() {
                public void onClick(View v) {
                    AccountBalanceFragment_.builder().build().showAsActivity(RedeemFragment.this);
                }
            }).attachTo(getActivity()).show();
        }
    }

    public void onRedeemFailed(Exception e) {
        dismissLoadingDialog();
        ToastUtils.showToast((Throwable) e, (int) R.string.toast_redeem_failed);
    }
}
