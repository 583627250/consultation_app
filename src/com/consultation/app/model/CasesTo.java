package com.consultation.app.model;

public class CasesTo {

    private String id;

    private String status;

    private String status_des;

    private int viewingCount;

    private String destination;

    private long create_time;

    private String title;

    private String depart_id;

    private String doctor_userid;

    private String consult_fee;

    private String doctor_name;

    private String expert_userid;

    private String expert_name;

    private String problem;

    private String consult_tp;

    private String opinion;

    private String uid;

    private int ToReadMsgCount;

    private String patient_name;

    private String patient_id;

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id=patient_id;
    }

    private PatientTo patient;

    private DoctorTo doctorTo;

    private DoctorTo expertTo;

    private CaseContentTo caseContentTo;

    public int getViewingCount() {
        return viewingCount;
    }

    public void setViewingCount(int viewingCount) {
        this.viewingCount=viewingCount;
    }

    public CaseContentTo getCaseContentTo() {
        return caseContentTo;
    }

    public void setCaseContentTo(CaseContentTo caseContentTo) {
        this.caseContentTo=caseContentTo;
    }

    public DoctorTo getDoctorTo() {
        return doctorTo;
    }

    public void setDoctorTo(DoctorTo doctorTo) {
        this.doctorTo=doctorTo;
    }

    public DoctorTo getExpertTo() {
        return expertTo;
    }

    public void setExpertTo(DoctorTo expertTo) {
        this.expertTo=expertTo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id=id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status=status;
    }

    public String getStatus_des() {
        return status_des;
    }

    public void setStatus_des(String status_des) {
        this.status_des=status_des;
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

    public int getToReadMsgCount() {
        return ToReadMsgCount;
    }

    public void setToReadMsgCount(int toReadMsgCount) {
        ToReadMsgCount=toReadMsgCount;
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

    public String getPatient_name() {
        return patient_name;
    }

    public void setPatient_name(String patient_name) {
        this.patient_name=patient_name;
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

    public CasesTo() {
        super();
    }

}