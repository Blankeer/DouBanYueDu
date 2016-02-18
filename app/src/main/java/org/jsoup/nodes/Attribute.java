package org.jsoup.nodes;

import com.douban.book.reader.fragment.GiftMessageEditFragment_;
import com.igexin.sdk.PushBuildConfig;
import io.realm.internal.Table;
import java.util.Arrays;
import java.util.Map.Entry;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.nodes.Document.OutputSettings.Syntax;

public class Attribute implements Entry<String, String>, Cloneable {
    private static final String[] booleanAttributes;
    private String key;
    private String value;

    static {
        booleanAttributes = new String[]{"allowfullscreen", "async", "autofocus", "checked", "compact", "declare", "default", "defer", "disabled", "formnovalidate", "hidden", "inert", "ismap", "itemscope", "multiple", "muted", "nohref", "noresize", "noshade", "novalidate", "nowrap", PushBuildConfig.sdk_conf_channelid, "readonly", "required", "reversed", "seamless", GiftMessageEditFragment_.SELECTED_ARG, "sortable", "truespeed", "typemustmatch"};
    }

    public Attribute(String key, String value) {
        Validate.notEmpty(key);
        Validate.notNull(value);
        this.key = key.trim().toLowerCase();
        this.value = value;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        Validate.notEmpty(key);
        this.key = key.trim().toLowerCase();
    }

    public String getValue() {
        return this.value;
    }

    public String setValue(String value) {
        Validate.notNull(value);
        String old = this.value;
        this.value = value;
        return old;
    }

    public String html() {
        StringBuilder accum = new StringBuilder();
        html(accum, new Document(Table.STRING_DEFAULT_VALUE).outputSettings());
        return accum.toString();
    }

    protected void html(StringBuilder accum, OutputSettings out) {
        accum.append(this.key);
        if (!shouldCollapseAttribute(out)) {
            accum.append("=\"");
            Entities.escape(accum, this.value, out, true, false, false);
            accum.append('\"');
        }
    }

    public String toString() {
        return html();
    }

    public static Attribute createFromEncoded(String unencodedKey, String encodedValue) {
        return new Attribute(unencodedKey, Entities.unescape(encodedValue, true));
    }

    protected boolean isDataAttribute() {
        return this.key.startsWith("data-") && this.key.length() > "data-".length();
    }

    protected final boolean shouldCollapseAttribute(OutputSettings out) {
        return (Table.STRING_DEFAULT_VALUE.equals(this.value) || this.value.equalsIgnoreCase(this.key)) && out.syntax() == Syntax.html && isBooleanAttribute();
    }

    protected boolean isBooleanAttribute() {
        return Arrays.binarySearch(booleanAttributes, this.key) >= 0;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Attribute)) {
            return false;
        }
        Attribute attribute = (Attribute) o;
        if (this.key == null ? attribute.key != null : !this.key.equals(attribute.key)) {
            return false;
        }
        if (this.value != null) {
            if (this.value.equals(attribute.value)) {
                return true;
            }
        } else if (attribute.value == null) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result;
        int i = 0;
        if (this.key != null) {
            result = this.key.hashCode();
        } else {
            result = 0;
        }
        int i2 = result * 31;
        if (this.value != null) {
            i = this.value.hashCode();
        }
        return i2 + i;
    }

    public Attribute clone() {
        try {
            return (Attribute) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
