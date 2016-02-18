package com.douban.book.reader.fragment;

import android.view.View;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.BaseArrayAdapter;
import com.douban.book.reader.adapter.ViewBinderAdapter;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.entity.WorksAgent;
import com.douban.book.reader.helper.WorksListUri;
import com.douban.book.reader.helper.WorksListUri.Type;
import com.douban.book.reader.manager.Lister;
import com.douban.book.reader.manager.WorksAgentManager;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.view.CoverLeftWorksView;
import com.douban.book.reader.view.CoverLeftWorksView_;
import com.douban.book.reader.view.card.WorksAgentCard_;
import com.mcxiaoke.next.ui.endless.EndlessListView;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;

@EFragment
public class WorksAgentFragment extends BaseEndlessListFragment<Works> {
    @FragmentArg
    int agentId;
    @Bean
    WorksAgentManager mWorksAgentManager;
    @Bean
    WorksManager mWorksManager;

    /* renamed from: com.douban.book.reader.fragment.WorksAgentFragment.1 */
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
        return this.mWorksManager.worksLister(WorksListUri.builder().id(this.agentId).type(Type.AGENT).build());
    }

    public BaseArrayAdapter<Works> onCreateAdapter() {
        return new AnonymousClass1(CoverLeftWorksView_.class);
    }

    protected void onListViewCreated(EndlessListView listView) {
        addHeaderView(WorksAgentCard_.build(getActivity()).agentId(this.agentId).showTitle(false).topPaddingResId(R.dimen.general_subview_vertical_padding_normal).linkEnabled(false));
        listView.setHeaderDividersEnabled(true);
    }

    @AfterViews
    void init() {
        loadData();
    }

    @Background
    void loadData() {
        try {
            updateViews((WorksAgent) this.mWorksAgentManager.get((Object) Integer.valueOf(this.agentId)));
        } catch (Exception e) {
            Logger.e(this.TAG, e);
        }
    }

    @UiThread
    void updateViews(WorksAgent worksAgent) {
        if (worksAgent != null) {
            setTitle(worksAgent.getTitle());
        }
    }
}
