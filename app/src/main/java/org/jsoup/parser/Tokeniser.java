package org.jsoup.parser;

import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import com.douban.book.reader.constant.Char;
import com.douban.book.reader.entity.Annotation.Privacy;
import java.util.Arrays;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Entities;

final class Tokeniser {
    private static final char[] notCharRefCharsSorted;
    static final char replacementChar = '\ufffd';
    Character charPending;
    private final char[] charRefHolder;
    private StringBuilder charsBuilder;
    private String charsString;
    Comment commentPending;
    StringBuilder dataBuffer;
    Doctype doctypePending;
    private Token emitPending;
    EndTag endPending;
    private ParseErrorList errors;
    private boolean isEmitPending;
    private String lastStartTag;
    private CharacterReader reader;
    private boolean selfClosingFlagAcknowledged;
    StartTag startPending;
    private TokeniserState state;
    Tag tagPending;

    static {
        notCharRefCharsSorted = new char[]{'\t', '\n', Char.CARRIAGE_RETURN, '\f', Char.SPACE, '<', '&'};
        Arrays.sort(notCharRefCharsSorted);
    }

    Tokeniser(CharacterReader reader, ParseErrorList errors) {
        this.state = TokeniserState.Data;
        this.isEmitPending = false;
        this.charsString = null;
        this.charsBuilder = new StringBuilder(AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT);
        this.dataBuffer = new StringBuilder(AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT);
        this.startPending = new StartTag();
        this.endPending = new EndTag();
        this.charPending = new Character();
        this.doctypePending = new Doctype();
        this.commentPending = new Comment();
        this.selfClosingFlagAcknowledged = true;
        this.charRefHolder = new char[1];
        this.reader = reader;
        this.errors = errors;
    }

    Token read() {
        if (!this.selfClosingFlagAcknowledged) {
            error("Self closing flag not acknowledged");
            this.selfClosingFlagAcknowledged = true;
        }
        while (!this.isEmitPending) {
            this.state.read(this, this.reader);
        }
        if (this.charsBuilder.length() > 0) {
            String str = this.charsBuilder.toString();
            this.charsBuilder.delete(0, this.charsBuilder.length());
            this.charsString = null;
            return this.charPending.data(str);
        } else if (this.charsString != null) {
            Token token = this.charPending.data(this.charsString);
            this.charsString = null;
            return token;
        } else {
            this.isEmitPending = false;
            return this.emitPending;
        }
    }

    void emit(Token token) {
        Validate.isFalse(this.isEmitPending, "There is an unread token pending!");
        this.emitPending = token;
        this.isEmitPending = true;
        if (token.type == TokenType.StartTag) {
            StartTag startTag = (StartTag) token;
            this.lastStartTag = startTag.tagName;
            if (startTag.selfClosing) {
                this.selfClosingFlagAcknowledged = false;
            }
        } else if (token.type == TokenType.EndTag && ((EndTag) token).attributes != null) {
            error("Attributes incorrectly present on end tag");
        }
    }

    void emit(String str) {
        if (this.charsString == null) {
            this.charsString = str;
            return;
        }
        if (this.charsBuilder.length() == 0) {
            this.charsBuilder.append(this.charsString);
        }
        this.charsBuilder.append(str);
    }

    void emit(char[] chars) {
        emit(String.valueOf(chars));
    }

    void emit(char c) {
        emit(String.valueOf(c));
    }

    TokeniserState getState() {
        return this.state;
    }

    void transition(TokeniserState state) {
        this.state = state;
    }

    void advanceTransition(TokeniserState state) {
        this.reader.advance();
        this.state = state;
    }

    void acknowledgeSelfClosingFlag() {
        this.selfClosingFlagAcknowledged = true;
    }

