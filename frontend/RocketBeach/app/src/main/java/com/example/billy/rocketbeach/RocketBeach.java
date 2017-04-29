package com.example.billy.rocketbeach;


import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

interface RocketBeach {
//    @Headers("Authorization: Token token=fbea86a136e44d649970fe2effc645b7")
    @GET("search/beaches.json")
    Call<List<Beach>> listBeaches(@QueryMap Map<String, String> queries, @Header("Authorization") String auth);

    @FormUrlEncoded
    @POST("users")
    Call<Beachgoer> addUser(@Field("user[email]") String email, @Field("user[password]") String pass, @Field("user[password_confirmation]") String confirm);
}

//Authorization: Token token=af1a4fd625374dffa4c7fbeab48ffff4