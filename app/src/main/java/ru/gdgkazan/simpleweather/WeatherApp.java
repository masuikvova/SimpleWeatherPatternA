package ru.gdgkazan.simpleweather;

import android.app.Application;

import ru.arturvasilov.sqlite.core.SQLite;

/**
 * @author Artur Vasilov
 */
public class WeatherApp extends Application {

    private static WeatherApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        SQLite.initialize(this);
        instance = this;
    }

    public static WeatherApp getInstance(){
        return  instance;
    }
}
