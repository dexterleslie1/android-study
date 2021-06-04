package com.future.demo.retrofit.service;

import com.yyd.common.http.response.ObjectResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ApiService {
    @GET("/http/library/api/getWithHeaderAndQueryParamter")
    Call<ResponseBody> getWithHeaderAndQueryParamter(
            @Header("customHeader") String customHeader,
            @Query("username") String username);

    @GET("/http/library/api/getWithObjectResponse")
    Call<ObjectResponse<String>> getWithObjectResponse();
}
