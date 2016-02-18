package com.douban.book.reader.event;

import com.douban.book.reader.activity.BaseActivity;
import com.douban.book.reader.fragment.BaseDialogFragment;
import com.douban.book.reader.fragment.BaseFragment;
import com.douban.book.reader.util.Logger;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.EventBusException;

public final class EventBusUtils {
    private static final String TAG;

    static {
        TAG = EventBusUtils.class.getSimpleName();
    }

    public static void register(Object object) {
        try {
            EventBus.getDefault().register(object);
            Logger.d(TAG, "registered: %s", object);
        } catch (EventBusException e) {
            if (!(object instanceof BaseActivity) && !(object instanceof BaseFragment) && !(object instanceof BaseDialogFragment)) {
                Logger.e(TAG, e);
            }
        }
    }

    public static void unregister(Object object) {
        try {
            EventBus.getDefault().unregister(object);
            Logger.d(TAG, "unregistered: %s", object);
        } catch (EventBusException e) {
            Logger.e(TAG, e);
        }
    }

    public static void post(Object event) {
        try {
            EventBus.getDefault().post(event);
            Logger.d(TAG, "Posted: %s", event);
        } catch (Throwable e) {
            Logger.e(TAG, e);
        }
    }
}
