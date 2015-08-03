package com.consultation.app.model;

import java.util.List;

public class DepartmentTo {

    private String id;

    private String pid;

    private String name;

    private String has_sub;

    private int index;

    private String status;

    private List<DepartmentBranchTo> branchTos;

    
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

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name=name;
    }

    
    public String getHas_sub() {
        return has_sub;
    }

    
    public void setHas_sub(String has_sub) {
        this.has_sub=has_sub;
    }

    
    public int getIndex() {
        return index;
    }

    
    public void setIndex(int index) {
        this.index=index;
    }

    
    public String getStatus() {
        return status;
    }

    
    public void setStatus(String status) {
        this.status=status;
    }

    
    public List<DepartmentBranchTo> getBranchTos() {
        return branchTos;
    }

    
    public void setBranchTos(List<DepartmentBranchTo> branchTos) {
        this.branchTos=branchTos;
    }


    public DepartmentTo() {
        super();
    }


    public DepartmentTo(String id, String pid, String name, String has_sub, int index, String status,
        List<DepartmentBranchTo> branchTos) {
        super();
        this.id=id;
        this.pid=pid;
        this.name=name;
        this.has_sub=has_sub;
        this.index=index;
        this.status=status;
        this.branchTos=branchTos;
    }
    
}
