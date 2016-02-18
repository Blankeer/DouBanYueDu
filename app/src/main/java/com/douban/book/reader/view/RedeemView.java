package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.RedeemRecord;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.network.client.RestClient;
import com.douban.book.reader.network.param.RequestParam;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.util.ViewUtils;
import com.sina.weibo.sdk.register.mobile.SelectCountryActivity;
import io.realm.internal.Table;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903178)
public class RedeemView extends LinearLayout {
    private static final String TAG;
    @ViewById(2131558665)
    Button mBtnConfirm;
    @ViewById(2131558922)
    EditText mInput;
    private RedeemViewListener mListener;
    @Bean
    UserManager mUserManager;

    public interface RedeemViewListener {
        void onRedeemFailed(Exception exception);

        void onRedeemStarted();

        void onRedeemSucceed(RedeemRecord redeemRecord);
    }

    static {
        TAG = RedeemView.class.getSimpleName();
    }

    public RedeemView(Context context) {
        super(context);
    }

    public RedeemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RedeemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setListener(RedeemViewListener listener) {
        this.mListener = listener;
    }

    @AfterViews
    void init() {
        ViewUtils.of(this).widthMatchParent().heightWrapContent().verticalPaddingResId(R.dimen.general_subview_vertical_padding_large).commit();
        setOrientation(1);
        this.mBtnConfirm.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String input = RedeemView.this.mInput.getText().toString();
                if (StringUtils.isEmpty(input)) {
                    ToastUtils.showToast((int) R.string.hint_redeem_input);
                    return;
                }
                RedeemView.this.onRedeemStarted();
                RedeemView.this.exchange(input);
            }
        });
    }

    @Background
    void exchange(String code) {
        try {
            RedeemRecord redeemRecord = (RedeemRecord) new RestClient("redeem", RedeemRecord.class).post(RequestParam.json().append(SelectCountryActivity.EXTRA_COUNTRY_CODE, code));
            this.mUserManager.getCurrentUserFromServer();
            onRedeemSucceed(redeemRecord);
        } catch (Exception e) {
            Logger.e(TAG, e);
            onRedeemFailed(e);
        }
    }

    @UiThread
    void onRedeemStarted() {
        if (this.mListener != null) {
            this.mListener.onRedeemStarted();
        }
    }

    @UiThread
    void onRedeemSucceed(RedeemRecord redeemRecord) {
        this.mInput.setText(Table.STRING_DEFAULT_VALUE);
        if (this.mListener != null) {
            this.mListener.onRedeemSucceed(redeemRecord);
        }
    }

    @UiThread
    void onRedeemFailed(Exception e) {
        if (this.mListener != null) {
            this.mListener.onRedeemFailed(e);
        }
    }
}
