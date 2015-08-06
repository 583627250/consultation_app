package com.consultation.app.model;

import java.util.List;

public class CaseModel {

    private String id = "0";

    private String name = "";

    private String level = "0";

    private List<TitleModel> titleModels;

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

    public List<TitleModel> getTitleModels() {
        return titleModels;
    }

    public void setTitleModels(List<TitleModel> titleModels) {
        this.titleModels=titleModels;
    }

    public CaseModel(String id, String name, String level, List<TitleModel> titleModels) {
        super();
        this.id=id;
        this.name=name;
        this.level=level;
        this.titleModels=titleModels;
    }

    public CaseModel() {
        super();
    }

}
