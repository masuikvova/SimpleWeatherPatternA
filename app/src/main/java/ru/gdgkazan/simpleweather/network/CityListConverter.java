package ru.gdgkazan.simpleweather.network;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import ru.gdgkazan.simpleweather.data.model.WeatherCity;


public class CityListConverter extends Converter.Factory {
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return CitiesConverter.INSTANCE;
    }

    final static class CitiesConverter implements Converter<ResponseBody, List<WeatherCity>> {
        static final CitiesConverter INSTANCE = new CitiesConverter();

        @Override
        public List<WeatherCity> convert(ResponseBody responseBody) throws IOException {
            List<WeatherCity> cities = new ArrayList<>();
            try {
                String data = responseBody.string();
                String[] lines = data.split(System.getProperty("line.separator"));
                if (lines.length > 1) {
                    for (int i = 1; i < lines.length; i++) {
                        String lineData[] = lines[i].split("\t");
                        if (lineData.length > 0) {
                            WeatherCity city = new WeatherCity(Integer.parseInt(lineData[0]), lineData[1]);
                            cities.add(city);
                        }
                    }
                }
            } catch (Exception e) {
                throw new IOException("Failed to parse City list", e);
            }
            return cities;
        }
    }
}
