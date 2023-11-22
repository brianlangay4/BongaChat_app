package com.bongachat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.sql.Connection;
import java.sql.DriverManager;

public class Uicount extends AppCompatActivity {

    ViewPager2 viewPager2;
    ViewPagerAdapter viewPagerAdapter;
    BottomNavigationView bottomNavigationView;

    String collectedUser;

    private ThemeViewModel themeViewModel;
    private SharedPreferences sharedPreferences;
    //private String selectedTheme;
    @Override
    public void onStart() {
        super.onStart();


        // Apply theme-related changes based on the retrieved theme value
       // performActionBasedOnTheme(selectedTheme);

        //calling our data fetcher for theme
        StringValueManager stringManager = new StringValueManager(this);

        String theme = "default";
        // To retrieve the current value
        String currentValue = stringManager.getValue();
        if(currentValue == null){
            theme = "default";
        } else if (currentValue != null) {
            theme = currentValue;
        }
        if(theme.equals("default")){
            bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.b2));

        } else if (theme.contains("rown")) {
            //brown theme apply here
            brown_theme();

        }else if (theme.contains("fault")) {
            //default theme apply there
            default_theme();

        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uicount);

        Toast.makeText(this, "welcome", Toast.LENGTH_SHORT).show();


        //themeViewModel = new ViewModelProvider(this).get(ThemeViewModel.class);






        //Intent intent = new Intent(this, MyService1.class);
        // startService(intent);

        SharedPreferences shared = getSharedPreferences("UserInfo", MODE_PRIVATE);
        collectedUser = shared.getString("user", "");


        //initial
        bottomNavigationView = findViewById(R.id.bottomNav);
        viewPager2 = findViewById(R.id.viewPagerL);

        viewPagerAdapter = new ViewPagerAdapter(this);

        viewPager2.setAdapter(viewPagerAdapter);

        //customs
        // bottomNavigationView.setItemActiveIndicatorColor(ColorStateList.valueOf(getColor(R.color.?att/colorSecondary)));

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.Feeds) {
                    viewPager2.setCurrentItem(0);
                } else if (id == R.id.chat) {
                    viewPager2.setCurrentItem(1);
                } else if (id == R.id.wallet) {
                    viewPager2.setCurrentItem(2);
                } else if (id == R.id.Settings) {
                    viewPager2.setCurrentItem(3);
                }

                return false;

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.Feeds).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.chat).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.wallet).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.Settings).setChecked(true);
                        break;
                }
                super.onPageSelected(position);

            }
        });

      /*  // new CheckAndUpdatePlayerIDTask().execute();

        Intent intent = new Intent(this, DatabaseIntentService.class);
        startService(intent);*/


    }


    private void default_theme(){
        Configuration configuration = getResources().getConfiguration();


        if ((configuration.uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            // Dark mode is enabled

            //status up and nav
            setStatusBarColor(getResources().getColor(R.color.black));
            setNavigationBarColor(getResources().getColor(R.color.black));

            //bottom nav
            bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.black));
            bottomNavigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.black)));
            //icon
            bottomNavigationView.setItemIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.g3)));

        } else {
            //light mode is on

            //status up and nav
            setStatusBarColor(getResources().getColor(R.color.g2));
            setNavigationBarColor(getResources().getColor(R.color.g2));

            //bottom nav
            bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.g2));
            bottomNavigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.g3)));
            bottomNavigationView.setItemIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.g3)));

        }

    }
    private void brown_theme(){
        Configuration configuration = getResources().getConfiguration();


        if ((configuration.uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            // Dark mode is enabled

            //status up and nav
            setStatusBarColor(getResources().getColor(R.color.black));
            setNavigationBarColor(getResources().getColor(R.color.black));

            //bottom nav
            bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.black));
            bottomNavigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.black)));
            //icon
            bottomNavigationView.setItemIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.b4)));

        } else {
            //light mode is on

            //status and nav
            setStatusBarColor(getResources().getColor(R.color.b3));
            setNavigationBarColor(getResources().getColor(R.color.b3));

            //bottom nav
            bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.b3));
            bottomNavigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.b4)));
            bottomNavigationView.setItemIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.b4)));

        }

    }
    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(color);
        }
    }
    private void setNavigationBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setNavigationBarColor(color);
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