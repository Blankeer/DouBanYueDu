package com.douban.book.reader.content.page;

import com.douban.book.reader.content.pack.WorksData;
import com.douban.book.reader.content.paragraph.Paragraph;
import com.douban.book.reader.exception.PagingException;
import com.douban.book.reader.helper.WorksListUri;
import com.douban.book.reader.util.IOUtils;
import com.douban.book.reader.util.JsonUtils;
import com.google.gson.stream.JsonReader;
import com.sina.weibo.sdk.component.WidgetRequestParam;
import io.fabric.sdk.android.services.network.HttpRequest;
import java.io.InputStreamReader;
import java.util.Map;
import org.json.JSONObject;

public class ParagraphIterator {
    private Map<Integer, String> mCacheMap;
    private int mCurPos;
    private Paragraph mLastParagraph;
    private Map<Integer, Integer> mParaIndexMap;
    private Paragraph mParagraph;
    private JsonReader mReader;

    public ParagraphIterator(int worksId, int packageId, Map<Integer, String> cacheMap, Map<Integer, Integer> paraIndexMap) throws Exception {
        this.mCacheMap = null;
        this.mParaIndexMap = null;
        this.mParagraph = null;
        this.mLastParagraph = null;
        this.mCurPos = 0;
        this.mCacheMap = cacheMap;
        this.mParaIndexMap = paraIndexMap;
        this.mReader = new JsonReader(new InputStreamReader(WorksData.get(worksId).getPackage(packageId).getInputStream(WidgetRequestParam.REQ_PARAM_COMMENT_CONTENT), HttpRequest.CHARSET_UTF8));
        this.mReader.beginArray();
    }

    public boolean hasNext() throws PagingException {
        try {
            return this.mReader.hasNext();
        } catch (Throwable e) {
            PagingException pagingException = new PagingException(e);
        }
    }

    public Paragraph next() throws PagingException {
        try {
            JSONObject json = JsonUtils.readJSONObject(this.mReader);
            this.mLastParagraph = this.mParagraph;
            this.mParagraph = Paragraph.parse(json);
            if (this.mCacheMap != null) {
                this.mCacheMap.put(Integer.valueOf(this.mCurPos), json.toString());
            }
            if (this.mParaIndexMap != null) {
                this.mParaIndexMap.put(Integer.valueOf(json.optInt(WorksListUri.KEY_ID)), Integer.valueOf(this.mCurPos));
            }
            this.mCurPos++;
            return this.mParagraph;
        } catch (Throwable e) {
            PagingException pagingException = new PagingException(e);
        }
    }

    public int getParagraphIndex() {
        return this.mCurPos - 1;
    }

    public int getParagraphId() {
        if (this.mParagraph != null) {
            return this.mParagraph.getId();
        }
        return 0;
    }

    public Paragraph getLastParagraph() {
        return this.mLastParagraph;
    }

    public void close() {
        IOUtils.closeSilently(this.mReader);
    }
}
