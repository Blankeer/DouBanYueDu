package io.realm.processor;

import io.fabric.sdk.android.services.network.HttpRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Set;

public class RealmAnalytics {
    private static final String ADDRESS_PREFIX = "https://api.mixpanel.com/track/?data=";
    private static final String ADDRESS_SUFFIX = "&ip=1";
    private static final int CONNECT_TIMEOUT = 4000;
    private static final String EVENT_NAME = "Run";
    private static final String JSON_TEMPLATE = "{\n   \"event\": \"%EVENT%\",\n   \"properties\": {\n      \"token\": \"%TOKEN%\",\n      \"distinct_id\": \"%USER_ID%\",\n      \"Anonymized MAC Address\": \"%USER_ID%\",\n      \"Anonymized Bundle ID\": \"%APP_ID%\",\n      \"Binding\": \"java\",\n      \"Realm Version\": \"%REALM_VERSION%\",\n      \"Host OS Type\": \"%OS_TYPE%\",\n      \"Host OS Version\": \"%OS_VERSION%\",\n      \"Target OS Type\": \"android\"\n   }\n}";
    private static final int READ_TIMEOUT = 2000;
    private static final String TOKEN = "ce0fac19508f6c8f20066d345d360fd0";
    private static RealmAnalytics instance;
    private Set<String> packages;

    private RealmAnalytics(Set<String> packages) {
        this.packages = packages;
    }

    public static RealmAnalytics getInstance(Set<String> packages) {
        if (instance == null) {
            instance = new RealmAnalytics(packages);
        }
        return instance;
    }

    private void send() {
        try {
            HttpURLConnection connection = (HttpURLConnection) getUrl().openConnection();
            connection.setRequestMethod(HttpRequest.METHOD_GET);
            connection.connect();
            connection.getResponseCode();
        } catch (IOException e) {
        } catch (NoSuchAlgorithmException e2) {
        }
    }

    public void execute() {
        Thread backgroundThread = new Thread(new Runnable() {
            public void run() {
                RealmAnalytics.this.send();
            }
        });
        backgroundThread.start();
        try {
            backgroundThread.join(6000);
        } catch (InterruptedException e) {
        }
    }

    public URL getUrl() throws MalformedURLException, SocketException, NoSuchAlgorithmException, UnsupportedEncodingException {
        return new URL(ADDRESS_PREFIX + Utils.base64Encode(generateJson()) + ADDRESS_SUFFIX);
    }

    public String generateJson() throws SocketException, NoSuchAlgorithmException {
        return JSON_TEMPLATE.replaceAll("%EVENT%", EVENT_NAME).replaceAll("%TOKEN%", TOKEN).replaceAll("%USER_ID%", getAnonymousUserId()).replaceAll("%APP_ID%", getAnonymousAppId()).replaceAll("%REALM_VERSION%", Version.VERSION).replaceAll("%OS_TYPE%", System.getProperty("os.name")).replaceAll("%OS_VERSION%", System.getProperty("os.version"));
    }

    public static String getAnonymousUserId() throws NoSuchAlgorithmException, SocketException {
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        if (networkInterfaces.hasMoreElements()) {
            return Utils.hexStringify(Utils.sha256Hash(((NetworkInterface) networkInterfaces.nextElement()).getHardwareAddress()));
        }
        throw new IllegalStateException("No network interfaces detected");
    }

    public String getAnonymousAppId() throws NoSuchAlgorithmException {
        StringBuilder stringBuilder = new StringBuilder();
        for (String modelPackage : this.packages) {
            stringBuilder.append(modelPackage).append(":");
        }
        return Utils.hexStringify(Utils.sha256Hash(stringBuilder.toString().getBytes()));
    }
}
