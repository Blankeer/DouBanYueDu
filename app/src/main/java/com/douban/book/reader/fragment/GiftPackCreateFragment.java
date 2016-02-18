package com.douban.book.reader.fragment;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.entity.GiftEvent;
import com.douban.book.reader.entity.GiftPack;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.event.PurchasedEvent;
import com.douban.book.reader.fragment.AlertDialogFragment.Builder;
import com.douban.book.reader.fragment.BaseFragment.OnActivityResultHandler;
import com.douban.book.reader.fragment.interceptor.ForcedLoginInterceptor;
import com.douban.book.reader.manager.GiftEventManager;
import com.douban.book.reader.manager.GiftPackManager;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.span.ThemedForegroundColorSpan;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.ReaderUriUtils;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.BoxedWorksView;
import com.douban.book.reader.view.GiftMessageView;
import com.douban.book.reader.view.RoundTipView;
import com.tencent.connect.common.Constants;
import java.util.Date;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

@EFragment
public class GiftPackCreateFragment extends BaseContentFragment {
    @FragmentArg
    int eventId;
    @ViewById(2131558600)
    TextView mAvailableAmount;
    @ViewById(2131558597)
    BoxedWorksView mBoxedWorksView;
    @ViewById(2131558606)
    Button mBtnCreateGiftPack;
    @ViewById(2131558603)
    TextView mBtnEditGiftNote;
    @ViewById(2131558605)
    TextView mBtnWriteGiftNote;
    private GiftEvent mGiftEvent;
    @Bean
    GiftEventManager mGiftEventManager;
    private CharSequence mGiftMessage;
    private Date mGiftMessageDate;
    @ViewById(2131558604)
    GiftMessageView mGiftMessageView;
    private GiftPack mGiftPack;
    @Bean
    GiftPackManager mGiftPackManager;
    @ViewById(2131558599)
    EditText mQuantity;
    @ViewById(2131558598)
    RoundTipView mRoundTipView1;
    @ViewById(2131558601)
    RoundTipView mRoundTipView2;
    @Bean
    UserManager mUserManager;
    @Bean
    WorksManager mWorksManager;
    @ViewById(2131558547)
    TextView mWorksSubTitle;
    @ViewById(2131558546)
    TextView mWorksTitle;
    @FragmentArg
    int worksId;

    public GiftPackCreateFragment() {
        this.mGiftMessageDate = new Date();
        setShowInterceptor(new ForcedLoginInterceptor());
    }

    @AfterViews
    void init() {
        setTitle((int) R.string.title_gift_pack_create);
        enablePullToRefresh(false);
        this.mBtnEditGiftNote.setText(RichText.textWithIcon((int) R.drawable.v_write_gift_note, (int) R.string.btn_edit_gift_note));
        this.mBtnCreateGiftPack.setText(RichText.textWithIcon((int) R.drawable.v_gift, (int) R.string.btn_create_gift_pack));
        ViewUtils.of(this.mBoxedWorksView).widthMatchParent().height(Res.getDimensionPixelSize(R.dimen.boxed_gift_view_height)).commit();
        this.mBoxedWorksView.isOpened(true).showBoxCover(true);
        this.mRoundTipView1.text(Constants.VIA_TO_TYPE_QQ_GROUP).showTipTail(true).tipColorResId(R.array.red);
        this.mRoundTipView2.text(Constants.VIA_SSO_LOGIN).showTipTail(true).tipColorResId(R.array.red);
        loadData();
    }

    protected void updateThemedViews() {
        super.updateThemedViews();
        ViewUtils.setDrawableTopLarge(this.mBtnWriteGiftNote, Res.getDrawable(R.drawable.v_write_gift_note));
    }

    protected int onObtainContentViewLayoutResId() {
        return R.layout.frag_gift_pack_create;
    }

    @Background
    void loadData() {
        try {
            updateViews(this.mWorksManager.getWorks(this.worksId));
        } catch (Throwable e) {
            ToastUtils.showToast(e);
        }
        if (this.eventId > 0) {
            try {
                this.mGiftEvent = (GiftEvent) this.mGiftEventManager.getFromRemote(Integer.valueOf(this.eventId));
            } catch (DataLoadException e2) {
                try {
                    this.mGiftEvent = this.mGiftEventManager.getGiftEvent(this.eventId);
                } catch (DataLoadException e1) {
                    Logger.e(this.TAG, e1);
                }
            }
            updateAvailableCount();
        }
    }

    @UiThread
    void updateViews(Works works) {
        if (this.mBoxedWorksView != null) {
            this.mBoxedWorksView.worksId(works.id);
        }
        ViewUtils.showTextIfNotEmpty(this.mWorksTitle, works.title);
        ViewUtils.showTextIfNotEmpty(this.mWorksSubTitle, works.subtitle);
        updateGiftMessageViewVisibility();
        updatePackButton();
    }

    private void updateGiftMessageViewVisibility() {
        boolean giftMessageWritten = StringUtils.isNotEmpty(this.mGiftMessage);
        ViewUtils.showIf(giftMessageWritten, this.mGiftMessageView, this.mBtnEditGiftNote);
        ViewUtils.goneIf(giftMessageWritten, this.mBtnWriteGiftNote);
        if (giftMessageWritten) {
            this.mGiftMessageView.giver(this.mUserManager.getDisplayUserName()).recipient(new RichText().appendWithSpans((int) R.string.text_default_recipient, new ThemedForegroundColorSpan(R.array.secondary_text_color))).message(this.mGiftMessage).messageDate(this.mGiftMessageDate);
        }
    }

    @TextChange({2131558599})
    void onQuantityChanged() {
        updatePackButton();
    }

