package com.tanmayee.whereismybus;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

/**
 * Created by Tarun Tater on 4/1/2015.
 */

@ParseClassName("Bus")

public class Bus extends ParseObject {

    String name;
    String direction;
    int full;
    boolean ac;
    ParseGeoPoint position;
    //double latitude;
    //double longitude;
    String time;
    boolean IsVerified;

    //default constructor
    public Bus(){
    }
    public void setName(String name){
        put("busName",name);
    }
    /* public void setLatitude(double latitude){
         put("latitude",latitude);
     }

     public void setLongitude(double longitude){
         put("longitude",longitude);
     }
 */
    public void setTime(String time){
        put("time",time);
    }
    public void setDirection(String direction){
        put("direction",direction);
    }

    public void setFull(int full){
        put("full",full);
    }
    public void setPosition(ParseGeoPoint position){
        put("position",position);
    }

    public void setAc(boolean ac){
        put("ac",ac);
    }
    public void setIsVerified(boolean isVerified){
        put("IsVerified",isVerified);
    }

    public String getBusName(){
        return getString("busName");
    }

    public String getDirection(){
        return getString("direction");
    }

    public String getTime(){
        return getString("time");
    }
    public int getFull(){
        return getInt("full");
    }
    public ParseGeoPoint getPosition(){
        return getParseGeoPoint("position");
    }
    public boolean getAc(){
        return getBoolean("ac");
    }
    public boolean getIsVerified(){
        return getBoolean("IsVerified");
    }
    /*  public double getLatitude(){
          return getDouble("latitude");
      }
      public double getLongitude(){
          return getDouble("longitude");
      }
  */
    public String toString() {
        return "Bus{" +
                ", name='" + this.getBusName() + '\'' +
                ", Fullness='" + String.valueOf(this.getFull()) + '\'' +
                '}';
    }
}