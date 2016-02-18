package org.jsoup.nodes;

import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.nodes.Document.OutputSettings.Syntax;

public class DocumentType extends Node {
    private static final String NAME = "name";
    private static final String PUBLIC_ID = "publicId";
    private static final String SYSTEM_ID = "systemId";

    public DocumentType(String name, String publicId, String systemId, String baseUri) {
        super(baseUri);
        attr(NAME, name);
        attr(PUBLIC_ID, publicId);
        attr(SYSTEM_ID, systemId);
    }

    public String nodeName() {
        return "#doctype";
    }

    void outerHtmlHead(StringBuilder accum, int depth, OutputSettings out) {
        if (out.syntax() != Syntax.html || has(PUBLIC_ID) || has(SYSTEM_ID)) {
            accum.append("<!DOCTYPE");
        } else {
            accum.append("<!doctype");
        }
        if (has(NAME)) {
            accum.append(" ").append(attr(NAME));
        }
        if (has(PUBLIC_ID)) {
            accum.append(" PUBLIC \"").append(attr(PUBLIC_ID)).append('\"');
        }
        if (has(SYSTEM_ID)) {
            accum.append(" \"").append(attr(SYSTEM_ID)).append('\"');
        }
        accum.append('>');
    }

    void outerHtmlTail(StringBuilder accum, int depth, OutputSettings out) {
    }

    private boolean has(String attribute) {
        return !StringUtil.isBlank(attr(attribute));
    }
}
