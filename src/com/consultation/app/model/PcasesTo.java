package com.consultation.app.model;

public class PcasesTo {

    private String id;

    private String content;

    private String status;

    private String destination;

    private long create_time;

    private String title;

    private String depart_id;

    private String doctor_userid;

    private int consult_fee;

    private String patient_userid;

    private String doctor_name;

    private String expert_userid;

    private String expert_name;

    private String problem;

    private String consult_tp;

    private String opinion;

    private String uid;

    private PatientTo patient;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id=id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content=content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status=status;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination=destination;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time=create_time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title=title;
    }

    public String getDepart_id() {
        return depart_id;
    }

    public void setDepart_id(String depart_id) {
        this.depart_id=depart_id;
    }

    public String getDoctor_userid() {
        return doctor_userid;
    }

    public void setDoctor_userid(String doctor_userid) {
        this.doctor_userid=doctor_userid;
    }

    public int getConsult_fee() {
        return consult_fee;
    }

    public void setConsult_fee(int consult_fee) {
        this.consult_fee=consult_fee;
    }

    public String getPatient_userid() {
        return patient_userid;
    }

    public void setPatient_userid(String patient_userid) {
        this.patient_userid=patient_userid;
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

    public String getConsult_tp() {
        return consult_tp;
    }

    public void setConsult_tp(String consult_tp) {
        this.consult_tp=consult_tp;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion=opinion;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid=uid;
    }

    public PatientTo getPatient() {
        return patient;
    }

    public void setPatient(PatientTo patient) {
        this.patient=patient;
    }

    public PcasesTo(String id, String content, String status, String destination, long create_time, String title, String depart_id,
        String doctor_userid, int consult_fee, String patient_userid, String doctor_name, String expert_userid,
        String expert_name, String problem, String consult_tp, String opinion, String uid, PatientTo patient) {
        super();
        this.id=id;
        this.content=content;
        this.status=status;
        this.destination=destination;
        this.create_time=create_time;
        this.title=title;
        this.depart_id=depart_id;
        this.doctor_userid=doctor_userid;
        this.consult_fee=consult_fee;
        this.patient_userid=patient_userid;
        this.doctor_name=doctor_name;
        this.expert_userid=expert_userid;
        this.expert_name=expert_name;
        this.problem=problem;
        this.consult_tp=consult_tp;
        this.opinion=opinion;
        this.uid=uid;
        this.patient=patient;
    }

    public PcasesTo() {
        super();
    }

}
