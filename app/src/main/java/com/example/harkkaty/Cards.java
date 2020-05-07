package com.example.harkkaty;

public class Cards {
    private double payLimit, takeLimit;
    private  String dead, accNbr;

    public Cards(double p, double t, String d, String a) {
        payLimit = p;
        takeLimit = t;
        dead = d;
        accNbr = a;

    }
    public double getPayLimit(){
        return payLimit;
    }
    public double getTakeLimit(){
        return takeLimit;
    }
    public String getDead(){
        return dead;
    }
    public String getAccNbr(){
        return accNbr;
    }
/*
    public void setPayLimit(double payLimit){
        this.payLimit = payLimit;
    }
    public void setTakeLimit(double takeLimit){
        this.takeLimit = takeLimit;
    }
    public void setAccNbr(String  accNbr){
        this.accNbr = accNbr;
    }

 */
}
