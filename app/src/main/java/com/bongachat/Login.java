package com.bongachat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;

public class Login extends AppCompatActivity {


    TextView username1, password1, Regbtn, preview0;
    Button btnsignin1;

    String collectUser;

    public static String PREFS_NAME = "UserInfo";
    public static String PREFS_Id = "UserId";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //navigation bottom color
        // Apply the current day/night mode
        getDelegate().applyDayNight();

        // Set the NavigationBar color based on the theme
        setNavigationBarColor();

        //initial
        username1 = findViewById(R.id.username1);
        password1 = findViewById(R.id.password1);
        btnsignin1 = findViewById(R.id.btnsignin1);
        preview0 = findViewById(R.id.preview0);
        Regbtn = findViewById(R.id.Regbtn);



       // LottieAnimationView lt1 = findViewById(R.id.loading);
        Intent intent = new Intent(this, Uicount.class);
        Intent intent2 = new Intent(this, Register.class);


        Regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent2);
            }
        });




        btnsignin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //lt1.setVisibility(View.VISIBLE);

                if (isConnected()==false) {
                    preview0.setVisibility(View.VISIBLE);
                    preview0.setText("No Internet connection");
                    preview0.setError("No Internet connection");
                    preview0.requestFocus();
                    //pop up confirm receivers identification
                    return;
                }else {
                    preview0.setVisibility(View.INVISIBLE);
                }

                Connection connection = connectionclass();
                if (connection != null) {

                    try {
                        String sqlcheck = "SELECT * FROM users WHERE  username='" + username1.getText().toString() + "' AND CONVERT(VARCHAR, password) ='" + password1.getText().toString() + "'";
                        Statement st0 = connection.createStatement();
                        ResultSet rs0 = st0.executeQuery(sqlcheck);


                        try {

                            if (rs0.next()) {
                                collectUser = username1.getText().toString();
                                SharedPreferences sharedPreferences = getSharedPreferences(Login.PREFS_NAME, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("hasLogged",true);
                                editor.putString("user",collectUser);
                                editor.commit();
                                startActivity(intent);
                                finish();

                            } else {
                                preview0.setVisibility(View.VISIBLE);
                                preview0.setText("Wrong Credentials");
                            }

                        } catch (SQLIntegrityConstraintViolationException e) {
                            preview0.setText(new StringBuilder().append("exists").append(e.getMessage()).toString());
                        }



                    } catch (Exception e) {
                        Log.e("Error", e.getMessage());

                    }
                }


            }
        });

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
    public Connection connectionclass() {
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