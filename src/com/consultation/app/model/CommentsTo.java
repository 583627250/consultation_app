package com.consultation.app.model;

public class CommentsTo {

    private String commenter;

    private String comment_desc;

    private String create_time;

    private String start_value;

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

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time=create_time;
    }

    public String getStart_value() {
        return start_value;
    }

    public void setStart_value(String start_value) {
        this.start_value=start_value;
    }

    public CommentsTo(String commenter, String comment_desc, String create_time, String start_value) {
        super();
        this.commenter=commenter;
        this.comment_desc=comment_desc;
        this.create_time=create_time;
        this.start_value=start_value;
    }

    public CommentsTo() {
        super();
    }

}
