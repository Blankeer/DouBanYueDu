package com.douban.book.reader.fragment;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.BaseArrayAdapter;
import com.douban.book.reader.adapter.ViewBinderAdapter;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.manager.Lister;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.view.CoverLeftWorksView;
import com.douban.book.reader.view.CoverLeftWorksView_;
import com.mcxiaoke.next.ui.endless.EndlessListView;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ItemClick;

@EFragment
public class SearchResultFragment extends BaseEndlessListFragment<Works> {
    private OnScrollListener mScrollListener;
    @Bean
    WorksManager mWorksManager;
    @FragmentArg
    String queryText;

    /* renamed from: com.douban.book.reader.fragment.SearchResultFragment.1 */
    class AnonymousClass1 extends ViewBinderAdapter<Works> {
        AnonymousClass1(Class type) {
            super(type);
        }

        protected void bindView(View itemView, Works data) {
            super.bindView(itemView, data);
            ((CoverLeftWorksView) itemView).showAbstract();
            ((CoverLeftWorksView) itemView).showRatingInfo(true);
        }
    }

    public Lister<Works> onCreateLister() {
        return getLister(this.queryText);
    }

    public BaseArrayAdapter<Works> onCreateAdapter() {
        return new AnonymousClass1(CoverLeftWorksView_.class);
    }

    protected void onListViewCreated(EndlessListView listView) {
        listView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (SearchResultFragment.this.mScrollListener != null) {
                    SearchResultFragment.this.mScrollListener.onScrollStateChanged(view, scrollState);
                }
            }

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (SearchResultFragment.this.mScrollListener != null) {
                    SearchResultFragment.this.mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }
            }
        });
        setEmptyHint((int) R.string.hint_empty_search_result);
    }

    public void setScrollListener(OnScrollListener listener) {
        this.mScrollListener = listener;
    }

    @ItemClick({2131558593})
    void onWorksItemClicked(Works works) {
        if (works != null) {
            WorksProfileFragment_.builder().worksId(works.id).build().showAsActivity((Fragment) this);
        }
    }

    public void updateQueryText(String queryText) {
        this.queryText = queryText;
        replaceLister(getLister(queryText));
    }

    private Lister<Works> getLister(String keyword) {
        return this.mWorksManager.searchStore(keyword);
    }
}
