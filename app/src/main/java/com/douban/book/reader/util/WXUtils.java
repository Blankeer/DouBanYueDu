package com.douban.book.reader.util;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.exception.WeixinShareException;
import com.douban.book.reader.manager.SessionManager;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.tencent.mm.sdk.modelmsg.SendAuth.Req;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXUtils {
    private static String TAG = null;
    private static final int TIME_LINE_SUPPORTED_VERSION = 553779201;
    private static final int WX_IMAGE_SIZE = 400;
    public static final String WX_PACKAGE_NAME = "com.tencent.mm";
    private static final int WX_THUMBNAIL_SIZE = 96;
    private static Bitmap sDefaultLinkBitmap;
    private static final IWXAPI sWxApi;

    public static class MessageBuilder {
        private WXMediaMessage mMediaMessage;
        private int mScene;
        private String mShareType;

        public MessageBuilder() {
            this.mMediaMessage = new WXMediaMessage();
            this.mScene = 0;
            this.mShareType = null;
        }

        public MessageBuilder scene(int scene) {
            this.mScene = scene;
            return this;
        }

        public MessageBuilder shareType(String shareType) {
            this.mShareType = shareType;
            return this;
        }

        public MessageBuilder title(String title) {
            this.mMediaMessage.title = title;
            return this;
        }

        public MessageBuilder description(CharSequence description) {
            this.mMediaMessage.description = String.valueOf(description);
            return this;
        }

        public MessageBuilder thumbnail(Uri url) {
            return thumbnail(StringUtils.toStr(url));
        }

        public MessageBuilder thumbnail(String url) {
            if (StringUtils.isNotEmpty(url)) {
                Bitmap bitmap = ImageLoaderUtils.loadImageSync(url, new ImageSize(WXUtils.WX_THUMBNAIL_SIZE, WXUtils.WX_THUMBNAIL_SIZE));
                if (bitmap != null) {
                    this.mMediaMessage.thumbData = BitmapUtils.getCompressedBits(bitmap, CompressFormat.JPEG, 85);
                }
            }
            return this;
        }

        public MessageBuilder url(Uri uri) {
            return url(StringUtils.toStr(uri));
        }

        public MessageBuilder url(String url) {
            String shareUrl = AnalysisUtils.buildShareUrl(url, this.mShareType, AnalysisUtils.SHARE_CHANNEL_WEIXIN, this.mScene == 1 ? AnalysisUtils.SHARE_METHOD_TIMELINE : AnalysisUtils.SHARE_METHOD_FRIEND);
            WXWebpageObject object = new WXWebpageObject();
            object.webpageUrl = shareUrl;
            this.mMediaMessage.mediaObject = object;
            return this;
        }

        public MessageBuilder text(String text) {
            WXTextObject object = new WXTextObject();
            object.text = String.valueOf(text);
            this.mMediaMessage.mediaObject = object;
            return this;
        }

        public MessageBuilder imageData(Uri url) {
            return imageData(String.valueOf(url));
        }

        public MessageBuilder imageData(String url) {
            Bitmap bitmap = ImageLoaderUtils.loadImageSync(url, new ImageSize(WXUtils.WX_IMAGE_SIZE, WXUtils.WX_IMAGE_SIZE));
            WXImageObject object = new WXImageObject();
            if (bitmap != null) {
                object.imageData = BitmapUtils.getCompressedBits(bitmap, CompressFormat.JPEG, 85);
            }
            this.mMediaMessage.mediaObject = object;
            return this;
        }

        public WXMediaMessage build() {
            return this.mMediaMessage;
        }
    }

    static {
        TAG = WXUtils.class.getSimpleName();
        sWxApi = WXAPIFactory.createWXAPI(App.get(), SessionManager.OPENID_APPID_WEIXIN, true);
        sWxApi.registerApp(SessionManager.OPENID_APPID_WEIXIN);
    }

    public static boolean isTimeLineSupported() {
        boolean z = true;
        if (sWxApi == null) {
            return false;
        }
        int wxSdkVersion = sWxApi.getWXAppSupportAPI();
        Logger.d(TAG, String.format("isSupportTimeLine() wxSdkVersion %s ", new Object[]{Integer.toHexString(wxSdkVersion)}), new Object[0]);
        if (wxSdkVersion < TIME_LINE_SUPPORTED_VERSION) {
            z = false;
        }
        return z;
    }

    private static IWXAPI getApi() {
        return sWxApi;
    }

    public static void handleIntent(Intent intent, IWXAPIEventHandler handler) {
        if (sWxApi != null) {
            sWxApi.handleIntent(intent, handler);
        }
    }

    public static void sendLoginRequest() {
        Req req = new Req();
        req.scope = "snsapi_userinfo";
        getApi().sendReq(req);
    }

    private static Bitmap getDefaultLinkBitmap() {
        if (sDefaultLinkBitmap == null) {
            sDefaultLinkBitmap = createDefaultLinkBitmap();
        }
        return sDefaultLinkBitmap;
    }

    private static Bitmap createDefaultLinkBitmap() {
        try {
            Bitmap bitmap = Bitmap.createBitmap(100, 100, Config.RGB_565);
            Drawable drawable = Res.getDrawableWithTint((int) R.drawable.v_link, (int) R.color.palette_day_blue);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(-1);
            CanvasUtils.drawDrawableCenteredInArea(canvas, drawable, new RectF(0.0f, 0.0f, 100.0f, 100.0f));
            return bitmap;
        } catch (Throwable e) {
            Logger.e(TAG, e);
            return null;
        }
    }

    public static String shareToWeixin(int scene, WXMediaMessage msg) throws WeixinShareException {
        if (sWxApi == null) {
            return null;
        }
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.message = msg;
        req.scene = scene;
        req.transaction = String.valueOf(System.currentTimeMillis());
        if (sWxApi.sendReq(req)) {
            return req.transaction;
        }
        throw new WeixinShareException(String.format("Failed to share to weixin. target=%s, messageType=%s, title=%s, description=%s", new Object[]{Integer.valueOf(req.scene), Integer.valueOf(msg.getType()), msg.title, msg.description}));
    }
}
