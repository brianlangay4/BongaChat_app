package com.example.bingamoney;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FetchImageTask extends AsyncTask<String, Void, byte[]> {
    private static final String DB_URL = "jdbc:jtds:sqlserver://SQL8005.site4now.net:1433/db_a9af88_bonga";
    private static final String DB_USER = "db_a9af88_bonga_admin";
    private static final String DB_PASSWORD = "Barnabas297$$";
    private ImageFetchCallback callback;

    public FetchImageTask(ImageFetchCallback callback) {
        this.callback = callback;
    }

    @Override
    protected byte[] doInBackground(String... params) {
        String username = params[0];
        byte[] imageData = null;


        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            //Connection connection = connectionClass();
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String query = "SELECT profileimage FROM verified WHERE username = ?";


            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                imageData = resultSet.getBytes("profileimage");
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return imageData;
    }

    @Override
    protected void onPostExecute(byte[] imageData) {
        if (callback != null) {
            callback.onImageFetched(imageData);
        }
    }

    public interface ImageFetchCallback {
        void onImageFetched(byte[] imageData);
    }

}
