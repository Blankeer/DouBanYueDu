package com.douban.book.reader.view.item;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.ViewBinder;
import com.douban.book.reader.entity.GiftPack;
import com.douban.book.reader.fragment.GiftPackDetailFragment_;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.BoxedWorksView;
import com.douban.book.reader.view.BoxedWorksView_;

public class GiftPackItemView extends FrameLayout implements ViewBinder<GiftPack> {
    private BoxedWorksView mBoxedWorksView;

    /* renamed from: com.douban.book.reader.view.item.GiftPackItemView.1 */
    class AnonymousClass1 implements OnClickListener {
        final /* synthetic */ GiftPack val$data;

        AnonymousClass1(GiftPack giftPack) {
            this.val$data = giftPack;
        }

        public void onClick(View v) {
            GiftPackDetailFragment_.builder().packId(this.val$data.id).build().showAsActivity(GiftPackItemView.this);
        }
    }

    public GiftPackItemView(Context context) {
        super(context);
        init(context);
    }

    public GiftPackItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GiftPackItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    void init(Context context) {
        this.mBoxedWorksView = BoxedWorksView_.build(context);
        ViewUtils.of(this.mBoxedWorksView).widthWrapContent().height(Res.getDimensionPixelSize(R.dimen.boxed_gift_view_height)).commit();
        this.mBoxedWorksView.isOpened(false);
        addView(this.mBoxedWorksView);
    }

    public void bindData(GiftPack data) {
        if (this.mBoxedWorksView != null) {
            this.mBoxedWorksView.worksId(data.works.id).showQuantity(data.quantity).isDepleted(data.isDepleted());
            this.mBoxedWorksView.setOnClickListener(new AnonymousClass1(data));
        }
    }
}
