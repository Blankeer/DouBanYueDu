package org.jsoup.nodes;

import com.douban.book.reader.constant.Char;
import com.douban.book.reader.entity.DbCacheEntity.Column;
import com.douban.book.reader.helper.WorksListUri;
import com.j256.ormlite.stmt.query.SimpleComparison;
import io.realm.internal.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.jsoup.helper.StringUtil;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.nodes.Document.OutputSettings.Syntax;
import org.jsoup.parser.Parser;
import org.jsoup.parser.Tag;
import org.jsoup.select.Collector;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator;
import org.jsoup.select.Evaluator.AllElements;
import org.jsoup.select.Evaluator.Attribute;
import org.jsoup.select.Evaluator.AttributeStarting;
import org.jsoup.select.Evaluator.AttributeWithValue;
import org.jsoup.select.Evaluator.AttributeWithValueContaining;
import org.jsoup.select.Evaluator.AttributeWithValueEnding;
import org.jsoup.select.Evaluator.AttributeWithValueMatching;
import org.jsoup.select.Evaluator.AttributeWithValueNot;
import org.jsoup.select.Evaluator.AttributeWithValueStarting;
import org.jsoup.select.Evaluator.Class;
import org.jsoup.select.Evaluator.ContainsOwnText;
import org.jsoup.select.Evaluator.ContainsText;
import org.jsoup.select.Evaluator.Id;
import org.jsoup.select.Evaluator.IndexEquals;
import org.jsoup.select.Evaluator.IndexGreaterThan;
import org.jsoup.select.Evaluator.IndexLessThan;
import org.jsoup.select.Evaluator.Matches;
import org.jsoup.select.Evaluator.MatchesOwn;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;
import org.jsoup.select.Selector;

public class Element extends Node {
    private static final Pattern classSplit;
    private Tag tag;

    /* renamed from: org.jsoup.nodes.Element.1 */
    class AnonymousClass1 implements NodeVisitor {
        final /* synthetic */ StringBuilder val$accum;

        AnonymousClass1(StringBuilder stringBuilder) {
            this.val$accum = stringBuilder;
        }

        public void head(Node node, int depth) {
            if (node instanceof TextNode) {
                Element.appendNormalisedText(this.val$accum, (TextNode) node);
            } else if (node instanceof Element) {
                Element element = (Element) node;
                if (this.val$accum.length() <= 0) {
                    return;
                }
                if ((element.isBlock() || element.tag.getName().equals("br")) && !TextNode.lastCharIsWhitespace(this.val$accum)) {
                    this.val$accum.append(" ");
                }
            }
        }

        public void tail(Node node, int depth) {
        }
    }

    static {
        classSplit = Pattern.compile("\\s+");
    }

    public Element(Tag tag, String baseUri, Attributes attributes) {
        super(baseUri, attributes);
        Validate.notNull(tag);
        this.tag = tag;
    }

    public Element(Tag tag, String baseUri) {
        this(tag, baseUri, new Attributes());
    }

    public String nodeName() {
        return this.tag.getName();
    }

    public String tagName() {
        return this.tag.getName();
    }

    public Element tagName(String tagName) {
        Validate.notEmpty(tagName, "Tag name must not be empty.");
        this.tag = Tag.valueOf(tagName);
        return this;
    }

    public Tag tag() {
        return this.tag;
    }

    public boolean isBlock() {
        return this.tag.isBlock();
    }

    public String id() {
        return this.attributes.get(WorksListUri.KEY_ID);
    }

    public Element attr(String attributeKey, String attributeValue) {
        super.attr(attributeKey, attributeValue);
        return this;
    }

    public Element attr(String attributeKey, boolean attributeValue) {
        this.attributes.put(attributeKey, attributeValue);
        return this;
    }

    public Map<String, String> dataset() {
        return this.attributes.dataset();
    }

    public final Element parent() {
        return (Element) this.parentNode;
    }

