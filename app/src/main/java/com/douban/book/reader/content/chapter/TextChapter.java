package com.douban.book.reader.content.chapter;

import android.content.Context;
import android.text.SpannableStringBuilder;
import com.crashlytics.android.Crashlytics;
import com.douban.book.reader.content.PageMetrics;
import com.douban.book.reader.content.page.PageInfo;
import com.douban.book.reader.content.page.ParagraphIterator;
import com.douban.book.reader.content.page.Position;
import com.douban.book.reader.content.page.Range;
import com.douban.book.reader.content.paragraph.Paragraph;
import com.douban.book.reader.exception.ManifestException;
import com.douban.book.reader.exception.PagingException;
import com.douban.book.reader.util.AssertUtils;
import com.douban.book.reader.util.WatchDogTimer;
import com.douban.book.reader.util.WatchDogTimer.Callback;
import com.douban.book.reader.util.WatchDogTimer.TimedOutException;
import com.douban.book.reader.view.page.AbsPageView;
import com.douban.book.reader.view.page.TextPageView;
import com.mcxiaoke.next.ui.widget.AdvancedShareActionProvider;

public class TextChapter extends ContentChapter {
    private final WatchDogTimer mWatchDogTimer;

    public TextChapter(int bookId, int packageId) throws ManifestException {
        super(bookId, packageId);
        this.mWatchDogTimer = new WatchDogTimer(new Callback() {
            public void onTimedOut(Object extra) {
                Crashlytics.logException(new TimedOutException(String.format("Paging for paragraph %s timed out. works_id=%s, pageMetrics=%s", new Object[]{extra, Integer.valueOf(TextChapter.this.getWorksId()), PageMetrics.getLast()})));
            }
        });
    }

