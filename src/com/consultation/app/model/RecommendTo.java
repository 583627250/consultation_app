package com.consultation.app.model;


public class RecommendTo {
    
    private String id;

    private String Title;
    
    private String depart_name;
    
    private String user_name;
    
    public String getTitle() {
        return Title;
    }
    
    public void setTitle(String title) {
        Title=title;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id=id;
    }
    
    public String getDepart_name() {
        return depart_name;
    }
    
    public void setDepart_name(String depart_name) {
        this.depart_name=depart_name;
    }
    
    public String getUser_name() {
        return user_name;
    }
    
    public void setUser_name(String user_name) {
        this.user_name=user_name;
    }

    public RecommendTo(String id, String title, String depart_name, String user_name) {
        super();
        this.id=id;
        Title=title;
        this.depart_name=depart_name;
        this.user_name=user_name;
    }

    public RecommendTo() {
        super();
    }
}
