package com.douban.book.reader.fragment.share;

import android.net.Uri;
import android.view.View;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.fragment.BaseShareEditFragment;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.network.client.JsonClient;
import com.douban.book.reader.network.param.JsonRequestParam;
import com.douban.book.reader.network.param.RequestParam;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.Utils;
import com.sina.weibo.sdk.component.WidgetRequestParam;
import io.realm.internal.Table;
import java.io.IOException;
import java.util.Iterator;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

@EFragment
public class ShareUrlEditFragment extends BaseShareEditFragment {
    @FragmentArg
    String defaultTitle;
    private Document mHtmlDocument;
    @FragmentArg
    String url;

    protected void initData() throws DataLoadException {
        super.initData();
        try {
            this.mHtmlDocument = Jsoup.connect(this.url).get();
        } catch (IOException e) {
            Logger.e(this.TAG, e);
        }
    }

    protected String getContentType() {
        return ShareUrlEditFragment_.URL_ARG;
    }

    protected Object getContentId() {
        return null;
    }

    protected String getContentTitle() {
        if (this.mHtmlDocument != null) {
            Iterator it = this.mHtmlDocument.select("meta[property=\"og:title\"").iterator();
            while (it.hasNext()) {
                if (StringUtils.isNotEmpty(((Element) it.next()).attr(WidgetRequestParam.REQ_PARAM_COMMENT_CONTENT))) {
                    return ((Element) it.next()).attr(WidgetRequestParam.REQ_PARAM_COMMENT_CONTENT);
                }
            }
            if (StringUtils.isNotEmpty(this.mHtmlDocument.title())) {
                return this.mHtmlDocument.title();
            }
        }
        return this.defaultTitle;
    }

    protected String getContentDescription() {
        if (this.mHtmlDocument != null) {
            Iterator it = this.mHtmlDocument.select("meta[property=\"og:description\"").iterator();
            if (it.hasNext()) {
                return ((Element) it.next()).attr(WidgetRequestParam.REQ_PARAM_COMMENT_CONTENT);
            }
        }
        return Table.STRING_DEFAULT_VALUE;
    }

    protected String getContentThumbnailUri() {
        if (this.mHtmlDocument != null) {
            Iterator it = this.mHtmlDocument.select("meta[property=\"og:image\"").iterator();
            if (it.hasNext()) {
                return ((Element) it.next()).attr(WidgetRequestParam.REQ_PARAM_COMMENT_CONTENT);
            }
        }
        return null;
    }

    protected Uri getContentUri() {
        return Uri.parse(this.url);
    }

    protected CharSequence getPlainTextShareContent() {
        return new RichText().appendAsNewLineIfNotEmpty(getContentTitle()).appendAsNewLineIfNotEmpty(getContentUri()).appendAsNewLineIfNotEmpty(getContentDescription());
    }

    protected void setupViews() {
        setTitle((int) R.string.title_share_link);
        addBottomView(createBottomView());
    }

    protected void postToServer(String content) throws DataLoadException {
        try {
            new JsonClient("url/rec").post(((JsonRequestParam) ((JsonRequestParam) RequestParam.json().append(ShareUrlEditFragment_.URL_ARG, this.url)).appendIfNotEmpty("text", content)).appendShareTo(getShareTo()));
        } catch (Throwable e) {
            throw new DataLoadException(e);
        }
    }

    private View createBottomView() {
        TextView textView = new TextView(App.get(), null, R.style.AppWidget_Text_Secondary);
        Utils.changeFonts(textView);
        textView.setText(RichText.textWithColoredIcon((int) R.drawable.v_link, (int) R.array.blue, StringUtils.isNotEmpty(getContentTitle()) ? getContentTitle() : this.url));
        return textView;
    }
}
