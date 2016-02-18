package com.douban.book.reader.network.param;

import com.douban.book.reader.network.param.RequestParam.Type;
import com.douban.book.reader.util.JsonIterator;
import com.douban.book.reader.util.JsonUtils;
import com.douban.book.reader.util.KeyValuePair;
import com.douban.book.reader.util.Logger;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonRequestParam extends RequestParam<JsonRequestParam> {
    private static final String TAG;
    private JSONObject mData;

    static {
        TAG = JsonRequestParam.class.getSimpleName();
    }

    public JsonRequestParam() {
        this.mData = new JSONObject();
    }

    public JsonRequestParam(Object object) throws JSONException {
        this.mData = JsonUtils.toJsonObj(object);
    }

    public Type getType() {
        return Type.JSON;
    }

    public boolean isEmpty() {
        return this.mData.length() <= 0;
    }

    protected void doAppend(String key, Object value) {
        try {
            if (value instanceof JsonRequestParam) {
                this.mData.putOpt(key, ((JsonRequestParam) value).getJsonObject());
            } else {
                this.mData.putOpt(key, value);
            }
        } catch (JSONException e) {
            Logger.e(TAG, e);
        }
    }

    public Iterator<KeyValuePair> iterator() {
        return new JsonIterator(this.mData);
    }

    public String toString() {
        return this.mData.toString();
    }

    public JSONObject getJsonObject() {
        return this.mData;
    }
}
