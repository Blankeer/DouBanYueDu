package natalya.net;

import io.fabric.sdk.android.services.common.AbstractSpiCall;
import io.fabric.sdk.android.services.network.HttpRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Crawler {
    public static InputStream crawlUrl(String url, int connectTimeout, int readTimeout) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setConnectTimeout(connectTimeout);
        con.setReadTimeout(readTimeout);
        con.setRequestMethod(HttpRequest.METHOD_GET);
        con.setDoInput(true);
        con.connect();
        return con.getInputStream();
    }

    public static InputStream crawlUrl(String url) throws IOException {
        return crawlUrl(url, 15000, AbstractSpiCall.DEFAULT_TIMEOUT);
    }
}
