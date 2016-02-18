package org.jsoup.parser;

import com.alipay.sdk.authjs.a;
import com.alipay.sdk.cons.c;
import com.douban.amonsul.StatConstant;
import com.douban.book.reader.helper.AppUri;
import com.sina.weibo.sdk.component.ShareRequestParam;
import com.tencent.connect.share.QzoneShare;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jsoup.helper.StringUtil;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

public class HtmlTreeBuilder extends TreeBuilder {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final String[] TagSearchButton;
    private static final String[] TagSearchEndTags;
    private static final String[] TagSearchList;
    private static final String[] TagSearchSelectScope;
    private static final String[] TagSearchSpecial;
    private static final String[] TagSearchTableScope;
    private static final String[] TagsScriptStyle;
    public static final String[] TagsSearchInScope;
    private boolean baseUriSetFromDoc;
    private Element contextElement;
    private EndTag emptyEnd;
    private FormElement formElement;
    private ArrayList<Element> formattingElements;
    private boolean fosterInserts;
    private boolean fragmentParsing;
    private boolean framesetOk;
    private Element headElement;
    private HtmlTreeBuilderState originalState;
    private List<String> pendingTableCharacters;
    private String[] specificScopeTarget;
    private HtmlTreeBuilderState state;

    static {
        $assertionsDisabled = !HtmlTreeBuilder.class.desiredAssertionStatus();
        TagsScriptStyle = new String[]{"script", "style"};
        TagsSearchInScope = new String[]{"applet", "caption", "html", "table", "td", "th", "marquee", "object"};
        TagSearchList = new String[]{"ol", "ul"};
        TagSearchButton = new String[]{"button"};
        TagSearchTableScope = new String[]{"html", "table"};
        TagSearchSelectScope = new String[]{"optgroup", "option"};
        TagSearchEndTags = new String[]{"dd", "dt", "li", "option", "optgroup", AppUri.AUTHORITY, "rp", StatConstant.JSON_KEY_RESOLUTION};
        TagSearchSpecial = new String[]{"address", "applet", "area", "article", "aside", "base", "basefont", "bgsound", "blockquote", "body", "br", "button", "caption", "center", "col", "colgroup", "command", "dd", "details", "dir", "div", "dl", "dt", "embed", "fieldset", "figcaption", "figure", "footer", c.c, "frame", "frameset", "h1", "h2", "h3", "h4", "h5", "h6", "head", "header", "hgroup", "hr", "html", "iframe", ShareRequestParam.REQ_UPLOAD_PIC_PARAM_IMG, "input", "isindex", "li", "link", "listing", "marquee", "menu", "meta", "nav", "noembed", "noframes", "noscript", "object", "ol", AppUri.AUTHORITY, a.f, "plaintext", "pre", "script", "section", "select", "style", QzoneShare.SHARE_TO_QQ_SUMMARY, "table", "tbody", "td", "textarea", "tfoot", "th", "thead", QzoneShare.SHARE_TO_QQ_TITLE, "tr", "ul", "wbr", "xmp"};
    }

    public /* bridge */ /* synthetic */ boolean processStartTag(String str, Attributes attributes) {
        return super.processStartTag(str, attributes);
    }

    HtmlTreeBuilder() {
        this.baseUriSetFromDoc = false;
        this.formattingElements = new ArrayList();
        this.pendingTableCharacters = new ArrayList();
        this.emptyEnd = new EndTag();
        this.framesetOk = true;
        this.fosterInserts = false;
        this.fragmentParsing = false;
        this.specificScopeTarget = new String[]{null};
    }

    Document parse(String input, String baseUri, ParseErrorList errors) {
        this.state = HtmlTreeBuilderState.Initial;
        this.baseUriSetFromDoc = false;
        return super.parse(input, baseUri, errors);
    }

