package com.douban.book.reader.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.WorksKind;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class WorksKindTabFragment_ extends WorksKindTabFragment implements HasViews, OnViewChangedListener {
    public static final String ROOT_KIND_ID_ARG = "rootKindId";
    private View contentView_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public static class FragmentBuilder_ extends FragmentBuilder<FragmentBuilder_, WorksKindTabFragment> {
        public WorksKindTabFragment build() {
            WorksKindTabFragment_ fragment_ = new WorksKindTabFragment_();
            fragment_.setArguments(this.args);
            return fragment_;
        }

        public FragmentBuilder_ rootKindId(int rootKindId) {
            this.args.putInt(WorksKindTabFragment_.ROOT_KIND_ID_ARG, rootKindId);
            return this;
        }
    }

    public WorksKindTabFragment_() {
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
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        injectFragmentArguments_();
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.onViewChangedNotifier_.notifyViewChanged(this);
    }

    public static FragmentBuilder_ builder() {
        return new FragmentBuilder_();
    }

    public void onViewChanged(HasViews hasViews) {
        AdapterView<?> view_list = (AdapterView) hasViews.findViewById(R.id.list);
        if (view_list != null) {
            view_list.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    WorksKindTabFragment_.this.onItemClicked((WorksKind) parent.getAdapter().getItem(position));
                }
            });
        }
    }

    private void injectFragmentArguments_() {
        Bundle args_ = getArguments();
        if (args_ != null && args_.containsKey(ROOT_KIND_ID_ARG)) {
            this.rootKindId = args_.getInt(ROOT_KIND_ID_ARG);
        }
    }
}
