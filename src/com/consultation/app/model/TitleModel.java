package com.consultation.app.model;

import java.util.List;

public class TitleModel {

    private String id;

    private String name;

    private String level;

    private String childCount;

    private String title;

    private List<ItemModel> itemModels;

    public String getChildCount() {
        return childCount;
    }

    public void setChildCount(String childCount) {
        this.childCount=childCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id=id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name=name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level=level;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title=title;
    }

    public List<ItemModel> getItemModels() {
        return itemModels;
    }

    public void setItemModels(List<ItemModel> itemModels) {
        this.itemModels=itemModels;
    }

    public TitleModel() {
        super();
    }

    public TitleModel(String id, String name, String level, String childCount, String title, List<ItemModel> itemModels) {
        super();
        this.id=id;
        this.name=name;
        this.level=level;
        this.childCount=childCount;
        this.title=title;
        this.itemModels=itemModels;
    }

}
