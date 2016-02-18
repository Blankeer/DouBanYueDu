package com.douban.book.reader.fragment;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.BaseArrayAdapter;
import com.douban.book.reader.adapter.ViewBinderAdapter;
import com.douban.book.reader.entity.Gift;
import com.douban.book.reader.entity.GiftPack;
import com.douban.book.reader.event.GiftPackUpdatedEvent;
import com.douban.book.reader.manager.GiftManager;
import com.douban.book.reader.manager.GiftPackManager;
import com.douban.book.reader.manager.Lister;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.view.GiftPackDetailBottomView_;
import com.douban.book.reader.view.card.Card;
import com.douban.book.reader.view.card.GiftBoxCard;
import com.douban.book.reader.view.card.GiftBoxCard_;
import com.douban.book.reader.view.card.GiftMessageCard;
import com.douban.book.reader.view.card.GiftMessageCard_;
import com.douban.book.reader.view.card.LinkImageCard_;
import com.douban.book.reader.view.card.TextCard;
import com.douban.book.reader.view.card.WorksInfoCard_;
import com.douban.book.reader.view.item.GiftRecipientItemView_;
import com.mcxiaoke.next.ui.endless.EndlessListView;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;

@EFragment
public class GiftPackDetailFragment extends BaseEndlessListFragment<Gift> {
    @FragmentArg
    String hashCode;
    @Bean
    GiftManager mGiftManager;
    private GiftPack mGiftPack;
    @Bean
    GiftPackManager mGiftPackManager;
    private LinearLayout mListHeaderView;
    @FragmentArg
    int packId;

    public Lister<Gift> onCreateLister() {
        if (this.packId > 0) {
            return this.mGiftManager.listerForPack(this.packId);
        }
        return null;
    }

    public BaseArrayAdapter<Gift> onCreateAdapter() {
        return new ViewBinderAdapter(GiftRecipientItemView_.class);
    }

    protected View onCreateBottomView() {
        if (this.packId > 0) {
            return GiftPackDetailBottomView_.build(getContext()).packId(this.packId);
        }
        if (StringUtils.isNotEmpty(this.hashCode)) {
            return GiftPackDetailBottomView_.build(getContext()).hashCode(this.hashCode);
        }
        return null;
    }

