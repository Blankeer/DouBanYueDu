package com.douban.book.reader.network.param;

import com.douban.book.reader.network.param.RequestParam.Type;
import com.douban.book.reader.util.IOUtils;
import com.douban.book.reader.util.KeyValuePair;
import io.fabric.sdk.android.services.network.HttpRequest;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;

public class MultiPartRequestParam extends UrlEncodedRequestParam<MultiPartRequestParam> {
    public static final String BOUNDARY = "ArkMultiPartFormDataBoundary";
    private static final String CRLF = "\r\n";
    private static final String TWO_HYPHEN = "--";
    private byte[] mData;

    public static class Entry {
        private InputStream mDataStream;
        private String mFileName;

        public Entry(byte[] data) {
            this("default-data-entry-name", data);
        }

        public Entry(String fileName, byte[] data) {
            this.mFileName = fileName;
            this.mDataStream = new ByteArrayInputStream(data);
        }

        public String getName() {
            return this.mFileName;
        }

        public InputStream getDataStream() {
            return this.mDataStream;
        }

        public String toString() {
            return this.mFileName;
        }
    }

    public Type getType() {
        return Type.MULTI_PART;
    }

    public synchronized byte[] getBytes() throws IOException {
        if (this.mData == null) {
            this.mData = inflate();
        }
        return this.mData;
    }

    private byte[] inflate() throws IOException {
        Throwable th;
        ByteArrayOutputStream os = null;
        Writer writer = null;
        try {
            ByteArrayOutputStream os2 = new ByteArrayOutputStream();
            try {
                Writer writer2 = new BufferedWriter(new OutputStreamWriter(os2, HttpRequest.CHARSET_UTF8));
                try {
                    Iterator<KeyValuePair> iterator = iterator();
                    while (iterator.hasNext()) {
                        KeyValuePair pair = (KeyValuePair) iterator.next();
                        writer2.append(TWO_HYPHEN);
                        writer2.append(BOUNDARY);
                        writer2.append(CRLF);
                        Object value = pair.getValue();
                        if (value instanceof Entry) {
                            Object[] objArr = new Object[2];
                            objArr[0] = pair.getKey();
                            objArr[1] = ((Entry) value).getName();
                            writer2.append(String.format("Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"", objArr));
                            writer2.append(CRLF);
                            writer2.append("Content-Type: application/octet-stream");
                            writer2.append(CRLF);
                            writer2.append(CRLF);
                            writer2.flush();
                            IOUtils.copyStream(((Entry) value).getDataStream(), os2);
                        } else {
                            writer2.append(String.format("Content-Disposition: form-data; name=\"%s\"", new Object[]{pair.getKey()}));
                            writer2.append(CRLF);
                            writer2.append("Content-Type: text/plain");
                            writer2.append(CRLF);
                            writer2.append(CRLF);
                            writer2.append(String.valueOf(value));
                        }
                        writer2.append(CRLF);
                    }
                    writer2.append(TWO_HYPHEN);
                    writer2.append(BOUNDARY);
                    writer2.append(TWO_HYPHEN);
                    writer2.flush();
                    byte[] toByteArray = os2.toByteArray();
                    IOUtils.closeSilently(os2);
                    IOUtils.closeSilently(writer2);
                    return toByteArray;
                } catch (Throwable th2) {
                    th = th2;
                    writer = writer2;
                    os = os2;
                }
            } catch (Throwable th3) {
                th = th3;
                os = os2;
                IOUtils.closeSilently(os);
                IOUtils.closeSilently(writer);
                throw th;
            }
        } catch (Throwable th4) {
            th = th4;
            IOUtils.closeSilently(os);
            IOUtils.closeSilently(writer);
            throw th;
        }
    }
}
