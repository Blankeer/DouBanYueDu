package natalya.io;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.realm.internal.Table;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import natalya.net.Crawler;

public class FileUtils {

    /* renamed from: natalya.io.FileUtils.1 */
    static class AnonymousClass1 implements FilenameFilter {
        final /* synthetic */ String val$title;

        AnonymousClass1(String str) {
            this.val$title = str;
        }

        public boolean accept(File file, String s) {
            return s.substring(s.lastIndexOf("/") + 1).startsWith(this.val$title);
        }
    }

    /* renamed from: natalya.io.FileUtils.2 */
    static class AnonymousClass2 implements FilenameFilter {
        final /* synthetic */ String val$finalExt;
        final /* synthetic */ String val$finalName;

        AnonymousClass2(String str, String str2) {
            this.val$finalName = str;
            this.val$finalExt = str2;
        }

        public boolean accept(File file, String s) {
            return s.substring(s.lastIndexOf("/") + 1).startsWith(this.val$finalName.replace("." + this.val$finalExt, Table.STRING_DEFAULT_VALUE));
        }
    }

    public static boolean isFileExists(String dir, String fileName) {
        return new File(dir + File.separator + fileName).exists();
    }

    public static String getStringFromFile(String dir, String fileName) {
        return getStringFromFile(dir, fileName, HttpRequest.CHARSET_UTF8);
    }

