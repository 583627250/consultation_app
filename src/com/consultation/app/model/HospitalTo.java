package com.consultation.app.model;

public class HospitalTo {
    
    private String id;

    private String name;

    private String area_city_id;
    
    private String status;

    public HospitalTo() {
        super();
    }

    public HospitalTo(String name, String area_city_id) {
        super();
        this.name=name;
        this.area_city_id=area_city_id;
    }

    
    public HospitalTo(String id, String name, String area_city_id, String status) {
        super();
        this.id=id;
        this.name=name;
        this.area_city_id=area_city_id;
        this.status=status;
    }

    public String getId() {
        return id;
    }

    
    public void setId(String id) {
        this.id=id;
    }

    
    public String getStatus() {
        return status;
    }

    
    public void setStatus(String status) {
        this.status=status;
    }

    
    public String getArea_city_id() {
        return area_city_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name=name;
    }

    public String getZipcode() {
        return area_city_id;
    }

    public void setArea_city_id(String area_city_id) {
        this.area_city_id=area_city_id;
    }

}
