package com.douban.amonsul.core;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import com.douban.amonsul.MobileStat;
import com.douban.amonsul.StatConstant;
import com.douban.amonsul.StatLogger;
import com.douban.amonsul.StatPrefs;
import com.douban.amonsul.StatUtils;
import com.douban.amonsul.core.CrashTrackHandler.CrashCallback;
import com.douban.amonsul.model.StatEvent;
import com.douban.amonsul.network.NetWorker;
import com.douban.amonsul.network.Response;
import com.douban.amonsul.store.AppEventStatHandler;
import com.douban.amonsul.store.CrashEventStatHandler;
import com.douban.amonsul.store.EventHandler;
import io.realm.internal.Table;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;

public class MobileStatManager {
    private static final int BLOCK_QUEUE_MAX_SIZE = 3;
    private static final int HANDLE_MSG_DISPATCH_EVENT = 1;
    private static final int HANDLE_MSG_SEND_CRASH = 2;
    public static final String TAG;
    private static MobileStatManager mInstance;
    private AppEventStatHandler mAppEventHandler;
    private AppStatSender mAppStatSender;
    private boolean mBindConfig;
    private Context mContext;
    private EventHandler mCrashEventHandler;
    private StatSender mCrashStatSender;
    private boolean mDispatchEventing;
    private Handler mMsgHandler;
    private NetWorker mNetWorker;
    private StatConfig mStatConfig;
    private ThreadPoolExecutor mThreadPool;

    /* renamed from: com.douban.amonsul.core.MobileStatManager.2 */
    class AnonymousClass2 implements Runnable {
        final /* synthetic */ StatEvent val$statEvent;

        AnonymousClass2(StatEvent statEvent) {
            this.val$statEvent = statEvent;
        }

        public void run() {
            MobileStatManager.this.dispatchEvent(this.val$statEvent);
        }
    }

    /* renamed from: com.douban.amonsul.core.MobileStatManager.3 */
    class AnonymousClass3 implements Runnable {
        final /* synthetic */ StatEvent val$statEvent;

        AnonymousClass3(StatEvent statEvent) {
            this.val$statEvent = statEvent;
        }

        public void run() {
            if (MobileStatManager.this.mNetWorker.sendEvent(MobileStatManager.this.mContext, this.val$statEvent) != 0) {
                MobileStatManager.this.dispatchEvent(this.val$statEvent);
                StatAccess.getInstance(MobileStatManager.this.mContext).importEvtRecord("ERROR", "send real Time event failed");
            } else if (MobileStat.DEBUG) {
                StatAccess.getInstance(MobileStatManager.this.mContext).importEvtRecord("INFO", " send real Time event id " + this.val$statEvent.getId());
                StatAccess.getInstance(MobileStatManager.this.mContext).realTimeEvtRecord();
                StatAccess.getInstance(MobileStatManager.this.mContext).evtUpload(MobileStatManager.HANDLE_MSG_DISPATCH_EVENT);
            }
        }
    }

    /* renamed from: com.douban.amonsul.core.MobileStatManager.4 */
    class AnonymousClass4 implements Runnable {
        final /* synthetic */ CrashCallback val$callback;
        final /* synthetic */ Throwable val$ex;

        AnonymousClass4(Throwable th, CrashCallback crashCallback) {
            this.val$ex = th;
            this.val$callback = crashCallback;
        }

        public void run() {
            if (this.val$ex != null) {
                StatLogger.d(MobileStatManager.TAG, " handle exception ");
                StringBuffer traceback = new StringBuffer();
                if (this.val$ex.getStackTrace() != null) {
                    traceback.append(StatUtils.stackTraceToString(this.val$ex.getStackTrace()));
                    traceback.append("\n\t");
                }
                traceback.append(StatUtils.getCauseTrace(this.val$ex));
                if (this.val$callback != null) {
                    this.val$callback.onCrash(traceback.toString());
                }
                MobileStatManager.this.mCrashEventHandler.saveEvent(StatEvent.setupEvent(MobileStatManager.this.mContext, StatConstant.STAT_EVENT_ID_ERROR, traceback.toString(), MobileStatManager.HANDLE_MSG_DISPATCH_EVENT));
            }
        }
    }

    static {
        TAG = MobileStatManager.class.getName();
    }

