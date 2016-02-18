package com.sina.weibo.sdk.cmd;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.sina.weibo.sdk.component.ShareRequestParam;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.NetUtils;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.utils.AesEncrypt;
import com.sina.weibo.sdk.utils.LogUtil;
import com.sina.weibo.sdk.utils.Utility;
import com.umeng.analytics.a;
import io.fabric.sdk.android.services.network.HttpRequest;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class WbAppActivator {
    private static final String TAG;
    private static WbAppActivator mInstance;
    private String mAppkey;
    private Context mContext;
    private AppInstallCmdExecutor mInstallExecutor;
    private AppInvokeCmdExecutor mInvokeExecutor;
    private volatile ReentrantLock mLock;

    /* renamed from: com.sina.weibo.sdk.cmd.WbAppActivator.1 */
    class AnonymousClass1 implements Runnable {
        private final /* synthetic */ SharedPreferences val$sdkSp;

        AnonymousClass1(SharedPreferences sharedPreferences) {
            this.val$sdkSp = sharedPreferences;
        }

        public void run() {
            WeiboException e;
            Throwable th;
            LogUtil.v(WbAppActivator.TAG, "mLock.isLocked()--->" + WbAppActivator.this.mLock.isLocked());
            if (WbAppActivator.this.mLock.tryLock()) {
                CmdInfo cmds = null;
                try {
                    String result = WbAppActivator.requestCmdInfo(WbAppActivator.this.mContext, WbAppActivator.this.mAppkey);
                    if (result != null) {
                        CmdInfo cmds2 = new CmdInfo(AesEncrypt.Decrypt(result));
                        try {
                            WbAppActivator.this.handleInstallCmd(cmds2.getInstallCmds());
                            WbAppActivator.this.handleInvokeCmd(cmds2.getInvokeCmds());
                            cmds = cmds2;
                        } catch (WeiboException e2) {
                            e = e2;
                            cmds = cmds2;
                            try {
                                LogUtil.e(WbAppActivator.TAG, e.getMessage());
                                WbAppActivator.this.mLock.unlock();
                                if (cmds != null) {
                                    FrequencyHelper.saveFrequency(WbAppActivator.this.mContext, this.val$sdkSp, (long) cmds.getFrequency());
                                    FrequencyHelper.saveLastTime(WbAppActivator.this.mContext, this.val$sdkSp, System.currentTimeMillis());
                                }
                                LogUtil.v(WbAppActivator.TAG, "after unlock \n mLock.isLocked()--->" + WbAppActivator.this.mLock.isLocked());
                            } catch (Throwable th2) {
                                th = th2;
                                WbAppActivator.this.mLock.unlock();
                                if (cmds != null) {
                                    FrequencyHelper.saveFrequency(WbAppActivator.this.mContext, this.val$sdkSp, (long) cmds.getFrequency());
                                    FrequencyHelper.saveLastTime(WbAppActivator.this.mContext, this.val$sdkSp, System.currentTimeMillis());
                                }
                                LogUtil.v(WbAppActivator.TAG, "after unlock \n mLock.isLocked()--->" + WbAppActivator.this.mLock.isLocked());
                                throw th;
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            cmds = cmds2;
                            WbAppActivator.this.mLock.unlock();
                            if (cmds != null) {
                                FrequencyHelper.saveFrequency(WbAppActivator.this.mContext, this.val$sdkSp, (long) cmds.getFrequency());
                                FrequencyHelper.saveLastTime(WbAppActivator.this.mContext, this.val$sdkSp, System.currentTimeMillis());
                            }
                            LogUtil.v(WbAppActivator.TAG, "after unlock \n mLock.isLocked()--->" + WbAppActivator.this.mLock.isLocked());
                            throw th;
                        }
                    }
                    WbAppActivator.this.mLock.unlock();
                    if (cmds != null) {
                        FrequencyHelper.saveFrequency(WbAppActivator.this.mContext, this.val$sdkSp, (long) cmds.getFrequency());
                        FrequencyHelper.saveLastTime(WbAppActivator.this.mContext, this.val$sdkSp, System.currentTimeMillis());
                    }
                    LogUtil.v(WbAppActivator.TAG, "after unlock \n mLock.isLocked()--->" + WbAppActivator.this.mLock.isLocked());
                } catch (WeiboException e3) {
                    e = e3;
                    LogUtil.e(WbAppActivator.TAG, e.getMessage());
                    WbAppActivator.this.mLock.unlock();
                    if (cmds != null) {
                        FrequencyHelper.saveFrequency(WbAppActivator.this.mContext, this.val$sdkSp, (long) cmds.getFrequency());
                        FrequencyHelper.saveLastTime(WbAppActivator.this.mContext, this.val$sdkSp, System.currentTimeMillis());
                    }
                    LogUtil.v(WbAppActivator.TAG, "after unlock \n mLock.isLocked()--->" + WbAppActivator.this.mLock.isLocked());
                }
            }
        }
    }

    private static class FrequencyHelper {
        private static final int DEFAULT_FREQUENCY = 3600000;
        private static final String KEY_FREQUENCY = "frequency_get_cmd";
        private static final String KEY_LAST_TIME_GET_CMD = "last_time_get_cmd";
        private static final String WEIBO_SDK_PREFERENCES_NAME = "com_sina_weibo_sdk";

        private FrequencyHelper() {
        }

        public static SharedPreferences getWeiboSdkSp(Context ctx) {
            return ctx.getSharedPreferences(WEIBO_SDK_PREFERENCES_NAME, 0);
        }

        public static long getFrequency(Context ctx, SharedPreferences sp) {
            if (sp != null) {
                return sp.getLong(KEY_FREQUENCY, a.h);
            }
            return a.h;
        }

        public static void saveFrequency(Context ctx, SharedPreferences sp, long frequency) {
            if (sp != null && frequency > 0) {
                Editor editor = sp.edit();
                editor.putLong(KEY_FREQUENCY, frequency);
                editor.commit();
            }
        }

        public static long getLastTime(Context ctx, SharedPreferences sp) {
            if (sp != null) {
                return sp.getLong(KEY_LAST_TIME_GET_CMD, 0);
            }
            return 0;
        }

        public static void saveLastTime(Context ctx, SharedPreferences sp, long lastTime) {
            if (sp != null) {
                Editor editor = sp.edit();
                editor.putLong(KEY_LAST_TIME_GET_CMD, lastTime);
                editor.commit();
            }
        }
    }

    static {
        TAG = WbAppActivator.class.getName();
    }

    private WbAppActivator(Context ctx, String appkey) {
        this.mLock = new ReentrantLock(true);
        this.mContext = ctx.getApplicationContext();
        this.mInvokeExecutor = new AppInvokeCmdExecutor(this.mContext);
        this.mInstallExecutor = new AppInstallCmdExecutor(this.mContext);
        this.mAppkey = appkey;
    }

    public static synchronized WbAppActivator getInstance(Context ctx, String appkey) {
        WbAppActivator wbAppActivator;
        synchronized (WbAppActivator.class) {
            if (mInstance == null) {
                mInstance = new WbAppActivator(ctx, appkey);
            }
            wbAppActivator = mInstance;
        }
        return wbAppActivator;
    }

    public void activateApp() {
        SharedPreferences sdkSp = FrequencyHelper.getWeiboSdkSp(this.mContext);
        if (System.currentTimeMillis() - FrequencyHelper.getLastTime(this.mContext, sdkSp) < FrequencyHelper.getFrequency(this.mContext, sdkSp)) {
            LogUtil.v(TAG, String.format("it's only %d ms from last time get cmd", new Object[]{Long.valueOf(period)}));
            return;
        }
        new Thread(new AnonymousClass1(sdkSp)).start();
    }

    private static String requestCmdInfo(Context ctx, String appkey) {
        String url = "http://api.weibo.cn/2/client/common_config";
        String pkgName = ctx.getPackageName();
        String keyHash = Utility.getSign(ctx, pkgName);
        WeiboParameters params = new WeiboParameters(appkey);
        params.put("appkey", appkey);
        params.put(ShareRequestParam.REQ_PARAM_PACKAGENAME, pkgName);
        params.put(ShareRequestParam.REQ_PARAM_KEY_HASH, keyHash);
        params.put(ShareRequestParam.REQ_PARAM_VERSION, WBConstants.WEIBO_SDK_VERSION_CODE);
        return NetUtils.internalHttpRequest(ctx, "http://api.weibo.cn/2/client/common_config", HttpRequest.METHOD_GET, params);
    }

    private void handleInstallCmd(List<AppInstallCmd> installCmds) {
        if (installCmds != null) {
            this.mInstallExecutor.start();
            for (AppInstallCmd installCmd : installCmds) {
                this.mInstallExecutor.doExecutor(installCmd);
            }
            this.mInstallExecutor.stop();
        }
    }

    private void handleInvokeCmd(List<AppInvokeCmd> invokeCmds) {
        if (invokeCmds != null) {
            for (AppInvokeCmd invokeCmd : invokeCmds) {
                this.mInvokeExecutor.doExecutor(invokeCmd);
            }
        }
    }
}
