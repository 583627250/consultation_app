package com.consultation.app.model;

public class DoctorCommentsTo {
    
    private String id;

    
    public String getId() {
        return id;
    }


    
    public void setId(String id) {
        this.id=id;
    }


    private String commenter;

    private String comment_desc;

    private long create_time;

    private int start_value;

    
    public String getCommenter() {
        return commenter;
    }

    
    public void setCommenter(String commenter) {
        this.commenter=commenter;
    }

    
    public String getComment_desc() {
        return comment_desc;
    }

    
    public void setComment_desc(String comment_desc) {
        this.comment_desc=comment_desc;
    }

    
    public long getCreate_time() {
        return create_time;
    }

    
    public void setCreate_time(long create_time) {
        this.create_time=create_time;
    }

    
    public int getStart_value() {
        return start_value;
    }

    
    public void setStart_value(int start_value) {
        this.start_value=start_value;
    }
    
    
}
