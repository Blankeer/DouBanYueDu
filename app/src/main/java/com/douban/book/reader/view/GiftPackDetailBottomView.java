package com.douban.book.reader.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.GiftPack;
import com.douban.book.reader.event.LoggedInEvent;
import com.douban.book.reader.fragment.AlertDialogFragment.Builder;
import com.douban.book.reader.fragment.BaseFragment;
import com.douban.book.reader.fragment.GiftDetailFragment_;
import com.douban.book.reader.fragment.LoginFragment_;
import com.douban.book.reader.fragment.share.ShareGiftPackEditFragment_;
import com.douban.book.reader.manager.GiftPackManager;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.util.ViewUtils;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903161)
public class GiftPackDetailBottomView extends LinearLayout {
    @ViewById(2131558855)
    TextView mBtnAction;
    GiftPack mGiftPack;
    @Bean
    GiftPackManager mGiftPackManager;
    @ViewById(2131558865)
    TextView mTipsView;

    public GiftPackDetailBottomView(Context context) {
        super(context);
    }

    public GiftPackDetailBottomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GiftPackDetailBottomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @AfterViews
    void init() {
        setOrientation(1);
        ThemedAttrs.ofView(this.mTipsView).append(R.attr.backgroundColorArray, Integer.valueOf(R.array.content_text_color)).updateView();
        this.mTipsView.setAlpha(0.9f);
        ViewUtils.setEventAware(this);
    }

    public GiftPackDetailBottomView packId(int packId) {
        loadData(packId);
        return this;
    }

    public GiftPackDetailBottomView hashCode(String hashCode) {
        loadDataByHashCode(hashCode);
        return this;
    }

    @Background
    void loadData(int packId) {
        try {
            this.mGiftPack = this.mGiftPackManager.getGiftPack(packId);
            updateViews();
        } catch (Throwable e) {
            ToastUtils.showToast(e);
        }
    }

    @Background
    void loadDataByHashCode(String hashCode) {
        try {
            this.mGiftPack = this.mGiftPackManager.getByHashCode(hashCode);
            updateViews();
        } catch (Throwable e) {
            ToastUtils.showToast(e);
        }
    }

    @UiThread
    void updateViews() {
        if (this.mGiftPack != null) {
            if (this.mGiftPack.isMine()) {
                CharSequence quantityInfo;
                if (this.mGiftPack.isUnlimited()) {
                    quantityInfo = Res.getString(R.string.pack_quantity_info_unlimited, Integer.valueOf(this.mGiftPack.giftCount));
                } else if (this.mGiftPack.isDepleted()) {
                    quantityInfo = Res.getString(R.string.pack_quantity_info_depleted, Integer.valueOf(this.mGiftPack.quantity));
                } else {
                    quantityInfo = Res.getString(R.string.pack_quantity_info, Integer.valueOf(this.mGiftPack.quantity), Integer.valueOf(this.mGiftPack.availableQuantity()));
                }
                ViewUtils.showTextIfNotEmpty(this.mTipsView, quantityInfo);
                if (this.mGiftPack.isDepleted()) {
                    this.mBtnAction.setText(R.string.btn_gift_pack_is_depleted_owner);
                    updateActionButtonEnabled(false);
                    return;
                }
                this.mBtnAction.setText(RichText.textWithIcon((int) R.drawable.v_gift, (int) R.string.btn_share_gift_pack));
                updateActionButtonEnabled(true);
                return;
            }
            CharSequence tips = null;
            if (!this.mGiftPack.isDepleted()) {
                if (UserManager.getInstance().getUserInfo().isAnonymous()) {
                    tips = Res.getString(R.string.tips_login_needed_to_receive_gift);
                } else {
                    tips = Res.getString(R.string.msg_current_account, UserManager.getInstance().getUserInfo().name);
                }
            }
            ViewUtils.showTextIfNotEmpty(this.mTipsView, tips);
            if (this.mGiftPack.isDepleted()) {
                this.mBtnAction.setText(R.string.btn_gift_pack_is_depleted);
                updateActionButtonEnabled(false);
                return;
            }
            this.mBtnAction.setText(RichText.textWithIcon((int) R.drawable.v_gift, (int) R.string.btn_gift_receive));
            updateActionButtonEnabled(true);
        }
    }

    private void updateActionButtonEnabled(boolean enabled) {
        ViewUtils.visible(this.mBtnAction);
        this.mBtnAction.setEnabled(enabled);
        ThemedAttrs.ofView(this.mBtnAction).append(R.attr.backgroundColorArray, Integer.valueOf(enabled ? R.array.red : R.array.secondary_text_color)).updateView();
    }

    @Click({2131558855})
    void onBtnActionClicked() {
        if (this.mGiftPack != null) {
            if (this.mGiftPack.isMine()) {
                ShareGiftPackEditFragment_.builder().packId(this.mGiftPack.id).build().showAsActivity((View) this);
            } else {
                receiveGiftCheckLogin();
            }
        }
    }

    @Background
    void receiveGift() {
        if (this.mGiftPack != null) {
            try {
                GiftDetailFragment_.builder().uuid(this.mGiftPackManager.receive(this.mGiftPack.hashCode).uuid).build().showAsActivity((View) this);
                BaseFragment fragment = ViewUtils.getAttachedFragment(this);
                if (fragment != null) {
                    fragment.finish();
                }
            } catch (Throwable e) {
                ToastUtils.showToast(e);
            }
        }
    }

    void receiveGiftCheckLogin() {
        if (UserManager.getInstance().isAnonymousUser()) {
            String actionName = Res.getString(R.string.verb_receive_gift);
            new Builder().setMessage(Res.getString(R.string.dialog_message_login_required, actionName)).setPositiveButton((int) R.string.dialog_button_login, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    LoginFragment_.builder().build().showAsActivity(GiftPackDetailBottomView.this);
                }
            }).setNegativeButton((int) R.string.dialog_button_cancel, null).create().show();
            return;
        }
        receiveGift();
    }

    public void onEventMainThread(LoggedInEvent event) {
        if (this.mGiftPack != null) {
            loadDataByHashCode(this.mGiftPack.hashCode);
        }
    }
}
