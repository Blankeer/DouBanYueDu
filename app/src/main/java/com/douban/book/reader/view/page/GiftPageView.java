package com.douban.book.reader.view.page;

import android.content.Context;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.DateUtils;
import com.douban.book.reader.util.Font;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.view.FlexibleScrollView;
import com.douban.book.reader.view.ParagraphView;
import com.douban.book.reader.view.ParagraphView.Indent;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903174)
public class GiftPageView extends AbsPageView {
    @ViewById(2131558863)
    ParagraphView mGiftNote;
    @ViewById(2131558862)
    FlexibleScrollView mGiftNoteContainer;
    @ViewById(2131558864)
    TextView mGiveTime;
    @ViewById(2131558762)
    TextView mGiver;
    @ViewById(2131558861)
    TextView mRecipient;
    @Bean
    WorksManager mWorksManager;

    public GiftPageView(Context context) {
        super(context);
    }

    @AfterViews
    void init() {
        this.mGiftNoteContainer.setMaxHeight((float) (App.get().getPageHeight() - Utils.dp2pixel(300.0f)));
        this.mRecipient.setText(Res.getString(R.string.text_recipient, UserManager.getInstance().getDisplayUserName()));
        this.mGiftNote.setFirstLineIndent(Indent.ALL);
    }

    public void setPage(int worksId, int page) {
        super.setPage(worksId, page);
        loadData(worksId);
    }

    public boolean isDraggable() {
        return false;
    }

    @Background
    void loadData(int worksId) {
        try {
            updateView(this.mWorksManager.getWorks(worksId));
        } catch (DataLoadException e) {
            Logger.e(this.TAG, e);
        }
    }

    @UiThread
    void updateView(Works works) {
        if (works.isGift()) {
            this.mGiver.setText(works.gift.from);
            this.mGiftNote.setParagraphText(works.gift.note);
            this.mGiveTime.setText(DateUtils.formatDateWithUnit(works.lastPurchaseTime));
            this.mRecipient.setTypeface(Font.SERIF);
            this.mGiver.setTypeface(Font.SERIF);
            this.mGiftNote.setTypeface(Font.SERIF);
            this.mGiveTime.setTypeface(Font.SERIF);
        }
    }
}
