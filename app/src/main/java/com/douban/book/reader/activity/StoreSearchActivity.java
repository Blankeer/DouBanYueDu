package com.douban.book.reader.activity;

import android.app.SearchManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.Tag;
import com.douban.book.reader.event.ArkEvent;
import com.douban.book.reader.fragment.SearchHistoryFragment.onItemClickListener;
import com.douban.book.reader.fragment.SearchHistoryFragment_;
import com.douban.book.reader.fragment.SearchResultFragment;
import com.douban.book.reader.fragment.SearchResultFragment_;
import com.douban.book.reader.fragment.TagFragment.TagLoader;
import com.douban.book.reader.fragment.TagFragment_;
import com.douban.book.reader.manager.SearchHistoryManager;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.panel.Transaction;
import com.douban.book.reader.theme.Theme;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.view.SpinnerSearchView;
import com.douban.book.reader.view.SpinnerSearchView.OnQueryTextListener;
import com.douban.book.reader.view.TagView;
import java.util.ArrayList;
import java.util.List;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.SystemService;

@EActivity(2130903066)
@OptionsMenu({2131623948})
public class StoreSearchActivity extends BaseActivity implements OnQueryTextListener {
    @OptionsMenuItem({2131558991})
    MenuItem mMenuSearch;
    @Bean
    SearchHistoryManager mSearchHistoryManager;
    @SystemService
    SearchManager mSearchManager;
    private SearchResultFragment mSearchResultFragment;
    private SpinnerSearchView mSearchView;
    @Bean
    WorksManager mWorksManager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(Theme.getResId(R.array.page_bg_color));
    }

    protected void initToolbar() {
        super.initToolbar();
        setShowUpButtonOnActionBar(false);
        setShowLogoOnActionBar(false);
        setActionBarShowTitleEnabled(false);
    }

    public void onEventMainThread(ArkEvent event) {
        if (event == ArkEvent.SEARCH_HISTORY_CHANGED && !isQueryTextNotEmpty()) {
            refreshQueryTextEmptyView();
        }
    }

    private void refreshQueryTextEmptyView() {
        if (this.mSearchHistoryManager.isEmpty()) {
            showTagFragment();
        } else {
            showHistoryFragment();
        }
    }

    private boolean isQueryTextNotEmpty() {
        if (this.mSearchView != null) {
            if (StringUtils.isNotEmpty(this.mSearchView.getQuery())) {
                return true;
            }
        }
        return false;
    }

    private void showSearchResult(String query) {
        if (!StringUtils.isNotEmpty(query)) {
            refreshQueryTextEmptyView();
        } else if (this.mSearchResultFragment != null && this.mSearchResultFragment.isVisible() && this.mSearchResultFragment.isAdded()) {
            this.mSearchResultFragment.updateQueryText(query);
        } else {
            this.mSearchResultFragment = SearchResultFragment_.builder().queryText(query).build();
            this.mSearchResultFragment.setScrollListener(new OnScrollListener() {
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if (scrollState == 1 || scrollState == 2) {
                        StoreSearchActivity.this.mSearchView.clearFocus();
                        Utils.hideKeyBoard(StoreSearchActivity.this);
                    }
                }

                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                }
            });
            Transaction.begin((FragmentActivity) this, (int) R.id.frag_container).replace(this.mSearchResultFragment).commit();
        }
    }

    private void showHistoryFragment() {
        Fragment searchHistoryFragment = SearchHistoryFragment_.builder().build();
        searchHistoryFragment.setListItemClickListener(new onItemClickListener() {
            public void onItemClicked(String historyText) {
                if (StoreSearchActivity.this.mSearchView != null) {
                    StoreSearchActivity.this.mSearchView.setQuery(historyText, true);
                }
            }
        });
        Transaction.begin((FragmentActivity) this, (int) R.id.frag_container).replace(searchHistoryFragment).commit();
    }

    private void showTagFragment() {
        Fragment tagFragment = TagFragment_.builder().tagTitle(Res.getString(R.string.title_hot_tags)).build();
        tagFragment.setTagClickedListener(new OnClickListener() {
            public void onClick(View v) {
                if (StoreSearchActivity.this.mSearchView != null) {
                    StoreSearchActivity.this.mSearchView.setQuery(((TagView) v).getEntity().name, true);
                }
            }
        });
        tagFragment.setTagLoader(new TagLoader() {
            public List<Tag> onLoadTag() throws DataLoadException {
                List<Tag> tagList = new ArrayList();
                try {
                    for (String hotWords : StoreSearchActivity.this.mWorksManager.hotSearchWordsList()) {
                        Tag dummyTag = new Tag();
                        dummyTag.name = hotWords;
                        tagList.add(dummyTag);
                    }
                } catch (Exception e) {
                    Logger.e(StoreSearchActivity.this.TAG, e);
                }
                return tagList;
            }
        });
        Transaction.begin((FragmentActivity) this, (int) R.id.frag_container).replace(tagFragment).commit();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        this.mSearchView = (SpinnerSearchView) MenuItemCompat.getActionView(this.mMenuSearch);
        if (this.mSearchView != null) {
            this.mSearchView.setQueryHint(Res.getString(R.string.hint_search_store));
            this.mSearchView.setOnQueryTextListener(this);
            MenuItemCompat.setOnActionExpandListener(this.mMenuSearch, new OnActionExpandListener() {
                public boolean onMenuItemActionExpand(MenuItem item) {
                    item.getActionView().requestFocus();
                    Utils.showKeyBoard(StoreSearchActivity.this, item.getActionView());
                    return true;
                }

                public boolean onMenuItemActionCollapse(MenuItem item) {
                    Utils.hideKeyBoard(StoreSearchActivity.this);
                    StoreSearchActivity.this.finish();
                    return true;
                }
            });
            this.mMenuSearch.expandActionView();
        }
        refreshQueryTextEmptyView();
        return super.onCreateOptionsMenu(menu);
    }

    public void onResume() {
        super.onResume();
        if (this.mSearchView != null) {
            this.mSearchView.requestFocus();
        }
    }

    public void onPause() {
        super.onPause();
        Utils.hideKeyBoard(this);
    }

    public boolean onQueryTextSubmit(String query) {
        if (StringUtils.isNotEmpty(query)) {
            this.mSearchHistoryManager.addNewQuery(query);
            showSearchResult(query);
        }
        return true;
    }

    public boolean onQueryTextChanged(String query) {
        showSearchResult(query);
        return true;
    }
}
