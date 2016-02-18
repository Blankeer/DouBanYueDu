package com.douban.book.reader.fragment;

import com.douban.book.reader.R;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.content.Book;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.util.Pref;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;

@EFragment
public class WorksUgcFragment extends TabFragment {
    private Book mBook;
    @Bean
    WorksManager mWorksManager;
    @FragmentArg
    boolean pagingNeeded;
    @FragmentArg
    int worksId;

    public WorksUgcFragment() {
        this.mBook = null;
        setOffScreenPageLimit(2);
        setTitle((int) R.string.title_note);
    }

    @AfterViews
    void init() {
        loadData();
    }

    public void onDetach() {
        super.onDetach();
        if (this.mBook != null) {
            this.mBook.closeBook();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @org.androidannotations.annotations.Background
    void loadData() {
        /*
        r6 = this;
        r1 = 1;
        r6.showLoadingDialog();	 Catch:{ Exception -> 0x0052 }
        r3 = r6.worksId;	 Catch:{ Exception -> 0x0052 }
        com.douban.book.reader.task.SyncManager.sync(r3);	 Catch:{ Exception -> 0x0052 }
        r3 = r6.pagingNeeded;	 Catch:{ Exception -> 0x0052 }
        if (r3 == 0) goto L_0x0049;
    L_0x000d:
        r3 = r6.mWorksManager;	 Catch:{ Exception -> 0x0052 }
        r4 = r6.worksId;	 Catch:{ Exception -> 0x0052 }
        r2 = r3.getWorks(r4);	 Catch:{ Exception -> 0x0052 }
        r3 = r2.isGallery();	 Catch:{ Exception -> 0x0052 }
        if (r3 != 0) goto L_0x0050;
    L_0x001b:
        r1 = 1;
    L_0x001c:
        r3 = r6.worksId;	 Catch:{ Exception -> 0x0052 }
        r3 = com.douban.book.reader.content.Book.get(r3);	 Catch:{ Exception -> 0x0052 }
        r6.mBook = r3;	 Catch:{ Exception -> 0x0052 }
        r3 = r6.mBook;	 Catch:{ Exception -> 0x0052 }
        r3.openBook();	 Catch:{ Exception -> 0x0052 }
        r3 = r6.mBook;	 Catch:{ Exception -> 0x0052 }
        r4 = r6.getActivity();	 Catch:{ Exception -> 0x0052 }
        r4 = com.douban.book.reader.content.PageMetrics.getFromActivity(r4);	 Catch:{ Exception -> 0x0052 }
        r5 = 0;
        r3.paging(r4, r5);	 Catch:{ Exception -> 0x0052 }
        r3 = r6.worksId;	 Catch:{ Exception -> 0x0052 }
        r3 = com.douban.book.reader.manager.BookmarkManager.ofWorks(r3);	 Catch:{ Exception -> 0x0052 }
        r3.updateIndex();	 Catch:{ Exception -> 0x0052 }
        r3 = r6.worksId;	 Catch:{ Exception -> 0x0052 }
        r3 = com.douban.book.reader.manager.AnnotationManager.ofWorks(r3);	 Catch:{ Exception -> 0x0052 }
        r3.updateIndex();	 Catch:{ Exception -> 0x0052 }
    L_0x0049:
        r6.dismissLoadingDialog();
    L_0x004c:
        r6.updateViews(r1);
        return;
    L_0x0050:
        r1 = 0;
        goto L_0x001c;
    L_0x0052:
        r0 = move-exception;
        r3 = r6.TAG;	 Catch:{ all -> 0x005c }
        com.douban.book.reader.util.Logger.e(r3, r0);	 Catch:{ all -> 0x005c }
        r6.dismissLoadingDialog();
        goto L_0x004c;
    L_0x005c:
        r3 = move-exception;
        r6.dismissLoadingDialog();
        throw r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.douban.book.reader.fragment.WorksUgcFragment.loadData():void");
    }

    @UiThread
    void updateViews(boolean showAnnotation) {
        appendTab(BookmarkFragment_.builder().worksId(this.worksId).build().setIcon((int) R.drawable.v_bookmark));
        appendTabIf(showAnnotation, UnderlineFragment_.builder().worksId(this.worksId).build().setIcon((int) R.drawable.v_underline));
        appendTabIf(showAnnotation, NoteFragment_.builder().worksId(this.worksId).build().setIcon((int) R.drawable.v_note));
        setDefaultPage(Pref.ofApp().getInt(Key.APP_UGC_DEFAULT_TAB, 0));
    }

    public boolean shouldFinish() {
        Pref.ofApp().set(Key.APP_UGC_DEFAULT_TAB, Integer.valueOf(getCurrentTabIndex()));
        return super.shouldFinish();
    }
}
