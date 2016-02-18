package com.douban.book.reader.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import com.douban.book.reader.util.Logger;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class PreferenceManagerCompat {
    private static final String TAG;

    /* renamed from: com.douban.book.reader.fragment.PreferenceManagerCompat.1 */
    static class AnonymousClass1 implements InvocationHandler {
        final /* synthetic */ OnPreferenceTreeClickListener val$listener;

        AnonymousClass1(OnPreferenceTreeClickListener onPreferenceTreeClickListener) {
            this.val$listener = onPreferenceTreeClickListener;
        }

        public Object invoke(Object proxy, Method method, Object[] args) {
            if (method.getName().equals("onPreferenceTreeClick")) {
                return Boolean.valueOf(this.val$listener.onPreferenceTreeClick((PreferenceScreen) args[0], (Preference) args[1]));
            }
            return null;
        }
    }

    interface OnPreferenceTreeClickListener {
        boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference);
    }

    static {
        TAG = PreferenceManagerCompat.class.getSimpleName();
    }

    static PreferenceManager newInstance(Activity activity, int firstRequestCode) {
        try {
            Constructor<PreferenceManager> c = PreferenceManager.class.getDeclaredConstructor(new Class[]{Activity.class, Integer.TYPE});
            c.setAccessible(true);
            return (PreferenceManager) c.newInstance(new Object[]{activity, Integer.valueOf(firstRequestCode)});
        } catch (Exception e) {
            Logger.e(TAG, "Couldn't call constructor PreferenceManager by reflection", e);
            return null;
        }
    }

    static void setFragment(PreferenceManager manager, BasePreferenceFragment fragment) {
    }

    static void setOnPreferenceTreeClickListener(PreferenceManager manager, OnPreferenceTreeClickListener listener) {
        try {
            Field onPreferenceTreeClickListener = PreferenceManager.class.getDeclaredField("mOnPreferenceTreeClickListener");
            onPreferenceTreeClickListener.setAccessible(true);
            if (listener != null) {
                onPreferenceTreeClickListener.set(manager, Proxy.newProxyInstance(onPreferenceTreeClickListener.getType().getClassLoader(), new Class[]{onPreferenceTreeClickListener.getType()}, new AnonymousClass1(listener)));
                return;
            }
            onPreferenceTreeClickListener.set(manager, null);
        } catch (Exception e) {
            Logger.e(TAG, "Couldn't set PreferenceManager.mOnPreferenceTreeClickListener by reflection", e);
        }
    }

    static PreferenceScreen inflateFromIntent(PreferenceManager manager, Intent intent, PreferenceScreen screen) {
        try {
            Method m = PreferenceManager.class.getDeclaredMethod("inflateFromIntent", new Class[]{Intent.class, PreferenceScreen.class});
            m.setAccessible(true);
            return (PreferenceScreen) m.invoke(manager, new Object[]{intent, screen});
        } catch (Exception e) {
            Logger.e(TAG, "Couldn't call PreferenceManager.inflateFromIntent by reflection", e);
            return null;
        }
    }

    static PreferenceScreen inflateFromResource(PreferenceManager manager, Activity activity, int resId, PreferenceScreen screen) {
        try {
            Method m = PreferenceManager.class.getDeclaredMethod("inflateFromResource", new Class[]{Context.class, Integer.TYPE, PreferenceScreen.class});
            m.setAccessible(true);
            return (PreferenceScreen) m.invoke(manager, new Object[]{activity, Integer.valueOf(resId), screen});
        } catch (Exception e) {
            Logger.e(TAG, "Couldn't call PreferenceManager.inflateFromResource by reflection", e);
            return null;
        }
    }

    static PreferenceScreen getPreferenceScreen(PreferenceManager manager) {
        try {
            Method m = PreferenceManager.class.getDeclaredMethod("getPreferenceScreen", new Class[0]);
            m.setAccessible(true);
            return (PreferenceScreen) m.invoke(manager, new Object[0]);
        } catch (Exception e) {
            Logger.e(TAG, "Couldn't call PreferenceManager.getPreferenceScreen by reflection", e);
            return null;
        }
    }

    static void dispatchActivityResult(PreferenceManager manager, int requestCode, int resultCode, Intent data) {
        try {
            Method m = PreferenceManager.class.getDeclaredMethod("dispatchActivityResult", new Class[]{Integer.TYPE, Integer.TYPE, Intent.class});
            m.setAccessible(true);
            m.invoke(manager, new Object[]{Integer.valueOf(requestCode), Integer.valueOf(resultCode), data});
        } catch (Exception e) {
            Logger.e(TAG, "Couldn't call PreferenceManager.dispatchActivityResult by reflection", e);
        }
    }

    static void dispatchActivityStop(PreferenceManager manager) {
        try {
            Method m = PreferenceManager.class.getDeclaredMethod("dispatchActivityStop", new Class[0]);
            m.setAccessible(true);
            m.invoke(manager, new Object[0]);
        } catch (Exception e) {
            Logger.e(TAG, "Couldn't call PreferenceManager.dispatchActivityStop by reflection", e);
        }
    }

    static void dispatchActivityDestroy(PreferenceManager manager) {
        try {
            Method m = PreferenceManager.class.getDeclaredMethod("dispatchActivityDestroy", new Class[0]);
            m.setAccessible(true);
            m.invoke(manager, new Object[0]);
        } catch (Exception e) {
            Logger.e(TAG, "Couldn't call PreferenceManager.dispatchActivityDestroy by reflection", e);
        }
    }

    static boolean setPreferences(PreferenceManager manager, PreferenceScreen screen) {
        try {
            Method m = PreferenceManager.class.getDeclaredMethod("setPreferences", new Class[]{PreferenceScreen.class});
            m.setAccessible(true);
            return ((Boolean) m.invoke(manager, new Object[]{screen})).booleanValue();
        } catch (Exception e) {
            Logger.e(TAG, "Couldn't call PreferenceManager.setPreferences by reflection", e);
            return false;
        }
    }
}
