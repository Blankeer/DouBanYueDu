package com.igexin.download;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import u.aly.dx;

class j extends Handler {
    final /* synthetic */ SdkDownLoader a;

    j(SdkDownLoader sdkDownLoader, Looper looper) {
        this.a = sdkDownLoader;
        super(looper);
    }

    public void handleMessage(Message message) {
        switch (message.what) {
            case dx.c /*2*/:
                synchronized (this.a.h) {
                    if (this.a.g.size() > 0 && this.a.updateData.size() > 0) {
                        for (DownloadInfo downloadInfo : this.a.updateData.values()) {
                            IDownloadCallback a = this.a.a(downloadInfo.mData8);
                            if (a != null) {
                                a.update(downloadInfo);
                            }
                        }
                    }
                    break;
                }
            default:
        }
    }
}
