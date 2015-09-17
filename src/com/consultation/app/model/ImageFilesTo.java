package com.consultation.app.model;

public class ImageFilesTo {

    private String id;

    private String pic_url;

    private String little_pic_url;

    private String case_id;

    private String test_name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id=id;
    }

    public String getPic_url() {
        return pic_url;
    }

    public void setPic_url(String pic_url) {
        this.pic_url=pic_url;
    }
    
    public String getLittle_pic_url() {
        return little_pic_url;
    }

    public void setLittle_pic_url(String little_pic_url) {
        this.little_pic_url=little_pic_url;
    }
    public String getCase_id() {
        return case_id;
    }

    public void setCase_id(String case_id) {
        this.case_id=case_id;
    }

    public String getTest_name() {
        return test_name;
    }

    public void setTest_name(String test_name) {
        this.test_name=test_name;
    }

}