    protected void onListViewCreated(EndlessListView listView) {
        this.mListHeaderView = new LinearLayout(getActivity());
        this.mListHeaderView.setOrientation(1);
        addHeaderView(this.mListHeaderView);
        loadMeta();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @org.androidannotations.annotations.Background
    void loadMeta() {
        /*
        r3 = this;
        r3.showLoadingDialog();	 Catch:{ Exception -> 0x0038 }
        r1 = r3.packId;	 Catch:{ Exception -> 0x0038 }
        if (r1 <= 0) goto L_0x002d;
    L_0x0007:
        r1 = r3.mGiftPackManager;	 Catch:{ Exception -> 0x0038 }
        r2 = r3.packId;	 Catch:{ Exception -> 0x0038 }
        r1 = r1.getGiftPack(r2);	 Catch:{ Exception -> 0x0038 }
        r3.mGiftPack = r1;	 Catch:{ Exception -> 0x0038 }
    L_0x0011:
        r3.updateHeaderView();	 Catch:{ Exception -> 0x0038 }
        r1 = r3.packId;	 Catch:{ Exception -> 0x0038 }
        if (r1 > 0) goto L_0x0029;
    L_0x0018:
        r1 = r3.mGiftPack;	 Catch:{ Exception -> 0x0038 }
        if (r1 == 0) goto L_0x0029;
    L_0x001c:
        r1 = r3.mGiftPack;	 Catch:{ Exception -> 0x0038 }
        r1 = r1.id;	 Catch:{ Exception -> 0x0038 }
        r3.packId = r1;	 Catch:{ Exception -> 0x0038 }
        r1 = r3.onCreateLister();	 Catch:{ Exception -> 0x0038 }
        r3.replaceLister(r1);	 Catch:{ Exception -> 0x0038 }
    L_0x0029:
        r3.dismissLoadingDialog();
    L_0x002c:
        return;
    L_0x002d:
        r1 = r3.mGiftPackManager;	 Catch:{ Exception -> 0x0038 }
        r2 = r3.hashCode;	 Catch:{ Exception -> 0x0038 }
        r1 = r1.getByHashCode(r2);	 Catch:{ Exception -> 0x0038 }
        r3.mGiftPack = r1;	 Catch:{ Exception -> 0x0038 }
        goto L_0x0011;
    L_0x0038:
        r0 = move-exception;
        r1 = r3.TAG;	 Catch:{ all -> 0x0042 }
        com.douban.book.reader.util.Logger.e(r1, r0);	 Catch:{ all -> 0x0042 }
        r3.dismissLoadingDialog();
        goto L_0x002c;
    L_0x0042:
        r1 = move-exception;
        r3.dismissLoadingDialog();
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.douban.book.reader.fragment.GiftPackDetailFragment.loadMeta():void");
    }

    @UiThread
    void updateHeaderView() {
        if (this.mGiftPack != null) {
            setTitle(this.mGiftPack.isMine() ? R.string.title_gift_pack_detail : R.string.title_gift_pack_receive);
            this.mListHeaderView.removeAllViews();
            addCard(LinkImageCard_.build(getContext()).heightResId(R.dimen.banner_height).image(this.mGiftPack.event.subjectImg));
            if (!this.mGiftPack.isMine() && this.mGiftPack.isDepleted()) {
                addCard(((TextCard) ((TextCard) ((TextCard) new TextCard(getContext()).gravity(1).content(Res.getString(R.string.pack_is_depleted_cannot_receive)).textSize(Res.getDimension(R.dimen.general_font_size_normal)).backgroundColor(R.color.gift_page_bg)).autoDimInNightMode()).paddingVerticalResId(R.dimen.general_subview_vertical_padding_large)).noDivider());
            }
            if (!this.mGiftPack.isMine() && this.mGiftPack.isDepleted() && this.mGiftPack.event != null && this.mGiftPack.event.isOnGoing()) {
                if (StringUtils.isNotEmpty(this.mGiftPack.event.url)) {
                    if (StringUtils.isNotEmpty(this.mGiftPack.event.giftPackAdImg)) {
                        addCard(LinkImageCard_.build(getContext()).heightResId(R.dimen.banner_height_large).image(this.mGiftPack.event.giftPackAdImg).uri(this.mGiftPack.event.url).noDivider());
                    }
                }
            }
            addCard(((TextCard) ((TextCard) ((TextCard) new TextCard(getContext()).gravity(1).content(Res.getString(R.string.format_gift_giver_detail, this.mGiftPack.giver.name, this.mGiftPack.getGiftAlias())).bold(true).textSize(Res.getDimension(R.dimen.general_font_size_large)).backgroundColorArray(R.array.gift_page_bg)).paddingTopResId(R.dimen.general_subview_vertical_padding_large)).paddingBottomResId(R.dimen.general_subview_vertical_padding_normal)).noDivider());
            addCard(((GiftMessageCard) ((GiftMessageCard) GiftMessageCard_.build(getContext()).giftPack(this.mGiftPack).backgroundColorArray(R.array.gift_page_bg)).noDivider()).clickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (!GiftPackDetailFragment.this.mGiftPack.isMine()) {
                        return;
                    }
                    if (GiftPackDetailFragment.this.mGiftPack.canEditMessage()) {
                        GiftMessageEditFragment_.builder().packId(GiftPackDetailFragment.this.mGiftPack.id).build().showAsActivity(GiftPackDetailFragment.this);
                    } else {
                        ToastUtils.showToast((int) R.string.toast_gift_pack_message_cannot_be_edited);
                    }
                }
            }));
            addCard(((GiftBoxCard) ((GiftBoxCard) GiftBoxCard_.build(getContext()).giftPack(this.mGiftPack).backgroundColorArray(R.array.gift_page_bg)).noDivider()).contentPaddingTopResId(R.dimen.general_subview_vertical_padding_large));
            addCard(WorksInfoCard_.build(getContext()).worksId(this.mGiftPack.works.id));
            addCard(new TextCard(getContext()).title(Res.getString(R.string.list_header_for_gift, Integer.valueOf(this.mGiftPack.giftCount))));
        }
    }

    public void onEventMainThread(GiftPackUpdatedEvent event) {
        if (event.isValidFor(this.mGiftPack)) {
            refreshSilently();
            loadMeta();
        }
    }

    private <T extends Card<T>> void addCard(Card<T> card) {
        this.mListHeaderView.addView(card);
    }
}
