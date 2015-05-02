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

public class ForgotPassword extends ActionBarActivity {

    private EditText emailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Parse.initialize(this, "DkI8q1Wpi9YWTetsbwd5aGyQEOXhFn8pw73TgdJ4", "hCsJGe732Vx6BbqkiGFvvk0m6n8ZrR4lJEWeLiOc");
        //ParseAnalytics.trackAppOpened(getIntent());
        ParseObject.registerSubclass(User.class);

        emailView = (EditText) findViewById(R.id.emailPassword);
        emailView.setError(null);

        final Button ForgotPasswordButton = (Button) findViewById(R.id.sendMail);
        ForgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final String email = emailView.getText().toString();

                if (email.equals("")) {
                    emailView.setError("The email field can not be empty");
                } else if (!email.contains("@")) {
                    emailView.setError("The email is not valid");
                }

                // Create an explicit Intent for starting the Buses
                // Activity
                else {

                    ParseQuery<User> query = ParseQuery.getQuery("User");//class name is User
                    query.whereEqualTo("email", email);

                    query.findInBackground(new FindCallback<User>() {
                        public void done(List<User> userList, ParseException e) {
                            if (e == null) {
                                //Log.d("score", "Retrieved " + userList.size() + " scores");
                                if (userList.size() == 0) {
                                    emailView.setError("The email is not valid");
                                } else {
                                    String p = generateRandom(9); // the random password
                                    //userList().setPassword(p);
                                    userList.get(0).setPassword(p);
                                    userList.get(0).saveInBackground();
                                    Toast toast = Toast.makeText(ForgotPassword.this, "Password Sent", Toast.LENGTH_SHORT);
                                    toast.show();

                                    Intent ForgotPasswordIntent;
                                    ForgotPasswordIntent = new Intent(ForgotPassword.this, Home.class);
                                    // Use the Intent to start the Buses Activity
                                    startActivity(ForgotPasswordIntent);
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
        getMenuInflater().inflate(R.menu.menu_forgot_password, menu);
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

    public static String generateRandom(int length) {
        StringBuffer buffer = new StringBuffer();
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int charactersLength = characters.length();

        for (int i = 0; i < length; i++) {
            double index = Math.random() * charactersLength;
            buffer.append(characters.charAt((int) index));
        }
        return buffer.toString();
    } //http://syntx.io/how-to-generate-a-random-string-in-java/
}