package com.example.hospitalmanagement.bean;

public class Order {
    private String oid;
    private int sid;
    private int uid;
    private String time;       // 排班时间
    private String departname; // 科室名称
    private String dname;      // 医生姓名
    private double price;      // 金额
    private int pay;           // 支付状态（0 未支付，1 已支付）

    public Order() {}

    public Order(String oid, int sid, int uid, String time, String departname, String dname, double price, int pay) {
        this.oid = oid;
        this.sid = sid;
        this.uid = uid;
        this.time = time;
        this.departname = departname;
        this.dname = dname;
        this.price = price;
        this.pay = pay;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDepartname() {
        return departname;
    }

    public void setDepartname(String departname) {
        this.departname = departname;
    }

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getPay() {
        return pay;
    }

    public void setPay(int pay) {
        this.pay = pay;
    }

    @Override
    public String toString() {
        return "Order{" +
                "oid='" + oid + '\'' +
                ", sid=" + sid +
                ", uid=" + uid +
                ", time='" + time + '\'' +
                ", departname='" + departname + '\'' +
                ", dname='" + dname + '\'' +
                ", price=" + price +
                ", pay=" + pay +
                '}';
    }
}
