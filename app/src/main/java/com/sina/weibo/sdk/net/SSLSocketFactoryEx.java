package com.sina.weibo.sdk.net;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import org.apache.http.conn.ssl.SSLSocketFactory;

public class SSLSocketFactoryEx extends SSLSocketFactory {
    private static final String TAG;
    SSLContext sslContext;

    public static class KeyStoresTrustManagerEX implements X509TrustManager {
        protected ArrayList<X509TrustManager> x509TrustManagers;

        protected KeyStoresTrustManagerEX(KeyStore... additionalkeyStores) {
            this.x509TrustManagers = new ArrayList();
            ArrayList<TrustManagerFactory> factories = new ArrayList();
            try {
                TrustManagerFactory original = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                original.init(null);
                factories.add(original);
                for (KeyStore keyStore : additionalkeyStores) {
                    TrustManagerFactory additionalCerts = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    additionalCerts.init(keyStore);
                    factories.add(additionalCerts);
                }
                Iterator it = factories.iterator();
                while (it.hasNext()) {
                    for (TrustManager tm : ((TrustManagerFactory) it.next()).getTrustManagers()) {
                        if (tm instanceof X509TrustManager) {
                            this.x509TrustManagers.add((X509TrustManager) tm);
                        }
                    }
                }
                if (this.x509TrustManagers.size() == 0) {
                    throw new RuntimeException("Couldn't find any X509TrustManagers");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            ((X509TrustManager) this.x509TrustManagers.get(0)).checkClientTrusted(chain, authType);
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            Iterator it = this.x509TrustManagers.iterator();
            while (it.hasNext()) {
                try {
                    ((X509TrustManager) it.next()).checkServerTrusted(chain, authType);
                    return;
                } catch (CertificateException e) {
                }
            }
            throw new CertificateException();
        }

        public X509Certificate[] getAcceptedIssuers() {
            ArrayList<X509Certificate> list = new ArrayList();
            Iterator it = this.x509TrustManagers.iterator();
            while (it.hasNext()) {
                list.addAll(Arrays.asList(((X509TrustManager) it.next()).getAcceptedIssuers()));
            }
            return (X509Certificate[]) list.toArray(new X509Certificate[list.size()]);
        }
    }

    static {
        TAG = SSLSocketFactoryEx.class.getName();
    }

    public SSLSocketFactoryEx(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        super(truststore);
        this.sslContext = SSLContext.getInstance("TLS");
        SSLContext sSLContext = this.sslContext;
        TrustManager[] trustManagerArr = new TrustManager[1];
        trustManagerArr[0] = new KeyStoresTrustManagerEX(truststore);
        sSLContext.init(null, trustManagerArr, null);
    }

    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
        return this.sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    public Socket createSocket() throws IOException {
        return this.sslContext.getSocketFactory().createSocket();
    }
}
