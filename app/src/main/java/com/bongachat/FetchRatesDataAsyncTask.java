package com.bongachat;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FetchDataAsyncTask extends AsyncTask<Void, Void, Integer> {
    private TextView textView;

    public FetchDataAsyncTask(TextView textView) {
        this.textView = textView;
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://currency-converter-by-api-ninjas.p.rapidapi.com/v1/convertcurrency?have=USD&want=TZS&amount=1")
                .get()
                .addHeader("X-RapidAPI-Key", "539376df86msha7c2542b821b5bbp1660b6jsn5c7daa1bde3b")
                .addHeader("X-RapidAPI-Host", "currency-converter-by-api-ninjas.p.rapidapi.com")
                .build();

        try {
            Response response = client.newCall(request).execute();
            String jsonData = response.body().string();
            JSONObject jsonObject = new JSONObject(jsonData);
            return jsonObject.getInt("new_amount");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Integer newAmount) {
        if (newAmount != null) {
            textView.setText(String.valueOf("1 USD = "+newAmount+"Tsh"));
        } else {
            textView.setText("Error retrieving data");
        }
    }
}