    public Elements parents() {
        Elements parents = new Elements();
        accumulateParents(this, parents);
        return parents;
    }

    private static void accumulateParents(Element el, Elements parents) {
        Element parent = el.parent();
        if (parent != null && !parent.tagName().equals("#root")) {
            parents.add(parent);
            accumulateParents(parent, parents);
        }
    }

    public Element child(int index) {
        return (Element) children().get(index);
    }

    public Elements children() {
        List elements = new ArrayList(this.childNodes.size());
        for (Node node : this.childNodes) {
            if (node instanceof Element) {
                elements.add((Element) node);
            }
        }
        return new Elements(elements);
    }

    public List<TextNode> textNodes() {
        List<TextNode> textNodes = new ArrayList();
        for (Node node : this.childNodes) {
            if (node instanceof TextNode) {
                textNodes.add((TextNode) node);
            }
        }
        return Collections.unmodifiableList(textNodes);
    }

    public List<DataNode> dataNodes() {
        List<DataNode> dataNodes = new ArrayList();
        for (Node node : this.childNodes) {
            if (node instanceof DataNode) {
                dataNodes.add((DataNode) node);
            }
        }
        return Collections.unmodifiableList(dataNodes);
    }

    public Elements select(String cssQuery) {
        return Selector.select(cssQuery, this);
    }

    public Element appendChild(Node child) {
        Validate.notNull(child);
        reparentChild(child);
        ensureChildNodes();
        this.childNodes.add(child);
        child.setSiblingIndex(this.childNodes.size() - 1);
        return this;
    }

    public Element prependChild(Node child) {
        Validate.notNull(child);
        addChildren(0, child);
        return this;
    }

    public Element insertChildren(int index, Collection<? extends Node> children) {
        Validate.notNull(children, "Children collection to be inserted must not be null.");
        int currentSize = childNodeSize();
        if (index < 0) {
            index += currentSize + 1;
        }
        boolean z = index >= 0 && index <= currentSize;
        Validate.isTrue(z, "Insert position out of bounds.");
        ArrayList<Node> nodes = new ArrayList(children);
        addChildren(index, (Node[]) nodes.toArray(new Node[nodes.size()]));
        return this;
    }

    public Element appendElement(String tagName) {
        Element child = new Element(Tag.valueOf(tagName), baseUri());
        appendChild(child);
        return child;
    }

    public Element prependElement(String tagName) {
        Element child = new Element(Tag.valueOf(tagName), baseUri());
        prependChild(child);
        return child;
    }

    public Element appendText(String text) {
        appendChild(new TextNode(text, baseUri()));
        return this;
    }

    public Element prependText(String text) {
        prependChild(new TextNode(text, baseUri()));
        return this;
    }

    public Element append(String html) {
        Validate.notNull(html);
        List<Node> nodes = Parser.parseFragment(html, this, baseUri());
        addChildren((Node[]) nodes.toArray(new Node[nodes.size()]));
        return this;
    }

    public Element prepend(String html) {
        Validate.notNull(html);
        List<Node> nodes = Parser.parseFragment(html, this, baseUri());
        addChildren(0, (Node[]) nodes.toArray(new Node[nodes.size()]));
        return this;
    }

    public Element before(String html) {
        return (Element) super.before(html);
    }

    public Element before(Node node) {
        return (Element) super.before(node);
    }

    public Element after(String html) {
        return (Element) super.after(html);
    }

    public Element after(Node node) {
        return (Element) super.after(node);
    }

    public Element empty() {
        this.childNodes.clear();
        return this;
    }

    public Element wrap(String html) {
        return (Element) super.wrap(html);
    }

