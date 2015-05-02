package com.tanmayee.whereismybus;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by tanmayee on 11/4/15.
 */
public class UpdateTask extends AsyncTask<Void, Void, Void> {

    private Context mCon;
    private List<Bus> all_buses;

    public UpdateTask(Context con) {
        mCon = con;
    }

    private void setbuses(List<Bus> buses) {
        this.all_buses = new ArrayList<>(buses);
        System.out.println("In UpdateTask : "+this.all_buses);
    }

    @Override
    protected Void doInBackground(Void... nope) {
        //Parse.initialize(this, "DkI8q1Wpi9YWTetsbwd5aGyQEOXhFn8pw73TgdJ4", "hCsJGe732Vx6BbqkiGFvvk0m6n8ZrR4lJEWeLiOc");
        Log.d("tag", "Entered Refresh Button");

        ParseObject.registerSubclass(Bus.class);

        ParseQuery<Bus> query = ParseQuery.getQuery("Bus");//class name is User
        Log.d("tag", "Entered query 1");
        query.findInBackground(new FindCallback<Bus>() {
            public void done(List<Bus> buses, com.parse.ParseException e) {
                if (e == null) {
                    try {
                        Log.d("tag", "Entered query");
                        setbuses(buses);
                        Log.d("no of buses", Integer.toString(buses.size()));
                        //addBusMarkers(buses);
                        Log.d("Updatetask", String.valueOf((buses)));
                        ((MapView) mCon).setAll_buses(buses);
                        ((MapView) mCon).clearMap();
                        ((MapView) mCon).addMarker();
                        ((MapView) mCon).addBusMarkers();
                    }
                    catch(NullPointerException npe) {
                        npe.printStackTrace();
                    }
                } else {
                    e.printStackTrace();
                    e.getMessage();
                }
            }
        });
        return null;
    }

    @Override
    protected void onPostExecute(Void nope) {
        // Give some feedback on the UI.
        // Change the menu back

        ((MapView) mCon).resetUpdating();
    }
}
