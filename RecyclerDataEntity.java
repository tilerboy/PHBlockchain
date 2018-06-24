package com.caihang.ylyim.bean;

import android.os.Bundle;

import java.util.function.Function;

public class RecyclerDataEntity {
    public String title;
    public String content;
    public Function function;
    public int itemIcon;
    public Bundle bundle;
    public String tag;
    public String url;

    public RecyclerDataEntity(String title) {
        this(title, null, 0, null, null, null);
    }

    public RecyclerDataEntity(String title, String content, int itemIcon, Function function, Bundle bundle) {
        this(title, content, itemIcon, function, bundle, null);
    }

    public RecyclerDataEntity(String title, String content, int itemIcon, Function function, Bundle bundle, String tag) {
        this(title, content, itemIcon, function, bundle, tag, null);
    }

    public RecyclerDataEntity(String title, String content, int itemIcon, Function function, Bundle bundle, String tag, String url) {
        this.title = title;
        this.content = content;
        this.function = function;
        this.itemIcon = itemIcon;
        this.bundle = bundle;
        this.tag = tag;
        this.url = url;
    }

    public RecyclerDataEntity setContent(String content) {
        this.content = content;
        return this;
    }

    public RecyclerDataEntity setFunction(Function function) {
        this.function = function;
        return this;
    }

    public RecyclerDataEntity setItemIcon(int itemIcon) {
        this.itemIcon = itemIcon;
        return this;
    }

    public RecyclerDataEntity setBundle(Bundle bundle) {
        this.bundle = bundle;
        return this;
    }

    public RecyclerDataEntity setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public RecyclerDataEntity setUrl(String url) {
        this.url = url;
        return this;
    }
}
