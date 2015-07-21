package com.consultation.app.model;

public class UserStatisticsTo {

    private int total_consult;

    private int star_value;

    private int total_comment;
    
    public int getTotal_consult() {
        return total_consult;
    }

    
    public void setTotal_consult(int total_consult) {
        this.total_consult=total_consult;
    }

    
    public int getStar_value() {
        return star_value;
    }

    
    public void setStar_value(int star_value) {
        this.star_value=star_value;
    }
    
    public int getTotal_comment() {
        return total_comment;
    }


    
    public void setTotal_comment(int total_comment) {
        this.total_comment=total_comment;
    }


    public UserStatisticsTo() {
        super();
    }


    public UserStatisticsTo(int total_consult, int star_value) {
        super();
        this.total_consult=total_consult;
        this.star_value=star_value;
    }


    public UserStatisticsTo(int total_consult, int star_value, int total_comment) {
        super();
        this.total_consult=total_consult;
        this.star_value=star_value;
        this.total_comment=total_comment;
    }
    
    
}
