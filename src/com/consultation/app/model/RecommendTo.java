package com.consultation.app.model;


public class RecommendTo {

    private String Title;
    
    private String contents;
    
    private String author;
    
    private String department;
    
    public String getTitle() {
        return Title;
    }

    
    public void setTitle(String title) {
        Title=title;
    }

    
    public String getContents() {
        return contents;
    }

    
    public void setContents(String contents) {
        this.contents=contents;
    }

    
    public String getAuthor() {
        return author;
    }

    
    public void setAuthor(String author) {
        this.author=author;
    }

    
    public String getDepartment() {
        return department;
    }

    
    public void setDepartment(String department) {
        this.department=department;
    }
    
    
}
