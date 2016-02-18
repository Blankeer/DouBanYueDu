package org.jsoup.parser;

import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

public class Parser {
    private static final int DEFAULT_MAX_ERRORS = 0;
    private ParseErrorList errors;
    private int maxErrors;
    private TreeBuilder treeBuilder;

    public Parser(TreeBuilder treeBuilder) {
        this.maxErrors = 0;
        this.treeBuilder = treeBuilder;
    }

    public Document parseInput(String html, String baseUri) {
        this.errors = isTrackErrors() ? ParseErrorList.tracking(this.maxErrors) : ParseErrorList.noTracking();
        return this.treeBuilder.parse(html, baseUri, this.errors);
    }

    public TreeBuilder getTreeBuilder() {
        return this.treeBuilder;
    }

    public Parser setTreeBuilder(TreeBuilder treeBuilder) {
        this.treeBuilder = treeBuilder;
        return this;
    }

    public boolean isTrackErrors() {
        return this.maxErrors > 0;
    }

    public Parser setTrackErrors(int maxErrors) {
        this.maxErrors = maxErrors;
        return this;
    }

    public List<ParseError> getErrors() {
        return this.errors;
    }

    public static Document parse(String html, String baseUri) {
        return new HtmlTreeBuilder().parse(html, baseUri, ParseErrorList.noTracking());
    }

    public static List<Node> parseFragment(String fragmentHtml, Element context, String baseUri) {
        return new HtmlTreeBuilder().parseFragment(fragmentHtml, context, baseUri, ParseErrorList.noTracking());
    }

    public static List<Node> parseXmlFragment(String fragmentXml, String baseUri) {
        return new XmlTreeBuilder().parseFragment(fragmentXml, baseUri, ParseErrorList.noTracking());
    }

    public static Document parseBodyFragment(String bodyHtml, String baseUri) {
        Document doc = Document.createShell(baseUri);
        Element body = doc.body();
        List<Node> nodeList = parseFragment(bodyHtml, body, baseUri);
        Node[] nodes = (Node[]) nodeList.toArray(new Node[nodeList.size()]);
        for (int i = nodes.length - 1; i > 0; i--) {
            nodes[i].remove();
        }
        for (Node node : nodes) {
            body.appendChild(node);
        }
        return doc;
    }

    public static String unescapeEntities(String string, boolean inAttribute) {
        return new Tokeniser(new CharacterReader(string), ParseErrorList.noTracking()).unescapeEntities(inAttribute);
    }

    public static Document parseBodyFragmentRelaxed(String bodyHtml, String baseUri) {
        return parse(bodyHtml, baseUri);
    }

    public static Parser htmlParser() {
        return new Parser(new HtmlTreeBuilder());
    }

    public static Parser xmlParser() {
        return new Parser(new XmlTreeBuilder());
    }
}
