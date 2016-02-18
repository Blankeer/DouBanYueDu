package com.douban.book.reader.data;

import com.douban.book.reader.util.JsonUtils;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import org.mapdb.Serializer;

public class JsonSerializer<T> implements Serializer<T>, Serializable {
    private Class<T> mType;

    public JsonSerializer(Class<T> cls) {
        this.mType = cls;
    }

    public void serialize(DataOutput out, T value) throws IOException {
        out.writeUTF(JsonUtils.toJson(value));
    }

    public T deserialize(DataInput in, int available) throws IOException {
        return JsonUtils.fromJson(in.readUTF(), this.mType);
    }

    public int fixedSize() {
        return -1;
    }
}
