package com.douban.book.reader.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.douban.book.reader.manager.GiftManager_;
import com.douban.book.reader.manager.GiftPackManager_;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class GiftPackDetailFragment_ extends GiftPackDetailFragment implements HasViews {
    public static final String HASH_CODE_ARG = "hashCode";
    public static final String PACK_ID_ARG = "packId";
    private View contentView_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.fragment.GiftPackDetailFragment_.2 */
    class AnonymousClass2 extends Task {
        AnonymousClass2(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.loadMeta();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public static class FragmentBuilder_ extends FragmentBuilder<FragmentBuilder_, GiftPackDetailFragment> {
        public GiftPackDetailFragment build() {
            GiftPackDetailFragment_ fragment_ = new GiftPackDetailFragment_();
            fragment_.setArguments(this.args);
            return fragment_;
        }

        public FragmentBuilder_ packId(int packId) {
            this.args.putInt(GiftPackDetailFragment_.PACK_ID_ARG, packId);
            return this;
        }

        public FragmentBuilder_ hashCode(String hashCode) {
            this.args.putString(GiftPackDetailFragment_.HASH_CODE_ARG, hashCode);
            return this;
        }
    }

    public GiftPackDetailFragment_() {
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
        this.mGiftManager = GiftManager_.getInstance_(getActivity());
        this.mGiftPackManager = GiftPackManager_.getInstance_(getActivity());
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
        if (args_ != null) {
            if (args_.containsKey(PACK_ID_ARG)) {
                this.packId = args_.getInt(PACK_ID_ARG);
            }
            if (args_.containsKey(HASH_CODE_ARG)) {
                this.hashCode = args_.getString(HASH_CODE_ARG);
            }
        }
    }

    void updateHeaderView() {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new Runnable() {
            public void run() {
                super.updateHeaderView();
            }
        }, 0);
    }

    void loadMeta() {
        BackgroundExecutor.execute(new AnonymousClass2(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }
}
