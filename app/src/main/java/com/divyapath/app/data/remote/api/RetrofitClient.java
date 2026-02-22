package com.divyapath.app.data.remote.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class RetrofitClient {

    private static final String GEOCODING_BASE_URL = "https://geocoding-api.open-meteo.com/";

    private static volatile Retrofit geocodingRetrofit;

    private RetrofitClient() {}

    public static Retrofit getGeocodingClient() {
        if (geocodingRetrofit == null) {
            synchronized (RetrofitClient.class) {
                if (geocodingRetrofit == null) {
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(10, TimeUnit.SECONDS)
                            .addInterceptor(logging)
                            .build();

                    geocodingRetrofit = new Retrofit.Builder()
                            .baseUrl(GEOCODING_BASE_URL)
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }
        return geocodingRetrofit;
    }

    public static GeocodingApiService getGeocodingService() {
        return getGeocodingClient().create(GeocodingApiService.class);
    }
}
