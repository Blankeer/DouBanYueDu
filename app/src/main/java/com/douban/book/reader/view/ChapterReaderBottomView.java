package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.activity.ReaderActivity_;
import com.douban.book.reader.activity.ReaderActivity_.IntentBuilder_;
import com.douban.book.reader.app.App;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.content.pack.WorksData;
import com.douban.book.reader.content.pack.WorksData.Status;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.event.DownloadProgressChangedEvent;
import com.douban.book.reader.event.DownloadStatusChangedEvent;
import com.douban.book.reader.fragment.WorksProfileFragment_;
import com.douban.book.reader.fragment.share.ShareChapterEditFragment_;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.ViewUtils;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import u.aly.dx;

@EViewGroup(2130903152)
public class ChapterReaderBottomView extends FrameLayout {
    private static final String TAG;
    private int mChapterId;
    @ViewById(2131558772)
    WorksCoverView mCoverView;
    @ViewById(2131558855)
    TextView mTvAction;
    @ViewById(2131558856)
    TextView mTvShare;
    private Works mWorks;
    private int mWorksId;
    @Bean
    WorksManager mWorksManager;

    /* renamed from: com.douban.book.reader.view.ChapterReaderBottomView.1 */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$douban$book$reader$content$pack$WorksData$Status;

        static {
            $SwitchMap$com$douban$book$reader$content$pack$WorksData$Status = new int[Status.values().length];
            try {
                $SwitchMap$com$douban$book$reader$content$pack$WorksData$Status[Status.READY.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$douban$book$reader$content$pack$WorksData$Status[Status.DOWNLOADING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$douban$book$reader$content$pack$WorksData$Status[Status.EMPTY.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    static {
        TAG = ChapterReaderBottomView.class.getSimpleName();
    }

    public ChapterReaderBottomView(Context context) {
        super(context);
    }

    public ChapterReaderBottomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChapterReaderBottomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @AfterViews
    void init() {
        ViewUtils.setEventAware(this);
    }

    public ChapterReaderBottomView setData(int worksId, int chapterId) {
        this.mWorksId = worksId;
        this.mChapterId = chapterId;
        loadData();
        return this;
    }

    @Background
    void loadData() {
        try {
            this.mWorks = this.mWorksManager.getWorks(this.mWorksId);
            updateViews();
        } catch (DataLoadException e) {
            Logger.e(TAG, e);
        }
    }

    @UiThread
    void updateViews() {
        if (this.mWorks != null) {
            this.mCoverView.setDrawableRatio(1.2857143f);
            this.mCoverView.works(this.mWorks);
            switch (AnonymousClass1.$SwitchMap$com$douban$book$reader$content$pack$WorksData$Status[WorksData.get(this.mWorksId).getStatus().ordinal()]) {
                case dx.b /*1*/:
                    this.mTvAction.setText(RichText.textWithIcon((int) R.drawable.v_read, (int) R.string.text_open_in_reader));
                    break;
                case dx.c /*2*/:
                    this.mTvAction.setText(String.format("%d%%", new Object[]{Integer.valueOf(WorksData.get(this.mWorksId).getDownloadProgress())}));
                    break;
                default:
                    this.mTvAction.setText(RichText.textWithIcon((int) R.drawable.v_read, (int) R.string.text_download_to_shelf));
                    break;
            }
            this.mTvShare.setText(RichText.singleIcon(R.drawable.v_share));
        }
    }

    public void onEventMainThread(DownloadProgressChangedEvent event) {
        updateViews();
    }

    public void onEventMainThread(DownloadStatusChangedEvent event) {
        updateViews();
    }

    @Click({2131558772})
    void openProfile() {
        WorksProfileFragment_.builder().worksId(this.mWorksId).build().showAsActivity((View) this);
    }

    @Click({2131558855})
    void openReader() {
        PageOpenHelper.from((View) this).open(((IntentBuilder_) ReaderActivity_.intent(App.get()).flags(67108864)).mBookId(this.mWorksId).chapterToShow(this.mChapterId).get());
    }

    @Click({2131558856})
    void startShare() {
        ShareChapterEditFragment_.builder().worksId(this.mWorksId).chapterId(this.mChapterId).build().showAsActivity(PageOpenHelper.from((View) this));
    }
}
