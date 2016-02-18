package com.douban.book.reader.view.card;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.View.OnClickListener;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.Gift;
import com.douban.book.reader.entity.GiftPack;
import com.douban.book.reader.util.PaintUtils;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.view.GiftMessageView;
import com.douban.book.reader.view.UserAvatarView;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup
public class GiftMessageCard extends Card<GiftMessageCard> {
    @ViewById(2131558535)
    UserAvatarView mAvatarView;
    @ViewById(2131558536)
    GiftMessageView mGiftMessageView;

    public GiftMessageCard(Context context) {
        super(context);
        setWillNotDraw(false);
        content((int) R.layout.card_gift_message);
        paddingHorizontalResId(R.dimen.general_subview_horizontal_padding_large);
        paddingVerticalResId(R.dimen.general_subview_vertical_padding_normal);
    }

    public GiftMessageCard gift(Gift gift) {
        if (gift != null) {
            this.mAvatarView.displayUserAvatar(gift.giver);
            this.mGiftMessageView.giver(gift.giver.name).recipient(gift.recipient.name).message(gift.message).messageDate(gift.createTime);
        }
        return this;
    }

    public GiftMessageCard giftPack(GiftPack giftPack) {
        if (giftPack != null) {
            this.mAvatarView.displayUserAvatar(giftPack.giver);
            this.mGiftMessageView.message(giftPack.message);
        }
        return this;
    }

    public GiftMessageCard clickListener(OnClickListener clickListener) {
        super.clickListener(clickListener);
        this.mGiftMessageView.setOnClickListener(clickListener);
        return this;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = PaintUtils.obtainStrokePaint(Res.getColor(R.color.gift_page_stroke_color));
        canvas.drawRect((float) getPaddingLeft(), (float) (getPaddingTop() + Utils.dp2pixel(15.0f)), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - getPaddingBottom()), paint);
        paint.setStyle(Style.FILL);
        paint.setColor(Res.getColor(R.array.page_highlight_bg_color));
        canvas.drawRect((float) getPaddingLeft(), (float) (getPaddingTop() + Utils.dp2pixel(15.0f)), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - getPaddingBottom()), paint);
        PaintUtils.recyclePaint(paint);
    }
}
