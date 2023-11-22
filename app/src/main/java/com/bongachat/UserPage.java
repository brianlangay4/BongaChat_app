package com.bongachat;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class UserPage extends Fragment {

    Button logout_btn;
    View veri;
    TextView username1;

    String collectedUser;
    View notfy;
    TextView notify_txt;
    TextView id;
    TextView myInfo_page;
    View wallet_page, my_info, settings, security, about;
    View background;
    ImageView imageView;
    private ActivityResultLauncher<Uri> cropLauncher;
    private Uri imageUri;

    private static final int GALLERY_REQUEST_CODE = 1;
    private int CROP_REQUEST_CODE = 2;
    private int CAMERA_REQUEST_CODE = 3;
    int imageSize = 224;


    View top;


    private static String user_id;
    public static String PREFS_NAME = "LogoutInfo";
    public static String PREFS_Id = "LogoutInfo";


    @Override
    public void onStart() {
        super.onStart();
        //performActionBasedOnTheme(getContext());


        //calling our data fetcher for theme
        StringValueManager stringManager = new StringValueManager(getContext());

        String theme = "default";
        // To retrieve the current value
        String currentValue = stringManager.getValue();
        if (currentValue == null) {
            theme = "default";
        } else if (currentValue != null) {
            theme = currentValue;
        }
        if (theme.contains("fault")) {
            Default_thheme();

        } else if (theme.contains("rown")) {
            brown_theme();
        }


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cropLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
            if (result) {
                // Handle the cropped image result here
            } else {
                // Handle failure or cancellation
            }
        });
    }

    @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState){

            // Inflate the layout for this fragment
            View inf = inflater.inflate(R.layout.fragment_user, container, false);


            background = inf.findViewById(R.id.main);

            logout_btn = inf.findViewById(R.id.logout);
            veri = inf.findViewById(R.id.verification);
            username1 = inf.findViewById(R.id.user_name);
            id = inf.findViewById(R.id.user_id);
            myInfo_page = inf.findViewById(R.id.my_info_page);
            wallet_page = inf.findViewById(R.id.wallet_page);

            top = inf.findViewById(R.id.constraintLayout);
            my_info = inf.findViewById(R.id.constraintLayout3);
            settings = inf.findViewById(R.id.constraintLayout4);
            security = inf.findViewById(R.id.constraintLayout5);
            about = inf.findViewById(R.id.constraintLayout6);
            imageView = inf.findViewById(R.id.user_imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // popout
                    showImageUploadPopup();
                }
            });


            settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), Settings.class);
                    startActivity(intent);
                }
            });


            // theme impl
            //top.setBackgroundColor(R.color.g0);


            wallet_page.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Intent intent = new Intent(getContext(),MyWalletf.class);
                    Toast.makeText(getActivity(), "under maintainance ", Toast.LENGTH_SHORT).show();
                    // startActivity(intent);
                }
            });

            myInfo_page.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //myInfo_page.setTextColor(getResources().getColor(R.color.grey));
                    Toast.makeText(getActivity(), "page under maintenance", Toast.LENGTH_SHORT).show();

                }
            });

            //myInfo_page.setTextColor(getResources().getColor(R.color.black));


            username1.setText("view username");

            startThread();


            username1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isConnected() == false) {
                        username1.setText("No Internet");
                    } else {
                        String username = collectedUser;
                        Connection connection = connectionClass();

                        MSSQLColumnFetcher columnFetcher = new MSSQLColumnFetcher(connection, username, new MSSQLColumnFetcher.OnColumnFetchListener() {
                            @Override
                            public void onColumnFetchComplete(String value) {
                                // Handle the fetched value here
                                if (value != null) {
                                    // Do something with the value
                                    username1.setText(value);
                                } else {
                                    // Handle the case when no value is found or an error occurs
                                    username1.setText("unverified user");
                                }
                            }
                        });

                        columnFetcher.execute();

                    }

                }
            });

            Intent intent = new Intent(inf.getContext(), verification_page.class);

            //current user
            SharedPreferences shared = getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);
            collectedUser = shared.getString("user", "");

            // Load and set the saved image from internal storage
            String username = collectedUser;
            Bitmap savedImage = loadImageFromFile(username);
            if (savedImage != null) {
                // Create a circular bitmap from the loaded image
                Bitmap circularBitmap = createCircularBitmap(savedImage);

                // Set the circular bitmap to the ImageView
                imageView.setImageBitmap(circularBitmap);
            }

            veri.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(intent);

                }
            });


            //user logging out
            logout_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLogoutPopup();
                }
            });

            return inf;
        }
        private void showImageUploadPopup () {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Upload Image")
                    .setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            openGallery();
                            //openCameraWithGalleryOption();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
        private void openGallery () {
            Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(cameraIntent, 1);
        }
    private void openCameraWithGalleryOption() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure there's a camera app to handle the intent
        if (cameraIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            // Create a chooser intent to provide camera and gallery options
            Intent chooserIntent = Intent.createChooser(cameraIntent, "Select Source");

            // If the user has a gallery app, add the gallery intent as an option
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryIntent.setType("image/*");

            // Add the gallery intent to the chooser
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{galleryIntent});

            startActivityForResult(chooserIntent, CAMERA_REQUEST_CODE);
        }
    }
    private Bitmap loadImageFromFile(String username) {
        try {
            // Get the app's internal storage directory
            File storageDir = getActivity().getFilesDir();

            // Create the file path for the saved image
            String fileName = "profile_image_" + username + ".jpg";
            File imageFile = new File(storageDir, fileName);

            // Load the image file and return the Bitmap
            if (imageFile.exists()) {
                return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /*  @Override
        public void onActivityResult ( int requestCode, int resultCode, Intent data){
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
                imageUri = data.getData();
                startCrop(imageUri);
            }
        }

        private void startCrop (Uri sourceUri){
            // Use the Android built-in crop action
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(sourceUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            cropIntent.putExtra("scale", true);
            cropIntent.putExtra("return-data", true);

            // Create a temporary file for the cropped image
            File croppedFile = new File(requireContext().getCacheDir(), "cropped_image.jpg");
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(croppedFile));

            startActivityForResult(cropIntent, CROP_REQUEST_CODE);
        }*/
      @Override
      public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
          if (resultCode == RESULT_OK) {
              Bitmap image = null;
              byte[] byteArray = null;

              if (requestCode == 3) {
                  image = (Bitmap) data.getExtras().get("data");
              } else {
                  assert data != null;
                  Uri dat = data.getData();
                  try {
                      image = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), dat);
                  } catch (IOException e) {
                      e.printStackTrace();
                  }
              }

              if (image != null) {
                  // Resize image to the desired size
                  int dimension = Math.min(image.getWidth(), image.getHeight());
                  image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                  image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);

                  // Convert Bitmap to byteArray for database storage
                  ByteArrayOutputStream out = new ByteArrayOutputStream();
                  image.compress(Bitmap.CompressFormat.JPEG, 100, out);
                  byteArray = out.toByteArray();

                  // Get the current logged-in username from SharedPreferences
                  //current user
                  SharedPreferences shared = getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);
                  collectedUser = shared.getString("user", "");
                  String username = collectedUser;

                  // Update the image for the user in the database
                  updateImageForUsername(username, byteArray);

                  // Update the image for the user in the database
                  updateImageForUsername(username, byteArray);

                  // Save the image as a file in internal storage
                  saveImageToFile(image, username);

                  // Create a circular bitmap from the original image
                  Bitmap circularBitmap = createCircularBitmap(image);

                  // Set the circular bitmap to the ImageView
                  imageView.setImageBitmap(circularBitmap);
              }
          }
          super.onActivityResult(requestCode, resultCode, data);
      }

    private void updateImageForUsername(String username, byte[] imageByteArray) {
        Connection connection = connectionClass();
        try {
            if (connection != null) {
                // Update the image for the user in the database
                String sqlUpdate = "UPDATE verified SET profileimage = ? WHERE username = ?";
                PreparedStatement statement = connection.prepareStatement(sqlUpdate);
                statement.setBinaryStream(1, new ByteArrayInputStream(imageByteArray), imageByteArray.length);
                statement.setString(2, username);
                statement.executeUpdate();

                // Close the connection
                connection.close();
            }
        } catch (Exception ex) {
            Log.e("Error", ex.getMessage());
        }
    }
    private Bitmap createCircularBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    private boolean isImageExistsInDatabase(byte[] imageByteArray) {
        // Query the database to check if the image already exists
        // Return true if the image exists, false otherwise
        // You'll need to implement this logic based on your database structure
        // You might use a hashing mechanism to compare images
        return false; // Placeholder, you need to implement this
    }

    private void insertImageIntoDatabase(byte[] imageByteArray) {
        Connection connection = connectionClass();
        try {
            if (connection != null) {
                // Insert the image into the database
                String sqlInsert = "INSERT INTO verified (profileimage) VALUES (?)";
                PreparedStatement statement = connection.prepareStatement(sqlInsert);
                statement.setBinaryStream(1, new ByteArrayInputStream(imageByteArray), imageByteArray.length);
                statement.executeUpdate();

                // Close the connection
                connection.close();
            }
        } catch (Exception ex) {
            Log.e("Error", ex.getMessage());
        }
    }
    private void saveImageToFile(Bitmap imageBitmap, String username) {
        try {
            // Get the app's internal storage directory
            File storageDir = getActivity().getFilesDir();

            // Create a unique file name based on the username
            String fileName = "profile_image_" + username + ".jpg";

            // Create a file object for the image
            File imageFile = new File(storageDir, fileName);

            // Create a FileOutputStream to write the image data to the file
            FileOutputStream outputStream = new FileOutputStream(imageFile);

            // Compress and write the image data to the file
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            // Close the FileOutputStream
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    public void startThread () {
            UsernameThread usernameThread = new UsernameThread();
            usernameThread.start();

        }
        public void stopThread () {
            UsernameThread usernameThread = new UsernameThread();
            usernameThread.stop();
        }
        class UsernameThread extends Thread {
            @Override
            public void run() {
                if (isConnected() == false) {
                    username1.setText("No Internet");
                } else {
                    String username = collectedUser;
                    Connection connection = connectionClass();

                    MSSQLColumnFetcher columnFetcher = new MSSQLColumnFetcher(connection, username, new MSSQLColumnFetcher.OnColumnFetchListener() {
                        @Override
                        public void onColumnFetchComplete(String value) {
                            // Handle the fetched value here
                            if (value != null) {
                                // Do something with the value
                                username1.setText(value);
                            } else {
                                // Handle the case when no value is found or an error occurs
                                username1.setText("unverified user");
                            }
                        }
                    });

                    columnFetcher.execute();

                    MSSQLColumnFetcher2 columnFetcher2 = new MSSQLColumnFetcher2(connection, username, new MSSQLColumnFetcher2.OnColumnFetchListener() {
                        @Override
                        public void onColumnFetchComplete(String value) {
                            // Handle the fetched value here
                            if (value != null) {
                                // Do something with the value
                                String limitedString = value.substring(0, Math.min(value.length(), 6));
                                id.setText(limitedString + "***");
                            } else {
                                // Handle the case when no value is found or an error occurs
                                id.setText("verify your account.");
                            }
                        }
                    });

                    columnFetcher2.execute();

                }


            }
        }

        private void showLogoutPopup () {
            View popupView = getLayoutInflater().inflate(R.layout.logout_warning, null);

            TextView textLogoutMessage = popupView.findViewById(R.id.image_view);
            Button buttonYes = popupView.findViewById(R.id.button_yes);
            Button buttonNo = popupView.findViewById(R.id.button_no);

            MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(requireContext());
            dialogBuilder.setView(popupView);

            AlertDialog dialog = dialogBuilder.create();

            buttonYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // If the user clicks "Yes", open another activity.
                    Intent intent1 = new Intent(getContext(), Login.class);
                    startActivity(intent1);
                    getActivity().finish();
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

        public static class MSSQLColumnFetcher extends AsyncTask<Void, Void, String> {
            private String connectionString;  // MSSQL database connection string
            Connection connection = connectionClass();
            private String username;  // Specific user
            private OnColumnFetchListener listener;  // Listener to handle the fetched value

            public MSSQLColumnFetcher(Connection connection, String username, OnColumnFetchListener listener) {
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
        public static class MSSQLColumnFetcher2 extends AsyncTask<Void, Void, String> {
            private String connectionString;  // MSSQL database connection string
            Connection connection = connectionClass();
            private String username;  // Specific user
            private OnColumnFetchListener listener;  // Listener to handle the fetched value

            public MSSQLColumnFetcher2(Connection connection, String username, OnColumnFetchListener listener) {
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
                    String query = "SELECT id FROM verified WHERE username = '" + username + "'";

                    ResultSet resultSet = statement.executeQuery(query);

                    if (resultSet.next()) {
                        return resultSet.getString("id");
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
        public void performActionBasedOnTheme (Context context){
            String selectedTheme = ThemeUtils.getSelectedTheme(context);

            // Perform actions based on the selected theme value
            if (selectedTheme.equals("default")) {
                performDefaultThemeActions();
            } else if (selectedTheme.equals("brown")) {
                performBrownThemeActions();
            } else if (selectedTheme.equals("navy")) {
                performNavyThemeActions();
            }
        }

        private void performDefaultThemeActions () {
            // Perform actions specific to the default theme
            // ...
            Default_thheme();
        }

        private void performBrownThemeActions () {
            // Perform actions specific to the brown theme
            // ...
            brown_theme();
        }

        private void performNavyThemeActions () {
            // Perform actions specific to the navy theme
            // ...
        }
        public void Default_thheme () {

            Configuration configuration = getResources().getConfiguration();


            //Dark mode is on


            //status
            setStatusBarColor(getResources().getColor(R.color.black));

            // Set the navigation bar color
            setNavigationBarColor(getResources().getColor(R.color.black));


            //menu
            // bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.black));


            //menu icons color


            Drawable cont0 = getResources().getDrawable(R.drawable.cont0);
            Drawable cont3 = getResources().getDrawable(R.drawable.cont3);

            if ((configuration.uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
                // Dark mode is enabled

                //default theme = green in dark mode

                //background
                background.setBackgroundColor(getResources().getColor(R.color.black));


                //top cont
                // Create a copy of the drawable with color applied
                Drawable drawable = DrawableCompat.wrap(cont3);
                DrawableCompat.setTintList(drawable, ColorStateList.valueOf(getResources().getColor(R.color.g0)));
                top.setBackground(getResources().getDrawable(R.drawable.cont0));

                //top cont texts
                username1.setTextColor(getResources().getColor(R.color.g1));
                id.setTextColor(getResources().getColor(R.color.g1));

                //indicators
                Drawable drawable1 = DrawableCompat.wrap(cont0);
                DrawableCompat.setTintList(drawable1, ColorStateList.valueOf(getResources().getColor(R.color.g0)));
                veri.setBackgroundColor(getResources().getColor(R.color.g0));
                veri.setBackground(getResources().getDrawable(R.drawable.cont3));

                wallet_page.setBackgroundColor(getResources().getColor(R.color.g0));
                wallet_page.setBackground(getResources().getDrawable(R.drawable.cont3));

                my_info.setBackgroundColor(getResources().getColor(R.color.g0));
                my_info.setBackground(getResources().getDrawable(R.drawable.cont3));

                settings.setBackgroundColor(getResources().getColor(R.color.g0));
                settings.setBackground(getResources().getDrawable(R.drawable.cont3));

                security.setBackgroundColor(getResources().getColor(R.color.g0));
                security.setBackground(getResources().getDrawable(R.drawable.cont3));

                about.setBackgroundColor(getResources().getColor(R.color.g0));
                about.setBackground(getResources().getDrawable(R.drawable.cont3));

                logout_btn.setBackgroundColor(getResources().getColor(R.color.g0));


            } else {
                //light mode is on

                //status
                setStatusBarColor(getResources().getColor(R.color.g2));

                // Set the navigation bar color
                setNavigationBarColor(getResources().getColor(R.color.g2));


                //background
                background.setBackgroundColor(getResources().getColor(R.color.g2));

                //menu icons color


                //default dark theme = green in dark mode


                //top
                //top cont
                // Create a copy of the drawable with color applied
                Drawable drawable = DrawableCompat.wrap(cont0);
                DrawableCompat.setTintList(drawable, ColorStateList.valueOf(getResources().getColor(R.color.g0)));
                top.setBackground(getResources().getDrawable(R.drawable.cont0));

                //top cont texts
                username1.setTextColor(getResources().getColor(R.color.g1));
                id.setTextColor(getResources().getColor(R.color.g1));

                //indicators
                Drawable drawable1 = DrawableCompat.wrap(cont3);
                DrawableCompat.setTintList(drawable1, ColorStateList.valueOf(getResources().getColor(R.color.g1)));

                veri.setBackgroundColor(getResources().getColor(R.color.g0));
                veri.setBackground(getResources().getDrawable(R.drawable.cont3));

                wallet_page.setBackgroundColor(getResources().getColor(R.color.g1));
                wallet_page.setBackground(getResources().getDrawable(R.drawable.cont3));

                my_info.setBackgroundColor(getResources().getColor(R.color.g1));
                my_info.setBackground(getResources().getDrawable(R.drawable.cont3));

                settings.setBackgroundColor(getResources().getColor(R.color.g1));
                settings.setBackground(getResources().getDrawable(R.drawable.cont3));

                security.setBackgroundColor(getResources().getColor(R.color.g1));
                security.setBackground(getResources().getDrawable(R.drawable.cont3));

                about.setBackgroundColor(getResources().getColor(R.color.g1));
                about.setBackground(getResources().getDrawable(R.drawable.cont3));

                logout_btn.setBackgroundColor(getResources().getColor(R.color.g1));
            }

        }
        public void brown_theme () {

            Configuration configuration = getResources().getConfiguration();


            //Dark mode is on


            //status
            setStatusBarColor(getResources().getColor(R.color.black));

            // Set the navigation bar color
            setNavigationBarColor(getResources().getColor(R.color.black));


            //background
            background.setBackgroundColor(getResources().getColor(R.color.black));


            //menu icons color


            Drawable cont0 = getResources().getDrawable(R.drawable.cont0);
            Drawable cont3 = getResources().getDrawable(R.drawable.cont3);

            if ((configuration.uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
                // Dark mode is enabled

                //default theme = green in light mode

                //top
                //top cont
                // Create a copy of the drawable with color applied
                Drawable drawable = DrawableCompat.wrap(cont3);
                DrawableCompat.setTintList(drawable, ColorStateList.valueOf(getResources().getColor(R.color.b0)));
                top.setBackground(getResources().getDrawable(R.drawable.cont0));

                //top cont texts
                username1.setTextColor(getResources().getColor(R.color.b1));
                id.setTextColor(getResources().getColor(R.color.b1));

                //indicators
                Drawable drawable1 = DrawableCompat.wrap(cont0);
                DrawableCompat.setTintList(drawable1, ColorStateList.valueOf(getResources().getColor(R.color.b0)));
                veri.setBackgroundColor(getResources().getColor(R.color.b0));
                veri.setBackground(getResources().getDrawable(R.drawable.cont3));

                wallet_page.setBackgroundColor(getResources().getColor(R.color.b0));
                wallet_page.setBackground(getResources().getDrawable(R.drawable.cont3));

                my_info.setBackgroundColor(getResources().getColor(R.color.b0));
                my_info.setBackground(getResources().getDrawable(R.drawable.cont3));

                settings.setBackgroundColor(getResources().getColor(R.color.b0));
                settings.setBackground(getResources().getDrawable(R.drawable.cont3));

                security.setBackgroundColor(getResources().getColor(R.color.b0));
                security.setBackground(getResources().getDrawable(R.drawable.cont3));

                about.setBackgroundColor(getResources().getColor(R.color.b0));
                about.setBackground(getResources().getDrawable(R.drawable.cont3));

                logout_btn.setBackgroundColor(getResources().getColor(R.color.b0));


            } else {
                //light mode is on

                //status
                setStatusBarColor(getResources().getColor(R.color.b3));

                // Set the navigation bar color
                setNavigationBarColor(getResources().getColor(R.color.b3));


                //background
                background.setBackgroundColor(getResources().getColor(R.color.b3));


                //menu icons color


                //default dark theme = green in dark mode


                //top
                //top cont
                // Create a copy of the drawable with color applied
                Drawable drawable = DrawableCompat.wrap(cont0);
                DrawableCompat.setTintList(drawable, ColorStateList.valueOf(getResources().getColor(R.color.b0)));
                top.setBackground(getResources().getDrawable(R.drawable.cont0));

                //top cont texts
                username1.setTextColor(getResources().getColor(R.color.b1));
                id.setTextColor(getResources().getColor(R.color.b1));

                //indicators
                Drawable drawable1 = DrawableCompat.wrap(cont3);
                DrawableCompat.setTintList(drawable1, ColorStateList.valueOf(getResources().getColor(R.color.b1)));

                veri.setBackgroundColor(getResources().getColor(R.color.b1));
                veri.setBackground(getResources().getDrawable(R.drawable.cont3));

                wallet_page.setBackgroundColor(getResources().getColor(R.color.b1));
                wallet_page.setBackground(getResources().getDrawable(R.drawable.cont3));

                my_info.setBackgroundColor(getResources().getColor(R.color.b1));
                my_info.setBackground(getResources().getDrawable(R.drawable.cont3));

                settings.setBackgroundColor(getResources().getColor(R.color.b1));
                settings.setBackground(getResources().getDrawable(R.drawable.cont3));

                security.setBackgroundColor(getResources().getColor(R.color.b1));
                security.setBackground(getResources().getDrawable(R.drawable.cont3));

                about.setBackgroundColor(getResources().getColor(R.color.b1));
                about.setBackground(getResources().getDrawable(R.drawable.cont3));

                logout_btn.setBackgroundColor(getResources().getColor(R.color.b0));
            }

        }
        private void setStatusBarColor ( int color){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getActivity().getWindow().setStatusBarColor(color);
            }
        }
        private void setNavigationBarColor ( int color){
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setNavigationBarColor(color);
            }
        }

        @SuppressLint("NewApi")
        public static Connection connectionClass () {
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
        boolean isConnected () {
            ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                return networkInfo.isConnected();
            } else
                return false;
        }

    }