package com.consultation.app.model;

import java.util.List;

public class CityTo {
    
    private String id;

    private String name;
    
    private String pid;
    
    private String status;
    
    private int index;

    private List<HospitalTo> hospitalList;
    
    public CityTo() {
        super();
    }

    public CityTo(String name, List<HospitalTo> hospitalList) {
        super();
        this.name=name;
        this.hospitalList=hospitalList;
    }

    
    public CityTo(String id, String name, String pid, String status, int index, List<HospitalTo> hospitalList) {
        super();
        this.id=id;
        this.name=name;
        this.pid=pid;
        this.status=status;
        this.index=index;
        this.hospitalList=hospitalList;
    }

    public String getId() {
        return id;
    }

    
    public void setId(String id) {
        this.id=id;
    }

    
    public String getPid() {
        return pid;
    }

    
    public void setPid(String pid) {
        this.pid=pid;
    }

    
    public String getStatus() {
        return status;
    }

    
    public void setStatus(String status) {
        this.status=status;
    }

    
    public int getIndex() {
        return index;
    }

    
    public void setIndex(int index) {
        this.index=index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name=name;
    }

    public List<HospitalTo> getHospitalList() {
        return hospitalList;
    }

    public void setHospitalList(List<HospitalTo> hospitalList) {
        this.hospitalList=hospitalList;
    }

    @Override
    public String toString() {
        return "CityModel [name=" + name + ", districtList=" + hospitalList + "]";
    }

}
