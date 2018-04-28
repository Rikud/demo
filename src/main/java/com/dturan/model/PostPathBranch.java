package com.dturan.model;

import java.math.BigDecimal;

public class PostPathBranch {

    private String path = null;
    private BigDecimal branch = null;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public BigDecimal getBranch() {
        return branch;
    }

    public void setBranch(BigDecimal branch) {
        this.branch = branch;
    }

}