    public void onPaging(ParagraphIterator iterator, PageMetrics pageMetrics) throws PagingException, InterruptedException {
        PageInfo pageInfo = null;
        float textAreaWidth = pageMetrics.width;
        float textAreaHeight = TextPageView.getTextAreaHeight(pageMetrics.height);
        float remainedHeight = textAreaHeight;
        float height = 0.0f;
        float lastParagraphBottomPadding = 0.0f;
        int pageNo = 0;
        while (iterator.hasNext()) {
            int pageNo2;
            AssertUtils.throwIfInterrupted(this);
            Paragraph paragraph = iterator.next();
            paragraph.setWidth(textAreaWidth);
            try {
                this.mWatchDogTimer.startWatching(120000, Integer.valueOf(paragraph.getId()));
                paragraph.generateWithThrow();
                height = paragraph.getHeight();
                float topPadding = paragraph.getPaddingTop();
                float bottomPadding = paragraph.getPaddingBottom();
                if (lastParagraphBottomPadding > 0.0f && topPadding > 0.0f) {
                    remainedHeight += Math.min(lastParagraphBottomPadding, topPadding);
                }
                lastParagraphBottomPadding = bottomPadding;
                if (pageInfo == null) {
                    pageInfo = createDefaultPageInfo();
                    pageInfo.startParaIndex = iterator.getParagraphIndex();
                    pageInfo.startParaId = iterator.getParagraphId();
                    pageInfo.startLine = 0;
                    pageInfo.startOffset = 0;
                }
                if (paragraph.getType() == 3) {
                    remainedHeight = textAreaHeight;
                    pageInfo.endParaIndex = iterator.getParagraphIndex();
                    pageInfo.endParaId = iterator.getParagraphId();
                    pageInfo.endOffset = 0;
                    pageNo2 = pageNo + 1;
                    appendPageInfo(pageNo, pageInfo);
                    pageInfo = null;
                    pageNo = pageNo2;
                } else {
                    if (height > remainedHeight && remainedHeight > textAreaHeight / 2.0f && !paragraph.isPagable()) {
                        height = paragraph.getHeight();
                        if (height > remainedHeight - 1.0f && paragraph.getMinHeight() <= remainedHeight - 1.0f) {
                            height = remainedHeight - 1.0f;
                        }
                    }
                    int startLine = 0;
                    pageNo2 = pageNo;
                    while (height > remainedHeight) {
                        AssertUtils.throwIfInterrupted(this);
                        startLine = paragraph.getPagableLineNum(startLine, remainedHeight - bottomPadding);
                        int startOffset = paragraph.getCharOffsetByLineNum(startLine);
                        if (startLine > 0) {
                            pageInfo.endParaIndex = iterator.getParagraphIndex();
                            pageInfo.endParaId = iterator.getParagraphId();
                            pageInfo.endLine = startLine;
                            pageInfo.endOffset = Math.max(0, startOffset - 1);
                            height = paragraph.getHeight(startLine);
                        } else {
                            pageInfo.endParaIndex = iterator.getParagraphIndex() - 1;
                            Paragraph lastParagraph = iterator.getLastParagraph();
                            if (lastParagraph != null) {
                                pageInfo.endParaId = lastParagraph.getId();
                                pageInfo.endLine = AdvancedShareActionProvider.WEIGHT_MAX;
                                pageInfo.endOffset = Math.max(lastParagraph.getText().length() - 1, 0);
                            }
                        }
                        pageNo = pageNo2 + 1;
                        appendPageInfo(pageNo2, pageInfo);
                        pageInfo = createDefaultPageInfo();
                        pageInfo.startParaIndex = iterator.getParagraphIndex();
                        pageInfo.startParaId = iterator.getParagraphId();
                        pageInfo.startLine = startLine;
                        pageInfo.startOffset = startOffset;
                        remainedHeight = textAreaHeight;
                        if (!paragraph.isPagable()) {
                            height = paragraph.getHeight();
                            if (height > remainedHeight - 1.0f) {
                                height = remainedHeight - 1.0f;
                                pageNo2 = pageNo;
                            }
                        }
                        pageNo2 = pageNo;
                    }
                    remainedHeight -= height;
                    pageNo = pageNo2;
                }
            } finally {
                this.mWatchDogTimer.stopWatching();
            }
        }
        if (height > 0.0f) {
            pageInfo.endParaIndex = iterator.getParagraphIndex();
            pageInfo.endParaId = iterator.getParagraphId();
            pageInfo.endLine = AdvancedShareActionProvider.WEIGHT_MAX;
            pageInfo.endOffset = AdvancedShareActionProvider.WEIGHT_MAX;
            pageNo2 = pageNo + 1;
            appendPageInfo(pageNo, pageInfo);
            pageNo = pageNo2;
        }
    }

    public AbsPageView getPageView(Context context, int pageNum) {
        return new TextPageView(context);
    }

    public CharSequence getText(Range range) {
        int startPara;
        int startOffset;
        int endPara;
        int endOffset;
        Position start = range.startPosition;
        Position end = range.endPosition;
        if (start.packageId == this.mPackageId && start.isValid()) {
            startPara = start.paragraphIndex;
            startOffset = start.paragraphOffset;
        } else {
            startPara = 0;
            startOffset = 0;
        }
        if (end.packageId == this.mPackageId && end.isValid()) {
            endPara = end.paragraphIndex;
            endOffset = end.paragraphOffset < AdvancedShareActionProvider.WEIGHT_MAX ? end.paragraphOffset + 1 : end.paragraphOffset;
        } else {
            endPara = getParagraphCount() - 1;
            endOffset = AdvancedShareActionProvider.WEIGHT_MAX;
        }
        SpannableStringBuilder sb = new SpannableStringBuilder();
        int i = start.paragraphIndex;
        while (i <= end.paragraphIndex) {
            Paragraph paragraph = getParagraph(i);
            if (paragraph != null && paragraph.canPinStop()) {
                sb.append(paragraph.getPrintableText(i == startPara ? startOffset : 0, i == endPara ? endOffset : AdvancedShareActionProvider.WEIGHT_MAX));
                if (i < endPara) {
                    sb.append("\n");
                }
            }
            i++;
        }
        return sb;
    }
}
