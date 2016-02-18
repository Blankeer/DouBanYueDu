package com.douban.amonsul.store;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import com.douban.amonsul.MobileStat;
import com.douban.amonsul.StatLogger;
import com.douban.amonsul.StatPrefs;
import com.douban.amonsul.model.AppEventStoreFile;
import com.douban.amonsul.utils.FileUtils;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileDataKeeper {
    private static final String TAG;
    private final String mIndexFileName;
    private final String mSpInfoKey;

    static {
        TAG = FileDataKeeper.class.getName();
    }

    public FileDataKeeper(String spKey, String indexFileName) {
        this.mSpInfoKey = spKey;
        this.mIndexFileName = indexFileName;
    }

    public boolean saveDataToFile(Context context, byte[] data) {
        String state = Environment.getExternalStorageState();
        long now = System.currentTimeMillis();
        String filePath = createFileName(context);
        AppEventStoreFile fileInfo = new AppEventStoreFile(filePath, now, true);
        if ("mounted".equals(state)) {
            FileUtils.saveDataToSD(context, fileInfo.getFileName(), data);
            if (!TextUtils.isEmpty(filePath)) {
                addStoreFileInfoIndex(context, fileInfo);
                return true;
            }
        }
        fileInfo.setSaveSD(false);
        if (!FileUtils.saveDataToLocalFile(context, filePath, data)) {
            return false;
        }
        addStoreFileInfoIndex(context, fileInfo);
        return true;
    }

    private void addStoreFileInfoIndex(Context context, AppEventStoreFile fileInfo) {
        synchronized (this.mIndexFileName) {
            if (FileUtils.saveDataToLocalFile(context, this.mIndexFileName, fileInfo.toJson().toString(), true)) {
                StatPrefs.getInstance(context).putInt(this.mSpInfoKey, StatPrefs.getInstance(context).getInt(this.mSpInfoKey, 0) + 1);
            }
        }
    }

    public void removeAllFile(Context context) {
        for (AppEventStoreFile storeFile : getAllFilesInfo(context)) {
            StatLogger.d(TAG, " Delete file " + storeFile.getFileName() + " inSD " + storeFile.isSaveSD() + " ret " + FileUtils.removeFile(context, storeFile.getFileName(), storeFile.isSaveSD()));
        }
        context.deleteFile(this.mIndexFileName);
        StatPrefs.getInstance(context).putInt(this.mSpInfoKey, 0);
    }

    public int getFileCnt(Context context) {
        return StatPrefs.getInstance(context).getInt(this.mSpInfoKey, 0);
    }

    public void resetFileInfoIndex(Context context, List<AppEventStoreFile> list) {
        if (list != null) {
            StringBuffer strBuffer = new StringBuffer();
            for (AppEventStoreFile fileInfo : list) {
                if (!TextUtils.isEmpty(fileInfo.toJson().toString())) {
                    strBuffer.append(fileInfo.toJson().toString());
                    if (list.lastIndexOf(fileInfo) != list.size() - 1) {
                        strBuffer.append(System.getProperty("line.separator"));
                    }
                }
            }
            StatLogger.d(TAG, " reset statFile Info index:[" + strBuffer.toString() + "]");
            synchronized (this.mIndexFileName) {
                FileUtils.saveDataToLocalFile(context, this.mIndexFileName, strBuffer.toString(), false);
                StatPrefs.getInstance(context).putInt(this.mSpInfoKey, list.size());
            }
        }
    }

    public List<AppEventStoreFile> getAllFilesInfo(Context context) {
        List<AppEventStoreFile> fileList = new ArrayList();
        FileInputStream fin = null;
        try {
            fin = context.openFileInput(this.mIndexFileName);
            BufferedReader buffreader = new BufferedReader(new InputStreamReader(fin));
            for (String line = buffreader.readLine(); !TextUtils.isEmpty(line); line = buffreader.readLine()) {
                AppEventStoreFile file = new AppEventStoreFile();
                file.parseJson(line);
                fileList.add(file);
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
        return fileList;
    }

    public void fixFileCnt(Context context) {
        StatPrefs.getInstance(context).putInt(this.mSpInfoKey, getAllFilesInfo(context).size());
    }

    private String createFileName(Context context) {
        return "amonsul_stat_" + String.valueOf(System.currentTimeMillis()) + ".dat";
    }
}
