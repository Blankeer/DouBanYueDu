package org.jsoup.nodes;

import com.douban.book.reader.constant.Char;
import io.realm.internal.Table;
import org.jsoup.helper.StringUtil;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document.OutputSettings;

public class TextNode extends Node {
    private static final String TEXT_KEY = "text";
    String text;

    public TextNode(String text, String baseUri) {
        this.baseUri = baseUri;
        this.text = text;
    }

    public String nodeName() {
        return "#text";
    }

    public String text() {
        return normaliseWhitespace(getWholeText());
    }

    public TextNode text(String text) {
        this.text = text;
        if (this.attributes != null) {
            this.attributes.put(TEXT_KEY, text);
        }
        return this;
    }

    public String getWholeText() {
        return this.attributes == null ? this.text : this.attributes.get(TEXT_KEY);
    }

    public boolean isBlank() {
        return StringUtil.isBlank(getWholeText());
    }

    public TextNode splitText(int offset) {
        boolean z;
        if (offset >= 0) {
            z = true;
        } else {
            z = false;
        }
        Validate.isTrue(z, "Split offset must be not be negative");
        if (offset < this.text.length()) {
            z = true;
        } else {
            z = false;
        }
        Validate.isTrue(z, "Split offset must not be greater than current text length");
        String head = getWholeText().substring(0, offset);
        String tail = getWholeText().substring(offset);
        text(head);
        TextNode tailNode = new TextNode(tail, baseUri());
        if (parent() != null) {
            parent().addChildren(siblingIndex() + 1, tailNode);
        }
        return tailNode;
    }

    void outerHtmlHead(StringBuilder accum, int depth, OutputSettings out) {
        boolean normaliseWhite;
        if (out.prettyPrint() && ((siblingIndex() == 0 && (this.parentNode instanceof Element) && ((Element) this.parentNode).tag().formatAsBlock() && !isBlank()) || (out.outline() && siblingNodes().size() > 0 && !isBlank()))) {
            indent(accum, depth, out);
        }
        if (out.prettyPrint() && (parent() instanceof Element) && !Element.preserveWhitespace(parent())) {
            normaliseWhite = true;
        } else {
            normaliseWhite = false;
        }
        Entities.escape(accum, getWholeText(), out, false, normaliseWhite, false);
    }

    void outerHtmlTail(StringBuilder accum, int depth, OutputSettings out) {
    }

    public String toString() {
        return outerHtml();
    }

    public static TextNode createFromEncoded(String encodedText, String baseUri) {
        return new TextNode(Entities.unescape(encodedText), baseUri);
    }

    static String normaliseWhitespace(String text) {
        return StringUtil.normaliseWhitespace(text);
    }

    static String stripLeadingWhitespace(String text) {
        return text.replaceFirst("^\\s+", Table.STRING_DEFAULT_VALUE);
    }

    static boolean lastCharIsWhitespace(StringBuilder sb) {
        return sb.length() != 0 && sb.charAt(sb.length() - 1) == Char.SPACE;
    }

    private void ensureAttributes() {
        if (this.attributes == null) {
            this.attributes = new Attributes();
            this.attributes.put(TEXT_KEY, this.text);
        }
    }

    public String attr(String attributeKey) {
        ensureAttributes();
        return super.attr(attributeKey);
    }

    public Attributes attributes() {
        ensureAttributes();
        return super.attributes();
    }

    public Node attr(String attributeKey, String attributeValue) {
        ensureAttributes();
        return super.attr(attributeKey, attributeValue);
    }

    public boolean hasAttr(String attributeKey) {
        ensureAttributes();
        return super.hasAttr(attributeKey);
    }

    public Node removeAttr(String attributeKey) {
        ensureAttributes();
        return super.removeAttr(attributeKey);
    }

    public String absUrl(String attributeKey) {
        ensureAttributes();
        return super.absUrl(attributeKey);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        TextNode textNode = (TextNode) o;
        if (this.text != null) {
            if (this.text.equals(textNode.text)) {
                return true;
            }
        } else if (textNode.text == null) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (super.hashCode() * 31) + (this.text != null ? this.text.hashCode() : 0);
    }
}
