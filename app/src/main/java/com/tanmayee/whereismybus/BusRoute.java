package com.tanmayee.whereismybus;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("BusRoute")
/**
 * Created by Tarun Tater on 4/11/2015.
 */
public class BusRoute extends ParseObject {

    String busName;
    String source;
    String destination;

    public void setBusName(String busName){
        put("BusName",busName);
    }

    public void setDestination(String destination){
        put("Destination",destination);
    }

    public void setStart(String source){
        put("Source",source);
    }

    public String getBusName(){
        return getString("BusName");
    }

    public String getSource(){
        return getString("Source");
    }

    public String getDestination(){
        return getString("Destination");
    }
}
