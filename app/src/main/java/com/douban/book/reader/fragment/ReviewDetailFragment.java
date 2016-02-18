package com.douban.book.reader.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.View;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.BaseArrayAdapter;
import com.douban.book.reader.adapter.ViewBinderAdapter;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.entity.Comment;
import com.douban.book.reader.entity.Review;
import com.douban.book.reader.event.CommentCreatedEvent;
import com.douban.book.reader.event.CommentDeletedEvent;
import com.douban.book.reader.event.ReviewChangedEvent;
import com.douban.book.reader.event.ReviewDeletedEvent;
import com.douban.book.reader.fragment.AlertDialogFragment.Builder;
import com.douban.book.reader.fragment.interceptor.ForcedLoginInterceptor;
import com.douban.book.reader.fragment.share.ShareReviewEditFragment_;
import com.douban.book.reader.manager.Lister;
import com.douban.book.reader.manager.ReviewManager;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.view.ReviewDetailView;
import com.douban.book.reader.view.ReviewDetailView_;
import com.douban.book.reader.view.item.CommentItemView;
import com.douban.book.reader.view.item.CommentItemView.Listener;
import com.douban.book.reader.view.item.CommentItemView_;
import com.mcxiaoke.next.ui.endless.EndlessListView;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.UiThread;

@EFragment
@OptionsMenu({2131623943})
public class ReviewDetailFragment extends BaseEndlessListFragment<Comment> implements Listener {
    private ReviewDetailView mHeaderView;
    @OptionsMenuItem({2131558990})
    MenuItem mMenuItemCreate;
    @OptionsMenuItem({2131558988})
    MenuItem mMenuItemShare;
    private Review mReview;
    @Bean
    ReviewManager mReviewManager;
    @FragmentArg
    int reviewId;
    @FragmentArg
    int worksId;

    /* renamed from: com.douban.book.reader.fragment.ReviewDetailFragment.2 */
    class AnonymousClass2 implements OnClickListener {
        final /* synthetic */ Comment val$comment;

        AnonymousClass2(Comment comment) {
            this.val$comment = comment;
        }

        public void onClick(DialogInterface dialog, int which) {
            ReviewDetailFragment.this.deleteComment(this.val$comment);
        }
    }

    /* renamed from: com.douban.book.reader.fragment.ReviewDetailFragment.1 */
    class AnonymousClass1 extends ViewBinderAdapter<Comment> {
        AnonymousClass1(Context context, Class type) {
            super(context, type);
        }

        protected void bindView(View itemView, Comment data) {
            super.bindView(itemView, data);
            ((CommentItemView) itemView).setListener(ReviewDetailFragment.this);
        }
    }

    public ReviewDetailFragment() {
        setTitle((int) R.string.title_review_detail);
    }

    public Lister<Comment> onCreateLister() {
        return this.mReviewManager.listComments(this.reviewId);
    }