    char[] consumeCharacterReference(Character additionalAllowedCharacter, boolean inAttribute) {
        if (this.reader.isEmpty()) {
            return null;
        }
        if (additionalAllowedCharacter != null && additionalAllowedCharacter.charValue() == this.reader.current()) {
            return null;
        }
        if (this.reader.matchesAnySorted(notCharRefCharsSorted)) {
            return null;
        }
        char[] charRef = this.charRefHolder;
        this.reader.mark();
        if (this.reader.matchConsume("#")) {
            boolean isHexMode = this.reader.matchConsumeIgnoreCase(Privacy.PRIVATE);
            String numRef = isHexMode ? this.reader.consumeHexSequence() : this.reader.consumeDigitSequence();
            if (numRef.length() == 0) {
                characterReferenceError("numeric reference with no numerals");
                this.reader.rewindToMark();
                return null;
            }
            if (!this.reader.matchConsume(";")) {
                characterReferenceError("missing semicolon");
            }
            int charval = -1;
            try {
                charval = Integer.valueOf(numRef, isHexMode ? 16 : 10).intValue();
            } catch (NumberFormatException e) {
            }
            if (charval == -1 || ((charval >= 55296 && charval <= 57343) || charval > 1114111)) {
                characterReferenceError("character outside of valid range");
                charRef[0] = replacementChar;
                return charRef;
            } else if (charval >= AccessibilityNodeInfoCompat.ACTION_CUT) {
                return Character.toChars(charval);
            } else {
                charRef[0] = (char) charval;
                return charRef;
            }
        }
        boolean found;
        String nameRef = this.reader.consumeLetterThenDigitSequence();
        boolean looksLegit = this.reader.matches(';');
        if (Entities.isBaseNamedEntity(nameRef) || (Entities.isNamedEntity(nameRef) && looksLegit)) {
            found = true;
        } else {
            found = false;
        }
        if (!found) {
            this.reader.rewindToMark();
            if (looksLegit) {
                characterReferenceError(String.format("invalid named referenece '%s'", new Object[]{nameRef}));
            }
            return null;
        } else if (inAttribute && (this.reader.matchesLetter() || this.reader.matchesDigit() || this.reader.matchesAny('=', Char.HYPHEN, Char.UNDERLINE))) {
            this.reader.rewindToMark();
            return null;
        } else {
            if (!this.reader.matchConsume(";")) {
                characterReferenceError("missing semicolon");
            }
            charRef[0] = Entities.getCharacterByName(nameRef).charValue();
            return charRef;
        }
    }

    Tag createTagPending(boolean start) {
        this.tagPending = start ? this.startPending.reset() : this.endPending.reset();
        return this.tagPending;
    }

    void emitTagPending() {
        this.tagPending.finaliseTag();
        emit(this.tagPending);
    }

    void createCommentPending() {
        this.commentPending.reset();
    }

    void emitCommentPending() {
        emit(this.commentPending);
    }

    void createDoctypePending() {
        this.doctypePending.reset();
    }

    void emitDoctypePending() {
        emit(this.doctypePending);
    }

    void createTempBuffer() {
        Token.reset(this.dataBuffer);
    }

    boolean isAppropriateEndTagToken() {
        return this.lastStartTag != null && this.tagPending.tagName.equals(this.lastStartTag);
    }

    String appropriateEndTagName() {
        if (this.lastStartTag == null) {
            return null;
        }
        return this.lastStartTag;
    }

    void error(TokeniserState state) {
        if (this.errors.canAddError()) {
            this.errors.add(new ParseError(this.reader.pos(), "Unexpected character '%s' in input state [%s]", Character.valueOf(this.reader.current()), state));
        }
    }

    void eofError(TokeniserState state) {
        if (this.errors.canAddError()) {
            this.errors.add(new ParseError(this.reader.pos(), "Unexpectedly reached end of file (EOF) in input state [%s]", state));
        }
    }

    private void characterReferenceError(String message) {
        if (this.errors.canAddError()) {
            this.errors.add(new ParseError(this.reader.pos(), "Invalid character reference: %s", message));
        }
    }

    private void error(String errorMsg) {
        if (this.errors.canAddError()) {
            this.errors.add(new ParseError(this.reader.pos(), errorMsg));
        }
    }

    boolean currentNodeInHtmlNS() {
        return true;
    }

    String unescapeEntities(boolean inAttribute) {
        StringBuilder builder = new StringBuilder();
        while (!this.reader.isEmpty()) {
            builder.append(this.reader.consumeTo('&'));
            if (this.reader.matches('&')) {
                this.reader.consume();
                char[] c = consumeCharacterReference(null, inAttribute);
                if (c == null || c.length == 0) {
                    builder.append('&');
                } else {
                    builder.append(c);
                }
            }
        }
        return builder.toString();
    }
}
