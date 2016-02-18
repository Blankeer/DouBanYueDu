package com.douban.book.reader.view.card;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.constant.Char;
import com.douban.book.reader.content.pack.Package;
import com.douban.book.reader.entity.GiftPack;
import com.douban.book.reader.entity.UserInfo;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.exception.PackageException;
import com.douban.book.reader.manager.GiftPackManager;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.span.LabelSpan;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.ReaderUriUtils;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.util.ViewUtils;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;

@EViewGroup
public class PurchaseCard extends Card<PurchaseCard> {
    private Button mBtnPurchase;
    private SwitchCompat mBtnSecretly;
    private GiftPack mGiftPack;
    @Bean
    GiftPackManager mGiftPackManager;
    private Listener mListener;
    private int mPrice;
    private TextView mTextPrice;
    private TextView mTitle;
    private Uri mUri;
    private int mUriType;
    private Works mWorks;
    private TextView mWorksInfo;
    @Bean
    WorksManager mWorksManager;

    public interface Listener {
        void performPurchase(boolean z);
    }

    public PurchaseCard(Context context) {
        super(context);
        this.mPrice = 0;
        init();
    }

    public PurchaseCard uri(Uri uri) {
        this.mUri = uri;
        this.mUriType = ReaderUriUtils.getType(this.mUri);
        loadData();
        return this;
    }

    public PurchaseCard listener(Listener listener) {
        this.mListener = listener;
        return this;
    }

    private void init() {
        noContentPadding();
        content((int) R.layout.card_purchase);
        findViews();
        this.mBtnPurchase.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (PurchaseCard.this.mListener != null) {
                    PurchaseCard.this.mListener.performPurchase(PurchaseCard.this.mBtnSecretly.isChecked());
                }
            }
        });
    }

    @Background
    void loadData() {
        try {
            if (this.mUriType == 10) {
                this.mGiftPack = this.mGiftPackManager.getGiftPack(ReaderUriUtils.getGiftPackId(this.mUri));
                this.mWorks = this.mWorksManager.getWorks(this.mGiftPack.works.id);
                this.mPrice = this.mGiftPack.price;
            } else {
                this.mWorks = this.mWorksManager.getWorks(ReaderUriUtils.getWorksId(this.mUri));
                this.mPrice = ReaderUriUtils.getPrice(this.mUri);
            }
            updateViews();
        } catch (DataLoadException e) {
            Logger.e(this.TAG, e);
        }
    }

    @UiThread
    void updateViews() {
        boolean z = true;
        try {
            Object[] objArr;
            RichText info = new RichText();
            if (this.mUriType == 0) {
                this.mTitle.setText(this.mWorks.titleWithQuote());
                this.mTextPrice.setText(this.mWorks.formatPriceWithColor());
                if (!this.mWorks.isColumnOrSerial() || this.mWorks.isCompleted()) {
                    z = false;
                }
                objArr = new Object[1];
                objArr[0] = Res.getString(R.string.hint_purchase_columns, this.mWorks.getWorksScheduleSummary());
                info.appendIf(z, objArr);
            } else if (this.mUriType == 2) {
                Package pack = Package.get(this.mUri);
                this.mTitle.setText(pack.getTitle());
                this.mTextPrice.setText(Utils.formatPriceWithSymbol(pack.getPrice()));
                z = StringUtils.isNotEmpty(this.mWorks.title);
                objArr = new Object[1];
                objArr[0] = Res.getString(R.string.msg_chapter_info, this.mWorks.title);
                info.appendIf(z, objArr);
            } else if (this.mUriType == 10) {
                this.mTitle.setText(new RichText().append(this.mWorks.titleWithQuote()).appendWithSpans(String.format("\u00d7%d", new Object[]{Integer.valueOf(this.mGiftPack.quantity)}), new LabelSpan().textColor(R.array.secondary_text_color)));
                this.mTextPrice.setText(Utils.formatPriceWithSymbol(this.mPrice));
            }
            UserInfo userInfo = UserManager.getInstance().getUserInfo();
            if (StringUtils.isNotEmpty(info)) {
                info.append('\n');
            }
            objArr = new Object[3];
            objArr[0] = Res.getString(R.string.account);
            objArr[1] = Character.valueOf(Char.FULLWIDTH_COLON);
            objArr[2] = userInfo.isAnonymous() ? Res.getString(R.string.not_logged_in) : userInfo.name;
            info.appendAsNewLine(objArr);
            info.appendAsNewLine(Res.getString(R.string.amount_balance), Character.valueOf(Char.FULLWIDTH_COLON), Utils.formatPriceWithSymbol(userInfo.amountLeft));
            ViewUtils.showTextIfNotEmpty(this.mWorksInfo, info);
            this.mBtnPurchase.setText(RichText.textWithIcon((int) R.drawable.v_purchase, this.mPrice > 0 ? R.string.btn_confirm_and_purchase : R.string.btn_confirm_and_get));
        } catch (PackageException e) {
            Logger.e(this.TAG, e);
        }
    }

    private void findViews() {
        this.mTitle = (TextView) findViewById(R.id.title_purchase);
        this.mTextPrice = (TextView) findViewById(R.id.price);
        this.mWorksInfo = (TextView) findViewById(R.id.works_info);
        this.mBtnPurchase = (Button) findViewById(R.id.btn_purchase);
        this.mBtnSecretly = (SwitchCompat) findViewById(R.id.btn_purchase_secretly);
    }
}
