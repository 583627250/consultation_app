package com.consultation.app.model;

import java.util.List;

public class TitleModel {

    private String id="0";

    private String name="";

    private String level="0";

    private String childCount="0";

    private String title="";

    private String isShow="True";

    private String Type="";

    private String isNormal="True";
    
    private String lineBreak="True";

    private String prefixP="";

    private String suffixP="";

    private String seperator="";

    private String noChecked="";
    
    public String getLineBreak() {
        return lineBreak;
    }

    public void setLineBreak(String lineBreak) {
        this.lineBreak=lineBreak;
    }

    public String getPrefixP() {
        return prefixP;
    }

    public void setPrefixP(String prefixP) {
        this.prefixP=prefixP;
    }

    public String getSuffixP() {
        return suffixP;
    }

    public void setSuffixP(String suffixP) {
        this.suffixP=suffixP;
    }

    public String getSeperator() {
        return seperator;
    }

    public void setSeperator(String seperator) {
        this.seperator=seperator;
    }

    public String getNoChecked() {
        return noChecked;
    }

    public void setNoChecked(String noChecked) {
        this.noChecked=noChecked;
    }

    public String getIsNormal() {
        return isNormal;
    }

    public void setIsNormal(String isNormal) {
        this.isNormal=isNormal;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type=type;
    }

    public String getIsShow() {
        return isShow;
    }

    public void setIsShow(String isShow) {
        this.isShow=isShow;
    }

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
}
