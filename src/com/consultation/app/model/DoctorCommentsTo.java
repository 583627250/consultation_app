package com.consultation.app.model;

public class DoctorCommentsTo {

    private String id;

    private String commenter;

    private String comment_desc;

    private long create_time;

    private int star_value;

    private String photo_url;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id=id;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url=photo_url;
    }

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

    public int getStar_value() {
        return star_value;
    }

    public void setStar_value(int star_value) {
        this.star_value=star_value;
    }

}
