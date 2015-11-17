package com.consultation.app.model;

public class HelpTo {

    private String id;

    private String content_url;

    private String title;

    private String have_sub;

    private String parent_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id=id;
    }

    public String getContent_url() {
        return content_url;
    }

    public void setContent_url(String content_url) {
        this.content_url=content_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title=title;
    }

    public String getHave_sub() {
        return have_sub;
    }

    public void setHave_sub(String have_sub) {
        this.have_sub=have_sub;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id=parent_id;
    }

}
