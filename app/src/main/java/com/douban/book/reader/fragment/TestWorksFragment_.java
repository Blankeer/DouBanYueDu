package com.douban.book.reader.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.douban.book.reader.R;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class TestWorksFragment_ extends TestWorksFragment implements HasViews, OnViewChangedListener {
    private View contentView_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public static class FragmentBuilder_ extends FragmentBuilder<FragmentBuilder_, TestWorksFragment> {
        public TestWorksFragment build() {
            TestWorksFragment_ fragment_ = new TestWorksFragment_();
            fragment_.setArguments(this.args);
            return fragment_;
        }
    }

    public TestWorksFragment_() {
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
        if (this.contentView_ == null) {
            this.contentView_ = inflater.inflate(R.layout.frag_test_works, container, false);
        }
        return this.contentView_;
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.contentView_ = null;
        this.mLayoutBase = null;
        this.mEditWorksId = null;
    }

    private void init_(Bundle savedInstanceState) {
        OnViewChangedNotifier.registerOnViewChangedListener(this);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.onViewChangedNotifier_.notifyViewChanged(this);
    }

    public static FragmentBuilder_ builder() {
        return new FragmentBuilder_();
    }

    public void onViewChanged(HasViews hasViews) {
        this.mLayoutBase = (LinearLayout) hasViews.findViewById(R.id.layout_base);
        this.mEditWorksId = (EditText) hasViews.findViewById(R.id.text_works_id);
        View view_btn_open_works = hasViews.findViewById(R.id.btn_open_works);
        View view_btn_open_column = hasViews.findViewById(R.id.btn_open_column);
        if (view_btn_open_works != null) {
            view_btn_open_works.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    TestWorksFragment_.this.onBtnOpenWorksClicked();
                }
            });
        }
        if (view_btn_open_column != null) {
            view_btn_open_column.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    TestWorksFragment_.this.onBtnOpenColumnClicked();
                }
            });
        }
        init();
    }
}
