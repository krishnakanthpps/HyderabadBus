package com.tanmayee.whereismybus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
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

public class Register extends ActionBarActivity {

    private EditText regUserNameView;
    private EditText regUserPasswordView;
    private EditText regUserConfirmPasswordView;
    private EditText regUserEmailView;
    public ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Parse.initialize(this, "DkI8q1Wpi9YWTetsbwd5aGyQEOXhFn8pw73TgdJ4", "hCsJGe732Vx6BbqkiGFvvk0m6n8ZrR4lJEWeLiOc");
        //ParseAnalytics.trackAppOpened(getIntent());
        ParseObject.registerSubclass(User.class);

        regUserNameView = (EditText) findViewById(R.id.regUserName);
        regUserEmailView = (EditText) findViewById(R.id.regUserEmail);
        regUserPasswordView = (EditText) findViewById(R.id.regUserPassword);
        regUserConfirmPasswordView = (EditText) findViewById(R.id.regUserConfirmPassword);

        regUserNameView.setError(null);
        regUserEmailView.setError(null);
        regUserPasswordView.setError(null);
        regUserConfirmPasswordView.setError(null);

        final Button registerButton = (Button) findViewById(R.id.regButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final String userName = regUserNameView.getText().toString();
                final String email = regUserEmailView.getText().toString();
                final String password = regUserPasswordView.getText().toString();
                final String confirm = regUserConfirmPasswordView.getText().toString();

                if(userName.equals(""))
                {
                    regUserNameView.setError("The name field can not be empty");
                }

                else if(email.equals(""))
                {
                    regUserEmailView.setError("The email field can not be empty");
                }

                else if(password.equals(""))
                {
                    regUserPasswordView.setError("The password field can not be empty");
                }
                else if(confirm.equals(""))
                {
                    regUserConfirmPasswordView.setError("The Confirm password field can not be empty");
                }

                else if(!password.equals(confirm))
                {
                    regUserConfirmPasswordView.setError("The password does not match");
                }
                else if(!email.contains("@"))
                {
                    regUserEmailView.setError("The email is not valid");
                }
                else if(password.length() < 8)
                {
                    regUserPasswordView.setError("The password should be of atleast length 8.");
                }
                // Create an explicit Intent for starting the Buses
                // Activity
                else {
                    ParseQuery<User> query = ParseQuery.getQuery("User");//class name is User
                    query.whereEqualTo("email", email);

                    pDialog = new ProgressDialog(Register.this);
                    pDialog.setMessage("Registering...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();

                    query.findInBackground(new FindCallback<User>() {
                        public void done(List<User> userList, ParseException e) {
                            if (e == null) {
                                //Log.d("score", "Retrieved " + userList.size() + " scores");
                                if(userList.size() != 0)
                                {
                                    regUserEmailView.setError("The email is already used");
                                }
                                else{
                                    User newUser = new User();
                                    newUser.setUserName(userName);
                                    newUser.setEmail(email);
                                    newUser.setPassword(password);
                                    newUser.setPoints(0);
                                    newUser.setIsOperator(false);//Default is false
                                    newUser.saveInBackground();
                                    Toast toast = Toast.makeText(Register.this,"Successfully Registered",Toast.LENGTH_SHORT);
                                    toast.show();
                                    Intent registerIntent;
                                    registerIntent = new Intent(Register.this, Home.class);
                                    // Use the Intent to start the Buses Activity
                                    startActivity(registerIntent);
                                    finish();
                                }
                            }
                            else {
                                Log.d("score", "Error: " + e.getMessage());
                            }
                            pDialog.dismiss();
                        }
                    });
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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