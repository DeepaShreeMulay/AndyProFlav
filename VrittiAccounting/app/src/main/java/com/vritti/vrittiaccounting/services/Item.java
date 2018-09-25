package com.vritti.vrittiaccounting.services;

/**
 * Created by Admin-3 on 10/25/2017.
 */

public class Item{
    public final String text;
    public final int icon;
    public Item(String text, Integer icon) {
        this.text = text;
        this.icon = icon;
    }
    @Override
    public String toString() {
        return text;
    }
}