    List<Node> parseFragment(String inputFragment, Element context, String baseUri, ParseErrorList errors) {
        this.state = HtmlTreeBuilderState.Initial;
        initialiseParse(inputFragment, baseUri, errors);
        this.contextElement = context;
        this.fragmentParsing = true;
        Element root = null;
        if (context != null) {
            if (context.ownerDocument() != null) {
                this.doc.quirksMode(context.ownerDocument().quirksMode());
            }
            String contextTag = context.tagName();
            if (StringUtil.in(contextTag, QzoneShare.SHARE_TO_QQ_TITLE, "textarea")) {
                this.tokeniser.transition(TokeniserState.Rcdata);
            } else {
                if (StringUtil.in(contextTag, "iframe", "noembed", "noframes", "style", "xmp")) {
                    this.tokeniser.transition(TokeniserState.Rawtext);
                } else if (contextTag.equals("script")) {
                    this.tokeniser.transition(TokeniserState.ScriptData);
                } else if (contextTag.equals("noscript")) {
                    this.tokeniser.transition(TokeniserState.Data);
                } else if (contextTag.equals("plaintext")) {
                    this.tokeniser.transition(TokeniserState.Data);
                } else {
                    this.tokeniser.transition(TokeniserState.Data);
                }
            }
            root = new Element(Tag.valueOf("html"), baseUri);
            this.doc.appendChild(root);
            this.stack.add(root);
            resetInsertionMode();
            Elements contextChain = context.parents();
            contextChain.add(0, context);
            Iterator it = contextChain.iterator();
            while (it.hasNext()) {
                Element parent = (Element) it.next();
                if (parent instanceof FormElement) {
                    this.formElement = (FormElement) parent;
                    break;
                }
            }
        }
        runParser();
        if (context == null || root == null) {
            return this.doc.childNodes();
        }
        return root.childNodes();
    }

    protected boolean process(Token token) {
        this.currentToken = token;
        return this.state.process(token, this);
    }

    boolean process(Token token, HtmlTreeBuilderState state) {
        this.currentToken = token;
        return state.process(token, this);
    }

    void transition(HtmlTreeBuilderState state) {
        this.state = state;
    }

    HtmlTreeBuilderState state() {
        return this.state;
    }

    void markInsertionMode() {
        this.originalState = this.state;
    }

    HtmlTreeBuilderState originalState() {
        return this.originalState;
    }

    void framesetOk(boolean framesetOk) {
        this.framesetOk = framesetOk;
    }

    boolean framesetOk() {
        return this.framesetOk;
    }

    Document getDocument() {
        return this.doc;
    }

    String getBaseUri() {
        return this.baseUri;
    }

    void maybeSetBaseUri(Element base) {
        if (!this.baseUriSetFromDoc) {
            String href = base.absUrl("href");
            if (href.length() != 0) {
                this.baseUri = href;
                this.baseUriSetFromDoc = true;
                this.doc.setBaseUri(href);
            }
        }
    }

    boolean isFragmentParsing() {
        return this.fragmentParsing;
    }

    void error(HtmlTreeBuilderState state) {
        if (this.errors.canAddError()) {
            this.errors.add(new ParseError(this.reader.pos(), "Unexpected token [%s] when in state [%s]", this.currentToken.tokenType(), state));
        }
    }

    Element insert(StartTag startTag) {
        if (startTag.isSelfClosing()) {
            Element el = insertEmpty(startTag);
            this.stack.add(el);
            this.tokeniser.transition(TokeniserState.Data);
            this.tokeniser.emit(this.emptyEnd.reset().name(el.tagName()));
            return el;
        }
        el = new Element(Tag.valueOf(startTag.name()), this.baseUri, startTag.attributes);
        insert(el);
        return el;
    }

    Element insertStartTag(String startTagName) {
        Element el = new Element(Tag.valueOf(startTagName), this.baseUri);
        insert(el);
        return el;
    }

    void insert(Element el) {
        insertNode(el);
        this.stack.add(el);
    }

    Element insertEmpty(StartTag startTag) {
        Tag tag = Tag.valueOf(startTag.name());
        Element el = new Element(tag, this.baseUri, startTag.attributes);
        insertNode(el);
        if (startTag.isSelfClosing()) {
            if (!tag.isKnownTag()) {
                tag.setSelfClosing();
                this.tokeniser.acknowledgeSelfClosingFlag();
            } else if (tag.isSelfClosing()) {
                this.tokeniser.acknowledgeSelfClosingFlag();
            }
        }
        return el;
    }

    FormElement insertForm(StartTag startTag, boolean onStack) {
        FormElement el = new FormElement(Tag.valueOf(startTag.name()), this.baseUri, startTag.attributes);
        setFormElement(el);
        insertNode(el);
        if (onStack) {
            this.stack.add(el);
        }
        return el;
    }

