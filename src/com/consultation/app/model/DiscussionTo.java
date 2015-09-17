package com.consultation.app.model;

import java.util.List;

public class DiscussionTo {

    private String id;

    private String at_userid;

    private String at_username;

    private String discusser_userid;

    private String discusser;

    private String content;

    private long create_time;

    private String is_view;

    private String case_id;
    
    private String have_photos;
    
    private UserTo userTo;
    
    private List<ImageFilesTo> imageFilesTos;
    
    public UserTo getUserTo() {
        return userTo;
    }
    
    public void setUserTo(UserTo userTo) {
        this.userTo=userTo;
    }

    public String getHave_photos() {
        return have_photos;
    }

    
    public void setHave_photos(String have_photos) {
        this.have_photos=have_photos;
    }

    
    public List<ImageFilesTo> getImageFilesTos() {
        return imageFilesTos;
    }

    
    public void setImageFilesTos(List<ImageFilesTo> imageFilesTos) {
        this.imageFilesTos=imageFilesTos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id=id;
    }

    public String getAt_userid() {
        return at_userid;
    }

    public void setAt_userid(String at_userid) {
        this.at_userid=at_userid;
    }

    public String getAt_username() {
        return at_username;
    }

    public void setAt_username(String at_username) {
        this.at_username=at_username;
    }

    public String getDiscusser_userid() {
        return discusser_userid;
    }

    public void setDiscusser_userid(String discusser_userid) {
        this.discusser_userid=discusser_userid;
    }

    public String getDiscusser() {
        return discusser;
    }

    public void setDiscusser(String discusser) {
        this.discusser=discusser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content=content;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time=create_time;
    }

    public String getIs_view() {
        return is_view;
    }

    public void setIs_view(String is_view) {
        this.is_view=is_view;
    }

    public String getCase_id() {
        return case_id;
    }

    public void setCase_id(String case_id) {
        this.case_id=case_id;
    }

    public DiscussionTo() {
        super();
    }

}
