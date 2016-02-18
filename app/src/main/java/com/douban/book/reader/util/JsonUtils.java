package com.douban.book.reader.util;

import android.os.Bundle;
import com.douban.book.reader.entity.store.StoreWidgetGsonTypeFactory;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {
    private static Gson sGson;

    static {
        sGson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ").setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).registerTypeAdapterFactory(new StoreWidgetGsonTypeFactory()).create();
    }

    public static String toJson(Object entity) {
        return sGson.toJson(entity);
    }

    public static JSONObject toJsonObj(Object entity) throws JSONException {
        return new JSONObject(toJson(entity));
    }

    public static Object fromJson(String jsonStr, Type type) {
        return sGson.fromJson(jsonStr, type);
    }

    public static <T> T fromJsonObj(JSONObject jsonObj, Class<T> type) {
        return fromJson(jsonObj.toString(), (Class) type);
    }

    public static <T> T fromJson(JsonReader jsonReader, Class<T> type) {
        return sGson.fromJson(jsonReader, (Type) type);
    }

    public static <T> T fromJson(Reader reader, Class<T> type) {
        return sGson.fromJson(reader, (Class) type);
    }

    public static <T> T fromJson(String jsonStr, Class<T> type) {
        return sGson.fromJson(jsonStr, (Class) type);
    }

    public static String readJSONBlock(JsonReader reader) throws IOException, JSONException {
        if (reader.peek() == JsonToken.BEGIN_ARRAY) {
            return readJSONArray(reader).toString();
        }
        if (reader.peek() == JsonToken.BEGIN_OBJECT) {
            return readJSONObject(reader).toString();
        }
        return null;
    }

    public static JSONArray readJSONArray(JsonReader reader) throws IOException, JSONException {
        JSONArray result = new JSONArray();
        reader.beginArray();
        while (reader.hasNext()) {
            result.put(nextValue(reader));
        }
        reader.endArray();
        return result;
    }

    public static JSONObject readJSONObject(JsonReader reader) throws IOException, JSONException {
        reader.beginObject();
        JSONObject result = new JSONObject();
        while (reader.hasNext()) {
            JSONArray value;
            String name = reader.nextName();
            if (reader.peek() == JsonToken.BEGIN_ARRAY) {
                value = new JSONArray();
                reader.beginArray();
                while (reader.hasNext()) {
                    value.put(nextValue(reader));
                }
                reader.endArray();
            } else {
                value = nextValue(reader);
            }
            result.put(name, value);
        }
        reader.endObject();
        return result;
    }

    private static Object nextValue(JsonReader reader) throws IOException, JSONException {
        JsonToken next = reader.peek();
        if (next == JsonToken.BEGIN_OBJECT) {
            return readJSONObject(reader);
        }
        if (next == JsonToken.BOOLEAN) {
            return Boolean.valueOf(reader.nextBoolean());
        }
        if (next == JsonToken.NUMBER) {
            return Long.valueOf(reader.nextLong());
        }
        if (next == JsonToken.STRING) {
            return reader.nextString();
        }
        if (next == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }
        reader.skipValue();
        return null;
    }

    public static <T> List<T> toArrayList(JSONArray jsonArray) {
        if (jsonArray == null) {
            return new ArrayList();
        }
        return (List) sGson.fromJson(jsonArray.toString(), new TypeToken<List<T>>() {
        }.getType());
    }

    public static JSONObject fromBundle(Bundle bundle) throws JSONException {
        JSONObject result = new JSONObject();
        if (!(bundle == null || bundle.isEmpty())) {
            for (String key : bundle.keySet()) {
                Object obj = bundle.get(key);
                if (obj instanceof Bundle) {
                    obj = fromBundle((Bundle) obj);
                }
                result.put(key, obj);
            }
        }
        return result;
    }

    public static JSONObject fromBundleSilently(Bundle bundle) {
        try {
            return fromBundle(bundle);
        } catch (JSONException e) {
            return new JSONObject();
        }
    }
}