    public String cssSelector() {
        if (id().length() > 0) {
            return "#" + id();
        }
        StringBuilder selector = new StringBuilder(tagName());
        String classes = StringUtil.join(classNames(), ".");
        if (classes.length() > 0) {
            selector.append(Char.DOT).append(classes);
        }
        if (parent() == null || (parent() instanceof Document)) {
            return selector.toString();
        }
        selector.insert(0, " > ");
        if (parent().select(selector.toString()).size() > 1) {
            selector.append(String.format(":nth-child(%d)", new Object[]{Integer.valueOf(elementSiblingIndex().intValue() + 1)}));
        }
        return parent().cssSelector() + selector.toString();
    }

    public Elements siblingElements() {
        if (this.parentNode == null) {
            return new Elements(0);
        }
        List<Element> elements = parent().children();
        Elements siblings = new Elements(elements.size() - 1);
        for (Element el : elements) {
            if (el != this) {
                siblings.add(el);
            }
        }
        return siblings;
    }

    public Element nextElementSibling() {
        if (this.parentNode == null) {
            return null;
        }
        List<Element> siblings = parent().children();
        Integer index = indexInList(this, siblings);
        Validate.notNull(index);
        if (siblings.size() > index.intValue() + 1) {
            return (Element) siblings.get(index.intValue() + 1);
        }
        return null;
    }

    public Element previousElementSibling() {
        if (this.parentNode == null) {
            return null;
        }
        List<Element> siblings = parent().children();
        Integer index = indexInList(this, siblings);
        Validate.notNull(index);
        if (index.intValue() > 0) {
            return (Element) siblings.get(index.intValue() - 1);
        }
        return null;
    }

    public Element firstElementSibling() {
        List<Element> siblings = parent().children();
        return siblings.size() > 1 ? (Element) siblings.get(0) : null;
    }

    public Integer elementSiblingIndex() {
        if (parent() == null) {
            return Integer.valueOf(0);
        }
        return indexInList(this, parent().children());
    }

    public Element lastElementSibling() {
        List<Element> siblings = parent().children();
        return siblings.size() > 1 ? (Element) siblings.get(siblings.size() - 1) : null;
    }

    private static <E extends Element> Integer indexInList(Element search, List<E> elements) {
        Validate.notNull(search);
        Validate.notNull(elements);
        for (int i = 0; i < elements.size(); i++) {
            if (((Element) elements.get(i)) == search) {
                return Integer.valueOf(i);
            }
        }
        return null;
    }

    public Elements getElementsByTag(String tagName) {
        Validate.notEmpty(tagName);
        return Collector.collect(new Evaluator.Tag(tagName.toLowerCase().trim()), this);
    }

    public Element getElementById(String id) {
        Validate.notEmpty(id);
        Elements elements = Collector.collect(new Id(id), this);
        if (elements.size() > 0) {
            return (Element) elements.get(0);
        }
        return null;
    }

    public Elements getElementsByClass(String className) {
        Validate.notEmpty(className);
        return Collector.collect(new Class(className), this);
    }

    public Elements getElementsByAttribute(String key) {
        Validate.notEmpty(key);
        return Collector.collect(new Attribute(key.trim().toLowerCase()), this);
    }

    public Elements getElementsByAttributeStarting(String keyPrefix) {
        Validate.notEmpty(keyPrefix);
        return Collector.collect(new AttributeStarting(keyPrefix.trim().toLowerCase()), this);
    }

    public Elements getElementsByAttributeValue(String key, String value) {
        return Collector.collect(new AttributeWithValue(key, value), this);
    }

    public Elements getElementsByAttributeValueNot(String key, String value) {
        return Collector.collect(new AttributeWithValueNot(key, value), this);
    }

    public Elements getElementsByAttributeValueStarting(String key, String valuePrefix) {
        return Collector.collect(new AttributeWithValueStarting(key, valuePrefix), this);
    }

    public Elements getElementsByAttributeValueEnding(String key, String valueSuffix) {
        return Collector.collect(new AttributeWithValueEnding(key, valueSuffix), this);
    }

    public Elements getElementsByAttributeValueContaining(String key, String match) {
        return Collector.collect(new AttributeWithValueContaining(key, match), this);
    }

