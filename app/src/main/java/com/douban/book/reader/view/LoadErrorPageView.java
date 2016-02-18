package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.activity.HomeActivity;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.event.ColorThemeChangedEvent;
import com.douban.book.reader.fragment.ShelfFragment_;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.ExceptionUtils;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ThemedUtils;
import com.douban.book.reader.util.ViewUtils;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903164)
public class LoadErrorPageView extends RelativeLayout {
    @ViewById(2131558869)
    Button mBtnRefresh;
    @ViewById(2131558870)
    Button mBtnToShelf;
    @ViewById(2131558868)
    TextView mHintText;
    @ViewById(2131558867)
    ImageView mImgBie;
    private ViewGroup mParentView;
    private RefreshClickListener mRefreshClickListener;

    public interface RefreshClickListener {
        void onClick();
    }

    public LoadErrorPageView(Context context) {
        super(context);
    }

    public LoadErrorPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadErrorPageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @AfterViews
    void init() {
        ViewUtils.setHorizontalPaddingResId(this, R.dimen.page_horizontal_padding);
        ThemedAttrs.ofView(this).append(R.attr.backgroundColorArray, Integer.valueOf(R.array.page_bg_color));
        ThemedUtils.updateView(this);
        updateImageColor();
        ViewUtils.setEventAware(this);
    }

    public LoadErrorPageView attachTo(ViewGroup parentView) {
        this.mParentView = parentView;
        return this;
    }

    public LoadErrorPageView hideToShelfButton() {
        ViewUtils.gone(this.mBtnToShelf);
        return this;
    }

    public LoadErrorPageView message(CharSequence message) {
        this.mHintText.setText(message);
        return this;
    }

    public LoadErrorPageView exception(Exception exception) {
        return message(ExceptionUtils.getHumanReadableMessage((Throwable) exception, (int) R.string.general_load_failed));
    }

    public LoadErrorPageView refreshClickListener(RefreshClickListener refreshClickListener) {
        this.mRefreshClickListener = refreshClickListener;
        return this;
    }

    public void show() {
        if (this.mParentView != null) {
            this.mParentView.addView(this, new LayoutParams(-1, -1));
        }
    }

    @Click({2131558869})
    void onBtnRefreshClicked() {
        if (this.mRefreshClickListener != null) {
            this.mRefreshClickListener.onClick();
            postDelayed(new Runnable() {
                public void run() {
                    if (LoadErrorPageView.this.mParentView != null) {
                        LoadErrorPageView.this.mParentView.removeView(LoadErrorPageView.this);
                    }
                }
            }, 1000);
        }
    }

    @Click({2131558870})
    void onBtnToShelf() {
        HomeActivity.showContent(PageOpenHelper.from((View) this), ShelfFragment_.class);
    }

    public void onEventMainThread(ColorThemeChangedEvent event) {
        updateImageColor();
    }

    private void updateImageColor() {
        if (this.mImgBie != null) {
            this.mImgBie.setImageDrawable(Res.getDrawableWithTint((int) R.drawable.v_bad_network, (int) R.array.bie_blue));
        }
    }
}
