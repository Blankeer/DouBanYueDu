package com.douban.book.reader.network;

import com.douban.book.reader.util.JsonUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Tag;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import java.io.Closeable;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Iterator;
import org.json.JSONObject;

public class EntityIterator<T> implements Iterator<T>, Closeable {
    private HttpURLConnection mHttpURLConnection;
    private JsonReader mReader;
    private Class<T> mType;

    public EntityIterator(HttpURLConnection httpURLConnection, JsonReader reader, Class<T> type) throws IOException {
        if (reader == null) {
            throw new IllegalArgumentException("reader can not be null");
        }
        this.mHttpURLConnection = httpURLConnection;
        this.mReader = reader;
        this.mReader.beginArray();
        this.mType = type;
    }

    public boolean hasNext() throws JsonParseException {
        try {
            return this.mReader != null && this.mReader.hasNext();
        } catch (Throwable e) {
            throw new JsonIOException(e);
        }
    }

    public T next() throws JsonParseException {
        try {
            if (this.mType == JSONObject.class) {
                return JsonUtils.readJSONObject(this.mReader);
            }
            return JsonUtils.fromJson(this.mReader, this.mType);
        } catch (Throwable e) {
            if (e instanceof JsonParseException) {
                throw ((JsonParseException) e);
            }
            throw new JsonParseException(e);
        }
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public void close() throws IOException {
        try {
            if (this.mHttpURLConnection != null) {
                this.mHttpURLConnection.disconnect();
            }
            if (this.mReader != null) {
                this.mReader.close();
            }
        } catch (IOException e) {
            throw new IOException("EntityIterator close exception", e);
        }
    }

    public void closeSilently() {
        try {
            close();
        } catch (IOException e) {
            Logger.e(Tag.NETWORK, e);
        }
    }
}
