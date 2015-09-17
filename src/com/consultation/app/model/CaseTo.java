package com.consultation.app.model;

import java.util.ArrayList;

public class CaseTo {

    private PatientCaseTo patientCase;

    private CaseContentTo caseContentTo;

    private ArrayList<ImageFilesTo> imageFilesTos;

    private ArrayList<DiscussionTo> discussionTos;

    private String handleReason;

    public PatientCaseTo getPatientCase() {
        return patientCase;
    }

    public void setPatientCase(PatientCaseTo patientCase) {
        this.patientCase=patientCase;
    }

    public CaseContentTo getCaseContentTo() {
        return caseContentTo;
    }

    public void setCaseContentTo(CaseContentTo caseContentTo) {
        this.caseContentTo=caseContentTo;
    }

    public ArrayList<ImageFilesTo> getImageFilesTos() {
        return imageFilesTos;
    }

    public void setImageFilesTos(ArrayList<ImageFilesTo> imageFilesTos) {
        this.imageFilesTos=imageFilesTos;
    }

    public ArrayList<DiscussionTo> getDiscussionTos() {
        return discussionTos;
    }

    public void setDiscussionTos(ArrayList<DiscussionTo> discussionTos) {
        this.discussionTos=discussionTos;
    }

    public String getHandleReason() {
        return handleReason;
    }

    public void setHandleReason(String handleReason) {
        this.handleReason=handleReason;
    }

    public CaseTo() {
        super();
    }

}