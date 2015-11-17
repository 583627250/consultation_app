package com.consultation.app.model;

public class ExpertGradeTo {

    private String expert_gradeid;

    private String expert_grade;

    private String clinic_fee;
    
    private String technology_fee;

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
    
    public String getClinic_fee() {
        return clinic_fee;
    }
    
    public void setClinic_fee(String clinic_fee) {
        this.clinic_fee=clinic_fee;
    }
    
    public String getTechnology_fee() {
        return technology_fee;
    }
    
    public void setTechnology_fee(String technology_fee) {
        this.technology_fee=technology_fee;
    }
}