    public BaseArrayAdapter<Comment> onCreateAdapter() {
        return new AnonymousClass1(getActivity(), CommentItemView_.class);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onPrepareOptionsMenu(android.view.Menu r8) {
        /*
        r7 = this;
        r3 = 1;
        r4 = 0;
        super.onPrepareOptionsMenu(r8);
        r2 = 2131558979; // 0x7f0d0243 float:1.874329E38 double:1.0531300636E-314;
        r0 = r8.findItem(r2);
        r2 = 2131558980; // 0x7f0d0244 float:1.8743291E38 double:1.053130064E-314;
        r1 = r8.findItem(r2);
        r2 = r7.mReview;
        if (r2 == 0) goto L_0x001f;
    L_0x0017:
        r2 = r7.mReview;
        r2 = r2.isPostedByMe();
        if (r2 != 0) goto L_0x0055;
    L_0x001f:
        r0.setVisible(r4);
        r1.setVisible(r4);
    L_0x0025:
        r5 = r7.mMenuItemCreate;
        r2 = r7.mReview;
        if (r2 == 0) goto L_0x005c;
    L_0x002b:
        r2 = new java.lang.CharSequence[r3];
        r6 = r7.mReview;
        r6 = r6.content;
        r2[r4] = r6;
        r2 = com.douban.book.reader.util.StringUtils.isNotEmpty(r2);
        if (r2 == 0) goto L_0x005c;
    L_0x0039:
        r2 = r3;
    L_0x003a:
        r5.setVisible(r2);
        r2 = r7.mMenuItemShare;
        r5 = r7.mReview;
        if (r5 == 0) goto L_0x005e;
    L_0x0043:
        r5 = new java.lang.CharSequence[r3];
        r6 = r7.mReview;
        r6 = r6.content;
        r5[r4] = r6;
        r5 = com.douban.book.reader.util.StringUtils.isNotEmpty(r5);
        if (r5 == 0) goto L_0x005e;
    L_0x0051:
        r2.setVisible(r3);
        return;
    L_0x0055:
        r0.setVisible(r3);
        r1.setVisible(r3);
        goto L_0x0025;
    L_0x005c:
        r2 = r4;
        goto L_0x003a;
    L_0x005e:
        r3 = r4;
        goto L_0x0051;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.douban.book.reader.fragment.ReviewDetailFragment.onPrepareOptionsMenu(android.view.Menu):void");
    }

    public void onDelete(Comment comment) {
        Builder dialog = new Builder();
        dialog.setMessage((int) R.string.msg_confirm_to_delete_comment);
        dialog.setPositiveButton((int) R.string.dialog_button_ok, new AnonymousClass2(comment));
        dialog.setNegativeButton((int) R.string.dialog_button_cancel, null);
        dialog.create().show();
    }

    protected void onListViewCreated(EndlessListView listView) {
        super.onListViewCreated(listView);
        this.mHeaderView = ReviewDetailView_.build(getActivity());
        addHeaderView(this.mHeaderView);
        setEmptyHint((int) R.string.hint_empty_comment);
        loadData();
    }

    public void onEventMainThread(ReviewChangedEvent event) {
        if (event.isValidForReview(this.reviewId)) {
            loadData();
        }
    }

    public void onEventMainThread(ReviewDeletedEvent event) {
        if (event.isValidForReviewId(this.reviewId)) {
            finish();
        }
    }

    public void onEventMainThread(CommentCreatedEvent event) {
        if (event.isValidForReviewId(this.reviewId)) {
            replaceLister(this.mReviewManager.listComments(this.reviewId));
        }
    }

    public void onEventMainThread(CommentDeletedEvent event) {
        if (event.isValidForReviewId(this.reviewId)) {
            replaceLister(this.mReviewManager.listComments(this.reviewId));
        }
    }

    @OptionsItem({2131558990})
    void onMenuItemCreateClicked() {
        if (this.mReview != null) {
            ReviewReplyEditFragment_.builder().reviewId(this.mReview.id).build().setShowInterceptor(new ForcedLoginInterceptor()).showAsActivity((Fragment) this);
        }
    }

    @OptionsItem({2131558988})
    void onMenuItemShareClicked() {
        ShareReviewEditFragment_.builder().worksId(this.worksId).reviewId(this.reviewId).build().showAsActivity(PageOpenHelper.from((Fragment) this));
    }

    @OptionsItem({2131558980})
    void onMenuItemEditClicked() {
        ReviewEditFragment_.builder().worksId(this.worksId).build().showAsActivity((Fragment) this);
    }

    @OptionsItem({2131558979})
    void onMenuItemDeleteClicked() {
        Builder dialog = new Builder();
        dialog.setMessage((int) R.string.msg_confirm_to_delete_review);
        dialog.setPositiveButton((int) R.string.dialog_button_ok, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ReviewDetailFragment.this.deleteReview();
            }
        });
        dialog.setNegativeButton((int) R.string.dialog_button_cancel, null);
        dialog.create().show();
    }

    @Background
    void deleteReview() {
        try {
            showBlockingLoadingDialog();
            this.mReviewManager.deleteReview(this.mReview);
            ToastUtils.showToast((int) R.string.toast_general_op_success);
        } catch (Exception e) {
            Logger.e(this.TAG, e);
            ToastUtils.showToast((int) R.string.toast_general_op_failed);
        } finally {
            dismissLoadingDialog();
        }
    }

    @Background
    void deleteComment(Comment comment) {
        try {
            showBlockingLoadingDialog();
            this.mReviewManager.deleteComment(this.reviewId, comment.id);
            ToastUtils.showToast((int) R.string.toast_general_op_success);
        } catch (Exception e) {
            Logger.e(this.TAG, e);
            ToastUtils.showToast((int) R.string.toast_general_op_failed);
        } finally {
            dismissLoadingDialog();
        }
    }

    @Background
    void loadData() {
        try {
            this.mReview = (Review) this.mReviewManager.get((Object) Integer.valueOf(this.reviewId));
            if (this.mReview.worksId > 0) {
                this.worksId = this.mReview.worksId;
            }
            updateHeaderView();
        } catch (Exception e) {
            Logger.e(this.TAG, e);
        }
    }

    @UiThread
    void updateHeaderView() {
        if (this.mReview != null) {
            this.mHeaderView.worksId(this.worksId).reviewId(this.reviewId);
        }
        invalidateOptionsMenu();
    }
}
