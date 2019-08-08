package com.future.study.android.okhttp;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;
import android.util.Log;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static org.junit.Assert.assertEquals;

/**
 *
 */
@RunWith(AndroidJUnit4.class)
public class OkHttpTest {
    private final static String Host = "192.168.1.40";
    private final static int Port = 8080;

    @Test
    public void postAndReturnString() throws Exception {
        OkHttpClient okHttpClient = new OkHttpClient();
        String name = "Dexter";
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        String url = this.getUrlPrefix() + "/http/library/api/postAndReturnString";
        RequestBody requestBody=this.createRequestBody(params);
        Request request=new Request.Builder().url(url).method("POST",requestBody).build();
        Response response=okHttpClient.newCall(request).execute();
        if(!response.isSuccessful()){
            int code=response.code();
            throw new Exception("http请求错误，状态码："+code);
        }
        String responseString=response.body().string();
        response.close();

        JSONObject responseObject = new JSONObject(responseString);
        String string1 = responseObject.getString("dataObject");
        Assert.assertEquals("你好，" + name, string1);
    }

    @Test
    public void postAndReturnWithException() throws Exception {
        OkHttpClient okHttpClient = new OkHttpClient();
        String name = "Dexter";
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        String url = this.getUrlPrefix() + "/http/library/api/postAndReturnWithException";
        RequestBody requestBody=this.createRequestBody(params);
        Request request=new Request.Builder().url(url).method("POST",requestBody).build();
        Response response=okHttpClient.newCall(request).execute();
        if(!response.isSuccessful()){
            int code=response.code();
            throw new Exception("http请求错误，状态码："+code);
        }
        String responseString=response.body().string();
        response.close();

        JSONObject responseObject = new JSONObject(responseString);
        int errorCode = responseObject.getInt("errorCode");
        String errorMessage = responseObject.getString("errorMessage");
        Assert.assertEquals(50000, errorCode);
        Assert.assertEquals("测试预期异常是否出现", errorMessage);
    }

    @Test
    public void upload() throws Exception {
        OkHttpClient okHttpClient = new OkHttpClient();
        String name = "Dexter";
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        String url = this.getUrlPrefix() + "/http/library/api/upload";

        Context context = InstrumentationRegistry.getTargetContext();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("p1.jpg");
        File file= File.createTempFile(UUID.randomUUID().toString(),".tmp");
        FileUtils.copyInputStreamToFile(inputStream,file);
        inputStream.close();
        inputStream=null;

        RequestBody requestBody=this.createMultipartRequestBody(params,file);
        Request request=new Request.Builder().url(url).method("POST",requestBody).build();
        Response response=okHttpClient.newCall(request).execute();
        if(!response.isSuccessful()){
            int code=response.code();
            throw new Exception("http请求错误，状态码："+code);
        }
        String responseString=response.body().string();
        response.close();

        JSONObject responseObject = new JSONObject(responseString);
        String str1 = responseObject.getString("name");
        String filename = responseObject.getString("file");
        Assert.assertEquals("你好，" + name, str1);
        Assert.assertTrue(!TextUtils.isEmpty(filename));
    }

    @Test
    public void download() throws Exception{
        OkHttpClient okHttpClient = new OkHttpClient();
        String name = "Dexter";
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        String url = this.getUrlPrefix() + "/http/library/api/upload";

        Context context = InstrumentationRegistry.getTargetContext();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("p1.jpg");
        File file1= File.createTempFile(UUID.randomUUID().toString(),".tmp");
        FileUtils.copyInputStreamToFile(inputStream,file1);
        inputStream.close();
        inputStream=null;

        RequestBody requestBody=this.createMultipartRequestBody(params,file1);
        Request request=new Request.Builder().url(url).method("POST",requestBody).build();
        Response response=okHttpClient.newCall(request).execute();
        if(!response.isSuccessful()){
            int code=response.code();
            throw new Exception("http请求错误，状态码："+code);
        }
        String responseString=response.body().string();
        response.close();

        JSONObject responseObject = new JSONObject(responseString);
        String str1 = responseObject.getString("name");
        String filename = responseObject.getString("file");

        url = this.getUrlPrefix() + "/http/library/api/download";
        params = new HashMap<>();
        params.put("filename", filename);
        requestBody=this.createRequestBody(params);
        request = new Request.Builder().url(url).method("POST",requestBody).build();
        response=okHttpClient.newCall(request).execute();
        if(!response.isSuccessful()){
            int code=response.code();
            throw new Exception("http请求错误，状态码："+code);
        }

        String contentType=response.header("Content-Type");
        File file2 = null;
        if("application/json".equalsIgnoreCase(contentType)){
            responseString=response.body().string();
            throw new Exception(responseString);
        }else {
            inputStream = response.body().byteStream();
            file2= File.createTempFile(UUID.randomUUID().toString(),".tmp");
            FileUtils.copyInputStreamToFile(inputStream,file2);
            inputStream.close();
            inputStream=null;
        }
        response.close();

        Assert.assertEquals(FileUtils.readFileToByteArray(file1).length,
                FileUtils.readFileToByteArray(file2).length);
    }

    private RequestBody createMultipartRequestBody(Map<String,Object> params, File file){
        MultipartBody.Builder builder=new MultipartBody.Builder();
        builder=builder.setType(MultipartBody.FORM);
        if(params!=null&&params.size()>0){
            for (String keyTemp : params.keySet()) {
                builder=builder.addFormDataPart(keyTemp, String.valueOf(params.get(keyTemp)));
            }
        }
        RequestBody requestBody =builder
                .addFormDataPart("file1", file.getName(),
                        RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build();
        return requestBody;
    }

    private RequestBody createRequestBody(Map<String,Object> params){
        RequestBody requestBody;
        FormBody.Builder builder = new FormBody.Builder();
        if(params!=null&&params.size()>0) {
            for (String keyTemp : params.keySet()) {
                builder.add(keyTemp, String.valueOf(params.get(keyTemp)));
            }
        }
        requestBody=builder.build();
        return requestBody;
    }

    private String getUrlPrefix() {
        String url = "http://" + Host + ":" + Port;
        return url;
    }
}
