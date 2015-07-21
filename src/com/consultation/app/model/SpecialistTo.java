package com.consultation.app.model;

public class SpecialistTo {

    private String id;

    private String hospital_name;

    private String depart_name;

    private String title;

    private String goodat_fields;

    private String approve_status;

    private UserTo user;

    private UserStatisticsTo userTj;

    
    public String getId() {
        return id;
    }

    
    public void setId(String id) {
        this.id=id;
    }

    
    public String getHospital_name() {
        return hospital_name;
    }

    
    public void setHospital_name(String hospital_name) {
        this.hospital_name=hospital_name;
    }

    
    public String getDepart_name() {
        return depart_name;
    }

    
    public void setDepart_name(String depart_name) {
        this.depart_name=depart_name;
    }

    
    public String getTitle() {
        return title;
    }

    
    public void setTitle(String title) {
        this.title=title;
    }

    
    public String getGoodat_fields() {
        return goodat_fields;
    }

    
    public void setGoodat_fields(String goodat_fields) {
        this.goodat_fields=goodat_fields;
    }

    
    public String getApprove_status() {
        return approve_status;
    }

    
    public void setApprove_status(String approve_status) {
        this.approve_status=approve_status;
    }

    
    public UserTo getUser() {
        return user;
    }

    
    public void setUser(UserTo user) {
        this.user=user;
    }

    
    public UserStatisticsTo getUserTj() {
        return userTj;
    }

    
    public void setUserTj(UserStatisticsTo userTj) {
        this.userTj=userTj;
    }


    public SpecialistTo() {
        super();
    }


    public SpecialistTo(String id, String hospital_name, String depart_name, String title, String goodat_fields,
        String approve_status, UserTo user, UserStatisticsTo userTj) {
        super();
        this.id=id;
        this.hospital_name=hospital_name;
        this.depart_name=depart_name;
        this.title=title;
        this.goodat_fields=goodat_fields;
        this.approve_status=approve_status;
        this.user=user;
        this.userTj=userTj;
    }
    
    

}
