package com.douban.book.reader.fragment;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.Gift;
import com.douban.book.reader.event.LoggedInEvent;
import com.douban.book.reader.fragment.share.ShareGiftEditFragment_;
import com.douban.book.reader.helper.AppUri;
import com.douban.book.reader.manager.GiftManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.KnotPageTopView;
import com.douban.book.reader.view.LoadErrorPageView.RefreshClickListener;
import com.douban.book.reader.view.card.ButtonCard;
import com.douban.book.reader.view.card.GiftBoxCard;
import com.douban.book.reader.view.card.GiftBoxCard_;
import com.douban.book.reader.view.card.GiftMessageCard;
import com.douban.book.reader.view.card.GiftMessageCard_;
import com.douban.book.reader.view.card.LinkImageCard_;
import com.douban.book.reader.view.card.TextCard;
import com.douban.book.reader.view.card.WorksInfoCard;
import com.douban.book.reader.view.card.WorksInfoCard_;
import java.util.UUID;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;

@EFragment
@OptionsMenu({2131623944})
public class GiftDetailFragment extends BaseCardFragment {
    @Bean
    GiftManager mGiftManager;
    private boolean mIsRefreshing;
    @FragmentArg
    UUID uuid;

    /* renamed from: com.douban.book.reader.fragment.GiftDetailFragment.2 */
    class AnonymousClass2 implements OnClickListener {
        final /* synthetic */ Gift val$gift;

        AnonymousClass2(Gift gift) {
            this.val$gift = gift;
        }

        public void onClick(View v) {
            WorksProfileFragment_.builder().worksId(this.val$gift.works.id).build().showAsActivity(GiftDetailFragment.this);
        }
    }

    /* renamed from: com.douban.book.reader.fragment.GiftDetailFragment.3 */
    class AnonymousClass3 implements OnClickListener {
        final /* synthetic */ Gift val$gift;

        AnonymousClass3(Gift gift) {
            this.val$gift = gift;
        }

        public void onClick(View v) {
            ShareGiftEditFragment_.builder().uuid(this.val$gift.uuid).build().showAsActivity(GiftDetailFragment.this);
        }
    }

    public GiftDetailFragment() {
        this.mIsRefreshing = false;
    }

    @AfterViews
    public void init() {
        setTitle((int) R.string.title_gift_detail);
        enablePullToRefresh(true);
        loadData();
    }

    public void onRefresh() {
        if (!isRefreshing()) {
            this.mIsRefreshing = true;
            loadData();
        }
    }

    @Background
    void loadData() {
        showLoadingDialog();
        try {
            Gift gift;
            if (isRefreshing()) {
                gift = (Gift) this.mGiftManager.getFromRemote(this.uuid);
            } else {
                gift = this.mGiftManager.getGift(this.uuid);
            }
            updateView(gift);
            this.mIsRefreshing = false;
            dismissLoadingDialog();
        } catch (Throwable e) {
            if (isRefreshing()) {
                ToastUtils.showToast(e);
            } else {
                loadFailed(e);
            }
            this.mIsRefreshing = false;
            dismissLoadingDialog();
        } catch (Throwable th) {
            this.mIsRefreshing = false;
            dismissLoadingDialog();
        }
    }

    @UiThread
    void loadFailed(DataLoadException e) {
        showLoadErrorPage(e, new RefreshClickListener() {
            public void onClick() {
                GiftDetailFragment.this.loadData();
            }
        });
        Logger.e(this.TAG, e);
    }

    @UiThread
    void updateView(Gift gift) {
        if (gift != null) {
            removeAllCards();
            View knot = new KnotPageTopView(getContext());
            ViewUtils.of(knot).widthMatchParent().height(Utils.dp2pixel(80.0f)).commit();
            addView(knot).noContentPadding();
            addCard(LinkImageCard_.build(getContext()).heightResId(R.dimen.banner_height).image(gift.event.subjectImg));
            addCard(((TextCard) ((TextCard) ((TextCard) new TextCard(getContext()).gravity(1).content(Res.getString(R.string.format_gift_giver_recipient_detail, gift.giver.name, gift.recipient.name, gift.getGiftAlias())).bold(true).textSize(Res.getDimension(R.dimen.general_font_size_large)).backgroundColorArray(R.array.gift_page_bg)).paddingTopResId(R.dimen.general_subview_vertical_padding_large)).paddingBottomResId(R.dimen.general_subview_vertical_padding_normal)).noDivider());
            addCard(((GiftMessageCard) GiftMessageCard_.build(getContext()).gift(gift).backgroundColorArray(R.array.gift_page_bg)).noDivider());
            addCard(((GiftBoxCard) GiftBoxCard_.build(getContext()).gift(gift).backgroundColorArray(R.array.gift_page_bg)).noDivider());
            addCard(((WorksInfoCard) WorksInfoCard_.build(getContext()).worksId(gift.works.id).backgroundColorArray(R.array.gift_page_bg)).noDivider());
            if (gift.isMine()) {
                addCard(((ButtonCard) new ButtonCard(getContext()).text(RichText.textWithIcon((int) R.drawable.v_read, (int) R.string.btn_to_my_own_works)).contentPaddingBottomResId(R.dimen.general_subview_vertical_padding_normal)).clickListener(new AnonymousClass2(gift)));
                addCard(((ButtonCard) ((ButtonCard) ((ButtonCard) new ButtonCard(getContext()).text(RichText.textWithIcon((int) R.drawable.v_share, (int) R.string.btn_share_my_gifts)).contentPaddingTop(0)).contentPaddingBottomResId(R.dimen.general_subview_vertical_padding_normal)).noDivider()).clickListener(new AnonymousClass3(gift)));
                addCard(((TextCard) new TextCard(getContext()).content(Res.getString(R.string.tips_for_received_gifts)).textColorArray(R.array.secondary_text_color).paddingVerticalResId(R.dimen.general_subview_vertical_padding_normal)).noDivider());
            } else {
                addCard(((ButtonCard) new ButtonCard(getContext()).text((int) R.string.btn_view_works_profile).openWhenClicked(AppUri.worksProfile(gift.works.id))).noDivider());
            }
            if (gift.event != null && gift.event.isOnGoing()) {
                if (StringUtils.isNotEmpty(gift.event.url)) {
                    if (StringUtils.isNotEmpty(gift.event.giftAdImg)) {
                        addCard(LinkImageCard_.build(getContext()).heightResId(R.dimen.banner_height_large).image(gift.event.giftAdImg).uri(gift.event.url).noDivider());
                    }
                }
            }
        }
    }

    @OptionsItem({2131558988})
    void onMenuItemShare() {
        ShareGiftEditFragment_.builder().uuid(this.uuid).build().showAsActivity((Fragment) this);
    }

    public void onEventMainThread(LoggedInEvent event) {
        onRefresh();
    }

    private boolean isRefreshing() {
        return this.mIsRefreshing;
    }
}
