package com.douban.book.reader.fragment;

import android.app.SearchManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v7.app.ActionBar;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.activity.HomeActivity;
import com.douban.book.reader.adapter.ShelfAdapter;
import com.douban.book.reader.app.App;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.entity.ShelfItem;
import com.douban.book.reader.event.NavigationDrawerClosedEvent;
import com.douban.book.reader.event.NavigationDrawerOpenedEvent;
import com.douban.book.reader.event.NewShelfItemAddedEvent;
import com.douban.book.reader.event.ShelfClearedEvent;
import com.douban.book.reader.helper.BookItemClickHelper;
import com.douban.book.reader.manager.ShelfManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.SpinnerSearchView;
import com.douban.book.reader.view.SpinnerSearchView.OnQueryTextListener;
import io.realm.internal.Table;
import java.util.Collection;
import java.util.List;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

@EFragment(2130903107)
@OptionsMenu({2131623945})
public class ShelfFragment extends BaseFragment implements OnQueryTextListener {
    private static final String TAG;
    private ActionMode mActionMode;
    private ShelfAdapter mAdapter;
    private BookItemClickHelper mBookItemClickHelper;
    private SparseBooleanArray mCheckedItemList;
    @ViewById(2131558640)
    GridView mGridView;
    @OptionsMenuItem({2131558991})
    MenuItem mMenuSearch;
    @ViewById(2131558646)
    Button mOpenMyWorks;
    @ViewById(2131558645)
    Button mOpenStore;
    @ViewById(2131558641)
    TextView mSearchEmptyView;
    @SystemService
    SearchManager mSearchManager;
    @ViewById(2131558642)
    View mShelfEmptyView;
    @Bean
    ShelfManager mShelfManager;

