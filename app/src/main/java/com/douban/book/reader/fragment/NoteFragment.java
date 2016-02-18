package com.douban.book.reader.fragment;

import android.support.v4.app.Fragment;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.activity.ReaderActivity_;
import com.douban.book.reader.activity.ReaderActivity_.IntentBuilder_;
import com.douban.book.reader.adapter.NoteAdapter;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.entity.Annotation;
import com.douban.book.reader.event.AnnotationUpdatedEvent;
import com.douban.book.reader.manager.AnnotationManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.Logger;
import java.util.List;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

@EFragment(2130903105)
public class NoteFragment extends BaseFragment {
    private NoteAdapter mAdapter;
    @ViewById(2131558629)
    TextView mHintView;
    @ViewById(2131558593)
    StickyListHeadersListView mListView;
    @FragmentArg
    int worksId;

    public NoteFragment() {
        setTitle((int) R.string.panel_tab_note);
    }

    @AfterViews
    void init() {
        updateHintView();
        loadData();
    }

    @ItemClick({2131558593})
    void onListItemClicked(Annotation annotation) {
        AnnotationManager.ofWorks(annotation.worksId).setActiveNote(annotation);
        PageOpenHelper.from((Fragment) this).open(((IntentBuilder_) ReaderActivity_.intent((Fragment) this).flags(67108864)).mBookId(this.worksId).positionToShow(annotation.getRange().startPosition).get());
    }

    public void onEventMainThread(AnnotationUpdatedEvent event) {
        if (event.isValidFor(this.worksId) && this.mAdapter != null) {
            this.mAdapter.notifyDataSetChanged();
        }
    }

    @Background
    void loadData() {
        try {
            setHint(R.string.dialog_msg_loading);
            onLoadSucceed(AnnotationManager.ofWorks(this.worksId).listAllNotes());
        } catch (DataLoadException e) {
            setHint(R.string.general_load_failed);
            Logger.e(this.TAG, e);
        }
    }

    @UiThread
    void onLoadSucceed(List<Annotation> array) {
        this.mAdapter = new NoteAdapter(getActivity(), this.worksId, array);
        this.mListView.setAdapter(this.mAdapter);
        updateHintView();
    }

    @UiThread
    void setHint(int resId) {
        if (resId > 0) {
            this.mHintView.setText(resId);
            this.mHintView.setVisibility(0);
            return;
        }
        this.mHintView.setVisibility(8);
    }

    private void updateHintView() {
        if (this.mAdapter != null) {
            if (this.mAdapter.isEmpty()) {
                this.mHintView.setText(R.string.text_note_empty);
            } else {
                this.mHintView.setVisibility(8);
            }
        }
    }
}
