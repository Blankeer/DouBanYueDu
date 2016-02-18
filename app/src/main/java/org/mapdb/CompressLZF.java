package org.mapdb;

import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.io.DataInput;
import java.io.IOException;
import java.nio.ByteBuffer;

public final class CompressLZF {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final int HASH_SIZE = 16384;
    private static final int MAX_LITERAL = 32;
    private static final int MAX_OFF = 8192;
    private static final int MAX_REF = 264;
    private int[] cachedHashTable;

    static {
        $assertionsDisabled = !CompressLZF.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    private static int first(byte[] in, int inPos) {
        return (in[inPos] << 8) | (in[inPos + 1] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT);
    }

    private static int next(int v, byte[] in, int inPos) {
        return (v << 8) | (in[inPos + 2] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT);
    }

    private static int hash(int h) {
        return ((h * 2777) >> 9) & 16383;
    }

    public int compress(byte[] in, int inLen, byte[] out, int outPos) {
        if (this.cachedHashTable == null) {
            this.cachedHashTable = new int[HASH_SIZE];
        }
        int[] hashTab = this.cachedHashTable;
        int literals = 0;
        outPos++;
        int future = first(in, 0);
        int inPos = 0;
        int outPos2 = outPos;
        while (inPos < inLen - 4) {
            int inPos2;
            byte p2 = in[inPos + 2];
            future = (future << 8) + (p2 & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT);
            int off = hash(future);
            int ref = hashTab[off];
            hashTab[off] = inPos;
            if (ref < inPos && ref > 0) {
                off = (inPos - ref) - 1;
                if (off < MAX_OFF && in[ref + 2] == p2 && in[ref + 1] == ((byte) (future >> 8)) && in[ref] == ((byte) (future >> 16))) {
                    int maxLen = (inLen - inPos) - 2;
                    if (maxLen > MAX_REF) {
                        maxLen = MAX_REF;
                    }
                    if (literals == 0) {
                        outPos = outPos2 - 1;
                    } else {
                        out[(outPos2 - literals) - 1] = (byte) (literals - 1);
                        literals = 0;
                        outPos = outPos2;
                    }
                    int len = 3;
                    while (len < maxLen && in[ref + len] == in[inPos + len]) {
                        len++;
                    }
                    len -= 2;
                    if (len < 7) {
                        outPos2 = outPos + 1;
                        out[outPos] = (byte) ((off >> 8) + (len << 5));
                        outPos = outPos2;
                    } else {
                        outPos2 = outPos + 1;
                        out[outPos] = (byte) ((off >> 8) + 224);
                        outPos = outPos2 + 1;
                        out[outPos2] = (byte) (len - 7);
                    }
                    outPos2 = outPos + 1;
                    out[outPos] = (byte) off;
                    outPos = outPos2 + 1;
                    inPos2 = inPos + len;
                    future = next(first(in, inPos2), in, inPos2);
                    inPos = inPos2 + 1;
                    hashTab[hash(future)] = inPos2;
                    future = next(future, in, inPos);
                    inPos2 = inPos + 1;
                    hashTab[hash(future)] = inPos;
                    inPos = inPos2;
                    outPos2 = outPos;
                }
            }
            outPos = outPos2 + 1;
            inPos2 = inPos + 1;
            out[outPos2] = in[inPos];
            literals++;
            if (literals == MAX_LITERAL) {
                out[(outPos - literals) - 1] = (byte) (literals - 1);
                literals = 0;
                outPos++;
            }
            inPos = inPos2;
            outPos2 = outPos;
        }
        while (inPos < inLen) {
            outPos = outPos2 + 1;
            inPos2 = inPos + 1;
            out[outPos2] = in[inPos];
            literals++;
            if (literals == MAX_LITERAL) {
                out[(outPos - literals) - 1] = (byte) (literals - 1);
                literals = 0;
                inPos = inPos2;
                outPos2 = outPos + 1;
            } else {
                inPos = inPos2;
                outPos2 = outPos;
            }
        }
        out[(outPos2 - literals) - 1] = (byte) (literals - 1);
        if (literals == 0) {
            return outPos2 - 1;
        }
        return outPos2;
    }

    public void expand(DataInput in, byte[] out, int outPos, int outLen) throws IOException {
        if ($assertionsDisabled || outLen >= 0) {
            do {
                int ctrl = in.readByte() & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
                if (ctrl < MAX_LITERAL) {
                    ctrl++;
                    in.readFully(out, outPos, ctrl);
                    outPos += ctrl;
                    continue;
                } else {
                    int len = ctrl >> 5;
                    if (len == 7) {
                        len += in.readByte() & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
                    }
                    len += 2;
                    ctrl = (((-((ctrl & 31) << 8)) - 1) - (in.readByte() & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT)) + outPos;
                    if (outPos + len >= out.length) {
                        throw new ArrayIndexOutOfBoundsException();
                    }
                    int i = 0;
                    int ctrl2 = ctrl;
                    int outPos2 = outPos;
                    while (i < len) {
                        outPos = outPos2 + 1;
                        ctrl = ctrl2 + 1;
                        out[outPos2] = out[ctrl2];
                        i++;
                        ctrl2 = ctrl;
                        outPos2 = outPos;
                    }
                    ctrl = ctrl2;
                    outPos = outPos2;
                    continue;
                }
            } while (outPos < outLen);
            return;
        }
        throw new AssertionError();
    }

    public void expand(ByteBuffer in, int inPos, byte[] out, int outPos, int outLen) {
        ByteBuffer in2 = null;
        if ($assertionsDisabled || outLen >= 0) {
            do {
                int inPos2 = inPos + 1;
                int ctrl = in.get(inPos) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
                if (ctrl < MAX_LITERAL) {
                    ctrl++;
                    if (in2 == null) {
                        in2 = in.duplicate();
                    }
                    in2.position(inPos2);
                    in2.get(out, outPos, ctrl);
                    outPos += ctrl;
                    inPos = inPos2 + ctrl;
                    continue;
                } else {
                    int len = ctrl >> 5;
                    if (len == 7) {
                        inPos = inPos2 + 1;
                        len += in.get(inPos2) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
                    } else {
                        inPos = inPos2;
                    }
                    len += 2;
                    inPos2 = inPos + 1;
                    ctrl = (((-((ctrl & 31) << 8)) - 1) - (in.get(inPos) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT)) + outPos;
                    if (outPos + len >= out.length) {
                        throw new ArrayIndexOutOfBoundsException();
                    }
                    int i = 0;
                    int ctrl2 = ctrl;
                    int outPos2 = outPos;
                    while (i < len) {
                        outPos = outPos2 + 1;
                        ctrl = ctrl2 + 1;
                        out[outPos2] = out[ctrl2];
                        i++;
                        ctrl2 = ctrl;
                        outPos2 = outPos;
                    }
                    ctrl = ctrl2;
                    outPos = outPos2;
                    inPos = inPos2;
                    continue;
                }
            } while (outPos < outLen);
            return;
        }
        throw new AssertionError();
    }
}
