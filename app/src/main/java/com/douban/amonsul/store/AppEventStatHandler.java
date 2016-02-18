package com.douban.amonsul.store;

import android.content.Context;
import com.douban.amonsul.MobileStat;
import com.douban.amonsul.StatLogger;
import com.douban.amonsul.StatPrefs;
import com.douban.amonsul.StatUtils;
import com.douban.amonsul.core.StatAccess;
import com.douban.amonsul.core.StatConfig;
import com.douban.amonsul.model.AppEventStoreFile;
import com.douban.amonsul.model.StatEvent;
import com.douban.amonsul.utils.FileUtils;
import java.util.Collections;
import java.util.List;

public class AppEventStatHandler extends EventHandler {
    private static final String APP_EVENT_STAT_FILENAME = "mobile_event_stat_file";
    private static final String FILE_INFO_INDEX_FILENAME = "mobile_file_index";
    private static final String SP_KEY_APP_EVENT_CNT = "sp_key_app_event_cnt";
    private static final String SP_KEY_FILE_CNT = "sp_key_file_number_cnt";
    private static final String TAG;
    private Context mContext;
    private EventDataKeeper mEventDataKeeper;
    private FileDataKeeper mFileDataKeeper;
    private final Object mLock;

    static {
        TAG = AppEventStatHandler.class.getName();
    }

    public AppEventStatHandler(Context context) {
        this.mLock = new Object();
        this.mContext = context;
        this.mEventDataKeeper = new EventDataKeeper(context, APP_EVENT_STAT_FILENAME);
        this.mFileDataKeeper = new FileDataKeeper(SP_KEY_FILE_CNT, FILE_INFO_INDEX_FILENAME);
    }

    public void saveEvent(StatEvent event) {
        if (event != null) {
            synchronized (this.mLock) {
                if (this.mEventDataKeeper.saveEvent(event)) {
                    StatPrefs sp = StatPrefs.getInstance(this.mContext);
                    sp.putInt(SP_KEY_APP_EVENT_CNT, sp.getInt(SP_KEY_APP_EVENT_CNT, 0) + 1);
                    StatAccess.getInstance(this.mContext).evtRecord();
                }
            }
        }
    }

    public int getEventsCnt() {
        return StatPrefs.getInstance(this.mContext).getInt(SP_KEY_APP_EVENT_CNT, 0);
    }

    public void cleanAllEvent() {
        if (MobileStat.DEBUG) {
            String renameFile = this.mEventDataKeeper.getFileName() + System.currentTimeMillis();
            StatLogger.d(TAG, " rename event delete file " + renameFile);
            FileUtils.renameFile(this.mContext, this.mEventDataKeeper.getFileName(), renameFile);
            this.mEventDataKeeper = new EventDataKeeper(this.mContext, APP_EVENT_STAT_FILENAME);
        } else {
            this.mEventDataKeeper.clearAllEvent();
        }
        StatPrefs.getInstance(this.mContext).putInt(SP_KEY_APP_EVENT_CNT, 0);
    }

    public List<StatEvent> getAllEvents() {
        return this.mEventDataKeeper.getAllEvents();
    }

    public String getEventJsonArrayStr() {
        return this.mEventDataKeeper.getEventJsonArrayString();
    }

    public void removeAllFiles() {
        this.mFileDataKeeper.removeAllFile(this.mContext);
    }

    private void fixFiles(StatConfig config) {
        if (this.mFileDataKeeper.getFileCnt(this.mContext) > config.getFileLimit()) {
            List<AppEventStoreFile> list = this.mFileDataKeeper.getAllFilesInfo(this.mContext);
            if (list != null) {
                int size = list.size();
                if (size != this.mFileDataKeeper.getFileCnt(this.mContext)) {
                    StatLogger.e(TAG, " ERROR file info number is not equal");
                    this.mFileDataKeeper.fixFileCnt(this.mContext);
                    StatAccess.getInstance(this.mContext).importEvtRecord("ERROR", "AppEvent event store File is not equal Index");
                } else if (size != 0) {
                    Collections.sort(list);
                    StatLogger.d(TAG, " try to remove extra file " + ((AppEventStoreFile) list.get(0)).getFileName() + " current file number " + this.mFileDataKeeper.getFileCnt(this.mContext));
                    if (FileUtils.removeFile(this.mContext, ((AppEventStoreFile) list.get(0)).getFileName(), ((AppEventStoreFile) list.get(0)).isSaveSD())) {
                        StatAccess.getInstance(this.mContext).importEvtRecord("WARN", "AppEvent event remove store file which not upload" + ((AppEventStoreFile) list.get(0)).getFileName());
                        list.remove(0);
                        this.mFileDataKeeper.resetFileInfoIndex(this.mContext, list);
                    }
                }
            }
        }
    }

    public void storeEventsToFile(StatConfig config) {
        if (getEventsCnt() >= config.getNumLimit()) {
            StatLogger.d(TAG, " store Event to File evt cnt " + getEventsCnt() + " numLimit " + config.getNumLimit());
            byte[] data = StatUtils.getEventsBytes(this.mContext, this.mEventDataKeeper.getEventJsonArrayString());
            StatAccess.getInstance(this.mContext).evtFileRecord();
            cleanAllEvent();
            this.mFileDataKeeper.saveDataToFile(this.mContext, data);
            fixFiles(config);
        }
    }

    public FileDataKeeper getFileDataKeeper() {
        return this.mFileDataKeeper;
    }
}
