package com.igexin.push.config;

import android.content.Context;
import com.igexin.a.a.c.a;
import com.igexin.push.core.g;
import com.j256.ormlite.stmt.query.SimpleComparison;
import io.fabric.sdk.android.services.network.HttpRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class m {
    private static String a;

    static {
        a = "FileConfig";
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void a() {
        /*
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = com.igexin.push.core.g.e;
        r0 = r0.append(r1);
        r1 = ".properties";
        r0 = r0.append(r1);
        r1 = r0.toString();
        r0 = com.igexin.push.core.g.g;
        r0 = r0.getResources();
        r2 = r0.getAssets();
        r0 = 0;
        r0 = r2.open(r1);	 Catch:{ Exception -> 0x008d, all -> 0x0096 }
        a(r0);	 Catch:{ Exception -> 0x008d, all -> 0x00c2 }
        if (r0 == 0) goto L_0x002c;
    L_0x0029:
        r0.close();	 Catch:{ Exception -> 0x00b5 }
    L_0x002c:
        r1 = new java.io.File;
        r2 = com.igexin.push.core.g.X;
        r1.<init>(r2);
        r1 = r1.exists();
        if (r1 == 0) goto L_0x008c;
    L_0x0039:
        r1 = new java.io.FileInputStream;	 Catch:{ Exception -> 0x00c0, all -> 0x00ab }
        r2 = com.igexin.push.core.g.X;	 Catch:{ Exception -> 0x00c0, all -> 0x00ab }
        r1.<init>(r2);	 Catch:{ Exception -> 0x00c0, all -> 0x00ab }
        r0 = new java.io.BufferedReader;	 Catch:{ Exception -> 0x0085, all -> 0x00be }
        r2 = new java.io.InputStreamReader;	 Catch:{ Exception -> 0x0085, all -> 0x00be }
        r3 = "UTF-8";
        r2.<init>(r1, r3);	 Catch:{ Exception -> 0x0085, all -> 0x00be }
        r0.<init>(r2);	 Catch:{ Exception -> 0x0085, all -> 0x00be }
    L_0x004c:
        r2 = r0.readLine();	 Catch:{ Exception -> 0x0085, all -> 0x00be }
        if (r2 == 0) goto L_0x00a0;
    L_0x0052:
        r3 = "#";
        r3 = r2.startsWith(r3);	 Catch:{ Exception -> 0x0085, all -> 0x00be }
        if (r3 != 0) goto L_0x004c;
    L_0x005a:
        r3 = "=";
        r2 = r2.split(r3);	 Catch:{ Exception -> 0x0085, all -> 0x00be }
        r3 = r2.length;	 Catch:{ Exception -> 0x0085, all -> 0x00be }
        r4 = 2;
        if (r3 < r4) goto L_0x004c;
    L_0x0064:
        r3 = 0;
        r3 = r2[r3];	 Catch:{ Exception -> 0x0085, all -> 0x00be }
        r3 = r3.trim();	 Catch:{ Exception -> 0x0085, all -> 0x00be }
        r4 = 1;
        r2 = r2[r4];	 Catch:{ Exception -> 0x0085, all -> 0x00be }
        r2 = r2.trim();	 Catch:{ Exception -> 0x0085, all -> 0x00be }
        r4 = "sdk.debug";
        r3 = r3.equals(r4);	 Catch:{ Exception -> 0x0085, all -> 0x00be }
        if (r3 == 0) goto L_0x004c;
    L_0x007a:
        r2 = java.lang.Boolean.valueOf(r2);	 Catch:{ Exception -> 0x0085, all -> 0x00be }
        r2 = r2.booleanValue();	 Catch:{ Exception -> 0x0085, all -> 0x00be }
        com.igexin.a.a.c.a.a = r2;	 Catch:{ Exception -> 0x0085, all -> 0x00be }
        goto L_0x004c;
    L_0x0085:
        r0 = move-exception;
        r0 = r1;
    L_0x0087:
        if (r0 == 0) goto L_0x008c;
    L_0x0089:
        r0.close();	 Catch:{ Exception -> 0x00ba }
    L_0x008c:
        return;
    L_0x008d:
        r1 = move-exception;
        if (r0 == 0) goto L_0x002c;
    L_0x0090:
        r0.close();	 Catch:{ Exception -> 0x0094 }
        goto L_0x002c;
    L_0x0094:
        r1 = move-exception;
        goto L_0x002c;
    L_0x0096:
        r1 = move-exception;
        r5 = r1;
        r1 = r0;
        r0 = r5;
    L_0x009a:
        if (r1 == 0) goto L_0x009f;
    L_0x009c:
        r1.close();	 Catch:{ Exception -> 0x00b8 }
    L_0x009f:
        throw r0;
    L_0x00a0:
        r0.close();	 Catch:{ Exception -> 0x0085, all -> 0x00be }
        if (r1 == 0) goto L_0x008c;
    L_0x00a5:
        r1.close();	 Catch:{ Exception -> 0x00a9 }
        goto L_0x008c;
    L_0x00a9:
        r0 = move-exception;
        goto L_0x008c;
    L_0x00ab:
        r1 = move-exception;
        r5 = r1;
        r1 = r0;
        r0 = r5;
    L_0x00af:
        if (r1 == 0) goto L_0x00b4;
    L_0x00b1:
        r1.close();	 Catch:{ Exception -> 0x00bc }
    L_0x00b4:
        throw r0;
    L_0x00b5:
        r1 = move-exception;
        goto L_0x002c;
    L_0x00b8:
        r1 = move-exception;
        goto L_0x009f;
    L_0x00ba:
        r0 = move-exception;
        goto L_0x008c;
    L_0x00bc:
        r1 = move-exception;
        goto L_0x00b4;
    L_0x00be:
        r0 = move-exception;
        goto L_0x00af;
    L_0x00c0:
        r1 = move-exception;
        goto L_0x0087;
    L_0x00c2:
        r1 = move-exception;
        r5 = r1;
        r1 = r0;
        r0 = r5;
        goto L_0x009a;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.igexin.push.config.m.a():void");
    }

    public static void a(Context context) {
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader;
        InputStreamReader inputStreamReader2;
        Throwable th;
        BufferedReader bufferedReader2 = null;
        try {
            inputStreamReader = new InputStreamReader(context.getResources().getAssets().open("green.cfg"));
            try {
                bufferedReader = new BufferedReader(inputStreamReader);
                while (true) {
                    try {
                        String readLine = bufferedReader.readLine();
                        if (readLine == null) {
                            break;
                        }
                        String[] split = readLine.split(SimpleComparison.EQUAL_TO_OPERATION);
                        if (split != null && split.length == 2) {
                            g.c().put(split[0].trim(), split[1].trim());
                        }
                    } catch (Exception e) {
                        inputStreamReader2 = inputStreamReader;
                    } catch (Throwable th2) {
                        Throwable th3 = th2;
                        bufferedReader2 = bufferedReader;
                        th = th3;
                    }
                }
                if (inputStreamReader != null) {
                    try {
                        inputStreamReader.close();
                    } catch (IOException e2) {
                        return;
                    }
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Exception e3) {
                bufferedReader = null;
                inputStreamReader2 = inputStreamReader;
                if (inputStreamReader2 != null) {
                    try {
                        inputStreamReader2.close();
                    } catch (IOException e4) {
                        return;
                    }
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Throwable th4) {
                th = th4;
                if (inputStreamReader != null) {
                    try {
                        inputStreamReader.close();
                    } catch (IOException e5) {
                        throw th;
                    }
                }
                if (bufferedReader2 != null) {
                    bufferedReader2.close();
                }
                throw th;
            }
        } catch (Exception e6) {
            bufferedReader = null;
            if (inputStreamReader2 != null) {
                inputStreamReader2.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        } catch (Throwable th5) {
            th = th5;
            inputStreamReader = null;
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            if (bufferedReader2 != null) {
                bufferedReader2.close();
            }
            throw th;
        }
    }

    public static void a(InputStream inputStream) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, HttpRequest.CHARSET_UTF8));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    bufferedReader.close();
                    return;
                } else if (!readLine.startsWith("#")) {
                    String[] split = readLine.split(SimpleComparison.EQUAL_TO_OPERATION);
                    if (split.length >= 2) {
                        String trim = split[0].trim();
                        readLine = split[1].trim();
                        if (trim.equals("sdk.cm_address")) {
                            SDKUrlConfig.XFR_ADDRESS_IPS = readLine.split(",");
                        } else if (trim.equals("sdk.config_address")) {
                            SDKUrlConfig.CONFIG_ADDRESS_IPS = readLine.split(",");
                        } else if (trim.equals("sdk.bi_address")) {
                            SDKUrlConfig.BI_ADDRESS_IPS = readLine.split(",");
                        } else if (trim.equals("sdk.state_address")) {
                            SDKUrlConfig.STATE_ADDRESS_IPS = readLine.split(",");
                        } else if (trim.equals("sdk.log_address")) {
                            SDKUrlConfig.LOG_ADDRESS_IPS = readLine.split(",");
                        } else if (trim.equals("sdk.amp_address")) {
                            SDKUrlConfig.AMP_ADDRESS_IPS = readLine.split(",");
                        } else if (trim.equals("sdk.lbs_address")) {
                            SDKUrlConfig.LBS_ADDRESS_IPS = readLine.split(",");
                        } else if (trim.equals("sdk.cm_address_backup")) {
                            SDKUrlConfig.XFR_ADDRESS_IPS_BAK = readLine.split(",");
                        } else if (trim.equals("sdk.inc_address")) {
                            SDKUrlConfig.INC_ADDRESS_IPS = readLine.split(",");
                        } else if (trim.equals("sdk.debug")) {
                            a.a = Boolean.valueOf(readLine).booleanValue();
                        } else if (trim.equals("sdk.domainbackup.enable")) {
                            l.f = Boolean.valueOf(readLine).booleanValue();
                        } else if (trim.equals("sdk.readlocalcell.enable")) {
                            l.g = Boolean.valueOf(readLine).booleanValue();
                        } else if (trim.equals("sdk.uploadapplist.enable")) {
                            l.h = Boolean.valueOf(readLine).booleanValue();
                        } else if (trim.equals("sdk.feature.sendmessage.enable")) {
                            l.i = Boolean.valueOf(readLine).booleanValue();
                        } else if (trim.equals("sdk.feature.settag.enable")) {
                            l.j = Boolean.valueOf(readLine).booleanValue();
                        } else if (trim.equals("sdk.feature.setsilenttime.enable")) {
                            l.k = Boolean.valueOf(readLine).booleanValue();
                        } else if (trim.equals("sdk.ca.enable")) {
                            l.n = Boolean.valueOf(readLine).booleanValue();
                        } else if (trim.equals("sdk.snl.enable")) {
                            l.o = Boolean.valueOf(readLine).booleanValue();
                        } else if (trim.equals("sdk.snl.maxactiveflow")) {
                            l.p = Long.valueOf(readLine).longValue();
                        } else if (trim.equals("sdk.feature.setheartbeatinterval.enable")) {
                            l.l = Boolean.valueOf(readLine).booleanValue();
                        } else if (trim.equals("sdk.feature.setsockettimeout.enable")) {
                            l.m = Boolean.valueOf(readLine).booleanValue();
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }
}