    void insert(Comment commentToken) {
        insertNode(new Comment(commentToken.getData(), this.baseUri));
    }

    void insert(Character characterToken) {
        Node node;
        String tagName = currentElement().tagName();
        if (tagName.equals("script") || tagName.equals("style")) {
            node = new DataNode(characterToken.getData(), this.baseUri);
        } else {
            node = new TextNode(characterToken.getData(), this.baseUri);
        }
        currentElement().appendChild(node);
    }

    private void insertNode(Node node) {
        if (this.stack.size() == 0) {
            this.doc.appendChild(node);
        } else if (isFosterInserts()) {
            insertInFosterParent(node);
        } else {
            currentElement().appendChild(node);
        }
        if ((node instanceof Element) && ((Element) node).tag().isFormListed() && this.formElement != null) {
            this.formElement.addElement((Element) node);
        }
    }

    Element pop() {
        return (Element) this.stack.remove(this.stack.size() - 1);
    }

    void push(Element element) {
        this.stack.add(element);
    }

    ArrayList<Element> getStack() {
        return this.stack;
    }

    boolean onStack(Element el) {
        return isElementInQueue(this.stack, el);
    }

    private boolean isElementInQueue(ArrayList<Element> queue, Element element) {
        for (int pos = queue.size() - 1; pos >= 0; pos--) {
            if (((Element) queue.get(pos)) == element) {
                return true;
            }
        }
        return false;
    }

    Element getFromStack(String elName) {
        for (int pos = this.stack.size() - 1; pos >= 0; pos--) {
            Element next = (Element) this.stack.get(pos);
            if (next.nodeName().equals(elName)) {
                return next;
            }
        }
        return null;
    }

    boolean removeFromStack(Element el) {
        for (int pos = this.stack.size() - 1; pos >= 0; pos--) {
            if (((Element) this.stack.get(pos)) == el) {
                this.stack.remove(pos);
                return true;
            }
        }
        return false;
    }

    void popStackToClose(String elName) {
        int pos = this.stack.size() - 1;
        while (pos >= 0) {
            Element next = (Element) this.stack.get(pos);
            this.stack.remove(pos);
            if (!next.nodeName().equals(elName)) {
                pos--;
            } else {
                return;
            }
        }
    }

    void popStackToClose(String... elNames) {
        int pos = this.stack.size() - 1;
        while (pos >= 0) {
            Element next = (Element) this.stack.get(pos);
            this.stack.remove(pos);
            if (!StringUtil.in(next.nodeName(), elNames)) {
                pos--;
            } else {
                return;
            }
        }
    }

    void popStackToBefore(String elName) {
        int pos = this.stack.size() - 1;
        while (pos >= 0 && !((Element) this.stack.get(pos)).nodeName().equals(elName)) {
            this.stack.remove(pos);
            pos--;
        }
    }

    void clearStackToTableContext() {
        clearStackToContext("table");
    }

    void clearStackToTableBodyContext() {
        clearStackToContext("tbody", "tfoot", "thead");
    }

    void clearStackToTableRowContext() {
        clearStackToContext("tr");
    }

    private void clearStackToContext(String... nodeNames) {
        int pos = this.stack.size() - 1;
        while (pos >= 0) {
            Element next = (Element) this.stack.get(pos);
            if (!StringUtil.in(next.nodeName(), nodeNames) && !next.nodeName().equals("html")) {
                this.stack.remove(pos);
                pos--;
            } else {
                return;
            }
        }
    }

    Element aboveOnStack(Element el) {
        if ($assertionsDisabled || onStack(el)) {
            for (int pos = this.stack.size() - 1; pos >= 0; pos--) {
                if (((Element) this.stack.get(pos)) == el) {
                    return (Element) this.stack.get(pos - 1);
                }
            }
            return null;
        }
        throw new AssertionError();
    }

    void insertOnStackAfter(Element after, Element in) {
        int i = this.stack.lastIndexOf(after);
        Validate.isTrue(i != -1);
        this.stack.add(i + 1, in);
    }

    void replaceOnStack(Element out, Element in) {
        replaceInQueue(this.stack, out, in);
    }

    private void replaceInQueue(ArrayList<Element> queue, Element out, Element in) {
        int i = queue.lastIndexOf(out);
        Validate.isTrue(i != -1);
        queue.set(i, in);
    }

