package com.douban.book.reader.fragment.share;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.douban.book.reader.manager.AnnotationManager_;
import com.douban.book.reader.manager.UserManager_;
import com.douban.book.reader.manager.WorksManager_;
import java.util.UUID;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class ShareNoteEditFragment_ extends ShareNoteEditFragment implements HasViews {
    public static final String NOTE_UUID_ARG = "noteUuid";
    private View contentView_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public static class FragmentBuilder_ extends FragmentBuilder<FragmentBuilder_, ShareNoteEditFragment> {
        public ShareNoteEditFragment build() {
            ShareNoteEditFragment_ fragment_ = new ShareNoteEditFragment_();
            fragment_.setArguments(this.args);
            return fragment_;
        }

        public FragmentBuilder_ noteUuid(UUID noteUuid) {
            this.args.putSerializable(ShareNoteEditFragment_.NOTE_UUID_ARG, noteUuid);
            return this;
        }
    }

    public ShareNoteEditFragment_() {
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
        this.mAnnotationManager = AnnotationManager_.getInstance_(getActivity());
        this.mUserManager = UserManager_.getInstance_(getActivity());
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
        if (args_ != null && args_.containsKey(NOTE_UUID_ARG)) {
            this.noteUuid = (UUID) args_.getSerializable(NOTE_UUID_ARG);
        }
    }
}
