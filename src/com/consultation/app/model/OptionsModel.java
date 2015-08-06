package com.consultation.app.model;

public class OptionsModel {

    private String id = "0";

    private String checked = "0";

    private String name = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id=id;
    }

    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked=checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name=name;
    }

    public OptionsModel() {
        super();
    }

    public OptionsModel(String id, String checked, String name) {
        super();
        this.id=id;
        this.checked=checked;
        this.name=name;
    }

}
