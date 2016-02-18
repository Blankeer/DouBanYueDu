package com.douban.book.reader.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import com.douban.book.reader.R;
import com.douban.book.reader.content.pack.WorksData;
import com.douban.book.reader.content.pack.WorksData.Status;
import com.douban.book.reader.event.ColorThemeChangedEvent;
import com.douban.book.reader.event.DownloadProgressChangedEvent;
import com.douban.book.reader.event.DownloadStatusChangedEvent;
import com.douban.book.reader.task.DownloadManager;
import com.douban.book.reader.theme.Theme;
import com.douban.book.reader.util.CanvasUtils;
import com.douban.book.reader.util.Font;
import com.douban.book.reader.util.PaintUtils;
import com.douban.book.reader.util.ReaderUri;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ThemedUtils;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.util.ViewUtils;
import u.aly.ci;
import u.aly.dj;
import u.aly.dx;

public class WorksStatusView extends View implements OnClickListener {
    private static final ColorFilter COLOR_FILTER;
    private static final ColorFilter COLOR_FILTER_DOWNLOAD;
    private static final ColorFilter COLOR_FILTER_DOWNLOAD_NIGHT;
    private static final ColorFilter COLOR_FILTER_NIGHT;
    private static final int CONTENT_HEIGHT;
    private static final int CONTENT_WIDTH;
    private static final float PADDING;
    private static final float PROGRESS_TEXT_SIZE;
    private static final float STATUS_TEXT_SIZE;
    private int mWorksId;

    /* renamed from: com.douban.book.reader.view.WorksStatusView.1 */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$douban$book$reader$content$pack$WorksData$Status;

