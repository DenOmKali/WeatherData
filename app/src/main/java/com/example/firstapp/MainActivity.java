package com.example.firstapp;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText userField;

    private Button mainBtn;

    private TextView resultInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userField = findViewById(R.id.user_field);
        mainBtn = findViewById(R.id.main_btn);
        resultInfo = findViewById(R.id.result_info);

        mainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userField.getText().toString().trim().equals("")){
                    Toast.makeText(MainActivity.this, R.string.no_user_input, Toast.LENGTH_LONG);
                } else {
                    String city = userField.getText().toString();

                    // Отримати свій ключ можна на сайті: https://openweathermap.org
                    String key = "8b2c2b5cab61b463e4a6fffb75f0783d" ;

                    String url = "https://api.openweathermap.org/data/2.5/weather?q="+ city +"&appid="+ key +"&units=metric&lang=ua";
                    new GetUrlData().execute(url);
                }
            }
        });

    }

    private class GetUrlData extends AsyncTask<String, String, String> {

        protected void onPreExecute(){
            super.onPreExecute();
            resultInfo.setText("Почекайте...");
        }


        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while((line = reader.readLine()) != null){
                    buffer.append(line).append("\n");
                }
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.disconnect();

                try {
                    if (reader != null)
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            return null;
            }

            @SuppressLint("SetTextI18n")
            @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    resultInfo.setText("Країна: " + jsonObject.getJSONObject("sys").getString("country")
                            +"\n"+ "Місто: " + jsonObject.getString("name")
                            +"\n"+ "Температура: " + jsonObject.getJSONObject("main").getDouble("temp") + " C°");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


        }
    }
}