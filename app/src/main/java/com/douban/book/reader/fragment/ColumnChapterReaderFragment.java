package com.douban.book.reader.fragment;

import android.net.Uri;
import android.view.View;
import android.webkit.JavascriptInterface;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.event.PurchasedEvent;
import com.douban.book.reader.helper.AppUri;
import com.douban.book.reader.helper.StoreUriHelper;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.util.ReaderUriUtils;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.view.ChapterReaderBottomView;
import com.douban.book.reader.view.ChapterReaderBottomView_;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;

@EFragment
public class ColumnChapterReaderFragment extends BaseWebFragment {
    @FragmentArg
    int chapterId;
    @FragmentArg
    int legacyColumnId;
    private ChapterReaderBottomView mBottomView;
    @Bean
    WorksManager mWorksManager;
    @FragmentArg
    int worksId;

    @AfterViews
    void init() {
        enableJavascript("ark_app");
        loadWorks();
    }

    protected View onCreateBottomView() {
        this.mBottomView = ChapterReaderBottomView_.build(App.get());
        return this.mBottomView;
    }

    @Background
    void loadWorks() {
        try {
            showLoadingDialog();
            Works works = null;
            if (this.worksId > 0) {
                works = this.mWorksManager.getWorks(this.worksId);
                this.legacyColumnId = works.columnId;
            } else if (this.legacyColumnId > 0) {
                works = this.mWorksManager.getWorksByLegacyColumnId(this.legacyColumnId);
                this.worksId = works.id;
            }
            if (works != null && works.isColumnOrSerial()) {
                setTitle(works.isSerial() ? R.string.title_for_serial_chapter : R.string.title_for_column_chapter);
            }
            this.mBottomView.setData(this.worksId, this.chapterId);
            loadUri();
        } catch (Throwable e) {
            ToastUtils.showToast(e);
            finish();
        }
    }

    public void onEventMainThread(PurchasedEvent event) {
        Uri purchasedItem = event.getPurchasedItem();
        if (this.worksId == ReaderUriUtils.getWorksId(purchasedItem) && this.chapterId == ReaderUriUtils.getPackageId(purchasedItem)) {
            onRefresh();
        }
    }

    @UiThread
    void loadUri() {
        loadUrl(StoreUriHelper.columnChapterReader(this.legacyColumnId, this.chapterId));
    }

    @JavascriptInterface
    public String get_chapter_purchase_uri(String worksId, String columnId) {
        return String.valueOf(AppUri.purchase(StringUtils.toInt(worksId), StringUtils.toInt(columnId), false));
    }

    @JavascriptInterface
    public String get_chapter_reader_uri(String worksId, String columnId) {
        return String.valueOf(AppUri.openInNewPage(AppUri.webReader(StringUtils.toInt(worksId), StringUtils.toInt(columnId))));
    }
}
