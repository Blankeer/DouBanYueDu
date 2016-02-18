package com.douban.book.reader.view.store.item;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.ViewBinder;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.fragment.WorksProfileFragment_;
import com.douban.book.reader.util.DebugSwitch;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.view.WorksCoverView;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903197)
public class StoreWorksItemView extends RelativeLayout implements ViewBinder<Works> {
    @ViewById(2131558774)
    TextView mAuthor;
    @ViewById(2131558772)
    WorksCoverView mCover;
    private boolean mShowPrice;
    @ViewById(2131558462)
    TextView mTitle;

    /* renamed from: com.douban.book.reader.view.store.item.StoreWorksItemView.1 */
    class AnonymousClass1 implements OnClickListener {
        final /* synthetic */ Works val$works;

        AnonymousClass1(Works works) {
            this.val$works = works;
        }

        public void onClick(View v) {
            WorksProfileFragment_.builder().worksId(this.val$works.id).build().showAsActivity(StoreWorksItemView.this);
        }
    }

    public StoreWorksItemView(Context context) {
        super(context);
        init();
    }

    public StoreWorksItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StoreWorksItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setGravity(1);
    }

    void setWorks(Works works) {
        if (works != null) {
            this.mCover.works(works);
            this.mTitle.setText(works.title);
            if (DebugSwitch.on(Key.APP_DEBUG_SHOW_BOOK_IDS)) {
                this.mTitle.setText(String.format("%s %s", new Object[]{Integer.valueOf(works.id), works.title}));
            }
            CharSequence info = null;
            if (this.mShowPrice && !works.isFree()) {
                info = works.formatPriceWithColor();
            }
            if (StringUtils.isEmpty(info)) {
                info = Res.getString(R.string.title_author, works.author);
            }
            this.mAuthor.setText(info);
            setOnClickListener(new AnonymousClass1(works));
        }
    }

    public void showPrice(boolean showPrice) {
        this.mShowPrice = showPrice;
    }

    public void bindData(Works data) {
        setWorks(data);
    }
}
