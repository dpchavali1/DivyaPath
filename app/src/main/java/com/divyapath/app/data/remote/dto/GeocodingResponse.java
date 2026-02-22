package com.divyapath.app.data.remote.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GeocodingResponse {

    @SerializedName("results")
    private List<GeocodingResult> results;

    public List<GeocodingResult> getResults() {
        return results;
    }
}
