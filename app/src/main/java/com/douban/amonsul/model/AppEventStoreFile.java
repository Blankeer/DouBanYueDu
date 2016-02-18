package com.douban.amonsul.model;

import com.douban.amonsul.MobileStat;
import com.douban.amonsul.StatLogger;
import org.json.JSONObject;

public class AppEventStoreFile implements Comparable {
    private static final String JSON_KEY_CREATETIME = "ct";
    private static final String JSON_KEY_FILEPATH = "fp";
    private static final String JSON_KEY_SAVESD = "svsd";
    private static final String TAG;
    private long mCreateTime;
    private String mFileName;
    private boolean mSaveSD;

    static {
        TAG = AppEventStoreFile.class.getSimpleName();
    }

    public AppEventStoreFile(String fileName, long createTime, boolean saveSD) {
        this.mFileName = fileName;
        this.mCreateTime = createTime;
        this.mSaveSD = saveSD;
    }

    public String getFileName() {
        return this.mFileName;
    }

    public void setFileName(String filePath) {
        this.mFileName = filePath;
    }

    public boolean isSaveSD() {
        return this.mSaveSD;
    }

    public void setSaveSD(boolean saveSD) {
        this.mSaveSD = saveSD;
    }

    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        try {
            obj.put(JSON_KEY_FILEPATH, this.mFileName);
            obj.put(JSON_KEY_CREATETIME, this.mCreateTime);
            obj.put(JSON_KEY_SAVESD, this.mSaveSD);
        } catch (Exception e) {
            if (MobileStat.DEBUG) {
                e.printStackTrace();
            }
        }
        return obj;
    }

    public void parseJson(String jsonStr) {
        try {
            JSONObject obj = new JSONObject(jsonStr);
            this.mFileName = obj.optString(JSON_KEY_FILEPATH);
            this.mCreateTime = obj.optLong(JSON_KEY_CREATETIME);
            this.mSaveSD = obj.optBoolean(JSON_KEY_SAVESD);
        } catch (Throwable e) {
            StatLogger.e(TAG, e);
        }
    }

    public int compareTo(Object another) {
        if (((AppEventStoreFile) another).mCreateTime > this.mCreateTime) {
            return 0;
        }
        return 1;
    }
}
