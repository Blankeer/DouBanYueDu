package com.douban.book.reader.database;

import com.douban.book.reader.util.IOUtils;
import com.douban.book.reader.util.JsonUtils;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.SerializableType;
import com.j256.ormlite.misc.SqlExceptionUtil;
import io.fabric.sdk.android.services.network.HttpRequest;
import java.io.ByteArrayInputStream;
import java.sql.SQLException;
import java.util.Arrays;
import org.json.JSONObject;

public class JsonType extends SerializableType {
    private static final JsonType singleTon;

    static {
        singleTon = new JsonType();
    }

    public static JsonType getSingleton() {
        return singleTon;
    }

    private JsonType() {
        super(SqlType.SERIALIZABLE, new Class[0]);
    }

    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
        byte[] bytes = (byte[]) sqlArg;
        try {
            return JsonUtils.fromJsonObj(IOUtils.streamToJSONObject(new ByteArrayInputStream(bytes)), fieldType.getType());
        } catch (Exception e) {
            throw SqlExceptionUtil.create("Could not read Gson object from byte array: " + Arrays.toString(bytes) + "(len " + bytes.length + ")", e);
        }
    }

    public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
        JSONObject jsonObject = null;
        try {
            jsonObject = JsonUtils.toJsonObj(javaObject);
            return jsonObject.toString().getBytes(HttpRequest.CHARSET_UTF8);
        } catch (Exception e) {
            throw SqlExceptionUtil.create("Could not write Gson object to byte array: " + jsonObject, e);
        }
    }
}
