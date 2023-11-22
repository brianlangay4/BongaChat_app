package com.example.bingamoney;

import static android.content.Context.MODE_PRIVATE;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;


public class Home extends Fragment {

    TextView  convertedRate;
    TextView  send_to;
    TextView  amount_to;
    TextView  info;
    TextView  info2;
    Button transfer;
    String Tid;
    String collectedUser,amount;
    Double transactionAmount;
    String username1;
    View notfy;
    TextView notify_txt;
    Button refresh;
    private Snackbar snackbar;
    private FragmentRefreshListener refreshListener;
    private ViewPager2 viewPager;

    private static String user_id;
    public static String PREFS_NAME="LogoutInfo";
    public static String PREFS_Id="LogoutInfo";

    private View tab1,tab2;

    private Button tab1Button,tab2Button;

    private boolean isTab1Visible = true;

    private RecyclerView recyclerView;

    private List<TransactionDataHome> transactionDataList;

    Handler handler;

    private TextView cityNameTextView, conditionTextView,temperatureTextView;

    private ConstraintLayout fixedLayout;
    private int originalHeight;
    private int scrollY = 0;
    private boolean isScrolling = false;
    private ValueAnimator currentAnimator;
    private Handler scrollHandler = new Handler();

    // Initialize LruCache
    int cacheSize = 10 * 1024 * 1024; // 10MB cache size
    LruCache<String, Bitmap> imageCache = new LruCache<>(cacheSize);


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {







        // Inflate the layout for this fragment
        View inf = inflater.inflate(R.layout.fragment_home,container,false);
        //init rate txt
        convertedRate = inf.findViewById(R.id.ratetxt);
        fixedLayout = inf.findViewById(R.id.top);


        originalHeight = fixedLayout.getLayoutParams().height;

        NestedScrollView nestedScrollView = inf.findViewById(R.id.nestedScrollView);



        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int y, int oldScrollX, int oldScrollY) {
                // Calculate the scroll velocity
                int scrollSpeed = Math.abs(y - scrollY);

                // Update the scrollY value
                scrollY = y;

                // Calculate the animation duration based on the scroll speed
                int animationDuration = 100; // Default animation duration in milliseconds
                if (scrollSpeed > 0) {
                    animationDuration = Math.min(100, 300 / scrollSpeed); // Maximum duration is 300ms
                }

                // Perform the animation based on the current scroll position
                if (scrollY > 0 && scrollY > originalHeight / 2) {
                    // Scroll down, reduce the height of fixedLayout to 50dp
                    animateViewHeight(fixedLayout, dpToPx(80), animationDuration);
                } else {
                    // Scroll up, restore the original height
                    animateViewHeight(fixedLayout, originalHeight, animationDuration);
                }
            }
        });


        //weather init
        cityNameTextView = inf.findViewById(R.id.city);
        temperatureTextView = inf.findViewById(R.id.degrees);
        conditionTextView = inf.findViewById(R.id.weather_des);

        //weather anim init
        LottieAnimationView sunny = inf.findViewById(R.id.sunny);
        LottieAnimationView clear = inf.findViewById(R.id.clear);
        LottieAnimationView rain = inf.findViewById(R.id.rain);
        LottieAnimationView foggy = inf.findViewById(R.id.foggy);
        LottieAnimationView light_rain = inf.findViewById(R.id.light_rain);
        LottieAnimationView sunrize = inf.findViewById(R.id.sunrize);
        LottieAnimationView snow = inf.findViewById(R.id.snow);
        LottieAnimationView snow_sunny = inf.findViewById(R.id.snow_sunny);
        LottieAnimationView cloudy = inf.findViewById(R.id.cloudy);
        LottieAnimationView partlyShower = inf.findViewById(R.id.party_shower);


        WeatherApiClient weatherApiClient = new WeatherApiClient(cityNameTextView,
                conditionTextView,
                temperatureTextView,
                partlyShower,
                cloudy,
                rain,
                sunny,
                foggy,
                light_rain,
                sunrize,
                snow_sunny,
                snow,
                clear
        );
       // weatherApiClient.execute("Dar es salaam");


        tab1 = inf.findViewById(R.id.tab1);
        tab2 = inf.findViewById(R.id.tab2);
        tab1Button = inf.findViewById(R.id.tab1_button);
        tab2Button = inf.findViewById(R.id.tab2_button);
        recyclerView = inf.findViewById(R.id.recent_recycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



        // Set initial visibility of tabs
        tab1.setVisibility(View.VISIBLE);
        tab2.setVisibility(View.GONE);


        Configuration configuration = getResources().getConfiguration();

        int color;
        int color2;
        if ((configuration.uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            // Dark mode is enabled
            color = getResources().getColor(R.color.dark0);
            color2 = getResources().getColor(R.color.dark01);


        } else {
            // Dark mode is not enabled
            color = getResources().getColor(R.color.light0);
            color2 = getResources().getColor(R.color.light01);
        }
        tab1Button.setBackgroundColor(color);
        tab2Button.setBackgroundColor(color2);




        tab1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tab2.setBackgroundColor();
                int color;
                int color2;
                if ((configuration.uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
                    // Dark mode is enabled
                    color = getResources().getColor(R.color.dark0);
                    color2 = getResources().getColor(R.color.dark01);
                } else {
                    // Dark mode is not enabled
                    color = getResources().getColor(R.color.light0);
                    color2 = getResources().getColor(R.color.light01);
                }
                tab1Button.setBackgroundColor(color);
                tab2Button.setBackgroundColor(color2);
                tab1.setVisibility(View.VISIBLE);
                tab2.setVisibility(View.GONE);
            }
        });

        tab2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color;
                int color2;
                if ((configuration.uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
                    // Dark mode is enabled
                    color = getResources().getColor(R.color.dark0);
                    color2 = getResources().getColor(R.color.dark01);
                } else {
                    // Dark mode is not enabled
                    color = getResources().getColor(R.color.light0);
                    color2 = getResources().getColor(R.color.light01);
                }
                tab1Button.setBackgroundColor(color2);
                tab2Button.setBackgroundColor(color);

                tab1.setVisibility(View.GONE);
                tab2.setVisibility(View.VISIBLE);
            }
        });


        //sending init
        send_to = inf.findViewById(R.id.send_to);
        amount_to  = inf.findViewById(R.id.amount_to_send);
        transfer = inf.findViewById(R.id.transfer_money);
        info = inf.findViewById(R.id.info);
        notfy = inf.findViewById(R.id.notify);
        notify_txt = inf.findViewById(R.id.notify_txt);
        refresh = inf.findViewById(R.id.refresh);

        notfy.setVisibility(View.INVISIBLE);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected()==false) {

                    notfy.setVisibility(View.VISIBLE);
                    notify_txt.setText("unable to connect");
                }
                else {
                    Intent intent = new Intent(getActivity(),Uicount.class);
                    startActivity(intent);

                }
            }
        });


        if (isConnected()==false) {

            notfy.setVisibility(View.VISIBLE);
            notify_txt.setText("No Internet Connection");
        }

        //Get time
        // Get the current date and time
        Date currentDate = new Date();
        // Define the desired date and time format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Format the date and time
        String formattedDateTime = dateFormat.format(currentDate);
        Connection connection = connectionClass();

        //current user
        SharedPreferences shared = getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);
        collectedUser = shared.getString("user", "");


        transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Connection connection = connectionClass();
                String username1 = send_to.getText().toString().trim();
                amount = amount_to.getText().toString().trim();


                if (isConnected() == false) {
                    notfy.setVisibility(View.VISIBLE);
                    notify_txt.setText("No Internet Connection");
                    send_to.setError("No internet connection");
                    send_to.requestFocus();

                    //pop up confirm receivers identification
                    return;
                }
                if (TextUtils.isEmpty(send_to.getText())) {
                    send_to.setError("Username is required");
                    send_to.requestFocus();
                    return;
                }

                // Then, check if the password fields are empty
                else if (TextUtils.isEmpty(amount)) {
                    amount_to.setError("Amount required");
                    amount_to.requestFocus();
                    return;
                }

                // Next, check if the username already exists in the database
                boolean usernameExists = checkUsernameExists(username1);
                if (!usernameExists) {
                    send_to.setError("Username does not exists");
                    send_to.requestFocus();

                    //pop up confirm receivers identification
                    return;
                }
                boolean usernameExists2 = checkUsernameExists2(username1);
                if (!usernameExists2) {
                    send_to.setError("Cannot send to unverified user");
                    send_to.requestFocus();
                    //pop up confirm receivers identification
                    return;
                }
                boolean usernameExists3 = checkUsernameExists2(collectedUser);
                if (!usernameExists3) {
                    send_to.setError("your not verified");
                    send_to.requestFocus();
                    //pop up confirm receivers identification
                    return;
                }
                if (collectedUser.equals(username1)) {
                    send_to.setError("can't send to self");
                    send_to.requestFocus();

                    //pop up confirm receivers identification
                    return;
                }
                Float amt = Float.valueOf(amount_to.getText().toString());
                if (amt < 1) {
                    amount_to.setError("cant send less than 1");
                    amount_to.requestFocus();

                    //pop up confirm receivers identification
                    return;
                }


                String username = collectedUser;

                MSSQLColumnFetcher columnFetcher2 = new MSSQLColumnFetcher(connection, username, new MSSQLColumnFetcher.OnColumnFetchListener() {
                    @Override
                    public void onColumnFetchComplete(String value) {
                        // Handle the fetched value here
                        if (value != null) {
                            // Do something with the value


                            Float amt = Float.valueOf(amount_to.getText().toString());
                            Float blc = Float.valueOf(value);
                            if (blc<amt){
                                amount_to.setError("insufficient balance");
                                amount_to.requestFocus();

                            }else {
                                AcceptP();
                                //update balance
                            }


                        }
                    }
                });

                columnFetcher2.execute();


            }



            });


        //rates,
        FetchDataAsyncTask fetchDataAsyncTask = new FetchDataAsyncTask(convertedRate);
        //fetchDataAsyncTask.execute();

        return inf;
    }
    // Function to add an image to cache
    public void addToCache(String key, Bitmap bitmap) {
        imageCache.put(key, bitmap);
    }

    // Function to retrieve an image from cache
    public Bitmap getFromCache(String key) {
        return imageCache.get(key);
    }
    public Bitmap getProfileImage(Connection connection, String loggedInUser) {
        // Check cache first
        Bitmap cachedImage = getFromCache(loggedInUser);
        if (cachedImage != null) {
            return cachedImage;
        }

        // Fetch image from the database
        String query = "SELECT profileimage FROM verified WHERE username = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, loggedInUser);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                byte[] imageBytes = resultSet.getBytes("profileimage");
                Bitmap image = BitmapFactory.decodeStream(new ByteArrayInputStream(imageBytes));

                // Add image to cache
                addToCache(loggedInUser, image);

                return image;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Image not found
    }

    @Override
    public void onPause() {
        super.onPause();
        // Cancel the current animation when the activity is paused to avoid potential issues
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }
    }

    private void animateViewHeight(final View view, final int newHeight, int duration) {
        if (view.getLayoutParams().height == newHeight) {
            return; // Animation not needed as the view is already at the target height
        }

        if (currentAnimator != null) {
            currentAnimator.cancel(); // Cancel the previous animation if it's running
        }

        currentAnimator = ValueAnimator.ofInt(view.getHeight(), newHeight);
        currentAnimator.setDuration(duration); // Animation duration based on scroll speed

        currentAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                view.getLayoutParams().height = animatedValue;
                view.requestLayout();
            }
        });

        currentAnimator.start();
    }

    // Utility method to convert dp to pixels
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
    private boolean checkUsernameExists(String username) {
        Connection connection = connectionClass();

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
    //verified
    private boolean checkUsernameExists2(String username) {
        Connection connection = connectionClass();

        try {
            String query = "SELECT * FROM verified WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next(); // Returns true if username already exists
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    private void AcceptP() {
        View popupView = getLayoutInflater().inflate(R.layout.payment_confirm, null);

        TextView textINfo = popupView.findViewById(R.id.image_view);
        TextView textINfo2 = popupView.findViewById(R.id.image_view2);
        Button buttonYes = popupView.findViewById(R.id.button_yes);
        Button buttonNo = popupView.findViewById(R.id.button_no);
        String username = send_to.getText().toString();
        Connection connection = connectionClass();

        MSSQLColumnFetcher2 columnFetcher = new MSSQLColumnFetcher2(connection, username, new MSSQLColumnFetcher2.OnColumnFetchListener() {
            @Override
            public void onColumnFetchComplete(String value) {
                // Handle the fetched value here
                if (value != null) {
                    // Do something with the value
                    textINfo.setText("Sending to: " + value);
                } else {
                    // Handle the case when no value is found or an error occurs
                    textINfo.setText("unverified user");
                }
            }
        });

        columnFetcher.execute();

        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(requireContext());
        dialogBuilder.setView(popupView);

        AlertDialog dialog = dialogBuilder.create();

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the user clicks "Yes", open another activity.

                buttonYes.setVisibility(View.GONE);
                String username = collectedUser;
                String username2 = send_to.getText().toString();
                Connection connection = connectionClass();

                MSSQLColumnFetcher columnFetcher2 = new MSSQLColumnFetcher(connection, username, new MSSQLColumnFetcher.OnColumnFetchListener() {
                    @Override
                    public void onColumnFetchComplete(String value) {
                        // Handle the fetched value here
                        if (value != null) {
                            // Do something with the value
                            textINfo.setText("successful transfer");
                            Float amt = Float.valueOf(amount_to.getText().toString());
                            Float blc = Float.valueOf(value);
                            DecimalFormat decimalFormat = new DecimalFormat("#.#");

                            // Round the float value to one decimal place
                            float roundedValue = Float.parseFloat(decimalFormat.format(blc - amt));

                            //string
                            String stringBlc = String.valueOf(roundedValue);

                            textINfo2.setText("sent " + amt + " \n new balace = " + (stringBlc));
                            buttonNo.setText("Done");
                            //sender
                            Handler hd = new Handler();
                            hd.post(new Runnable() {
                                @Override
                                public void run() {
                                    UpdateBalanceTask updateTask = new UpdateBalanceTask(collectedUser, (blc - amt), Home.this::onBalanceUpdate);
                                    // Execute the task
                                    updateTask.execute();
                                }
                            });

                            //send transaction

                        } else {
                            // Handle the case when no value is found or an error occurs
                            textINfo.setText("unverified user");
                        }
                    }
                });

                columnFetcher2.execute();

                //receiver
                MSSQLColumnFetcher columnFetcher3 = new MSSQLColumnFetcher(connection, username2, new MSSQLColumnFetcher.OnColumnFetchListener() {
                    @Override
                    public void onColumnFetchComplete(String value) {
                        // Handle the fetched value here
                        if (value != null) {
                            // Do something with the value
                            Float amt = Float.valueOf(amount_to.getText().toString());
                            Float blc = Float.valueOf(value);
                            //receiver
                            try {
                                Connection connection = connectionClass();

                                // Update the balance in the wallet table for the specified username
                                String query = "UPDATE wallet SET balance = ? WHERE username = ?";
                                PreparedStatement statement = connection.prepareStatement(query);

                                // Set the new balance and username values
                                statement.setFloat(1, amt + blc);
                                statement.setString(2, username2);

                                // Execute the update query
                                statement.executeUpdate();

                                // Close the statement and connection
                                statement.close();
                                connection.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                        } else {
                            // Handle the case when no value is found or an error occurs
                            textINfo.setText("unverified user");
                        }
                    }
                });

                columnFetcher3.execute();
                Handler hd0 = new Handler();
                String targetUsername = send_to.getText().toString();
                String sourceUsername = collectedUser;
                String amount = amount_to.getText().toString();
                String message = "Received " + amount + "from " + collectedUser;


                Handler hd = new Handler();
                hd.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "successful", Toast.LENGTH_SHORT).show();
                    }
                }, 8000);
                Toast.makeText(getActivity(), "sending", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the user clicks "No", close the dialog.
                dialog.dismiss();
            }
        });

        dialog.show();
    }


   private void showFancyNotification() {
       // Inflate the custom layout
       View customView = LayoutInflater.from(getActivity()).inflate(R.layout.notification1, null);

       // Find the TextView and set the "Successful" message
       TextView messageTextView = customView.findViewById(R.id.messageTextView);
       messageTextView.setText("Successful");



       // Create a Snackbar with the custom layout
       snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "", Snackbar.LENGTH_INDEFINITE);
       Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
       layout.setPadding(0, 0, 0, 0);
       layout.addView(customView, 0);

       // Show the Snackbar
       snackbar.show();

       // Schedule the notification to disappear after 5 seconds
       new Handler().postDelayed(Home.this::dismissNotification, 5000);
   }

    // Dismiss the notification
    private void dismissNotification() {
        if (snackbar != null && snackbar.isShown()) {
            snackbar.dismiss();
        }
    }



    // Rest of the fragment code...

    public void setRefreshListener(FragmentRefreshListener listener) {
        this.refreshListener = listener;
    }

    private void refreshOtherFragment() {
        if (refreshListener != null) {
            refreshListener.onRefresh();
        }
    }




        public void onBalanceUpdate() {
        // Handle the completion of the balance update
        // This method will be called when the update task completes successfully
        //Toast.makeText(getActivity(), "Balance updated successfully", Toast.LENGTH_SHORT).show();
        showFancyNotification();


        refreshOtherFragment();

        //sent to transaction
        Connection connection = connectionClass();
        try {
            //get the id
            int max = 99988;
            int min = 10000;
            Random random = new Random();
            String i = String.valueOf(random.nextInt(max + min) + min);

            int max2 = 59979;
            int min2 = 12347;
            Random random2 = new Random();
            String i2 = String.valueOf(random2.nextInt(max2 + min2) + min);

            byte[] imageData = fetchImageSynchronously(collectedUser);
            // Convert byte array to input stream
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);

            // Assuming you have a valid 'connection' object
            Bitmap profileImage = getProfileImage(connection, collectedUser);
            ByteArrayInputStream inputStream2 = new ByteArrayInputStream(profileImage.getNinePatchChunk());

            if (profileImage != null) {
                // Display the profile image in an ImageView or any other appropriate UI element
                //imageView.setImageBitmap(profileImage);
            } else {
                // Handle case when image is not found
                // You might display a default image or show an error message
            }



            Tid = i+i2;
            String username2 = send_to.getText().toString();
            Float amt = Float.valueOf(amount_to.getText().toString());
            String query = "INSERT INTO trans1 (Tid, method, sender, receiver, amount, status, senderimg) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, Tid);
            preparedStatement.setString(2, "Bonga balance");
            preparedStatement.setString(3, collectedUser);
            preparedStatement.setString(4, username2);
            preparedStatement.setString(5, String.valueOf(amt));
            preparedStatement.setString(6, "credited successful");
            preparedStatement.setBinaryStream(7, inputStream, imageData.length);
             //preparedStatement.setBlob(7, profileImage);
            //preparedStatement.setBinaryStream(7, inputStream2);
            preparedStatement.executeUpdate();

            //replicate
            String query1 = "INSERT INTO trans2 (method, sender, receiver, amount, status) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement1 = connection.prepareStatement(query1);
            preparedStatement1.setString(1, "Bonga balance");
            preparedStatement1.setString(2, collectedUser);
            preparedStatement1.setString(3, username2);
            preparedStatement1.setString(4, String.valueOf(amt));
            preparedStatement1.setString(5, "credited successful");
            preparedStatement1.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
    private byte[] fetchImageSynchronously(String username) {
        final CountDownLatch latch = new CountDownLatch(1);
        final byte[][] imageDataWrapper = new byte[1][];

        FetchImageTask fetchImageTask = new FetchImageTask(new FetchImageTask.ImageFetchCallback() {
            @Override
            public void onImageFetched(byte[] imageData) {
                imageDataWrapper[0] = imageData;
                latch.countDown();
            }
        });
        fetchImageTask.execute(username);

        try {
            latch.await(); // This blocks the current thread until the latch is counted down
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return imageDataWrapper[0];
    }









    public static class MSSQLColumnFetcher2 extends AsyncTask<Void, Void, String> {
        private String connectionString;  // MSSQL database connection string
        Connection connection = connectionClass();
        private String username;  // Specific user
        private MSSQLColumnFetcher2.OnColumnFetchListener listener;  // Listener to handle the fetched value

        public MSSQLColumnFetcher2(Connection connection, String username, MSSQLColumnFetcher2.OnColumnFetchListener listener) {
            this.connection = connection;
            this.username = username;
            this.listener = listener;
        }


        @Override
        protected String doInBackground(Void... voids) {
            try {
                Connection connection = connectionClass();
                Statement statement = connection.createStatement();

                // Fetch the fullname based on the username
                String query = "SELECT fullname FROM verified WHERE username = '" + username + "'";

                ResultSet resultSet = statement.executeQuery(query);

                if (resultSet.next()) {
                    return resultSet.getString("fullname");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                //Connection connection = connectionClass();
                Statement statement = connection.createStatement();

                // Fetch the fullname based on the username
                String query = "SELECT balance FROM wallet WHERE username = '" + username + "'";

                ResultSet resultSet2 = statement.executeQuery(query);

                if (resultSet2.next()) {
                    return resultSet2.getString("balance");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String value) {
            if (listener != null) {
                listener.onColumnFetchComplete(value);
            }
        }

        public interface OnColumnFetchListener {
            void onColumnFetchComplete(String value);
        }
    }
    public static class MSSQLColumnFetcher extends AsyncTask<Void, Void, String> {
        private String connectionString;  // MSSQL database connection string
        Connection connection = connectionClass();
        private String username;  // Specific user
        private OnColumnFetchListener listener;  // Listener to handle the fetched value

        public MSSQLColumnFetcher(Connection connection , String username, OnColumnFetchListener listener) {
            this.connection = connection;
            this.username = username;
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                Connection connection = connectionClass();
                Statement statement = connection.createStatement();

                // Fetch the fullname based on the username
                String query = "SELECT balance FROM wallet WHERE username = '" + username + "'";

                ResultSet resultSet = statement.executeQuery(query);

                if (resultSet.next()) {
                    return resultSet.getString("balance");
                }
            } catch ( SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String value) {
            if (listener != null) {
                listener.onColumnFetchComplete(value);
            }
        }


        public interface OnColumnFetchListener {
            void onColumnFetchComplete(String value);
        }
    }

    public static class UpdateBalanceTask extends AsyncTask<Void, Void, Void> {
        private String collectedUser;  // Username of the specific user
        private Float newBalance;  // New balance value
        private OnBalanceUpdateListener listener;  // Listener to handle the completion of the balance update

        public UpdateBalanceTask(String collectedUser, Float newBalance, OnBalanceUpdateListener listener) {
            this.collectedUser = collectedUser;
            this.newBalance = newBalance;
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Connection connection = connectionClass();

                // Update the balance in the wallet table for the specified username
                String query = "UPDATE wallet SET balance = ? WHERE username = ?";
                PreparedStatement statement = connection.prepareStatement(query);

                // Set the new balance and username values
                statement.setFloat(1, newBalance);
                statement.setString(2, collectedUser);

                // Execute the update query
                statement.executeUpdate();

                // Close the statement and connection
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (listener != null) {
                listener.onBalanceUpdate();
            }
        }

        public interface OnBalanceUpdateListener {
            void onBalanceUpdate();
        }
    }


    private class FetchDataFromDatabaseTask extends AsyncTask<String, Void, List<TransactionDataHome>> {

        @Override
        protected List<TransactionDataHome> doInBackground(String... params) {
            String username = collectedUser;
            transactionDataList = new ArrayList<TransactionDataHome>();

            try {
                Connection connection = connectionClass();
                Statement statement = connection.createStatement();
                String query = "SELECT TOP 3 sender, receiver, amount, time FROM trans1 WHERE sender = '" + username + "' ORDER BY time DESC";
                ResultSet resultSet = statement.executeQuery(query);

                int rowCount = 0; // Track the number of rows fetched
                while (resultSet.next()) {
                    rowCount++;
                    String detail = "Sent via Bonga";
                    String trans_info1 = resultSet.getString("receiver");
                    String limitedString = trans_info1.substring(0, Math.min(trans_info1.length(), 18));
                    String trans1 = "to " + limitedString;
                    String trans_info2 = resultSet.getString("amount");
                    String time = resultSet.getString("time");
                    String limitedString2 = time.substring(0, Math.min(time.length(), 16));

                    transactionDataList.add(new TransactionDataHome(detail, trans1, trans_info2, limitedString2));
                }

                resultSet.close();
                statement.close();
                connection.close();

                Log.d("db", "Fetched " + rowCount + " rows from the database.");

            } catch (SQLException e) {
                Log.e("db", "Error fetching data from database: " + e.getMessage());
            }

            return transactionDataList;
        }



        @Override
        protected void onPostExecute(List<TransactionDataHome> transactionDataList) {
            // update the UI with the last three fetched data
            int startIndex = Math.max(transactionDataList.size() - 3, 0);
            List<TransactionDataHome> lastThreeTransactions = transactionDataList.subList(startIndex, transactionDataList.size());
            TransDataAdapterHome adapter = new TransDataAdapterHome(lastThreeTransactions, getContext());
            recyclerView.setAdapter(adapter);
        }
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
    boolean isConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo!=null){
            return networkInfo.isConnected();
        }else
            return false;
    }
}

