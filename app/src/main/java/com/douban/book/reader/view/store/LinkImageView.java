package com.douban.book.reader.view.store;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.douban.book.reader.R;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.entity.store.LinksStoreWidgetEntity.Link;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.Analysis;
import com.douban.book.reader.util.ImageLoaderUtils;
import com.douban.book.reader.util.ViewUtils;

public class LinkImageView extends ImageView {

    /* renamed from: com.douban.book.reader.view.store.LinkImageView.1 */
    class AnonymousClass1 implements OnClickListener {
        final /* synthetic */ String val$uri;

        AnonymousClass1(String str) {
            this.val$uri = str;
        }

        public void onClick(View v) {
            PageOpenHelper.from(LinkImageView.this).open(this.val$uri);
            Analysis.sendEventWithExtra("banner", "click", this.val$uri);
        }
    }

    /* renamed from: com.douban.book.reader.view.store.LinkImageView.2 */
    class AnonymousClass2 implements OnClickListener {
        final /* synthetic */ Uri val$uri;

        AnonymousClass2(Uri uri) {
            this.val$uri = uri;
        }

        public void onClick(View v) {
            PageOpenHelper.from(LinkImageView.this).open(this.val$uri);
            Analysis.sendEventWithExtra("banner", "click", String.valueOf(this.val$uri));
        }
    }

    public LinkImageView(Context context) {
        super(context);
        init();
    }

    public LinkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LinkImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setAdjustViewBounds(true);
        ViewUtils.of(this).width(-1).height(-2).commit();
        ThemedAttrs.ofView(this).append(R.attr.autoDimInNightMode, Boolean.valueOf(true)).updateView();
        setScaleType(ScaleType.CENTER_CROP);
    }

    public void setData(Link link) {
        setImage(link.img);
        setLinkUri(link.uri);
    }

    public void setImage(String image) {
        ImageLoaderUtils.displayImage(image, this);
    }

    public void setLinkUri(String uri) {
        setOnClickListener(new AnonymousClass1(uri));
    }

    public void setLinkUri(Uri uri) {
        setOnClickListener(new AnonymousClass2(uri));
    }
}
