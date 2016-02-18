package com.douban.book.reader.view.store.item;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.entity.store.ChartsStoreWidgetEntity.Chart;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.ViewUtils;
import java.util.Arrays;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903196)
public class ChartItemView extends RelativeLayout {
    private static final String TAG;
    private static final int[][] THEME;
    @ViewById(2131558950)
    Button mBtnChart;
    @ViewById(2131558462)
    TextView mTitle;
    @ViewById(2131558894)
    View mTopDivider;
    @ViewById(2131558949)
    StoreWorksItemView mWorksView;

    /* renamed from: com.douban.book.reader.view.store.item.ChartItemView.1 */
    class AnonymousClass1 implements OnClickListener {
        final /* synthetic */ Chart val$chart;

        AnonymousClass1(Chart chart) {
            this.val$chart = chart;
        }

        public void onClick(View v) {
            if (this.val$chart.moreBtn != null) {
                PageOpenHelper.from(ChartItemView.this).open(this.val$chart.moreBtn.uri);
            }
        }
    }

    static {
        TAG = ChartItemView.class.getSimpleName();
        THEME = new int[][]{new int[]{R.array.topic_widget_title_theme_orange, R.array.topic_widget_bg_theme_orange}, new int[]{R.array.topic_widget_title_theme_green, R.array.topic_widget_bg_theme_green}, new int[]{R.array.topic_widget_title_theme_blue, R.array.topic_widget_bg_theme_blue}};
    }

    public ChartItemView(Context context) {
        super(context);
        init();
    }

    public ChartItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChartItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        ViewUtils.setBottomPaddingResId(this, R.dimen.general_subview_vertical_padding_large);
    }

    public void setChart(Chart chart, int theme) {
        this.mTitle.setText(chart.title);
        this.mWorksView.setWorks(chart.topWorks);
        if (chart.moreBtn != null) {
            this.mBtnChart.setVisibility(0);
            this.mBtnChart.setText(chart.moreBtn.text);
        } else {
            this.mBtnChart.setVisibility(8);
        }
        if (theme < 0 || theme > THEME.length) {
            theme = 0;
        }
        for (View view : Arrays.asList(new TextView[]{this.mTitle, this.mBtnChart})) {
            ThemedAttrs.ofView(view).append(R.attr.textColorArray, Integer.valueOf(THEME[theme][0])).updateView();
        }
        ThemedAttrs.ofView(this).append(R.attr.backgroundColorArray, Integer.valueOf(THEME[theme][1])).updateView();
        ThemedAttrs.ofView(this.mTopDivider).append(R.attr.backgroundColorArray, Integer.valueOf(THEME[theme][0])).updateView();
        this.mBtnChart.setOnClickListener(new AnonymousClass1(chart));
    }
}
