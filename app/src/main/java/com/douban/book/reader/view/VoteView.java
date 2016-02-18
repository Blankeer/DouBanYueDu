package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.constant.Char;
import com.douban.book.reader.entity.Review;
import com.douban.book.reader.span.IconFontSpan;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.ViewUtils;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903200)
public class VoteView extends LinearLayout {
    @ViewById(2131558964)
    TextView mBtnVoteDown;
    @ViewById(2131558963)
    TextView mBtnVoteUp;
    private int mVoteDownCount;
    private VoteListener mVoteListener;
    private int mVoteUpCount;
    private boolean mVotedDown;
    private boolean mVotedUp;

    public interface VoteListener {
        void performVote(boolean z);
    }

    public VoteView(Context context) {
        super(context);
    }

    public VoteView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VoteView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @AfterViews
    void init() {
        updateView();
    }

    public void displayForReview(Review review) {
        this.mVoteUpCount = review.usefulCount;
        this.mVoteDownCount = review.uselessCount;
        this.mVotedUp = review.isUpVoted();
        this.mVotedDown = review.isDownVoted();
        updateView();
    }

    public void setVoteListener(VoteListener listener) {
        this.mVoteListener = listener;
    }

    @Click({2131558963, 2131558964})
    void onVoteClicked(View clickedView) {
        performVote(clickedView.getId() == R.id.vote_up);
    }

    @Background
    void performVote(boolean isVoteUp) {
        if (this.mVoteListener != null) {
            disableVoteButtons();
            this.mVoteListener.performVote(isVoteUp);
            enableVoteButtons();
        }
    }

    @UiThread
    void disableVoteButtons() {
        ViewUtils.disable(this.mBtnVoteUp, this.mBtnVoteDown);
    }

    @UiThread
    void enableVoteButtons() {
        ViewUtils.enable(this.mBtnVoteUp, this.mBtnVoteDown);
    }

    private void updateView() {
        this.mBtnVoteUp.setText(getVoteStr(true, this.mVoteUpCount, this.mVotedUp));
        this.mBtnVoteDown.setText(getVoteStr(false, this.mVoteDownCount, this.mVotedDown));
    }

    private CharSequence getVoteStr(boolean isVoteUp, int voteCount, boolean voted) {
        IconFontSpan icon = new IconFontSpan(R.drawable.v_vote_up).ratio(1.5f);
        if (!isVoteUp) {
            icon.verticalFlipped();
        }
        if (voted) {
            int gravity = (isVoteUp ? 80 : 48) | 5;
            icon.badge((int) R.drawable.ic_checkmark);
            icon.badgeGravity(gravity);
        }
        return new RichText().appendIcon(icon).append((char) Char.SPACE).append(String.valueOf(voteCount));
    }
}
