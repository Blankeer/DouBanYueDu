package com.douban.book.reader.view.card;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.drawable.BadgeDrawable;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.event.ArkRequest;
import com.douban.book.reader.event.ColorThemeChangedEvent;
import com.douban.book.reader.fragment.AlertDialogFragment.Builder;
import com.douban.book.reader.fragment.LoginFragment_;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.span.ThemedForegroundColorSpan;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.SpanUtils;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.util.ViewUtils;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

@EViewGroup
public class ColumnInfoCard extends Card<ColumnInfoCard> {
    @ViewById(2131558523)
    TextView mBtnSubscribe;
    @ViewById(2131558522)
    TextView mColumnPlanInfo;
    @ViewById(2131558524)
    ProgressBar mProgressBar;
    private Works mWorks;
    private int mWorksId;
    @Bean
    WorksManager mWorksManager;

    public ColumnInfoCard(Context context) {
        super(context);
        content((int) R.layout.card_column_info);
        noContentPadding();
        ViewUtils.setEventAware(this);
    }

    public ColumnInfoCard worksId(int worksId) {
        this.mWorksId = worksId;
        loadData(this.mWorksId);
        return this;
    }

    public void onEventMainThread(ColorThemeChangedEvent event) {
        updateSubscribeButtons();
    }

    public void onEventMainThread(ArkRequest request) {
        if (request == ArkRequest.WORKS_PROFILE_COLUMN_SUBSCRIBE) {
            toggleSubscribeStatus();
        }
    }

    @Click({2131558523})
    void onBtnSubscribeClicked() {
        if (UserManager.getInstance().isAnonymousUser() && !this.mWorks.hasSubscribed) {
            String actionName = Res.getString(R.string.btn_subscribe);
            new Builder().setMessage(Res.getString(R.string.dialog_message_login_suggested, actionName)).setPositiveButton((int) R.string.dialog_button_login, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    LoginFragment_.builder().requestToSendAfterLogin(ArkRequest.WORKS_PROFILE_COLUMN_SUBSCRIBE).build().showAsActivity(ColumnInfoCard.this);
                }
            }).setNegativeButton(Res.getString(R.string.dialog_button_skip_login, actionName), new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    ColumnInfoCard.this.toggleSubscribeStatus();
                }
            }).create().show();
        } else if (this.mWorks.hasSubscribed) {
            new Builder().setMessage(Res.getString(R.string.dialog_message_cancel_subscription_confirm)).setPositiveButton((int) R.string.dialog_button_ok, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    ColumnInfoCard.this.toggleSubscribeStatus();
                }
            }).setNegativeButton(Res.getString(R.string.dialog_button_cancel), null).create().show();
        } else {
            toggleSubscribeStatus();
        }
    }

    @UiThread
    void onWorksSubscribedOpStart() {
        this.mProgressBar.setVisibility(0);
        this.mBtnSubscribe.setVisibility(8);
    }

    @UiThread
    void onWorksSubscribedOpFinish() {
        this.mProgressBar.setVisibility(8);
        this.mBtnSubscribe.setVisibility(0);
    }

    @Background
    void toggleSubscribeStatus() {
        boolean hasSubscribed = this.mWorks.hasSubscribed;
        onWorksSubscribedOpStart();
        if (hasSubscribed) {
            try {
                this.mWorksManager.unSubscribe(this.mWorksId);
            } catch (DataLoadException e) {
                ToastUtils.showToast(hasSubscribed ? R.string.toast_cancel_subscription_failed : R.string.toast_subscribe_failed);
                return;
            } finally {
                onWorksSubscribedOpFinish();
            }
        } else {
            this.mWorksManager.subscribe(this.mWorksId);
        }
        Works works = this.mWorksManager.getFromRemote(Integer.valueOf(this.mWorksId));
        ToastUtils.showToast(hasSubscribed ? R.string.toast_cancel_subscription_succeed : R.string.toast_subscribe_succeed);
        updateViews(works);
        onWorksSubscribedOpFinish();
    }

    @Background
    void loadData(int worksId) {
        try {
            updateViews(this.mWorksManager.getWorks(worksId));
        } catch (DataLoadException e) {
            Logger.e(this.TAG, e);
        }
    }

    @UiThread
    void updateViews(Works works) {
        this.mWorks = works;
        if (works.schedule == null) {
            hide();
        } else if (works.isCompleted()) {
            this.mColumnPlanInfo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            this.mColumnPlanInfo.setText(Res.getString(R.string.msg_column_completed, Integer.valueOf(schedule.written_num), Integer.valueOf(works.subscriptionCount)));
            ViewUtils.setTextAppearance(getContext(), this.mColumnPlanInfo, R.style.AppWidget_Text_Secondary);
            this.mBtnSubscribe.setVisibility(8);
        } else {
            this.mColumnPlanInfo.setText(RichText.textWithIcon((int) R.drawable.v_writing, Res.getString(R.string.msg_column_uncompleted, Integer.valueOf(schedule.written_num), Integer.valueOf(schedule.plan_num), Integer.valueOf(works.subscriptionCount))));
            ViewUtils.setTextAppearance(getContext(), this.mColumnPlanInfo, R.style.AppWidget_Text_Content);
            this.mColumnPlanInfo.setTextSize(0, (float) Res.getDimensionPixelSize(R.dimen.general_font_size_medium));
            this.mBtnSubscribe.setVisibility(0);
            updateSubscribeButtons();
        }
    }

    private void updateSubscribeButtons() {
        this.mBtnSubscribe.setText(SpanUtils.applySpan(Res.getString(this.mWorks.hasSubscribed ? R.string.text_has_subscribed : R.string.text_subscribe_updated), new ThemedForegroundColorSpan(R.array.secondary_text_color)));
        ViewUtils.setDrawableTopLarge(this.mBtnSubscribe, new BadgeDrawable((int) R.drawable.v_subscribe).offsetWhenBadged(-0.15f).badgeVerticalOffset(-0.1f).showBadge(this.mWorks.hasSubscribed));
    }
}
