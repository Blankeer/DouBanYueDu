package org.jsoup.parser;

import com.alipay.sdk.authjs.a;
import com.alipay.sdk.cons.c;
import com.douban.amonsul.StatConstant;
import com.douban.book.reader.constant.DeviceType;
import com.douban.book.reader.helper.AppUri;
import com.sina.weibo.sdk.component.ShareRequestParam;
import com.sina.weibo.sdk.component.WidgetRequestParam;
import com.sina.weibo.sdk.register.mobile.SelectCountryActivity;
import com.tencent.connect.share.QzoneShare;
import java.util.HashMap;
import java.util.Map;
import org.jsoup.helper.Validate;

public class Tag {
    private static final String[] blockTags;
    private static final String[] emptyTags;
    private static final String[] formListedTags;
    private static final String[] formSubmitTags;
    private static final String[] formatAsInlineTags;
    private static final String[] inlineTags;
    private static final String[] preserveWhitespaceTags;
    private static final Map<String, Tag> tags;
    private boolean canContainBlock;
    private boolean canContainInline;
    private boolean empty;
    private boolean formList;
    private boolean formSubmit;
    private boolean formatAsBlock;
    private boolean isBlock;
    private boolean preserveWhitespace;
    private boolean selfClosing;
    private String tagName;

    static {
        int i = 0;
        tags = new HashMap();
        blockTags = new String[]{"html", "head", "body", "frameset", "script", "noscript", "style", "meta", "link", QzoneShare.SHARE_TO_QQ_TITLE, "frame", "noframes", "section", "nav", "aside", "hgroup", "header", "footer", AppUri.AUTHORITY, "h1", "h2", "h3", "h4", "h5", "h6", "ul", "ol", "pre", "div", "blockquote", "hr", "address", "figure", "figcaption", c.c, "fieldset", "ins", "del", "s", "dl", "dt", "dd", "li", "table", "caption", "thead", "tfoot", "tbody", "colgroup", "col", "tr", "th", "td", "video", "audio", "canvas", "details", "menu", "plaintext", "template", "article", "main", "svg", "math"};
        inlineTags = new String[]{"object", "base", "font", "tt", "i", "b", "u", "big", "small", "em", "strong", "dfn", SelectCountryActivity.EXTRA_COUNTRY_CODE, "samp", "kbd", "var", "cite", "abbr", "time", "acronym", "mark", "ruby", StatConstant.JSON_KEY_RESOLUTION, "rp", DeviceType.IPAD, ShareRequestParam.REQ_UPLOAD_PIC_PARAM_IMG, "br", "wbr", "map", WidgetRequestParam.REQ_PARAM_COMMENT_TOPIC, "sub", "sup", "bdo", "iframe", "embed", "span", "input", "select", "textarea", "label", "button", "optgroup", "option", "legend", "datalist", "keygen", "output", NotificationCompatApi21.CATEGORY_PROGRESS, "meter", "area", a.f, ShareRequestParam.REQ_PARAM_SOURCE, "track", QzoneShare.SHARE_TO_QQ_SUMMARY, "command", "device", "area", "basefont", "bgsound", "menuitem", a.f, ShareRequestParam.REQ_PARAM_SOURCE, "track", ShareRequestParam.RESP_UPLOAD_PIC_PARAM_DATA, "bdi"};
        emptyTags = new String[]{"meta", "link", "base", "frame", ShareRequestParam.REQ_UPLOAD_PIC_PARAM_IMG, "br", "wbr", "embed", "hr", "input", "keygen", "col", "command", "device", "area", "basefont", "bgsound", "menuitem", a.f, ShareRequestParam.REQ_PARAM_SOURCE, "track"};
        formatAsInlineTags = new String[]{QzoneShare.SHARE_TO_QQ_TITLE, DeviceType.IPAD, AppUri.AUTHORITY, "h1", "h2", "h3", "h4", "h5", "h6", "pre", "address", "li", "th", "td", "script", "style", "ins", "del", "s"};
        preserveWhitespaceTags = new String[]{"pre", "plaintext", QzoneShare.SHARE_TO_QQ_TITLE, "textarea"};
        formListedTags = new String[]{"button", "fieldset", "input", "keygen", "object", "output", "select", "textarea"};
        formSubmitTags = new String[]{"input", "keygen", "object", "select", "textarea"};
        for (String tagName : blockTags) {
            register(new Tag(tagName));
        }
        for (String tagName2 : inlineTags) {
            Tag tag = new Tag(tagName2);
            tag.isBlock = false;
            tag.canContainBlock = false;
            tag.formatAsBlock = false;
            register(tag);
        }
        for (String tagName22 : emptyTags) {
            tag = (Tag) tags.get(tagName22);
            Validate.notNull(tag);
            tag.canContainBlock = false;
            tag.canContainInline = false;
            tag.empty = true;
        }
        for (String tagName222 : formatAsInlineTags) {
            tag = (Tag) tags.get(tagName222);
            Validate.notNull(tag);
            tag.formatAsBlock = false;
        }
        for (String tagName2222 : preserveWhitespaceTags) {
            tag = (Tag) tags.get(tagName2222);
            Validate.notNull(tag);
            tag.preserveWhitespace = true;
        }
        for (String tagName22222 : formListedTags) {
            tag = (Tag) tags.get(tagName22222);
            Validate.notNull(tag);
            tag.formList = true;
        }
        String[] strArr = formSubmitTags;
        int length = strArr.length;
        while (i < length) {
            tag = (Tag) tags.get(strArr[i]);
            Validate.notNull(tag);
            tag.formSubmit = true;
            i++;
        }
    }

