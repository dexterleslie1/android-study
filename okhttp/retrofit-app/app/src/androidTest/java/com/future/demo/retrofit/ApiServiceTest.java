package com.future.demo.retrofit;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.future.demo.retrofit.service.ApiService;
import com.google.gson.Gson;
import com.yyd.common.http.response.ObjectResponse;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@RunWith(AndroidJUnit4.class)
public class ApiServiceTest {
    // 创建ApiService实例
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.1.171:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    ApiService apiService = retrofit.create(ApiService.class);

    @Test
    public void getWithHeaderAndQueryParamter() throws IOException {
        String customHeader = "customHeaderValue";
        String username = "dexterleslie";
        Call<ResponseBody> call = apiService.getWithHeaderAndQueryParamter(customHeader, username);
        ResponseBody responseBody = call.execute().body();
        Log.d(ApiServiceTest.class.getSimpleName(), responseBody.string());
    }

    @Test
    public void getWithObjectResponse() throws IOException {
        Call<ObjectResponse<String>> call = apiService.getWithObjectResponse();
        ObjectResponse<String> response = call.execute().body();
        String json = new Gson().toJson(response);
        Log.d(ApiServiceTest.class.getSimpleName(), json);
    }
}
