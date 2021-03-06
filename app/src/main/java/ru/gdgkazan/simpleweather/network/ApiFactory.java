package ru.gdgkazan.simpleweather.network;

import android.support.annotation.NonNull;

import com.readystatesoftware.chuck.ChuckInterceptor;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.gdgkazan.simpleweather.BuildConfig;
import ru.gdgkazan.simpleweather.WeatherApp;

/**
 * @author Artur Vasilov
 */
public final class ApiFactory {

    private static OkHttpClient sClient;

    private static volatile WeatherService sService;

    public static final int CITY_LIST = 0;
    public static final int WEATHER_OF_CITY = 1;


    private ApiFactory() {
    }

    @NonNull
    public static WeatherService getWeatherService() {
        WeatherService service = sService;
        if (service == null) {
            synchronized (ApiFactory.class) {
                service = sService;
                if (service == null) {
                    service = sService = buildRetrofit().create(WeatherService.class);
                }
            }
        }
        return service;
    }

    @NonNull
    public static WeatherService getWeatherService(int param) {
        WeatherService service = sService;
        if (service == null) {
            synchronized (ApiFactory.class) {
                service = sService;
                if (service == null) {
                    switch (param){
                        case CITY_LIST:
                            service = sService = buildCityList().create(WeatherService.class);
                            break;
                        case WEATHER_OF_CITY:
                            service = sService = buildRetrofit().create(WeatherService.class);
                            break;
                    }
                }
            }
        }
        return service;
    }

    @NonNull
    private static Retrofit buildCityList() {
        return new Retrofit.Builder()
                .baseUrl("http://openweathermap.org")
                .client(getClient())
                .addConverterFactory(new CityListConverter())
                .build();
    }

    @NonNull
    private static Retrofit buildRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.API_ENDPOINT)
                .client(getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @NonNull
    private static OkHttpClient getClient() {
        OkHttpClient client = sClient;
        if (client == null) {
            synchronized (ApiFactory.class) {
                client = sClient;
                if (client == null) {
                    client = sClient = buildClient();
                }
            }
        }
        return client;
    }

    @NonNull
    private static OkHttpClient buildClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor())
                .addInterceptor(new ApiKeyInterceptor())
                .addInterceptor(new ChuckInterceptor(WeatherApp.getInstance()))
                .build();
    }
}
