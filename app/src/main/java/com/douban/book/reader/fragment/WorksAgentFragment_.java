package com.douban.book.reader.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.douban.book.reader.entity.WorksAgent;
import com.douban.book.reader.manager.WorksAgentManager_;
import com.douban.book.reader.manager.WorksManager_;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class WorksAgentFragment_ extends WorksAgentFragment implements HasViews, OnViewChangedListener {
    public static final String AGENT_ID_ARG = "agentId";
    private View contentView_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.fragment.WorksAgentFragment_.1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ WorksAgent val$worksAgent;

        AnonymousClass1(WorksAgent worksAgent) {
            this.val$worksAgent = worksAgent;
        }

        public void run() {
            super.updateViews(this.val$worksAgent);
        }
    }

    /* renamed from: com.douban.book.reader.fragment.WorksAgentFragment_.2 */
    class AnonymousClass2 extends Task {
        AnonymousClass2(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.loadData();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public static class FragmentBuilder_ extends FragmentBuilder<FragmentBuilder_, WorksAgentFragment> {
        public WorksAgentFragment build() {
            WorksAgentFragment_ fragment_ = new WorksAgentFragment_();
            fragment_.setArguments(this.args);
            return fragment_;
        }

        public FragmentBuilder_ agentId(int agentId) {
            this.args.putInt(WorksAgentFragment_.AGENT_ID_ARG, agentId);
            return this;
        }
    }

    public WorksAgentFragment_() {
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
    }

    public void onCreate(Bundle savedInstanceState) {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        init_(savedInstanceState);
        super.onCreate(savedInstanceState);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public View findViewById(int id) {
        if (this.contentView_ == null) {
            return null;
        }
        return this.contentView_.findViewById(id);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.contentView_ = super.onCreateView(inflater, container, savedInstanceState);
        return this.contentView_;
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.contentView_ = null;
    }

    private void init_(Bundle savedInstanceState) {
        injectFragmentArguments_();
        this.mWorksManager = WorksManager_.getInstance_(getActivity());
        this.mWorksAgentManager = WorksAgentManager_.getInstance_(getActivity());
        OnViewChangedNotifier.registerOnViewChangedListener(this);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.onViewChangedNotifier_.notifyViewChanged(this);
    }

    public static FragmentBuilder_ builder() {
        return new FragmentBuilder_();
    }

    private void injectFragmentArguments_() {
        Bundle args_ = getArguments();
        if (args_ != null && args_.containsKey(AGENT_ID_ARG)) {
            this.agentId = args_.getInt(AGENT_ID_ARG);
        }
    }

    public void onViewChanged(HasViews hasViews) {
        init();
    }

    void updateViews(WorksAgent worksAgent) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass1(worksAgent), 0);
    }

    void loadData() {
        BackgroundExecutor.execute(new AnonymousClass2(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }
}
