package com.crashlytics.android.answers;

import com.douban.book.reader.entity.DbCacheEntity.Column;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONObject;

class AnswersAttributes {
    final Map<String, Object> attributes;
    final AnswersEventValidator validator;

    public AnswersAttributes(AnswersEventValidator validator) {
        this.attributes = new ConcurrentHashMap();
        this.validator = validator;
    }

    void put(String key, String value) {
        if (!this.validator.isNull(key, "key") && !this.validator.isNull(value, Column.VALUE)) {
            putAttribute(this.validator.limitStringLength(key), this.validator.limitStringLength(value));
        }
    }

    void put(String key, Number value) {
        if (!this.validator.isNull(key, "key") && !this.validator.isNull(value, Column.VALUE)) {
            putAttribute(this.validator.limitStringLength(key), value);
        }
    }

    void putAttribute(String key, Object value) {
        if (!this.validator.isFullMap(this.attributes, key)) {
            this.attributes.put(key, value);
        }
    }

    public String toString() {
        return new JSONObject(this.attributes).toString();
    }
}
