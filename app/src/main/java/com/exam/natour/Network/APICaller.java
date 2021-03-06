package com.exam.natour.Network;


import com.exam.natour.Model.AuthUser;
import com.exam.natour.Model.LoginResponse.LoginResponse;
import com.exam.natour.Model.PathDetailResponse.Coordinate;
import com.exam.natour.Model.PathDetailResponse.InterestPoint;
import com.exam.natour.Model.PathDetailResponse.PathDetailResponse;
import com.exam.natour.Model.PathsResponse.PathsResponse;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

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

    @Headers({"Accept: application/json"})
    @GET("paths/filter")
    Call<PathsResponse> filterPath(@Query("distance") String raggio,
                                   @Query("length") String distanza,
                                   @Query("duration") String durata,
                                   @Query("disability") Integer disability,
                                   @QueryMap Map<String, String> difficulties,
                                   @QueryMap Map<String, Double> userCoordinates);


    //Get path endpoint
    @Headers({"Accept: application/json"})
    @GET("path/{id}")
    Call<PathDetailResponse> getPath(@Path(value = "id", encoded = true) String id);

    //Add new path
    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST("path")
    Call<PathDetailResponse> savePath(@Field("title") String title,
                                      @Field("description") String description,
                                      @Field("location") String location,
                                      @Field("difficulty") String difficulty,
                                      @Field("disability") Integer disability,
                                      @Field("length") Double length,
                                      @Field("duration") Long duration,
                                      @FieldMap Map<String,String> coordinates,
                                      @FieldMap Map<String,String> interestPoints);


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


    //Send Email
    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST("sendemail")
    Call<JSONObject> sendEmail(@Field("title") String title,@Field("content") String content);

    //Report Path
    @Headers({"Accept: application/json"})
    @POST("report/{path}")
    Call<JSONObject> reportPath(@Path(value = "path", encoded = true) String path);
}