    public Elements getElementsByAttributeValueMatching(String key, Pattern pattern) {
        return Collector.collect(new AttributeWithValueMatching(key, pattern), this);
    }

    public Elements getElementsByAttributeValueMatching(String key, String regex) {
        try {
            return getElementsByAttributeValueMatching(key, Pattern.compile(regex));
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Pattern syntax error: " + regex, e);
        }
    }

    public Elements getElementsByIndexLessThan(int index) {
        return Collector.collect(new IndexLessThan(index), this);
    }

    public Elements getElementsByIndexGreaterThan(int index) {
        return Collector.collect(new IndexGreaterThan(index), this);
    }

    public Elements getElementsByIndexEquals(int index) {
        return Collector.collect(new IndexEquals(index), this);
    }

    public Elements getElementsContainingText(String searchText) {
        return Collector.collect(new ContainsText(searchText), this);
    }

    public Elements getElementsContainingOwnText(String searchText) {
        return Collector.collect(new ContainsOwnText(searchText), this);
    }

    public Elements getElementsMatchingText(Pattern pattern) {
        return Collector.collect(new Matches(pattern), this);
    }

    public Elements getElementsMatchingText(String regex) {
        try {
            return getElementsMatchingText(Pattern.compile(regex));
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Pattern syntax error: " + regex, e);
        }
    }

    public Elements getElementsMatchingOwnText(Pattern pattern) {
        return Collector.collect(new MatchesOwn(pattern), this);
    }

    public Elements getElementsMatchingOwnText(String regex) {
        try {
            return getElementsMatchingOwnText(Pattern.compile(regex));
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Pattern syntax error: " + regex, e);
        }
    }

    public Elements getAllElements() {
        return Collector.collect(new AllElements(), this);
    }

    public String text() {
        StringBuilder accum = new StringBuilder();
        new NodeTraversor(new AnonymousClass1(accum)).traverse(this);
        return accum.toString().trim();
    }

    public String ownText() {
        StringBuilder sb = new StringBuilder();
        ownText(sb);
        return sb.toString().trim();
    }

    private void ownText(StringBuilder accum) {
        for (Node child : this.childNodes) {
            if (child instanceof TextNode) {
                appendNormalisedText(accum, (TextNode) child);
            } else if (child instanceof Element) {
                appendWhitespaceIfBr((Element) child, accum);
            }
        }
    }

    private static void appendNormalisedText(StringBuilder accum, TextNode textNode) {
        String text = textNode.getWholeText();
        if (preserveWhitespace(textNode.parentNode)) {
            accum.append(text);
        } else {
            StringUtil.appendNormalisedWhitespace(accum, text, TextNode.lastCharIsWhitespace(accum));
        }
    }

    private static void appendWhitespaceIfBr(Element element, StringBuilder accum) {
        if (element.tag.getName().equals("br") && !TextNode.lastCharIsWhitespace(accum)) {
            accum.append(" ");
        }
    }

    static boolean preserveWhitespace(Node node) {
        if (node == null || !(node instanceof Element)) {
            return false;
        }
        Element element = (Element) node;
        if (element.tag.preserveWhitespace() || (element.parent() != null && element.parent().tag.preserveWhitespace())) {
            return true;
        }
        return false;
    }

    public Element text(String text) {
        Validate.notNull(text);
        empty();
        appendChild(new TextNode(text, this.baseUri));
        return this;
    }

    public boolean hasText() {
        for (Node child : this.childNodes) {
            if (child instanceof TextNode) {
                if (!((TextNode) child).isBlank()) {
                    return true;
                }
            } else if ((child instanceof Element) && ((Element) child).hasText()) {
                return true;
            }
        }
        return false;
    }

