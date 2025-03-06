package com.example.termitewallet;

import com.example.termitewallet.ResponseServices.ApiResponseAddress;
import com.example.termitewallet.ResponseServices.ApiResponseBalance;
import com.example.termitewallet.ResponseServices.ApiResponseTransaction;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
        @Headers("Content-Type: application/json")
        @GET("GetAddress")
        Call<ApiResponseAddress> getAddress();
        @Headers("Content-Type: application/json")
        @POST("GetBalance")
        Call<ApiResponseBalance> getBalance(@Body RequestBody address);
        @Headers("Content-Type: application/json")
        @POST("PostTransaction")
        Call<ApiResponseTransaction> sendTransaction(@Body RequestBody transaction);

}
