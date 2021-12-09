package com.fcossetta.simpletest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface Api {

    String BASE_URL = "https://www.google.it";

    @GET
    Call<ResponseBody> makeCall(@Url String url);
}