package com.example.firebase;

public class User {
    private String profile;
    private float C_AC;
    private float sum;
    private int E_bill;
    private int Time;
    private int on_off;


    public User(){}

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public float getC_AC() {
        return C_AC;
    }

    public void setC_AC(float C_AC) {
        this.C_AC = C_AC;
    }

    public float getSum() {
        return sum;
    }

    public void setSum(float sum) {
        this.sum = sum;
    }

    public int getE_bill() { return E_bill; }

    public void setE_bill(int E_bill) { this.E_bill = E_bill; }

    public int getTime() { return Time; }

    public void setTime(int Time) { this.Time = Time; }

    public  int getOn_off() {return on_off;}

    public  void setOn_off() {this.on_off=on_off;}

}

