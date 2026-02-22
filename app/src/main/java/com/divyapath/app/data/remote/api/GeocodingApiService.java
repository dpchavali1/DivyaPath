package com.divyapath.app.data.remote.api;

import com.divyapath.app.data.remote.dto.GeocodingResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GeocodingApiService {

    @GET("v1/search")
    Call<GeocodingResponse> searchCities(
            @Query("name") String query,
            @Query("count") int count,
            @Query("language") String language
    );
}
