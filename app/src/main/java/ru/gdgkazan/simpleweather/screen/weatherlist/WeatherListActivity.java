package ru.gdgkazan.simpleweather.screen.weatherlist;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.arturvasilov.sqlite.core.BasicTableObserver;
import ru.arturvasilov.sqlite.core.SQLite;
import ru.arturvasilov.sqlite.core.Where;
import ru.arturvasilov.sqlite.rx.RxSQLite;
import ru.gdgkazan.simpleweather.R;
import ru.gdgkazan.simpleweather.data.model.WeatherCity;
import ru.gdgkazan.simpleweather.data.tables.RequestTable;
import ru.gdgkazan.simpleweather.data.tables.WeatherCityTable;
import ru.gdgkazan.simpleweather.network.NetworkService;
import ru.gdgkazan.simpleweather.network.model.NetworkRequest;
import ru.gdgkazan.simpleweather.network.model.Request;
import ru.gdgkazan.simpleweather.network.model.RequestStatus;
import ru.gdgkazan.simpleweather.screen.general.LoadingDialog;
import ru.gdgkazan.simpleweather.screen.general.LoadingView;
import ru.gdgkazan.simpleweather.screen.general.SimpleDividerItemDecoration;
import ru.gdgkazan.simpleweather.screen.weather.WeatherActivity;
import ru.gdgkazan.simpleweather.utils.RxSchedulers;
import rx.Observable;

/**
 * @author Artur Vasilov
 */
public class WeatherListActivity extends AppCompatActivity implements CitiesAdapter.OnItemClick, BasicTableObserver {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.empty)
    View mEmptyView;

    private CitiesAdapter mAdapter;

    private LoadingView mLoadingView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_list);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this, false));
        mAdapter = new CitiesAdapter(null, this);
        mRecyclerView.setAdapter(mAdapter);
        mLoadingView = LoadingDialog.view(getSupportFragmentManager());

        loadCityList();
        /**
         * TODO : task
         *
         * 1) Load all cities (pair id-name) if WeatherCityTable is empty from http://openweathermap.org/help/city_list.txt
         * and save them to database
         * 2) Using the information from the first step find id for each current city
         * 3) Load forecast in all cities using one single request http://openweathermap.org/current#severalid
         * 4) Do all this work in NetworkService (you need to modify it to better support multiple requests)
         * 5) Use SQLite API to manage communication
         * 6) Handle configuration changes
         * 7) Modify all the network layer to create a universal way for managing queries with pattern A
         */
    }

    @Override
    public void onItemClick(@NonNull WeatherCity city) {
        startActivity(WeatherActivity.makeIntent(this, city.getCityName()));
    }

    private void loadCityList() {
        mLoadingView.showLoadingIndicator();
        RxSQLite.get().query(WeatherCityTable.TABLE)
                .compose(RxSchedulers.async())
                .subscribe(cities -> {
                    if (cities.size() > 0) {
                        showList(cities);
                        mLoadingView.hideLoadingIndicator();
                    }else
                        startService();
                },throwable -> {
                    startService();
                });
    }

    private void startService() {
        SQLite.get().registerObserver(RequestTable.TABLE, this);
        Request request = new Request(NetworkRequest.CITY_LIST);
        NetworkService.start(this, request);
    }

    @Override
    public void onTableChanged() {
        Where where = Where.create().equalTo(RequestTable.REQUEST, NetworkRequest.CITY_LIST);
        RxSQLite.get().querySingle(RequestTable.TABLE, where)
                .compose(RxSchedulers.async())
                .flatMap(request -> {
                    if (request.getStatus() == RequestStatus.IN_PROGRESS) {
                        mLoadingView.showLoadingIndicator();
                        return Observable.empty();
                    } else if (request.getStatus() == RequestStatus.ERROR) {
                        return Observable.error(new IOException(request.getError()));
                    }
                    return RxSQLite.get().query(WeatherCityTable.TABLE).compose(RxSchedulers.async());
                })
                .subscribe(cities -> {
                    showList(cities);
                    mLoadingView.hideLoadingIndicator();
                }, throwable -> {
                    showError();
                    mLoadingView.hideLoadingIndicator();
                });
    }

    private void showError() {
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
    }

    private void showList(List<WeatherCity> data) {
        if (data != null) {
            mAdapter.changeDataSet(data);
        }
    }
}
