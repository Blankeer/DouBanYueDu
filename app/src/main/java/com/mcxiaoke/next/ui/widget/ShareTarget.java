package com.mcxiaoke.next.ui.widget;

import android.graphics.drawable.Drawable;
import android.view.MenuItem.OnMenuItemClickListener;

public class ShareTarget implements Comparable<ShareTarget> {
    public String className;
    public Drawable icon;
    public int id;
    public OnMenuItemClickListener listener;
    public String packageName;
    public CharSequence title;
    public int weight;

    public ShareTarget(CharSequence title, Drawable icon, OnMenuItemClickListener listener) {
        this(title, icon, 0, listener);
    }

    public ShareTarget(CharSequence title, Drawable icon, int id, OnMenuItemClickListener listener) {
        this.title = title;
        this.icon = icon;
        this.id = id;
        this.listener = listener;
    }

    public int compareTo(ShareTarget another) {
        return another.weight - this.weight;
    }

    public String toString() {
        return "ShareTarget{id=" + this.id + ", weight=" + this.weight + ", packageName='" + this.packageName + '\'' + ", className='" + this.className + '\'' + ", title=" + this.title + ", icon=" + this.icon + ", listener=" + this.listener + '}';
    }
}