        static {
            $SwitchMap$com$douban$book$reader$content$pack$WorksData$Status = new int[Status.values().length];
            try {
                $SwitchMap$com$douban$book$reader$content$pack$WorksData$Status[Status.PENDING.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$douban$book$reader$content$pack$WorksData$Status[Status.PAUSED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$douban$book$reader$content$pack$WorksData$Status[Status.FAILED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$douban$book$reader$content$pack$WorksData$Status[Status.READY.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$douban$book$reader$content$pack$WorksData$Status[Status.EMPTY.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$douban$book$reader$content$pack$WorksData$Status[Status.DOWNLOADING.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    static {
        PROGRESS_TEXT_SIZE = Res.getDimension(R.dimen.general_font_size_small);
        STATUS_TEXT_SIZE = Res.getDimension(R.dimen.general_font_size_tiny);
        PADDING = (float) Utils.dp2pixel(4.0f);
        COLOR_FILTER = new PorterDuffColorFilter(Res.getColor(R.color.day_secondary_text), Mode.MULTIPLY);
        COLOR_FILTER_DOWNLOAD = new PorterDuffColorFilter(Res.getColor(R.color.palette_day_blue), Mode.MULTIPLY);
        COLOR_FILTER_NIGHT = new PorterDuffColorFilter(Res.getColor(R.color.night_secondary_text), Mode.MULTIPLY);
        COLOR_FILTER_DOWNLOAD_NIGHT = new PorterDuffColorFilter(Res.getColor(R.color.palette_night_blue), Mode.MULTIPLY);
        CONTENT_HEIGHT = Utils.dp2pixel(40.0f);
        CONTENT_WIDTH = Utils.dp2pixel(40.0f);
    }

    public WorksStatusView(Context context) {
        super(context);
        init();
    }

    public WorksStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WorksStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnClickListener(this);
        ViewUtils.setEventAware(this);
    }

    public void worksId(int worksId) {
        this.mWorksId = worksId;
        invalidate();
    }

    public void onEventMainThread(ColorThemeChangedEvent event) {
        invalidate();
    }

    public void onEventMainThread(DownloadProgressChangedEvent event) {
        invalidate();
    }

    public void onEventMainThread(DownloadStatusChangedEvent event) {
        invalidate();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        if (MeasureSpec.getMode(widthMeasureSpec) == 0) {
            measuredWidth = (CONTENT_WIDTH + getPaddingLeft()) + getPaddingRight();
        }
        if (MeasureSpec.getMode(heightMeasureSpec) == 0) {
            measuredHeight = (CONTENT_HEIGHT + getPaddingTop()) + getPaddingBottom();
        }
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    protected void onDraw(Canvas canvas) {
        float centerX = ((float) getWidth()) / 2.0f;
        float centerY = ((float) ((getHeight() + getPaddingTop()) - getPaddingBottom())) / 2.0f;
        Status status = WorksData.get(this.mWorksId).getStatus();
        if (status == Status.DOWNLOADING) {
            Paint paint = PaintUtils.obtainPaint();
            paint.setColor(Res.getColor(R.array.green));
            paint.setTextSize(PROGRESS_TEXT_SIZE);
            paint.setTypeface(Font.SANS_SERIF_BOLD);
            CanvasUtils.drawTextCenteredOnPoint(canvas, paint, centerX, centerY, String.format("%d%%", new Object[]{Integer.valueOf(WorksData.get(this.mWorksId).getDisplayDownloadProgress())}));
            PaintUtils.recyclePaint(paint);
            return;
        }
        int iconResId = 0;
        int textResId = 0;
        ColorFilter colorFilter = null;
        switch (AnonymousClass1.$SwitchMap$com$douban$book$reader$content$pack$WorksData$Status[status.ordinal()]) {
            case dx.b /*1*/:
                iconResId = R.drawable.ic_download_pending_small;
                textResId = R.string.works_download_status_pending;
                colorFilter = Theme.isNight() ? COLOR_FILTER_NIGHT : COLOR_FILTER;
                break;
            case dx.c /*2*/:
                iconResId = R.drawable.ic_download_paused_small;
                textResId = R.string.works_download_status_paused;
                if (Theme.isNight()) {
                    colorFilter = ThemedUtils.NIGHT_MODE_COLOR_FILTER;
                } else {
                    colorFilter = null;
                }
                break;
            case dx.d /*3*/:
                iconResId = R.drawable.ic_download_failed_small;
                textResId = R.string.works_download_status_failed;
                if (Theme.isNight()) {
                    colorFilter = ThemedUtils.NIGHT_MODE_COLOR_FILTER;
                } else {
                    colorFilter = null;
                }
                break;
            case dx.e /*4*/:
                iconResId = R.drawable.ic_download_ready_small;
                textResId = R.string.works_download_status_ready;
                colorFilter = Theme.isNight() ? COLOR_FILTER_NIGHT : COLOR_FILTER;
                break;
            case dj.f /*5*/:
                iconResId = R.drawable.ic_download;
                textResId = 0;
                colorFilter = Theme.isNight() ? COLOR_FILTER_DOWNLOAD_NIGHT : COLOR_FILTER_DOWNLOAD;
                break;
        }
        paint = PaintUtils.obtainPaint();
        paint.setColor(Res.getColor(R.array.secondary_text_color));
        paint.setTextSize(STATUS_TEXT_SIZE);
        paint.setTypeface(Font.SANS_SERIF);
        Drawable drawable = Res.getDrawable(iconResId);
        if (drawable != null) {
            drawable.setColorFilter(colorFilter);
        }
        CanvasUtils.drawIconTextCenteredOnPoint(canvas, paint, centerX, centerY, Res.getDrawable(iconResId), Res.getString(textResId), PADDING);
        PaintUtils.recyclePaint(paint);
    }

    public void onClick(View v) {
        if (this.mWorksId > 0) {
            Status status = WorksData.get(this.mWorksId).getStatus();
            Uri uri = ReaderUri.works(this.mWorksId);
            switch (AnonymousClass1.$SwitchMap$com$douban$book$reader$content$pack$WorksData$Status[status.ordinal()]) {
                case dx.b /*1*/:
                case ci.g /*6*/:
                    DownloadManager.stopDownloading(uri);
                case dx.e /*4*/:
                default:
                    DownloadManager.scheduleDownload(uri);
            }
        }
    }
}
