package com.douban.book.reader.exception;

import com.douban.book.reader.manager.exception.DataLoadException;

public class ShareFailedWhileCreatingNoteException extends DataLoadException {
    public ShareFailedWhileCreatingNoteException(Throwable cause) {
        super(cause);
    }

    public ShareFailedWhileCreatingNoteException(String detailMessage) {
        super(detailMessage);
    }

    public ShareFailedWhileCreatingNoteException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
