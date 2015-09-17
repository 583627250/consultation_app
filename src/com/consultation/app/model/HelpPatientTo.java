package com.consultation.app.model;

public class HelpPatientTo {

    private String id;

    private String title;

    private String patient_name;

    private String status;

    private String status_desc;

    private String photo_url;
    
    private long create_time;
    
    public long getCreate_time() {
        return create_time;
    }

    
    public void setCreate_time(long create_time) {
        this.create_time=create_time;
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

    public String getPatient_name() {
        return patient_name;
    }

    public void setPatient_name(String patient_name) {
        this.patient_name=patient_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status=status;
    }

    public String getStatus_desc() {
        return status_desc;
    }

    public void setStatus_desc(String status_desc) {
        this.status_desc=status_desc;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url=photo_url;
    }

}
