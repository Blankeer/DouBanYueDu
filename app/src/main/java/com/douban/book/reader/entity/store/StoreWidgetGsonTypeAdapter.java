package com.douban.book.reader.entity.store;

import com.douban.book.reader.entity.store.BaseStoreWidgetEntity.Type;
import com.douban.book.reader.util.JsonUtils;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.tencent.open.SocialConstants;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public class StoreWidgetGsonTypeAdapter extends TypeAdapter<BaseStoreWidgetEntity> {
    private static final Map<String, Class<? extends BaseStoreWidgetEntity>> WIDGET_TYPE;
    protected TypeAdapter<BaseStoreWidgetEntity> defaultAdapter;

    static {
        WIDGET_TYPE = Collections.unmodifiableMap(new HashMap<String, Class<? extends BaseStoreWidgetEntity>>() {
            {
                put(Type.BANNER, BannerStoreWidgetEntity.class);
                put(Type.CHARTS, ChartsStoreWidgetEntity.class);
                put(Type.NEW_WORKS, NewWorksStoreWidgetEntity.class);
                put(Type.LINK, LinkStoreWidgetEntity.class);
                put(Type.LINKS, LinksStoreWidgetEntity.class);
                put(Type.TOPIC, TopicStoreWidgetEntity.class);
                put(Type.KIND_LIST, KindListStoreWidgetEntity.class);
                put(Type.MORE_WORKS, MoreWorksStoreWidgetEntity.class);
                put(Type.BUTTONS, ButtonsStoreWidgetEntity.class);
                put(Type.PROMOTION, PromotionStoreWidgetEntity.class);
                put(Type.KIND, TopicStoreWidgetEntity.class);
            }
        });
    }

    public StoreWidgetGsonTypeAdapter(TypeAdapter<BaseStoreWidgetEntity> defaultAdapter) {
        this.defaultAdapter = defaultAdapter;
    }

    public void write(JsonWriter out, BaseStoreWidgetEntity value) throws IOException {
        this.defaultAdapter.write(out, value);
    }

    public BaseStoreWidgetEntity read(JsonReader in) throws IOException {
        try {
            JSONObject jsonObject = JsonUtils.readJSONObject(in);
            Class<? extends BaseStoreWidgetEntity> widgetClass = (Class) WIDGET_TYPE.get(jsonObject.optString(SocialConstants.PARAM_TYPE));
            if (widgetClass != null) {
                return (BaseStoreWidgetEntity) JsonUtils.fromJsonObj(jsonObject, widgetClass);
            }
            throw new IllegalArgumentException(String.format("Unknown widget type %s", new Object[]{type}));
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
