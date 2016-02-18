package com.douban.book.reader.entity;

import com.tencent.connect.common.Constants;

public class Session {
    public String accessToken;
    public String deviceId;
    public int doubanUserId;
    public int expiresIn;
    public int openIdType;
    public String refreshToken;

    public boolean isWeiboUser() {
        return this.openIdType == Header.DOUBLE_1;
    }

    public boolean isQQUser() {
        return this.openIdType == Header.DOUBLE_0;
    }

    public boolean isOpenIdLogin() {
        return this.openIdType > 0;
    }

    public String getOpenIdTypeName() {
        switch (this.openIdType) {
            case Header.DOUBLE_0 /*103*/:
                return Constants.SOURCE_QQ;
            case Header.DOUBLE_1 /*104*/:
                return "Weibo";
            case Header.ARRAY_BYTE_ALL_EQUAL /*110*/:
                return "Weixin";
            default:
                return "<Unknown>";
        }
    }
}