    void resetInsertionMode() {
        boolean last = false;
        int pos = this.stack.size() - 1;
        while (pos >= 0) {
            Element node = (Element) this.stack.get(pos);
            if (pos == 0) {
                last = true;
                node = this.contextElement;
            }
            String name = node.nodeName();
            if ("select".equals(name)) {
                transition(HtmlTreeBuilderState.InSelect);
                return;
            } else if ("td".equals(name) || ("th".equals(name) && !last)) {
                transition(HtmlTreeBuilderState.InCell);
                return;
            } else if ("tr".equals(name)) {
                transition(HtmlTreeBuilderState.InRow);
                return;
            } else if ("tbody".equals(name) || "thead".equals(name) || "tfoot".equals(name)) {
                transition(HtmlTreeBuilderState.InTableBody);
                return;
            } else if ("caption".equals(name)) {
                transition(HtmlTreeBuilderState.InCaption);
                return;
            } else if ("colgroup".equals(name)) {
                transition(HtmlTreeBuilderState.InColumnGroup);
                return;
            } else if ("table".equals(name)) {
                transition(HtmlTreeBuilderState.InTable);
                return;
            } else if ("head".equals(name)) {
                transition(HtmlTreeBuilderState.InBody);
                return;
            } else if ("body".equals(name)) {
                transition(HtmlTreeBuilderState.InBody);
                return;
            } else if ("frameset".equals(name)) {
                transition(HtmlTreeBuilderState.InFrameset);
                return;
            } else if ("html".equals(name)) {
                transition(HtmlTreeBuilderState.BeforeHead);
                return;
            } else if (last) {
                transition(HtmlTreeBuilderState.InBody);
                return;
            } else {
                pos--;
            }
        }
    }

    private boolean inSpecificScope(String targetName, String[] baseTypes, String[] extraTypes) {
        this.specificScopeTarget[0] = targetName;
        return inSpecificScope(this.specificScopeTarget, baseTypes, extraTypes);
    }

    private boolean inSpecificScope(String[] targetNames, String[] baseTypes, String[] extraTypes) {
        for (int pos = this.stack.size() - 1; pos >= 0; pos--) {
            String elName = ((Element) this.stack.get(pos)).nodeName();
            if (StringUtil.in(elName, targetNames)) {
                return true;
            }
            if (StringUtil.in(elName, baseTypes)) {
                return false;
            }
            if (extraTypes != null && StringUtil.in(elName, extraTypes)) {
                return false;
            }
        }
        Validate.fail("Should not be reachable");
        return false;
    }

    boolean inScope(String[] targetNames) {
        return inSpecificScope(targetNames, TagsSearchInScope, null);
    }

    boolean inScope(String targetName) {
        return inScope(targetName, null);
    }

    boolean inScope(String targetName, String[] extras) {
        return inSpecificScope(targetName, TagsSearchInScope, extras);
    }

    boolean inListItemScope(String targetName) {
        return inScope(targetName, TagSearchList);
    }

    boolean inButtonScope(String targetName) {
        return inScope(targetName, TagSearchButton);
    }

    boolean inTableScope(String targetName) {
        return inSpecificScope(targetName, TagSearchTableScope, null);
    }

    boolean inSelectScope(String targetName) {
        for (int pos = this.stack.size() - 1; pos >= 0; pos--) {
            String elName = ((Element) this.stack.get(pos)).nodeName();
            if (elName.equals(targetName)) {
                return true;
            }
            if (!StringUtil.in(elName, TagSearchSelectScope)) {
                return false;
            }
        }
        Validate.fail("Should not be reachable");
        return false;
    }

    void setHeadElement(Element headElement) {
        this.headElement = headElement;
    }

    Element getHeadElement() {
        return this.headElement;
    }

    boolean isFosterInserts() {
        return this.fosterInserts;
    }

    void setFosterInserts(boolean fosterInserts) {
        this.fosterInserts = fosterInserts;
    }

    FormElement getFormElement() {
        return this.formElement;
    }

    void setFormElement(FormElement formElement) {
        this.formElement = formElement;
    }

    void newPendingTableCharacters() {
        this.pendingTableCharacters = new ArrayList();
    }

    List<String> getPendingTableCharacters() {
        return this.pendingTableCharacters;
    }

