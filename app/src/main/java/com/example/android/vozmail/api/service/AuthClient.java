package com.example.android.vozmail.api.service;

import com.example.android.vozmail.api.model.Labels;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;


public interface AuthClient {


    @GET("{userId}/labels")
    Call<Object> Labels(@Path("userId") String userId, @Header("Authorization") String token, @Query("key") String apiKey);


}
