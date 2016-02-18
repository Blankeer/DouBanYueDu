package com.douban.book.reader.fragment;

import android.view.View;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.alipay.PurchaseHelper;
import com.douban.book.reader.app.App;
import com.douban.book.reader.fragment.interceptor.LoginRecommendedInterceptor;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.util.ExceptionUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.view.ParagraphView.Indent;
import com.douban.book.reader.view.card.DepositAmountSelectionCard;
import com.douban.book.reader.view.card.DepositAmountSelectionCard.Listener;
import com.douban.book.reader.view.card.TextCard;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;

@EFragment
public class AccountDepositFragment extends BaseCardFragment implements Listener {
    public AccountDepositFragment() {
        setShowInterceptor(new LoginRecommendedInterceptor(Res.getString(R.string.action_deposit)));
    }

    @AfterViews
    void init() {
        setTitle((int) R.string.title_account_deposit);
        initAccountInfo();
        addCard(new DepositAmountSelectionCard(App.get()).listener(this));
        addCard(((TextCard) new TextCard(App.get()).title((int) R.string.title_caution)).contentShowBullet().content(Res.getString(R.string.msg_deposit_caution)).firstLineIndent(Indent.NONE));
    }

    public void onConfirmDeposit(int amount) {
        deposit(amount);
    }

    private void initAccountInfo() {
        View view = View.inflate(App.get(), R.layout.view_simple_account_info, null);
        if (UserManager.getInstance().getUserInfo().type == 0) {
            ((TextView) view.findViewById(R.id.account)).setText(R.string.msg_current_account_anonymous);
        } else {
            ((TextView) view.findViewById(R.id.account)).setText(Res.getString(R.string.msg_current_account, UserManager.getInstance().getUserInfo().name));
        }
        ((TextView) view.findViewById(R.id.balance)).setText(Res.getString(R.string.msg_account_balance, Utils.formatPriceWithSymbol(userInfo.amountLeft)));
        addCard(((TextCard) new TextCard(getActivity()).content(view)).noDivider());
    }

    @Background
    void deposit(int amount) {
        try {
            showBlockingLoadingDialog();
            new PurchaseHelper(this).deposit(amount);
            onDepositSucceed();
        } catch (Throwable e) {
            dismissLoadingDialog();
            ToastUtils.showToast(ExceptionUtils.getHumanReadableMessage(e, (int) R.string.general_load_failed));
            Logger.e(this.TAG, e);
        }
    }

    @Background
    void refreshUserInfo() {
        try {
            UserManager.getInstance().getCurrentUserFromServer();
        } catch (Exception e) {
            Logger.e(this.TAG, e);
        }
    }

    @UiThread
    void onDepositSucceed() {
        dismissLoadingDialog();
        refreshUserInfo();
        ToastUtils.showToast((int) R.string.toast_deposit_succeed);
        finish();
    }
}
