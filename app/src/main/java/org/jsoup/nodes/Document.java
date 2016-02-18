package org.jsoup.nodes;

import com.sina.weibo.sdk.component.ShareRequestParam;
import com.tencent.connect.share.QzoneShare;
import com.wnafee.vector.BuildConfig;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.realm.internal.Table;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.helper.StringUtil;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

public class Document extends Element {
    private String location;
    private OutputSettings outputSettings;
    private QuirksMode quirksMode;
    private boolean updateMetaCharset;

    public static class OutputSettings implements Cloneable {
        private Charset charset;
        private CharsetEncoder charsetEncoder;
        private EscapeMode escapeMode;
        private int indentAmount;
        private boolean outline;
        private boolean prettyPrint;
        private Syntax syntax;

        public enum Syntax {
            html,
            xml
        }

        public OutputSettings() {
            this.escapeMode = EscapeMode.base;
            this.charset = Charset.forName(HttpRequest.CHARSET_UTF8);
            this.charsetEncoder = this.charset.newEncoder();
            this.prettyPrint = true;
            this.outline = false;
            this.indentAmount = 1;
            this.syntax = Syntax.html;
        }

        public EscapeMode escapeMode() {
            return this.escapeMode;
        }

        public OutputSettings escapeMode(EscapeMode escapeMode) {
            this.escapeMode = escapeMode;
            return this;
        }

        public Charset charset() {
            return this.charset;
        }

        public OutputSettings charset(Charset charset) {
            this.charset = charset;
            this.charsetEncoder = charset.newEncoder();
            return this;
        }

        public OutputSettings charset(String charset) {
            charset(Charset.forName(charset));
            return this;
        }

        CharsetEncoder encoder() {
            return this.charsetEncoder;
        }

        public Syntax syntax() {
            return this.syntax;
        }

        public OutputSettings syntax(Syntax syntax) {
            this.syntax = syntax;
            return this;
        }

        public boolean prettyPrint() {
            return this.prettyPrint;
        }

        public OutputSettings prettyPrint(boolean pretty) {
            this.prettyPrint = pretty;
            return this;
        }

        public boolean outline() {
            return this.outline;
        }

        public OutputSettings outline(boolean outlineMode) {
            this.outline = outlineMode;
            return this;
        }

        public int indentAmount() {
            return this.indentAmount;
        }

        public OutputSettings indentAmount(int indentAmount) {
            Validate.isTrue(indentAmount >= 0);
            this.indentAmount = indentAmount;
            return this;
        }

