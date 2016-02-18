package com.douban.book.reader.view.panel;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.douban.book.reader.R;
import com.douban.book.reader.content.Book;
import com.douban.book.reader.entity.Progress;
import com.douban.book.reader.event.PagingStatusChangedEvent;
import com.douban.book.reader.event.ReadingProgressUpdatedEvent;
import com.douban.book.reader.location.Toc;
import com.douban.book.reader.manager.ProgressManager;
import com.douban.book.reader.task.PagingTaskManager;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.IndexedSeekBar;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903139)
public class SeekBarView extends LinearLayout {
    @ViewById(2131558831)
    LinearLayout mBtnJump;
    @ViewById(2131558832)
    ImageView mBtnJumpImage;
    private boolean mJumpedToHistory;
    private ProgressManager mProgressManager;
    @ViewById(2131558833)
    IndexedSeekBar mSeekBar;
    private int mWorksId;

    public SeekBarView(Context context) {
        super(context);
        this.mJumpedToHistory = false;
    }

    public SeekBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mJumpedToHistory = false;
    }

    public SeekBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mJumpedToHistory = false;
    }

    public void setWorksId(int worksId) {
        this.mWorksId = worksId;
        init();
    }

    void init() {
        this.mProgressManager = ProgressManager.ofWorks(this.mWorksId);
        ThemedAttrs.ofView(this.mBtnJump).append(R.attr.backgroundDrawableArray, Integer.valueOf(R.array.reader_btn_jump_bg)).updateView();
        updateSeekBar();
        updateJumpButton();
        ViewUtils.setEventAware(this);
    }

    @Click({2131558831})
    void onJumpBtnClicked() {
        this.mProgressManager.toggleHistoryProgress();
        this.mJumpedToHistory = !this.mJumpedToHistory;
        updateJumpButton();
    }

    public void onEventMainThread(ReadingProgressUpdatedEvent event) {
        updateSeekBar();
        updateJumpButton();
    }

    public void onEventMainThread(PagingStatusChangedEvent event) {
        if (event.isValidFor(this.mWorksId)) {
            updateSeekBar();
            updateSeekBarIndex();
        }
    }

    private void updateSeekBar() {
        if (this.mSeekBar != null) {
            if (PagingTaskManager.isPaging(this.mWorksId)) {
                this.mSeekBar.setEnabled(false);
                this.mSeekBar.setMax(0);
                return;
            }
            int currentPage;
            Book book = Book.get(this.mWorksId);
            Progress progress = this.mProgressManager.getLocalProgress();
            int maxProgress = book.getPageCount() - 1;
            if (progress == null) {
                currentPage = 0;
            } else {
                currentPage = book.getPageForPosition(progress.getPosition());
            }
            currentPage = Math.min(maxProgress, Math.max(0, currentPage));
            this.mSeekBar.setMax(maxProgress);
            this.mSeekBar.setProgress(currentPage);
            this.mSeekBar.setEnabled(true);
        }
    }

    private void updateSeekBarIndex() {
        if (this.mSeekBar != null) {
            this.mSeekBar.setIndexes(Toc.get(this.mWorksId).getTocPageArray());
        }
    }

    private void updateJumpButton() {
        ThemedAttrs.ofView(this.mBtnJumpImage).append(R.attr.srcArray, Integer.valueOf(this.mJumpedToHistory ? R.array.reader_btn_jump_to_next : R.array.reader_btn_jump_to_prev)).updateView();
        ViewUtils.visibleIf(this.mProgressManager.hasHistoryProgress(), this.mBtnJump);
    }
}
