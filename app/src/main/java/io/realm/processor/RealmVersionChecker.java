package io.realm.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic.Kind;

public class RealmVersionChecker {
    private static final int CONNECT_TIMEOUT = 4000;
    private static final int READ_TIMEOUT = 2000;
    public static final String REALM_ANDROID_DOWNLOAD_URL = "http://static.realm.io/downloads/java/latest";
    private static final String REALM_VERSION = "0.82.0";
    private static final String VERSION_URL = "http://static.realm.io/update/java?";
    private static RealmVersionChecker instance;
    private static boolean isFirstRound;
    private ProcessingEnvironment processingEnvironment;

    static {
        instance = null;
        isFirstRound = true;
    }

    private RealmVersionChecker(ProcessingEnvironment processingEnvironment) {
        this.processingEnvironment = processingEnvironment;
    }

    public static RealmVersionChecker getInstance(ProcessingEnvironment processingEnvironment) {
        if (instance == null) {
            instance = new RealmVersionChecker(processingEnvironment);
        }
        return instance;
    }

    private void launchRealmCheck() {
        String latestVersionStr = checkLatestVersion();
        if (!latestVersionStr.equals(REALM_VERSION)) {
            printMessage("Version " + latestVersionStr + " of Realm is now available: " + REALM_ANDROID_DOWNLOAD_URL);
        }
    }

    public void executeRealmVersionUpdate() {
        if (isFirstRound) {
            isFirstRound = false;
            Thread backgroundThread = new Thread(new Runnable() {
                public void run() {
                    RealmVersionChecker.this.launchRealmCheck();
                }
            });
            backgroundThread.start();
            try {
                backgroundThread.join(6000);
            } catch (InterruptedException e) {
            }
        }
    }

    private String checkLatestVersion() {
        String result = REALM_VERSION;
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL("http://static.realm.io/update/java?0.82.0").openConnection();
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            result = rd.readLine();
            rd.close();
            return result;
        } catch (IOException e) {
            return result;
        }
    }

    private void printMessage(String message) {
        this.processingEnvironment.getMessager().printMessage(Kind.OTHER, message);
    }
}
