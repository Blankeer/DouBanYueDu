package com.douban.book.reader.view.panel;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.content.Book;
import com.douban.book.reader.event.PagingEndedEvent;
import com.douban.book.reader.location.Toc;
import com.douban.book.reader.task.PagingTaskManager;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.Utils;
import java.util.List;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903140)
public class SeekTipView extends LinearLayout {
    @ViewById(2131558834)
    View mOverlay;
    @ViewById(2131558837)
    View mOverlayGuide;
    @ViewById(2131558836)
    LinearLayout mOverlayIndex;
    @ViewById(2131558835)
    HorizontalScrollView mOverlayIndexScroller;
    @ViewById(2131558838)
    TextView mOverlayProgress;
    @ViewById(2131558839)
    TextView mOverlayTitle;
    private boolean mScrollByChapter;
    private int mWorksId;

    public SeekTipView(Context context) {
        super(context);
        this.mScrollByChapter = false;
    }

    public SeekTipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mScrollByChapter = false;
    }

    public SeekTipView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mScrollByChapter = false;
    }

    public void setWorksId(int worksId) {
        this.mWorksId = worksId;
        if (!PagingTaskManager.isPaging(this.mWorksId)) {
            updateIndexView();
        }
    }

    public void onEventMainThread(PagingEndedEvent event) {
        updateIndexView();
    }

    public void updateTip(int page) {
        Book book = Book.get(this.mWorksId);
        String title = Toc.get(book.getBookId()).getTitleForPage(page);
        if (!TextUtils.isEmpty(title)) {
            this.mOverlayTitle.setText(title);
        }
        if (this.mScrollByChapter) {
            List<Integer> tocPageArray = Toc.get(this.mWorksId).getTocPageArray();
            int rightMost = 0;
            int count = tocPageArray.size();
            int index = 1;
            for (int i = 0; i < count; i++) {
                TextView tv = (TextView) this.mOverlayIndex.getChildAt(i);
                if (tv != null) {
                    if (page == ((Integer) tocPageArray.get(i)).intValue()) {
                        tv.setBackgroundColor(-7829368);
                        int right = tv.getRight();
                        if (right > rightMost) {
                            rightMost = right;
                        }
                        index = i + 1;
                    } else {
                        tv.setBackgroundColor(getResources().getColor(R.color.reader_overlay_chapter_index_bg));
                    }
                }
            }
            this.mOverlayIndexScroller.scrollTo(Math.max(0, rightMost - this.mOverlay.getRight()), 0);
            this.mOverlayProgress.setText(Res.getString(R.string.text_reader_overlay_chapter_progress, Integer.valueOf(index), Integer.valueOf(count)));
            return;
        }
        if (book.getPageCount() - 1 > 0) {
            this.mOverlayProgress.setText(String.format("%d%%", new Object[]{Integer.valueOf((page * 100) / pageCount)}));
        }
    }

    public void changeScrollMode(boolean isScrollByChapter) {
        this.mScrollByChapter = isScrollByChapter;
        if (isScrollByChapter) {
            this.mOverlayIndex.setVisibility(0);
            this.mOverlayGuide.setVisibility(8);
            return;
        }
        this.mOverlayIndex.setVisibility(8);
        this.mOverlayGuide.setVisibility(0);
    }

    private void updateIndexView() {
        this.mOverlayIndex.removeAllViews();
        LayoutParams params = new LayoutParams(Utils.dp2pixel(24.0f), -1);
        int textColor = Res.getColor(R.color.reader_overlay_chapter_index_fg);
        for (int i = 1; i <= Toc.get(this.mWorksId).getTocItemCount(); i++) {
            TextView tv = new TextView(App.get());
            tv.setText(String.valueOf(i));
            tv.setTextSize(1, 8.0f);
            tv.setTextColor(textColor);
            tv.setGravity(17);
            this.mOverlayIndex.addView(tv, params);
        }
        Utils.changeFonts(this.mOverlayIndex);
    }
}
