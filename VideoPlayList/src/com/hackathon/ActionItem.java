package com.hackathon;

//  The original popup window code came from www.londatiga.net.
//  I've modified it.

import android.graphics.drawable.Drawable;
import android.view.View;

public class ActionItem {
    private Drawable icon;
    private String title;
    private View.OnClickListener listener;

    public ActionItem() {
        this("", null, null);
    }

    public ActionItem(Drawable icon) {
        this("", icon, null);
    }

    public ActionItem(String itemTitle, Drawable itemIcon, View.OnClickListener onClickListener) {
        this.title = itemTitle;
        this.icon = itemIcon;
        this.listener = onClickListener;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public Drawable getIcon() {
        return this.icon;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public View.OnClickListener getOnClickListener() {
        return this.listener;
    }
}
