package com.consultation.app.model;

import java.util.ArrayList;

public class DoctorTo {

    private String id;

    private String hospital_name;

    private String depart_name;

    private String title;

    private String goodat_fields;

    private String approve_status;

    private String expert_gradeid;

    private String expert_grade;

    private UserTo user;

    private UserStatisticsTo userTj;

    private ArrayList<HelpPatientTo> helpPatientTos;

    private ArrayList<DoctorCommentsTo> comments;

    
    public String getExpert_gradeid() {
        return expert_gradeid;
    }

    
    public void setExpert_gradeid(String expert_gradeid) {
        this.expert_gradeid=expert_gradeid;
    }

    
    public String getExpert_grade() {
        return expert_grade;
    }

    
    public void setExpert_grade(String expert_grade) {
        this.expert_grade=expert_grade;
    }
    
    public ArrayList<HelpPatientTo> getHelpPatientTos() {
        return helpPatientTos;
    }

    
    public void setHelpPatientTos(ArrayList<HelpPatientTo> helpPatientTos) {
        this.helpPatientTos=helpPatientTos;
    }

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

    public ArrayList<DoctorCommentsTo> getComments() {
        return comments;
    }

    public void setComments(ArrayList<DoctorCommentsTo> comments) {
        this.comments=comments;
    }

    public DoctorTo() {
        super();
    }
}
