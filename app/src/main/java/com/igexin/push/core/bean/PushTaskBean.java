package com.igexin.push.core.bean;

import java.util.List;
import java.util.Map;

public class PushTaskBean {
    private String a;
    private String b;
    private String c;
    private String d;
    private String e;
    private List f;
    private byte[] g;
    private String h;
    private String i;
    private int j;
    private int k;
    private boolean l;
    private boolean m;
    private boolean n;
    private Map o;
    private int p;

    public PushTaskBean() {
        this.l = false;
        this.m = false;
        this.n = false;
    }

    public String getAction() {
        return this.a;
    }

    public List getActionChains() {
        return this.f;
    }

    public String getAppKey() {
        return this.i;
    }

    public String getAppid() {
        return this.b;
    }

    public BaseAction getBaseAction(String str) {
        for (BaseAction baseAction : getActionChains()) {
            if (baseAction.getActionId().equals(str)) {
                return baseAction;
            }
        }
        return null;
    }

    public Map getConditionMap() {
        return this.o;
    }

    public int getCurrentActionid() {
        return this.j;
    }

    public String getId() {
        return this.c;
    }

    public String getMessageId() {
        return this.d;
    }

    public String getMsgAddress() {
        return this.h;
    }

    public byte[] getMsgExtra() {
        return this.g;
    }

    public int getPerActionid() {
        return this.k;
    }

    public int getStatus() {
        return this.p;
    }

    public String getTaskId() {
        return this.e;
    }

    public boolean isCDNType() {
        return this.n;
    }

    public boolean isHttpImg() {
        return this.l;
    }

    public boolean isStop() {
        return this.m;
    }

    public void setAction(String str) {
        this.a = str;
    }

    public void setActionChains(List list) {
        this.f = list;
    }

    public void setAppKey(String str) {
        this.i = str;
    }

    public void setAppid(String str) {
        this.b = str;
    }

    public void setCDNType(boolean z) {
        this.n = z;
    }

    public void setConditionMap(Map map) {
        this.o = map;
    }

    public void setCurrentActionid(int i) {
        this.j = i;
    }

    public void setHttpImg(boolean z) {
        this.l = z;
    }

    public void setId(String str) {
        this.c = str;
    }

    public void setMessageId(String str) {
        this.d = str;
    }

    public void setMsgAddress(String str) {
        this.h = str;
    }

    public void setMsgExtra(byte[] bArr) {
        this.g = bArr;
    }

    public void setPerActionid(int i) {
        this.k = i;
    }

    public void setStatus(int i) {
        this.p = i;
    }

    public void setStop(boolean z) {
        this.m = z;
    }

    public void setTaskId(String str) {
        this.e = str;
    }
}
