package com.consultation.app.model;

public class SymptomTo {

    private String name;
    
    private int id;

    private boolean isCheckMain;

    private boolean isCheckAccompany;
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id=id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name=name;
    }

    public boolean isCheckMain() {
        return isCheckMain;
    }

    public void setCheckMain(boolean isCheckMain) {
        this.isCheckMain=isCheckMain;
    }

    public boolean isCheckAccompany() {
        return isCheckAccompany;
    }

    public void setCheckAccompany(boolean isCheckAccompany) {
        this.isCheckAccompany=isCheckAccompany;
    }

}
