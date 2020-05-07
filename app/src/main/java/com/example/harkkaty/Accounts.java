package com.example.harkkaty;

public class Accounts {

    private double money;
    private String user, accNumber, accType, frozen;
    private  String card;

    public Accounts(String u, String ac, String at, double m, String f, String c){
        //For accounts
        user = u;
        accNumber = ac;
        accType = at;
        money = m;
        frozen = f;
        card = c;



    }
    public String getUser(){
        return user;
    }
    public String getAccNumber(){
        return accNumber;
    }
    public String  getAccType(){
        return accType;
    }
    public double getMoney(){
        return money;
    }
    public String getFrozen(){
        return frozen;
    }
    public String getCard(){
        return card;
    }
    /*
    public void setMoney(double money){
        this.money = money;
    }
    public void setType(String accType){
        this.accType = accType;
    }
    public void setFrozen(String  frozen){
        this.frozen = frozen;
    }
    public void setCard(String  card){
        this.card = card;
    }

     */

}


