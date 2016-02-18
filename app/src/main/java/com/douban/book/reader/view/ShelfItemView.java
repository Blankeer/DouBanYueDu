package com.douban.book.reader.view;

import android.content.Context;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.ViewBinder;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.content.pack.WorksData;
import com.douban.book.reader.content.pack.WorksData.Status;
import com.douban.book.reader.entity.Manifest;
import com.douban.book.reader.entity.ShelfItem;
import com.douban.book.reader.event.DownloadProgressChangedEvent;
import com.douban.book.reader.event.DownloadStatusChangedEvent;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.exception.DataException;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.util.DebugSwitch;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.util.WorksIdentity;
import com.douban.book.reader.view.WorksCoverView.Label;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import u.aly.ci;
import u.aly.dj;
import u.aly.dx;

@EViewGroup(2130903190)
public class ShelfItemView extends RelativeLayout implements ViewBinder<ShelfItem>, Checkable {
    private static final String TAG;
    private boolean mChecked;
    @ViewById(2131558772)
    WorksCoverView mCover;
    @ViewById(2131558872)
    ProgressBar mDownloadProgress;
    @ViewById(2131558941)
    TextView mDownloadProgressText;
    @ViewById(2131558940)
    ImageView mDownloadStatus;
    @ViewById(2131558942)
    CheckableImageView mIcCheck;
    private boolean mIsInMultiChoiceMode;
    @ViewById(2131558943)
    TextView mTitle;
    private int mWorksId;
    @Bean
    WorksManager mWorksManager;

    /* renamed from: com.douban.book.reader.view.ShelfItemView.1 */
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
                $SwitchMap$com$douban$book$reader$content$pack$WorksData$Status[Status.PENDING.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$douban$book$reader$content$pack$WorksData$Status[Status.FAILED.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$douban$book$reader$content$pack$WorksData$Status[Status.PAUSED.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$douban$book$reader$content$pack$WorksData$Status[Status.EMPTY.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    static {
        TAG = ShelfItemView.class.getSimpleName();
    }

    public ShelfItemView(Context context) {
        super(context);
        this.mIsInMultiChoiceMode = false;
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBusUtils.register(this);
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBusUtils.unregister(this);
    }

    public void setChecked(boolean checked) {
        this.mChecked = checked;
        this.mIcCheck.setChecked(checked);
    }

    public boolean isChecked() {
        return this.mChecked;
    }

    public void toggle() {
        setChecked(!this.mChecked);
    }

    @AfterViews
    void initView() {
        this.mDownloadProgressText.getPaint().setFakeBoldText(true);
        this.mDownloadProgressText.setTextScaleX(1.05f);
    }

    public void bindData(ShelfItem data) {
        this.mWorksId = data.id;
        loadWorksData();
    }

    @Background
    void loadWorksData() {
        try {
            setWorks(Manifest.load(this.mWorksId));
        } catch (DataException e) {
            Logger.e(TAG, e);
        }
    }

    @UiThread
    void setWorks(Manifest manifest) {
        this.mTitle.setText(manifest.title);
        if (DebugSwitch.on(Key.APP_DEBUG_SHOW_BOOK_IDS)) {
            this.mTitle.setText(String.format("%s %s", new Object[]{Integer.valueOf(manifest.id), manifest.title}));
        }
        this.mCover.url(manifest.cover);
        updateStatus();
    }

    public void setInMultiChoiceMode(boolean isInMultiChoiceMode) {
        this.mIsInMultiChoiceMode = isInMultiChoiceMode;
        invalidate();
    }

    public void onEventMainThread(DownloadStatusChangedEvent event) {
        if (event.isValidFor(this.mWorksId)) {
            updateStatus();
        }
    }

    public void onEventMainThread(DownloadProgressChangedEvent event) {
        if (event.isValidFor(this.mWorksId)) {
            updateStatus();
        }
    }

    private void updateStatus() {
        try {
            boolean z;
            Manifest manifest = Manifest.get(this.mWorksId);
            WorksData worksData = WorksData.get(manifest.id);
            this.mDownloadProgress.setProgress(worksData.getDisplayDownloadProgress());
            this.mDownloadProgressText.setText(String.format("%d%%", new Object[]{Integer.valueOf(downloadProgress)}));
            ViewUtils.gone(this.mDownloadProgress, this.mDownloadProgressText, this.mDownloadStatus);
            this.mCover.setChecked(false);
            this.mCover.noLabel();
            Status status = worksData.getStatus();
            switch (AnonymousClass1.$SwitchMap$com$douban$book$reader$content$pack$WorksData$Status[status.ordinal()]) {
                case dx.b /*1*/:
                    if (!WorksIdentity.isColumnOrSerial(manifest.identities) && manifest.isPartial) {
                        this.mCover.label(Label.SAMPLE);
                        break;
                    }
                case dx.c /*2*/:
                    ViewUtils.visible(this.mDownloadProgress, this.mDownloadProgressText);
                    this.mDownloadProgress.setEnabled(true);
                    this.mCover.setChecked(true);
                    break;
                case dx.d /*3*/:
                    ViewUtils.visible(this.mDownloadProgress, this.mDownloadStatus);
                    this.mDownloadStatus.setImageResource(R.drawable.ic_download_pending);
                    this.mDownloadProgress.setEnabled(true);
                    this.mCover.setChecked(true);
                    break;
                case dx.e /*4*/:
                case dj.f /*5*/:
                case ci.g /*6*/:
                    ViewUtils.visible(this.mDownloadProgress, this.mDownloadStatus);
                    this.mDownloadProgress.setEnabled(false);
                    this.mDownloadStatus.setImageResource(status == Status.PAUSED ? R.drawable.ic_download_paused : R.drawable.ic_download_failed);
                    this.mCover.setChecked(true);
                    break;
            }
            ViewUtils.showIf(this.mIsInMultiChoiceMode, this.mIcCheck);
            WorksCoverView worksCoverView = this.mCover;
            if (this.mCover.isChecked() || this.mIsInMultiChoiceMode) {
                z = true;
            } else {
                z = false;
            }
            worksCoverView.setChecked(z);
        } catch (DataException e) {
        }
    }
}
