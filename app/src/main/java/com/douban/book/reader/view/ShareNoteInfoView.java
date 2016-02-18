package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.douban.book.reader.entity.Annotation;
import com.douban.book.reader.manager.AnnotationManager;
import com.douban.book.reader.util.Logger;
import java.util.UUID;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903185)
public class ShareNoteInfoView extends LinearLayout {
    private static final String TAG;
    @Bean
    AnnotationManager mAnnotationManager;
    @ViewById(2131558934)
    TextView mNoteContent;

    static {
        TAG = ShareNoteInfoView.class.getSimpleName();
    }

    public ShareNoteInfoView(Context context) {
        super(context);
    }

    public ShareNoteInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShareNoteInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ShareNoteInfoView noteUuid(UUID uuid) {
        loadData(uuid);
        return this;
    }

    @AfterViews
    void init() {
        setOrientation(1);
    }

    @Background
    void loadData(UUID uuid) {
        try {
            updateViews((Annotation) this.mAnnotationManager.get((Object) uuid));
        } catch (Exception e) {
            Logger.e(TAG, e);
        }
    }

    @UiThread
    void updateViews(Annotation annotation) {
        if (annotation != null) {
            this.mNoteContent.setText(annotation.note);
        }
    }
}
