package com.tanmayee.whereismybus;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


public class Filter extends ActionBarActivity {

    private EditText filterView;
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        Parse.initialize(this, "DkI8q1Wpi9YWTetsbwd5aGyQEOXhFn8pw73TgdJ4", "hCsJGe732Vx6BbqkiGFvvk0m6n8ZrR4lJEWeLiOc");
        //ParseAnalytics.trackAppOpened(getIntent());
        ParseObject.registerSubclass(Bus.class);
        filterView = (EditText) findViewById(R.id.busFilter);
        filterView.setError(null);

        final Button filterBusButton = (Button) findViewById(R.id.filterButton);
        filterBusButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final String busName = filterView.getText().toString();

                if (busName.equals("")) {
                    filterView.setError("The busName field can not be empty");
                } else if (busName.contains("-")) {
                    filterView.setError("The busName is invalid.");
                }
                else {

                    ParseQuery<Bus> query = ParseQuery.getQuery("Bus");//class name is User
                    query.whereEqualTo("busName", busName);

                    query.findInBackground(new FindCallback<Bus>() {
                        public void done(List<Bus> busList, ParseException e) {
                            if (e == null) {
                                //Log.d("score", "Retrieved " + userList.size() + " scores");
                                if (busList.size() == 0) {
                                    filterView.setError("No buses reported");
                                } else {
                                    Toast toast = Toast.makeText(Filter.this, "Filtering", Toast.LENGTH_SHORT);
                                    toast.show();

                                    Intent filterIntent;
                                    filterIntent = new Intent(Filter.this, MapView.class);
                                    filterIntent.putExtra("latitude", getIntent().getExtras().getDouble(LATITUDE));
                                    filterIntent.putExtra("longitude", getIntent().getExtras().getDouble(LONGITUDE));
                                    // Use the Intent to start the Buses Activity
                                    startActivity(filterIntent);
                                    finish();//assumed to be for back to back :P
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}