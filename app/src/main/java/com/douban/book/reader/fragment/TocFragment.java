package com.douban.book.reader.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ListView;
import com.douban.book.reader.R;
import com.douban.book.reader.activity.ReaderActivity_;
import com.douban.book.reader.activity.ReaderActivity_.IntentBuilder_;
import com.douban.book.reader.adapter.BaseArrayAdapter;
import com.douban.book.reader.adapter.ViewBinderAdapter;
import com.douban.book.reader.app.App;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.content.Book;
import com.douban.book.reader.content.chapter.LastPageChapter;
import com.douban.book.reader.content.page.Position;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.location.Toc;
import com.douban.book.reader.location.TocItem;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.view.item.TocItemView;
import com.douban.book.reader.view.item.TocItemView_;
import java.util.List;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ItemClick;

@EFragment
public class TocFragment extends BaseListFragment<TocItem> {
    private int mSelectedIndex;
    private boolean mShouldShowNoPurchaseSign;
    private Works mWorks;
    @Bean
    WorksManager mWorksManager;
    @FragmentArg
    boolean referToWorksData;
    @FragmentArg
    int worksId;

    /* renamed from: com.douban.book.reader.fragment.TocFragment.1 */
    class AnonymousClass1 extends ViewBinderAdapter<TocItem> {
        AnonymousClass1(Context context, Class type) {
            super(context, type);
        }

        protected void bindView(View itemView, TocItem data) {
            ((TocItemView) itemView).setReferToWorksData(TocFragment.this.referToWorksData);
            ((TocItemView) itemView).setShouldShowNotPurchasedSign(TocFragment.this.mShouldShowNoPurchaseSign);
            super.bindView(itemView, data);
        }
    }

    public TocFragment() {
        this.referToWorksData = true;
        this.mShouldShowNoPurchaseSign = false;
        setTitle((int) R.string.panel_tab_book_catalog);
    }

    public List<TocItem> onLoadData() {
        List<TocItem> tocItems = Toc.get(this.worksId).getTocList();
        this.mSelectedIndex = tocItems.indexOf(Toc.get(this.worksId).getTocItemForReadingProgress());
        try {
            this.mWorks = this.mWorksManager.getWorks(this.worksId);
            if (this.mWorks != null) {
                this.mShouldShowNoPurchaseSign = this.mWorks.isColumnOrSerial();
            }
        } catch (DataLoadException e) {
        }
        return tocItems;
    }

    public BaseArrayAdapter<TocItem> onCreateAdapter() {
        return new AnonymousClass1(getActivity(), TocItemView_.class);
    }

    @ItemClick({2131558593})
    void onItemClicked(TocItem tocItem) {
        if (this.referToWorksData) {
            Position position = tocItem.getPosition();
            if (!Position.isValid(position)) {
                Book book = Book.get(this.worksId);
                position = book.getPositionForChapter(book.getChapterByType(LastPageChapter.class));
            }
            if (Position.isValid(position)) {
                PageOpenHelper.from((Fragment) this).open(((IntentBuilder_) ReaderActivity_.intent((Fragment) this).flags(67108864)).mBookId(this.worksId).positionToShow(position).get());
                return;
            }
            return;
        }
        ColumnChapterReaderFragment_.builder().worksId(tocItem.worksId).chapterId(tocItem.packageId).build().showAsActivity((Fragment) this);
    }

    protected void onListViewCreated(ListView listView) {
        setEmptyHint((int) R.string.hint_empty_toc);
    }

    protected void onLoadSucceed() {
        this.mListView.smoothScrollToPositionFromTop(this.mSelectedIndex, App.get().getPageHeight() / 2, 0);
        if (this.mWorks != null && !this.referToWorksData && this.mWorks.isColumnOrSerial()) {
            setTitle(Res.getString(this.mWorks.isSerial() ? R.string.title_for_serial_toc : R.string.title_for_column_toc, this.mWorks.title));
        }
    }
}
