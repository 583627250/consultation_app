package com.consultation.app.model;

public class PayRecordTo {

    private String amount;

    private String type;

    private String create_time;

    private CasesTo casesTo;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount=amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type=type;
    }

    public CasesTo getCasesTo() {
        return casesTo;
    }

    public void setCasesTo(CasesTo casesTo) {
        this.casesTo=casesTo;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time=create_time;
    }

}
