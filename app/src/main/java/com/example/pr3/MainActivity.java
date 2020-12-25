package com.example.pr3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();
        updateWeatherData("Оренбург");

    }

    private void updateWeatherData (final String city) {
        new Thread() {
            public void run() {
                final JSONObject json = ConnectFetchJava.getJSON(MainActivity.this, city);
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(MainActivity.this,
                                    city + "-информация не найдена",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run(){
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();

    }

    private void renderWeather (JSONObject json) {
        try {

//            Ответы:
//                      json.getString("name") - название города
//                      json.getJSONObject("sys").getString("country") - название страны
//                      JSONObject details =  json.getJSONArray("weather").getJSONObject(0) - первый элемент массива метеорологических данных.
//                           details.getInt("id")  - идентификатор погоды\
//                           details.getString("description") - краткое описание погоды

//                      JSONObject main = json.getJSONObject("main"); - узел main
//                          main.getString("humidity")  - влажность
//                          main.getString("pressure")  - давление
//                            main.getDouble("temp")    - температура

//                      DateFormat df = DateFormat.getDateTimeInstance();
//                      String updatedOn = df.format(new Date(json.getLong("dt")*1000)); - время получения информации системой

//                      json.getJSONObject("sys").getLong("sunrise") - время восхода
//                      json.getJSONObject("sys").getLong("sunset") - время заката
            JSONObject details =  json.getJSONArray("weather").getJSONObject(0);
            ((TextView)findViewById(R.id.weatherTextView)).setText(details.getString("description").toUpperCase());


        } catch(Exception e) {
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }

}