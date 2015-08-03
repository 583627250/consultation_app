package com.consultation.app.model;

import java.util.List;

public class ProvinceTo {
    
    private String id;

    private String name;
    
    private String pid;
    
    private String status;
    
    private int index;

    private List<CityTo> cityList;

    public ProvinceTo() {
        super();
    }

    public ProvinceTo(String name, List<CityTo> cityList) {
        super();
        this.name=name;
        this.cityList=cityList;
    }

    
    public ProvinceTo(String id, String name, String pid, String status, int index, List<CityTo> cityList) {
        super();
        this.id=id;
        this.name=name;
        this.pid=pid;
        this.status=status;
        this.index=index;
        this.cityList=cityList;
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

    public List<CityTo> getCityList() {
        return cityList;
    }

    public void setCityList(List<CityTo> cityList) {
        this.cityList=cityList;
    }

    @Override
    public String toString() {
        return "ProvinceModel [name=" + name + ", cityList=" + cityList + "]";
    }

}