    void setPendingTableCharacters(List<String> pendingTableCharacters) {
        this.pendingTableCharacters = pendingTableCharacters;
    }

    void generateImpliedEndTags(String excludeTag) {
        while (excludeTag != null && !currentElement().nodeName().equals(excludeTag) && StringUtil.in(currentElement().nodeName(), TagSearchEndTags)) {
            pop();
        }
    }

    void generateImpliedEndTags() {
        generateImpliedEndTags(null);
    }

    boolean isSpecial(Element el) {
        return StringUtil.in(el.nodeName(), TagSearchSpecial);
    }

    Element lastFormattingElement() {
        return this.formattingElements.size() > 0 ? (Element) this.formattingElements.get(this.formattingElements.size() - 1) : null;
    }

    Element removeLastFormattingElement() {
        int size = this.formattingElements.size();
        if (size > 0) {
            return (Element) this.formattingElements.remove(size - 1);
        }
        return null;
    }

    void pushActiveFormattingElements(Element in) {
        int numSeen = 0;
        for (int pos = this.formattingElements.size() - 1; pos >= 0; pos--) {
            Element el = (Element) this.formattingElements.get(pos);
            if (el == null) {
                break;
            }
            if (isSameFormattingElement(in, el)) {
                numSeen++;
            }
            if (numSeen == 3) {
                this.formattingElements.remove(pos);
                break;
            }
        }
        this.formattingElements.add(in);
    }

    private boolean isSameFormattingElement(Element a, Element b) {
        return a.nodeName().equals(b.nodeName()) && a.attributes().equals(b.attributes());
    }

    void reconstructFormattingElements() {
        Element last = lastFormattingElement();
        if (last != null && !onStack(last)) {
            Element entry = last;
            int size = this.formattingElements.size();
            int pos = size - 1;
            boolean skip = false;
            while (pos != 0) {
                pos--;
                entry = (Element) this.formattingElements.get(pos);
                if (entry != null) {
                    if (onStack(entry)) {
                        break;
                    }
                }
                break;
            }
            skip = true;
            do {
                if (!skip) {
                    pos++;
                    entry = (Element) this.formattingElements.get(pos);
                }
                Validate.notNull(entry);
                skip = false;
                Element newEl = insertStartTag(entry.nodeName());
                newEl.attributes().addAll(entry.attributes());
                this.formattingElements.set(pos, newEl);
            } while (pos != size - 1);
        }
    }

    void clearFormattingElementsToLastMarker() {
        while (!this.formattingElements.isEmpty()) {
            if (removeLastFormattingElement() == null) {
                return;
            }
        }
    }

    void removeFromActiveFormattingElements(Element el) {
        for (int pos = this.formattingElements.size() - 1; pos >= 0; pos--) {
            if (((Element) this.formattingElements.get(pos)) == el) {
                this.formattingElements.remove(pos);
                return;
            }
        }
    }

    boolean isInActiveFormattingElements(Element el) {
        return isElementInQueue(this.formattingElements, el);
    }

    Element getActiveFormattingElement(String nodeName) {
        int pos = this.formattingElements.size() - 1;
        while (pos >= 0) {
            Element next = (Element) this.formattingElements.get(pos);
            if (next == null) {
                break;
            } else if (next.nodeName().equals(nodeName)) {
                return next;
            } else {
                pos--;
            }
        }
        return null;
    }

    void replaceActiveFormattingElement(Element out, Element in) {
        replaceInQueue(this.formattingElements, out, in);
    }

    void insertMarkerToFormattingElements() {
        this.formattingElements.add(null);
    }

    void insertInFosterParent(Node in) {
        Element fosterParent;
        Element lastTable = getFromStack("table");
        boolean isLastTableParent = false;
        if (lastTable == null) {
            fosterParent = (Element) this.stack.get(0);
        } else if (lastTable.parent() != null) {
            fosterParent = lastTable.parent();
            isLastTableParent = true;
        } else {
            fosterParent = aboveOnStack(lastTable);
        }
        if (isLastTableParent) {
            Validate.notNull(lastTable);
            lastTable.before(in);
            return;
        }
        fosterParent.appendChild(in);
    }

    public String toString() {
        return "TreeBuilder{currentToken=" + this.currentToken + ", state=" + this.state + ", currentElement=" + currentElement() + '}';
    }
}
