package com.douban.book.reader.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import com.douban.book.reader.app.App;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.controller.TaskController;
import com.douban.book.reader.entity.DummyEntity;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.network.client.RestClient;
import com.douban.book.reader.network.exception.RestException;
import com.douban.book.reader.network.param.RequestParam;
import com.igexin.sdk.PushManagerReceiver;
import com.igexin.sdk.PushReceiver;
import com.igexin.sdk.PushService;
import com.igexin.sdk.PushServiceUser;
import com.tencent.connect.common.Constants;
import io.realm.internal.Table;
import java.util.ArrayList;
import java.util.List;

public class PushManager {
    private static final String TAG;
    private static com.igexin.sdk.PushManager sGexinInstance;
    private static RestClient<DummyEntity> sPushClient;

    /* renamed from: com.douban.book.reader.util.PushManager.1 */
    static class AnonymousClass1 implements Runnable {
        final /* synthetic */ String val$clientId;

        AnonymousClass1(String str) {
            this.val$clientId = str;
        }

        public void run() {
            try {
                PushManager.sPushClient.put(RequestParam.json().append(Constants.PARAM_CLIENT_ID, this.val$clientId));
                Pref.ofApp().set(Key.APP_PUSH_SERVICE_STATE_SYNCED, Boolean.valueOf(true));
            } catch (RestException e) {
                Logger.e(PushManager.TAG, e);
            }
        }
    }

    static {
        TAG = PushManager.class.getSimpleName();
        sGexinInstance = com.igexin.sdk.PushManager.getInstance();
        sPushClient = new RestClient("push_notification/igetui_client", DummyEntity.class);
    }

    public static void init() {
        if (isPushEnabled()) {
            start();
        } else {
            stop();
        }
    }

    public static void stop() {
        Logger.d(TAG, "stop service", new Object[0]);
        App app = App.get();
        Pref.ofApp().set(Key.APP_PUSH_SERVICE_STATE_SYNCED, Boolean.valueOf(false));
        sGexinInstance.turnOffPush(app);
        sGexinInstance.stopService(app);
        unregisterDevices();
        setEnablePushComponentInApp(app, false);
    }

    public static void start() {
        Logger.d(TAG, "start service", new Object[0]);
        App app = App.get();
        setEnablePushComponentInApp(app, true);
        Pref.ofApp().set(Key.APP_PUSH_SERVICE_STATE_SYNCED, Boolean.valueOf(false));
        sGexinInstance.initialize(app);
        sGexinInstance.turnOnPush(app);
    }

    public static boolean isPushEnabled() {
        return Pref.ofApp().getBoolean(Key.SETTING_ENABLE_PUSH_SERVICE, true);
    }

    public static void registerDevice(String clientId) {
        CharSequence oldClientId = Pref.ofApp().getString(Key.APP_PUSH_SERVICE_CLIENT_ID, Table.STRING_DEFAULT_VALUE);
        if (!(Pref.ofApp().getBoolean(Key.APP_PUSH_SERVICE_STATE_SYNCED, false) && StringUtils.equals(oldClientId, (CharSequence) clientId))) {
            Pref.ofApp().set(Key.APP_PUSH_SERVICE_CLIENT_ID, clientId);
            asyncRegister(clientId);
        }
        bindAlias();
    }

    public static void unregisterDevices() {
        Pref.ofApp().remove(Key.APP_PUSH_SERVICE_CLIENT_ID);
        asyncUnregister();
        unbindAlias();
    }

    private static void bindAlias() {
        try {
            sGexinInstance.bindAlias(App.get(), String.valueOf(UserManager.getInstance().getUserId()));
        } catch (Throwable e) {
            Logger.ec(TAG, e, "Failed to bind alias for getui.", new Object[0]);
        }
    }

    private static void unbindAlias() {
        try {
            sGexinInstance.unBindAlias(App.get(), String.valueOf(UserManager.getInstance().getUserId()), true);
        } catch (Throwable e) {
            Logger.ec(TAG, e, "Failed to bind alias for getui.", new Object[0]);
        }
    }

    private static void asyncRegister(String clientId) {
        TaskController.run(new AnonymousClass1(clientId));
    }

    private static void asyncUnregister() {
        TaskController.run(new Runnable() {
            public void run() {
                try {
                    PushManager.sPushClient.deleteEntity();
                    Pref.ofApp().set(Key.APP_PUSH_SERVICE_STATE_SYNCED, Boolean.valueOf(true));
                } catch (RestException e) {
                    Logger.e(PushManager.TAG, e);
                }
            }
        });
    }

    private static void setEnablePushComponentInApp(Context context, boolean enabled) {
        PackageManager pManager = context.getPackageManager();
        List<ComponentName> componentNames = new ArrayList();
        componentNames.add(new ComponentName(context, PushService.class));
        componentNames.add(new ComponentName(context, PushServiceUser.class));
        componentNames.add(new ComponentName(context, PushReceiver.class));
        componentNames.add(new ComponentName(context, PushManagerReceiver.class));
        if (enabled) {
            try {
                for (ComponentName componentName : componentNames) {
                    pManager.setComponentEnabledSetting(componentName, 0, 1);
                }
                Logger.d(TAG, "Enabled push related components", new Object[0]);
                return;
            } catch (Exception e) {
                Logger.e(TAG, e);
                return;
            }
        }
        for (ComponentName componentName2 : componentNames) {
            pManager.setComponentEnabledSetting(componentName2, 2, 1);
        }
        Logger.d(TAG, "Disabled push related components", new Object[0]);
    }
}
