package com.douban.book.reader.view.card;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.Gift;
import com.douban.book.reader.entity.GiftPack;
import com.douban.book.reader.fragment.WorksProfileFragment_;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.BoxedWorksView;
import com.douban.book.reader.view.BoxedWorksView_;
import org.androidannotations.annotations.EViewGroup;

@EViewGroup
public class GiftBoxCard extends Card<GiftBoxCard> {
    private BoxedWorksView mBoxedWorksView;

    /* renamed from: com.douban.book.reader.view.card.GiftBoxCard.1 */
    class AnonymousClass1 implements OnClickListener {
        final /* synthetic */ Gift val$gift;

        AnonymousClass1(Gift gift) {
            this.val$gift = gift;
        }

        public void onClick(View v) {
            WorksProfileFragment_.builder().worksId(this.val$gift.works.id).build().showAsActivity(GiftBoxCard.this);
        }
    }

    /* renamed from: com.douban.book.reader.view.card.GiftBoxCard.2 */
    class AnonymousClass2 implements OnClickListener {
        final /* synthetic */ GiftPack val$giftPack;

        AnonymousClass2(GiftPack giftPack) {
            this.val$giftPack = giftPack;
        }

        public void onClick(View v) {
            WorksProfileFragment_.builder().worksId(this.val$giftPack.works.id).build().showAsActivity(GiftBoxCard.this);
        }
    }

    public GiftBoxCard(Context context) {
        super(context);
    }

    private void addBoxView(int height) {
        this.mBoxedWorksView = BoxedWorksView_.build(getContext());
        ViewUtils.of(this.mBoxedWorksView).widthMatchParent().height(height).commit();
        content(this.mBoxedWorksView);
    }

    public GiftBoxCard gift(Gift gift) {
        addBoxView(Res.getDimensionPixelSize(R.dimen.boxed_gift_view_height));
        this.mBoxedWorksView.worksId(gift.works.id).isOpened(true).showBoxCover(true);
        this.mBoxedWorksView.setOnClickListener(new AnonymousClass1(gift));
        return this;
    }

    public GiftBoxCard giftPack(GiftPack giftPack) {
        addBoxView(Res.getDimensionPixelSize(R.dimen.boxed_gift_view_height_large));
        ViewUtils.of(this.mBoxedWorksView).bottomMargin(-Res.getDimensionPixelSize(R.dimen.general_subview_vertical_padding_medium)).commit();
        this.mBoxedWorksView.worksId(giftPack.works.id).isOpened(false);
        this.mBoxedWorksView.setOnClickListener(new AnonymousClass2(giftPack));
        return this;
    }
}
