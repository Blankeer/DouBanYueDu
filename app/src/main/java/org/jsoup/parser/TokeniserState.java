package org.jsoup.parser;

import android.support.v4.internal.view.SupportMenu;
import android.support.v4.media.TransportMediator;
import com.alipay.sdk.protocol.h;
import com.douban.book.reader.constant.Char;
import com.j256.ormlite.stmt.query.SimpleComparison;
import com.tencent.connect.share.QQShare;
import java.util.Arrays;
import se.emilsjolander.stickylistheaders.R;
import u.aly.dx;

enum TokeniserState {
    Data {
        void read(Tokeniser t, CharacterReader r) {
            switch (r.current()) {
                case dx.a /*0*/:
                    t.error((TokeniserState) this);
                    t.emit(r.consume());
                case HeaderMapDB.HASHER_DOUBLE_ARRAY /*38*/:
                    t.advanceTransition(CharacterReferenceInData);
                case QQShare.QQ_SHARE_SUMMARY_MAX_LENGTH /*60*/:
                    t.advanceTransition(TagOpen);
                case SupportMenu.USER_MASK /*65535*/:
                    t.emit(new EOF());
                default:
                    t.emit(r.consumeData());
            }
        }
    },
    CharacterReferenceInData {
        void read(Tokeniser t, CharacterReader r) {
            char[] c = t.consumeCharacterReference(null, false);
            if (c == null) {
                t.emit('&');
            } else {
                t.emit(c);
            }
            t.transition(Data);
        }
    },
    Rcdata {
        void read(Tokeniser t, CharacterReader r) {
            switch (r.current()) {
                case dx.a /*0*/:
                    t.error((TokeniserState) this);
                    r.advance();
                    t.emit((char) TokeniserState.replacementChar);
                case HeaderMapDB.HASHER_DOUBLE_ARRAY /*38*/:
                    t.advanceTransition(CharacterReferenceInRcdata);
                case QQShare.QQ_SHARE_SUMMARY_MAX_LENGTH /*60*/:
                    t.advanceTransition(RcdataLessthanSign);
                case SupportMenu.USER_MASK /*65535*/:
                    t.emit(new EOF());
                default:
                    t.emit(r.consumeToAny('&', '<', TokeniserState.nullChar));
            }
        }
    },
    CharacterReferenceInRcdata {
        void read(Tokeniser t, CharacterReader r) {
            char[] c = t.consumeCharacterReference(null, false);
            if (c == null) {
                t.emit('&');
            } else {
                t.emit(c);
            }
            t.transition(Rcdata);
        }
    },
    Rawtext {
        void read(Tokeniser t, CharacterReader r) {
            switch (r.current()) {
                case dx.a /*0*/:
                    t.error((TokeniserState) this);
                    r.advance();
                    t.emit((char) TokeniserState.replacementChar);
                case QQShare.QQ_SHARE_SUMMARY_MAX_LENGTH /*60*/:
                    t.advanceTransition(RawtextLessthanSign);
                case SupportMenu.USER_MASK /*65535*/:
                    t.emit(new EOF());
                default:
                    t.emit(r.consumeToAny('<', TokeniserState.nullChar));
            }
        }
    },
    ScriptData {
        void read(Tokeniser t, CharacterReader r) {
            switch (r.current()) {
                case dx.a /*0*/:
                    t.error((TokeniserState) this);
                    r.advance();
                    t.emit((char) TokeniserState.replacementChar);
                case QQShare.QQ_SHARE_SUMMARY_MAX_LENGTH /*60*/:
                    t.advanceTransition(ScriptDataLessthanSign);
                case SupportMenu.USER_MASK /*65535*/:
                    t.emit(new EOF());
                default:
                    t.emit(r.consumeToAny('<', TokeniserState.nullChar));
            }
        }
    },
    PLAINTEXT {
        void read(Tokeniser t, CharacterReader r) {
            switch (r.current()) {
                case dx.a /*0*/:
                    t.error((TokeniserState) this);
                    r.advance();
                    t.emit((char) TokeniserState.replacementChar);
                case SupportMenu.USER_MASK /*65535*/:
                    t.emit(new EOF());
                default:
                    t.emit(r.consumeTo((char) TokeniserState.nullChar));
            }
        }
    },
    TagOpen {
        void read(Tokeniser t, CharacterReader r) {
            switch (r.current()) {
                case HeaderMapDB.HASHER_BASIC /*33*/:
                    t.advanceTransition(MarkupDeclarationOpen);
                case HeaderMapDB.SERIALIZER_COMPRESSION_WRAPPER /*47*/:
                    t.advanceTransition(EndTagOpen);
                case Header.LONG_15 /*63*/:
                    t.advanceTransition(BogusComment);
                default:
                    if (r.matchesLetter()) {
                        t.createTagPending(true);
                        t.transition(TagName);
                        return;
                    }
                    t.error((TokeniserState) this);
                    t.emit('<');
                    t.transition(Data);
            }
        }
    },
    EndTagOpen {
        void read(Tokeniser t, CharacterReader r) {
            if (r.isEmpty()) {
                t.eofError(this);
                t.emit("</");
                t.transition(Data);
            } else if (r.matchesLetter()) {
                t.createTagPending(false);
                t.transition(TagName);
            } else if (r.matches('>')) {
                t.error((TokeniserState) this);
                t.advanceTransition(Data);
            } else {
                t.error((TokeniserState) this);
                t.advanceTransition(BogusComment);
            }
        }
    },
    TagName {
        void read(Tokeniser t, CharacterReader r) {
            t.tagPending.appendTagName(r.consumeTagName().toLowerCase());
            switch (r.consume()) {
                case dx.a /*0*/:
                    t.tagPending.appendTagName(TokeniserState.replacementStr);
                case h.h /*9*/:
                case h.i /*10*/:
                case R.styleable.StickyListHeadersListView_android_scrollingCache /*12*/:
                case R.styleable.StickyListHeadersListView_android_transcriptMode /*13*/:
                case TransportMediator.FLAG_KEY_MEDIA_STOP /*32*/:
                    t.transition(BeforeAttributeName);
                case HeaderMapDB.SERIALIZER_COMPRESSION_WRAPPER /*47*/:
                    t.transition(SelfClosingStartTag);
                case Header.LONG_14 /*62*/:
                    t.emitTagPending();
                    t.transition(Data);
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.transition(Data);
                default:
            }
        }
    },
    RcdataLessthanSign {
        void read(Tokeniser t, CharacterReader r) {
            if (r.matches((char) Char.SLASH)) {
                t.createTempBuffer();
                t.advanceTransition(RCDATAEndTagOpen);
            } else if (!r.matchesLetter() || t.appropriateEndTagName() == null || r.containsIgnoreCase("</" + t.appropriateEndTagName())) {
                t.emit(SimpleComparison.LESS_THAN_OPERATION);
                t.transition(Rcdata);
            } else {
                t.tagPending = t.createTagPending(false).name(t.appropriateEndTagName());
                t.emitTagPending();
                r.unconsume();
                t.transition(Data);
            }
        }
    },
    RCDATAEndTagOpen {
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                t.createTagPending(false);
                t.tagPending.appendTagName(Character.toLowerCase(r.current()));
                t.dataBuffer.append(Character.toLowerCase(r.current()));
                t.advanceTransition(RCDATAEndTagName);
                return;
            }
            t.emit("</");
            t.transition(Rcdata);
        }
    },
    RCDATAEndTagName {
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                String name = r.consumeLetterSequence();
                t.tagPending.appendTagName(name.toLowerCase());
                t.dataBuffer.append(name);
                return;
            }
            switch (r.consume()) {
                case h.h /*9*/:
                case h.i /*10*/:
                case R.styleable.StickyListHeadersListView_android_scrollingCache /*12*/:
                case R.styleable.StickyListHeadersListView_android_transcriptMode /*13*/:
                case TransportMediator.FLAG_KEY_MEDIA_STOP /*32*/:
                    if (t.isAppropriateEndTagToken()) {
                        t.transition(BeforeAttributeName);
                    } else {
                        anythingElse(t, r);
                    }
                case HeaderMapDB.SERIALIZER_COMPRESSION_WRAPPER /*47*/:
                    if (t.isAppropriateEndTagToken()) {
                        t.transition(SelfClosingStartTag);
                    } else {
                        anythingElse(t, r);
                    }
                case Header.LONG_14 /*62*/:
                    if (t.isAppropriateEndTagToken()) {
                        t.emitTagPending();
                        t.transition(Data);
                        return;
                    }
                    anythingElse(t, r);
                default:
                    anythingElse(t, r);
            }
        }

        private void anythingElse(Tokeniser t, CharacterReader r) {
            t.emit("</" + t.dataBuffer.toString());
            r.unconsume();
            t.transition(Rcdata);
        }
    },
    RawtextLessthanSign {
        void read(Tokeniser t, CharacterReader r) {
            if (r.matches((char) Char.SLASH)) {
                t.createTempBuffer();
                t.advanceTransition(RawtextEndTagOpen);
                return;
            }
            t.emit('<');
            t.transition(Rawtext);
        }
    },
    RawtextEndTagOpen {
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                t.createTagPending(false);
                t.transition(RawtextEndTagName);
                return;
            }
            t.emit("</");
            t.transition(Rawtext);
        }
    },
    RawtextEndTagName {
        void read(Tokeniser t, CharacterReader r) {
            TokeniserState.handleDataEndTag(t, r, Rawtext);
        }
    },
    ScriptDataLessthanSign {
        void read(Tokeniser t, CharacterReader r) {
            switch (r.consume()) {
                case HeaderMapDB.HASHER_BASIC /*33*/:
                    t.emit("<!");
                    t.transition(ScriptDataEscapeStart);
                case HeaderMapDB.SERIALIZER_COMPRESSION_WRAPPER /*47*/:
                    t.createTempBuffer();
                    t.transition(ScriptDataEndTagOpen);
                default:
                    t.emit(SimpleComparison.LESS_THAN_OPERATION);
                    r.unconsume();
                    t.transition(ScriptData);
            }
        }
    },
    ScriptDataEndTagOpen {
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                t.createTagPending(false);
                t.transition(ScriptDataEndTagName);
                return;
            }
            t.emit("</");
            t.transition(ScriptData);
        }
    },
    ScriptDataEndTagName {
        void read(Tokeniser t, CharacterReader r) {
            TokeniserState.handleDataEndTag(t, r, ScriptData);
        }
    },
    ScriptDataEscapeStart {
        void read(Tokeniser t, CharacterReader r) {
            if (r.matches((char) Char.HYPHEN)) {
                t.emit((char) Char.HYPHEN);
                t.advanceTransition(ScriptDataEscapeStartDash);
                return;
            }
            t.transition(ScriptData);
        }
    },
    ScriptDataEscapeStartDash {
        void read(Tokeniser t, CharacterReader r) {
            if (r.matches((char) Char.HYPHEN)) {
                t.emit((char) Char.HYPHEN);
                t.advanceTransition(ScriptDataEscapedDashDash);
                return;
            }
            t.transition(ScriptData);
        }
    },
    ScriptDataEscaped {
        void read(Tokeniser t, CharacterReader r) {
            if (r.isEmpty()) {
                t.eofError(this);
                t.transition(Data);
                return;
            }
            switch (r.current()) {
                case dx.a /*0*/:
                    t.error((TokeniserState) this);
                    r.advance();
                    t.emit((char) TokeniserState.replacementChar);
                case QQShare.QQ_SHARE_TITLE_MAX_LENGTH /*45*/:
                    t.emit((char) Char.HYPHEN);
                    t.advanceTransition(ScriptDataEscapedDash);
                case QQShare.QQ_SHARE_SUMMARY_MAX_LENGTH /*60*/:
                    t.advanceTransition(ScriptDataEscapedLessthanSign);
                default:
                    t.emit(r.consumeToAny(Char.HYPHEN, '<', TokeniserState.nullChar));
            }
        }
    },
    ScriptDataEscapedDash {
        void read(Tokeniser t, CharacterReader r) {
            if (r.isEmpty()) {
                t.eofError(this);
                t.transition(Data);
                return;
            }
            char c = r.consume();
            switch (c) {
                case dx.a /*0*/:
                    t.error((TokeniserState) this);
                    t.emit((char) TokeniserState.replacementChar);
                    t.transition(ScriptDataEscaped);
                case QQShare.QQ_SHARE_TITLE_MAX_LENGTH /*45*/:
                    t.emit(c);
                    t.transition(ScriptDataEscapedDashDash);
                case QQShare.QQ_SHARE_SUMMARY_MAX_LENGTH /*60*/:
                    t.transition(ScriptDataEscapedLessthanSign);
                default:
                    t.emit(c);
                    t.transition(ScriptDataEscaped);
            }
        }
    },
    ScriptDataEscapedDashDash {
        void read(Tokeniser t, CharacterReader r) {
            if (r.isEmpty()) {
                t.eofError(this);
                t.transition(Data);
                return;
            }
            char c = r.consume();
            switch (c) {
                case dx.a /*0*/:
                    t.error((TokeniserState) this);
                    t.emit((char) TokeniserState.replacementChar);
                    t.transition(ScriptDataEscaped);
                case QQShare.QQ_SHARE_TITLE_MAX_LENGTH /*45*/:
                    t.emit(c);
                case QQShare.QQ_SHARE_SUMMARY_MAX_LENGTH /*60*/:
                    t.transition(ScriptDataEscapedLessthanSign);
                case Header.LONG_14 /*62*/:
                    t.emit(c);
                    t.transition(ScriptData);
                default:
                    t.emit(c);
                    t.transition(ScriptDataEscaped);
            }
        }
    },
    ScriptDataEscapedLessthanSign {
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                t.createTempBuffer();
                t.dataBuffer.append(Character.toLowerCase(r.current()));
                t.emit(SimpleComparison.LESS_THAN_OPERATION + r.current());
                t.advanceTransition(ScriptDataDoubleEscapeStart);
            } else if (r.matches((char) Char.SLASH)) {
                t.createTempBuffer();
                t.advanceTransition(ScriptDataEscapedEndTagOpen);
            } else {
                t.emit('<');
                t.transition(ScriptDataEscaped);
            }
        }
    },
    ScriptDataEscapedEndTagOpen {
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                t.createTagPending(false);
                t.tagPending.appendTagName(Character.toLowerCase(r.current()));
                t.dataBuffer.append(r.current());
                t.advanceTransition(ScriptDataEscapedEndTagName);
                return;
            }
            t.emit("</");
            t.transition(ScriptDataEscaped);
        }
    },
    ScriptDataEscapedEndTagName {
        void read(Tokeniser t, CharacterReader r) {
            TokeniserState.handleDataEndTag(t, r, ScriptDataEscaped);
        }
    },
    ScriptDataDoubleEscapeStart {
        void read(Tokeniser t, CharacterReader r) {
            TokeniserState.handleDataDoubleEscapeTag(t, r, ScriptDataDoubleEscaped, ScriptDataEscaped);
        }
    },
    ScriptDataDoubleEscaped {
        void read(Tokeniser t, CharacterReader r) {
            char c = r.current();
            switch (c) {
                case dx.a /*0*/:
                    t.error((TokeniserState) this);
                    r.advance();
                    t.emit((char) TokeniserState.replacementChar);
                case QQShare.QQ_SHARE_TITLE_MAX_LENGTH /*45*/:
                    t.emit(c);
                    t.advanceTransition(ScriptDataDoubleEscapedDash);
                case QQShare.QQ_SHARE_SUMMARY_MAX_LENGTH /*60*/:
                    t.emit(c);
                    t.advanceTransition(ScriptDataDoubleEscapedLessthanSign);
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.transition(Data);
                default:
                    t.emit(r.consumeToAny(Char.HYPHEN, '<', TokeniserState.nullChar));
            }
        }
    },
    ScriptDataDoubleEscapedDash {
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case dx.a /*0*/:
                    t.error((TokeniserState) this);
                    t.emit((char) TokeniserState.replacementChar);
                    t.transition(ScriptDataDoubleEscaped);
                case QQShare.QQ_SHARE_TITLE_MAX_LENGTH /*45*/:
                    t.emit(c);
                    t.transition(ScriptDataDoubleEscapedDashDash);
                case QQShare.QQ_SHARE_SUMMARY_MAX_LENGTH /*60*/:
                    t.emit(c);
                    t.transition(ScriptDataDoubleEscapedLessthanSign);
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.transition(Data);
                default:
                    t.emit(c);
                    t.transition(ScriptDataDoubleEscaped);
            }
        }
    },
    ScriptDataDoubleEscapedDashDash {
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case dx.a /*0*/:
                    t.error((TokeniserState) this);
                    t.emit((char) TokeniserState.replacementChar);
                    t.transition(ScriptDataDoubleEscaped);
                case QQShare.QQ_SHARE_TITLE_MAX_LENGTH /*45*/:
                    t.emit(c);
                case QQShare.QQ_SHARE_SUMMARY_MAX_LENGTH /*60*/:
                    t.emit(c);
                    t.transition(ScriptDataDoubleEscapedLessthanSign);
                case Header.LONG_14 /*62*/:
                    t.emit(c);
                    t.transition(ScriptData);
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.transition(Data);
                default:
                    t.emit(c);
                    t.transition(ScriptDataDoubleEscaped);
            }
        }
    },
    ScriptDataDoubleEscapedLessthanSign {
        void read(Tokeniser t, CharacterReader r) {
            if (r.matches((char) Char.SLASH)) {
                t.emit((char) Char.SLASH);
                t.createTempBuffer();
                t.advanceTransition(ScriptDataDoubleEscapeEnd);
                return;
            }
            t.transition(ScriptDataDoubleEscaped);
        }
    },
    ScriptDataDoubleEscapeEnd {
        void read(Tokeniser t, CharacterReader r) {
            TokeniserState.handleDataDoubleEscapeTag(t, r, ScriptDataEscaped, ScriptDataDoubleEscaped);
        }
    },
    BeforeAttributeName {
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case dx.a /*0*/:
                    t.error((TokeniserState) this);
                    t.tagPending.newAttribute();
                    r.unconsume();
                    t.transition(AttributeName);
                case h.h /*9*/:
                case h.i /*10*/:
                case R.styleable.StickyListHeadersListView_android_scrollingCache /*12*/:
                case R.styleable.StickyListHeadersListView_android_transcriptMode /*13*/:
                case TransportMediator.FLAG_KEY_MEDIA_STOP /*32*/:
                case HeaderMapDB.HASHER_BYTE_ARRAY /*34*/:
                case HeaderMapDB.COMPARATOR_BYTE_ARRAY /*39*/:
                case QQShare.QQ_SHARE_SUMMARY_MAX_LENGTH /*60*/:
                case Header.LONG_13 /*61*/:
                    t.error((TokeniserState) this);
                    t.tagPending.newAttribute();
                    t.tagPending.appendAttributeName(c);
                    t.transition(AttributeName);
                case HeaderMapDB.SERIALIZER_COMPRESSION_WRAPPER /*47*/:
                    t.transition(SelfClosingStartTag);
                case Header.LONG_14 /*62*/:
                    t.emitTagPending();
                    t.transition(Data);
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.transition(Data);
                default:
                    t.tagPending.newAttribute();
                    r.unconsume();
                    t.transition(AttributeName);
            }
        }
    },
    AttributeName {
        void read(Tokeniser t, CharacterReader r) {
            t.tagPending.appendAttributeName(r.consumeToAnySorted(TokeniserState.attributeNameCharsSorted).toLowerCase());
            char c = r.consume();
            switch (c) {
                case dx.a /*0*/:
                    t.error((TokeniserState) this);
                    t.tagPending.appendAttributeName((char) TokeniserState.replacementChar);
                case h.h /*9*/:
                case h.i /*10*/:
                case R.styleable.StickyListHeadersListView_android_scrollingCache /*12*/:
                case R.styleable.StickyListHeadersListView_android_transcriptMode /*13*/:
                case TransportMediator.FLAG_KEY_MEDIA_STOP /*32*/:
                    t.transition(AfterAttributeName);
                case HeaderMapDB.HASHER_BYTE_ARRAY /*34*/:
                case HeaderMapDB.COMPARATOR_BYTE_ARRAY /*39*/:
                case QQShare.QQ_SHARE_SUMMARY_MAX_LENGTH /*60*/:
                    t.error((TokeniserState) this);
                    t.tagPending.appendAttributeName(c);
                case HeaderMapDB.SERIALIZER_COMPRESSION_WRAPPER /*47*/:
                    t.transition(SelfClosingStartTag);
                case Header.LONG_13 /*61*/:
                    t.transition(BeforeAttributeValue);
                case Header.LONG_14 /*62*/:
                    t.emitTagPending();
                    t.transition(Data);
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.transition(Data);
                default:
            }
        }
    },
    AfterAttributeName {
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case dx.a /*0*/:
                    t.error((TokeniserState) this);
                    t.tagPending.appendAttributeName((char) TokeniserState.replacementChar);
                    t.transition(AttributeName);
                case h.h /*9*/:
                case h.i /*10*/:
                case R.styleable.StickyListHeadersListView_android_scrollingCache /*12*/:
                case R.styleable.StickyListHeadersListView_android_transcriptMode /*13*/:
                case TransportMediator.FLAG_KEY_MEDIA_STOP /*32*/:
                case HeaderMapDB.HASHER_BYTE_ARRAY /*34*/:
                case HeaderMapDB.COMPARATOR_BYTE_ARRAY /*39*/:
                case QQShare.QQ_SHARE_SUMMARY_MAX_LENGTH /*60*/:
                    t.error((TokeniserState) this);
                    t.tagPending.newAttribute();
                    t.tagPending.appendAttributeName(c);
                    t.transition(AttributeName);
                case HeaderMapDB.SERIALIZER_COMPRESSION_WRAPPER /*47*/:
                    t.transition(SelfClosingStartTag);
                case Header.LONG_13 /*61*/:
                    t.transition(BeforeAttributeValue);
                case Header.LONG_14 /*62*/:
                    t.emitTagPending();
                    t.transition(Data);
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.transition(Data);
                default:
                    t.tagPending.newAttribute();
                    r.unconsume();
                    t.transition(AttributeName);
            }
        }
    },
    BeforeAttributeValue {
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case dx.a /*0*/:
                    t.error((TokeniserState) this);
                    t.tagPending.appendAttributeValue((char) TokeniserState.replacementChar);
                    t.transition(AttributeValue_unquoted);
                case h.h /*9*/:
                case h.i /*10*/:
                case R.styleable.StickyListHeadersListView_android_scrollingCache /*12*/:
                case R.styleable.StickyListHeadersListView_android_transcriptMode /*13*/:
                case TransportMediator.FLAG_KEY_MEDIA_STOP /*32*/:
                case HeaderMapDB.HASHER_BYTE_ARRAY /*34*/:
                    t.transition(AttributeValue_doubleQuoted);
                case HeaderMapDB.HASHER_DOUBLE_ARRAY /*38*/:
                    r.unconsume();
                    t.transition(AttributeValue_unquoted);
                case HeaderMapDB.COMPARATOR_BYTE_ARRAY /*39*/:
                    t.transition(AttributeValue_singleQuoted);
                case QQShare.QQ_SHARE_SUMMARY_MAX_LENGTH /*60*/:
                case Header.LONG_13 /*61*/:
                case Header.FLOAT_M1 /*96*/:
                    t.error((TokeniserState) this);
                    t.tagPending.appendAttributeValue(c);
                    t.transition(AttributeValue_unquoted);
                case Header.LONG_14 /*62*/:
                    t.error((TokeniserState) this);
                    t.emitTagPending();
                    t.transition(Data);
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.emitTagPending();
                    t.transition(Data);
                default:
                    r.unconsume();
                    t.transition(AttributeValue_unquoted);
            }
        }
    },
    AttributeValue_doubleQuoted {
        void read(Tokeniser t, CharacterReader r) {
            String value = r.consumeToAnySorted(TokeniserState.attributeDoubleValueCharsSorted);
            if (value.length() > 0) {
                t.tagPending.appendAttributeValue(value);
            } else {
                t.tagPending.setEmptyAttributeValue();
            }
            switch (r.consume()) {
                case dx.a /*0*/:
                    t.error((TokeniserState) this);
                    t.tagPending.appendAttributeValue((char) TokeniserState.replacementChar);
                case HeaderMapDB.HASHER_BYTE_ARRAY /*34*/:
                    t.transition(AfterAttributeValue_quoted);
                case HeaderMapDB.HASHER_DOUBLE_ARRAY /*38*/:
                    char[] ref = t.consumeCharacterReference(Character.valueOf('\"'), true);
                    if (ref != null) {
                        t.tagPending.appendAttributeValue(ref);
                    } else {
                        t.tagPending.appendAttributeValue('&');
                    }
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.transition(Data);
                default:
            }
        }
    },
    AttributeValue_singleQuoted {
        void read(Tokeniser t, CharacterReader r) {
            String value = r.consumeToAnySorted(TokeniserState.attributeSingleValueCharsSorted);
            if (value.length() > 0) {
                t.tagPending.appendAttributeValue(value);
            } else {
                t.tagPending.setEmptyAttributeValue();
            }
            switch (r.consume()) {
                case dx.a /*0*/:
                    t.error((TokeniserState) this);
                    t.tagPending.appendAttributeValue((char) TokeniserState.replacementChar);
                case HeaderMapDB.HASHER_DOUBLE_ARRAY /*38*/:
                    char[] ref = t.consumeCharacterReference(Character.valueOf('\''), true);
                    if (ref != null) {
                        t.tagPending.appendAttributeValue(ref);
                    } else {
                        t.tagPending.appendAttributeValue('&');
                    }
                case HeaderMapDB.COMPARATOR_BYTE_ARRAY /*39*/:
                    t.transition(AfterAttributeValue_quoted);
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.transition(Data);
                default:
            }
        }
    },
    AttributeValue_unquoted {
        void read(Tokeniser t, CharacterReader r) {
            String value = r.consumeToAny('\t', '\n', Char.CARRIAGE_RETURN, '\f', Char.SPACE, '&', '>', TokeniserState.nullChar, '\"', '\'', '<', '=', '`');
            if (value.length() > 0) {
                t.tagPending.appendAttributeValue(value);
            }
            char c = r.consume();
            switch (c) {
                case dx.a /*0*/:
                    t.error((TokeniserState) this);
                    t.tagPending.appendAttributeValue((char) TokeniserState.replacementChar);
                case h.h /*9*/:
                case h.i /*10*/:
                case R.styleable.StickyListHeadersListView_android_scrollingCache /*12*/:
                case R.styleable.StickyListHeadersListView_android_transcriptMode /*13*/:
                case TransportMediator.FLAG_KEY_MEDIA_STOP /*32*/:
                    t.transition(BeforeAttributeName);
                case HeaderMapDB.HASHER_BYTE_ARRAY /*34*/:
                case HeaderMapDB.COMPARATOR_BYTE_ARRAY /*39*/:
                case QQShare.QQ_SHARE_SUMMARY_MAX_LENGTH /*60*/:
                case Header.LONG_13 /*61*/:
                case Header.FLOAT_M1 /*96*/:
                    t.error((TokeniserState) this);
                    t.tagPending.appendAttributeValue(c);
                case HeaderMapDB.HASHER_DOUBLE_ARRAY /*38*/:
                    char[] ref = t.consumeCharacterReference(Character.valueOf('>'), true);
                    if (ref != null) {
                        t.tagPending.appendAttributeValue(ref);
                    } else {
                        t.tagPending.appendAttributeValue('&');
                    }
                case Header.LONG_14 /*62*/:
                    t.emitTagPending();
                    t.transition(Data);
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.transition(Data);
                default:
            }
        }
    },
    AfterAttributeValue_quoted {
        void read(Tokeniser t, CharacterReader r) {
            switch (r.consume()) {
                case h.h /*9*/:
                case h.i /*10*/:
                case R.styleable.StickyListHeadersListView_android_scrollingCache /*12*/:
                case R.styleable.StickyListHeadersListView_android_transcriptMode /*13*/:
                case TransportMediator.FLAG_KEY_MEDIA_STOP /*32*/:
                    t.transition(BeforeAttributeName);
                case HeaderMapDB.SERIALIZER_COMPRESSION_WRAPPER /*47*/:
                    t.transition(SelfClosingStartTag);
                case Header.LONG_14 /*62*/:
                    t.emitTagPending();
                    t.transition(Data);
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.transition(Data);
                default:
                    t.error((TokeniserState) this);
                    r.unconsume();
                    t.transition(BeforeAttributeName);
            }
        }
    },
    SelfClosingStartTag {
        void read(Tokeniser t, CharacterReader r) {
            switch (r.consume()) {
                case Header.LONG_14 /*62*/:
                    t.tagPending.selfClosing = true;
                    t.emitTagPending();
                    t.transition(Data);
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.transition(Data);
                default:
                    t.error((TokeniserState) this);
                    t.transition(BeforeAttributeName);
            }
        }
    },
    BogusComment {
        void read(Tokeniser t, CharacterReader r) {
            r.unconsume();
            Token comment = new Comment();
            comment.bogus = true;
            comment.data.append(r.consumeTo('>'));
            t.emit(comment);
            t.advanceTransition(Data);
        }
    },
    MarkupDeclarationOpen {
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchConsume("--")) {
                t.createCommentPending();
                t.transition(CommentStart);
            } else if (r.matchConsumeIgnoreCase("DOCTYPE")) {
                t.transition(Doctype);
            } else if (r.matchConsume("[CDATA[")) {
                t.transition(CdataSection);
            } else {
                t.error((TokeniserState) this);
                t.advanceTransition(BogusComment);
            }
        }
    },
    CommentStart {
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case dx.a /*0*/:
                    t.error((TokeniserState) this);
                    t.commentPending.data.append(TokeniserState.replacementChar);
                    t.transition(Comment);
                case QQShare.QQ_SHARE_TITLE_MAX_LENGTH /*45*/:
                    t.transition(CommentStartDash);
                case Header.LONG_14 /*62*/:
                    t.error((TokeniserState) this);
                    t.emitCommentPending();
                    t.transition(Data);
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.emitCommentPending();
                    t.transition(Data);
                default:
                    t.commentPending.data.append(c);
                    t.transition(Comment);
            }
        }
    },
    CommentStartDash {
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case dx.a /*0*/:
                    t.error((TokeniserState) this);
                    t.commentPending.data.append(TokeniserState.replacementChar);
                    t.transition(Comment);
                case QQShare.QQ_SHARE_TITLE_MAX_LENGTH /*45*/:
                    t.transition(CommentStartDash);
                case Header.LONG_14 /*62*/:
                    t.error((TokeniserState) this);
                    t.emitCommentPending();
                    t.transition(Data);
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.emitCommentPending();
                    t.transition(Data);
                default:
                    t.commentPending.data.append(c);
                    t.transition(Comment);
            }
        }
    },
    Comment {
        void read(Tokeniser t, CharacterReader r) {
            switch (r.current()) {
                case dx.a /*0*/:
                    t.error((TokeniserState) this);
                    r.advance();
                    t.commentPending.data.append(TokeniserState.replacementChar);
                case QQShare.QQ_SHARE_TITLE_MAX_LENGTH /*45*/:
                    t.advanceTransition(CommentEndDash);
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.emitCommentPending();
                    t.transition(Data);
                default:
                    t.commentPending.data.append(r.consumeToAny(Char.HYPHEN, TokeniserState.nullChar));
            }
        }
    },
    CommentEndDash {
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case dx.a /*0*/:
                    t.error((TokeniserState) this);
                    t.commentPending.data.append(Char.HYPHEN).append(TokeniserState.replacementChar);
                    t.transition(Comment);
                case QQShare.QQ_SHARE_TITLE_MAX_LENGTH /*45*/:
                    t.transition(CommentEnd);
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.emitCommentPending();
                    t.transition(Data);
                default:
                    t.commentPending.data.append(Char.HYPHEN).append(c);
                    t.transition(Comment);
            }
        }
    },
    CommentEnd {
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case dx.a /*0*/:
                    t.error((TokeniserState) this);
                    t.commentPending.data.append("--").append(TokeniserState.replacementChar);
                    t.transition(Comment);
                case HeaderMapDB.HASHER_BASIC /*33*/:
                    t.error((TokeniserState) this);
                    t.transition(CommentEndBang);
                case QQShare.QQ_SHARE_TITLE_MAX_LENGTH /*45*/:
                    t.error((TokeniserState) this);
                    t.commentPending.data.append(Char.HYPHEN);
                case Header.LONG_14 /*62*/:
                    t.emitCommentPending();
                    t.transition(Data);
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.emitCommentPending();
                    t.transition(Data);
                default:
                    t.error((TokeniserState) this);
                    t.commentPending.data.append("--").append(c);
                    t.transition(Comment);
            }
        }
    },
    CommentEndBang {
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case dx.a /*0*/:
                    t.error((TokeniserState) this);
                    t.commentPending.data.append("--!").append(TokeniserState.replacementChar);
                    t.transition(Comment);
                case QQShare.QQ_SHARE_TITLE_MAX_LENGTH /*45*/:
                    t.commentPending.data.append("--!");
                    t.transition(CommentEndDash);
                case Header.LONG_14 /*62*/:
                    t.emitCommentPending();
                    t.transition(Data);
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.emitCommentPending();
                    t.transition(Data);
                default:
                    t.commentPending.data.append("--!").append(c);
                    t.transition(Comment);
            }
        }
    },
    Doctype {
        void read(Tokeniser t, CharacterReader r) {
            switch (r.consume()) {
                case h.h /*9*/:
                case h.i /*10*/:
                case R.styleable.StickyListHeadersListView_android_scrollingCache /*12*/:
                case R.styleable.StickyListHeadersListView_android_transcriptMode /*13*/:
                case TransportMediator.FLAG_KEY_MEDIA_STOP /*32*/:
                    t.transition(BeforeDoctypeName);
                    return;
                case Header.LONG_14 /*62*/:
                    break;
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    break;
                default:
                    t.error((TokeniserState) this);
                    t.transition(BeforeDoctypeName);
                    return;
            }
            t.error((TokeniserState) this);
            t.createDoctypePending();
            t.doctypePending.forceQuirks = true;
            t.emitDoctypePending();
            t.transition(Data);
        }
    },
    BeforeDoctypeName {
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                t.createDoctypePending();
                t.transition(DoctypeName);
                return;
            }
            char c = r.consume();
            switch (c) {
                case dx.a /*0*/:
                    t.error((TokeniserState) this);
                    t.createDoctypePending();
                    t.doctypePending.name.append(TokeniserState.replacementChar);
                    t.transition(DoctypeName);
                case h.h /*9*/:
                case h.i /*10*/:
                case R.styleable.StickyListHeadersListView_android_scrollingCache /*12*/:
                case R.styleable.StickyListHeadersListView_android_transcriptMode /*13*/:
                case TransportMediator.FLAG_KEY_MEDIA_STOP /*32*/:
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.createDoctypePending();
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                default:
                    t.createDoctypePending();
                    t.doctypePending.name.append(c);
                    t.transition(DoctypeName);
            }
        }
    },
    DoctypeName {
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                t.doctypePending.name.append(r.consumeLetterSequence().toLowerCase());
                return;
            }
            char c = r.consume();
            switch (c) {
                case dx.a /*0*/:
                    t.error((TokeniserState) this);
                    t.doctypePending.name.append(TokeniserState.replacementChar);
                case h.h /*9*/:
                case h.i /*10*/:
                case R.styleable.StickyListHeadersListView_android_scrollingCache /*12*/:
                case R.styleable.StickyListHeadersListView_android_transcriptMode /*13*/:
                case TransportMediator.FLAG_KEY_MEDIA_STOP /*32*/:
                    t.transition(AfterDoctypeName);
                case Header.LONG_14 /*62*/:
                    t.emitDoctypePending();
                    t.transition(Data);
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                default:
                    t.doctypePending.name.append(c);
            }
        }
    },
    AfterDoctypeName {
        void read(Tokeniser t, CharacterReader r) {
            if (r.isEmpty()) {
                t.eofError(this);
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
                t.transition(Data);
            } else if (r.matchesAny('\t', '\n', Char.CARRIAGE_RETURN, '\f', Char.SPACE)) {
                r.advance();
            } else if (r.matches('>')) {
                t.emitDoctypePending();
                t.advanceTransition(Data);
            } else if (r.matchConsumeIgnoreCase("PUBLIC")) {
                t.transition(AfterDoctypePublicKeyword);
            } else if (r.matchConsumeIgnoreCase("SYSTEM")) {
                t.transition(AfterDoctypeSystemKeyword);
            } else {
                t.error((TokeniserState) this);
                t.doctypePending.forceQuirks = true;
                t.advanceTransition(BogusDoctype);
            }
        }
    },
    AfterDoctypePublicKeyword {
        void read(Tokeniser t, CharacterReader r) {
            switch (r.consume()) {
                case h.h /*9*/:
                case h.i /*10*/:
                case R.styleable.StickyListHeadersListView_android_scrollingCache /*12*/:
                case R.styleable.StickyListHeadersListView_android_transcriptMode /*13*/:
                case TransportMediator.FLAG_KEY_MEDIA_STOP /*32*/:
                    t.transition(BeforeDoctypePublicIdentifier);
                case HeaderMapDB.HASHER_BYTE_ARRAY /*34*/:
                    t.error((TokeniserState) this);
                    t.transition(DoctypePublicIdentifier_doubleQuoted);
                case HeaderMapDB.COMPARATOR_BYTE_ARRAY /*39*/:
                    t.error((TokeniserState) this);
                    t.transition(DoctypePublicIdentifier_singleQuoted);
                case Header.LONG_14 /*62*/:
                    t.error((TokeniserState) this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                default:
                    t.error((TokeniserState) this);
                    t.doctypePending.forceQuirks = true;
                    t.transition(BogusDoctype);
            }
        }
    },
    BeforeDoctypePublicIdentifier {
        void read(Tokeniser t, CharacterReader r) {
            switch (r.consume()) {
                case h.h /*9*/:
                case h.i /*10*/:
                case R.styleable.StickyListHeadersListView_android_scrollingCache /*12*/:
                case R.styleable.StickyListHeadersListView_android_transcriptMode /*13*/:
                case TransportMediator.FLAG_KEY_MEDIA_STOP /*32*/:
                case HeaderMapDB.HASHER_BYTE_ARRAY /*34*/:
                    t.transition(DoctypePublicIdentifier_doubleQuoted);
                case HeaderMapDB.COMPARATOR_BYTE_ARRAY /*39*/:
                    t.transition(DoctypePublicIdentifier_singleQuoted);
                case Header.LONG_14 /*62*/:
                    t.error((TokeniserState) this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                default:
                    t.error((TokeniserState) this);
                    t.doctypePending.forceQuirks = true;
                    t.transition(BogusDoctype);
            }
        }
    },
    DoctypePublicIdentifier_doubleQuoted {
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case dx.a /*0*/:
                    t.error((TokeniserState) this);
                    t.doctypePending.publicIdentifier.append(TokeniserState.replacementChar);
                case HeaderMapDB.HASHER_BYTE_ARRAY /*34*/:
                    t.transition(AfterDoctypePublicIdentifier);
                case Header.LONG_14 /*62*/:
                    t.error((TokeniserState) this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                default:
                    t.doctypePending.publicIdentifier.append(c);
            }
        }
    },
    DoctypePublicIdentifier_singleQuoted {
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case dx.a /*0*/:
                    t.error((TokeniserState) this);
                    t.doctypePending.publicIdentifier.append(TokeniserState.replacementChar);
                case HeaderMapDB.COMPARATOR_BYTE_ARRAY /*39*/:
                    t.transition(AfterDoctypePublicIdentifier);
                case Header.LONG_14 /*62*/:
                    t.error((TokeniserState) this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                default:
                    t.doctypePending.publicIdentifier.append(c);
            }
        }
    },
    AfterDoctypePublicIdentifier {
        void read(Tokeniser t, CharacterReader r) {
            switch (r.consume()) {
                case h.h /*9*/:
                case h.i /*10*/:
                case R.styleable.StickyListHeadersListView_android_scrollingCache /*12*/:
                case R.styleable.StickyListHeadersListView_android_transcriptMode /*13*/:
                case TransportMediator.FLAG_KEY_MEDIA_STOP /*32*/:
                    t.transition(BetweenDoctypePublicAndSystemIdentifiers);
                case HeaderMapDB.HASHER_BYTE_ARRAY /*34*/:
                    t.error((TokeniserState) this);
                    t.transition(DoctypeSystemIdentifier_doubleQuoted);
                case HeaderMapDB.COMPARATOR_BYTE_ARRAY /*39*/:
                    t.error((TokeniserState) this);
                    t.transition(DoctypeSystemIdentifier_singleQuoted);
                case Header.LONG_14 /*62*/:
                    t.emitDoctypePending();
                    t.transition(Data);
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                default:
                    t.error((TokeniserState) this);
                    t.doctypePending.forceQuirks = true;
                    t.transition(BogusDoctype);
            }
        }
    },
    BetweenDoctypePublicAndSystemIdentifiers {
        void read(Tokeniser t, CharacterReader r) {
            switch (r.consume()) {
                case h.h /*9*/:
                case h.i /*10*/:
                case R.styleable.StickyListHeadersListView_android_scrollingCache /*12*/:
                case R.styleable.StickyListHeadersListView_android_transcriptMode /*13*/:
                case TransportMediator.FLAG_KEY_MEDIA_STOP /*32*/:
                case HeaderMapDB.HASHER_BYTE_ARRAY /*34*/:
                    t.error((TokeniserState) this);
                    t.transition(DoctypeSystemIdentifier_doubleQuoted);
                case HeaderMapDB.COMPARATOR_BYTE_ARRAY /*39*/:
                    t.error((TokeniserState) this);
                    t.transition(DoctypeSystemIdentifier_singleQuoted);
                case Header.LONG_14 /*62*/:
                    t.emitDoctypePending();
                    t.transition(Data);
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                default:
                    t.error((TokeniserState) this);
                    t.doctypePending.forceQuirks = true;
                    t.transition(BogusDoctype);
            }
        }
    },
    AfterDoctypeSystemKeyword {
        void read(Tokeniser t, CharacterReader r) {
            switch (r.consume()) {
                case h.h /*9*/:
                case h.i /*10*/:
                case R.styleable.StickyListHeadersListView_android_scrollingCache /*12*/:
                case R.styleable.StickyListHeadersListView_android_transcriptMode /*13*/:
                case TransportMediator.FLAG_KEY_MEDIA_STOP /*32*/:
                    t.transition(BeforeDoctypeSystemIdentifier);
                case HeaderMapDB.HASHER_BYTE_ARRAY /*34*/:
                    t.error((TokeniserState) this);
                    t.transition(DoctypeSystemIdentifier_doubleQuoted);
                case HeaderMapDB.COMPARATOR_BYTE_ARRAY /*39*/:
                    t.error((TokeniserState) this);
                    t.transition(DoctypeSystemIdentifier_singleQuoted);
                case Header.LONG_14 /*62*/:
                    t.error((TokeniserState) this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                default:
                    t.error((TokeniserState) this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
            }
        }
    },
    BeforeDoctypeSystemIdentifier {
        void read(Tokeniser t, CharacterReader r) {
            switch (r.consume()) {
                case h.h /*9*/:
                case h.i /*10*/:
                case R.styleable.StickyListHeadersListView_android_scrollingCache /*12*/:
                case R.styleable.StickyListHeadersListView_android_transcriptMode /*13*/:
                case TransportMediator.FLAG_KEY_MEDIA_STOP /*32*/:
                case HeaderMapDB.HASHER_BYTE_ARRAY /*34*/:
                    t.transition(DoctypeSystemIdentifier_doubleQuoted);
                case HeaderMapDB.COMPARATOR_BYTE_ARRAY /*39*/:
                    t.transition(DoctypeSystemIdentifier_singleQuoted);
                case Header.LONG_14 /*62*/:
                    t.error((TokeniserState) this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                default:
                    t.error((TokeniserState) this);
                    t.doctypePending.forceQuirks = true;
                    t.transition(BogusDoctype);
            }
        }
    },
    DoctypeSystemIdentifier_doubleQuoted {
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case dx.a /*0*/:
                    t.error((TokeniserState) this);
                    t.doctypePending.systemIdentifier.append(TokeniserState.replacementChar);
                case HeaderMapDB.HASHER_BYTE_ARRAY /*34*/:
                    t.transition(AfterDoctypeSystemIdentifier);
                case Header.LONG_14 /*62*/:
                    t.error((TokeniserState) this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                default:
                    t.doctypePending.systemIdentifier.append(c);
            }
        }
    },
    DoctypeSystemIdentifier_singleQuoted {
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case dx.a /*0*/:
                    t.error((TokeniserState) this);
                    t.doctypePending.systemIdentifier.append(TokeniserState.replacementChar);
                case HeaderMapDB.COMPARATOR_BYTE_ARRAY /*39*/:
                    t.transition(AfterDoctypeSystemIdentifier);
                case Header.LONG_14 /*62*/:
                    t.error((TokeniserState) this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                default:
                    t.doctypePending.systemIdentifier.append(c);
            }
        }
    },
    AfterDoctypeSystemIdentifier {
        void read(Tokeniser t, CharacterReader r) {
            switch (r.consume()) {
                case h.h /*9*/:
                case h.i /*10*/:
                case R.styleable.StickyListHeadersListView_android_scrollingCache /*12*/:
                case R.styleable.StickyListHeadersListView_android_transcriptMode /*13*/:
                case TransportMediator.FLAG_KEY_MEDIA_STOP /*32*/:
                case Header.LONG_14 /*62*/:
                    t.emitDoctypePending();
                    t.transition(Data);
                case SupportMenu.USER_MASK /*65535*/:
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                default:
                    t.error((TokeniserState) this);
                    t.transition(BogusDoctype);
            }
        }
    },
    BogusDoctype {
        void read(Tokeniser t, CharacterReader r) {
            switch (r.consume()) {
                case Header.LONG_14 /*62*/:
                    t.emitDoctypePending();
                    t.transition(Data);
                case SupportMenu.USER_MASK /*65535*/:
                    t.emitDoctypePending();
                    t.transition(Data);
                default:
            }
        }
    },
    CdataSection {
        void read(Tokeniser t, CharacterReader r) {
            t.emit(r.consumeTo("]]>"));
            r.matchConsume("]]>");
            t.transition(Data);
        }
    };
    
    private static final char[] attributeDoubleValueCharsSorted;
    private static final char[] attributeNameCharsSorted;
    private static final char[] attributeSingleValueCharsSorted;
    private static final char eof = '\uffff';
    static final char nullChar = '\u0000';
    private static final char replacementChar = '\ufffd';
    private static final String replacementStr;

    abstract void read(Tokeniser tokeniser, CharacterReader characterReader);

    static {
        attributeSingleValueCharsSorted = new char[]{'\'', '&', nullChar};
        attributeDoubleValueCharsSorted = new char[]{'\"', '&', nullChar};
        attributeNameCharsSorted = new char[]{'\t', '\n', Char.CARRIAGE_RETURN, '\f', Char.SPACE, Char.SLASH, '=', '>', nullChar, '\"', '\'', '<'};
        replacementStr = String.valueOf(replacementChar);
        Arrays.sort(attributeSingleValueCharsSorted);
        Arrays.sort(attributeDoubleValueCharsSorted);
        Arrays.sort(attributeNameCharsSorted);
    }

    private static void handleDataEndTag(Tokeniser t, CharacterReader r, TokeniserState elseTransition) {
        if (r.matchesLetter()) {
            String name = r.consumeLetterSequence();
            t.tagPending.appendTagName(name.toLowerCase());
            t.dataBuffer.append(name);
            return;
        }
        boolean needsExitTransition = false;
        if (t.isAppropriateEndTagToken() && !r.isEmpty()) {
            char c = r.consume();
            switch (c) {
                case h.h /*9*/:
                case h.i /*10*/:
                case R.styleable.StickyListHeadersListView_android_scrollingCache /*12*/:
                case R.styleable.StickyListHeadersListView_android_transcriptMode /*13*/:
                case TransportMediator.FLAG_KEY_MEDIA_STOP /*32*/:
                    t.transition(BeforeAttributeName);
                    break;
                case HeaderMapDB.SERIALIZER_COMPRESSION_WRAPPER /*47*/:
                    t.transition(SelfClosingStartTag);
                    break;
                case Header.LONG_14 /*62*/:
                    t.emitTagPending();
                    t.transition(Data);
                    break;
                default:
                    t.dataBuffer.append(c);
                    needsExitTransition = true;
                    break;
            }
        }
        needsExitTransition = true;
        if (needsExitTransition) {
            t.emit("</" + t.dataBuffer.toString());
            t.transition(elseTransition);
        }
    }

    private static void handleDataDoubleEscapeTag(Tokeniser t, CharacterReader r, TokeniserState primary, TokeniserState fallback) {
        if (r.matchesLetter()) {
            String name = r.consumeLetterSequence();
            t.dataBuffer.append(name.toLowerCase());
            t.emit(name);
            return;
        }
        char c = r.consume();
        switch (c) {
            case h.h /*9*/:
            case h.i /*10*/:
            case R.styleable.StickyListHeadersListView_android_scrollingCache /*12*/:
            case R.styleable.StickyListHeadersListView_android_transcriptMode /*13*/:
            case TransportMediator.FLAG_KEY_MEDIA_STOP /*32*/:
            case HeaderMapDB.SERIALIZER_COMPRESSION_WRAPPER /*47*/:
            case Header.LONG_14 /*62*/:
                if (t.dataBuffer.toString().equals("script")) {
                    t.transition(primary);
                } else {
                    t.transition(fallback);
                }
                t.emit(c);
            default:
                r.unconsume();
                t.transition(fallback);
        }
    }
}
