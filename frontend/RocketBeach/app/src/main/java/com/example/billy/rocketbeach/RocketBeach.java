package com.example.billy.rocketbeach;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

interface RocketBeach {
    @GET("search/beaches.json")
    Call<List<Beach>> listBeaches(@QueryMap Map<String, String> queries, @Header("X-Auth-Token") String auth);

    @GET("beaches/{beach_id}.json")
    Call<Beach> getBeachInfo(@Path(value = "beach_id", encoded = true) String beachId, @Header("X-Auth-Token") String auth);

    @FormUrlEncoded
    @POST("users")
    Call<Beachgoer> addUser(@Field("user[email]") String email, @Field("user[password]") String pass, @Field("user[password_confirmation]") String confirm);

    @FormUrlEncoded
    @POST("users/sign_in")
    Call<Beachgoer> loginUser(@Field("user_login[email]") String email, @Field("user_login[password]") String pass);

    @FormUrlEncoded
    @POST("register_device")
    Call<Beachgoer> registerUserDevice(@Field("device_token") String token, @Header("X-Auth-Token") String auth);

    @GET("me")
    Call<Beachgoer> getMe(@Header("X-Auth-Token") String token);
}