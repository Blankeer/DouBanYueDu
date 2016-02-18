package com.douban.book.reader.view.card;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.LayoutParams;
import com.douban.book.reader.R;
import com.douban.book.reader.helper.AppUri;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.util.ViewUtils;
import com.igexin.download.Downloads;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import io.fabric.sdk.android.services.common.AbstractSpiCall;

public class DepositAmountSelectionCard extends Card<DepositAmountSelectionCard> {
    private static final int[] DEPOSIT_AMOUNT_ARRAY;
    private Listener mListener;

    /* renamed from: com.douban.book.reader.view.card.DepositAmountSelectionCard.1 */
    class AnonymousClass1 implements OnClickListener {
        final /* synthetic */ RadioGroup val$radioGroup;

        AnonymousClass1(RadioGroup radioGroup) {
            this.val$radioGroup = radioGroup;
        }

        public void onClick(View v) {
            int checkedAmountId = this.val$radioGroup.getCheckedRadioButtonId();
            if (checkedAmountId <= 0) {
                ToastUtils.showToast((int) R.string.toast_confirm_select_amount);
                return;
            }
            try {
                DepositAmountSelectionCard.this.onConfirmDeposit(((Integer) DepositAmountSelectionCard.this.findViewById(checkedAmountId).getTag()).intValue());
            } catch (Exception e) {
                Logger.e(DepositAmountSelectionCard.this.TAG, e);
                ToastUtils.showToast((int) R.string.general_load_failed);
            }
        }
    }

    public interface Listener {
        void onConfirmDeposit(int i);
    }

    static {
        DEPOSIT_AMOUNT_ARRAY = new int[]{Downloads.STATUS_SUCCESS, AppUri.OPEN_URL, 2000, BaseImageDownloader.DEFAULT_HTTP_CONNECT_TIMEOUT, AbstractSpiCall.DEFAULT_TIMEOUT};
    }

    public DepositAmountSelectionCard(Context context) {
        super(context);
        init();
    }

    public DepositAmountSelectionCard listener(Listener listener) {
        this.mListener = listener;
        return this;
    }

    private void init() {
        title((int) R.string.title_select_deposit_amount);
        ViewUtils.setBottomPaddingResId(this, R.dimen.general_subview_vertical_padding_medium);
        content((int) R.layout.view_deposit_amount_selection);
        noContentPadding();
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.amount_group);
        int checkedViewId = 0;
        for (int valueOf : DEPOSIT_AMOUNT_ARRAY) {
            Integer amount = Integer.valueOf(valueOf);
            int itemId = addAmountItemView(radioGroup, amount.intValue());
            if (amount.intValue() == DEPOSIT_AMOUNT_ARRAY[0]) {
                checkedViewId = itemId;
            }
            radioGroup.addView(ViewUtils.createDivider());
        }
        if (checkedViewId > 0) {
            radioGroup.check(checkedViewId);
        }
        Button btnDeposit = (Button) findViewById(R.id.btn_deposit);
        btnDeposit.setText(RichText.textWithIcon((int) R.drawable.v_chinese_yuan, (int) R.string.btn_deposit_with_alipay));
        btnDeposit.setOnClickListener(new AnonymousClass1(radioGroup));
    }

    private int addAmountItemView(ViewGroup container, int amount) {
        RadioButton radio = (RadioButton) View.inflate(getContext(), R.layout.item_amount_deposit, null);
        radio.setText(Utils.formatPriceWithSymbol(amount));
        radio.setTag(Integer.valueOf(amount));
        container.addView(radio, new LayoutParams(-1, -2));
        return radio.getId();
    }

    private void onConfirmDeposit(int amount) {
        if (this.mListener != null) {
            this.mListener.onConfirmDeposit(amount);
        }
    }
}
