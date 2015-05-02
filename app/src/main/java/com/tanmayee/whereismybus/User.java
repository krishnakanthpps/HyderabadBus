package com.tanmayee.whereismybus;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("User")

/**
 * Created by Tarun Tater on 3/28/2015.
 */
public class User extends ParseObject{

    String name;
    String password;
    String email;
    int Points;
    boolean IsOperator;
    //default constructor
    public User(){
    }
    public void setUserName(String name){
        put("username",name);
    }

    public void setPassword(String password){
        put("password",password);
    }

    public void setEmail(String email){
        put("email",email);
    }
    public void setPoints(int Points){
        put("Points",Points);
    }
    public void setIsOperator(boolean IsOperator){
        put("IsOperator",IsOperator);
    }

    public String getUserName(){
        return getString("username");
    }
    public String getEmail(){
        return getString("email");
    }
    public String getPassword(){
        return getString("password");
    }
    public boolean getIsOperator(){
        return getBoolean("IsOperator");
    }
    public int getPoints(){
        return getInt("Points");
    }
    @Override
    public String toString() {
        return "User{" +
                ", name='" + this.getUserName() + '\'' +
                ", Points='" + String.valueOf(this.getPoints()) + '\'' +
                '}';
    }
}