package com.consultation.app.model;


public class TitleTo {

    private String id;
    
    private String name;
    
    private int index;

    
    public String getId() {
        return id;
    }

    
    public void setId(String id) {
        this.id=id;
    }

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name=name;
    }

    
    public int getIndex() {
        return index;
    }

    
    public void setIndex(int index) {
        this.index=index;
    }


    public TitleTo() {
        super();
    }


    public TitleTo(String id, String name, int index) {
        super();
        this.id=id;
        this.name=name;
        this.index=index;
    }
    
}