    public class MultiChoiceModeListener implements android.widget.AbsListView.MultiChoiceModeListener {
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            if (inflater != null) {
                inflater.inflate(R.menu.shelf_context, menu);
            }
            ShelfFragment.this.mActionMode = mode;
            if (ShelfFragment.this.mAdapter != null) {
                ShelfFragment.this.mAdapter.setMultiChoiceMode(true);
            }
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete /*2131558979*/:
                    ShelfFragment.this.removeBooksFromShelf(ShelfFragment.this.mGridView.getCheckedItemIds());
                    ShelfFragment.this.updateShelfEmptyView();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        public void onDestroyActionMode(ActionMode mode) {
            ShelfFragment.this.mActionMode = null;
            if (ShelfFragment.this.mAdapter != null) {
                ShelfFragment.this.mAdapter.setMultiChoiceMode(false);
            }
        }

        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            mode.setTitle(Res.getString(R.string.action_title_selected_n_items, Integer.valueOf(ShelfFragment.this.mGridView.getCheckedItemCount())));
        }
    }

    /* renamed from: com.douban.book.reader.fragment.ShelfFragment.1 */
    class AnonymousClass1 implements OnActionExpandListener {
        final /* synthetic */ SpinnerSearchView val$searchView;

        AnonymousClass1(SpinnerSearchView spinnerSearchView) {
            this.val$searchView = spinnerSearchView;
        }

        public boolean onMenuItemActionExpand(MenuItem item) {
            ActionBar actionBar = ShelfFragment.this.getActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(false);
                actionBar.setDisplayShowHomeEnabled(false);
                actionBar.setDisplayShowTitleEnabled(false);
            }
            item.getActionView().requestFocus();
            Utils.showKeyBoard(ShelfFragment.this.getActivity(), item.getActionView());
            return true;
        }

        public boolean onMenuItemActionCollapse(MenuItem item) {
            ActionBar actionBar = ShelfFragment.this.getActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setDisplayShowTitleEnabled(true);
            }
            this.val$searchView.setQuery(Table.STRING_DEFAULT_VALUE, false);
            item.getActionView().clearFocus();
            Utils.hideKeyBoard(ShelfFragment.this.getActivity());
            return true;
        }
    }

    static {
        TAG = ShelfFragment.class.getSimpleName();
    }

    public ShelfFragment() {
        setTitle((int) R.string.act_title_shelf);
    }

    @AfterViews
    void init() {
        this.mBookItemClickHelper = new BookItemClickHelper(PageOpenHelper.from((Fragment) this));
        this.mOpenStore.setText(RichText.textWithIcon((int) R.drawable.v_store, (int) R.string.title_works_insert_from_store));
        this.mOpenMyWorks.setText(RichText.textWithIcon((int) R.drawable.v_mine, (int) R.string.title_works_insert_from_own_list));
        this.mGridView.setChoiceMode(3);
        this.mGridView.setMultiChoiceModeListener(new MultiChoiceModeListener());
        loadData();
    }

    public void onResume() {
        super.onResume();
        this.mShelfManager.clearNewAddedWorksMark();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        SpinnerSearchView searchView = (SpinnerSearchView) MenuItemCompat.getActionView(this.mMenuSearch);
        if (searchView != null) {
            searchView.setOnQueryTextListener(this);
            searchView.setQueryHint(Res.getString(R.string.hint_search_reading_works));
        }
        MenuItemCompat.setOnActionExpandListener(this.mMenuSearch, new AnonymousClass1(searchView));
    }

    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    public boolean onQueryTextChanged(String newText) {
        if (this.mAdapter == null) {
            return false;
        }
        this.mAdapter.getFilter().filter(newText);
        return true;
    }

    @Click({2131558645})
    void onBtnToStoreClicked() {
        HomeActivity.showContent(PageOpenHelper.from((Fragment) this), StoreFragment_.class);
    }

    @Click({2131558646})
    void onBtnToUserOwn() {
        HomeActivity.showContent(PageOpenHelper.from((Fragment) this), UserOwnListFragment_.class);
    }

    @ItemClick({2131558640})
    void onShelfItemClicked(ShelfItem shelfItem) {
        this.mBookItemClickHelper.performBookItemClick(shelfItem.id);
    }

    @Background
    void loadData() {
        try {
            showLoadingDialog();
            updateView(this.mShelfManager.getAll());
        } catch (DataLoadException e) {
            Logger.e(TAG, e);
            ToastUtils.showToast((int) R.string.general_load_failed);
        } finally {
            dismissLoadingDialog();
        }
    }

    @UiThread
    void updateView(List<ShelfItem> worksList) {
        this.mAdapter = new ShelfAdapter(App.get());
        this.mAdapter.addAll((Collection) worksList);
        if (this.mActionMode != null) {
            this.mAdapter.setMultiChoiceMode(true);
        }
        this.mGridView.setAdapter(this.mAdapter);
        updateShelfEmptyView();
    }

    @UiThread
    void updateShelfEmptyView() {
        ViewUtils.showIf(this.mAdapter.isEmpty(), this.mShelfEmptyView);
        if (!this.mAdapter.isEmpty()) {
            this.mGridView.setEmptyView(this.mSearchEmptyView);
        }
    }

    public void onPause() {
        super.onPause();
        Utils.hideKeyBoard(getActivity());
    }

    public void onEventMainThread(NewShelfItemAddedEvent event) {
        loadData();
    }

    public void onEventMainThread(NavigationDrawerOpenedEvent event) {
        if (this.mActionMode != null) {
            this.mCheckedItemList = this.mGridView.getCheckedItemPositions().clone();
            this.mActionMode.finish();
        }
    }

    public void onEventMainThread(NavigationDrawerClosedEvent event) {
        if (this.mCheckedItemList != null) {
            for (int i = 0; i < this.mCheckedItemList.size(); i++) {
                if (this.mCheckedItemList.valueAt(i)) {
                    this.mGridView.setItemChecked(this.mCheckedItemList.keyAt(i), true);
                }
            }
        }
        this.mCheckedItemList = null;
    }

    public void onEventMainThread(ShelfClearedEvent event) {
        loadData();
    }

    private void removeBooksFromShelf(long[] ids) {
        for (long id : ids) {
            int worksId = (int) id;
            try {
                ShelfItem item = this.mShelfManager.get(worksId);
                this.mShelfManager.delete(worksId);
                if (this.mAdapter != null) {
                    this.mAdapter.remove(item);
                }
            } catch (Exception e) {
                Logger.e(TAG, e);
            }
        }
        ToastUtils.showToast((int) R.string.toast_general_delete_success);
    }
}
