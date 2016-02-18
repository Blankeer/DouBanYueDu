package com.douban.amonsul.store;

import android.content.Context;
import android.text.TextUtils;
import com.douban.amonsul.MobileStat;
import com.douban.amonsul.model.StatEvent;
import com.douban.amonsul.utils.FileUtils;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class EventDataKeeper {
    private static final String TAG;
    private Context mContext;
    private String mFileName;

    static {
        TAG = EventDataKeeper.class.getSimpleName();
    }

    public EventDataKeeper(Context context, String fileName) {
        this.mContext = context;
        this.mFileName = fileName;
    }

    public String getFileName() {
        return this.mFileName;
    }

    public boolean saveEvent(StatEvent event) {
        if (event == null) {
            return false;
        }
        return FileUtils.saveDataToLocalFile(this.mContext, this.mFileName, event.toJson(this.mContext, true).toString(), true);
    }

    public List<StatEvent> getAllEvents() {
        List<StatEvent> events = new ArrayList();
        FileInputStream fin = null;
        try {
            fin = this.mContext.openFileInput(this.mFileName);
            BufferedReader buffreader = new BufferedReader(new InputStreamReader(fin));
            for (String line = buffreader.readLine(); !TextUtils.isEmpty(line); line = buffreader.readLine()) {
                StatEvent stEvent = new StatEvent();
                stEvent.parseJson(line);
                events.add(stEvent);
            }
            if (fin != null) {
                try {
                    fin.close();
                } catch (Exception e) {
                    if (MobileStat.DEBUG) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e2) {
            if (MobileStat.DEBUG) {
                e2.printStackTrace();
            }
            if (fin != null) {
                try {
                    fin.close();
                } catch (Exception e22) {
                    if (MobileStat.DEBUG) {
                        e22.printStackTrace();
                    }
                }
            }
        } catch (Throwable th) {
            if (fin != null) {
                try {
                    fin.close();
                } catch (Exception e222) {
                    if (MobileStat.DEBUG) {
                        e222.printStackTrace();
                    }
                }
            }
        }
        return events;
    }

    public String getEventJsonArrayString() {
        FileInputStream fin = null;
        StringBuffer arrayString = new StringBuffer();
        try {
            fin = this.mContext.openFileInput(this.mFileName);
            BufferedReader buffreader = new BufferedReader(new InputStreamReader(fin));
            arrayString.append("[");
            for (String line = buffreader.readLine(); !TextUtils.isEmpty(line); line = buffreader.readLine()) {
                arrayString.append(line);
                arrayString.append(",");
            }
            arrayString.deleteCharAt(arrayString.length() - 1);
            arrayString.append("]");
            if (fin != null) {
                try {
                    fin.close();
                } catch (Exception e) {
                    if (MobileStat.DEBUG) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e2) {
            if (MobileStat.DEBUG) {
                e2.printStackTrace();
            }
            if (fin != null) {
                try {
                    fin.close();
                } catch (Exception e22) {
                    if (MobileStat.DEBUG) {
                        e22.printStackTrace();
                    }
                }
            }
        } catch (Throwable th) {
            if (fin != null) {
                try {
                    fin.close();
                } catch (Exception e222) {
                    if (MobileStat.DEBUG) {
                        e222.printStackTrace();
                    }
                }
            }
        }
        return arrayString.toString();
    }

    public void clearAllEvent() {
        try {
            this.mContext.deleteFile(this.mFileName);
        } catch (Exception e) {
            if (MobileStat.DEBUG) {
                e.printStackTrace();
            }
        }
    }
}
