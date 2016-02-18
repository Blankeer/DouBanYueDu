package com.douban.book.reader.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.douban.book.reader.R;
import java.io.File;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class FileListFragment_ extends FileListFragment implements HasViews, OnViewChangedListener {
    public static final String PATH_ARG = "path";
    private View contentView_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public static class FragmentBuilder_ extends FragmentBuilder<FragmentBuilder_, FileListFragment> {
        public FileListFragment build() {
            FileListFragment_ fragment_ = new FileListFragment_();
            fragment_.setArguments(this.args);
            return fragment_;
        }

        public FragmentBuilder_ path(File path) {
            this.args.putSerializable(FileListFragment_.PATH_ARG, path);
            return this;
        }
    }

    public FileListFragment_() {
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
                    FileListFragment_.this.onItemClick((File) parent.getAdapter().getItem(position));
                }
            });
        }
    }

    private void injectFragmentArguments_() {
        Bundle args_ = getArguments();
        if (args_ != null && args_.containsKey(PATH_ARG)) {
            this.path = (File) args_.getSerializable(PATH_ARG);
        }
    }
}
