package com.douban.book.reader.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.Annotation;
import com.douban.book.reader.entity.UserInfo;
import com.douban.book.reader.manager.AnnotationManager_;
import com.douban.book.reader.manager.UserManager_;
import com.douban.book.reader.view.NoteNavigationView;
import com.douban.book.reader.view.NotePrivacyInfoView;
import com.douban.book.reader.view.ParagraphView;
import com.douban.book.reader.view.UserAvatarView;
import io.realm.internal.Table;
import java.util.UUID;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class NoteDetailFragment_ extends NoteDetailFragment implements HasViews, OnViewChangedListener {
    public static final String ID_OR_UUID_ARG = "idOrUuid";
    public static final String NOTE_NAVIGATION_ENABLED_ARG = "noteNavigationEnabled";
    public static final String UUID_ARG = "uuid";
    private View contentView_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.fragment.NoteDetailFragment_.1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ UserInfo val$userInfo;

        AnonymousClass1(UserInfo userInfo) {
            this.val$userInfo = userInfo;
        }

        public void run() {
            super.updateUserInfo(this.val$userInfo);
        }
    }

    /* renamed from: com.douban.book.reader.fragment.NoteDetailFragment_.2 */
    class AnonymousClass2 implements Runnable {
        final /* synthetic */ Annotation val$annotation;

        AnonymousClass2(Annotation annotation) {
            this.val$annotation = annotation;
        }

        public void run() {
            super.updateAnnotationInfo(this.val$annotation);
        }
    }

    /* renamed from: com.douban.book.reader.fragment.NoteDetailFragment_.4 */
    class AnonymousClass4 extends Task {
        final /* synthetic */ Object val$idOrUuidToLoad;

        AnonymousClass4(String x0, long x1, String x2, Object obj) {
            this.val$idOrUuidToLoad = obj;
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.loadData(this.val$idOrUuidToLoad);
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    /* renamed from: com.douban.book.reader.fragment.NoteDetailFragment_.5 */
    class AnonymousClass5 extends Task {
        final /* synthetic */ int val$userId;

        AnonymousClass5(String x0, long x1, String x2, int i) {
            this.val$userId = i;
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.loadUserInfo(this.val$userId);
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    /* renamed from: com.douban.book.reader.fragment.NoteDetailFragment_.6 */
    class AnonymousClass6 extends Task {
        AnonymousClass6(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.onMenuShareClicked();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    /* renamed from: com.douban.book.reader.fragment.NoteDetailFragment_.7 */
    class AnonymousClass7 extends Task {
        AnonymousClass7(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.deleteNote();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public static class FragmentBuilder_ extends FragmentBuilder<FragmentBuilder_, NoteDetailFragment> {
        public NoteDetailFragment build() {
            NoteDetailFragment_ fragment_ = new NoteDetailFragment_();
            fragment_.setArguments(this.args);
            return fragment_;
        }

        public FragmentBuilder_ uuid(UUID uuid) {
            this.args.putSerializable(NoteDetailFragment_.UUID_ARG, uuid);
            return this;
        }

        public FragmentBuilder_ idOrUuid(String idOrUuid) {
            this.args.putString(NoteDetailFragment_.ID_OR_UUID_ARG, idOrUuid);
            return this;
        }

        public FragmentBuilder_ noteNavigationEnabled(boolean noteNavigationEnabled) {
            this.args.putBoolean(NoteDetailFragment_.NOTE_NAVIGATION_ENABLED_ARG, noteNavigationEnabled);
            return this;
        }
    }

    public NoteDetailFragment_() {
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
            this.contentView_ = inflater.inflate(R.layout.frag_note_detail, container, false);
        }
        return this.contentView_;
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.contentView_ = null;
        this.mUserAvatar = null;
        this.mUserInfo = null;
        this.mNoteUuid = null;
        this.mNoteDetail = null;
        this.mQuotedText = null;
        this.mNoteCreatedDate = null;
        this.mPrivacyView = null;
        this.mDividerUnderPrivacyView = null;
        this.mNoteNavigationView = null;
        this.mLoadErrorViewBase = null;
    }

    private void init_(Bundle savedInstanceState) {
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        injectFragmentArguments_();
        this.mUserManager = UserManager_.getInstance_(getActivity());
        this.mAnnotationManager = AnnotationManager_.getInstance_(getActivity());
        setHasOptionsMenu(true);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.onViewChangedNotifier_.notifyViewChanged(this);
    }

    public static FragmentBuilder_ builder() {
        return new FragmentBuilder_();
    }

    public void onViewChanged(HasViews hasViews) {
        this.mUserAvatar = (UserAvatarView) hasViews.findViewById(R.id.user_avatar);
        this.mUserInfo = (ParagraphView) hasViews.findViewById(R.id.user_info);
        this.mNoteUuid = (TextView) hasViews.findViewById(R.id.note_uuid);
        this.mNoteDetail = (ParagraphView) hasViews.findViewById(R.id.note_detail);
        this.mQuotedText = (ParagraphView) hasViews.findViewById(R.id.quoted_text);
        this.mNoteCreatedDate = (TextView) hasViews.findViewById(R.id.note_created_date);
        this.mPrivacyView = (NotePrivacyInfoView) hasViews.findViewById(R.id.note_privacy);
        this.mDividerUnderPrivacyView = hasViews.findViewById(R.id.divider_under_privacy_view);
        this.mNoteNavigationView = (NoteNavigationView) hasViews.findViewById(R.id.note_navigation);
        this.mLoadErrorViewBase = (ViewGroup) hasViews.findViewById(R.id.error_view_base);
        init();
    }

    private void injectFragmentArguments_() {
        Bundle args_ = getArguments();
        if (args_ != null) {
            if (args_.containsKey(UUID_ARG)) {
                this.uuid = (UUID) args_.getSerializable(UUID_ARG);
            }
            if (args_.containsKey(ID_OR_UUID_ARG)) {
                this.idOrUuid = args_.getString(ID_OR_UUID_ARG);
            }
            if (args_.containsKey(NOTE_NAVIGATION_ENABLED_ARG)) {
                this.noteNavigationEnabled = args_.getBoolean(NOTE_NAVIGATION_ENABLED_ARG);
            }
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.share, menu);
        inflater.inflate(R.menu.edit, menu);
        inflater.inflate(R.menu.delete, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId_ = item.getItemId();
        if (itemId_ == R.id.action_share) {
            onMenuShareClicked();
            return true;
        } else if (itemId_ == R.id.action_edit) {
            onMenuEditClicked();
            return true;
        } else if (itemId_ != R.id.action_delete) {
            return super.onOptionsItemSelected(item);
        } else {
            onMenuDeleteClicked();
            return true;
        }
    }

    void updateUserInfo(UserInfo userInfo) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass1(userInfo), 0);
    }

    void updateAnnotationInfo(Annotation annotation) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass2(annotation), 0);
    }

    void dismissLoadingErrorView() {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new Runnable() {
            public void run() {
                super.dismissLoadingErrorView();
            }
        }, 0);
    }

    void loadData(Object idOrUuidToLoad) {
        BackgroundExecutor.execute(new AnonymousClass4(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE, idOrUuidToLoad));
    }

    void loadUserInfo(int userId) {
        BackgroundExecutor.execute(new AnonymousClass5(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE, userId));
    }

    void onMenuShareClicked() {
        BackgroundExecutor.execute(new AnonymousClass6(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }

    void deleteNote() {
        BackgroundExecutor.execute(new AnonymousClass7(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }
}
