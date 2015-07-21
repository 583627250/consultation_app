package com.consultation.app.model;

public class PatientTo {

    private String address;

    private String id;

    private String state;

    private long create_time;

    private String tp;

    private String doctor;

    private String mobile_ph;

    private String pwd;

    private String real_name;

    private String sex;

    private String birth_year;

    private String birth_month;

    private String birth_day;

    private String identity_id;

    private String area_province;

    private String area_city;

    private String area_county;

    private String icon_url;

    private String modify_time;

    private String uid;

    
    public String getAddress() {
        return address;
    }

    
    public void setAddress(String address) {
        this.address=address;
    }

    
    public String getId() {
        return id;
    }

    
    public void setId(String id) {
        this.id=id;
    }

    
    public String getState() {
        return state;
    }

    
    public void setState(String state) {
        this.state=state;
    }

    
    public long getCreate_time() {
        return create_time;
    }

    
    public void setCreate_time(long create_time) {
        this.create_time=create_time;
    }

    
    public String getTp() {
        return tp;
    }

    
    public void setTp(String tp) {
        this.tp=tp;
    }

    
    public String getDoctor() {
        return doctor;
    }

    
    public void setDoctor(String doctor) {
        this.doctor=doctor;
    }

    
    public String getMobile_ph() {
        return mobile_ph;
    }

    
    public void setMobile_ph(String mobile_ph) {
        this.mobile_ph=mobile_ph;
    }

    
    public String getPwd() {
        return pwd;
    }

    
    public void setPwd(String pwd) {
        this.pwd=pwd;
    }

    
    public String getReal_name() {
        return real_name;
    }

    
    public void setReal_name(String real_name) {
        this.real_name=real_name;
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

    
    public String getIdentity_id() {
        return identity_id;
    }

    
    public void setIdentity_id(String identity_id) {
        this.identity_id=identity_id;
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

    
    public String getIcon_url() {
        return icon_url;
    }

    
    public void setIcon_url(String icon_url) {
        this.icon_url=icon_url;
    }

    
    public String getModify_time() {
        return modify_time;
    }

    
    public void setModify_time(String modify_time) {
        this.modify_time=modify_time;
    }

    
    public String getUid() {
        return uid;
    }

    
    public void setUid(String uid) {
        this.uid=uid;
    }


    public PatientTo(String address, String id, String state, long create_time, String tp, String doctor, String mobile_ph,
        String pwd, String real_name, String sex, String birth_year, String birth_month, String birth_day, String identity_id,
        String area_province, String area_city, String area_county, String icon_url, String modify_time, String uid) {
        super();
        this.address=address;
        this.id=id;
        this.state=state;
        this.create_time=create_time;
        this.tp=tp;
        this.doctor=doctor;
        this.mobile_ph=mobile_ph;
        this.pwd=pwd;
        this.real_name=real_name;
        this.sex=sex;
        this.birth_year=birth_year;
        this.birth_month=birth_month;
        this.birth_day=birth_day;
        this.identity_id=identity_id;
        this.area_province=area_province;
        this.area_city=area_city;
        this.area_county=area_county;
        this.icon_url=icon_url;
        this.modify_time=modify_time;
        this.uid=uid;
    }


    public PatientTo() {
        super();
    }
    
    
}
