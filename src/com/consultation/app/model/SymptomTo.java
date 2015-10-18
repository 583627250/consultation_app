package com.consultation.app.model;

public class SymptomTo {

    private String name;

    private boolean isCheckMain;

    private boolean isCheckAccompany;

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