    public static String getStringFromFile(String dir, String fileName, String enc) {
        Exception e;
        Throwable th;
        String result = null;
        InputStream in = null;
        BufferedReader bufferedReader = null;
        try {
            in = openInputStream(dir, fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(in, enc));
            try {
                StringBuilder str = new StringBuilder();
                while (true) {
                    String temp = br.readLine();
                    if (temp == null) {
                        break;
                    }
                    str.append(temp);
                }
                result = str.toString();
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e22) {
                        e22.printStackTrace();
                    }
                }
            } catch (Exception e3) {
                e = e3;
                bufferedReader = br;
                try {
                    e.printStackTrace();
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e222) {
                            e222.printStackTrace();
                        }
                    }
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException e2222) {
                            e2222.printStackTrace();
                        }
                    }
                    return result;
                } catch (Throwable th2) {
                    th = th2;
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e22222) {
                            e22222.printStackTrace();
                        }
                    }
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException e222222) {
                            e222222.printStackTrace();
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                bufferedReader = br;
                if (in != null) {
                    in.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                throw th;
            }
        } catch (Exception e4) {
            e = e4;
            e.printStackTrace();
            if (in != null) {
                in.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            return result;
        }
        return result;
    }

    public static boolean writeStringToFile(String str, String dir, String fileName) {
        return writeStringToFile(str, HttpRequest.CHARSET_UTF8, dir, fileName);
    }

    public static boolean writeStringToFile(String str, String enc, String dir, String fileName) {
        if (str == null || dir == null || fileName == null || !mkdirs(new File(dir))) {
            return false;
        }
        File target = new File(dir + File.separator + fileName);
        try {
            if (!target.exists()) {
                target.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(target);
            out.write(str.getBytes(enc));
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                target.delete();
                return false;
            } catch (Exception e2) {
                return false;
            }
        }
    }

    public static boolean writeStreamToFile(InputStream in, String dir, String fileName) {
        if (in == null || dir == null || fileName == null || !mkdirs(new File(dir))) {
            return false;
        }
        File target = new File(dir + File.separator + fileName);
        try {
            if (!target.exists()) {
                target.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(target);
            int b;
            do {
                b = in.read();
                if (b != -1) {
                    out.write(b);
                    continue;
                }
            } while (b != -1);
            in.close();
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                target.delete();
                return false;
            } catch (Exception e2) {
                return false;
            }
        }
    }

    public static boolean mkdirs(File dir) {
        if (dir == null || (dir.exists() && dir.isDirectory())) {
            return true;
        }
        return dir.mkdirs();
    }

    public static FileInputStream openInputStream(String dir, String fileName) {
        return openInputStream(dir + File.separator + fileName);
    }

    public static FileInputStream openInputStream(String filePath) {
        File f = new File(filePath);
        if (f.exists()) {
            try {
                return new FileInputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.trim().equals(Table.STRING_DEFAULT_VALUE)) {
            return null;
        }
        int pos = fileName.lastIndexOf(".");
        if (pos <= -1 || pos >= fileName.length()) {
            return Table.STRING_DEFAULT_VALUE;
        }
        return fileName.substring(pos + 1);
    }

    public static String getFileName(String url) {
        if (url == null || url.trim().equals(Table.STRING_DEFAULT_VALUE)) {
            return null;
        }
        int pos = url.lastIndexOf("/");
        if (pos <= -1 || pos >= url.length()) {
            return Table.STRING_DEFAULT_VALUE;
        }
        return url.substring(pos + 1);
    }

    public static String saveFileToMedia(Context context, File source, File dir, String title, String description, OnScanCompletedListener listener) {
        FileNotFoundException e;
        IOException e2;
        Throwable th;
        if (!dir.exists()) {
            dir.mkdirs();
        }
        InputStream in = null;
        OutputStream out = null;
        File saveFile = new File(dir, title + ".jpg");
        try {
            OutputStream out2;
            if (saveFile.exists()) {
                File file = dir;
                saveFile = new File(file, title + "-" + dir.listFiles(new AnonymousClass1(title)).length + ".jpg");
            }
            InputStream in2 = new FileInputStream(source);
            try {
                out2 = new FileOutputStream(saveFile);
            } catch (FileNotFoundException e3) {
                e = e3;
                in = in2;
                try {
                    e.printStackTrace();
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e22) {
                            e22.printStackTrace();
                        }
                    }
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e222) {
                            e222.printStackTrace();
                        }
                    }
                    return null;
                } catch (Throwable th2) {
                    th = th2;
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e2222) {
                            e2222.printStackTrace();
                        }
                    }
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e22222) {
                            e22222.printStackTrace();
                        }
                    }
                    throw th;
                }
            } catch (IOException e4) {
                e22222 = e4;
                in = in2;
                e22222.printStackTrace();
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e222222) {
                        e222222.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e2222222) {
                        e2222222.printStackTrace();
                    }
                }
                return null;
            } catch (Throwable th3) {
                th = th3;
                in = in2;
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                throw th;
            }
            try {
                byte[] buf = new byte[AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT];
                while (true) {
                    int len = in2.read(buf);
                    if (len <= 0) {
                        break;
                    }
                    out2.write(buf, 0, len);
                }
                in2.close();
                out2.close();
                MediaScannerConnection.scanFile(context, new String[]{saveFile.getAbsolutePath()}, new String[]{"image/jpg"}, listener);
                String absolutePath = saveFile.getAbsolutePath();
                if (in2 != null) {
                    try {
                        in2.close();
                    } catch (IOException e22222222) {
                        e22222222.printStackTrace();
                    }
                }
                if (out2 != null) {
                    try {
                        out2.close();
                    } catch (IOException e222222222) {
                        e222222222.printStackTrace();
                    }
                }
                out = out2;
                in = in2;
                return absolutePath;
            } catch (FileNotFoundException e5) {
                e = e5;
                out = out2;
                in = in2;
                e.printStackTrace();
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                return null;
            } catch (IOException e6) {
                e222222222 = e6;
                out = out2;
                in = in2;
                e222222222.printStackTrace();
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                return null;
            } catch (Throwable th4) {
                th = th4;
                out = out2;
                in = in2;
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                throw th;
            }
        } catch (FileNotFoundException e7) {
            e = e7;
            e.printStackTrace();
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            return null;
        } catch (IOException e8) {
            e222222222 = e8;
            e222222222.printStackTrace();
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            return null;
        }
    }

    public static String saveFileToMedia(Context context, File source, String dirName, String title, String description, OnScanCompletedListener listener) {
        return saveFileToMedia(context, source, new File(Environment.getExternalStorageDirectory() + File.separator + "Pictures" + File.separator + dirName), title, description, listener);
    }

    public static String saveImageToMedia(Context context, Uri uri, String dirName, String name, OnScanCompletedListener listener) throws IOException {
        File dir = new File(Environment.getExternalStorageDirectory() + File.separator + "Pictures" + File.separator + dirName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        InputStream inputStream = Crawler.crawlUrl(uri.toString());
        if (name == null || name.equals(Table.STRING_DEFAULT_VALUE)) {
            name = getFileName(uri.toString());
        }
        String ext = getFileExtension(name);
        if (ext.equals(Table.STRING_DEFAULT_VALUE)) {
            ext = "jpg";
            name = name + "." + ext;
        }
        File saveFile = new File(dir.getAbsolutePath(), name);
        if (saveFile.exists()) {
            name = name.replace("." + ext, Table.STRING_DEFAULT_VALUE) + "-" + dir.listFiles(new AnonymousClass2(name, ext)).length + "." + ext;
            saveFile = new File(dir, name);
        }
        if (!writeStreamToFile(inputStream, dir.getAbsolutePath(), name)) {
            return null;
        }
        String type = "image/";
        if (ext.equalsIgnoreCase("png")) {
            type = type + "png";
        } else {
            type = type + "jpg";
        }
        MediaScannerConnection.scanFile(context, new String[]{saveFile.getAbsolutePath()}, new String[]{type}, listener);
        return saveFile.getAbsolutePath();
    }
}
