package com.douban.book.reader.view.page;

import android.content.Context;
import android.widget.Button;
import android.widget.ImageView;
import com.douban.book.reader.R;
import com.douban.book.reader.event.ColorThemeChangedEvent;
import com.douban.book.reader.task.DownloadManager;
import com.douban.book.reader.util.ReaderUri;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.ViewUtils;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903172)
public class ChapterCorruptedPageView extends AbsPageView {
    @ViewById(2131558892)
    Button mBtnDownload;
    @ViewById(2131558458)
    ImageView mImage;
    private int mWorksId;

    public ChapterCorruptedPageView(Context context) {
        super(context);
    }

    @AfterViews
    void init() {
        updateImageColor();
        this.mBtnDownload.setText(RichText.textWithIcon((int) R.drawable.v_download, (int) R.string.download));
        setGeneralTouchListener(this);
        ViewUtils.setEventAware(this);
    }

    public void setData(int worksId, int packageId) {
        this.mWorksId = worksId;
        this.mPackageId = packageId;
    }

    public void onEventMainThread(ColorThemeChangedEvent event) {
        updateImageColor();
    }

    public boolean isDraggable() {
        return false;
    }

    private void updateImageColor() {
        if (this.mImage != null) {
            this.mImage.setImageDrawable(Res.getDrawableWithTint((int) R.drawable.v_bad_package, (int) R.array.bie_blue));
        }
    }

    @Click({2131558892})
    void onBtnDownloadClicked() {
        DownloadManager.scheduleDownload(ReaderUri.works(this.mWorksId));
    }
}
