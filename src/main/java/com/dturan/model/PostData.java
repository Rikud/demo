package com.dturan.model;

public class PostData {

    private Integer id;
    private Integer branch;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBranch() {
        return branch;
    }

    public void setBranch(Integer branch) {
        this.branch = branch;
    }

    public PostData() {
        this.id = null;
        this.branch = null;
    }
}
