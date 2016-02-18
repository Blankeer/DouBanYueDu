package org.jsoup.parser;

import com.j256.ormlite.stmt.query.SimpleComparison;
import io.realm.internal.Table;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.BooleanAttribute;

abstract class Token {
    TokenType type;

    enum TokenType {
        Doctype,
        StartTag,
        EndTag,
        Comment,
        Character,
        EOF
    }

    static final class Character extends Token {
        private String data;

        Character() {
            super();
            this.type = TokenType.Character;
        }

        Token reset() {
            this.data = null;
            return this;
        }

        Character data(String data) {
            this.data = data;
            return this;
        }

        String getData() {
            return this.data;
        }

        public String toString() {
            return getData();
        }
    }

    static final class Comment extends Token {
        boolean bogus;
        final StringBuilder data;

        Token reset() {
            Token.reset(this.data);
            this.bogus = false;
            return this;
        }

        Comment() {
            super();
            this.data = new StringBuilder();
            this.bogus = false;
            this.type = TokenType.Comment;
        }

        String getData() {
            return this.data.toString();
        }

        public String toString() {
            return "<!--" + getData() + "-->";
        }
    }

    static final class Doctype extends Token {
        boolean forceQuirks;
        final StringBuilder name;
        final StringBuilder publicIdentifier;
        final StringBuilder systemIdentifier;

        Doctype() {
            super();
            this.name = new StringBuilder();
            this.publicIdentifier = new StringBuilder();
            this.systemIdentifier = new StringBuilder();
            this.forceQuirks = false;
            this.type = TokenType.Doctype;
        }

        Token reset() {
            Token.reset(this.name);
            Token.reset(this.publicIdentifier);
            Token.reset(this.systemIdentifier);
            this.forceQuirks = false;
            return this;
        }

        String getName() {
            return this.name.toString();
        }

        String getPublicIdentifier() {
            return this.publicIdentifier.toString();
        }

        public String getSystemIdentifier() {
            return this.systemIdentifier.toString();
        }

        public boolean isForceQuirks() {
            return this.forceQuirks;
        }
    }

    static final class EOF extends Token {
        EOF() {
            super();
            this.type = TokenType.EOF;
        }

        Token reset() {
            return this;
        }
    }

    static abstract class Tag extends Token {
        Attributes attributes;
        private boolean hasEmptyAttributeValue;
        private boolean hasPendingAttributeValue;
        private String pendingAttributeName;
        private StringBuilder pendingAttributeValue;
        boolean selfClosing;
        protected String tagName;

        Tag() {
            super();
            this.pendingAttributeValue = new StringBuilder();
            this.hasEmptyAttributeValue = false;
            this.hasPendingAttributeValue = false;
            this.selfClosing = false;
        }

        Tag reset() {
            this.tagName = null;
            this.pendingAttributeName = null;
            Token.reset(this.pendingAttributeValue);
            this.hasEmptyAttributeValue = false;
            this.hasPendingAttributeValue = false;
            this.selfClosing = false;
            this.attributes = null;
            return this;
        }

        final void newAttribute() {
            if (this.attributes == null) {
                this.attributes = new Attributes();
            }
            if (this.pendingAttributeName != null) {
                Attribute attribute;
                if (this.hasPendingAttributeValue) {
                    attribute = new Attribute(this.pendingAttributeName, this.pendingAttributeValue.toString());
                } else if (this.hasEmptyAttributeValue) {
                    attribute = new Attribute(this.pendingAttributeName, Table.STRING_DEFAULT_VALUE);
                } else {
                    attribute = new BooleanAttribute(this.pendingAttributeName);
                }
                this.attributes.put(attribute);
            }
            this.pendingAttributeName = null;
            this.hasEmptyAttributeValue = false;
            this.hasPendingAttributeValue = false;
            Token.reset(this.pendingAttributeValue);
        }

        final void finaliseTag() {
            if (this.pendingAttributeName != null) {
                newAttribute();
            }
        }

        final String name() {
            boolean z = this.tagName == null || this.tagName.length() == 0;
            Validate.isFalse(z);
            return this.tagName;
        }

        final Tag name(String name) {
            this.tagName = name;
            return this;
        }

        final boolean isSelfClosing() {
            return this.selfClosing;
        }

        final Attributes getAttributes() {
            return this.attributes;
        }

        final void appendTagName(String append) {
            if (this.tagName != null) {
                append = this.tagName.concat(append);
            }
            this.tagName = append;
        }

        final void appendTagName(char append) {
            appendTagName(String.valueOf(append));
        }

        final void appendAttributeName(String append) {
            if (this.pendingAttributeName != null) {
                append = this.pendingAttributeName.concat(append);
            }
            this.pendingAttributeName = append;
        }

        final void appendAttributeName(char append) {
            appendAttributeName(String.valueOf(append));
        }

        final void appendAttributeValue(String append) {
            ensureAttributeValue();
            this.pendingAttributeValue.append(append);
        }

        final void appendAttributeValue(char append) {
            ensureAttributeValue();
            this.pendingAttributeValue.append(append);
        }

        final void appendAttributeValue(char[] append) {
            ensureAttributeValue();
            this.pendingAttributeValue.append(append);
        }

        final void setEmptyAttributeValue() {
            this.hasEmptyAttributeValue = true;
        }

        private void ensureAttributeValue() {
            this.hasPendingAttributeValue = true;
        }
    }

    static final class EndTag extends Tag {
        EndTag() {
            this.type = TokenType.EndTag;
        }

        public String toString() {
            return "</" + name() + SimpleComparison.GREATER_THAN_OPERATION;
        }
    }

    static final class StartTag extends Tag {
        StartTag() {
            this.attributes = new Attributes();
            this.type = TokenType.StartTag;
        }

        Tag reset() {
            super.reset();
            this.attributes = new Attributes();
            return this;
        }

        StartTag nameAttr(String name, Attributes attributes) {
            this.tagName = name;
            this.attributes = attributes;
            return this;
        }

        public String toString() {
            if (this.attributes == null || this.attributes.size() <= 0) {
                return SimpleComparison.LESS_THAN_OPERATION + name() + SimpleComparison.GREATER_THAN_OPERATION;
            }
            return SimpleComparison.LESS_THAN_OPERATION + name() + " " + this.attributes.toString() + SimpleComparison.GREATER_THAN_OPERATION;
        }
    }

    abstract Token reset();

    private Token() {
    }

    String tokenType() {
        return getClass().getSimpleName();
    }

    static void reset(StringBuilder sb) {
        if (sb != null) {
            sb.delete(0, sb.length());
        }
    }

    final boolean isDoctype() {
        return this.type == TokenType.Doctype;
    }

    final Doctype asDoctype() {
        return (Doctype) this;
    }

    final boolean isStartTag() {
        return this.type == TokenType.StartTag;
    }

    final StartTag asStartTag() {
        return (StartTag) this;
    }

    final boolean isEndTag() {
        return this.type == TokenType.EndTag;
    }

    final EndTag asEndTag() {
        return (EndTag) this;
    }

    final boolean isComment() {
        return this.type == TokenType.Comment;
    }

    final Comment asComment() {
        return (Comment) this;
    }

    final boolean isCharacter() {
        return this.type == TokenType.Character;
    }

    final Character asCharacter() {
        return (Character) this;
    }

    final boolean isEOF() {
        return this.type == TokenType.EOF;
    }
}