    public String data() {
        StringBuilder sb = new StringBuilder();
        for (Node childNode : this.childNodes) {
            if (childNode instanceof DataNode) {
                sb.append(((DataNode) childNode).getWholeData());
            } else if (childNode instanceof Element) {
                sb.append(((Element) childNode).data());
            }
        }
        return sb.toString();
    }

    public String className() {
        return attr("class").trim();
    }

    public Set<String> classNames() {
        Set<String> classNames = new LinkedHashSet(Arrays.asList(classSplit.split(className())));
        classNames.remove(Table.STRING_DEFAULT_VALUE);
        return classNames;
    }

    public Element classNames(Set<String> classNames) {
        Validate.notNull(classNames);
        this.attributes.put("class", StringUtil.join((Collection) classNames, " "));
        return this;
    }

    public boolean hasClass(String className) {
        String classAttr = this.attributes.get("class");
        if (classAttr.equals(Table.STRING_DEFAULT_VALUE) || classAttr.length() < className.length()) {
            return false;
        }
        for (String name : classSplit.split(classAttr)) {
            if (className.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public Element addClass(String className) {
        Validate.notNull(className);
        Set<String> classes = classNames();
        classes.add(className);
        classNames(classes);
        return this;
    }

    public Element removeClass(String className) {
        Validate.notNull(className);
        Set<String> classes = classNames();
        classes.remove(className);
        classNames(classes);
        return this;
    }

    public Element toggleClass(String className) {
        Validate.notNull(className);
        Set<String> classes = classNames();
        if (classes.contains(className)) {
            classes.remove(className);
        } else {
            classes.add(className);
        }
        classNames(classes);
        return this;
    }

    public String val() {
        if (tagName().equals("textarea")) {
            return text();
        }
        return attr(Column.VALUE);
    }

    public Element val(String value) {
        if (tagName().equals("textarea")) {
            text(value);
        } else {
            attr(Column.VALUE, value);
        }
        return this;
    }

    void outerHtmlHead(StringBuilder accum, int depth, OutputSettings out) {
        if (accum.length() > 0 && out.prettyPrint() && (this.tag.formatAsBlock() || ((parent() != null && parent().tag().formatAsBlock()) || out.outline()))) {
            indent(accum, depth, out);
        }
        accum.append(SimpleComparison.LESS_THAN_OPERATION).append(tagName());
        this.attributes.html(accum, out);
        if (!this.childNodes.isEmpty() || !this.tag.isSelfClosing()) {
            accum.append(SimpleComparison.GREATER_THAN_OPERATION);
        } else if (out.syntax() == Syntax.html && this.tag.isEmpty()) {
            accum.append('>');
        } else {
            accum.append(" />");
        }
    }

    void outerHtmlTail(StringBuilder accum, int depth, OutputSettings out) {
        if (!this.childNodes.isEmpty() || !this.tag.isSelfClosing()) {
            if (out.prettyPrint() && !this.childNodes.isEmpty() && (this.tag.formatAsBlock() || (out.outline() && (this.childNodes.size() > 1 || (this.childNodes.size() == 1 && !(this.childNodes.get(0) instanceof TextNode)))))) {
                indent(accum, depth, out);
            }
            accum.append("</").append(tagName()).append(SimpleComparison.GREATER_THAN_OPERATION);
        }
    }

    public String html() {
        StringBuilder accum = new StringBuilder();
        html(accum);
        return getOutputSettings().prettyPrint() ? accum.toString().trim() : accum.toString();
    }

    private void html(StringBuilder accum) {
        for (Node node : this.childNodes) {
            node.outerHtml(accum);
        }
    }

    public Element html(String html) {
        empty();
        append(html);
        return this;
    }

    public String toString() {
        return outerHtml();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass() || !super.equals(o)) {
            return false;
        }
        return this.tag.equals(((Element) o).tag);
    }

    public int hashCode() {
        return (super.hashCode() * 31) + (this.tag != null ? this.tag.hashCode() : 0);
    }

    public Element clone() {
        return (Element) super.clone();
    }
}
