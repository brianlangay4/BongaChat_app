package com.bongachat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.LruCache;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import com.airbnb.lottie.LottieAnimationView;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {

    private UserAdapter adapter;
    private List<User> userList;
    private RecyclerView recyclerView;
    private LruCache<String, User> cache; // Moved cache to class level

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        LottieAnimationView add = findViewById(R.id.add_friend);
        add.setVisibility(View.VISIBLE);
        add.setRepeatCount(0);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add.setRepeatCount(0);
                add.playAnimation();
            }
        });

        // Initialize userList (empty for now)
        userList = new ArrayList<>();

        // Initialize RecyclerView and set the adapter
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new UserAdapter(userList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Initialize the LruCache for caching search results (10 entries)
        cache = new LruCache<>(10);

        // Fetch initial data or populate userList as needed
        // For this example, you can load some initial data here.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Get the SearchView and set up the query listener
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Check if the result is already cached
                User cachedResult = cache.get(newText);
                if (cachedResult != null) {
                    userList.clear();
                    userList.add(cachedResult);
                    adapter.notifyDataSetChanged();
                } else {
                    // If not cached, perform the search in the background
                    new SearchTask().execute(newText);
                }

                return true;
            }
        });

        return true;
    }

    // Background search task
    private class SearchTask extends AsyncTask<String, Void, User> {
        @Override
        protected User doInBackground(String... params) {
            String query = params[0];
            // Implement the database query to search for users matching the query
            return performDatabaseSearch(query);
        }

        @Override
        protected void onPostExecute(User result) {
            super.onPostExecute(result);
            if (result != null) {
                // Add the result to the cache
                cache.put(result.getUsername(), result);

                // Update the UI with the search result
                userList.clear();
                userList.add(result);
                adapter.notifyDataSetChanged();
            }
        }
    }

    // Perform the actual database search here
    private User performDatabaseSearch(String query) {
        Connection connection = connectionClass();
        if (connection == null) {
            return null; // Handle connection error
        }

        User user = null;

        try {
            // Create and execute a SQL query to search for a user by username
            String sqlQuery = "SELECT * FROM verified WHERE username LIKE ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, "%" + query + "%"); // Use '%' for wildcard search

            ResultSet resultSet = preparedStatement.executeQuery();

            // Check if there's a result
            if (resultSet.next()) {
                // Retrieve user data from the result set
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String fullName = resultSet.getString("fullname");
                int sect = resultSet.getInt("sect");
                String time = resultSet.getString("time");
                String profileImage = resultSet.getString("profileimage");
                String userIdNo = resultSet.getString("useridno");
                String idPhoto = resultSet.getString("idphoto");

                // Create a User object with the fetched data
                user = new User(id, username, fullName, sect, time, profileImage, userIdNo, idPhoto);
            }

            // Close resources
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any SQL errors here
        }

        return user;
    }

    @SuppressLint("NewApi")
    public static Connection connectionClass() {
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
