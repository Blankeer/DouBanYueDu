package com.douban.book.reader.helper;

import android.net.Uri;
import com.douban.book.reader.constant.Char;
import com.douban.book.reader.fragment.StoreTabFragment;
import com.douban.book.reader.util.ReaderUri;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.UriUtils;
import u.aly.dx;

public class WorksListUri {
    public static final String KEY_DISPLAY = "display";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_TYPE = "type";

    public static class Builder {
        private android.net.Uri.Builder mUriBuilder;

        public Builder() {
            this.mUriBuilder = new android.net.Uri.Builder().scheme(ReaderUri.SCHEME).authority(AppUri.AUTHORITY).appendPath(AppUri.PATH_WORKS_LIST);
        }

        public Builder type(Type type) {
            this.mUriBuilder.appendQueryParameter(WorksListUri.KEY_TYPE, type.getName());
            return this;
        }

        public Builder id(int id) {
            this.mUriBuilder.appendQueryParameter(WorksListUri.KEY_ID, String.valueOf(id));
            return this;
        }

        public Builder name(String name) {
            this.mUriBuilder.appendQueryParameter(WorksListUri.KEY_NAME, name);
            return this;
        }

        public Builder display(Display... displayInfo) {
            this.mUriBuilder.appendQueryParameter(WorksListUri.KEY_DISPLAY, String.valueOf(StringUtils.join((char) Char.PIPE, (Object[]) displayInfo)));
            return this;
        }

        public Builder queryParam(String key, String value) {
            if (StringUtils.isNotEmpty(key, value)) {
                this.mUriBuilder.appendQueryParameter(key, value);
            }
            return this;
        }

        public Uri build() {
            return this.mUriBuilder.build();
        }
    }

    public enum Display {
        PRICE;

        public String toString() {
            return name().toLowerCase();
        }
    }

    public enum Type {
        KIND("kind"),
        TAG("tag"),
        RANK("rank"),
        TOPIC("topic"),
        RECOMMENDATION("rec"),
        NEW_WORKS("new_works"),
        PUB("pub"),
        AGENT("agent"),
        PROMOTION(NotificationCompatApi21.CATEGORY_PROMO),
        GENERAL("general");
        
        private final String name;

        private Type(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public boolean match(String type) {
            return StringUtils.equals((CharSequence) type, getName());
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Uri fromWebUri(Uri uri) {
        if (uri == null) {
            return null;
        }
        if (UriUtils.isAppUri(uri)) {
            return uri;
        }
        String type = UriUtils.getFirstPathSegment(uri);
        Builder builder = builder().id(UriUtils.getIntPathSegmentNextTo(uri, type));
        for (String name : uri.getQueryParameterNames()) {
            builder.queryParam(name, uri.getQueryParameter(name));
        }
        Object obj = -1;
        switch (type.hashCode()) {
            case -987494927:
                if (type.equals("provider")) {
                    obj = 2;
                    break;
                }
                break;
            case -799212381:
                if (type.equals(StoreTabFragment.STORE_TAB_PROMOTION)) {
                    obj = 3;
                    break;
                }
                break;
            case 3292052:
                if (type.equals("kind")) {
                    obj = 1;
                    break;
                }
                break;
            case 110546223:
                if (type.equals("topic")) {
                    obj = null;
                    break;
                }
                break;
        }
        switch (obj) {
            case dx.a /*0*/:
                return builder.type(Type.TOPIC).build();
            case dx.b /*1*/:
                return builder.type(Type.KIND).build();
            case dx.c /*2*/:
                return builder.type(Type.AGENT).build();
            case dx.d /*3*/:
                return builder.type(Type.PROMOTION).build();
            default:
                return uri;
        }
    }

    public static String getName(Uri worksListUri) {
        return worksListUri.getQueryParameter(KEY_NAME);
    }

    public static int getId(Uri worksListUri) {
        return StringUtils.toInt(worksListUri.getQueryParameter(KEY_ID));
    }

    public static Type getType(Uri worksListUri) {
        Type type = matchType(worksListUri.getQueryParameter(KEY_TYPE));
        return type != null ? type : Type.GENERAL;
    }

    private static Type matchType(String typeName) {
        for (Type type : Type.values()) {
            if (type.match(typeName)) {
                return type;
            }
        }
        return Type.GENERAL;
    }

    public static boolean shouldShowPrice(Uri uri) {
        if (StringUtils.isNotEmpty(uri.getQueryParameter(KEY_DISPLAY))) {
            for (String item : uri.getQueryParameter(KEY_DISPLAY).split("\\|")) {
                if (StringUtils.equalsIgnoreCase(item, String.valueOf(Display.PRICE))) {
                    return true;
                }
            }
        }
        return false;
    }
}