    MobileStatManager(Context ctx) {
        this.mBindConfig = false;
        this.mDispatchEventing = false;
        this.mMsgHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (MobileStatManager.this.mThreadPool.getQueue().size() >= MobileStatManager.BLOCK_QUEUE_MAX_SIZE || MobileStatManager.this.mDispatchEventing) {
                    Message msg2 = new Message();
                    msg2.obj = msg.obj;
                    msg2.what = msg.what;
                    MobileStatManager.this.mMsgHandler.sendMessageDelayed(msg2, 100);
                    return;
                }
                switch (msg.what) {
                    case MobileStatManager.HANDLE_MSG_DISPATCH_EVENT /*1*/:
                        MobileStatManager.this.doDispatchEvent(msg.obj);
                    case MobileStatManager.HANDLE_MSG_SEND_CRASH /*2*/:
                        MobileStatManager.this.doSendCrashInfo();
                    default:
                }
            }
        };
        Context context = ctx.getApplicationContext();
        this.mContext = context;
        StatPrefs.getInstance(context);
        this.mThreadPool = new ThreadPoolExecutor(HANDLE_MSG_DISPATCH_EVENT, 4, 2, TimeUnit.SECONDS, new ArrayBlockingQueue(BLOCK_QUEUE_MAX_SIZE), new CallerRunsPolicy());
        this.mCrashEventHandler = new CrashEventStatHandler(context);
        this.mCrashStatSender = new CrashStatSender();
        this.mAppEventHandler = new AppEventStatHandler(context);
        this.mAppStatSender = new AppStatSender();
        this.mNetWorker = new NetWorker();
        this.mStatConfig = new StatConfig();
        this.mStatConfig.init(context);
        CrashTrackHandler.getInstance().init(context);
        doRequestConfig();
    }

    public static synchronized MobileStatManager getInstance(Context context) {
        MobileStatManager mobileStatManager;
        synchronized (MobileStatManager.class) {
            if (mInstance == null) {
                mInstance = new MobileStatManager(context);
            }
            mobileStatManager = mInstance;
        }
        return mobileStatManager;
    }

    public void onEvent(Context context, String eventId, String extra, int count, String action, boolean realTime) {
        onEvent(context, eventId, extra, count, action, realTime, Table.STRING_DEFAULT_VALUE);
    }

    public void onEvent(Context context, String eventId, String extra, int count, String action, boolean realTime, String attributes) {
        StatEvent evt = StatEvent.setupEvent(context, eventId, extra, count, action, attributes);
        StatPrefs sp = StatPrefs.getInstance(context);
        if (MobileStat.DEBUG) {
            StatLogger.v(TAG, "onEvent() " + evt);
        }
        boolean firstRun = sp.getBoolean(StatConstant.PRE_KEY_SP_FIRST_RUN, true);
        if (firstRun || realTime) {
            if (firstRun) {
                sp.putBoolean(StatConstant.PRE_KEY_SP_FIRST_RUN, false);
            }
            doPostRealTimeEvent(evt);
            return;
        }
        Message msg = new Message();
        msg.obj = evt;
        msg.what = HANDLE_MSG_DISPATCH_EVENT;
        this.mMsgHandler.sendMessage(msg);
    }

    private void doDispatchEvent(StatEvent statEvent) {
        this.mThreadPool.execute(new AnonymousClass2(statEvent));
    }

    private void doPostRealTimeEvent(StatEvent statEvent) {
        this.mThreadPool.execute(new AnonymousClass3(statEvent));
    }

    private void dispatchEvent(StatEvent statEvent) {
        this.mDispatchEventing = true;
        this.mAppEventHandler.saveEvent(statEvent);
        if (StatUtils.isNetworkAvailable(this.mContext)) {
            sendEventInfo();
            Message msg = new Message();
            msg.what = HANDLE_MSG_SEND_CRASH;
            this.mMsgHandler.sendMessage(msg);
            this.mDispatchEventing = false;
            return;
        }
        this.mAppEventHandler.storeEventsToFile(this.mStatConfig);
        this.mDispatchEventing = false;
    }

    public void onCreate(Context context) {
        doRequestConfig();
        onEvent(context, StatConstant.STAT_EVENT_ID_ONLAUNCH, Table.STRING_DEFAULT_VALUE, HANDLE_MSG_DISPATCH_EVENT, Table.STRING_DEFAULT_VALUE, true);
    }

    public void doHandleException(Throwable ex, CrashCallback callback) {
        this.mThreadPool.execute(new AnonymousClass4(ex, callback));
    }

    private void doSendCrashInfo() {
        this.mThreadPool.execute(new Runnable() {
            public void run() {
                if (StatUtils.isNetworkAvailable(MobileStatManager.this.mContext) && MobileStatManager.this.mCrashStatSender.isSendStat(MobileStatManager.this.mContext, MobileStatManager.this.mCrashEventHandler, MobileStatManager.this.mStatConfig)) {
                    MobileStatManager.this.mCrashStatSender.sendMobileStat(MobileStatManager.this.mContext, MobileStatManager.this.mCrashEventHandler, MobileStatManager.this.mNetWorker, null);
                }
            }
        });
    }

    private void sendEventInfo() {
        if (StatUtils.isNetworkAvailable(this.mContext) && this.mAppStatSender.isSendStat(this.mContext, this.mAppEventHandler, this.mStatConfig)) {
            this.mAppStatSender.sendMobileStat(this.mContext, this.mAppEventHandler, this.mNetWorker, null);
        }
    }

    public void doRequestConfig() {
        if (this.mBindConfig) {
            StatLogger.d(TAG, " not requestConfig for bind config");
        } else {
            this.mThreadPool.execute(new Runnable() {
                public void run() {
                    try {
                        Response res = MobileStatManager.this.mNetWorker.requestConfig(MobileStatManager.this.mContext);
                        if (res != null && !MobileStatManager.this.mBindConfig) {
                            StatConfig config = new StatConfig(res.getResponseContent());
                            MobileStatManager.this.mStatConfig = config;
                            MobileStatManager.this.mStatConfig.saveConfig(MobileStatManager.this.mContext);
                            StatLogger.d(MobileStatManager.TAG, " getConfig from server" + config.toString());
                        }
                    } catch (Exception e) {
                        if (MobileStat.DEBUG) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    public void bindConfig(StatConfig config) {
        if (config != null) {
            this.mStatConfig = config;
            this.mBindConfig = true;
        }
    }

    public void unBindConfig() {
        this.mStatConfig = null;
        this.mStatConfig = new StatConfig();
        this.mStatConfig.init(this.mContext);
        this.mBindConfig = false;
    }

    public StatConfig getStatConfig() {
        return this.mStatConfig;
    }

    protected EventHandler getCrashEventHandler() {
        return this.mCrashEventHandler;
    }

    protected EventHandler getAppEventHandler() {
        return this.mAppEventHandler;
    }

    public void cleanAllRecord() {
        this.mAppEventHandler.removeAllFiles();
        this.mAppEventHandler.cleanAllEvent();
        this.mCrashEventHandler.cleanAllEvent();
    }
}
