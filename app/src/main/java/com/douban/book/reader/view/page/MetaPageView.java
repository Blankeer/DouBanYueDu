package com.douban.book.reader.view.page;

import android.content.Context;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.entity.Manifest;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.util.DebugSwitch;
import com.douban.book.reader.util.Font;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.ViewUtils;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903176)
public class MetaPageView extends AbsPageView {
    @ViewById(2131558774)
    TextView mAuthor;
    @ViewById(2131558914)
    TextView mISBN;
    @ViewById(2131558551)
    TextView mOnSaleTime;
    @ViewById(2131558913)
    TextView mPublishTime;
    @ViewById(2131558912)
    TextView mPublisher;
    @ViewById(2131558910)
    TextView mSubTitle;
    @ViewById(2131558462)
    TextView mTitle;
    @ViewById(2131558911)
    TextView mTranslator;
    @ViewById(2131558915)
    TextView mWorksAcknowledgements;
    @Bean
    WorksManager mWorksManager;

    public MetaPageView(Context context) {
        super(context);
    }

    public void setPage(int worksId, int page) {
        super.setPage(worksId, page);
        loadData(worksId);
    }

    @Background
    void loadData(int worksId) {
        try {
            setWorks(this.mWorksManager.getWorks(worksId), Manifest.get(worksId));
        } catch (Exception e) {
            Logger.e(this.TAG, e);
        }
    }

    @UiThread
    void setWorks(Works works, Manifest manifest) {
        this.mTitle.setText(works.title);
        if (DebugSwitch.on(Key.APP_DEBUG_SHOW_BOOK_IDS)) {
            this.mTitle.setText(String.format("%s %s", new Object[]{Integer.valueOf(works.id), works.title}));
        }
        this.mTitle.setTypeface(Font.SANS_SERIF_BOLD);
        ViewUtils.showTextIfNotEmpty(this.mSubTitle, works.subtitle);
        this.mAuthor.setText(Res.getString(R.string.title_author_with_prefix, works.author));
        ViewUtils.showTextIf(StringUtils.isNotEmpty(manifest.translator), this.mTranslator, Res.getString(R.string.title_translator, manifest.translator));
        ViewUtils.showTextIf(StringUtils.isNotEmpty(works.publisher), this.mPublisher, Res.getString(R.string.title_publisher, works.publisher));
        this.mWorksAcknowledgements.setText(manifest.getAcknowledgements());
    }

    public boolean isDraggable() {
        return false;
    }
}
