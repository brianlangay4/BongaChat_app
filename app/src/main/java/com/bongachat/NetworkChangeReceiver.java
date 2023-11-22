package com.bongachat;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (isConnected(context)) {
            //Toast.makeText(context, "Internet connected", Toast.LENGTH_SHORT).show();
            // Perform any additional actions when connected
            Intent newIntent = new Intent(context, Uicount.class);
            Intent newIntent2 = new Intent(context, Login.class);

            Connection connection = connectionClass();
            if(connection != null) {
                SharedPreferences sharedPreferences = context.getSharedPreferences(Login.PREFS_NAME, 0);
                boolean hasLogged = sharedPreferences.getBoolean("hasLogged", false);

                if (hasLogged) {
                    newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(newIntent);

                }
                else {
                    newIntent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(newIntent2);
                }
            }
        }
    }

    private boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }
    @SuppressLint("NewApi")
    public Connection connectionClass () {
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
