package org.jsoup.parser;

import java.util.List;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document.OutputSettings.Syntax;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.nodes.XmlDeclaration;
import u.aly.ci;
import u.aly.dj;
import u.aly.dx;

public class XmlTreeBuilder extends TreeBuilder {

    /* renamed from: org.jsoup.parser.XmlTreeBuilder.1 */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jsoup$parser$Token$TokenType;

        static {
            $SwitchMap$org$jsoup$parser$Token$TokenType = new int[TokenType.values().length];
            try {
                $SwitchMap$org$jsoup$parser$Token$TokenType[TokenType.StartTag.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jsoup$parser$Token$TokenType[TokenType.EndTag.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$jsoup$parser$Token$TokenType[TokenType.Comment.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$jsoup$parser$Token$TokenType[TokenType.Character.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$jsoup$parser$Token$TokenType[TokenType.Doctype.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$org$jsoup$parser$Token$TokenType[TokenType.EOF.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    public /* bridge */ /* synthetic */ boolean processStartTag(String str, Attributes attributes) {
        return super.processStartTag(str, attributes);
    }

    protected void initialiseParse(String input, String baseUri, ParseErrorList errors) {
        super.initialiseParse(input, baseUri, errors);
        this.stack.add(this.doc);
        this.doc.outputSettings().syntax(Syntax.xml);
    }

    protected boolean process(Token token) {
        switch (AnonymousClass1.$SwitchMap$org$jsoup$parser$Token$TokenType[token.type.ordinal()]) {
            case dx.b /*1*/:
                insert(token.asStartTag());
                break;
            case dx.c /*2*/:
                popStackToClose(token.asEndTag());
                break;
            case dx.d /*3*/:
                insert(token.asComment());
                break;
            case dx.e /*4*/:
                insert(token.asCharacter());
                break;
            case dj.f /*5*/:
                insert(token.asDoctype());
                break;
            case ci.g /*6*/:
                break;
            default:
                Validate.fail("Unexpected token type: " + token.type);
                break;
        }
        return true;
    }

    private void insertNode(Node node) {
        currentElement().appendChild(node);
    }

    Element insert(StartTag startTag) {
        Tag tag = Tag.valueOf(startTag.name());
        Element el = new Element(tag, this.baseUri, startTag.attributes);
        insertNode(el);
        if (startTag.isSelfClosing()) {
            this.tokeniser.acknowledgeSelfClosingFlag();
            if (!tag.isKnownTag()) {
                tag.setSelfClosing();
            }
        } else {
            this.stack.add(el);
        }
        return el;
    }

    void insert(Comment commentToken) {
        Node comment = new Comment(commentToken.getData(), this.baseUri);
        Node insert = comment;
        if (commentToken.bogus) {
            String data = comment.getData();
            if (data.length() > 1 && (data.startsWith("!") || data.startsWith("?"))) {
                insert = new XmlDeclaration(data.substring(1), comment.baseUri(), data.startsWith("!"));
            }
        }
        insertNode(insert);
    }

    void insert(Character characterToken) {
        insertNode(new TextNode(characterToken.getData(), this.baseUri));
    }

    void insert(Doctype d) {
        insertNode(new DocumentType(d.getName(), d.getPublicIdentifier(), d.getSystemIdentifier(), this.baseUri));
    }

    private void popStackToClose(EndTag endTag) {
        int pos;
        String elName = endTag.name();
        Element firstFound = null;
        for (pos = this.stack.size() - 1; pos >= 0; pos--) {
            Element next = (Element) this.stack.get(pos);
            if (next.nodeName().equals(elName)) {
                firstFound = next;
                break;
            }
        }
        if (firstFound != null) {
            pos = this.stack.size() - 1;
            while (pos >= 0) {
                next = (Element) this.stack.get(pos);
                this.stack.remove(pos);
                if (next != firstFound) {
                    pos--;
                } else {
                    return;
                }
            }
        }
    }

    List<Node> parseFragment(String inputFragment, String baseUri, ParseErrorList errors) {
        initialiseParse(inputFragment, baseUri, errors);
        runParser();
        return this.doc.childNodes();
    }
}
