package com.future.demo.retrofit;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.future.demo.retrofit.service.ApiService;
import com.future.demo.retrofit.service.ApiService2;
import com.google.gson.Gson;
import com.yyd.common.http.response.ObjectResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@RunWith(AndroidJUnit4.class)
public class ApiServiceTest {
    private ApiService apiService;
    private ApiService2 apiService2;

    @Before
    public void setup() {
        // 创建ApiService实例
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.171:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // 参考以下链接使用Interceptor添加http header
        // https://stackoverflow.com/questions/32963394/how-to-use-interceptor-to-add-headers-in-retrofit-2-0
        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.171:8080")
                // OkHttpClient添加http header
                .client(new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
                                .addHeader("customHeader", "customHeaderValue2")
                                .build();
                        Response response = chain.proceed(request);
                        return response;
                    }
                }).build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService2 = retrofit.create(ApiService2.class);
    }

    @Test
    public void getWithHeaderAndQueryParamter() throws IOException {
        String customHeader = "customHeaderValue";
        String username = "dexterleslie";
        Call<ResponseBody> call = apiService.getWithHeaderAndQueryParamter(customHeader, username);
        ResponseBody responseBody = call.execute().body();
        Log.d(ApiServiceTest.class.getSimpleName(), responseBody.string());

        call = apiService2.getWithHeaderAndQueryParamter(username);
        responseBody = call.execute().body();
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
