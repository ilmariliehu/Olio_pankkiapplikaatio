package com.example.harkkaty;

public class Users {

    private String username, password, name, address, email, salt;

    public Users (String u, String  p, String n, String a, String e, String s){
        username = u;
        password = p;
        name = n;
        address = a;
        email = e;
        salt = s;
    }

    public String getUsername(){ return username; }
    public String  getPassword(){
        return password;
    }
    public String getName(){
        return name;
    }
    public String getAddress(){
        return address;
    }
    public String getEmail(){
        return email;
    }
    public String getSalt(){
        return salt;
    }
/*
    public void setAddress(String address){
        this.address = address;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setSalt(String salt){
        this.salt = salt;
    }
*/

}
