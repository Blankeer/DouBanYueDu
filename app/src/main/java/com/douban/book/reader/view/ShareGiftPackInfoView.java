package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.Gift;
import com.douban.book.reader.entity.GiftPack;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ViewUtils;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903184)
public class ShareGiftPackInfoView extends LinearLayout {
    @ViewById(2131558761)
    BoxedWorksView mGiftBox;
    @ViewById(2131558518)
    TextView mMessage;
    @ViewById(2131558462)
    TextView mTitle;

    public ShareGiftPackInfoView(Context context) {
        super(context);
    }

    public ShareGiftPackInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShareGiftPackInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @AfterViews
    void init() {
        setOrientation(0);
    }

    public ShareGiftPackInfoView setGiftPack(GiftPack giftPack) {
        if (giftPack != null) {
            this.mGiftBox.worksId(giftPack.works.id).isOpened(false);
            ViewUtils.showTextIfNotEmpty(this.mTitle, giftPack.works.titleWithQuote());
            ViewUtils.showTextIfNotEmpty(this.mMessage, giftPack.message);
        }
        return this;
    }

    public ShareGiftPackInfoView setGift(Gift gift) {
        if (gift != null) {
            this.mGiftBox.worksId(gift.works.id).isOpened(false);
            ViewUtils.showTextIfNotEmpty(this.mTitle, Res.getString(R.string.title_for_shared_gift_simple, gift.giver.name, gift.works.title));
            ViewUtils.showTextIfNotEmpty(this.mMessage, gift.message);
        }
        return this;
    }
}
