package org.jsoup.parser;

import java.util.ArrayList;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

abstract class TreeBuilder {
    protected String baseUri;
    protected Token currentToken;
    protected Document doc;
    private EndTag end;
    protected ParseErrorList errors;
    CharacterReader reader;
    protected ArrayList<Element> stack;
    private StartTag start;
    Tokeniser tokeniser;

    protected abstract boolean process(Token token);

    TreeBuilder() {
        this.start = new StartTag();
        this.end = new EndTag();
    }

    protected void initialiseParse(String input, String baseUri, ParseErrorList errors) {
        Validate.notNull(input, "String input must not be null");
        Validate.notNull(baseUri, "BaseURI must not be null");
        this.doc = new Document(baseUri);
        this.reader = new CharacterReader(input);
        this.errors = errors;
        this.tokeniser = new Tokeniser(this.reader, errors);
        this.stack = new ArrayList(32);
        this.baseUri = baseUri;
    }

    Document parse(String input, String baseUri) {
        return parse(input, baseUri, ParseErrorList.noTracking());
    }

    Document parse(String input, String baseUri, ParseErrorList errors) {
        initialiseParse(input, baseUri, errors);
        runParser();
        return this.doc;
    }

    protected void runParser() {
        Token token;
        do {
            token = this.tokeniser.read();
            process(token);
            token.reset();
        } while (token.type != TokenType.EOF);
    }

    protected boolean processStartTag(String name) {
        if (this.currentToken == this.start) {
            return process(new StartTag().name(name));
        }
        return process(this.start.reset().name(name));
    }

    public boolean processStartTag(String name, Attributes attrs) {
        if (this.currentToken == this.start) {
            return process(new StartTag().nameAttr(name, attrs));
        }
        this.start.reset();
        this.start.nameAttr(name, attrs);
        return process(this.start);
    }

    protected boolean processEndTag(String name) {
        if (this.currentToken == this.end) {
            return process(new EndTag().name(name));
        }
        return process(this.end.reset().name(name));
    }

    protected Element currentElement() {
        int size = this.stack.size();
        return size > 0 ? (Element) this.stack.get(size - 1) : null;
    }
}