    private Tag(String tagName) {
        this.isBlock = true;
        this.formatAsBlock = true;
        this.canContainBlock = true;
        this.canContainInline = true;
        this.empty = false;
        this.selfClosing = false;
        this.preserveWhitespace = false;
        this.formList = false;
        this.formSubmit = false;
        this.tagName = tagName.toLowerCase();
    }

    public String getName() {
        return this.tagName;
    }

    public static Tag valueOf(String tagName) {
        Validate.notNull(tagName);
        Tag tag = (Tag) tags.get(tagName);
        if (tag != null) {
            return tag;
        }
        tagName = tagName.trim().toLowerCase();
        Validate.notEmpty(tagName);
        tag = (Tag) tags.get(tagName);
        if (tag != null) {
            return tag;
        }
        tag = new Tag(tagName);
        tag.isBlock = false;
        tag.canContainBlock = true;
        return tag;
    }

    public boolean isBlock() {
        return this.isBlock;
    }

    public boolean formatAsBlock() {
        return this.formatAsBlock;
    }

    public boolean canContainBlock() {
        return this.canContainBlock;
    }

    public boolean isInline() {
        return !this.isBlock;
    }

    public boolean isData() {
        return (this.canContainInline || isEmpty()) ? false : true;
    }

    public boolean isEmpty() {
        return this.empty;
    }

    public boolean isSelfClosing() {
        return this.empty || this.selfClosing;
    }

    public boolean isKnownTag() {
        return tags.containsKey(this.tagName);
    }

    public static boolean isKnownTag(String tagName) {
        return tags.containsKey(tagName);
    }

    public boolean preserveWhitespace() {
        return this.preserveWhitespace;
    }

    public boolean isFormListed() {
        return this.formList;
    }

    public boolean isFormSubmittable() {
        return this.formSubmit;
    }

    Tag setSelfClosing() {
        this.selfClosing = true;
        return this;
    }

    public boolean equals(Object o) {
        boolean z = true;
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tag)) {
            return false;
        }
        Tag tag = (Tag) o;
        if (!this.tagName.equals(tag.tagName) || this.canContainBlock != tag.canContainBlock || this.canContainInline != tag.canContainInline || this.empty != tag.empty || this.formatAsBlock != tag.formatAsBlock || this.isBlock != tag.isBlock || this.preserveWhitespace != tag.preserveWhitespace || this.selfClosing != tag.selfClosing || this.formList != tag.formList) {
            return false;
        }
        if (this.formSubmit != tag.formSubmit) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        int i;
        int i2 = 1;
        int hashCode = ((this.tagName.hashCode() * 31) + (this.isBlock ? 1 : 0)) * 31;
        if (this.formatAsBlock) {
            i = 1;
        } else {
            i = 0;
        }
        hashCode = (hashCode + i) * 31;
        if (this.canContainBlock) {
            i = 1;
        } else {
            i = 0;
        }
        hashCode = (hashCode + i) * 31;
        if (this.canContainInline) {
            i = 1;
        } else {
            i = 0;
        }
        hashCode = (hashCode + i) * 31;
        if (this.empty) {
            i = 1;
        } else {
            i = 0;
        }
        hashCode = (hashCode + i) * 31;
        if (this.selfClosing) {
            i = 1;
        } else {
            i = 0;
        }
        hashCode = (hashCode + i) * 31;
        if (this.preserveWhitespace) {
            i = 1;
        } else {
            i = 0;
        }
        hashCode = (hashCode + i) * 31;
        if (this.formList) {
            i = 1;
        } else {
            i = 0;
        }
        i = (hashCode + i) * 31;
        if (!this.formSubmit) {
            i2 = 0;
        }
        return i + i2;
    }

    public String toString() {
        return this.tagName;
    }

    private static void register(Tag tag) {
        tags.put(tag.tagName, tag);
    }
}
