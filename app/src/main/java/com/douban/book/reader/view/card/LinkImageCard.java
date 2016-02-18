package com.douban.book.reader.view.card;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.DimenRes;
import android.widget.ImageView.ScaleType;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.store.LinkImageView;
import org.androidannotations.annotations.EViewGroup;

@EViewGroup
public class LinkImageCard extends Card<LinkImageCard> {
    final LinkImageView mLinkImageView;

    public LinkImageCard(Context context) {
        super(context);
        this.mLinkImageView = new LinkImageView(context);
        content(this.mLinkImageView);
        noContentPadding();
        this.mLinkImageView.setAdjustViewBounds(false);
        this.mLinkImageView.setScaleType(ScaleType.CENTER_CROP);
    }

    public LinkImageCard height(int height) {
        ViewUtils.of(this.mLinkImageView).widthMatchParent().height(height).commit();
        ViewUtils.of(getContentView()).height(height).commit();
        return this;
    }

    public LinkImageCard heightResId(@DimenRes int heightResId) {
        return height(Res.getDimensionPixelSize(heightResId));
    }

    public LinkImageCard scaleType(ScaleType scaleType) {
        this.mLinkImageView.setScaleType(scaleType);
        return this;
    }

    public LinkImageCard uri(Uri uri) {
        this.mLinkImageView.setLinkUri(uri);
        return this;
    }

    public LinkImageCard uri(String uri) {
        this.mLinkImageView.setLinkUri(uri);
        return this;
    }

    public LinkImageCard image(String image) {
        this.mLinkImageView.setImage(image);
        return this;
    }
}
