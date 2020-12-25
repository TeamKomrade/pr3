package com.example.pr3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import static com.example.pr3.ConnectFetchJava.getIconUrl;
import static com.example.pr3.StaticWeatherAnalyser.getCityField;
import static com.example.pr3.StaticWeatherAnalyser.getDetailsField;
import static com.example.pr3.StaticWeatherAnalyser.getLastUpdateTime;
import static com.example.pr3.StaticWeatherAnalyser.getTemperatureField;

public class MainActivity extends AppCompatActivity {
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new ConnectFetchJava(this, "Orenburg", new ConnectFetchJava.OnConnectionCompleteListener() {
            @Override
            public void onSuccess(JSONObject response) {
                renderWeather(response);
            }

            @Override
            public void onFail(String message) {

                Toast.makeText(MainActivity.this,
                        message,
                        Toast.LENGTH_LONG).show();
            }
        });
    }


    private void renderWeather(JSONObject json){
        try {

            Glide
                    .with(this)
                    .load(getIconUrl(json))
                    .into((ImageView)findViewById(R.id.weather_icon));
            ((TextView)findViewById(R.id.city_field)).setText(getCityField(json));
            ((TextView)findViewById(R.id.updated_field)).setText(getLastUpdateTime(json));
            ((TextView)findViewById(R.id.details_field)).setText(getDetailsField(json));
            ((TextView)findViewById(R.id.current_temperature_field)).setText(getTemperatureField(json));

        }catch(Exception e){
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }

}