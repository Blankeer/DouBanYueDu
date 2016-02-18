package com.igexin.download;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class e implements ServiceConnection {
    final /* synthetic */ DownloadService a;

    public e(DownloadService downloadService) {
        this.a = downloadService;
    }

    public void a() {
        synchronized (this.a) {
            if (this.a.h != null) {
                this.a.h = null;
                try {
                    this.a.unbindService(this);
                } catch (IllegalArgumentException e) {
                }
            }
        }
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        this.a.g = false;
        synchronized (this.a) {
            try {
                Method method = Class.forName("android.media.IMediaScannerService").getField("Stub").getType().getMethod("asInterface", new Class[]{IBinder.class});
                this.a.h = method.invoke(null, new Object[]{iBinder});
                if (this.a.h != null) {
                    this.a.a();
                }
            } catch (ClassNotFoundException e) {
            } catch (SecurityException e2) {
            } catch (NoSuchFieldException e3) {
            } catch (NoSuchMethodException e4) {
            } catch (IllegalArgumentException e5) {
            } catch (IllegalAccessException e6) {
            } catch (InvocationTargetException e7) {
            }
        }
    }

    public void onServiceDisconnected(ComponentName componentName) {
        synchronized (this.a) {
            this.a.h = null;
        }
    }
}
