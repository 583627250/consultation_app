package com.consultation.app.model;

public class CasesTo {

    private String id;

    private String title;

    private String patient_name;

    private String photo_url;

    private String status;

    private String opinion;

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

    public String getPatient_name() {
        return patient_name;
    }

    public void setPatient_name(String patient_name) {
        this.patient_name=patient_name;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url=photo_url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status=status;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion=opinion;
    }

    public CasesTo() {
        super();
    }

    public CasesTo(String id, String title, String patient_name, String photo_url, String status, String opinion) {
        super();
        this.id=id;
        this.title=title;
        this.patient_name=patient_name;
        this.photo_url=photo_url;
        this.status=status;
        this.opinion=opinion;
    }

}
