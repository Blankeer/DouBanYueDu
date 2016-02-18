package com.douban.book.reader.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ByteCountingInputStream extends FilterInputStream {
    long bytesRead;

    public ByteCountingInputStream(InputStream underlying) {
        super(underlying);
        this.bytesRead = 0;
    }

    public int read(byte[] bytes, int i, int i1) throws IOException {
        int n = super.read(bytes, i, i1);
        if (n != -1) {
            this.bytesRead += (long) n;
        }
        return n;
    }

    public long getBytesRead() {
        return this.bytesRead;
    }
}
