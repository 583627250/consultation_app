package com.consultation.app.model;

public class UserTo {

    private String user_name;

    private String sex;

    private String birth_year;
    
    private String birth_month;
    
    private String birth_day;

    private String tp;

    private String icon_url;

    private String area_province;
    
    private String area_city;
    
    
    public String getBirth_month() {
        return birth_month;
    }


    
    public void setBirth_month(String birth_month) {
        this.birth_month=birth_month;
    }


    
    public String getBirth_day() {
        return birth_day;
    }


    
    public void setBirth_day(String birth_day) {
        this.birth_day=birth_day;
    }


    
    public String getArea_province() {
        return area_province;
    }


    
    public void setArea_province(String area_province) {
        this.area_province=area_province;
    }


    
    public String getArea_city() {
        return area_city;
    }


    
    public void setArea_city(String area_city) {
        this.area_city=area_city;
    }


    
    public String getArea_county() {
        return area_county;
    }


    
    public void setArea_county(String area_county) {
        this.area_county=area_county;
    }


    private String area_county;
    
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
