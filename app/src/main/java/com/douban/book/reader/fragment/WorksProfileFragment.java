package com.douban.book.reader.fragment;

import android.support.v4.app.Fragment;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.entity.GiftEvent;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.fragment.share.ShareWorksEditFragment_;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.view.LoadErrorPageView.RefreshClickListener;
import com.douban.book.reader.view.ParagraphView.Indent;
import com.douban.book.reader.view.WorksTagsCard;
import com.douban.book.reader.view.card.ColumnInfoCard_;
import com.douban.book.reader.view.card.ColumnTocCard_;
import com.douban.book.reader.view.card.ReviewCard;
import com.douban.book.reader.view.card.SimilarWorksCard_;
import com.douban.book.reader.view.card.TextCard;
import com.douban.book.reader.view.card.WorksActionCard_;
import com.douban.book.reader.view.card.WorksAgentCard_;
import com.douban.book.reader.view.card.WorksGiftEventCard_;
import com.douban.book.reader.view.card.WorksPreReleaseCard_;
import com.douban.book.reader.view.card.WorksPurchaseCard_;
import com.douban.book.reader.view.card.WorksSummaryCard;
import com.douban.book.reader.view.card.WorksTocCard_;
import com.douban.book.reader.view.card.WorksUgcCard_;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;

@EFragment
@OptionsMenu({2131623951})
public class WorksProfileFragment extends BaseCardFragment {
    @FragmentArg
    int legacyColumnId;
    private boolean mIsRefreshing;
    private WorksManager mWorksManager;
    @FragmentArg
    int worksId;

    public WorksProfileFragment() {
        this.mWorksManager = WorksManager.getInstance();
        this.mIsRefreshing = false;
    }

    @AfterViews
    public void init() {
        setTitle((int) R.string.title_works_profile);
        enablePullToRefresh(true);
        loadWorks();
    }

    @OptionsItem({2131558988})
    void onMenuItemShare() {
        ShareWorksEditFragment_.builder().worksId(this.worksId).build().showAsActivity(PageOpenHelper.from((Fragment) this));
    }

    public void onRefresh() {
        if (!isRefreshing()) {
            this.mIsRefreshing = true;
            loadWorks();
        }
    }

    @Background
    void loadWorks() {
        if (isRefreshing()) {
            showBlockingLoadingDialog();
        } else {
            showLoadingDialog();
        }
        try {
            Works works;
            if (this.worksId == 0 && this.legacyColumnId > 0) {
                works = this.mWorksManager.getWorksByLegacyColumnId(this.legacyColumnId);
                this.worksId = works.id;
            } else if (isRefreshing()) {
                works = this.mWorksManager.getFromRemote(Integer.valueOf(this.worksId));
            } else {
                works = this.mWorksManager.getWorks(this.worksId);
            }
            updateView(works);
            if (isRefreshing()) {
                ToastUtils.showToast((int) R.string.toast_general_refresh_success);
            }
            this.mIsRefreshing = false;
            dismissLoadingDialog();
        } catch (Throwable e) {
            if (isRefreshing()) {
                ToastUtils.showToast(e, (int) R.string.toast_general_refresh_failed);
            } else {
                loadFailed(e);
            }
            this.mIsRefreshing = false;
            dismissLoadingDialog();
        } catch (Throwable th) {
            this.mIsRefreshing = false;
            dismissLoadingDialog();
        }
    }

    @UiThread
    void loadFailed(DataLoadException e) {
        showLoadErrorPage(e, new RefreshClickListener() {
            public void onClick() {
                WorksProfileFragment.this.loadWorks();
            }
        });
        Logger.e(this.TAG, e);
    }

    @UiThread
    void updateView(Works works) {
        removeAllCards();
        addCard(new WorksSummaryCard(App.get()).works(works).noDivider());
        addCardIf(works.isContentReady(), WorksActionCard_.build(App.get()).worksId(this.worksId).noDivider());
        addCardIf(works.isOnPre, WorksPreReleaseCard_.build(App.get()).worksId(works.id));
        addCardIf(works.isSalable, WorksPurchaseCard_.build(App.get()).worksId(this.worksId));
        addCardIf(works.hasOwned, WorksUgcCard_.build(App.get()).worksId(this.worksId));
        addCardIf(works.isColumnOrSerial(), ColumnInfoCard_.build(App.get()).worksId(works.id));
        if (works.giftEvents != null) {
            for (GiftEvent event : works.giftEvents) {
                addCard(WorksGiftEventCard_.build(App.get()).event(event, this.worksId));
            }
        }
        addCard(((TextCard) new TextCard(App.get()).title((int) R.string.card_title_abstract)).content(works.abstractText).firstLineIndent(Indent.ALL).expandable());
        if (works.isContentReady()) {
            if (works.isColumnOrSerial()) {
                addCard(ColumnTocCard_.build(App.get()).worksId(this.worksId).title(works.isSerial() ? R.string.card_title_serial_toc : R.string.card_title_column_toc));
            } else {
                addCard(((TextCard) WorksTocCard_.build(App.get()).worksId(this.worksId).title((int) R.string.card_title_toc)).expandable());
            }
        }
        addCard(WorksAgentCard_.build(App.get()).agentId(works.agentId));
        addCardIf(!works.isOnPre, ((ReviewCard) new ReviewCard(App.get()).title((int) R.string.card_title_reviews)).worksId(this.worksId));
        addCard(((WorksTagsCard) new WorksTagsCard(App.get()).title((int) R.string.card_title_works_tags)).worksId(this.worksId));
        addCard(SimilarWorksCard_.build(App.get()).worksId(this.worksId));
    }

    private boolean isRefreshing() {
        return this.mIsRefreshing;
    }
}
