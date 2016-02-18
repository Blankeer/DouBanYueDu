package org.jsoup.safety;

import com.douban.book.reader.constant.Constants;
import com.douban.book.reader.constant.DeviceType;
import com.douban.book.reader.helper.AppUri;
import com.sina.weibo.sdk.component.ShareRequestParam;
import com.sina.weibo.sdk.component.WidgetRequestParam;
import com.sina.weibo.sdk.register.mobile.SelectCountryActivity;
import com.tencent.connect.share.QzoneShare;
import com.tencent.open.SocialConstants;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

public class Whitelist {
    private Map<TagName, Set<AttributeKey>> attributes;
    private Map<TagName, Map<AttributeKey, AttributeValue>> enforcedAttributes;
    private boolean preserveRelativeLinks;
    private Map<TagName, Map<AttributeKey, Set<Protocol>>> protocols;
    private Set<TagName> tagNames;

    static abstract class TypedValue {
        private String value;

        TypedValue(String value) {
            Validate.notNull(value);
            this.value = value;
        }

        public int hashCode() {
            return (this.value == null ? 0 : this.value.hashCode()) + 31;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            TypedValue other = (TypedValue) obj;
            if (this.value == null) {
                if (other.value != null) {
                    return false;
                }
                return true;
            } else if (this.value.equals(other.value)) {
                return true;
            } else {
                return false;
            }
        }

        public String toString() {
            return this.value;
        }
    }

    static class AttributeKey extends TypedValue {
        AttributeKey(String value) {
            super(value);
        }

        static AttributeKey valueOf(String value) {
            return new AttributeKey(value);
        }
    }

    static class AttributeValue extends TypedValue {
        AttributeValue(String value) {
            super(value);
        }

        static AttributeValue valueOf(String value) {
            return new AttributeValue(value);
        }
    }

    static class Protocol extends TypedValue {
        Protocol(String value) {
            super(value);
        }

        static Protocol valueOf(String value) {
            return new Protocol(value);
        }
    }

    static class TagName extends TypedValue {
        TagName(String value) {
            super(value);
        }

        static TagName valueOf(String value) {
            return new TagName(value);
        }
    }

    public static Whitelist none() {
        return new Whitelist();
    }

    public static Whitelist simpleText() {
        return new Whitelist().addTags("b", "em", "i", "strong", "u");
    }

    public static Whitelist basic() {
        String[] strArr = new String[]{"cite"};
        return new Whitelist().addTags(DeviceType.IPAD, "b", "blockquote", "br", "cite", SelectCountryActivity.EXTRA_COUNTRY_CODE, "dd", "dl", "dt", "em", "i", "li", "ol", AppUri.AUTHORITY, "pre", WidgetRequestParam.REQ_PARAM_COMMENT_TOPIC, "small", "span", "strike", "strong", "sub", "sup", "u", "ul").addAttributes(DeviceType.IPAD, "href").addAttributes("blockquote", strArr).addAttributes(WidgetRequestParam.REQ_PARAM_COMMENT_TOPIC, "cite").addProtocols(DeviceType.IPAD, "href", "ftp", "http", Constants.API_SCHEME, "mailto").addProtocols("blockquote", "cite", "http", Constants.API_SCHEME).addProtocols("cite", "cite", "http", Constants.API_SCHEME).addEnforcedAttribute(DeviceType.IPAD, "rel", "nofollow");
    }

    public static Whitelist basicWithImages() {
        return basic().addTags(ShareRequestParam.REQ_UPLOAD_PIC_PARAM_IMG).addAttributes(ShareRequestParam.REQ_UPLOAD_PIC_PARAM_IMG, "align", "alt", SettingsJsonConstants.ICON_HEIGHT_KEY, "src", QzoneShare.SHARE_TO_QQ_TITLE, SettingsJsonConstants.ICON_WIDTH_KEY).addProtocols(ShareRequestParam.REQ_UPLOAD_PIC_PARAM_IMG, "src", "http", Constants.API_SCHEME);
    }

