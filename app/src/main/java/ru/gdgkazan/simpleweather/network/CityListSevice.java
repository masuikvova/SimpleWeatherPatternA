package ru.gdgkazan.simpleweather.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import ru.gdgkazan.simpleweather.data.model.WeatherCity;

/**
 * Created by vladimir.masyuk on 8/15/2017.
 */

public interface CityListSevice {
    @GET("help/city_list.txt")
    Call<List<WeatherCity>> getCityList();
}
