package com.douban.book.reader.fragment;

import android.view.View;
import android.view.View.OnClickListener;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.entity.PageMeta;
import com.douban.book.reader.entity.Topic;
import com.douban.book.reader.manager.PageManager;
import com.douban.book.reader.manager.PageManager.Page;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.LoadErrorPageView.RefreshClickListener;
import com.douban.book.reader.view.ParagraphView.Indent;
import com.douban.book.reader.view.card.Card;
import com.douban.book.reader.view.card.TextCard;
import com.douban.book.reader.view.store.card.BaseWidgetCard;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;

@EFragment
public class IntroPageFragment extends BaseCardFragment {
    @Bean
    PageManager mPageManager;
    private PageMeta mPageMeta;
    @FragmentArg
    Page page;

    /* renamed from: com.douban.book.reader.fragment.IntroPageFragment.2 */
    class AnonymousClass2 implements OnClickListener {
        final /* synthetic */ Topic val$topic;

        AnonymousClass2(Topic topic) {
            this.val$topic = topic;
        }

        public void onClick(View v) {
            PageOpenHelper.from(IntroPageFragment.this).open(this.val$topic.uri);
        }
    }

    @AfterViews
    void init() {
        loadPageMeta();
    }

    @Background
    void loadPageMeta() {
        showLoadingDialog();
        try {
            this.mPageMeta = this.mPageManager.getPageMeta(this.page);
            refreshViews();
        } catch (DataLoadException e) {
            Logger.e(this.TAG, e);
            onLoadFailed(e);
        } finally {
            dismissLoadingDialog();
        }
    }

    @UiThread
    void onLoadFailed(DataLoadException ex) {
        showLoadErrorPage(ex, new RefreshClickListener() {
            public void onClick() {
                IntroPageFragment.this.loadPageMeta();
            }
        });
    }

    @UiThread
    void refreshViews() {
        if (this.mPageMeta != null) {
            refreshTitle();
            Card introCard = ((TextCard) new TextCard(App.get()).noDivider()).content(this.mPageMeta.description).firstLineIndent(Indent.ALL);
            ViewUtils.setTopPaddingResId(introCard, R.dimen.general_subview_vertical_padding_medium);
            addCard(introCard);
            for (Topic topic : this.mPageMeta.topicList) {
                addCard(((TextCard) ((TextCard) ((TextCard) ((TextCard) new TextCard(App.get()).title(BaseWidgetCard.getTitleWithIcon(R.drawable.v_topic, topic.title))).titleColorArray(R.array.blue)).subTitle(topic.description)).moreBtnVisible(true)).clickListener(new AnonymousClass2(topic)));
            }
        }
    }

    @UiThread
    void refreshTitle() {
        if (this.mPageMeta != null) {
            setTitle(this.mPageMeta.title);
        }
    }
}
