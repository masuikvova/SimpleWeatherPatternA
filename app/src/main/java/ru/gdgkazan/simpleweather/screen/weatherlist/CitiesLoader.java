package ru.gdgkazan.simpleweather.screen.weatherlist;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import ru.arturvasilov.sqlite.core.SQLite;
import ru.arturvasilov.sqlite.rx.RxSQLite;
import ru.gdgkazan.simpleweather.data.model.WeatherCity;
import ru.gdgkazan.simpleweather.data.tables.WeatherCityTable;
import ru.gdgkazan.simpleweather.network.NetworkService;
import ru.gdgkazan.simpleweather.network.model.NetworkRequest;
import ru.gdgkazan.simpleweather.network.model.Request;


public class CitiesLoader extends AsyncTaskLoader<List<WeatherCity>> {
    private Context context;

    public CitiesLoader(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<WeatherCity> loadInBackground() {
        List<WeatherCity> data = SQLite.get().query(WeatherCityTable.TABLE);
        if( data.size() == 0){
            startService();
        }
        return data;
    }

    private void startService() {
        Request request = new Request(NetworkRequest.CITY_LIST);
        NetworkService.start(context, request);
    }
}
