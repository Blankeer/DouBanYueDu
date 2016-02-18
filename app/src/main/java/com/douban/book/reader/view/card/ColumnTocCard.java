package com.douban.book.reader.view.card;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import com.douban.book.reader.R;
import com.douban.book.reader.fragment.ColumnChapterReaderFragment_;
import com.douban.book.reader.fragment.TocFragment_;
import com.douban.book.reader.location.Toc;
import com.douban.book.reader.location.TocItem;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.item.TocItemView;
import com.douban.book.reader.view.item.TocItemView_;
import java.util.List;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;

@EViewGroup
public class ColumnTocCard extends Card<ColumnTocCard> {
    private static final int MAX_TOC_ITEMS_TO_SHOW = 5;
    private Button mBtnBottom;
    private LinearLayout mItemsLayoutBase;
    private int mWorksId;

    /* renamed from: com.douban.book.reader.view.card.ColumnTocCard.1 */
    class AnonymousClass1 implements OnClickListener {
        final /* synthetic */ TocItem val$tocItem;

        AnonymousClass1(TocItem tocItem) {
            this.val$tocItem = tocItem;
        }

        public void onClick(View v) {
            ColumnChapterReaderFragment_.builder().worksId(this.val$tocItem.worksId).chapterId(this.val$tocItem.packageId).build().showAsActivity(ColumnTocCard.this);
        }
    }

    public ColumnTocCard(Context context) {
        super(context);
        content((int) R.layout.card_item_list_with_bottom_button);
    }

    public ColumnTocCard worksId(int worksId) {
        this.mWorksId = worksId;
        loadData();
        return this;
    }

    @AfterViews
    void init() {
        ViewUtils.setBottomPaddingResId(this, R.dimen.general_subview_vertical_padding_medium);
        ViewUtils.setEventAware(this);
        this.mItemsLayoutBase = (LinearLayout) findViewById(R.id.item_list_layout_base);
        this.mBtnBottom = (Button) findViewById(R.id.btn_bottom);
        this.mBtnBottom.setText(RichText.textWithIcon((int) R.drawable.v_toc, (int) R.string.btn_all_chapters));
        noContentPadding();
    }

    @Click({2131558538})
    void onBottomButtonClicked() {
        TocFragment_.builder().worksId(this.mWorksId).referToWorksData(false).build().showAsActivity((View) this);
    }

    @UiThread
    void updateContent(List<TocItem> tocItemList) {
        boolean z;
        if (tocItemList == null || tocItemList.isEmpty()) {
            onLoadFailed();
        } else {
            this.mBtnBottom.setText(RichText.textWithIcon((int) R.drawable.v_toc, Res.getString(R.string.btn_full_toc, Integer.valueOf(tocItemList.size()))));
            for (TocItem tocItem : tocItemList.subList(0, Math.min(MAX_TOC_ITEMS_TO_SHOW, tocItemList.size()))) {
                TocItemView tocItemView = TocItemView_.build(getContext());
                tocItemView.setReferToWorksData(false);
                tocItemView.bindData(tocItem);
                this.mItemsLayoutBase.addView(tocItemView);
                tocItemView.setOnClickListener(new AnonymousClass1(tocItem));
            }
        }
        if (tocItemList == null || tocItemList.size() <= MAX_TOC_ITEMS_TO_SHOW) {
            z = true;
        } else {
            z = false;
        }
        ViewUtils.goneIf(z, this.mBtnBottom);
    }

    @Background
    void loadData() {
        try {
            updateContent(Toc.get(this.mWorksId).getTocList());
        } catch (Exception e) {
            Logger.e(this.TAG, e);
            onLoadFailed();
        }
    }

    @UiThread
    void onLoadFailed() {
        hide();
    }
}
