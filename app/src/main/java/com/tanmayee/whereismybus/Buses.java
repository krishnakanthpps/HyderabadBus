package com.tanmayee.whereismybus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Buses extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    private CheckBox acView;
    private Spinner busRouteSpinner;
    private SeekBar seekBarView;
    private TextView textView;
    private Spinner busNameSpinner;
    private String directionObtained;
    private String busNameChoosen;
    SessionManager session;
    List<String> buslist = new ArrayList<String>();
    List<String> directionList = new ArrayList<String>();
    private ProgressDialog pDialog;

    public void setDirectionObtained(String direction){
        this.directionObtained = direction;
    }
    public void setBusNameChoosen(String busChoosen){
        this.busNameChoosen = busChoosen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buses);

        Parse.initialize(this, "DkI8q1Wpi9YWTetsbwd5aGyQEOXhFn8pw73TgdJ4", "hCsJGe732Vx6BbqkiGFvvk0m6n8ZrR4lJEWeLiOc");
        //ParseAnalytics.trackAppOpened(getIntent());
        ParseObject.registerSubclass(BusRoute.class);

        seekBarView = (SeekBar) findViewById(R.id.seekBar);
        //seekBarView.setOnSeekBarChangeListener(this);

        acView = (CheckBox) findViewById(R.id.AC);
        textView = (TextView) findViewById(R.id.fullness);

        session = new SessionManager(getApplicationContext());

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        final String email = user.get(SessionManager.KEY_EMAIL);
        String points = user.get(SessionManager.KEY_POINTS); //have Not updated this - Be careful!!

        acView.setError(null);
        textView.setError(null);

        busNameSpinner = (Spinner) findViewById(R.id.busName);

        buslist.add("Select the Bus");
        directionList.add("Bus going towards");
        ParseQuery<BusRoute> queryBus = ParseQuery.getQuery("BusRoute");
        queryBus.whereNotEqualTo("BusName", "IRA");

        Log.d("ADebugTag", "Line");

//tanmayee - Not getting into the below loop. Please check once..

        try {
            List<BusRoute> BusList = queryBus.find();
            Log.d("ADebugTag", "Reached here0");
            for (int i = 0; i < BusList.size(); i++) {
                buslist.add(BusList.get(i).getString("BusName"));
                Log.d("ADebugTag", "Reached here");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.d("ADebugTag", "Reached here");

        ArrayAdapter <String> dataAdapter = new ArrayAdapter<String>(Buses.this,android.R.layout.simple_spinner_item, buslist);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        busNameSpinner.setAdapter(dataAdapter);
        Log.d("ADebugTag", "Reached here : " + busNameSpinner.getCount());

        busNameSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener(){

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(parent.getItemAtPosition(position)!=null) {
                            String busNameChoose = parent.getItemAtPosition(position).toString();
                            Log.d("ADebugTag", "bus name is : " + busNameChoose);
                            setBusNameChoosen(busNameChoose);
                            setSecondSpinner(busNameChoose);
                            Log.d("ADebugTag", "Update : " + busNameChoosen);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent){}
                }
        );
        Log.d("ADebugTag", "Update Tarun : " + busNameChoosen);

        textView.setText("Occupancy: " + seekBarView.getProgress() + "%");
        seekBarView.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                progress = progressValue;
                textView.setText("Occupancy: " + progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textView.setText("Occupancy: " + progress + "%");
            }

        });

        ParseObject.registerSubclass(Bus.class);

        final Button subButton = (Button) findViewById(R.id.submitButton);
        subButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //final String direction = directionObtained;
                final String busName = busNameChoosen;
                Log.d("ADebugTag", "Final BusName : " + busName);
                Log.d("ADebugTag", "Update : " + busNameChoosen);

                final String direction = directionObtained;
                final int fullN = seekBarView.getProgress();
                final boolean ac = acView.isChecked();
                final boolean verified = false;
                textView.setText("Occupancy: " + seekBarView.getProgress() + "%");

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String time = sdf.format(new Date());
                //double lat = 12.8447593;
                //double lon = 77.6633234;
                double lat = getIntent().getExtras().getDouble("latitude");
                double lon = getIntent().getExtras().getDouble("longitude");
                ParseGeoPoint point = new ParseGeoPoint(lat, lon);
                Bus newBus = new Bus();
                newBus.setName(busName);
                newBus.setDirection(direction);
                newBus.setFull(fullN);
                newBus.setTime(time);
                newBus.setAc(ac);
                newBus.setIsVerified(verified);
                newBus.setPosition(point);

                //Process Dialog
                pDialog = new ProgressDialog(Buses.this);
                pDialog.setMessage("Recording Bus...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();

                newBus.saveInBackground();

                ParseObject.registerSubclass(User.class);
                ParseQuery<User> queryUser = ParseQuery.getQuery("User");
                queryUser.whereEqualTo("email", email);
                try {
                    List<User> UserList = queryUser.find();
                    int updatedPoints = UserList.get(0).getPoints() + 10;
                    UserList.get(0).put("Points",updatedPoints);

                    UserList.get(0).saveInBackground();
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                Toast toast = Toast.makeText(Buses.this, "Bus recorded. Thanks :D", Toast.LENGTH_SHORT);

                pDialog.dismiss();

                toast.show();
                Intent busesIntent;
//                    busesIntent = new Intent(Buses.this, Home.class);
                busesIntent = new Intent(Buses.this, MapView.class);
                busesIntent.putExtra("latitude", getIntent().getExtras().getDouble("latitude"));
                busesIntent.putExtra("longitude", getIntent().getExtras().getDouble("longitude"));

                // Use the Intent to start the Buses Activity
                startActivity(busesIntent);
                finish();
            }

        });

    }

    public void setSecondSpinner(String choosen){

        ParseQuery<BusRoute> query = ParseQuery.getQuery("BusRoute");//class name is User
        query.whereEqualTo("BusName", choosen);
        Log.d("ADebugTag", "Busname Tanmayee : " + choosen);

        try {
            List<BusRoute> RouteList = query.find();
            busRouteSpinner = (Spinner) findViewById(R.id.spinnerDirection);
            if (RouteList.size() > 0) {

                if(directionList.size() > 1)
                {

                    directionList.set(1,RouteList.get(0).getSource());
                    directionList.set(2,RouteList.get(0).getDestination());
                }
                else{
                    System.out.println("rrrrrrrrrrr");
                    directionList.add(1, RouteList.get(0).getSource());
                    directionList.add(2,RouteList.get(0).getDestination());
                }
                System.out.println("xxxxxxxx: ");
                System.out.println(directionList.get(1));
                ArrayAdapter<String> dataAdapterforRoute;
                dataAdapterforRoute = new ArrayAdapter<String>(Buses.this, android.R.layout.simple_spinner_item, directionList);

                dataAdapterforRoute.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                busRouteSpinner.setAdapter(dataAdapterforRoute);

                busRouteSpinner.setOnItemSelectedListener(
                        new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                    if (parent.getItemAtPosition(position).toString().equals("Bus going towards")) {
                                        Toast toastBus = Toast.makeText(Buses.this, "Please choose the direction", Toast.LENGTH_SHORT);
                                        toastBus.show();

                                }
                                else {

                                        String direction = parent.getItemAtPosition(position).toString();
                                        setDirectionObtained(direction);
                                        Log.d("else part:",direction );

                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        }
                );
            }
        }
        catch(ParseException e){
            e.printStackTrace();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_buses, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        session = new SessionManager(getApplicationContext());
        //noinspection SimplifiableIfStatement
        if (id == R.id.Logout) {
            session.logoutUser();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}