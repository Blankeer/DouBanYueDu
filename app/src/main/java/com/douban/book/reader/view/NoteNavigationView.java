package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.Annotation;
import com.douban.book.reader.manager.AnnotationManager;
import com.douban.book.reader.span.IconFontSpan;
import com.douban.book.reader.util.RichText;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903168)
public class NoteNavigationView extends LinearLayout {
    private AnnotationManager mAnnotationManager;
    @ViewById(2131558881)
    TextView mBtnNext;
    @ViewById(2131558880)
    TextView mBtnPrev;
    private Annotation mNextNote;
    private Annotation mPreviousNote;

    public NoteNavigationView(Context context) {
        super(context);
    }

    public NoteNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoteNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NoteNavigationView setCurrentNote(Annotation note) {
        boolean z = true;
        this.mAnnotationManager = AnnotationManager.ofWorks(note.worksId);
        this.mNextNote = this.mAnnotationManager.getNextNote(note);
        this.mBtnNext.setEnabled(this.mNextNote != null);
        this.mBtnNext.setText(RichText.buildUpon((int) R.string.btn_note_next).appendIcon((int) R.drawable.v_arrow_right));
        this.mPreviousNote = this.mAnnotationManager.getPreviousNote(note);
        TextView textView = this.mBtnPrev;
        if (this.mPreviousNote == null) {
            z = false;
        }
        textView.setEnabled(z);
        this.mBtnPrev.setText(new RichText().appendIcon(new IconFontSpan(R.drawable.v_arrow_right).flipped()).append((int) R.string.btn_note_prev));
        return this;
    }

    @Click({2131558880})
    void showPreviousNote() {
        this.mAnnotationManager.setActiveNote(this.mPreviousNote);
    }

    @Click({2131558881})
    void showNextNote() {
        this.mAnnotationManager.setActiveNote(this.mNextNote);
    }
}
