package com.douban.amonsul.store;

import android.content.Context;
import com.douban.amonsul.StatLogger;
import com.douban.amonsul.StatPrefs;
import com.douban.amonsul.core.StatAccess;
import com.douban.amonsul.model.StatEvent;
import java.util.List;

public class CrashEventStatHandler extends EventHandler {
    private static final String EVENT_DATA_KEEP_FILE_NAME = "mobile_stat_crash_file";
    private static final String KEY_CRASH_EVENT_LOCAL_CNT = "key_crash_event_local_cnt";
    private static final String TAG;
    private Context mContext;
    private EventDataKeeper mEventDataKeeper;
    private final Object mLock;

    static {
        TAG = CrashEventStatHandler.class.getSimpleName();
    }

    public CrashEventStatHandler(Context context) {
        this.mLock = new Object();
        this.mContext = context;
        this.mEventDataKeeper = new EventDataKeeper(context, EVENT_DATA_KEEP_FILE_NAME);
    }

    public void saveEvent(StatEvent evt) {
        synchronized (this.mLock) {
            if (evt != null) {
                if (getEventsCnt() >= 100) {
                    cleanAllEvent();
                    StatLogger.d(TAG, " crash evt cnt exceed delete local file ");
                    StatAccess.getInstance(this.mContext).importEvtRecord(TAG, " crash evt cnt exceed delete local file ");
                }
                this.mEventDataKeeper.saveEvent(evt);
                StatPrefs.getInstance(this.mContext).putInt(KEY_CRASH_EVENT_LOCAL_CNT, StatPrefs.getInstance(this.mContext).getInt(KEY_CRASH_EVENT_LOCAL_CNT, 0) + 1);
                StatAccess.getInstance(this.mContext).crashRecord();
            }
        }
    }

    public int getEventsCnt() {
        return StatPrefs.getInstance(this.mContext).getInt(KEY_CRASH_EVENT_LOCAL_CNT, 0);
    }

    public void cleanAllEvent() {
        this.mEventDataKeeper.clearAllEvent();
        StatPrefs.getInstance(this.mContext).putInt(KEY_CRASH_EVENT_LOCAL_CNT, 0);
    }

    public List<StatEvent> getAllEvents() {
        return this.mEventDataKeeper.getAllEvents();
    }

    public String getEventJsonArrayStr() {
        return this.mEventDataKeeper.getEventJsonArrayString();
    }
}
