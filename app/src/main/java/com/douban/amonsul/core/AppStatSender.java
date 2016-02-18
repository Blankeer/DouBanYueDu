package com.douban.amonsul.core;

import android.content.Context;
import com.douban.amonsul.MobileStat;
import com.douban.amonsul.StatConstant;
import com.douban.amonsul.StatLogger;
import com.douban.amonsul.StatPrefs;
import com.douban.amonsul.StatUtils;
import com.douban.amonsul.core.StatSender.SendCallback;
import com.douban.amonsul.model.AppEventStoreFile;
import com.douban.amonsul.network.NetWorker;
import com.douban.amonsul.store.AppEventStatHandler;
import com.douban.amonsul.store.EventHandler;
import com.douban.amonsul.utils.FileUtils;
import java.util.ArrayList;
import java.util.List;

public class AppStatSender extends StatSender {
    private static final String TAG;

    static {
        TAG = AppStatSender.class.getName();
    }

    public boolean isSendStat(Context context, EventHandler eventHandler, StatConfig config) {
        boolean isTime;
        if ((System.currentTimeMillis() - StatPrefs.getInstance(context).getLong(StatConstant.KEY_LAST_UPLOAD, 0)) / 1000 > config.getTimeLimit()) {
            isTime = true;
        } else {
            isTime = false;
        }
        if (eventHandler.getEventsCnt() >= config.getNumLimit() || isTime) {
            return true;
        }
        return false;
    }

    public void sendMobileStat(Context context, EventHandler eventHandler, NetWorker networker, SendCallback callback) {
        this.mIsWorking = true;
        if (!(eventHandler instanceof AppEventStatHandler)) {
            callback.onResult(1);
        }
        StatLogger.d(TAG, " app statSender mobile stat ");
        StatPrefs.getInstance(context).putLong(StatConstant.KEY_LAST_UPLOAD, System.currentTimeMillis());
        AppEventStatHandler appHandler = (AppEventStatHandler) eventHandler;
        byte[] data = StatUtils.getEventsBytes(context, appHandler.getEventJsonArrayStr());
        int retCode = 1;
        if (data != null && networker.sendEventsData(context, data) == 0) {
            if (MobileStat.DEBUG) {
                StatLogger.d(TAG, " app statSender send event ok clear all events ");
                StatAccess.getInstance(context).evtUpload(appHandler.getEventsCnt());
            }
            appHandler.cleanAllEvent();
            retCode = 0;
        }
        if (appHandler.getFileDataKeeper().getFileCnt(context) > 0) {
            List<AppEventStoreFile> flist = appHandler.getFileDataKeeper().getAllFilesInfo(context);
            List<AppEventStoreFile> newList = new ArrayList();
            for (int index = 0; index < flist.size(); index++) {
                AppEventStoreFile file = (AppEventStoreFile) flist.get(index);
                byte[] filedatas = FileUtils.getFileData(context, file.getFileName(), file.isSaveSD());
                if (filedatas != null) {
                    if (networker.sendEventsData(context, filedatas) == 0) {
                        FileUtils.removeFile(context, file.getFileName(), file.isSaveSD());
                        StatAccess.getInstance(context).evtFileUpload();
                    } else {
                        newList.add(file);
                    }
                }
            }
            appHandler.getFileDataKeeper().resetFileInfoIndex(context, newList);
        }
        this.mIsWorking = false;
        if (callback != null) {
            callback.onResult(retCode);
        }
    }
}
