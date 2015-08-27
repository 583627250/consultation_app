package com.consultation.app.model;

public class FeedBackTo {

    private String content;

    private long create_time;

    private String reply;

    private long reply_time;

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

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply=reply;
    }

    public long getReply_time() {
        return reply_time;
    }

    public void setReply_time(long reply_time) {
        this.reply_time=reply_time;
    }

    public FeedBackTo() {
        super();
    }

}
