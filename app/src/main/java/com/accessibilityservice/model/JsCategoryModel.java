package com.accessibilityservice.model;


import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class JsCategoryModel implements Serializable {
    private List<JsModel> items = new CopyOnWriteArrayList();
    private String name;

    public JsCategoryModel(String str) {
        this.name = str;
    }

    public void addItem(JsModel jsModel) {
        this.items.add(jsModel);
    }

    public Object getItem(int i) {
        return i == 0 ? this : this.items.get(i - 1);
    }

    public int getItemCount() {
        return this.items.size() + 1;
    }

    public List<JsModel> getItems() {
        return this.items;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }
}
