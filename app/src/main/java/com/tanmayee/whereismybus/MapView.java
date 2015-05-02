package com.tanmayee.whereismybus;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MapView extends ActionBarActivity implements OnMapReadyCallback{

    private GoogleMap googleMap;
    TextView textView;
    private EditText filterView;
    GoogleApiClient mGoogleApiClient;
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    List<Bus> all_buses;
    private Menu myMenu;

    public void clearMap() {
        googleMap.clear();
    }

    public void resetUpdating() {
        // Get our refresh item from the menu
        MenuItem m = myMenu.findItem(R.id.action_refresh);
        if(m.getActionView()!=null)
        {
            // Remove the animation.
            m.getActionView().clearAnimation();
            m.setActionView(null);
        }
    }

    public void setAll_buses(List<Bus> all_buses) {
        this.all_buses = all_buses;
        System.out.println("In MapView: "+all_buses);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        Parse.initialize(this, "DkI8q1Wpi9YWTetsbwd5aGyQEOXhFn8pw73TgdJ4", "hCsJGe732Vx6BbqkiGFvvk0m6n8ZrR4lJEWeLiOc");
        ParseObject.registerSubclass(Bus.class);

        ParseQuery<Bus> query = ParseQuery.getQuery("Bus");//class name is User
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyDDmm_HHmmss");
        String time = sdf.format(new Date());

        filterView = (EditText) findViewById(R.id.busFilter);
        filterView.setError(null);

        final String busN = "-";
        query.whereNotEqualTo("busName", busN);
        System.out.println("I'm here");

        createMapView();
        addMarker();

        query.findInBackground(new FindCallback<Bus>() {
            public void done(List<Bus> buses, com.parse.ParseException e) {
                if (e == null) {
                    setAll_buses(buses);
                    System.out.println(buses);
                    addBusMarkers(buses);
                } else {

                }
            }
        });
        final Button reportButton = (Button) findViewById(R.id.report);
        final Button filterButton = (Button) findViewById(R.id.filter);

        reportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent reportIntent;
                reportIntent = new Intent(MapView.this, Buses.class);
                // Use the Intent to start the LoginScreen Activity
                reportIntent.putExtra("latitude", getIntent().getExtras().getDouble(LATITUDE));
                reportIntent.putExtra("longitude", getIntent().getExtras().getDouble(LONGITUDE));
                startActivity(reportIntent);

            }

        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO
                // Create an explicit Intent for starting the LoginScreen
                // Activity
                final String busName = filterView.getText().toString();
                if (busName.contains("-")) {
                    filterView.setError("The busName is invalid.");
                }
                else if (busName.equals("")) {
                    filterView.setError("No input specified");
                }
                else{
                    clearMap();
                    List<Bus> filtered_buses = new ArrayList<Bus>();
                    for (Bus bus: all_buses) {
                        System.out.println(bus.getBusName()+"\t"+busName);
                        if(bus.getBusName().equalsIgnoreCase(busName)) {
                            filtered_buses.add(bus);
                        }
                    }
                    System.out.println(filtered_buses);
                    addMarker();
                    addBusMarkers(filtered_buses);
                }
            }
        });
    }

    public void addBusMarkers(List<Bus> buses) {
        String text = Integer.toString(buses.size()) + " buses found";
        Toast noOfBuses = Toast.makeText(MapView.this, text, Toast.LENGTH_LONG);
        noOfBuses.show();

        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);

        if (null != googleMap) {
            for (Bus myBus : buses) {
                String info = Integer.toString(myBus.getFull()) + "% full ";
                if (myBus.getAc() == true) {
                    info = info + "AC to ";
                } else {
                    info = info + "Non AC to ";
                }
                info = info + myBus.getDirection();
                googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(myBus.getPosition().getLatitude(), myBus.getPosition().getLongitude()))
                                .title(myBus.getBusName())
                                .snippet(info)
                                .draggable(false).icon(bitmapDescriptor)
                );
            }
        }
    }

    public void addBusMarkers(){
        System.out.println("all_buses: " + all_buses);
        List<Bus> buses = new ArrayList<Bus>(all_buses);
        System.out.println("buses: "+ all_buses);
        String text = Integer.toString(buses.size()) + " buses found";
        Toast noOfBuses = Toast.makeText(MapView.this, text, Toast.LENGTH_LONG);
        noOfBuses.show();

        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);

        if (null != googleMap) {
            for (Bus myBus : buses) {
                String info = Integer.toString(myBus.getFull()) + "% full";
                if (myBus.getAc() == true) {
                    info = info + "AC";
                } else {
                    info = info + "Non AC";
                }
                info = info + myBus.getDirection();
                googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(myBus.getPosition().getLatitude(), myBus.getPosition().getLongitude()))
                                .title(myBus.getBusName())
                                .snippet(info)
                                .draggable(false).icon(bitmapDescriptor)
                );
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        // TODO write all stuff that must be done to a google map after it has been initialized
        map.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));
    }

    public void addMarker() {

        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                final double longitude = location.getLongitude();
                final double latitude = location.getLatitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        /** Make sure that the map has been initialised **/
        if (null != googleMap) {
            double myLatitude = getIntent().getExtras().getDouble(LATITUDE);
            double myLongitude = getIntent().getExtras().getDouble(LONGITUDE);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(myLatitude, myLongitude)));
            googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(myLatitude, myLongitude))
                            .title("You are here")
                            .draggable(false)
            );
        }
    }

    private void createMapView() {
        /**
         * Catch the null pointer exception that
         * may be thrown when initialising the map
         */
        try {
            if (null == googleMap) {
                googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

                /**
                 * If the map is still null after attempted initialisation,
                 * show an error to the user
                 */
                if (null == googleMap) {
                    Toast.makeText(getApplicationContext(),
                            "Error creating map", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (NullPointerException exception) {
            Log.e("mapApp", exception.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map_view, menu);
        myMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_refresh: {
                LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ImageView iv = (ImageView)inflater.inflate(R.layout.iv_refresh, null);
                Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate_refresh);
                rotation.setRepeatCount(Animation.INFINITE);
                iv.startAnimation(rotation);
                item.setActionView(iv);
                new UpdateTask(this).execute();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
