package com.douban.book.reader.event;

import android.net.Uri;
import com.douban.book.reader.util.ReaderUriUtils;

public class PackageEvent extends WorksEvent {
    private Uri mUri;

    public PackageEvent(Uri uri) {
        super(ReaderUriUtils.getWorksId(uri));
        this.mUri = uri;
    }

    public Uri getUri() {
        return this.mUri;
    }

    public boolean isValidFor(Uri uri) {
        int type = ReaderUriUtils.getType(uri);
        if (type == 0) {
            if (ReaderUriUtils.getWorksId(uri) == ReaderUriUtils.getWorksId(this.mUri)) {
                return true;
            }
            return false;
        } else if (type != 2) {
            return false;
        } else {
            if (ReaderUriUtils.getPackageId(uri) != ReaderUriUtils.getPackageId(this.mUri)) {
                return false;
            }
            return true;
        }
    }
}
