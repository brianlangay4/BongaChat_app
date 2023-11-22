package com.bongachat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class verification_page extends AppCompatActivity {


    TextView info;
    EditText full_name;
    EditText id;
    Button upload_id;
    Button submit;
    String Tid;
    String collectedUser;

    private static String user_id;
    public static String PREFS_NAME="LogoutInfo";
    public static String PREFS_Id="LogoutInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_page);

        //init
        info = findViewById(R.id.info2);
        full_name = findViewById(R.id.full_name);
        id = findViewById(R.id.age);
        submit = findViewById(R.id.submit);
        Connection connection = connectionClass();

        //get the id
        int max = 888888;
        int min = 12222;
        Random random = new Random();
        String i = String.valueOf(random.nextInt(max + min) + min);

        int max2 = 999777;
        int min2 = 10000;
        Random random2 = new Random();
        String i2 = String.valueOf(random2.nextInt(max2 + min2) + min);

        Tid = i+i2;

        //Get time
        // Get the current date and time
        Date currentDate = new Date();
        // Define the desired date and time format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Format the date and time
        String formattedDateTime = dateFormat.format(currentDate);




        //current user
        SharedPreferences shared = getSharedPreferences("UserInfo", MODE_PRIVATE);
        collectedUser = shared.getString("user", "");

        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String full = full_name.getText().toString().trim();
                String age = id.getText().toString().trim();

                if (TextUtils.isEmpty(full)) {
                    full_name.setError("Full name required");
                    full_name.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(age)) {
                    id.setError("Please Enter your Age");
                    id.requestFocus();
                    return;
                }
                try {
                    String query = "INSERT INTO verified (id, username, fullname, sect) VALUES (?, ?, ?, ?)";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, Tid);
                    preparedStatement.setString(2, collectedUser);
                    preparedStatement.setString(3, full);
                    preparedStatement.setString(4, age);
                    preparedStatement.executeUpdate();

                    Intent intent = new Intent(verification_page.this, Uicount.class);
                    startActivity(intent);
                    info.setText("verified successful");
                    Toast.makeText(verification_page.this, "verified successful", Toast.LENGTH_SHORT).show();


                } catch (SQLException e) {
                    e.printStackTrace();
                }

                /*// Open another activity
                Intent intent = new Intent(verification_page.this, Home.class);
                startActivity(intent);
                info.setText("verified successful");
                Toast.makeText(verification_page.this, "verified successful", Toast.LENGTH_SHORT).show();
                finish();
            */
            }
        });


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
}