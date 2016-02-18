package com.douban.book.reader.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.net.Uri;
import com.alipay.sdk.protocol.h;
import com.douban.book.reader.R;
import com.douban.book.reader.alipay.PurchaseHelper;
import com.douban.book.reader.app.App;
import com.douban.book.reader.content.pack.Package;
import com.douban.book.reader.entity.GiftPack;
import com.douban.book.reader.entity.Manifest;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.fragment.AlertDialogFragment.Builder;
import com.douban.book.reader.fragment.interceptor.LoginRecommendedInterceptor;
import com.douban.book.reader.manager.GiftPackManager;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.task.DownloadManager;
import com.douban.book.reader.util.AnalysisUtils;
import com.douban.book.reader.util.ExceptionUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.ReaderUri;
import com.douban.book.reader.util.ReaderUriUtils;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.view.ParagraphView.Indent;
import com.douban.book.reader.view.card.PurchaseCard.Listener;
import com.douban.book.reader.view.card.PurchaseCard_;
import com.douban.book.reader.view.card.TextCard;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;

@EFragment
public class PurchaseFragment extends BaseCardFragment implements Listener {
    @FragmentArg
    int chapterId;
    @FragmentArg
    int giftPackId;
    private GiftPack mGiftPack;
    @Bean
    GiftPackManager mGiftPackManager;
    private int mUriType;
    private Works mWorks;
    @Bean
    WorksManager mWorksManager;
    @FragmentArg
    boolean promptDownload;
    @FragmentArg
    Uri uri;
    @FragmentArg
    int worksId;

    public PurchaseFragment() {
        this.promptDownload = true;
        setShowInterceptor(new LoginRecommendedInterceptor(Res.getString(R.string.action_purchase)));
    }

    @AfterViews
    void init() {
        if (this.uri == null) {
            if (this.chapterId > 0) {
                this.uri = ReaderUri.pack(this.worksId, this.chapterId);
            } else if (this.worksId > 0) {
                this.uri = ReaderUri.works(this.worksId);
            } else if (this.giftPackId > 0) {
                this.uri = ReaderUri.giftPack(this.giftPackId);
            }
        }
        if (this.uri != null) {
            this.mUriType = ReaderUriUtils.getType(this.uri);
        }
        loadData();
    }

    @Background
    void loadData() {
        try {
            switch (this.mUriType) {
                case h.i /*10*/:
                    this.mGiftPack = this.mGiftPackManager.getGiftPack(ReaderUriUtils.getGiftPackId(this.uri));
                    break;
                default:
                    this.worksId = ReaderUriUtils.getWorksId(this.uri);
                    this.mWorks = this.mWorksManager.getWorks(this.worksId);
                    if (ReaderUriUtils.getType(this.uri) == 2) {
                        Manifest.load(this.worksId);
                        break;
                    }
                    break;
            }
            updateViews();
        } catch (Exception e) {
            Logger.e(this.TAG, e);
            ToastUtils.showToast((int) R.string.toast_general_load_failed);
            finish();
        }
    }

    @UiThread
    void updateViews() {
        if (isTakeForFree()) {
            setTitle((int) R.string.btn_take_for_free);
        } else if (ReaderUriUtils.getType(this.uri) == 2) {
            setTitle((int) R.string.title_purchase_chapter);
        } else {
            setTitle((int) R.string.title_purchase);
        }
        this.mCardView.setPadding(0, 0, 0, 0);
        Context context = getActivity();
        if (context == null) {
            context = App.get();
        }
        addCard(PurchaseCard_.build(context).uri(this.uri).listener(this));
        addCard(((TextCard) new TextCard(context).title((int) R.string.title_caution)).contentShowBullet().content(Res.getString(R.string.msg_purchase_caution)).firstLineIndent(Indent.NONE));
    }

    public void performPurchase(boolean secretly) {
        showBlockingLoadingDialog();
        doPurchase(secretly);
    }

    @Background
    void doPurchase(boolean secretly) {
        try {
            new PurchaseHelper(this).purchase(this.uri, secretly);
            onPurchaseSucceed();
        } catch (Throwable e) {
            dismissLoadingDialog();
            ToastUtils.showToast(ExceptionUtils.getHumanReadableMessage(e, Res.getString(R.string.toast_purchase_failed, getActionStr())));
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

    @Background
    void updateWorks() {
        if (this.mWorks != null) {
            try {
                if (this.mWorks.isUncompletedColumnOrSerial()) {
                    this.mWorksManager.subscribe(this.mWorks.id);
                }
                this.mWorksManager.getFromRemote(Integer.valueOf(this.mWorks.id));
            } catch (Exception e) {
                Logger.e(this.TAG, e);
            }
        }
    }

    @Background
    void updateGiftPack() {
        if (this.mGiftPack != null) {
            try {
                this.mGiftPackManager.getFromRemote(Integer.valueOf(this.mGiftPack.id));
            } catch (Exception e) {
                Logger.e(this.TAG, e);
            }
        }
    }

    @UiThread
    void onPurchaseSucceed() {
        dismissLoadingDialog();
        refreshUserInfo();
        if (!isTakeForFree()) {
            AnalysisUtils.sendPurchaseEvent(this.uri);
        }
        if (this.mUriType == 10) {
            updateGiftPack();
            finish();
            return;
        }
        updateWorks();
        if (this.promptDownload) {
            new Builder().setMessage(Res.getString(R.string.dialog_message_purchase_succeed, getActionStr())).setPositiveButton((int) R.string.dialog_button_download_now, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    DownloadManager.scheduleDownload(PurchaseFragment.this.uri);
                }
            }).setNegativeButton((int) R.string.dialog_button_download_later, null).setOnDismissListener(new OnDismissListener() {
                public void onDismiss(DialogInterface dialog) {
                    PurchaseFragment.this.finish();
                }
            }).create().show();
            return;
        }
        finish();
    }

    private boolean isTakeForFree() {
        if (this.mUriType != 10) {
            if (ReaderUriUtils.getType(this.uri) == 2) {
                try {
                    if (Package.get(this.uri).getPrice() > 0) {
                        return false;
                    }
                    return true;
                } catch (Exception e) {
                    Logger.e(this.TAG, e);
                }
            }
            if (this.mWorks == null || this.mWorks.price > 0) {
                return false;
            }
            return true;
        } else if (this.mGiftPack == null || this.mGiftPack.price > 0) {
            return false;
        } else {
            return true;
        }
    }

    private String getActionStr() {
        return Res.getString(isTakeForFree() ? R.string.verb_get : R.string.verb_purchase);
    }
}
