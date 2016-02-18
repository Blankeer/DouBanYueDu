package com.douban.book.reader.exception;

public class PackageSizeThresholdExceededException extends WorksException {
    private long mPackageSize;

    public PackageSizeThresholdExceededException(Throwable cause) {
        super(cause);
    }

    public PackageSizeThresholdExceededException(String detailMessage) {
        super(detailMessage);
    }

    public PackageSizeThresholdExceededException(long packageSize) {
        this.mPackageSize = packageSize;
    }

    public long getPackageSize() {
        return this.mPackageSize;
    }
}
