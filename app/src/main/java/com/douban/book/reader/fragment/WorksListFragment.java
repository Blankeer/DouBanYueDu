package com.douban.book.reader.fragment;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;
import com.douban.book.reader.R;
import com.douban.book.reader.activity.StoreSearchActivity_;
import com.douban.book.reader.adapter.BaseArrayAdapter;
import com.douban.book.reader.adapter.ViewBinderAdapter;
import com.douban.book.reader.app.App;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.helper.WorksListUri;
import com.douban.book.reader.helper.WorksListUri.Type;
import com.douban.book.reader.manager.Lister;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.DebugSwitch;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.ChartWorksItemView;
import com.douban.book.reader.view.ChartWorksItemView_;
import com.douban.book.reader.view.CoverLeftWorksView;
import com.douban.book.reader.view.CoverLeftWorksView_;
import com.douban.book.reader.view.ParagraphView;
import com.douban.book.reader.view.ParagraphView.Indent;
import com.mcxiaoke.next.ui.endless.EndlessListView;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import u.aly.dx;

@OptionsMenu({2131623950})
@EFragment
public class WorksListFragment extends BaseEndlessListFragment<Works> {
    private FrameLayout mListHeaderView;
    private ParagraphView mListMetaView;
    private boolean mShouldShowPrice;
    @Bean
    WorksManager mWorksManager;
    @FragmentArg
    Uri uri;

    /* renamed from: com.douban.book.reader.fragment.WorksListFragment.3 */
    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$com$douban$book$reader$helper$WorksListUri$Type;

        static {
            $SwitchMap$com$douban$book$reader$helper$WorksListUri$Type = new int[Type.values().length];
            try {
                $SwitchMap$com$douban$book$reader$helper$WorksListUri$Type[Type.TOPIC.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$douban$book$reader$helper$WorksListUri$Type[Type.TAG.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$douban$book$reader$helper$WorksListUri$Type[Type.RECOMMENDATION.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$douban$book$reader$helper$WorksListUri$Type[Type.RANK.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$douban$book$reader$helper$WorksListUri$Type[Type.KIND.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$douban$book$reader$helper$WorksListUri$Type[Type.PUB.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$douban$book$reader$helper$WorksListUri$Type[Type.GENERAL.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    public static class WorksListMeta {
        public String description;
        public String icon;
        public String title;
    }

    /* renamed from: com.douban.book.reader.fragment.WorksListFragment.1 */
    class AnonymousClass1 extends ViewBinderAdapter<Works> {
        AnonymousClass1(Class type) {
            super(type);
        }

        protected void bindView(View itemView, Works data) {
            ((ChartWorksItemView) itemView).setRankNum(getPosition(data) + 1);
            super.bindView(itemView, data);
        }
    }

    /* renamed from: com.douban.book.reader.fragment.WorksListFragment.2 */
    class AnonymousClass2 extends ViewBinderAdapter<Works> {
        AnonymousClass2(Class type) {
            super(type);
        }

        protected void bindView(View itemView, Works data) {
            ((CoverLeftWorksView) itemView).showAbstract();
            ((CoverLeftWorksView) itemView).showRatingInfo(true);
            ((CoverLeftWorksView) itemView).showPrice(WorksListFragment.this.mShouldShowPrice);
            super.bindView(itemView, data);
        }
    }

    @AfterViews
    void init() {
        this.mShouldShowPrice = WorksListUri.shouldShowPrice(this.uri);
    }

    public Lister<Works> onCreateLister() {
        if (this.uri != null) {
            return this.mWorksManager.worksLister(this.uri);
        }
        return null;
    }

    public BaseArrayAdapter<Works> onCreateAdapter() {
        if (WorksListUri.getType(this.uri) == Type.RANK) {
            return new AnonymousClass1(ChartWorksItemView_.class);
        }
        return new AnonymousClass2(CoverLeftWorksView_.class);
    }

    protected void onListViewCreated(EndlessListView listView) {
        this.mListMetaView = new ParagraphView(App.get());
        ThemedAttrs.ofView(this.mListMetaView).append(R.attr.textColorArray, Integer.valueOf(R.array.content_text_color)).updateView();
        ViewUtils.setTextAppearance(App.get(), this.mListMetaView, R.style.AppWidget_Text_Content_Block);
        this.mListMetaView.setFirstLineIndent(Indent.AUTO);
        ViewUtils.setVerticalPaddingResId(this.mListMetaView, R.dimen.general_subview_vertical_padding_medium);
        ViewUtils.setHorizontalPaddingResId(this.mListMetaView, R.dimen.page_horizontal_padding);
        this.mListHeaderView = new FrameLayout(getActivity());
        addHeaderView(this.mListHeaderView);
        switch (AnonymousClass3.$SwitchMap$com$douban$book$reader$helper$WorksListUri$Type[WorksListUri.getType(this.uri).ordinal()]) {
            case dx.b /*1*/:
                setTitle((int) R.string.title_works_list_topic);
                break;
            case dx.c /*2*/:
                setTitle((int) R.string.title_works_list_tag);
                break;
            case dx.d /*3*/:
                setTitle((int) R.string.title_works_list_recommendation);
                break;
            case dx.e /*4*/:
                setTitle((int) R.string.title_works_list_chart);
                break;
            default:
                setTitle((int) R.string.title_works_list);
                break;
        }
        loadMeta();
    }

    @OptionsItem({2131558991})
    void onMenuItemSearchClicked() {
        StoreSearchActivity_.intent((Fragment) this).start();
    }

    @Background
    void loadMeta() {
        try {
            updateMetaView(this.mWorksManager.worksListMeta(this.uri));
        } catch (Exception e) {
            Logger.e(this.TAG, e);
        }
    }

    @UiThread
    void updateMetaView(WorksListMeta meta) {
        if (meta != null) {
            setTitle(meta.title);
            String metaText = meta.description;
            if (DebugSwitch.on(Key.APP_DEBUG_SHOW_WEBVIEW_URL)) {
                metaText = String.format("%s%n%s", new Object[]{this.uri, metaText});
            }
            if (StringUtils.isNotEmpty(metaText)) {
                this.mListMetaView.setParagraphText(metaText);
                this.mListView.setHeaderDividersEnabled(true);
                this.mListHeaderView.addView(this.mListMetaView);
                return;
            }
            this.mListHeaderView.removeView(this.mListMetaView);
        }
    }

    @ItemClick({2131558593})
    void onWorksItemClicked(Works works) {
        if (works != null) {
            WorksProfileFragment_.builder().worksId(works.id).build().showAsActivity((Fragment) this);
        }
    }
}
