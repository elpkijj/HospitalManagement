package com.example.hospitalmanagement.bean;

public class Doctor {
    private int did;
    private String dname;
    private String dlevel;
    private String dinfo;
    private int departid; // departid 保持为 int
    private String departname; // 新增字段 departname
    private String sex;   // sex 修改为 String 类型
    private String ddetail;

    public Doctor() {}

    public Doctor(int did, String dname, String dlevel, String dinfo, int departid, String departname, String sex, String ddetail) {
        this.did = did;
        this.dname = dname;
        this.dlevel = dlevel;
        this.dinfo = dinfo;
        this.departid = departid;
        this.departname = departname;
        this.sex = sex;
        this.ddetail = ddetail;
    }

    public int getDid() {
        return did;
    }

    public void setDid(int did) {
        this.did = did;
    }

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    public String getDlevel() {
        return dlevel;
    }

    public void setDlevel(String dlevel) {
        this.dlevel = dlevel;
    }

    public String getDinfo() {
        return dinfo;
    }

    public void setDinfo(String dinfo) {
        this.dinfo = dinfo;
    }

    public int getDepartid() {
        return departid;
    }

    public void setDepartid(int departid) {
        this.departid = departid;
    }

    public String getDepartname() {
        return departname;
    }

    public void setDepartname(String departname) {
        this.departname = departname;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getDdetail() {
        return ddetail;
    }

    public void setDdetail(String ddetail) {
        this.ddetail = ddetail;
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "did=" + did +
                ", dname='" + dname + '\'' +
                ", dlevel='" + dlevel + '\'' +
                ", dinfo='" + dinfo + '\'' +
                ", departid=" + departid +
                ", departname='" + departname + '\'' +
                ", sex='" + sex + '\'' +
                ", ddetail='" + ddetail + '\'' +
                '}';
    }
}
