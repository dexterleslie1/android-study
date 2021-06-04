package com.future.demo.retrofit.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService2 {
    @GET("/http/library/api/getWithHeaderAndQueryParamter")
    Call<ResponseBody> getWithHeaderAndQueryParamter(@Query("username") String username);
}
