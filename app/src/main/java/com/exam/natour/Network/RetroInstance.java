package com.exam.natour.Network;

import com.exam.natour.Network.Interceptor.AuthInterceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroInstance {

    public static String BASE_URL = "https://natour.pepeandrea.it/api/";

    private static Retrofit retrofit;

    public static Retrofit getRetrofitClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(makeOkHttpClient())
                    .build();
        }
        return retrofit;
    }

    private static OkHttpClient makeOkHttpClient(){
        return new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor())
                .build();
    }
}