    public static Whitelist relaxed() {
        String[] strArr = new String[]{"cite"};
        strArr = new String[]{"span", SettingsJsonConstants.ICON_WIDTH_KEY};
        strArr = new String[]{"span", SettingsJsonConstants.ICON_WIDTH_KEY};
        strArr = new String[]{"start", SocialConstants.PARAM_TYPE};
        Whitelist addAttributes = new Whitelist().addTags(DeviceType.IPAD, "b", "blockquote", "br", "caption", "cite", SelectCountryActivity.EXTRA_COUNTRY_CODE, "col", "colgroup", "dd", "div", "dl", "dt", "em", "h1", "h2", "h3", "h4", "h5", "h6", "i", ShareRequestParam.REQ_UPLOAD_PIC_PARAM_IMG, "li", "ol", AppUri.AUTHORITY, "pre", WidgetRequestParam.REQ_PARAM_COMMENT_TOPIC, "small", "span", "strike", "strong", "sub", "sup", "table", "tbody", "td", "tfoot", "th", "thead", "tr", "u", "ul").addAttributes(DeviceType.IPAD, "href", QzoneShare.SHARE_TO_QQ_TITLE).addAttributes("blockquote", strArr).addAttributes("col", strArr).addAttributes("colgroup", strArr).addAttributes(ShareRequestParam.REQ_UPLOAD_PIC_PARAM_IMG, "align", "alt", SettingsJsonConstants.ICON_HEIGHT_KEY, "src", QzoneShare.SHARE_TO_QQ_TITLE, SettingsJsonConstants.ICON_WIDTH_KEY).addAttributes("ol", strArr);
        return addAttributes.addAttributes(WidgetRequestParam.REQ_PARAM_COMMENT_TOPIC, "cite").addAttributes("table", QzoneShare.SHARE_TO_QQ_SUMMARY, SettingsJsonConstants.ICON_WIDTH_KEY).addAttributes("td", "abbr", "axis", "colspan", "rowspan", SettingsJsonConstants.ICON_WIDTH_KEY).addAttributes("th", "abbr", "axis", "colspan", "rowspan", com.tencent.connect.common.Constants.PARAM_SCOPE, SettingsJsonConstants.ICON_WIDTH_KEY).addAttributes("ul", SocialConstants.PARAM_TYPE).addProtocols(DeviceType.IPAD, "href", "ftp", "http", Constants.API_SCHEME, "mailto").addProtocols("blockquote", "cite", "http", Constants.API_SCHEME).addProtocols("cite", "cite", "http", Constants.API_SCHEME).addProtocols(ShareRequestParam.REQ_UPLOAD_PIC_PARAM_IMG, "src", "http", Constants.API_SCHEME).addProtocols(WidgetRequestParam.REQ_PARAM_COMMENT_TOPIC, "cite", "http", Constants.API_SCHEME);
    }

    public Whitelist() {
        this.tagNames = new HashSet();
        this.attributes = new HashMap();
        this.enforcedAttributes = new HashMap();
        this.protocols = new HashMap();
        this.preserveRelativeLinks = false;
    }

    public Whitelist addTags(String... tags) {
        Validate.notNull(tags);
        for (String tagName : tags) {
            Validate.notEmpty(tagName);
            this.tagNames.add(TagName.valueOf(tagName));
        }
        return this;
    }

    public Whitelist removeTags(String... tags) {
        Validate.notNull(tags);
        for (String tag : tags) {
            Validate.notEmpty(tag);
            TagName tagName = TagName.valueOf(tag);
            if (this.tagNames.remove(tagName)) {
                this.attributes.remove(tagName);
                this.enforcedAttributes.remove(tagName);
                this.protocols.remove(tagName);
            }
        }
        return this;
    }

