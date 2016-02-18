package com.sina.weibo.sdk.register.mobile;

import android.text.TextUtils;
import java.io.Serializable;

public class Country implements Comparable<Country>, Serializable {
    public static final String CHINA_CODE = "0086";
    private static final long serialVersionUID = 0;
    private String code;
    private String[] mccs;
    private String name;
    private String pinyin;

    public Country(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public String[] getMccs() {
        return this.mccs;
    }

    public void setMccs(String[] mccs) {
        this.mccs = mccs;
    }

    public String getPinyin() {
        return PinyinUtils.getObject().getPinyin(this.name).toLowerCase();
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getCode() {
        return this.code;
    }

    public int compareTo(Country another) {
        if (TextUtils.isEmpty(this.pinyin)) {
            return -1;
        }
        if (another == null || TextUtils.isEmpty(another.pinyin)) {
            return 1;
        }
        return this.pinyin.compareTo(another.pinyin);
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }
}
