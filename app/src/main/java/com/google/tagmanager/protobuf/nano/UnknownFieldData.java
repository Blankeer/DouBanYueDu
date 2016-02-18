package com.google.tagmanager.protobuf.nano;

import java.util.Arrays;

public final class UnknownFieldData {
    final byte[] bytes;
    final int tag;

    UnknownFieldData(int tag, byte[] bytes) {
        this.tag = tag;
        this.bytes = bytes;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UnknownFieldData)) {
            return false;
        }
        UnknownFieldData other = (UnknownFieldData) o;
        if (this.tag == other.tag && Arrays.equals(this.bytes, other.bytes)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = this.tag + 527;
        for (byte b : this.bytes) {
            result = (result * 31) + b;
        }
        return result;
    }
}
