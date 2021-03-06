package ru.gdgkazan.simpleweather.network.model;

import android.support.annotation.StringDef;

/**
 * @author Artur Vasilov
 */
@StringDef({NetworkRequest.CITY_WEATHER,NetworkRequest.CITY_LIST})
public @interface NetworkRequest {

    String CITY_WEATHER = "city_weather";

    String CITY_LIST = "city_list";
}


