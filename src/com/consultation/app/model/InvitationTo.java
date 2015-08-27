package com.consultation.app.model;

public class InvitationTo {

    private String mobile_ph;

    private String code;

    private long create_time;

    private long valid_date;

    private String is_joined;

    private String joined_time;

    public String getMobile_ph() {
        return mobile_ph;
    }

    public void setMobile_ph(String mobile_ph) {
        this.mobile_ph=mobile_ph;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code=code;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time=create_time;
    }

    public long getValid_date() {
        return valid_date;
    }

    public void setValid_date(long valid_date) {
        this.valid_date=valid_date;
    }

    public String getIs_joined() {
        return is_joined;
    }

    public void setIs_joined(String is_joined) {
        this.is_joined=is_joined;
    }

    public String getJoined_time() {
        return joined_time;
    }

    public void setJoined_time(String joined_time) {
        this.joined_time=joined_time;
    }

    public InvitationTo() {
        super();
    }

}
