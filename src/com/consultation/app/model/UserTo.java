package com.consultation.app.model;

public class UserTo {

    private String user_name;

    private String sex;

    private String birth_year;

    private String tp;

    private String icon_url;

    
    public String getUser_name() {
        return user_name;
    }

    
    public void setUser_name(String user_name) {
        this.user_name=user_name;
    }

    
    public String getSex() {
        return sex;
    }

    
    public void setSex(String sex) {
        this.sex=sex;
    }

    
    public String getBirth_year() {
        return birth_year;
    }

    
    public void setBirth_year(String birth_year) {
        this.birth_year=birth_year;
    }

    
    public String getTp() {
        return tp;
    }

    
    public void setTp(String tp) {
        this.tp=tp;
    }

    
    public String getIcon_url() {
        return icon_url;
    }

    
    public void setIcon_url(String icon_url) {
        this.icon_url=icon_url;
    }


    public UserTo() {
        super();
    }


    public UserTo(String user_name, String sex, String birth_year, String tp, String icon_url) {
        super();
        this.user_name=user_name;
        this.sex=sex;
        this.birth_year=birth_year;
        this.tp=tp;
        this.icon_url=icon_url;
    }
    
    
}
