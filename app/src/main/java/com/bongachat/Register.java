package com.bongachat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class Register extends AppCompatActivity {

    TextView preview;
    Button signup, signin;

    // Assuming you have the following variables declared:
    String id; // contains the value of the ID
    EditText usernameEditText; // the input field for username
    EditText passwordEditText; // the input field for password
    EditText retypePasswordEditText; // the input field for re-typing password


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Intent intent1 = new Intent(Register.this, Login.class);

        //full screen
        //View decorView = getWindow().getDecorView();
        //WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        //change statusbar color
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //getWindow().setStatusBarColor(getColor(R.color.green00));

        //navigation bottom color
        // Apply the current day/night mode
        getDelegate().applyDayNight();

        // Set the NavigationBar color based on the theme
        setNavigationBarColor();


        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        retypePasswordEditText = findViewById(R.id.repassword);
        preview = findViewById(R.id.preview);
        preview.setVisibility(View.INVISIBLE);
        //id = findViewById(R.id.id);

        //get the id
        int max = 999;
        int min = 100;
        Random random = new Random();
        String i = String.valueOf(random.nextInt(max + min) + min);

        int max2 = 799;
        int min2 = 199;
        Random random2 = new Random();
        String i2 = String.valueOf(random2.nextInt(max2 + min2) + min);

        id = i+i2;


        Connection connection = connectionClass();
        Button loginButton = findViewById(R.id.btnsignup);
        loginButton.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String retypePassword = retypePasswordEditText.getText().toString().trim();

                if (isConnected()==false) {
                    preview.setVisibility(View.VISIBLE);
                    preview.setText("No Internet connection");
                    preview.setError("No Internet connection");
                    preview.requestFocus();
                    //pop up confirm receivers identification
                    return;
                }else {
                    preview.setVisibility(View.INVISIBLE);
                }

                // First, check if the username field is empty
                if (TextUtils.isEmpty(username)) {
                    usernameEditText.setError("Username is required");
                    usernameEditText.requestFocus();
                    return;
                }

                // Next, check if the username already exists in the database
                boolean usernameExists = checkUsernameExists(username);
                if (usernameExists) {
                    usernameEditText.setError("Username already exists");
                    usernameEditText.requestFocus();
                    return;
                }

                // Then, check if the password fields are empty
                if (TextUtils.isEmpty(password)) {
                    passwordEditText.setError("Password is required");
                    passwordEditText.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(retypePassword)) {
                    retypePasswordEditText.setError("Please re-type your password");
                    retypePasswordEditText.requestFocus();
                    return;
                }

                // Finally, check if the passwords match
                if (!password.equals(retypePassword)) {
                    retypePasswordEditText.setError("Passwords do not match");
                    retypePasswordEditText.requestFocus();
                    return;
                }

                // If all checks pass, insert the new user's details into the table
                try {
                    String query = "INSERT INTO users (id, username, password) VALUES (?, ?, ?)";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, id);
                    preparedStatement.setString(2, username);
                    preparedStatement.setString(3, password);
                    preparedStatement.executeUpdate();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    String query = "INSERT INTO wallet (id, username, balance) VALUES (?, ?, ?)";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, id);
                    preparedStatement.setString(2, username);
                    preparedStatement.setString(3, "0.00");
                    preparedStatement.executeUpdate();

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                // Open another activity
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                preview.setVisibility(View.VISIBLE);
                preview.setText("Created successful");
                finish();

            }
        });







    }
    // Helper function to check if the username already exists in the database
    private boolean checkUsernameExists(String username) {
        Connection connection = connectionClass();
        // Use SQL query to check if the username exists in the database
        // Assuming you have already established a connection to the MSSQL database
        try {
            String query = "SELECT * FROM users WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next(); // Returns true if username already exists
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDelegate().applyDayNight();
        setNavigationBarColor();
    }

    private void setNavigationBarColor() {
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                switch (currentNightMode) {
                    case Configuration.UI_MODE_NIGHT_YES:
                        // Dark theme is active, set dark color for NavigationBar
                        getWindow().setNavigationBarColor(getResources().getColor(R.color.dark0));
                        break;
                    case Configuration.UI_MODE_NIGHT_NO:
                        // Light theme is active, set light color for NavigationBar
                        getWindow().setNavigationBarColor(getResources().getColor(R.color.light0));
                        break;
                }
            }
        }
    }

    @SuppressLint("NewApi")
    public Connection connectionClass() {
        Connection con = null;
        String ip = "SQL8005.site4now.net", port = "1433", username = "db_a9af88_bonga_admin", password = "Barnabas297$$", databasename = "db_a9af88_bonga";
        StrictMode.ThreadPolicy tp = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(tp);
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            String connectionUrl = "jdbc:jtds:sqlserver://" + ip + ":" + port + ";databasename=" + databasename + ";User=" + username + ";password=" + password + ";";
            con = DriverManager.getConnection(connectionUrl);
        } catch (Exception exception) {
            Log.e("Error", exception.getMessage());
        }
        return con;

    }
    boolean isConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo!=null){
            return networkInfo.isConnected();
        }else
            return false;
    }
}