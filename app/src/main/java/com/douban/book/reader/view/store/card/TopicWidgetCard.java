package com.douban.book.reader.view.store.card;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import com.douban.book.reader.R;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.entity.store.TopicStoreWidgetEntity;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.view.store.card.content.TopicWidgetContentView_;
import org.androidannotations.annotations.EViewGroup;

@EViewGroup
public class TopicWidgetCard extends BaseWidgetCard<TopicStoreWidgetEntity> {
    private static final int[][] THEME;
    private static final String THEME_BLUE = "blue";
    private static final String THEME_DEEP_BLUE = "deep_blue";
    private static final String THEME_GREEN = "green";
    private static final String THEME_NORMAL = "normal";
    private static final String THEME_ORANGE = "orange";
    private static final String THEME_PINK = "pink";

    /* renamed from: com.douban.book.reader.view.store.card.TopicWidgetCard.1 */
    class AnonymousClass1 implements OnClickListener {
        final /* synthetic */ TopicStoreWidgetEntity val$entity;

        AnonymousClass1(TopicStoreWidgetEntity topicStoreWidgetEntity) {
            this.val$entity = topicStoreWidgetEntity;
        }

        public void onClick(View v) {
            PageOpenHelper.from(TopicWidgetCard.this).open(this.val$entity.payload.moreBtn.uri);
        }
    }

    static {
        THEME = new int[][]{new int[]{R.array.topic_widget_bg_theme_normal, R.array.topic_widget_title_theme_normal}, new int[]{R.array.topic_widget_bg_theme_pink, R.array.topic_widget_title_theme_pink}, new int[]{R.array.topic_widget_bg_theme_green, R.array.topic_widget_title_theme_green}, new int[]{R.array.topic_widget_bg_theme_blue, R.array.topic_widget_title_theme_blue}, new int[]{R.array.topic_widget_bg_theme_orange, R.array.topic_widget_title_theme_orange}, new int[]{R.array.topic_widget_bg_theme_deep_blue, R.array.topic_widget_title_theme_deep_blue}};
    }

    public TopicWidgetCard(Context context) {
        super(context);
    }

    protected void onEntityBound(TopicStoreWidgetEntity entity) {
        if (entity != null && entity.payload != null) {
            int[] themeValue = getThemeValue(entity.payload.theme);
            ThemedAttrs.ofView(this).append(R.attr.backgroundColorArray, Integer.valueOf(themeValue[0])).updateView();
            titleColorArray(themeValue[1]);
            if (!(entity.payload == null || entity.payload.moreBtn == null)) {
                titleClickListener(new AnonymousClass1(entity));
            }
            if (entity.payload == null || entity.payload.worksList == null || entity.payload.worksList.size() <= 0) {
                hide();
                return;
            }
            setVisibility(0);
            ((TopicWidgetContentView_) getOrCreateContentView(TopicWidgetContentView_.class)).setTopicWidget(entity);
        }
    }

    private int[] getThemeValue(String theme) {
        if (StringUtils.equals((CharSequence) theme, THEME_PINK)) {
            return THEME[1];
        }
        if (StringUtils.equals((CharSequence) theme, THEME_GREEN)) {
            return THEME[2];
        }
        if (StringUtils.equals((CharSequence) theme, THEME_BLUE)) {
            return THEME[3];
        }
        if (StringUtils.equals((CharSequence) theme, THEME_ORANGE)) {
            return THEME[4];
        }
        if (StringUtils.equals((CharSequence) theme, THEME_DEEP_BLUE)) {
            return THEME[5];
        }
        return THEME[0];
    }
}
