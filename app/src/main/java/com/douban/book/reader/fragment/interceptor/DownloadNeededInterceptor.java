package com.douban.book.reader.fragment.interceptor;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import com.douban.book.reader.R;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.content.pack.WorksData;
import com.douban.book.reader.content.pack.WorksData.Status;
import com.douban.book.reader.fragment.AlertDialogFragment.Builder;
import com.douban.book.reader.task.DownloadManager;
import com.douban.book.reader.task.SyncManager;
import com.douban.book.reader.util.ReaderUri;
import com.douban.book.reader.util.ToastUtils;

public class DownloadNeededInterceptor implements Interceptor {
    private int mWorksId;

    public DownloadNeededInterceptor(int worksId) {
        this.mWorksId = worksId;
    }

    public void performShowAsActivity(PageOpenHelper helper, Intent intent) {
        if (WorksData.get(this.mWorksId).getStatus() != Status.READY) {
            new Builder().setMessage((int) R.string.dialog_message_works_download_need).setPositiveButton((int) R.string.dialog_button_download, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    DownloadManager.scheduleDownload(ReaderUri.works(DownloadNeededInterceptor.this.mWorksId));
                    SyncManager.sync(DownloadNeededInterceptor.this.mWorksId);
                    ToastUtils.showToast((int) R.string.toast_download_started);
                }
            }).setNegativeButton((int) R.string.dialog_button_cancel, null).create().show();
        } else {
            helper.open(intent);
        }
    }
}
