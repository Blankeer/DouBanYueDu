package com.douban.book.reader.view.card;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.constant.Char;
import com.douban.book.reader.drawable.BadgeDrawable;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.event.ArkRequest;
import com.douban.book.reader.event.ColorThemeChangedEvent;
import com.douban.book.reader.event.WorksUpdatedEvent;
import com.douban.book.reader.fragment.AlertDialogFragment.Builder;
import com.douban.book.reader.fragment.LoginFragment_;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.span.LabelSpan;
import com.douban.book.reader.span.ThemedForegroundColorSpan;
import com.douban.book.reader.util.DateUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.util.ViewUtils;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;

@EViewGroup
public class WorksPreReleaseCard extends Card<WorksPreReleaseCard> {
    private TextView mBtnGetNotified;
    private TextView mTvOnSaleTime;
    private Works mWorks;
    private int mWorksId;
    @Bean
    WorksManager mWorksManager;

    public WorksPreReleaseCard(Context context) {
        super(context);
        init();
    }

    private void init() {
        content((int) R.layout.card_works_pre_release);
        this.mTvOnSaleTime = (TextView) findViewById(R.id.on_sale_time);
        this.mBtnGetNotified = (TextView) findViewById(R.id.btn_get_notified);
        noContentPadding();
        ViewUtils.setEventAware(this);
    }

    public WorksPreReleaseCard worksId(int worksId) {
        this.mWorksId = worksId;
        loadWorks();
        return this;
    }

    public void onEventMainThread(WorksUpdatedEvent event) {
        if (event.isValidFor(this.mWorksId)) {
            loadWorks();
        }
    }

    public void onEventMainThread(ColorThemeChangedEvent event) {
        updateButtons();
    }

    public void onEventMainThread(ArkRequest request) {
        if (request == ArkRequest.WORKS_PROFILE_GET_NOTIFIED_WHEN_RELEASED) {
            toggleNotifieeStatus();
        }
    }

    @Background
    void loadWorks() {
        try {
            this.mWorks = this.mWorksManager.getWorks(this.mWorksId);
            updateViews(this.mWorks);
        } catch (Exception e) {
            Logger.e(this.TAG, e);
        }
    }

    @Click({2131558553})
    void onBtnGetNotifiedClicked() {
        if (UserManager.getInstance().isAnonymousUser() && !this.mWorks.isNotifiee()) {
            String actionName = Res.getString(R.string.action_add_notifiee);
            new Builder().setMessage(Res.getString(R.string.dialog_message_login_suggested, actionName)).setPositiveButton((int) R.string.dialog_button_login, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    LoginFragment_.builder().requestToSendAfterLogin(ArkRequest.WORKS_PROFILE_GET_NOTIFIED_WHEN_RELEASED).build().showAsActivity(WorksPreReleaseCard.this);
                }
            }).setNegativeButton(Res.getString(R.string.dialog_button_skip_login, actionName), new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    WorksPreReleaseCard.this.toggleNotifieeStatus();
                }
            }).create().show();
        } else if (this.mWorks.isNotifiee()) {
            new Builder().setMessage(Res.getString(R.string.dialog_message_remove_notifiee_confirm)).setPositiveButton((int) R.string.dialog_button_remove_notifiee, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    WorksPreReleaseCard.this.toggleNotifieeStatus();
                }
            }).setNegativeButton(Res.getString(R.string.dialog_button_keep_notifiee), null).create().show();
        } else {
            toggleNotifieeStatus();
        }
    }

    @Background
    void toggleNotifieeStatus() {
        boolean isNotifiee = this.mWorks.releaseInfo != null && this.mWorks.releaseInfo.isNotifiee;
        if (isNotifiee) {
            try {
                this.mWorksManager.removeNotifiee(this.mWorksId);
            } catch (Throwable e) {
                ToastUtils.showToast(e);
                return;
            }
        }
        this.mWorksManager.addNotifiee(this.mWorksId);
        this.mWorks = this.mWorksManager.getFromRemote(Integer.valueOf(this.mWorksId));
        ToastUtils.showToast(isNotifiee ? R.string.toast_remove_notifiee_succeed : R.string.toast_add_notifiee_succeed);
        updateViews(this.mWorks);
    }

    @UiThread
    void updateViews(Works works) {
        RichText onSaleInfo = new RichText().appendWithSpans((int) R.string.works_is_on_pre_sale, new LabelSpan().backgroundColor(R.array.red).noRoundCorner());
        if (!(works.releaseInfo == null || works.releaseInfo.date == null)) {
            onSaleInfo.append((char) Char.SPACE).append((int) R.string.works_sale_time).append(DateUtils.formatYearMonth(works.releaseInfo.date));
        }
        this.mTvOnSaleTime.setText(onSaleInfo);
        updateButtons();
    }

    private void updateButtons() {
        boolean isNotifiee;
        if (this.mWorks == null || !this.mWorks.isNotifiee()) {
            isNotifiee = false;
        } else {
            isNotifiee = true;
        }
        this.mBtnGetNotified.setText(new RichText().appendWithSpans((int) R.string.works_btn_get_notified, new ThemedForegroundColorSpan(R.array.secondary_text_color)));
        ViewUtils.setDrawableTopLarge(this.mBtnGetNotified, new BadgeDrawable((int) R.drawable.v_remind).badgeHorizontalOffset(0.05f).badgeVerticalOffset(-0.1f).showBadge(isNotifiee));
    }
}