    private void updatePackButton() {
        this.mBtnCreateGiftPack.setEnabled(shouldEnablePackButton());
    }

    private boolean shouldEnablePackButton() {
        if (StringUtils.toInt(this.mQuantity.getText()) > 0 && !StringUtils.isEmpty(this.mGiftMessage)) {
            return true;
        }
        return false;
    }

    @UiThread
    void updateAvailableCount() {
        if (this.mGiftEvent != null) {
            if (this.mGiftEvent.availableAmount > 0) {
                ViewUtils.showText(this.mAvailableAmount, Res.getString(R.string.description_available_count, Integer.valueOf(this.mGiftEvent.availableAmount)));
            } else if (this.mGiftEvent.amount > 0) {
                new Builder().setMessage(Res.getString(R.string.dialog_message_no_quantity_for_gift, Integer.valueOf(this.mGiftEvent.amount))).setNegativeButton((int) R.string.dialog_button_ok, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        GiftPackCreateFragment.this.finishSkippingCheck();
                    }
                }).setOnCancelListener(new OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        GiftPackCreateFragment.this.finishSkippingCheck();
                    }
                }).create().show((Fragment) this);
            }
        }
    }

    @Click({2131558605, 2131558603})
    void onBtnEditGiftMessageClicked() {
        CharSequence message = this.mGiftMessageView.getMessage();
        boolean selected = false;
        if (StringUtils.isEmpty(message)) {
            message = this.mGiftEvent != null ? this.mGiftEvent.defaultMessage : Res.getString(R.string.default_gift_message);
            selected = true;
        }
        GiftMessageEditFragment_.builder().message(StringUtils.toStr(message)).selected(selected).build().showAsActivity(PageOpenHelper.from((Fragment) this).onResult(new OnActivityResultHandler() {
            public void onActivityResultedOk(Intent data) {
                GiftPackCreateFragment.this.mGiftMessage = data.getStringExtra(GiftMessageEditFragment.KEY_RESULT_MESSAGE);
                GiftPackCreateFragment.this.updateGiftMessageViewVisibility();
                GiftPackCreateFragment.this.updatePackButton();
            }
        }));
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @org.androidannotations.annotations.Background
    @org.androidannotations.annotations.Click({2131558606})
    void onBtnCreateGiftPackClicked() {
        /*
        r6 = this;
        r6.showLoadingDialog();	 Catch:{ DataLoadException -> 0x0047 }
        r1 = r6.mGiftPackManager;	 Catch:{ DataLoadException -> 0x0047 }
        r2 = r6.worksId;	 Catch:{ DataLoadException -> 0x0047 }
        r3 = r6.eventId;	 Catch:{ DataLoadException -> 0x0047 }
        r4 = r6.mQuantity;	 Catch:{ DataLoadException -> 0x0047 }
        r4 = r4.getText();	 Catch:{ DataLoadException -> 0x0047 }
        r4 = com.douban.book.reader.util.StringUtils.toInt(r4);	 Catch:{ DataLoadException -> 0x0047 }
        r5 = r6.mGiftMessage;	 Catch:{ DataLoadException -> 0x0047 }
        r1 = r1.create(r2, r3, r4, r5);	 Catch:{ DataLoadException -> 0x0047 }
        r6.mGiftPack = r1;	 Catch:{ DataLoadException -> 0x0047 }
        r1 = r6.mGiftPack;	 Catch:{ DataLoadException -> 0x0047 }
        r1 = r1.isPacked();	 Catch:{ DataLoadException -> 0x0047 }
        if (r1 == 0) goto L_0x002a;
    L_0x0023:
        r6.redirectToDetailPage();	 Catch:{ DataLoadException -> 0x0047 }
    L_0x0026:
        r6.dismissLoadingDialog();
    L_0x0029:
        return;
    L_0x002a:
        r1 = com.douban.book.reader.fragment.PurchaseFragment_.builder();	 Catch:{ DataLoadException -> 0x0047 }
        r2 = r6.mGiftPack;	 Catch:{ DataLoadException -> 0x0047 }
        r2 = r2.id;	 Catch:{ DataLoadException -> 0x0047 }
        r2 = com.douban.book.reader.util.ReaderUri.giftPack(r2);	 Catch:{ DataLoadException -> 0x0047 }
        r1 = r1.uri(r2);	 Catch:{ DataLoadException -> 0x0047 }
        r2 = 0;
        r1 = r1.promptDownload(r2);	 Catch:{ DataLoadException -> 0x0047 }
        r1 = r1.build();	 Catch:{ DataLoadException -> 0x0047 }
        r1.showAsActivity(r6);	 Catch:{ DataLoadException -> 0x0047 }
        goto L_0x0026;
    L_0x0047:
        r0 = move-exception;
        com.douban.book.reader.util.ToastUtils.showToast(r0);	 Catch:{ all -> 0x004f }
        r6.dismissLoadingDialog();
        goto L_0x0029;
    L_0x004f:
        r1 = move-exception;
        r6.dismissLoadingDialog();
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.douban.book.reader.fragment.GiftPackCreateFragment.onBtnCreateGiftPackClicked():void");
    }

    public void onEventMainThread(PurchasedEvent event) {
        int packId = ReaderUriUtils.getGiftPackId(event.getPurchasedItem());
        if (this.mGiftPack != null && this.mGiftPack.id == packId) {
            redirectToDetailPage();
        }
    }

    private void redirectToDetailPage() {
        if (this.mGiftPack != null) {
            GiftPackDetailFragment_.builder().packId(this.mGiftPack.id).build().showAsActivity((Fragment) this);
        }
        finish();
    }
}
