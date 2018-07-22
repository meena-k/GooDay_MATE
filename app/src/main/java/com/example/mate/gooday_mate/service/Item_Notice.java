package com.example.mate.gooday_mate.service;

public class Item_Notice {
    private int id, icon;
    private String title, metadata, content;

    public Item_Notice(int id, int icon, String title, String metadata, String content) {
        this.id = id;
        this.icon = icon;
        this.title = title;
        this.metadata = metadata;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
