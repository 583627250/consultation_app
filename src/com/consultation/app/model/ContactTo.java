package com.consultation.app.model;

public class ContactTo {

    private String name;

    private String phone;

    private boolean isCheck;

    private String isInvitation;

    public String getIsInvitation() {
        return isInvitation;
    }

    public void setInvitation(String isInvitation) {
        this.isInvitation=isInvitation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name=name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone=phone;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean isCheck) {
        this.isCheck=isCheck;
    }

}
