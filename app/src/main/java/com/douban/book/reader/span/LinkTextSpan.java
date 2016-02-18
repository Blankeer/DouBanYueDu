package com.douban.book.reader.span;

import android.net.Uri;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import com.douban.book.reader.R;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.util.Res;

public class LinkTextSpan extends ClickableSpan {
    private final Uri mUri;

    public LinkTextSpan() {
        this(null);
    }

    public LinkTextSpan(Uri uri) {
        this.mUri = uri;
    }

    public void onClick(View widget) {
        PageOpenHelper.from(widget).open(this.mUri);
    }

    public void updateDrawState(TextPaint tp) {
        tp.setColor(Res.getColor(R.array.green));
    }
}
