package com.douban.amonsul.core;

import android.content.Context;
import com.douban.amonsul.MobileStat;
import com.douban.amonsul.StatConstant;
import com.douban.amonsul.StatPrefs;
import org.json.JSONObject;

public class StatConfig {
    private static final String TAG;
    private boolean mAvailable;
    private int mFileLimit;
    private int mNumLimit;
    private long mTimeLimit;

    static {
        TAG = StatConfig.class.getSimpleName();
    }

    public StatConfig(String json) {
        boolean z = true;
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (Integer.parseInt(jsonObject.opt("on").toString()) != 1) {
                z = false;
            }
            this.mAvailable = z;
            this.mNumLimit = Integer.parseInt(jsonObject.opt("num_limit").toString());
            this.mTimeLimit = Long.parseLong(jsonObject.opt("time_limit").toString());
            this.mFileLimit = 20;
        } catch (Exception ex) {
            if (MobileStat.DEBUG) {
                ex.printStackTrace();
            }
        }
    }

    public StatConfig() {
        this.mAvailable = true;
        this.mNumLimit = StatConstant.DEFAULT_MAX_EVENT_COUNT;
        this.mTimeLimit = 86400;
        this.mFileLimit = 20;
    }

    public StatConfig(boolean available, int numLimit, long timeLimit) {
        this.mAvailable = available;
        this.mNumLimit = numLimit;
        this.mTimeLimit = timeLimit;
    }

    public boolean isAvailable() {
        return this.mAvailable;
    }

    public void setAvailable(boolean available) {
        this.mAvailable = available;
    }

    public int getNumLimit() {
        return this.mNumLimit;
    }

    public void setNumLimit(int numLimit) {
        this.mNumLimit = numLimit;
    }

    public long getTimeLimit() {
        return this.mTimeLimit;
    }

    public void setTimeLimit(long timeLimit) {
        this.mTimeLimit = timeLimit;
    }

    public void setFileLimit(int fileLimit) {
        this.mFileLimit = fileLimit;
    }

    public int getFileLimit() {
        return this.mFileLimit;
    }

    public void init(Context context) {
        StatPrefs sp = StatPrefs.getInstance(context);
        setNumLimit(sp.getInt(StatConstant.KEY_MAX_EVENT_COUNT, StatConstant.DEFAULT_MAX_EVENT_COUNT));
        setTimeLimit(sp.getLong(StatConstant.KEY_MAX_FORCE_UPLOAD_SECOND, 86400));
        setAvailable(sp.getBoolean(StatConstant.KEY_STAT_AVAILABLE, true));
        this.mFileLimit = 20;
    }

    public void saveConfig(Context context) {
        StatPrefs sp = StatPrefs.getInstance(context);
        sp.putInt(StatConstant.KEY_MAX_EVENT_COUNT, getNumLimit());
        sp.putLong(StatConstant.KEY_MAX_FORCE_UPLOAD_SECOND, getTimeLimit());
        sp.putBoolean(StatConstant.KEY_STAT_AVAILABLE, isAvailable());
    }

    public String toString() {
        return "StatConfig{available=" + this.mAvailable + ", numLimit=" + this.mNumLimit + ", timeLimit=" + this.mTimeLimit + '}';
    }
}