    public Whitelist addAttributes(String tag, String... keys) {
        boolean z;
        int i = 0;
        Validate.notEmpty(tag);
        Validate.notNull(keys);
        if (keys.length > 0) {
            z = true;
        } else {
            z = false;
        }
        Validate.isTrue(z, "No attributes supplied.");
        TagName tagName = TagName.valueOf(tag);
        if (!this.tagNames.contains(tagName)) {
            this.tagNames.add(tagName);
        }
        Set<AttributeKey> attributeSet = new HashSet();
        int length = keys.length;
        while (i < length) {
            String key = keys[i];
            Validate.notEmpty(key);
            attributeSet.add(AttributeKey.valueOf(key));
            i++;
        }
        if (this.attributes.containsKey(tagName)) {
            ((Set) this.attributes.get(tagName)).addAll(attributeSet);
        } else {
            this.attributes.put(tagName, attributeSet);
        }
        return this;
    }

    public Whitelist removeAttributes(String tag, String... keys) {
        boolean z;
        Set<AttributeKey> currentSet;
        int i = 0;
        Validate.notEmpty(tag);
        Validate.notNull(keys);
        if (keys.length > 0) {
            z = true;
        } else {
            z = false;
        }
        Validate.isTrue(z, "No attributes supplied.");
        TagName tagName = TagName.valueOf(tag);
        Set<AttributeKey> attributeSet = new HashSet();
        int length = keys.length;
        while (i < length) {
            String key = keys[i];
            Validate.notEmpty(key);
            attributeSet.add(AttributeKey.valueOf(key));
            i++;
        }
        if (this.tagNames.contains(tagName) && this.attributes.containsKey(tagName)) {
            currentSet = (Set) this.attributes.get(tagName);
            currentSet.removeAll(attributeSet);
            if (currentSet.isEmpty()) {
                this.attributes.remove(tagName);
            }
        }
        if (tag.equals(":all")) {
            for (TagName name : this.attributes.keySet()) {
                currentSet = (Set) this.attributes.get(name);
                currentSet.removeAll(attributeSet);
                if (currentSet.isEmpty()) {
                    this.attributes.remove(name);
                }
            }
        }
        return this;
    }

    public Whitelist addEnforcedAttribute(String tag, String key, String value) {
        Validate.notEmpty(tag);
        Validate.notEmpty(key);
        Validate.notEmpty(value);
        TagName tagName = TagName.valueOf(tag);
        if (!this.tagNames.contains(tagName)) {
            this.tagNames.add(tagName);
        }
        AttributeKey attrKey = AttributeKey.valueOf(key);
        AttributeValue attrVal = AttributeValue.valueOf(value);
        if (this.enforcedAttributes.containsKey(tagName)) {
            ((Map) this.enforcedAttributes.get(tagName)).put(attrKey, attrVal);
        } else {
            Map<AttributeKey, AttributeValue> attrMap = new HashMap();
            attrMap.put(attrKey, attrVal);
            this.enforcedAttributes.put(tagName, attrMap);
        }
        return this;
    }

    public Whitelist removeEnforcedAttribute(String tag, String key) {
        Validate.notEmpty(tag);
        Validate.notEmpty(key);
        TagName tagName = TagName.valueOf(tag);
        if (this.tagNames.contains(tagName) && this.enforcedAttributes.containsKey(tagName)) {
            Map<AttributeKey, AttributeValue> attrMap = (Map) this.enforcedAttributes.get(tagName);
            attrMap.remove(AttributeKey.valueOf(key));
            if (attrMap.isEmpty()) {
                this.enforcedAttributes.remove(tagName);
            }
        }
        return this;
    }

    public Whitelist preserveRelativeLinks(boolean preserve) {
        this.preserveRelativeLinks = preserve;
        return this;
    }

