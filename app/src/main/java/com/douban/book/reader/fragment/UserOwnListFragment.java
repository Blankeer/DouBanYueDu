package com.douban.book.reader.fragment;

import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import com.douban.book.reader.R;
import com.douban.book.reader.activity.StoreSearchActivity_;
import com.douban.book.reader.adapter.BaseArrayAdapter;
import com.douban.book.reader.adapter.ViewBinderAdapter;
import com.douban.book.reader.app.App;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.event.ArkEvent;
import com.douban.book.reader.manager.Lister;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.util.LoginPrompt;
import com.douban.book.reader.util.Pref;
import com.douban.book.reader.view.RedirectEmptyView_;
import com.douban.book.reader.view.WorksItemView;
import com.douban.book.reader.view.WorksItemView_;
import com.mcxiaoke.next.ui.endless.EndlessListView;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

@EFragment
@OptionsMenu({2131623949})
public class UserOwnListFragment extends BaseEndlessListFragment<Works> {
    @InstanceState
    boolean mBriefMode;
    @OptionsMenuItem({2131558993})
    MenuItem mMenuViewModeItem;
    @ViewById(2131558593)
    EndlessListView mWorksList;
    @Bean
    WorksManager mWorksManager;

    /* renamed from: com.douban.book.reader.fragment.UserOwnListFragment.1 */
    class AnonymousClass1 extends ViewBinderAdapter<Works> {
        AnonymousClass1(Class type) {
            super(type);
        }

        protected void bindView(View itemView, Works data) {
            ((WorksItemView) itemView).setBriefMode(UserOwnListFragment.this.mBriefMode);
            super.bindView(itemView, data);
        }
    }

    public UserOwnListFragment() {
        this.mBriefMode = Pref.ofApp().getBoolean(Key.APP_SHOW_USER_OWN_LIST_IN_BRIEF_MODE, false);
    }

    public BaseArrayAdapter<Works> onCreateAdapter() {
        return new AnonymousClass1(WorksItemView_.class);
    }

    public Lister<Works> onCreateLister() {
        return this.mWorksManager.listPurchased();
    }

    protected View onCreateEmptyView() {
        return RedirectEmptyView_.build(getActivity()).hint(R.string.hint_empty_user_own_list);
    }

    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        this.mMenuViewModeItem.setIcon(this.mBriefMode ? R.drawable.ic_action_view_mode_normal : R.drawable.ic_action_view_mode_brief);
    }

    @OptionsItem({2131558993})
    void onViewModeItemClicked() {
        Animation animation = AnimationUtils.loadAnimation(App.get(), R.anim.fade_out);
        animation.setDuration(200);
        animation.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                Animation inAnimation = AnimationUtils.loadAnimation(App.get(), R.anim.fade_in);
                inAnimation.setDuration(200);
                UserOwnListFragment.this.mListView.startAnimation(inAnimation);
                UserOwnListFragment.this.mBriefMode = !UserOwnListFragment.this.mBriefMode;
                Pref.ofApp().set(Key.APP_SHOW_USER_OWN_LIST_IN_BRIEF_MODE, Boolean.valueOf(UserOwnListFragment.this.mBriefMode));
                UserOwnListFragment.this.mAdapter.notifyDataSetChanged();
                UserOwnListFragment.this.invalidateOptionsMenu();
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });
        this.mListView.startAnimation(animation);
    }

    @OptionsItem({2131558991})
    void onSearchItemClicked() {
        StoreSearchActivity_.intent((Fragment) this).start();
    }

    @ItemClick({2131558593})
    void onWorksItemClicked(Works works) {
        if (works != null) {
            WorksProfileFragment_.builder().worksId(works.id).build().showAsActivity((Fragment) this);
        }
    }

    protected void onListViewCreated(EndlessListView listView) {
        setTitle((int) R.string.title_user_own);
        LoginPrompt.showIfNeeded(getActivity());
    }

    public void onEventMainThread(ArkEvent event) {
        if (event == ArkEvent.LOGIN_COMPLETED) {
            onRefresh();
        }
    }
}
