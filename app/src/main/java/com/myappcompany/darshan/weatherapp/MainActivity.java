package com.myappcompany.darshan.weatherapp;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText cityEditText;
    TextView resultTextView;

    public void toast() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(),"could not find weather:("+"\n"+"Check your connection or city name",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void getWeather(View view)
    {

        try {
            DownloadTask task = new DownloadTask();

            String cityNameEncoded = URLEncoder.encode(cityEditText.getText().toString(), "UTF-8");

            task.execute("https://openweathermap.org/data/2.5/weather?q=" + cityNameEncoded + "&appid=439d4b804bc8187953eb36d2a8c26a02");

            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(cityEditText.getWindowToken(), 0);

        } catch (Exception e) {

            e.printStackTrace();
            toast();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityEditText = findViewById(R.id.cityEditText);
        resultTextView = findViewById(R.id.resultTextView);


    }



    public class DownloadTask extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... urls)
        {

            String result = "";
            URL url;
            HttpURLConnection urlConnection;

            try {

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();

                while (data != -1)
                {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;

            }catch (Exception e)
            {
                e.printStackTrace();
                toast();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);

                String weatherInfo = jsonObject.getString("weather");

                String tempInfo = jsonObject.getString("main");

                JSONArray jsonArray = new JSONArray(weatherInfo);

                String message = "";

                String temp = jsonObject.getJSONObject("main").getString("temp");
                String tempMin = jsonObject.getJSONObject("main").getString("temp_min");
                String tempMax = jsonObject.getJSONObject("main").getString("temp_max");
                String pressure = jsonObject.getJSONObject("main").getString("pressure");
                String humidity = jsonObject.getJSONObject("main").getString("humidity");


                String main = "";
                String description = "";

                for(int i = 0; i<jsonArray.length(); i++)
                {
                    JSONObject jsonPart = jsonArray.getJSONObject(i);

                    main = jsonPart.getString("main");
                    description = jsonPart.getString("description");
                }
                if(!main.equals("") && !description.equals("") && !temp.equals(""))
                {
                    message += main+": "+description+"\r\n"+"Temp: "+temp+"°C"+"\n"+"Maximum temp: "+tempMax+"°C"+"\n"+"Minimum temp: "+tempMin+"°C"+"\n"+"Humidity: "+humidity+"%"+"\n"+"Pressure: "+pressure+"hPa";
                }
                if(!message.equals("")) {
                    resultTextView.setText(message);
                }else {
                    toast();
                    }
            }catch (Exception e)
            {
                toast();
                e.printStackTrace();

            }
        }
    }
}
