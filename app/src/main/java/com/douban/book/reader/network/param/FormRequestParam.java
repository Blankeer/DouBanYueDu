package com.douban.book.reader.network.param;

import com.douban.book.reader.network.param.RequestParam.Type;

public class FormRequestParam extends UrlEncodedRequestParam<FormRequestParam> {
    public Type getType() {
        return Type.FORM;
    }
}
