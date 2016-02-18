package com.mcxiaoke.packer.helper;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class PackerNg {
    private static final String EMPTY_STRING = "";
    private static final String TAG;

    public static class Helper {
        static final byte[] MAGIC;
        static final int SHORT_LENGTH = 2;
        static final String UTF_8 = "UTF-8";
        static final int ZIP_COMMENT_MAX_LENGTH = 65535;

        static {
            MAGIC = new byte[]{(byte) 33, (byte) 90, (byte) 88, (byte) 75, (byte) 33};
        }

        private static String getSourceDir(Object context) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchFieldException, NoSuchMethodException {
            Class<?> contextClass = Class.forName("android.content.Context");
            Class<?> applicationInfoClass = Class.forName("android.content.pm.ApplicationInfo");
            return (String) applicationInfoClass.getField("sourceDir").get(contextClass.getMethod("getApplicationInfo", new Class[0]).invoke(context, new Object[0]));
        }

        private static boolean isMagicMatched(byte[] buffer) {
            if (buffer.length != MAGIC.length) {
                return false;
            }
            for (int i = 0; i < MAGIC.length; i++) {
                if (buffer[i] != MAGIC[i]) {
                    return false;
                }
            }
            return true;
        }

        private static void writeBytes(byte[] data, DataOutput out) throws IOException {
            out.write(data);
        }

        private static void writeShort(int i, DataOutput out) throws IOException {
            ByteBuffer bb = ByteBuffer.allocate(SHORT_LENGTH).order(ByteOrder.LITTLE_ENDIAN);
            bb.putShort((short) i);
            out.write(bb.array());
        }

        private static short readShort(DataInput input) throws IOException {
            byte[] buf = new byte[SHORT_LENGTH];
            input.readFully(buf);
            return ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getShort(0);
        }

        public static void writeZipComment(File file, String comment) throws IOException {
            if (hasZipCommentMagic(file)) {
                throw new IllegalStateException("zip comment already exists, ignore.");
            }
            byte[] data = comment.getBytes(UTF_8);
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.seek(file.length() - 2);
            writeShort((data.length + SHORT_LENGTH) + MAGIC.length, raf);
            writeBytes(data, raf);
            writeShort(data.length, raf);
            writeBytes(MAGIC, raf);
            raf.close();
        }

        public static boolean hasZipCommentMagic(File file) throws IOException {
            Throwable th;
            RandomAccessFile raf = null;
            try {
                RandomAccessFile raf2 = new RandomAccessFile(file, "r");
                try {
                    byte[] buffer = new byte[MAGIC.length];
                    raf2.seek(raf2.length() - ((long) MAGIC.length));
                    raf2.readFully(buffer);
                    boolean isMagicMatched = isMagicMatched(buffer);
                    if (raf2 != null) {
                        raf2.close();
                    }
                    return isMagicMatched;
                } catch (Throwable th2) {
                    th = th2;
                    raf = raf2;
                    if (raf != null) {
                        raf.close();
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                if (raf != null) {
                    raf.close();
                }
                throw th;
            }
        }

        public static String readZipComment(File file) throws IOException {
            Throwable th;
            RandomAccessFile raf = null;
            try {
                RandomAccessFile raf2 = new RandomAccessFile(file, "r");
                try {
                    byte[] buffer = new byte[MAGIC.length];
                    long index = raf2.length() - ((long) MAGIC.length);
                    raf2.seek(index);
                    raf2.readFully(buffer);
                    if (isMagicMatched(buffer)) {
                        index -= 2;
                        raf2.seek(index);
                        int length = readShort(raf2);
                        if (length > 0) {
                            raf2.seek(index - ((long) length));
                            byte[] bytesComment = new byte[length];
                            raf2.readFully(bytesComment);
                            String str = new String(bytesComment, UTF_8);
                            if (raf2 == null) {
                                return str;
                            }
                            raf2.close();
                            return str;
                        }
                    }
                    if (raf2 != null) {
                        raf2.close();
                    }
                    return null;
                } catch (Throwable th2) {
                    th = th2;
                    raf = raf2;
                    if (raf != null) {
                        raf.close();
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                if (raf != null) {
                    raf.close();
                }
                throw th;
            }
        }

        private static String readZipCommentMmp(File file) throws IOException {
            Throwable th;
            long fz = file.length();
            RandomAccessFile raf = null;
            MappedByteBuffer map = null;
            try {
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
                try {
                    map = randomAccessFile.getChannel().map(MapMode.READ_ONLY, fz - 10240, 10240);
                    map.order(ByteOrder.LITTLE_ENDIAN);
                    byte[] buffer = new byte[MAGIC.length];
                    int index = 10240 - MAGIC.length;
                    map.position(index);
                    map.get(buffer);
                    if (isMagicMatched(buffer)) {
                        index -= 2;
                        map.position(index);
                        int length = map.getShort();
                        if (length > 0) {
                            map.position(index - length);
                            byte[] bytesComment = new byte[length];
                            map.get(bytesComment);
                            String str = new String(bytesComment, UTF_8);
                            if (map != null) {
                                map.clear();
                            }
                            if (randomAccessFile == null) {
                                return str;
                            }
                            randomAccessFile.close();
                            return str;
                        }
                    }
                    if (map != null) {
                        map.clear();
                    }
                    if (randomAccessFile != null) {
                        randomAccessFile.close();
                    }
                    return null;
                } catch (Throwable th2) {
                    th = th2;
                    raf = randomAccessFile;
                    if (map != null) {
                        map.clear();
                    }
                    if (raf != null) {
                        raf.close();
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                if (map != null) {
                    map.clear();
                }
                if (raf != null) {
                    raf.close();
                }
                throw th;
            }
        }

        public static void writeMarket(File file, String market) throws IOException {
            writeZipComment(file, market);
        }

        public static String readMarket(File file) throws IOException {
            return readZipComment(file);
        }

        public static boolean verifyMarket(File file, String market) throws IOException {
            return market.equals(readMarket(file));
        }

        public static void println(String msg) {
            System.out.println(PackerNg.TAG + ": " + msg);
        }

        public static List<String> parseMarkets(File file) throws IOException {
            List<String> markets = new ArrayList();
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            int lineNo = 1;
            while (true) {
                String line = br.readLine();
                if (line != null) {
                    String[] parts = line.split("#");
                    if (parts.length > 0) {
                        String market = parts[0].trim();
                        if (market.length() > 0) {
                            markets.add(market);
                        } else {
                            println("skip invalid market line " + lineNo + ":'" + line + "'");
                        }
                    } else {
                        println("skip invalid market line" + lineNo + ":'" + line + "'");
                    }
                    lineNo++;
                } else {
                    br.close();
                    fr.close();
                    return markets;
                }
            }
        }

        public static void copyFile(File src, File dest) throws IOException {
            if (!dest.exists()) {
                dest.createNewFile();
            }
            FileChannel source = null;
            FileChannel destination = null;
            try {
                source = new FileInputStream(src).getChannel();
                destination = new FileOutputStream(dest).getChannel();
                destination.transferFrom(source, 0, source.size());
                if (source != null) {
                    source.close();
                }
                if (destination != null) {
                    destination.close();
                }
            } catch (Throwable th) {
                if (source != null) {
                    source.close();
                }
                if (destination != null) {
                    destination.close();
                }
            }
        }

        public static boolean deleteDir(File dir) {
            File[] files = dir.listFiles();
            if (files == null || files.length == 0) {
                return false;
            }
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDir(file);
                } else {
                    file.delete();
                }
            }
            return true;
        }

        public static String getExtension(String fileName) {
            int dot = fileName.lastIndexOf(".");
            if (dot > 0) {
                return fileName.substring(dot + 1);
            }
            return null;
        }

        public static String getBaseName(String fileName) {
            int dot = fileName.lastIndexOf(".");
            if (dot > 0) {
                return fileName.substring(0, dot);
            }
            return fileName;
        }
    }

    static {
        TAG = PackerNg.class.getSimpleName();
    }

    public static String getMarket(Object context) {
        return getMarket(context, EMPTY_STRING);
    }

    public static String getMarket(Object context, String defaultValue) {
        String market;
        try {
            market = Helper.readMarket(new File(Helper.getSourceDir(context)));
        } catch (Exception e) {
            market = null;
        }
        if (market == null) {
            return defaultValue;
        }
        return market;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            Helper.println("Usage: java -jar packer-ng-x.x.x.jar your_apk_file market_file");
            System.exit(1);
        }
        Helper.println("command args: " + Arrays.toString(args));
        File apkFile = new File(args[0]);
        File marketFile = new File(args[1]);
        if (!apkFile.exists()) {
            Helper.println("apk file:" + apkFile + " not exists or not readable");
            System.exit(1);
        } else if (marketFile.exists()) {
            Helper.println("apk file: " + apkFile);
            Helper.println("market file: " + marketFile);
            List<String> markets = Helper.parseMarkets(marketFile);
            if (markets == null || markets.isEmpty()) {
                Helper.println("not markets found.");
                System.exit(1);
                return;
            }
            Helper.println("markets: " + markets);
            String baseName = Helper.getBaseName(apkFile.getName());
            String extName = Helper.getExtension(apkFile.getName());
            File outputDir = new File("apks");
            if (outputDir.exists()) {
                Helper.deleteDir(outputDir);
            } else {
                outputDir.mkdirs();
            }
            int processed = 0;
            for (String market : markets) {
                String apkName = baseName + "-" + market + "." + extName;
                File destFile = new File(outputDir, apkName);
                Helper.copyFile(apkFile, destFile);
                Helper.writeMarket(destFile, market);
                if (Helper.verifyMarket(destFile, market)) {
                    processed++;
                    Helper.println("processed apk " + apkName);
                } else {
                    destFile.delete();
                    Helper.println("failed to process " + apkName);
                }
            }
            Helper.println("all " + processed + " processed apks saved to " + outputDir);
        } else {
            Helper.println("markets file:" + marketFile + " not exists or not readable");
            System.exit(1);
        }
    }
}
