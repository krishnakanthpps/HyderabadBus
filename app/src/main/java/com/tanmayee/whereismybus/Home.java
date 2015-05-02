package com.tanmayee.whereismybus;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.lang.Object;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Home extends ActionBarActivity {

    private EditText emailView;
    private EditText passwordView;
    private CheckBox rememberMeView;
    private CheckBox operatorView;
    // Session Manager Class
    SessionManager session;
    public ProgressDialog pDialog;

    public ProgressDialog getpDialog() {
        return pDialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Parse.initialize(this, "DkI8q1Wpi9YWTetsbwd5aGyQEOXhFn8pw73TgdJ4", "hCsJGe732Vx6BbqkiGFvvk0m6n8ZrR4lJEWeLiOc");
        //ParseAnalytics.trackAppOpened(getIntent());
        ParseObject.registerSubclass(User.class);

        // Session Manager
        session = new SessionManager(getApplicationContext());

        emailView = (EditText) findViewById(R.id.email);
        passwordView = (EditText) findViewById(R.id.password);
        operatorView = (CheckBox) findViewById(R.id.operator);
        emailView.setError(null);

        final Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final String Email = emailView.getText().toString();
                final String password = passwordView.getText().toString();
                final boolean op = operatorView.isChecked();
                if (Email.equals("")) {
                    emailView.setError("The email field can not be empty");
                } else if (password.equals("")) {
                    passwordView.setError("The password field can not be empty");
                } else if (!Email.contains("@")) {
                    emailView.setError("The email is not valid");
                }

                // Create an explicit Intent for starting the Buses
                // Activity
                else {

                    ParseQuery<User> query = ParseQuery.getQuery("User");//class name is User
                    query.whereEqualTo("email", Email);

                    pDialog = new ProgressDialog(Home.this);
                    pDialog.setMessage("Logging in...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();

                    query.findInBackground(new FindCallback<User>() {
                        public void done(List<User> userList, ParseException e) {
                            if (e == null) {
                                //Log.d("score", "Retrieved " + userList.size() + " scores");
                                if (userList.size() == 0) {
                                    emailView.setError("The email is not valid");
                                } else {
                                    if (password.equals(userList.get(0).getPassword()) && op == (userList.get(0).getIsOperator())) {
                                        if (op == false) {

                                            session.createLoginSession(Email, password);
                                            String username = userList.get(0).getUserName();

                                            Intent loginIntent;
                                            loginIntent = new Intent(Home.this, Location_Report.class);
                                            loginIntent.putExtra("username",username);
                                            // Use the Intent to start the Buses Activity
                                            startActivity(loginIntent);
                                        } else {
                                            Intent loginIntent;
                                            loginIntent = new Intent(Home.this, BusesOp.class);
                                            // Use the Intent to start the Buses Activity
                                            startActivity(loginIntent);
                                        }
                                    } else if (password.equals(userList.get(0).getPassword()) && op != (userList.get(0).getIsOperator())) {
                                        operatorView.setError("Not operator");
                                        Toast toast = Toast.makeText(Home.this, "Sorry, you are not an operator", Toast.LENGTH_SHORT);
                                        toast.show();
                                    } else {
                                        passwordView.setError("The password is incorrect");
                                    }
                                }
                            } else {
                                Log.d("score", "Error: " + e.getMessage());
                            }
                            pDialog.dismiss();
                        }
                    });
                }
            }
        });

        final TextView RegisterButton = (TextView) findViewById(R.id.register);
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Create an explicit Intent for starting the LoginScreen
                // Activity
                Intent RegisterIntent;
                RegisterIntent = new Intent(Home.this, Register.class);
                // Use the Intent to start the LoginScreen Activity
                startActivity(RegisterIntent);

            }


        });

        final TextView ForgotButton = (TextView) findViewById(R.id.lostPassword);
        ForgotButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Create an explicit Intent for starting the LoginScreen
                // Activity
                Intent ForgotIntent;
                ForgotIntent = new Intent(Home.this, ForgotPassword.class);
                // Use the Intent to start the LoginScreen Activity
                startActivity(ForgotIntent);

            }


        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        //do nothing
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.Logout) {
            Intent loginIntent;
            loginIntent = new Intent(Home.this, Home.class);
            // Use the Intent to start the Buses Activity
            startActivity(loginIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}