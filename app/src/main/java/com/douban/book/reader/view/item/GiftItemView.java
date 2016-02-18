package com.douban.book.reader.view.item;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.ViewBinder;
import com.douban.book.reader.entity.Gift;
import com.douban.book.reader.fragment.GiftDetailFragment_;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.BoxedWorksView;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903120)
public class GiftItemView extends LinearLayout implements ViewBinder<Gift> {
    @ViewById(2131558761)
    BoxedWorksView mBoxedWorksView;
    @ViewById(2131558762)
    TextView mGiver;

    /* renamed from: com.douban.book.reader.view.item.GiftItemView.1 */
    class AnonymousClass1 implements OnClickListener {
        final /* synthetic */ Gift val$data;

        AnonymousClass1(Gift gift) {
            this.val$data = gift;
        }

        public void onClick(View v) {
            GiftDetailFragment_.builder().uuid(this.val$data.uuid).build().showAsActivity(GiftItemView.this);
        }
    }

    public GiftItemView(Context context) {
        super(context);
    }

    public GiftItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GiftItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @AfterViews
    void init() {
        setOrientation(1);
        this.mBoxedWorksView.isOpened(true);
    }

    public void bindData(Gift data) {
        if (this.mBoxedWorksView != null) {
            this.mBoxedWorksView.worksId(data.works.id);
            this.mBoxedWorksView.setOnClickListener(new AnonymousClass1(data));
        }
        ViewUtils.showText(this.mGiver, Res.getString(R.string.text_giver_name, data.giver.name));
    }
}
