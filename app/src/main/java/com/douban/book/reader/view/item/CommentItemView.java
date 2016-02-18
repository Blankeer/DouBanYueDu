package com.douban.book.reader.view.item;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.douban.book.reader.adapter.ViewBinder;
import com.douban.book.reader.entity.Comment;
import com.douban.book.reader.util.DateUtils;
import com.douban.book.reader.view.UserAvatarView;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903122)
public class CommentItemView extends RelativeLayout implements ViewBinder<Comment> {
    @ViewById(2131558767)
    UserAvatarView mAvatar;
    @ViewById(2131558769)
    TextView mBtnDelete;
    @ViewById(2131558770)
    TextView mContent;
    @ViewById(2131558768)
    TextView mCreatedInfo;
    private Listener mListener;

    /* renamed from: com.douban.book.reader.view.item.CommentItemView.1 */
    class AnonymousClass1 implements OnClickListener {
        final /* synthetic */ Comment val$comment;

        AnonymousClass1(Comment comment) {
            this.val$comment = comment;
        }

        public void onClick(View v) {
            if (CommentItemView.this.mListener != null) {
                CommentItemView.this.mListener.onDelete(this.val$comment);
            }
        }
    }

    public interface Listener {
        void onDelete(Comment comment);
    }

    public CommentItemView(Context context) {
        super(context);
    }

    public CommentItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommentItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    public void bindData(Comment comment) {
        this.mAvatar.displayUserAvatar(comment.user);
        this.mCreatedInfo.setText(String.format("%s %s", new Object[]{comment.user.getDisplayName(), DateUtils.formatDate(comment.createTime)}));
        this.mContent.setText(comment.content);
        if (comment.isPostedByMe()) {
            this.mBtnDelete.setVisibility(0);
            this.mBtnDelete.setOnClickListener(new AnonymousClass1(comment));
            return;
        }
        this.mBtnDelete.setVisibility(8);
    }
}
