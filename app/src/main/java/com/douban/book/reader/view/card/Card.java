package com.douban.book.reader.view.card;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.helper.UriOpenHelper;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ViewUtils;
import io.github.zzn2.colorize.Colorize;

public class Card<T extends Card> extends LinearLayout {
    protected final String TAG;
    private RelativeLayout mContent;
    private View mDivider;
    private View mHeaderView;
    private TextView mSubTitle;
    private TextView mTitle;
    private LinearLayout mTopViews;

    /* renamed from: com.douban.book.reader.view.card.Card.1 */
    class AnonymousClass1 implements OnClickListener {
        final /* synthetic */ Uri val$uri;

        AnonymousClass1(Uri uri) {
            this.val$uri = uri;
        }

        public void onClick(View v) {
            UriOpenHelper.openUri(PageOpenHelper.from(Card.this), this.val$uri);
        }
    }

    public Card(Context context) {
        super(context);
        this.TAG = getClass().getSimpleName();
        init(context);
    }

    private void init(Context context) {
        setOrientation(1);
        inflate(context, R.layout.view_card, this);
        this.mDivider = findViewById(R.id.divider);
        this.mTitle = (TextView) findViewById(com.wnafee.vector.R.id.title);
        this.mTopViews = (LinearLayout) findViewById(R.id.top_views);
        this.mContent = (RelativeLayout) findViewById(R.id.content);
        this.mSubTitle = (TextView) findViewById(R.id.sub_title);
        this.mHeaderView = findViewById(R.id.header_layout);
    }

    public T noHeader() {
        this.mHeaderView.setVisibility(8);
        return self();
    }

    public T noDivider() {
        this.mDivider.setVisibility(8);
        return self();
    }

    public T noTitle() {
        this.mTitle.setVisibility(8);
        return self();
    }

    public T title(CharSequence title) {
        ViewUtils.showTextIfNotEmpty(this.mTitle, title);
        return self();
    }

    public T titleColor(int color) {
        this.mTitle.setTextColor(color);
        ThemedAttrs.ofView(this.mTitle).updateView();
        return self();
    }

    public T titleColorArray(int color) {
        ThemedAttrs.ofView(this.mTitle).append(R.attr.textColorArray, Integer.valueOf(color)).updateView();
        ThemedAttrs.ofView(this.mTitle).updateView();
        return self();
    }

    public T title(int resId) {
        Context context = getContext();
        if (context != null) {
            title(context.getString(resId));
        }
        return self();
    }

    public T content(View contentView) {
        this.mContent.removeAllViews();
        this.mContent.addView(contentView);
        return self();
    }

    public T content(@LayoutRes int resId) {
        Context context = getContext();
        if (context != null) {
            this.mContent.removeAllViews();
            inflate(context, resId, this.mContent);
        }
        return self();
    }

    public View getContentView() {
        try {
            return this.mContent.getChildAt(0);
        } catch (Exception e) {
            return null;
        }
    }

    public T backgroundColor(@ColorRes int resId) {
        setBackgroundDrawable(new ColorDrawable(Res.getColor(resId)));
        return self();
    }

    public T autoDimInNightMode() {
        ThemedAttrs.ofView(this).append(R.attr.autoDimInNightMode, Boolean.valueOf(true)).updateView();
        return self();
    }

    public T backgroundColorArray(@ArrayRes int backgroundResId) {
        ThemedAttrs.ofView(this).append(R.attr.backgroundColorArray, Integer.valueOf(backgroundResId)).updateView();
        return self();
    }

    public T icon(int resId) {
        ViewUtils.setDrawableLeft(this.mTitle, resId);
        ThemedAttrs.ofView(this.mTitle).updateView();
        return self();
    }

    public T iconColorUpdatedWithTitle(boolean iconColorUpdatedWithTitle) {
        ThemedAttrs.ofView(this.mTitle).append(R.attr.colorizeDrawableEnabled, Boolean.valueOf(true)).updateView();
        return self();
    }

    public T subTitle(String subTitle) {
        ViewUtils.showTextIfNotEmpty(this.mSubTitle, subTitle);
        return self();
    }

    public T titleClickListener(OnClickListener clickListener) {
        this.mTitle.setOnClickListener(clickListener);
        return self();
    }

    public T clickListener(OnClickListener clickListener) {
        setOnClickListener(clickListener);
        return self();
    }

    public T openWhenClicked(Uri uri) {
        return clickListener(new AnonymousClass1(uri));
    }

    public T moreBtnVisible(boolean visible) {
        if (visible && this.mTopViews.getChildCount() <= 0) {
            this.mTopViews.addView(createMoreBtn());
        }
        ViewUtils.visibleIf(visible, this.mTopViews);
        return self();
    }

    public T padding(int pixels) {
        setPadding(pixels, pixels, pixels, pixels);
        return self();
    }

    public T paddingHorizontal(int pixels) {
        setPadding(pixels, getPaddingTop(), pixels, getPaddingBottom());
        return self();
    }

    public T paddingHorizontalResId(@DimenRes int resId) {
        ViewUtils.setHorizontalPaddingResId(this, resId);
        return self();
    }

    public T noContentPadding() {
        if (this.mContent != null) {
            ViewUtils.setHorizontalPadding(this.mContent, 0);
        }
        return self();
    }

    public T paddingVertical(int pixels) {
        setPadding(getPaddingLeft(), pixels, getPaddingRight(), pixels);
        return self();
    }

    public T paddingVerticalResId(@DimenRes int resId) {
        ViewUtils.setVerticalPaddingResId(this, resId);
        return self();
    }

    public T contentPaddingVerticalResId(@DimenRes int resId) {
        ViewUtils.setVerticalPaddingResId(this.mContent, resId);
        return self();
    }

    public T paddingTop(int pixels) {
        ViewUtils.setTopPadding(this, pixels);
        return self();
    }

    public T contentPaddingTop(int pixels) {
        ViewUtils.setTopPadding(this.mContent, pixels);
        return self();
    }

    public T contentPaddingTopResId(@DimenRes int resId) {
        ViewUtils.setTopPaddingResId(this.mContent, resId);
        return self();
    }

    public T paddingTopResId(@DimenRes int resId) {
        ViewUtils.setTopPaddingResId(this, resId);
        return self();
    }

    public T paddingBottom(int pixels) {
        ViewUtils.setBottomPadding(this, pixels);
        return self();
    }

    public T paddingBottomResId(@DimenRes int resId) {
        ViewUtils.setBottomPaddingResId(this, resId);
        return self();
    }

    public T contentPaddingBottomResId(@DimenRes int resId) {
        ViewUtils.setBottomPaddingResId(this.mContent, resId);
        return self();
    }

    public T backgroundHighlighted() {
        ThemedAttrs.ofView(this).append(R.attr.backgroundColorArray, Integer.valueOf(R.array.page_highlight_bg_color)).updateView();
        return self();
    }

    public T hide() {
        noHeader();
        noDivider();
        setVisibility(8);
        return self();
    }

    private static TextView createMoreBtn() {
        TextView btnMore = new TextView(App.get());
        ViewUtils.of(btnMore).heightWrapContent().widthWrapContent().commit();
        ThemedAttrs.ofView(btnMore).append(R.attr.textColorArray, Integer.valueOf(R.array.blue)).updateView();
        ViewUtils.setDrawableLeft(btnMore, (int) R.drawable.ic_arrow_right);
        Colorize.applyTo(btnMore);
        ViewUtils.setEventAware(btnMore);
        return btnMore;
    }

    protected T self() {
        return this;
    }
}
