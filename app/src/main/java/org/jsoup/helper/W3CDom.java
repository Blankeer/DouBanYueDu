package org.jsoup.helper;

import java.io.StringWriter;
import java.util.Iterator;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class W3CDom {
    protected DocumentBuilderFactory factory;

    protected class W3CBuilder implements NodeVisitor {
        private Element dest;
        private final Document doc;

        public W3CBuilder(Document doc) {
            this.doc = doc;
        }

        public void head(Node source, int depth) {
            if (source instanceof org.jsoup.nodes.Element) {
                org.jsoup.nodes.Element sourceEl = (org.jsoup.nodes.Element) source;
                Element el = this.doc.createElement(sourceEl.tagName());
                copyAttributes(sourceEl, el);
                if (this.dest == null) {
                    this.doc.appendChild(el);
                } else {
                    this.dest.appendChild(el);
                }
                this.dest = el;
            } else if (source instanceof TextNode) {
                this.dest.appendChild(this.doc.createTextNode(((TextNode) source).getWholeText()));
            } else if (source instanceof Comment) {
                this.dest.appendChild(this.doc.createComment(((Comment) source).getData()));
            } else if (source instanceof DataNode) {
                this.dest.appendChild(this.doc.createTextNode(((DataNode) source).getWholeData()));
            }
        }

        public void tail(Node source, int depth) {
            if ((source instanceof org.jsoup.nodes.Element) && (this.dest.getParentNode() instanceof Element)) {
                this.dest = (Element) this.dest.getParentNode();
            }
        }

        private void copyAttributes(Node source, Element el) {
            Iterator it = source.attributes().iterator();
            while (it.hasNext()) {
                Attribute attribute = (Attribute) it.next();
                el.setAttribute(attribute.getKey(), attribute.getValue());
            }
        }
    }

    public W3CDom() {
        this.factory = DocumentBuilderFactory.newInstance();
    }

    public Document fromJsoup(org.jsoup.nodes.Document in) {
        Validate.notNull(in);
        try {
            Document out = this.factory.newDocumentBuilder().newDocument();
            convert(in, out);
            return out;
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    public void convert(org.jsoup.nodes.Document in, Document out) {
        if (!StringUtil.isBlank(in.location())) {
            out.setDocumentURI(in.location());
        }
        new NodeTraversor(new W3CBuilder(out)).traverse(in.child(0));
    }

    public String asString(Document doc) {
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            TransformerFactory.newInstance().newTransformer().transform(domSource, new StreamResult(writer));
            return writer.toString();
        } catch (TransformerException e) {
            throw new IllegalStateException(e);
        }
    }
}
