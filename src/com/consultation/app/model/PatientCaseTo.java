package com.consultation.app.model;

public class PatientCaseTo {

    private String id;

    private String status;

    private String status_desc;

    private String title;

    private String create_time;

    private String doctor_userid;

    private String consult_fee;

    private String doctor_name;

    private String expert_userid;

    private String expert_name;

    private String problem;

    private String consult_tp;

    private String opinion;

    private String depart_id;

    private String case_templ_id;

    private String patient_name;

    private String patient_userid;

    private UserTo userTo;

    public PatientCaseTo() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id=id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title=title;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time=create_time;
    }

    public String getStatus_desc() {
        return status_desc;
    }

    public String getDepart_id() {
        return depart_id;
    }

    public String getCase_templ_id() {
        return case_templ_id;
    }

    public void setCase_templ_id(String case_templ_id) {
        this.case_templ_id=case_templ_id;
    }

    public void setDepart_id(String depart_id) {
        this.depart_id=depart_id;
    }

    public void setStatus_desc(String status_desc) {
        this.status_desc=status_desc;
    }

    public String getDoctor_userid() {
        return doctor_userid;
    }

    public void setDoctor_userid(String doctor_userid) {
        this.doctor_userid=doctor_userid;
    }

    public String getConsult_fee() {
        return consult_fee;
    }

    public void setConsult_fee(String consult_fee) {
        this.consult_fee=consult_fee;
    }

    public String getDoctor_name() {
        return doctor_name;
    }

    public void setDoctor_name(String doctor_name) {
        this.doctor_name=doctor_name;
    }

    public String getExpert_userid() {
        return expert_userid;
    }

    public void setExpert_userid(String expert_userid) {
        this.expert_userid=expert_userid;
    }

    public String getExpert_name() {
        return expert_name;
    }

    public void setExpert_name(String expert_name) {
        this.expert_name=expert_name;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem=problem;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion=opinion;
    }

    public String getPatient_name() {
        return patient_name;
    }

    public void setPatient_name(String patient_name) {
        this.patient_name=patient_name;
    }

    public String getPatient_userid() {
        return patient_userid;
    }

    public void setPatient_userid(String patient_userid) {
        this.patient_userid=patient_userid;
    }

    public UserTo getUserTo() {
        return userTo;
    }

    public void setUserTo(UserTo userTo) {
        this.userTo=userTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status=status;
    }

    public String getConsult_tp() {
        String statusText="公开讨论";
        switch(Integer.parseInt(consult_tp)) {
            case 10:
                statusText="公开讨论";
                break;
            case 20:
                statusText="专家咨询";
                break;
            default:
                break;
        }
        return statusText;
    }

    public void setConsult_tp(String consult_tp) {
        this.consult_tp=consult_tp;
    }

}
