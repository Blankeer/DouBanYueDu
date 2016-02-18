package com.douban.book.reader.entity.store;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

public class StoreWidgetGsonTypeFactory implements TypeAdapterFactory {
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (type.getRawType() != BaseStoreWidgetEntity.class) {
            return null;
        }
        return new StoreWidgetGsonTypeAdapter(gson.getDelegateAdapter(this, type));
    }
}
