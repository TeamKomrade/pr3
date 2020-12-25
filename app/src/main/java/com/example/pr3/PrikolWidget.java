package com.example.pr3;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.AppWidgetTarget;

import org.json.JSONObject;

import java.util.Arrays;

import static com.example.pr3.ConnectFetchJava.getIconUrl;
import static com.example.pr3.StaticWeatherAnalyser.getTemperatureField;

/**
 * Implementation of App Widget functionality.
 */
public class PrikolWidget extends AppWidgetProvider {
    final String LOG_TAG = "jopaLogs";

    static void updateAppWidget(final Context context, SharedPreferences sharedPreferences, final AppWidgetManager appWidgetManager,
                                final int appWidgetId) {

        // Читаем параметры Preferences
        String widgetCity = sharedPreferences.getString(ConfigActivity.WIDGET_CITY + appWidgetId, null);
        if (widgetCity == null) return;

        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.prikol_widget);
        remoteViews.setTextViewText(R.id.city_field,widgetCity);

        new  ConnectFetchJava(context, widgetCity, new ConnectFetchJava.OnConnectionCompleteListener() {
            @Override
            public void onSuccess(JSONObject response) {
                renderWeather(response,context,remoteViews,appWidgetId, appWidgetManager);
            }

            @Override
            public void onFail(String message) {
            }
        });
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }


    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        // Удаляем Preferences
        SharedPreferences.Editor editor = context.getSharedPreferences(
                ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE).edit();
        for (int widgetID : appWidgetIds) {
            editor.remove(ConfigActivity.WIDGET_CITY + widgetID);
        }
        Log.d(LOG_TAG, "onDeleted " + Arrays.toString(appWidgetIds));

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //<
        SharedPreferences sp = context.getSharedPreferences(
                ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE);
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context,sp, appWidgetManager, appWidgetId);
        }
        Log.d(LOG_TAG, "onUpdate " + Arrays.toString(appWidgetIds));
    }

    @Override
    public void onEnabled(Context context) {
        Log.d(LOG_TAG, "cockEnabled");
    }

    @Override
    public void onDisabled(Context context) {
        Log.d(LOG_TAG, "cockDisabled");
    }

    public static void pushWidgetUpdate(Context context, RemoteViews rv) {
        ComponentName myWidget = new ComponentName(context, PrikolWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(myWidget, rv);
    }



    public static void renderWeather(JSONObject json, Context context, RemoteViews remoteViews, int appWidgetId, AppWidgetManager appWidgetManager){
        try {
            AppWidgetTarget appWidgetTarget = new AppWidgetTarget(context, remoteViews, R.id.weather_icon, appWidgetId);

            Glide.with(context.getApplicationContext())
                    .load(getIconUrl(json))
                    .asBitmap().
                    into( appWidgetTarget );
            remoteViews.setTextViewText(R.id.details_field, getTemperatureField(json));
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }catch(Exception e){
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }

}

