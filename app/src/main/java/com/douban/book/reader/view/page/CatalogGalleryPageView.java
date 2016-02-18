package com.douban.book.reader.view.page;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import com.douban.book.reader.R;
import com.douban.book.reader.constant.Constants;
import com.douban.book.reader.content.paragraph.ContainerParagraph;
import com.douban.book.reader.content.paragraph.Paragraph;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.view.ParagraphView;

public class CatalogGalleryPageView extends AbsGalleryPageView {
    private static final int CATALOG_ITEM_MARGIN_BOTTOM = 14;
    private static final int CATALOG_ITEM_MARGIN_LEFT = 29;
    private static final int CATALOG_ITEM_MARGIN_RIGHT = 29;
    private static final int CATALOG_ITEM_MARGIN_TOP = 14;
    LinearLayout mCatalogWrapper;

    public CatalogGalleryPageView(Context context) {
        super(context);
    }

    protected void initView() {
        View.inflate(getContext(), R.layout.catalog_page_view_gallery, this);
        super.initView();
        this.mCatalogWrapper = (LinearLayout) findViewById(R.id.gallery_catalog_wrapper);
    }

    protected void fillView() {
        super.fillView();
        Paragraph[] paragraphs = ((ContainerParagraph) this.mParagraph).getChildrenParagraph();
        if (paragraphs != null && this.mCatalogWrapper != null) {
            this.mCatalogWrapper.removeAllViews();
            for (Paragraph paragraph : paragraphs) {
                ParagraphView catalogItem = new ParagraphView(getContext());
                catalogItem.setParagraphText(paragraph.getPrintableText());
                catalogItem.setTextSize((float) Utils.dp2pixel((float) Constants.TEXTSIZE_CATALOG[Math.min(Math.max(0, (int) ((paragraph.getTextSizeRatio() * 10.0f) - 11.0f)), Constants.TEXTSIZE_CATALOG.length)]));
                catalogItem.setTextBold();
                ThemedAttrs.ofView(catalogItem).append(R.attr.textColorArray, Integer.valueOf(R.array.reader_legend_text_color)).updateView();
                LayoutParams lp = new LayoutParams(-1, -2);
                lp.leftMargin = Utils.dp2pixel(29.0f);
                lp.rightMargin = Utils.dp2pixel(29.0f);
                lp.topMargin = Utils.dp2pixel(14.0f);
                lp.bottomMargin = Utils.dp2pixel(14.0f);
                catalogItem.setLayoutParams(lp);
                this.mCatalogWrapper.addView(catalogItem);
            }
        }
    }

    protected void drawHeader(Canvas canvas) {
    }
}
