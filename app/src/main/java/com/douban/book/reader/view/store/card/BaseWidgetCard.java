package com.douban.book.reader.view.store.card;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.view.View;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.store.BaseStoreWidgetEntity;
import com.douban.book.reader.entity.store.BaseStoreWidgetEntity.Type;
import com.douban.book.reader.span.IconFontSpan;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.ReflectionUtils;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.Tag;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.card.Card;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class BaseWidgetCard<T extends BaseStoreWidgetEntity> extends Card<BaseWidgetCard> {
    private static Map<String, Class<?>> sCardClassMap;
    private static Map<String, Method> sCardCreatorMap;
    private T mWidgetEntity;

    static {
        sCardClassMap = new HashMap();
        sCardCreatorMap = new HashMap();
        sCardClassMap.put(Type.BANNER, BannerWidgetCard_.class);
        sCardClassMap.put(Type.CHARTS, ChartsWidgetCard_.class);
        sCardClassMap.put(Type.NEW_WORKS, NewWorksWidgetCard_.class);
        sCardClassMap.put(Type.LINK, LinkWidgetCard_.class);
        sCardClassMap.put(Type.LINKS, LinksWidgetCard_.class);
        sCardClassMap.put(Type.TOPIC, TopicWidgetCard_.class);
        sCardClassMap.put(Type.KIND, TopicWidgetCard_.class);
        sCardClassMap.put(Type.KIND_LIST, KindListWidgetCard_.class);
        sCardClassMap.put(Type.MORE_WORKS, MoreWorksWidgetCard_.class);
        sCardClassMap.put(Type.BUTTONS, ButtonsWidgetCard_.class);
        sCardClassMap.put(Type.PROMOTION, PromotionWidgetCard_.class);
    }

    public BaseWidgetCard(Context context) {
        super(context);
    }

    public static <T extends BaseStoreWidgetEntity> BaseWidgetCard<T> createOrRebind(Context context, View convertView, T storeWidget) {
        BaseWidgetCard<T> card = null;
        if (ReflectionUtils.isInstanceOf((Object) convertView, getClassType(storeWidget.type))) {
            card = (BaseWidgetCard) convertView;
        } else {
            Method creator = getCreator(storeWidget.type);
            if (creator != null) {
                try {
                    card = (BaseWidgetCard) creator.invoke(null, new Object[]{context});
                } catch (Exception e) {
                    Logger.e(Tag.GENERAL, e);
                }
            }
        }
        if (card != null) {
            card.bindEntity(storeWidget);
        }
        return card;
    }

    public void bindEntity(T entity) {
        this.mWidgetEntity = entity;
        init();
        onEntityBound(entity);
    }

    protected void onEntityBound(T t) {
    }

    protected <ViewType> ViewType getOrCreateContentView(Class<ViewType> cls) {
        View contentView = getContentView();
        if (ReflectionUtils.isInstanceOf((Object) contentView, (Class) cls)) {
            return contentView;
        }
        contentView = ViewUtils.createView(cls, getContext());
        content(contentView);
        return contentView;
    }

    public BaseWidgetCard content(View contentView) {
        super.content(contentView);
        noContentPadding();
        return (BaseWidgetCard) self();
    }

    private void init() {
        if (this.mWidgetEntity != null) {
            int resId = -1;
            if (StringUtils.equalsIgnoreCase(this.mWidgetEntity.type, Type.KIND)) {
                resId = R.drawable.v_category;
            } else if (StringUtils.equalsIgnoreCase(this.mWidgetEntity.type, Type.TOPIC)) {
                resId = R.drawable.v_topic;
            }
            title(getTitleWithIcon(resId, this.mWidgetEntity.title));
            subTitle(this.mWidgetEntity.subtitle);
            moreBtnVisible(false);
        }
    }

    public static CharSequence getTitleWithIcon(@DrawableRes int resId, CharSequence title) {
        RichText result = new RichText();
        if (resId > 0) {
            result.appendIcon(new IconFontSpan(resId).ratio(1.2f).verticalOffsetRatio(-0.07f).paddingRight(0.1f));
        }
        if (StringUtils.isNotEmpty(title)) {
            result.append(title);
        }
        return result;
    }

    private static Class<?> getClassType(String widgetType) {
        return (Class) sCardClassMap.get(widgetType);
    }

    private static Method getCreator(String widgetType) {
        Method creator = (Method) sCardCreatorMap.get(widgetType);
        if (creator == null) {
            try {
                creator = getClassType(widgetType).getMethod("build", new Class[]{Context.class});
                sCardCreatorMap.put(widgetType, creator);
            } catch (Exception e) {
                return null;
            }
        }
        return creator;
    }
}
