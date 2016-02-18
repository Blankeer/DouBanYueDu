package org.jsoup.nodes;

import com.j256.ormlite.stmt.query.SimpleComparison;
import com.sina.weibo.sdk.component.ShareRequestParam;
import org.jsoup.nodes.Document.OutputSettings;

public class XmlDeclaration extends Node {
    static final String DECL_KEY = "declaration";
    private final boolean isProcessingInstruction;

    public XmlDeclaration(String data, String baseUri, boolean isProcessingInstruction) {
        super(baseUri);
        this.attributes.put(DECL_KEY, data);
        this.isProcessingInstruction = isProcessingInstruction;
    }

    public String nodeName() {
        return "#declaration";
    }

    public String getWholeDeclaration() {
        String decl = this.attributes.get(DECL_KEY);
        if (!decl.equals("xml") || this.attributes.size() <= 1) {
            return this.attributes.get(DECL_KEY);
        }
        StringBuilder sb = new StringBuilder(decl);
        String version = this.attributes.get(ShareRequestParam.REQ_PARAM_VERSION);
        if (version != null) {
            sb.append(" version=\"").append(version).append("\"");
        }
        String encoding = this.attributes.get("encoding");
        if (encoding != null) {
            sb.append(" encoding=\"").append(encoding).append("\"");
        }
        return sb.toString();
    }

    void outerHtmlHead(StringBuilder accum, int depth, OutputSettings out) {
        accum.append(SimpleComparison.LESS_THAN_OPERATION).append(this.isProcessingInstruction ? "!" : "?").append(getWholeDeclaration()).append(SimpleComparison.GREATER_THAN_OPERATION);
    }

    void outerHtmlTail(StringBuilder accum, int depth, OutputSettings out) {
    }

    public String toString() {
        return outerHtml();
    }
}