        public OutputSettings clone() {
            try {
                OutputSettings clone = (OutputSettings) super.clone();
                clone.charset(this.charset.name());
                clone.escapeMode = EscapeMode.valueOf(this.escapeMode.name());
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public enum QuirksMode {
        noQuirks,
        quirks,
        limitedQuirks
    }

    public Document(String baseUri) {
        super(Tag.valueOf("#root"), baseUri);
        this.outputSettings = new OutputSettings();
        this.quirksMode = QuirksMode.noQuirks;
        this.updateMetaCharset = false;
        this.location = baseUri;
    }

    public static Document createShell(String baseUri) {
        Validate.notNull(baseUri);
        Document doc = new Document(baseUri);
        Element html = doc.appendElement("html");
        html.appendElement("head");
        html.appendElement("body");
        return doc;
    }

    public String location() {
        return this.location;
    }

    public Element head() {
        return findFirstElementByTagName("head", this);
    }

    public Element body() {
        return findFirstElementByTagName("body", this);
    }

    public String title() {
        Element titleEl = getElementsByTag(QzoneShare.SHARE_TO_QQ_TITLE).first();
        return titleEl != null ? StringUtil.normaliseWhitespace(titleEl.text()).trim() : Table.STRING_DEFAULT_VALUE;
    }

    public void title(String title) {
        Validate.notNull(title);
        Element titleEl = getElementsByTag(QzoneShare.SHARE_TO_QQ_TITLE).first();
        if (titleEl == null) {
            head().appendElement(QzoneShare.SHARE_TO_QQ_TITLE).text(title);
        } else {
            titleEl.text(title);
        }
    }

    public Element createElement(String tagName) {
        return new Element(Tag.valueOf(tagName), baseUri());
    }

    public Document normalise() {
        Element htmlEl = findFirstElementByTagName("html", this);
        if (htmlEl == null) {
            htmlEl = appendElement("html");
        }
        if (head() == null) {
            htmlEl.prependElement("head");
        }
        if (body() == null) {
            htmlEl.appendElement("body");
        }
        normaliseTextNodes(head());
        normaliseTextNodes(htmlEl);
        normaliseTextNodes(this);
        normaliseStructure("head", htmlEl);
        normaliseStructure("body", htmlEl);
        ensureMetaCharsetElement();
        return this;
    }

    private void normaliseTextNodes(Element element) {
        List<Node> toMove = new ArrayList();
        for (Node node : element.childNodes) {
            Node node2;
            if (node2 instanceof TextNode) {
                TextNode tn = (TextNode) node2;
                if (!tn.isBlank()) {
                    toMove.add(tn);
                }
            }
        }
        for (int i = toMove.size() - 1; i >= 0; i--) {
            node2 = (Node) toMove.get(i);
            element.removeChild(node2);
            body().prependChild(new TextNode(" ", Table.STRING_DEFAULT_VALUE));
            body().prependChild(node2);
        }
    }

    private void normaliseStructure(String tag, Element htmlEl) {
        Elements elements = getElementsByTag(tag);
        Element master = elements.first();
        if (elements.size() > 1) {
            Node dupe;
            List<Node> toMove = new ArrayList();
            for (int i = 1; i < elements.size(); i++) {
                dupe = (Node) elements.get(i);
                for (Node node : dupe.childNodes) {
                    toMove.add(node);
                }
                dupe.remove();
            }
            for (Node dupe2 : toMove) {
                master.appendChild(dupe2);
            }
        }
        if (!master.parent().equals(htmlEl)) {
            htmlEl.appendChild(master);
        }
    }

    private Element findFirstElementByTagName(String tag, Node node) {
        if (node.nodeName().equals(tag)) {
            return (Element) node;
        }
        for (Node child : node.childNodes) {
            Node found = findFirstElementByTagName(tag, child);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    public String outerHtml() {
        return super.html();
    }

    public Element text(String text) {
        body().text(text);
        return this;
    }

    public String nodeName() {
        return "#document";
    }

    public void charset(Charset charset) {
        updateMetaCharsetElement(true);
        this.outputSettings.charset(charset);
        ensureMetaCharsetElement();
    }

    public Charset charset() {
        return this.outputSettings.charset();
    }

    public void updateMetaCharsetElement(boolean update) {
        this.updateMetaCharset = update;
    }

    public boolean updateMetaCharsetElement() {
        return this.updateMetaCharset;
    }

    public Document clone() {
        Document clone = (Document) super.clone();
        clone.outputSettings = this.outputSettings.clone();
        return clone;
    }

    private void ensureMetaCharsetElement() {
        if (this.updateMetaCharset) {
            Syntax syntax = outputSettings().syntax();
            if (syntax == Syntax.html) {
                Element metaCharset = select("meta[charset]").first();
                if (metaCharset != null) {
                    metaCharset.attr(HttpRequest.PARAM_CHARSET, charset().displayName());
                } else {
                    Element head = head();
                    if (head != null) {
                        head.appendElement("meta").attr(HttpRequest.PARAM_CHARSET, charset().displayName());
                    }
                }
                select("meta[name=charset]").remove();
            } else if (syntax == Syntax.xml) {
                Node node = (Node) childNodes().get(0);
                XmlDeclaration decl;
                if (node instanceof XmlDeclaration) {
                    decl = (XmlDeclaration) node;
                    if (decl.attr("declaration").equals("xml")) {
                        decl.attr("encoding", charset().displayName());
                        if (decl.attr(ShareRequestParam.REQ_PARAM_VERSION) != null) {
                            decl.attr(ShareRequestParam.REQ_PARAM_VERSION, BuildConfig.VERSION_NAME);
                            return;
                        }
                        return;
                    }
                    decl = new XmlDeclaration("xml", this.baseUri, false);
                    decl.attr(ShareRequestParam.REQ_PARAM_VERSION, BuildConfig.VERSION_NAME);
                    decl.attr("encoding", charset().displayName());
                    prependChild(decl);
                    return;
                }
                decl = new XmlDeclaration("xml", this.baseUri, false);
                decl.attr(ShareRequestParam.REQ_PARAM_VERSION, BuildConfig.VERSION_NAME);
                decl.attr("encoding", charset().displayName());
                prependChild(decl);
            }
        }
    }

    public OutputSettings outputSettings() {
        return this.outputSettings;
    }

    public Document outputSettings(OutputSettings outputSettings) {
        Validate.notNull(outputSettings);
        this.outputSettings = outputSettings;
        return this;
    }

    public QuirksMode quirksMode() {
        return this.quirksMode;
    }

    public Document quirksMode(QuirksMode quirksMode) {
        this.quirksMode = quirksMode;
        return this;
    }
}
