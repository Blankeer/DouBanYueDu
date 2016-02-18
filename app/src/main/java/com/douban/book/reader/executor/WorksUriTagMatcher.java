package com.douban.book.reader.executor;

import android.net.Uri;
import com.douban.book.reader.executor.TaggedRunnableExecutor.TagMatcher;
import com.douban.book.reader.util.ReaderUriUtils;

public class WorksUriTagMatcher implements TagMatcher {
    private int mWorksId;

    public WorksUriTagMatcher(int worksId) {
        this.mWorksId = worksId;
    }

    public boolean match(Object tagInExecutor) {
        return (tagInExecutor instanceof Uri) && this.mWorksId == ReaderUriUtils.getWorksId((Uri) tagInExecutor);
    }
}