    public Whitelist addProtocols(String tag, String key, String... protocols) {
        Map<AttributeKey, Set<Protocol>> attrMap;
        Set<Protocol> protSet;
        Validate.notEmpty(tag);
        Validate.notEmpty(key);
        Validate.notNull(protocols);
        TagName tagName = TagName.valueOf(tag);
        AttributeKey attrKey = AttributeKey.valueOf(key);
        if (this.protocols.containsKey(tagName)) {
            attrMap = (Map) this.protocols.get(tagName);
        } else {
            attrMap = new HashMap();
            this.protocols.put(tagName, attrMap);
        }
        if (attrMap.containsKey(attrKey)) {
            protSet = (Set) attrMap.get(attrKey);
        } else {
            protSet = new HashSet();
            attrMap.put(attrKey, protSet);
        }
        for (String protocol : protocols) {
            Validate.notEmpty(protocol);
            protSet.add(Protocol.valueOf(protocol));
        }
        return this;
    }

    public Whitelist removeProtocols(String tag, String key, String... protocols) {
        Validate.notEmpty(tag);
        Validate.notEmpty(key);
        Validate.notNull(protocols);
        TagName tagName = TagName.valueOf(tag);
        AttributeKey attrKey = AttributeKey.valueOf(key);
        if (this.protocols.containsKey(tagName)) {
            Map<AttributeKey, Set<Protocol>> attrMap = (Map) this.protocols.get(tagName);
            if (attrMap.containsKey(attrKey)) {
                Set<Protocol> protSet = (Set) attrMap.get(attrKey);
                for (String protocol : protocols) {
                    Validate.notEmpty(protocol);
                    protSet.remove(Protocol.valueOf(protocol));
                }
                if (protSet.isEmpty()) {
                    attrMap.remove(attrKey);
                    if (attrMap.isEmpty()) {
                        this.protocols.remove(tagName);
                    }
                }
            }
        }
        return this;
    }

    protected boolean isSafeTag(String tag) {
        return this.tagNames.contains(TagName.valueOf(tag));
    }

    protected boolean isSafeAttribute(String tagName, Element el, Attribute attr) {
        TagName tag = TagName.valueOf(tagName);
        AttributeKey key = AttributeKey.valueOf(attr.getKey());
        if (this.attributes.containsKey(tag) && ((Set) this.attributes.get(tag)).contains(key)) {
            if (!this.protocols.containsKey(tag)) {
                return true;
            }
            boolean z;
            Map<AttributeKey, Set<Protocol>> attrProts = (Map) this.protocols.get(tag);
            if (!attrProts.containsKey(key) || testValidProtocol(el, attr, (Set) attrProts.get(key))) {
                z = true;
            } else {
                z = false;
            }
            return z;
        } else if (tagName.equals(":all") || !isSafeAttribute(":all", el, attr)) {
            return false;
        } else {
            return true;
        }
    }

    private boolean testValidProtocol(Element el, Attribute attr, Set<Protocol> protocols) {
        String value = el.absUrl(attr.getKey());
        if (value.length() == 0) {
            value = attr.getValue();
        }
        if (!this.preserveRelativeLinks) {
            attr.setValue(value);
        }
        for (Protocol protocol : protocols) {
            String prot = protocol.toString();
            if (!prot.equals("#")) {
                if (value.toLowerCase().startsWith(prot + ":")) {
                    return true;
                }
            } else if (isValidAnchor(value)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidAnchor(String value) {
        return value.startsWith("#") && !value.matches(".*\\s.*");
    }

    Attributes getEnforcedAttributes(String tagName) {
        Attributes attrs = new Attributes();
        TagName tag = TagName.valueOf(tagName);
        if (this.enforcedAttributes.containsKey(tag)) {
            for (Entry<AttributeKey, AttributeValue> entry : ((Map) this.enforcedAttributes.get(tag)).entrySet()) {
                attrs.put(((AttributeKey) entry.getKey()).toString(), ((AttributeValue) entry.getValue()).toString());
            }
        }
        return attrs;
    }
}
