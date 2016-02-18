package com.google.tagmanager;

import com.google.android.gms.common.util.VisibleForTesting;
import com.igexin.download.Downloads;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import io.fabric.sdk.android.services.network.HttpRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class HttpUrlConnectionNetworkClient implements NetworkClient {
    private HttpURLConnection mConnection;

    HttpUrlConnectionNetworkClient() {
    }

    public InputStream getInputStream(String urlStr) throws IOException {
        this.mConnection = getUrlConnection(urlStr);
        return handleServerResponse(this.mConnection);
    }

    public void sendPostRequest(String urlStr, byte[] content) throws IOException {
        OutputStream sendToServer;
        HttpURLConnection connection = getUrlConnection(urlStr);
        try {
            connection.setRequestProperty(HttpRequest.HEADER_CONTENT_LENGTH, Integer.toString(content.length));
            connection.setRequestMethod(HttpRequest.METHOD_POST);
            connection.setDoOutput(true);
            connection.connect();
            sendToServer = connection.getOutputStream();
            sendToServer.write(content);
            sendToServer.flush();
            sendToServer.close();
            handleServerResponse(connection);
            closeURLConnection(connection);
        } catch (Throwable th) {
            closeURLConnection(connection);
        }
    }

    private InputStream handleServerResponse(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();
        if (responseCode == Downloads.STATUS_SUCCESS) {
            return connection.getInputStream();
        }
        String errorMessage = "Bad response: " + responseCode;
        if (responseCode == 404) {
            throw new FileNotFoundException(errorMessage);
        }
        throw new IOException(errorMessage);
    }

    public void close() {
        closeURLConnection(this.mConnection);
    }

    private void closeURLConnection(HttpURLConnection connection) {
        if (connection != null) {
            connection.disconnect();
        }
    }

    @VisibleForTesting
    HttpURLConnection getUrlConnection(String urlStr) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(urlStr).openConnection();
        connection.setReadTimeout(BaseImageDownloader.DEFAULT_HTTP_READ_TIMEOUT);
        connection.setConnectTimeout(BaseImageDownloader.DEFAULT_HTTP_READ_TIMEOUT);
        return connection;
    }
}
