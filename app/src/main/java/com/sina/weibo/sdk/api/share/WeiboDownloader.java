package com.sina.weibo.sdk.api.share;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.utils.Utility;

public class WeiboDownloader {
    private static final String CANCEL_CHINESS = "\u4ee5\u540e\u518d\u8bf4";
    private static final String CANCEL_ENGLISH = "Download Later";
    private static final String OK_CHINESS = "\u73b0\u5728\u4e0b\u8f7d";
    private static final String OK_ENGLISH = "Download Now";
    private static final String PROMPT_CHINESS = "\u672a\u5b89\u88c5\u5fae\u535a\u5ba2\u6237\u7aef\uff0c\u662f\u5426\u73b0\u5728\u53bb\u4e0b\u8f7d\uff1f";
    private static final String PROMPT_ENGLISH = "Sina Weibo client is not installed, download now?";
    private static final String TITLE_CHINESS = "\u63d0\u793a";
    private static final String TITLE_ENGLISH = "Notice";

    /* renamed from: com.sina.weibo.sdk.api.share.WeiboDownloader.1 */
    class AnonymousClass1 implements OnClickListener {
        private final /* synthetic */ Context val$context;

        AnonymousClass1(Context context) {
            this.val$context = context;
        }

        public void onClick(DialogInterface dialog, int which) {
            WeiboDownloader.downloadWeibo(this.val$context);
        }
    }

    /* renamed from: com.sina.weibo.sdk.api.share.WeiboDownloader.2 */
    class AnonymousClass2 implements OnClickListener {
        private final /* synthetic */ IWeiboDownloadListener val$listener;

        AnonymousClass2(IWeiboDownloadListener iWeiboDownloadListener) {
            this.val$listener = iWeiboDownloadListener;
        }

        public void onClick(DialogInterface dialog, int which) {
            if (this.val$listener != null) {
                this.val$listener.onCancel();
            }
        }
    }

    public static Dialog createDownloadConfirmDialog(Context context, IWeiboDownloadListener listener) {
        String title = TITLE_CHINESS;
        String prompt = PROMPT_CHINESS;
        String ok = OK_CHINESS;
        String cancel = CANCEL_CHINESS;
        if (!Utility.isChineseLocale(context.getApplicationContext())) {
            title = TITLE_ENGLISH;
            prompt = PROMPT_ENGLISH;
            ok = OK_ENGLISH;
            cancel = CANCEL_ENGLISH;
        }
        return new Builder(context).setMessage(prompt).setTitle(title).setPositiveButton(ok, new AnonymousClass1(context)).setNegativeButton(cancel, new AnonymousClass2(listener)).create();
    }

    private static void downloadWeibo(Context context) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setFlags(268435456);
        intent.setData(Uri.parse(WBConstants.WEIBO_DOWNLOAD_URL));
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
