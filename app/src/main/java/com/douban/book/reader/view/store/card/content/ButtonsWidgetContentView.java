package com.douban.book.reader.view.store.card.content;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import com.douban.book.reader.R;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.drawable.AutoStateDrawable;
import com.douban.book.reader.drawable.RoundCornerDrawable;
import com.douban.book.reader.entity.store.BaseStoreWidgetEntity.LinkButton;
import com.douban.book.reader.helper.AppUri;
import com.douban.book.reader.span.IconFontSpan;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.DebugSwitch;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.ViewUtils;
import java.util.List;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;

@EViewGroup
public class ButtonsWidgetContentView extends LinearLayout {

    /* renamed from: com.douban.book.reader.view.store.card.content.ButtonsWidgetContentView.1 */
    class AnonymousClass1 implements OnClickListener {
        final /* synthetic */ LinkButton val$button;

        AnonymousClass1(LinkButton linkButton) {
            this.val$button = linkButton;
        }

        public void onClick(View v) {
            PageOpenHelper.from(ButtonsWidgetContentView.this).open(this.val$button.uri);
        }
    }

    public ButtonsWidgetContentView(Context context) {
        super(context);
    }

    public ButtonsWidgetContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ButtonsWidgetContentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @AfterViews
    public void init() {
        setOrientation(0);
        setGravity(17);
        ViewUtils.of(this).width(-1).height(-2).commit();
        ViewUtils.setVerticalPaddingResId(this, R.dimen.general_subview_vertical_padding_medium);
        ViewUtils.setHorizontalPaddingResId(this, R.dimen.page_horizontal_padding);
    }

    public void setButtons(List<LinkButton> buttonList) {
        if (getChildCount() <= 0 && buttonList != null) {
            for (LinkButton button : buttonList) {
                Button btn = new Button(getContext());
                btn.setAllCaps(false);
                btn.setText(button.text);
                btn.setTextSize(0, Res.getDimension(R.dimen.general_font_size_large));
                ThemedAttrs.ofView(btn).append(R.attr.textColorArray, Integer.valueOf(R.array.invert_text_color)).updateView();
                btn.setGravity(17);
                int uriType = AppUri.getType(button.uri);
                if (uriType == 2) {
                    btn.setText(textWithIcon(R.drawable.v_hermes, button.text));
                    ViewUtils.setBackground(btn, createButtonBg(R.drawable.bg_btn_hermes));
                    if (DebugSwitch.on(Key.APP_DEBUG_USE_WEB_HERMES)) {
                        button.uri = "http://read.douban.com/translation";
                    }
                } else if (uriType == 1) {
                    btn.setText(textWithIcon(R.drawable.v_column, button.text));
                    ViewUtils.setBackground(btn, createButtonBg(R.drawable.bg_btn_column));
                }
                btn.setTag(button);
                btn.setOnClickListener(new AnonymousClass1(button));
                ViewUtils.of(btn).width(0).height(Math.round(((float) Res.getDimensionPixelSize(R.dimen.btn_height)) * 1.6f)).weight(1).leftMargin(button.equals(buttonList.get(0)) ? 0 : Res.getDimensionPixelSize(R.dimen.general_subview_horizontal_padding_normal)).commit();
                addView(btn);
            }
        }
    }

    private static Drawable createButtonBg(int resId) {
        return new AutoStateDrawable(new RoundCornerDrawable(Res.getDrawable(resId)));
    }

    private static RichText textWithIcon(@DrawableRes int iconResId, CharSequence str) {
        RichText richText = new RichText();
        if (iconResId > 0) {
            richText.appendIcon(new IconFontSpan(iconResId).ratio(1.5f).paddingRight(0.5f).verticalOffsetRatio(-0.09f));
        }
        richText.append(str);
        return richText;
    }
}
