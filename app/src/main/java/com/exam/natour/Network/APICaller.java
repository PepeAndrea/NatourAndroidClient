package com.exam.natour.Network;


import com.exam.natour.Model.AuthUser;
import com.exam.natour.Model.LoginResponse.LoginResponse;
import com.exam.natour.Model.PathsResponse.PathsResponse;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APICaller {
    //Login endpoint
    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST("login")
    Call<LoginResponse> login(@Field("email") String email,
                              @Field("password") String password);

    //Get all paths endpoint
    @Headers({"Accept: application/json"})
    @GET("paths")
    Call<PathsResponse> getAllPaths();

    //CheckToken endpoint
    @Headers({"Accept: application/json"})
    @GET("checkUser")
    Call<LoginResponse> checkToken();

    //Signup endpoint
    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST("register")
    Call<LoginResponse> signup(@Field("email") String email,
                               @Field("name") String username,
                               @Field("password") String password,
                               @Field("password_confirmation") String passwordConfirmation);

    //Login with provider endpoint
    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST("login/{provider}")
    Call<LoginResponse> loginProvider(@Path(value = "provider", encoded = true) String provider,
                                      @Field("provider_token") String token);

    //Logout
    @Headers({"Accept: application/json"})
    @POST("logout")
    Call<JSONObject> logout();
}
